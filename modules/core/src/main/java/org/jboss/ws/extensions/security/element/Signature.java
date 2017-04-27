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

import java.security.PublicKey;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.jboss.logging.Logger;
import org.jboss.ws.extensions.security.KeyResolver;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Element;

/**
 * @author Jason T. Greene
 */
public class Signature implements SecurityProcess
{
   private static Logger log = Logger.getLogger(Signature.class);
   private XMLSignature signature;

   /* Used only for decoding */
   private PublicKey publicKey;
   /* Used only for jaas authentication */
   private BinarySecurityToken securityToken;

   private Element cachedElement;

   public Signature(XMLSignature signature)
   {
      this.signature = signature;
   }

   public Signature(Element element, KeyResolver resolver) throws WSSecurityException
   {
      try
      {
         signature = new XMLSignature(element, null);
         publicKey = resolver.resolvePublicKey(signature.getKeyInfo());
         try
         {
            securityToken = resolver.resolve(signature.getKeyInfo());
         }
         catch (Exception e)
         {
            //log exception and ignore, KeyInfo might not reference a security token
            log.debug("KeyInfo does not contain any reference to a binary security token.", e);
         }
      }
      catch (XMLSecurityException e)
      {
         throw new WSSecurityException("Error decoding xml signature: " + e.getMessage(), e);
      }
   }

   public XMLSignature getSignature()
   {
      return signature;
   }

   public void setSignature(XMLSignature signature)
   {
      this.signature = signature;
   }

   public Element getElement()
   {
      if (cachedElement != null)
         return cachedElement;

      cachedElement = signature.getElement();
      return cachedElement;
   }

   public PublicKey getPublicKey()
   {
      return publicKey;
   }
   
   public BinarySecurityToken getSecurityToken()
   {
      return securityToken;
   }
}
