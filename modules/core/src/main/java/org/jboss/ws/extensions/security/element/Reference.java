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

import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

abstract public class Reference implements SecurityElement
{
   public static final String DIRECT_REFERENCE = "directReference";
   public static final String KEY_IDENTIFIER = "keyIdentifier";
   public static final String X509ISSUER_SERIAL = "x509IssuerSerial";
   
   public static Reference getReference(Element element) throws WSSecurityException
   {
      String name = element.getLocalName();
      if ("Reference".equals(name))
      {
         return new DirectReference(element);
      }
      else if ("KeyIdentifier".equals(name))
      {
         return new KeyIdentifier(element);
      }
      else if ("X509Data".equals(name))
      {
         return new X509IssuerSerial(element);
      }
      else
      {
         throw new WSSecurityException("Unkown reference element: " + name);
      }
   }
   
   public static Reference getReference(String tokenRefType, Document message, BinarySecurityToken token) throws WSSecurityException
   {
      if (tokenRefType == null || DIRECT_REFERENCE.equals(tokenRefType))
      {
         return new DirectReference(message, token);
      }
      else if (KEY_IDENTIFIER.equals(tokenRefType))
      {
         return new KeyIdentifier(message, token);
      }
      else if (X509ISSUER_SERIAL.equals(tokenRefType))
      {
         return new X509IssuerSerial(message, token);
      }
      else
      {
         throw new WSSecurityException("Unkown token reference type: " + tokenRefType);
      }
   }
}
