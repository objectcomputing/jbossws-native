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

import org.apache.xml.security.utils.XMLUtils;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.extensions.security.exception.InvalidSecurityHeaderException;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>X509IssuerSerial</code> is a reference type within a
 * <code>SecurityTokenReference</code> that referes to a token that
 * using the Issuer's DN, and certificate serial number.
 *
 * @see org.jboss.ws.extensions.security.element.SecurityTokenReference
 * @see org.jboss.ws.extensions.security.element.BinarySecurityToken
 *
 * @author Jason T. Greene
 */
public class X509IssuerSerial extends Reference
{
   private Document doc;

   private String issuer;

   private String serial;

   private Element cachedElement;

   public X509IssuerSerial(Document doc, BinarySecurityToken token) throws WSSecurityException
   {
      this.doc = doc;
      referenceToken(token);
   }

   public X509IssuerSerial(Element element) throws WSSecurityException
   {
      this.doc = element.getOwnerDocument();

      if (! "X509Data".equals(element.getLocalName()))
         throw new InvalidSecurityHeaderException("Invalid message, invalid local name on a X509Data element");

      element = Util.getFirstChildElement(element);
      if (element == null)
         throw new InvalidSecurityHeaderException("X509DataElement empty");

      if (! element.getLocalName().equals("X509IssuerSerial"))
         throw new InvalidSecurityHeaderException("Only X509IssuerSerial is supported for an X509Data element");

      element = Util.getFirstChildElement(element);
      if (element == null)
         throw new InvalidSecurityHeaderException("X509IssuerSerial empty");


      while (element != null)
      {
         String name = element.getLocalName();
         if (name.equals("X509IssuerName"))
            issuer = XMLUtils.getFullTextChildrenFromElement(element);
         else if (name.equals("X509SerialNumber"))
            serial = XMLUtils.getFullTextChildrenFromElement(element);

         element = Util.getNextSiblingElement(element);
      }

      if (serial == null)
         throw new InvalidSecurityHeaderException("X509SerialNumber missing from X509IssuerSerial");

      if (issuer == null)
         throw new InvalidSecurityHeaderException("X509IssuerName missing from X509IssuerSerial");
   }

   public void referenceToken(BinarySecurityToken token) throws WSSecurityException
   {
      if (! (token instanceof X509Token))
         throw new WSSecurityException("X509IssuerSerial tried to reference something besides an X509 token");

      X509Token x509 = (X509Token) token;
      X509Certificate cert = x509.getCert();

      this.issuer = cert.getIssuerDN().toString();
      this.serial = cert.getSerialNumber().toString();
   }

   public String getIssuer()
   {
      return issuer;
   }

   public String getSerial()
   {
      return serial;
   }

   public Document getDocument()
   {
      return doc;
   }

   public Element getElement()
   {
      if (cachedElement != null)
         return cachedElement;

      Element element = doc.createElementNS(Constants.XML_SIGNATURE_NS, "ds:X509Data");
      Element issuerSerial = doc.createElementNS(Constants.XML_SIGNATURE_NS, "ds:X509IssuerSerial");
      element.appendChild(issuerSerial);

      element = doc.createElementNS(Constants.XML_SIGNATURE_NS, "ds:X509IssuerName");
      element.appendChild(doc.createTextNode(issuer));
      issuerSerial.appendChild(element);

      element = doc.createElementNS(Constants.XML_SIGNATURE_NS, "ds:X509SerialNumber");
      element.appendChild(doc.createTextNode(serial));
      issuerSerial.appendChild(element);

      cachedElement = element;
      return cachedElement;
   }
}
