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

import java.util.Collection;
import java.util.HashSet;

import javax.crypto.SecretKey;

import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.SecurityStore;
import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.extensions.security.element.EncryptedKey;
import org.jboss.ws.extensions.security.element.ReferenceList;
import org.jboss.ws.extensions.security.element.SecurityHeader;
import org.jboss.ws.extensions.security.element.SecurityProcess;
import org.jboss.ws.extensions.security.exception.FailedCheckException;
import org.jboss.ws.extensions.security.exception.InvalidSecurityHeaderException;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DecryptionOperation implements DecodingOperation
{

   private SecurityHeader header;

   private SecurityStore store;

   public DecryptionOperation(SecurityHeader header, SecurityStore store) throws WSSecurityException
   {
      this.header = header;
      this.store = store;
   }

   private boolean isContent(Element element)
   {
      return Constants.XENC_CONTENT_TYPE.equals(element.getAttribute("Type"));
   }

   private String getEncryptionAlgorithm(Element element) throws WSSecurityException
   {
      element = Util.findElement(element, "EncryptionMethod", Constants.XML_ENCRYPTION_NS);
      if (element == null)
         throw new InvalidSecurityHeaderException("Encrypted element corrupted, no encryption method");

      String alg = element.getAttribute("Algorithm");
      if (alg == null || alg.length() == 0)
         throw new InvalidSecurityHeaderException("Encrypted element corrupted, no algorithm specified");

      return alg;
   }

   private String decryptElement(Element element, SecretKey key) throws WSSecurityException
   {
      Element previous;
      boolean parent;
      boolean isContent;

      // We find the decrypted element by traversing to the element before the
      // encrypted data. If there is no sibling before the encrypted data, then
      // we traverse to the parent.
      // "Now take a step back . . . and then a step forward . . . and then a
      // step back . . . and then we're cha-chaing." -Chris Knight
      parent = isContent = isContent(element);
      if (parent)
      {
         previous = (Element) element.getParentNode();
      }
      else
      {
         previous = Util.getPreviousSiblingElement(element);
         if (previous == null)
         {
            parent = true;
            previous = (Element) element.getParentNode();
         }
      }

      String alg = getEncryptionAlgorithm(element);
      try
      {
         XMLCipher cipher = XMLCipher.getInstance(alg);
         cipher.init(XMLCipher.DECRYPT_MODE, key);
         cipher.doFinal(element.getOwnerDocument(), element);
      }
      catch (XMLEncryptionException e)
      {
         throw new FailedCheckException("Decryption was invalid.");
      }
      catch (Exception e)
      {
         throw new WSSecurityException("Could not decrypt element: " + e.getMessage(), e);
      }

      if (isContent)
         return Util.getWsuId(previous);

      Element decrypted = (parent) ? Util.getFirstChildElement(previous) : Util.getNextSiblingElement(previous);
      if (decrypted == null)
         return null;

      return Util.getWsuId(decrypted);
   }

   private boolean isEncryptedData(Element element)
   {
      return "EncryptedData".equals(element.getLocalName()) && Constants.XML_ENCRYPTION_NS.equals(element.getNamespaceURI());
   }

   public Collection<String> process(Document message, SecurityProcess process) throws WSSecurityException
   {
      Collection<String> ids = new HashSet<String>();
      EncryptedKey key = (EncryptedKey) process;
      ReferenceList list = key.getReferenceList();
      for (String uri : list.getAllReferences())
      {
         Element element = Util.findElementByWsuId(message.getDocumentElement(), uri);
         if (element == null)
            throw new WSSecurityException("A reference list refered to an element that was not found: " + uri);

         if (!isEncryptedData(element))
            throw new WSSecurityException("Malformed reference list, a non encrypted data element was referenced: " + uri);

         ids.add(decryptElement(element, key.getSecretKey()));
      }

      return ids;
   }
}
