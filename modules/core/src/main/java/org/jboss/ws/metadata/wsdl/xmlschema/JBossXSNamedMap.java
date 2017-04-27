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
package org.jboss.ws.metadata.wsdl.xmlschema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;

/**
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 20, 2005
 */
public class JBossXSNamedMap  implements XSNamedMap
{
   protected List<XSObject> list = new ArrayList<XSObject>();
   /**
    * The number of <code>XSObjects</code> in the <code>XSObjectList</code>.
    * The range of valid child object indices is 0 to <code>length-1</code>
    * inclusive.
    */
   public int getLength()
   {
      return list.size();
   }

   /**
    *  Returns the <code>index</code>th item in the collection or
    * <code>null</code> if <code>index</code> is greater than or equal to
    * the number of objects in the list. The index starts at 0.
    * @param index  index into the collection.
    * @return  The <code>XSObject</code> at the <code>index</code>th
    *   position in the <code>XSObjectList</code>, or <code>null</code> if
    *   the index specified is not valid.
    */
   public XSObject item(int index)
   {
      return (XSObject)list.get(index);
   }

   /**
    * Retrieves an <code>XSObject</code> specified by local name and
    * namespace URI.
    * <br>Per XML Namespaces, applications must use the value <code>null</code> as the
    * <code>namespace</code> parameter for methods if they wish to specify
    * no namespace.
    * @param namespace The namespace URI of the <code>XSObject</code> to
    *   retrieve, or <code>null</code> if the <code>XSObject</code> has no
    *   namespace.
    * @param localName The local name of the <code>XSObject</code> to
    *   retrieve.
    * @return A <code>XSObject</code> (of any type) with the specified local
    *   name and namespace URI, or <code>null</code> if they do not
    *   identify any object in this map.
    */
   public XSObject itemByName(String namespace, String localName)
   {
      XSObject xso = null; 
      //Since our list may contain types from xerces implementation
      for(XSObject obj: list)
      {
         if(localName.equals(obj.getName()) &&
               namespace.equals(obj.getNamespace()))
            return obj;
      }

      return xso;
   }

   public void addItem(XSObject obj)
   {
      list.add(obj); 
   }
   
   public void addItems(Collection col)
   {
      list.addAll(col);
   }
   
   public List<XSObject> toList()
   {
      return list;
   }
}
