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

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.xml.security.utils.XMLUtils;
import org.jboss.util.Base64;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class X509Token extends BinarySecurityToken
{
   private X509Certificate cert;

   public static final String TYPE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3";

   public X509Token(X509Certificate cert, Document doc)
   {
      super(doc);
      this.cert = cert;
   }

   public X509Token(Element element) throws WSSecurityException
   {
      super(element.getOwnerDocument());

      String id = element.getAttributeNS(Constants.WSU_NS, Constants.ID);
      if (id != null && id.length() > 0)
         setId(id);

      if (! Constants.BASE64_ENCODING_TYPE.equals(element.getAttribute("EncodingType")))
         throw new WSSecurityException("Invalid encoding type (only base64 is supported) for token:" + id);

      setCert(decodeCert(XMLUtils.getFullTextChildrenFromElement(element)));
   }

   @Override
   public String getEncodingType()
   {
      return Constants.BASE64_ENCODING_TYPE;
   }

   @Override
   public String getValueType()
   {
      return TYPE;
   }

   @Override
   public String getEncodedValue(boolean noWhitespace)
   {
      try
      {
         return Base64.encodeBytes(cert.getEncoded(), (noWhitespace) ? Base64.DONT_BREAK_LINES : 0);
      }
      catch (CertificateEncodingException e)
      {
         throw new RuntimeException("Could not encode X509 token", e);
      }
   }

   public X509Certificate getCert()
   {
      return cert;
   }

   public void setCert(X509Certificate cert)
   {
      this.cert = cert;
   }

   public X509Certificate decodeCert(String data) throws WSSecurityException
   {
      try
      {
         CertificateFactory factory = CertificateFactory.getInstance("X.509");
         return (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(Base64.decode(data)));
      }
      catch(Exception e)
      {
         throw new WSSecurityException("Error decoding BinarySecurityToken: " + e.getMessage());
      }
   }

   public Object getUniqueContent()
   {
      return cert;
   }
}
