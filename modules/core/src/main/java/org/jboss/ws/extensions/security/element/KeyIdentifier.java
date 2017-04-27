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
package org.jboss.ws.extensions.security.element;

import java.security.cert.X509Certificate;

import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.XMLUtils;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>KeyIdentifier</code> is a reference type within a
 * <code>SecurityTokenReference</code> that referes to a token that
 * using some uniquely identifying characteristic. An example is an
 * X.509v3 Subject Key Identifier, which is the only currently
 * supported key identifier by this class.
 *
 * @see org.jboss.ws.extensions.security.element.SecurityTokenReference
 * @see org.jboss.ws.extensions.security.element.BinarySecurityToken
 *
 * @author Jason T. Greene
 */
public class KeyIdentifier extends Reference
{
   public static final String SKI_TYPE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509SubjectKeyIdentifier";

   private Document doc;

   private String value;

   private Element cachedElement;

   public KeyIdentifier(Document doc, BinarySecurityToken token) throws WSSecurityException
   {
      this.doc = doc;
      referenceToken(token);
   }

   public KeyIdentifier(Element element) throws WSSecurityException
   {
      this.doc = element.getOwnerDocument();

      if (! "KeyIdentifier".equals(element.getLocalName()))
         throw new WSSecurityException("Invalid message, invalid local name on a KeyIdentifier");

      String valueType = element.getAttribute("ValueType");
      if (valueType == null || valueType.length() == 0)
         throw new WSSecurityException("Inavliad message, KeyIdentifier element is missing an ValueType");

      if (! SKI_TYPE.equals(valueType))
         throw new WSSecurityException("Currently only SubjectKeyIdentifiers are supported, was passed: " + valueType);

      // Lets be soft on encoding type since other clients don't properly use it
      this.value = XMLUtils.getFullTextChildrenFromElement(element);
   }

   public void referenceToken(BinarySecurityToken token) throws WSSecurityException
   {
      if (! (token instanceof X509Token))
         throw new WSSecurityException("KeyIdentifier tried to reference something besides an X509 token");

      X509Token x509 = (X509Token) token;
      X509Certificate cert = x509.getCert();

      // Maybee we should make one ourselves if it isn't there?
      byte[] encoded = cert.getExtensionValue("2.5.29.14");
      if (encoded == null)
         throw new WSSecurityException("Certificate did not contain a subject key identifier!");

      // We need to skip 4 bytes [(OCTET STRING) (LENGTH)[(OCTET STRING) (LENGTH) (Actual data)]]
      int trunc = encoded.length - 4;

      byte[] identifier = new byte[trunc];
      
      System.arraycopy(encoded, 4, identifier, 0, trunc);
      value = Base64.encode(identifier);
   }

   public String getValue()
   {
      return value;
   }

   public String getValueType()
   {
      // Support only SKI at the moment
      return SKI_TYPE;
   }

   public Document getDocument()
   {
      return doc;
   }

   public byte[] getIdentifier() throws WSSecurityException
   {
      if (value == null)
         return null;

      try
      {
         return Base64.decode(value);
      }
      catch (Base64DecodingException e)
      {
         throw new WSSecurityException("Error decoding key identifier", e);
      }
   }

   public Element getElement()
   {
      if (cachedElement != null)
         return cachedElement;

      Element element = doc.createElementNS(Constants.WSSE_NS, Constants.WSSE_PREFIX + ":" + "KeyIdentifier");
      element.setAttribute("ValueType", getValueType());
      element.setAttribute("EncodingType", Constants.BASE64_ENCODING_TYPE);
      element.appendChild(doc.createTextNode(value));

      cachedElement = element;
      return cachedElement;
   }
}
