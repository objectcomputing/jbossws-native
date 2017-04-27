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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.extensions.security.QNameTarget;
import org.jboss.ws.extensions.security.SecurityStore;
import org.jboss.ws.extensions.security.SignatureKeysAssociation;
import org.jboss.ws.extensions.security.Target;
import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.extensions.security.element.EncryptedKey;
import org.jboss.ws.extensions.security.element.Reference;
import org.jboss.ws.extensions.security.element.ReferenceList;
import org.jboss.ws.extensions.security.element.SecurityHeader;
import org.jboss.ws.extensions.security.element.X509Token;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EncryptionOperation implements EncodingOperation
{
   private List<Target> targets;
   private String alias;
   private String algorithm;
   private String wrap;
   private String tokenRefType;
   
   private static class Algorithm
   {
      Algorithm(String jceName, String xmlName, int size)
      {
         this.jceName = jceName;
         this.xmlName = xmlName;
         this.size = size;
      }

      public String jceName;
      public String xmlName;
      public int size;
   }

   private static HashMap<String, Algorithm> algorithms;

   private static final String DEFAULT_ALGORITHM = "aes-128";

   static
   {
      algorithms = new HashMap<String, Algorithm>(4);
      algorithms.put("aes-128", new Algorithm("AES", XMLCipher.AES_128, 128));
      algorithms.put("aes-192", new Algorithm("AES", XMLCipher.AES_192, 192));
      algorithms.put("aes-256", new Algorithm("AES", XMLCipher.AES_256, 256));
      algorithms.put("tripledes", new Algorithm("TripleDes", XMLCipher.TRIPLEDES, 168));
   }

   public EncryptionOperation(List<Target> targets, String alias, String algorithm, String wrap, String tokenRefType)
   {
      super();
      this.targets = targets;
      this.alias = alias;
      this.algorithm = algorithm;
      this.wrap = wrap;
      this.tokenRefType = tokenRefType;
   }

   private void processTarget(XMLCipher cipher, Document message, Target target, ReferenceList list, SecretKey key) throws WSSecurityException
   {
      if (!(target instanceof QNameTarget))
         throw new NotImplementedException();

      QName name = ((QNameTarget)target).getName();

      Element element = Util.findElement(message.getDocumentElement(), name);
      if (element == null)
         throw new RuntimeException("Could not find element");

      // Ensure that the element has an id, so that encryption verification can be performed
      Util.assignWsuId(element);

      try
      {
         cipher.init(XMLCipher.ENCRYPT_MODE, key);
         EncryptedData encrypted = cipher.getEncryptedData();
         String id = Util.generateId("encrypted");
         encrypted.setId(id);
         list.add(id);
         cipher.doFinal(message, element, target.isContent());
      }
      catch (Exception e)
      {
         throw new WSSecurityException("Error encrypting target: " + name, e);
      }
   }

   public SecretKey getSecretKey(String algorithm) throws WSSecurityException
   {
      Algorithm alg = algorithms.get(algorithm);

      try
      {
         KeyGenerator kgen = KeyGenerator.getInstance(alg.jceName);
         kgen.init(alg.size);
         return kgen.generateKey();
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new WSSecurityException(e.getMessage());
      }
   }
   
   public void process(Document message, SecurityHeader header, SecurityStore store) throws WSSecurityException
   {
      if (! algorithms.containsKey(algorithm))
         algorithm = DEFAULT_ALGORITHM;

      SecretKey secretKey = getSecretKey(algorithm);
      XMLCipher cipher;
      try
      {
         cipher = XMLCipher.getInstance(algorithms.get(algorithm).xmlName);
         cipher.init(XMLCipher.ENCRYPT_MODE, secretKey);
      }
      catch (XMLSecurityException e)
      {
         throw new WSSecurityException("Error initializing xml cipher" + e.getMessage(), e);
      }

      ReferenceList list = new ReferenceList();

      if (targets == null || targets.size() == 0)
      {
         // By default we encrypt the content of the body element
         String namespace = message.getDocumentElement().getNamespaceURI();
         processTarget(cipher, message, new QNameTarget(new QName(namespace, "Body"), true), list, secretKey);
      }
      else
      {
         for (Target target : targets)
            processTarget(cipher, message, target, list, secretKey);
      }
      
      X509Certificate cert = getCertificate(store, alias);
      X509Token token = (X509Token) header.getSharedToken(cert);

      // Can we reuse an existing token?
      if (token == null)
      {
         token = new X509Token(cert, message);
         if (tokenRefType == null || Reference.DIRECT_REFERENCE.equals(tokenRefType))
            header.addToken(token);
      }

      EncryptedKey eKey = new EncryptedKey(message, secretKey, token, list, wrap, tokenRefType);
      header.addSecurityProcess(eKey);
   }
   
   @SuppressWarnings("unchecked")
   private X509Certificate getCertificate(SecurityStore store, String alias) throws WSSecurityException
   {
      X509Certificate cert = null;
      if (alias != null)
      {
         cert = store.getCertificate(alias);
         if (cert == null)
            throw new WSSecurityException("Cannot load certificate from keystore; alias = " + alias);
      }
      else
      {
         List<PublicKey> publicKeys = SignatureKeysAssociation.getPublicKeys();
         if (publicKeys != null && publicKeys.size() == 1)
            cert = store.getCertificateByPublicKey(publicKeys.iterator().next());
         if (cert == null)
            throw new WSSecurityException("Cannot get the certificate for message encryption! Verify the keystore contents, " +
            		"considering the certificate is obtained through the alias specified in the encrypt configuration element " +
            		"or (server side only) through a single key used to sign the incoming message.");
      }
      return cert;
   }
   
   
   public static boolean probeUnlimitedCrypto() throws WSSecurityException
   {
      try
      {
         //Check AES-256
         KeyGenerator kgen = KeyGenerator.getInstance("AES");
         kgen.init(256);
         SecretKey key = kgen.generateKey();
         Cipher c = Cipher.getInstance("AES");
         c.init(Cipher.ENCRYPT_MODE, key);
         
         //Check Blowfish
         kgen = KeyGenerator.getInstance("Blowfish");
         key = kgen.generateKey();
         c = Cipher.getInstance("Blowfish");
         c.init(Cipher.ENCRYPT_MODE, key);
         
         return true;
      }
      catch (InvalidKeyException e)
      {
         return false;
      }
      catch (Exception e)
      {
         throw new WSSecurityException("Error probing cryptographic permissions", e);
      }
   }
}
