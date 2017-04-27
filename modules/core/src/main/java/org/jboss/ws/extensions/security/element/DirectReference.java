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

import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>DirectReference</code> is a reference type within a
 * <code>SecurityTokenReference</code> that referes to a token that is
 * retrievable via a URL. This is typically used for refering to a
 * <code>BinarySecurityToken</code> that is within the same message.
 *
 * @see org.jboss.ws.extensions.security.element.SecurityTokenReference
 * @see org.jboss.ws.extensions.security.element.BinarySecurityToken
 *
 * @author Jason T. Greene
 */
public class DirectReference extends Reference
{

   private Document doc;

   private String uri;

   private String valueType;

   private Element cachedElement;

   public DirectReference(Document doc)
   {
      this.doc = doc;
   }

   public DirectReference(Document doc, BinarySecurityToken token)
   {
      this.doc = doc;
      referenceToken(token);
   }

   public DirectReference(Element element) throws WSSecurityException
   {
      this.doc = element.getOwnerDocument();

      if (!"Reference".equals(element.getLocalName()))
         throw new WSSecurityException("Invalid message, invalid local name on a DirectReference");

      String uri = element.getAttribute("URI");
      if (uri == null || uri.length() == 0)
         throw new WSSecurityException("Inavliad message, Reference element is missing a URI");

      setUri(uri);

      String valueType = element.getAttribute("ValueType");
      if (valueType == null || valueType.length() == 0)
         throw new WSSecurityException("Inavliad message, Reference element is missing a ValueType");

      setValueType(valueType);
   }

   public String getUri()
   {
      return uri;
   }

   public void referenceToken(BinarySecurityToken token)
   {
      setUri("#" + token.getId());
      setValueType(token.getValueType());
   }

   public void setUri(String uri)
   {
      this.uri = uri;
   }

   public String getValueType()
   {
      return valueType;
   }

   public void setValueType(String valueType)
   {
      this.valueType = valueType;
   }

   public Element getElement()
   {
      if (cachedElement != null)
         return cachedElement;

      Element element = doc.createElementNS(Constants.WSSE_NS, Constants.WSSE_PREFIX + ":" + "Reference");
      element.setAttribute("ValueType", getValueType());
      element.setAttribute("URI", getUri());

      cachedElement = element;
      return cachedElement;
   }
}
