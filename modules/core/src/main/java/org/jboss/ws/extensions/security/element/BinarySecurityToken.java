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
import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Jason T. Greene
 */
abstract public class BinarySecurityToken implements Token
{
   private Document doc;

   private String id;

   private Element cachedElement;

   public static BinarySecurityToken createBinarySecurityToken(Element element) throws WSSecurityException
   {
      String valueType = element.getAttribute("ValueType");
      if (X509Token.TYPE.equals(valueType))
         return new X509Token(element);
      else
         throw new WSSecurityException("Unkown Binary Security Token!!!");
   }

   public BinarySecurityToken(Document doc)
   {
      this.doc = doc;
   }

   abstract public String getValueType();

   abstract public String getEncodingType();

   abstract public String getEncodedValue(boolean noWhitespace);

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

   public Element getElement()
   {
      if (cachedElement != null)
         return cachedElement;

      Element element = doc.createElementNS(Constants.WSSE_NS, Constants.WSSE_PREFIX + ":" + "BinarySecurityToken");
      element.setAttributeNS(Constants.WSU_NS, Constants.WSU_ID, getId());
      element.setAttribute("ValueType", getValueType());
      element.setAttribute("EncodingType", getEncodingType());
      element.appendChild(doc.createTextNode(getEncodedValue(false)));

      cachedElement = element;
      return cachedElement;
   }

   public Element getSTRTransformElement()
   {
      Element element = doc.createElementNS(Constants.WSSE_NS, Constants.WSSE_PREFIX + ":" + "BinarySecurityToken");
      Util.addNamespace(element, Constants.WSSE_PREFIX, Constants.WSSE_NS);
      element.setAttribute("ValueType", getValueType());
      element.appendChild(doc.createTextNode(getEncodedValue(true)));

      return element;
   }
}
