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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import org.apache.xml.security.keys.KeyInfo;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.extensions.security.element.BinarySecurityToken;
import org.jboss.ws.extensions.security.element.DirectReference;
import org.jboss.ws.extensions.security.element.KeyIdentifier;
import org.jboss.ws.extensions.security.element.Reference;
import org.jboss.ws.extensions.security.element.SecurityTokenReference;
import org.jboss.ws.extensions.security.element.X509IssuerSerial;
import org.jboss.ws.extensions.security.element.X509Token;
import org.jboss.ws.extensions.security.exception.SecurityTokenUnavailableException;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Element;

/**
 * <code>KeyResolver</code> is responsible for locating security tokens
 * within a WS-Security message.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class KeyResolver
{
   private HashMap<String, BinarySecurityToken> tokenCache = new HashMap<String, BinarySecurityToken>();

   private SecurityStore store;

   public KeyResolver(SecurityStore store)
   {
      this.store = store;
   }

   private SecurityTokenReference extractSecurityTokenReference(KeyInfo info) throws WSSecurityException
   {
      Element child = Util.getFirstChildElement(info.getElement());
      if (child == null)
         throw new WSSecurityException("Empty KeyInfo");

      if (! child.getLocalName().equals("SecurityTokenReference"))
         throw new WSSecurityException("KeyInfo did not contain expected SecurityTokenReference, instead got: " + child.getLocalName());

      return new SecurityTokenReference(child);
   }

   public void cacheToken(BinarySecurityToken token)
   {
      tokenCache.put(token.getId(), token);
   }

   public BinarySecurityToken resolve(SecurityTokenReference reference) throws WSSecurityException
   {
      Reference ref = reference.getReference();
      if (ref instanceof DirectReference)
      {
         DirectReference direct = (DirectReference) ref;
         return resolveDirectReference(direct);
      }
      else if (ref instanceof KeyIdentifier)
      {
         KeyIdentifier identifier = (KeyIdentifier) ref;
         return resolveKeyIdentifier(identifier);
      }
      else if (ref instanceof X509IssuerSerial)
      {
         X509IssuerSerial issuerSerial = (X509IssuerSerial) ref;
         return resolveX509IssuerSerial(issuerSerial);
      }

      throw new NotImplementedException("Currently only DirectReference, KeyIdentifier and X509IssuerSerial are supported!");
   }

   private BinarySecurityToken resolveDirectReference(DirectReference direct) throws WSSecurityException
   {
      String id = direct.getUri().substring(1);

      BinarySecurityToken token = tokenCache.get(id);
      if (token == null)
         throw new SecurityTokenUnavailableException("Could not resolve token id: " + id);

      return token;
   }

   private BinarySecurityToken resolveKeyIdentifier(KeyIdentifier identifier) throws WSSecurityException
   {
      // Support only SKI at the moment
      X509Certificate cert = store.getCertificateBySubjectKeyIdentifier(identifier.getIdentifier());
      if (cert == null)
         throw new SecurityTokenUnavailableException("Could not locate certificate by key identifier");
      return new X509Token(cert, identifier.getDocument());
   }

   private BinarySecurityToken resolveX509IssuerSerial(X509IssuerSerial issuerSerial) throws WSSecurityException
   {
      X509Certificate cert = store.getCertificateByIssuerSerial(issuerSerial.getIssuer(), issuerSerial.getSerial());
     if (cert == null)
        throw new SecurityTokenUnavailableException("Could not locate certificate by issuer and serial number");

     return new X509Token(cert, issuerSerial.getDocument());
   }

   public X509Certificate resolveCertificate(SecurityTokenReference reference) throws WSSecurityException
   {
      BinarySecurityToken token = resolve(reference);

      if (! (token instanceof X509Token))
         throw new WSSecurityException("Expected X509Token, cache contained: " + token.getClass().getName());

      return ((X509Token)token).getCert();
   }

   public PublicKey resolvePublicKey(SecurityTokenReference reference) throws WSSecurityException
   {
      return resolveCertificate(reference).getPublicKey();
   }

   public PrivateKey resolvePrivateKey(SecurityTokenReference reference) throws WSSecurityException
   {
      return store.getPrivateKey(resolveCertificate(reference));
   }

   public BinarySecurityToken resolve(KeyInfo info) throws WSSecurityException
   {
      return resolve(extractSecurityTokenReference(info));
   }

   public X509Certificate resolveCertificate(KeyInfo info) throws WSSecurityException
   {
      return resolveCertificate(extractSecurityTokenReference(info));
   }

   public PublicKey resolvePublicKey(KeyInfo info) throws WSSecurityException
   {
      return resolvePublicKey(extractSecurityTokenReference(info));
   }

   public PrivateKey resolvePrivateKey(KeyInfo info) throws WSSecurityException
   {
      return resolvePrivateKey(extractSecurityTokenReference(info));
   }
}
