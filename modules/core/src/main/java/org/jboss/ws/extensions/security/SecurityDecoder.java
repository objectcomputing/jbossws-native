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

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.jboss.ws.extensions.security.element.EncryptedKey;
import org.jboss.ws.extensions.security.element.SecurityHeader;
import org.jboss.ws.extensions.security.element.SecurityProcess;
import org.jboss.ws.extensions.security.element.Signature;
import org.jboss.ws.extensions.security.element.Timestamp;
import org.jboss.ws.extensions.security.element.Token;
import org.jboss.ws.extensions.security.element.UsernameToken;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.jboss.ws.extensions.security.nonce.NonceFactory;
import org.jboss.ws.extensions.security.operation.AuthorizeOperation;
import org.jboss.ws.extensions.security.operation.DecryptionOperation;
import org.jboss.ws.extensions.security.operation.ReceiveUsernameOperation;
import org.jboss.ws.extensions.security.operation.ReceiveX509Certificate;
import org.jboss.ws.extensions.security.operation.RequireEncryptionOperation;
import org.jboss.ws.extensions.security.operation.RequireOperation;
import org.jboss.ws.extensions.security.operation.RequireSignatureOperation;
import org.jboss.ws.extensions.security.operation.SignatureVerificationOperation;
import org.jboss.ws.extensions.security.operation.TimestampVerificationOperation;
import org.jboss.ws.metadata.wsse.Authenticate;
import org.jboss.ws.metadata.wsse.Authorize;
import org.jboss.ws.metadata.wsse.TimestampVerification;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class SecurityDecoder
{
   private Element headerElement;

   private Calendar now =  null;

   private SecurityHeader header;

   private Document message;
   
   private NonceFactory nonceFactory;

   private SecurityStore store;
   
   private TimestampVerification timestampVerification;
   
   private Authenticate authenticate;   

   private HashSet<String> signedIds = new HashSet<String>();

   private HashSet<String> encryptedIds = new HashSet<String>();

   public SecurityDecoder(SecurityStore store, NonceFactory nonceFactory, TimestampVerification timestampVerification, Authenticate authenticate)
   {
      org.apache.xml.security.Init.init();
      this.store = store;
      this.nonceFactory = nonceFactory;
      this.timestampVerification = timestampVerification;
      this.authenticate = authenticate;
   }

   /**
    * A special constructor that allows you to use a different value when validating the message.
    * DO NOT USE THIS UNLESS YOU REALLY KNOW WHAT YOU ARE DOING!.
    *
    * @param SecurityStore the security store that contains key and trust information
    * @param now The timestamp to use as the current time when validating a message expiration
    */
   public SecurityDecoder(SecurityStore store, Calendar now, NonceFactory nonceFactory, TimestampVerification timestampVerification, Authenticate authenticate)
   {
      this(store, nonceFactory, timestampVerification, authenticate);
      this.now = now;
   }

   private Element getHeader(Document message) throws WSSecurityException
   {
      Element header = Util.findElement(message.getDocumentElement(), "Security", Constants.WSSE_NS);
      if (header == null)
         throw new WSSecurityException("Expected security header was not found");

      return header;
   }

   private void detachHeader()
   {
      headerElement.getParentNode().removeChild(headerElement);
   }


   private void decode() throws WSSecurityException
   {
      // Validate a timestamp if it is present
      Timestamp timestamp = header.getTimestamp();

      if (timestamp != null)
      {
         TimestampVerificationOperation operation =
            (now == null) ? new TimestampVerificationOperation(timestampVerification) : new TimestampVerificationOperation(now);
         operation.process(message, timestamp);
      }

      if (authenticate == null || authenticate.isUsernameAuth())
      {
         for (Token token : header.getTokens())
         {
            if (token instanceof UsernameToken)
               new ReceiveUsernameOperation(header, store, (nonceFactory != null ? nonceFactory.getStore() : null)).process(message, token);
         }
      }

      signedIds.clear();
      encryptedIds.clear();

      SignatureVerificationOperation signatureVerifier = new SignatureVerificationOperation(header, store);
      DecryptionOperation decrypter = new DecryptionOperation(header, store);

      for (SecurityProcess process : header.getSecurityProcesses())
      {
         // If this list gets much larger it should probably be a hash lookup
         if (process instanceof Signature)
         {
            Signature signature = (Signature)process;
            Collection<String> ids = signatureVerifier.process(message, signature);
            if (ids != null)
              signedIds.addAll(ids);
            if (authenticate != null && authenticate.isSignatureCertAuth())
               new ReceiveX509Certificate(authenticate.getSignatureCertAuth().getCertificatePrincipal()).process(message, signature.getSecurityToken());
         }
         else if (process instanceof EncryptedKey)
         {
            Collection<String> ids = decrypter.process(message, process);
            if (ids != null)
               encryptedIds.addAll(ids);
         }
      }      
      
   }

   public void verify(List<RequireOperation> requireOperations) throws WSSecurityException
   {
      if (requireOperations == null)
         return;

      for (RequireOperation op : requireOperations)
      {
         Collection<String> processedIds = null;
         if (op instanceof RequireSignatureOperation)
         {
            processedIds = signedIds;
         }
         else if (op instanceof RequireEncryptionOperation)
         {
            processedIds = encryptedIds;
         }
         op.process(message, header, processedIds);
      }
   }

   public void decode(Document message) throws WSSecurityException
   {
      decode(message, getHeader(message));
   }

   public void decode(Document message, Element headerElement) throws WSSecurityException
   {
      this.headerElement = headerElement;
      this.header = new SecurityHeader(this.headerElement, store);
      this.message = message;

      decode();
   }

   public void complete()
   {
      // On completion we must remove the header so that no one else can process this
      // message (required by the specification)
      detachHeader();
   }
}
