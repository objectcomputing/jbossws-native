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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.xml.security.encryption.Reference;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.Util;
import org.w3c.dom.Element;

/**
 * @author Jason T. Greene
 */
public class ReferenceList
{

   private LinkedList<String> references = new LinkedList<String>();

   public ReferenceList()
   {
   }

   public ReferenceList(Element element)
   {
      Element child = Util.getFirstChildElement(element);
      while (child != null)
      {
         // Skip key references, they aren't used by WS-Security
         if (Constants.XML_ENCRYPTION_NS.equals(child.getNamespaceURI())
               && Constants.XENC_DATAREFERENCE.equals(child.getLocalName()))
         {
            String uri = child.getAttribute("URI");
            if (uri != null && uri.length() > 1 && uri.charAt(0) == '#')
               references.add(uri.substring(1));
         }

         child = Util.getNextSiblingElement(child);
      }
   }

   public ReferenceList(org.apache.xml.security.encryption.ReferenceList list)
   {
      Iterator i = list.getReferences();

      while (i.hasNext())
      {
         Reference r = (Reference) i.next();
         references.add(r.getURI());
      }
   }

   public void add(String id)
   {
      // We prepend so that decryption can handle nested elements by just
      // following the order
      references.addFirst(id);
   }

   public void populateRealReferenceList(org.apache.xml.security.encryption.ReferenceList list)
   {
      for (String i : references)
      {
         list.add(list.newDataReference("#" + i));
      }
   }

   public Collection<String> getAllReferences()
   {
      return references;
   }
}
