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
public class SecurityTokenReference
{
   private String id;

   private Reference reference;

   private Element cachedElement;

   public SecurityTokenReference(Reference reference)
   {
      this.reference = reference;
   }

   public SecurityTokenReference(Element element) throws WSSecurityException
   {
      if (! "SecurityTokenReference".equals(element.getLocalName()))
         throw new WSSecurityException("SecurityTokenReference was passed an invalid local name");

      String id = element.getAttributeNS(Constants.WSU_NS, Constants.ID);
      if (id == null || id.length() == 0)
         setId(id);

      Element child = Util.getFirstChildElement(element);
      if (child == null)
         throw new WSSecurityException("Invalid message, SecurityTokenRefence is empty: " + id);

      this.reference = Reference.getReference(child);
   }

   public Reference getReference()
   {
      return reference;
   }

   public String getId()
   {
      if (id == null)
         id = Util.generateId("reference");

      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public Element getElement() throws WSSecurityException
   {
      if (cachedElement != null)
         return cachedElement;

      Element referenceElement = reference.getElement();
      Document doc = referenceElement.getOwnerDocument();
      Element element = doc.createElementNS(Constants.WSSE_NS, Constants.WSSE_PREFIX + ":" + "SecurityTokenReference");
      element.setAttributeNS(Constants.WSU_NS, Constants.WSU_ID, getId());
      element.appendChild(referenceElement);

      cachedElement = element;
      return cachedElement;
   }
}
