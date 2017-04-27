/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ws.extensions.security;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.XMLUtils;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.WSException;
import org.jboss.ws.core.utils.ThreadLocalAssociation;
import org.jboss.ws.extensions.security.element.BinarySecurityToken;
import org.jboss.ws.extensions.security.element.SecurityTokenReference;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * <code>STRTransform</code> implements the STR-Transform specified in the
 * WS-Security specification. This class dynamically registers itself with
 * XML Security on its first load (using a static initializer).
 *
 * You must call the static yet thread safe setSecurityStore() before use of
 * this class.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class STRTransform extends TransformSpi
{
   public static final String STR_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#STR-Transform";

   static
   {
      try
      {
         Transform.register(STR_URI, STRTransform.class.getName());
      }
      catch (Exception e)
      {
         // Eat
      }
   }

   private String canonicalize(Element element, Element method) throws TransformationException, InvalidCanonicalizerException, CanonicalizationException
   {
      if (method == null || ! method.getLocalName().equals("CanonicalizationMethod"))
         throw new TransformationException("CanonicalizationMethod expected!");

      String algorithm = method.getAttribute("Algorithm");
      if (algorithm == null || algorithm.length() == 0)
         throw new TransformationException("CanonicalizationMethod missing algorithm!");

      Canonicalizer canon = Canonicalizer.getInstance(algorithm);

      return new String(canon.canonicalizeSubtree(element, "#default"));
   }

   @Override
   protected String engineGetURI()
   {
      return STR_URI;
   }


   @Override
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input) throws IOException, CanonicalizationException,
         InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException
   {

      SecurityStore store = ThreadLocalAssociation.localStrTransformAssoc().get();

      if (store == null)
         throw new WSException("SecurityStore Thread Local not initialized before call!");

      try
      {
         if (! input.isElement())
            throw new NotImplementedException("Only element input is supported");

         // Resolve the BinarySecurityToken associated with this SecurityTokenReference
         Element element = (Element)input.getSubNode();
         SecurityTokenReference ref = new SecurityTokenReference(element);
         KeyResolver resolver = new KeyResolver(store);
         BinarySecurityToken token = resolver.resolve(ref);

         // Get the specially formated dom element for this element
         element = token.getSTRTransformElement();

         // Obtain the canonicalizer specified in the transformation parameters
         Element parameters = XMLUtils.selectNode(this._transformObject.getElement().getFirstChild(), Constants.WSSE_NS,
               "TransformationParameters", 0);
         if (parameters == null)
            throw new TransformationException("wsse:TransformationParameters expected!");

         Element method = Util.getFirstChildElement(parameters);
         String transformed = canonicalize(element, method);

         // Now WS-Security says we must augment the transformed output to ensure that there is
         // a default namespace
         int startTag = transformed.indexOf('<');
         int endTag = transformed.indexOf('>', startTag + 1);
         String within = transformed.substring(startTag + 1, endTag);
         if (! within.contains("xmlns="))
         {
            int insPos = within.indexOf(" ") + startTag + 1;
            transformed = new StringBuilder(transformed).insert(insPos, " xmlns=\"\"").toString();
         }

         return new XMLSignatureInput(transformed.getBytes());
      }
      catch (WSSecurityException e)
      {
         throw new TransformationException(e.getMessage(), e);
      }
      finally
      {
         ThreadLocalAssociation.localStrTransformAssoc().set(null);
      }
   }

   public static void setSecurityStore(SecurityStore store)
   {
      ThreadLocalAssociation.localStrTransformAssoc().set(store);
   }

   public boolean wantsOctetStream()
   {
      return false;
   }

   public boolean wantsNodeSet()
   {
      return false;
   }

   public boolean returnsOctetStream()
   {
      return false;
   }

   public boolean returnsNodeSet()
   {
      return false;
   }
}
