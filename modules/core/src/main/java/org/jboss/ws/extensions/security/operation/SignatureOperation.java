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
package org.jboss.ws.extensions.security.operation;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.extensions.security.QNameTarget;
import org.jboss.ws.extensions.security.SecurityStore;
import org.jboss.ws.extensions.security.Target;
import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.extensions.security.WsuIdResolver;
import org.jboss.ws.extensions.security.WsuIdTarget;
import org.jboss.ws.extensions.security.element.Reference;
import org.jboss.ws.extensions.security.element.SecurityHeader;
import org.jboss.ws.extensions.security.element.SecurityTokenReference;
import org.jboss.ws.extensions.security.element.Signature;
import org.jboss.ws.extensions.security.element.X509Token;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class SignatureOperation implements EncodingOperation
{
   private List<Target> targets;
   private String alias;
   private String tokenRefType;

   public SignatureOperation(List<Target> targets, String alias, String tokenRefType)
   {
      super();
      this.targets = targets;
      this.alias = alias;
      this.tokenRefType = tokenRefType;
   }
   
   private void processTarget(XMLSignature sig, Document message, Target target)
   {
      if (target instanceof QNameTarget)
         processQNameTarget(sig, message, (QNameTarget) target);
      else if (target instanceof WsuIdTarget)
         processWsuIdTarget(sig, message, (WsuIdTarget) target);
      else
         throw new NotImplementedException();
   }

   private void processQNameTarget(XMLSignature sig, Document message, QNameTarget target)
   {
      QName name = target.getName();

      Transforms transforms = new Transforms(message);
      try
      {
         transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
      }
      catch (TransformationException e)
      {
         throw new RuntimeException(e);
      }

      Element element = Util.findElement(message.getDocumentElement(), name);
      if (element == null)
         throw new RuntimeException("Could not find element");

      String id = Util.assignWsuId(element);

      try
      {
         sig.addDocument("#" + id, transforms);
      }
      catch (XMLSignatureException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void processWsuIdTarget(XMLSignature sig, Document message, WsuIdTarget target)
   {
      String id = target.getId();

      Transforms transforms = new Transforms(message);
      try
      {
         transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
      }
      catch (TransformationException e)
      {
         throw new RuntimeException(e);
      }

      try
      {
         sig.addDocument("#" + id, transforms);
      }
      catch (XMLSignatureException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void process(Document message, SecurityHeader header, SecurityStore store) throws WSSecurityException
   {
      Element envelope = message.getDocumentElement();
      XMLSignature sig;
      try
      {
         sig = new XMLSignature(message, null, XMLSignature.ALGO_ID_SIGNATURE_RSA, Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
      }
      catch (XMLSecurityException e)
      {
         throw new WSSecurityException("Error building signature", e);
      }

      // For now we pass our resolver the root document because the signature element isn't attached
      // to the evelope yet (no wsse header). Perhaps we should do this differently
      sig.addResourceResolver(new WsuIdResolver(message, header.getElement()));
      PrivateKey key = store.getPrivateKey(alias);

      if (targets == null || targets.size() == 0)
      {
         // By default we sign the body element, and a timestamp if it is available
         String namespace = envelope.getNamespaceURI();
         processTarget(sig, message, new QNameTarget(new QName(namespace, "Body")));
         if (header.getTimestamp() != null)
            processTarget(sig, message, new WsuIdTarget("timestamp"));
      }
      else
      {
         for (Target target : targets)
            processTarget(sig, message, target);
      }

      try
      {
         sig.sign(key);
      }
      catch (XMLSignatureException e)
      {
         throw new WSSecurityException("Error signing message: " + e.getMessage(), e);
      }

      X509Certificate cert = store.getCertificate(alias);
      X509Token token = (X509Token) header.getSharedToken(cert);

      // Can we reuse an existing token?
      if (token == null)
      {
         token = new X509Token(cert, message);
         if (tokenRefType == null || Reference.DIRECT_REFERENCE.equals(tokenRefType))
            header.addToken(token);
      }

      SecurityTokenReference reference = new SecurityTokenReference(Reference.getReference(tokenRefType, message, token));
      sig.getKeyInfo().addUnknownElement(reference.getElement());

      header.addSecurityProcess(new Signature(sig));
   }
}
