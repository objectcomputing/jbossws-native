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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.jboss.ws.extensions.security.STRTransform;
import org.jboss.ws.extensions.security.SecurityStore;
import org.jboss.ws.extensions.security.SignatureKeysAssociation;
import org.jboss.ws.extensions.security.WsuIdResolver;
import org.jboss.ws.extensions.security.element.SecurityHeader;
import org.jboss.ws.extensions.security.element.SecurityProcess;
import org.jboss.ws.extensions.security.element.Signature;
import org.jboss.ws.extensions.security.exception.FailedCheckException;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;

public class SignatureVerificationOperation implements DecodingOperation
{
   private SecurityHeader header;

   private SecurityStore store;

   public SignatureVerificationOperation(SecurityHeader header, SecurityStore store) throws WSSecurityException
   {
      this.header = header;
      this.store = store;
   }

   public Collection<String> process(Document message, SecurityProcess process) throws WSSecurityException
   {
      Signature signature = (Signature) process;
      XMLSignature xmlSig = signature.getSignature();

      xmlSig.addResourceResolver(new WsuIdResolver(message));
      STRTransform.setSecurityStore(store);

      try
      {
         if (! xmlSig.checkSignatureValue(signature.getPublicKey()))
            throw new FailedCheckException("Signature is invalid.");
         
         SignatureKeysAssociation.saveKey(signature.getPublicKey());
      }
      catch (XMLSignatureException e)
      {
         throw new WSSecurityException("An unexpected error occured while verifying signature", e);
      }
      finally
      {
         STRTransform.setSecurityStore(null);
      }

      SignedInfo info = xmlSig.getSignedInfo();
      int length = info.getLength();
      Collection<String> processed = new ArrayList<String>(length);
      try
      {
         for (int i = 0; i < length; i++)
         {
            String uri = info.item(i).getURI();
            if (uri != null && uri.length() > 1 && uri.charAt(0)=='#')
               processed.add(uri.substring(1));
         }
      }
      catch (XMLSecurityException e)
      {
         throw new WSSecurityException("Could not extract references", e);
      }

      return processed;
   }
}
