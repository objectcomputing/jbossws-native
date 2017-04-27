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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.xml.security.utils.XMLUtils;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class Timestamp implements SecurityElement
{

   private String id = "timestamp";

   private Integer ttl;

   private Document doc;

   private Calendar created;

   private Calendar expires;

   private Element cachedElement;

   public Timestamp(Integer ttl, Document doc)
   {
      this.doc = doc;
      this.ttl = ttl;
   }

   public Timestamp(Element element) throws WSSecurityException
   {
      this.doc = element.getOwnerDocument();
      String id = element.getAttributeNS(Constants.WSU_NS, Constants.ID);
      if (id != null && id.length() > 0)
         this.id = id;

      Element child = Util.getFirstChildElement(element);
      if (child == null || !Constants.WSU_NS.equals(child.getNamespaceURI()) || !"Created".equals(child.getLocalName()))
         throw new WSSecurityException("Created child expected in Timestamp element");

      this.created = SimpleTypeBindings.unmarshalDateTime(XMLUtils.getFullTextChildrenFromElement(child));

      child = Util.getNextSiblingElement(child);
      if (child == null)
         return;

      this.expires = SimpleTypeBindings.unmarshalDateTime(XMLUtils.getFullTextChildrenFromElement(child));
   }

   private void setupTime()
   {
      created = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
      if (ttl != null)
      {
         expires = (Calendar) created.clone();
         expires.add(Calendar.SECOND, ttl.intValue());
      }
   }

   private String getId()
   {
      return id;
   }

   public Calendar getCreated()
   {
      return created;
   }

   public Calendar getExpires()
   {
      return expires;
   }

   public Element getElement() throws WSSecurityException
   {
      if (cachedElement != null)
         return cachedElement;

      setupTime();

      Element element = doc.createElementNS(Constants.WSU_NS, Constants.WSU_PREFIX + ":" + "Timestamp");
      element.setAttributeNS(Constants.WSU_NS, Constants.WSU_ID, getId());
      Element child = doc.createElementNS(Constants.WSU_NS, Constants.WSU_PREFIX + ":" + "Created");
      child.appendChild(doc.createTextNode(SimpleTypeBindings.marshalDateTime(created)));
      element.appendChild(child);

      if (expires != null)
      {
         child = doc.createElementNS(Constants.WSU_NS, Constants.WSU_PREFIX + ":" + "Expires");
         child.appendChild(doc.createTextNode(SimpleTypeBindings.marshalDateTime(expires)));
         element.appendChild(child);
      }

      cachedElement = element;
      return cachedElement;
   }
}
