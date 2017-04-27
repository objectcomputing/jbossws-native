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

import java.util.HashMap;
import java.util.LinkedList;

import org.jboss.ws.extensions.security.BinarySecurityTokenValidator;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.KeyResolver;
import org.jboss.ws.extensions.security.SecurityStore;
import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.extensions.security.exception.UnsupportedSecurityTokenException;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>SecurityHeader</code> represents the wsse:security element of WS-Security,
 * and is responsible for storing the processing state of a message.
 *
 * @author Jason T. Greene
 */
public class SecurityHeader implements SecurityElement
{
   private Document document;

   private Timestamp timestamp;

   private LinkedList<Token> tokens = new LinkedList<Token>();

   private HashMap<Object, Token> sharedTokens = new HashMap<Object, Token>();

   private LinkedList<SecurityProcess> securityProcesses = new LinkedList<SecurityProcess>();

   // Looks like this is only for embedded tokens
   private LinkedList<SecurityTokenReference> securityTokenReferences = new LinkedList<SecurityTokenReference>();

   public SecurityHeader(Document document)
   {
      this.document = document;
   }

   public SecurityHeader(Element element, SecurityStore store) throws WSSecurityException
   {
      document = element.getOwnerDocument();
      KeyResolver resolver = new KeyResolver(store);
      BinarySecurityTokenValidator validator = new BinarySecurityTokenValidator(store);
      Element child = Util.getFirstChildElement(element);
      while  (child != null)
      {
         String tag = child.getLocalName();

         if (tag.equals("BinarySecurityToken"))
         {
            BinarySecurityToken token = BinarySecurityToken.createBinarySecurityToken(child);
            validator.validateToken(token);
            resolver.cacheToken(token);
            tokens.add(token);
         }
         else if (tag.equals("UsernameToken"))
            tokens.add(new UsernameToken(child));
         else if (tag.equals("Timestamp"))
            timestamp = new Timestamp(child);
         else if (tag.equals("Signature"))
            securityProcesses.add(new Signature(child, resolver));
         else if (tag.equals("EncryptedKey"))
            securityProcesses.add(new EncryptedKey(child, resolver));
         else if (tag.equals("ReferenceList"))
            throw new UnsupportedSecurityTokenException("ReferenceLists outside of encrypted keys (shared secrets) are not supported.");

         child = Util.getNextSiblingElement(child);
      }
   }

   public Timestamp getTimestamp()
   {
      return timestamp;
   }

   public void setTimestamp(Timestamp timestamp)
   {
      this.timestamp = timestamp;
   }

   /**
    * @return Returns the securityTokenReferences.
    */
   public LinkedList getSecurityTokenReferences()
   {
      return securityTokenReferences;
   }
   /**
    * @param securityTokenReferences The securityTokenReferences to set.
    */
   public void setSecurityTokenReferences(LinkedList<SecurityTokenReference> securityTokenReferences)
   {
      this.securityTokenReferences = securityTokenReferences;
   }
   /**
    * @return Returns the securityProcesses.
    */
   public LinkedList<SecurityProcess> getSecurityProcesses()
   {
      return securityProcesses;
   }

   /**
    * @param securityProcesses The securityProcesses to set.
    */
   public void setSecurityProcesses(LinkedList<SecurityProcess> securityProcesses)
   {
      this.securityProcesses = securityProcesses;
   }
   /**
    * @return the tokens.
    */
   public LinkedList<Token> getTokens()
   {
      return tokens;
   }

   public void addToken(Token token)
   {
      tokens.addFirst(token);
      Object content = token.getUniqueContent();
      if (content != null)
         sharedTokens.put(content, token);
   }

   public Token getSharedToken(Object uniqueContent)
   {
      if (uniqueContent == null)
         return null;

      return sharedTokens.get(uniqueContent);
   }

   public void addSecurityProcess(SecurityProcess process)
   {
      securityProcesses.addFirst(process);
   }

   public void addSecurityTokenReference(SecurityTokenReference reference)
   {
      securityTokenReferences.addFirst(reference);
   }

   public Element getElement() throws WSSecurityException
   {
      Element element = document.createElementNS(Constants.WSSE_NS, Constants.WSSE_HEADER);
      Util.addNamespace(element, Constants.WSSE_PREFIX, Constants.WSSE_NS);
      Util.addNamespace(element, Constants.WSU_PREFIX, Constants.WSU_NS);
      Util.addNamespace(element, Constants.XML_ENCRYPTION_PREFIX, Constants.XML_SIGNATURE_NS);

      if (timestamp != null)
         element.appendChild(timestamp.getElement());

      for (Token t : tokens)
         element.appendChild(t.getElement());

      for (SecurityTokenReference r : securityTokenReferences)
         element.appendChild(r.getElement());

      for (SecurityProcess p : securityProcesses)
         element.appendChild(p.getElement());

      return element;
   }
}
