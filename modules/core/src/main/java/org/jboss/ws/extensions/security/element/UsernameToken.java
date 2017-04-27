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

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.xml.security.utils.XMLUtils;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Jason T. Greene
 */
public class UsernameToken implements Token
{
   private String username;

   private String password;
   
   private boolean digest;
   
   private String nonce;
   
   private String created;

   private Document doc;

   private String id;

   private Element cachedElement;

   public UsernameToken(String username, String password, Document doc, boolean digest, String nonce, String created)
   {
      this.username = username;
      this.password = password;
      this.doc = doc;
      this.digest = digest;
      this.nonce = nonce;
      this.created = created;
   }

   @SuppressWarnings("unchecked")
   public UsernameToken(Element element) throws WSSecurityException
   {
      this.doc = element.getOwnerDocument();
      String id = element.getAttributeNS(Constants.WSU_NS, Constants.ID);
      if (id != null && id.length() > 0)
         setId(id);

      Element child = Util.getFirstChildElement(element);
      if (child == null || ! Constants.WSSE_NS.equals(child.getNamespaceURI()) || ! "Username".equals(child.getLocalName()))
         throw new WSSecurityException("Username child expected in UsernameToken element");

      this.username = XMLUtils.getFullTextChildrenFromElement(child);

      child = Util.getNextSiblingElement(child);
      if (child == null || ! Constants.WSSE_NS.equals(child.getNamespaceURI()) || ! "Password".equals(child.getLocalName()))
         throw new WSSecurityException("Password child expected in UsernameToken element");

      this.password = XMLUtils.getFullTextChildrenFromElement(child);
      String passwordType = child.getAttribute("Type");
      this.digest = Constants.PASSWORD_DIGEST_TYPE.equals(passwordType);
      
      Iterator<Element> itNonce = DOMUtils.getChildElements(element, new QName(Constants.WSSE_NS, "Nonce"));
      if (itNonce != null && itNonce.hasNext())
      {
         Element elem = itNonce.next();
         String encodingType = elem.getAttribute("EncodingType");
         if (encodingType != null && encodingType.length() > 0 && !Constants.BASE64_ENCODING_TYPE.equalsIgnoreCase(encodingType))
            throw new WSSecurityException("Unsupported nonce encoding type: " + encodingType);
         this.nonce = XMLUtils.getFullTextChildrenFromElement(elem);
      }
      
      Iterator<Element> itCreated = DOMUtils.getChildElements(element, new QName(Constants.WSSE_NS, "Created"));
      if (itCreated != null && itCreated.hasNext())
      {
         this.created = XMLUtils.getFullTextChildrenFromElement(itCreated.next());
      }
   }

   public String getId()
   {
      if (id == null)
         id = Util.generateId("token");

      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }


   public String getPassword()
   {
      return password;
   }


   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public Element getElement()
   {
      if (cachedElement != null)
         return cachedElement;

      Element element = doc.createElementNS(Constants.WSSE_NS, Constants.WSSE_PREFIX + ":" + "UsernameToken");
      element.setAttributeNS(Constants.WSU_NS, Constants.WSU_ID, getId());
      Element child = doc.createElementNS(Constants.WSSE_NS, Constants.WSSE_PREFIX + ":" + "Username");
      child.appendChild(doc.createTextNode(username));
      element.appendChild(child);
      child = doc.createElementNS(Constants.WSSE_NS, Constants.WSSE_PREFIX + ":" + "Password");
      child.appendChild(doc.createTextNode(password));
      child.setAttribute("Type", digest ? Constants.PASSWORD_DIGEST_TYPE : Constants.PASSWORD_TEXT_TYPE);
      element.appendChild(child);
      if (digest)
      {
         if (nonce != null)
         {
            child = doc.createElementNS(Constants.WSSE_NS, Constants.WSSE_PREFIX + ":" + "Nonce");
            child.appendChild(doc.createTextNode(nonce));
            child.setAttribute("EncodingType", Constants.BASE64_ENCODING_TYPE);
            element.appendChild(child);
         }
         if (created != null)
         {
            child = doc.createElementNS(Constants.WSSE_NS, Constants.WSSE_PREFIX + ":" + "Created");
            child.appendChild(doc.createTextNode(created));
            element.appendChild(child);
         }
      }
      cachedElement = element;
      return cachedElement;
   }

   public Object getUniqueContent()
   {
      return null;
   }

   public boolean isDigest()
   {
      return digest;
   }

   public String getNonce()
   {
      return nonce;
   }

   public String getCreated()
   {
      return created;
   }
}
