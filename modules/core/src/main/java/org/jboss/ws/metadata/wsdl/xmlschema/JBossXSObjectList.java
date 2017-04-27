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
import java.util.List;

import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;

/**
 * Xerces Schema API
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 20, 2005
 */
public class JBossXSObjectList implements XSObjectList
{
   protected List list = new ArrayList();

   public JBossXSObjectList()
   {
   }

   public JBossXSObjectList(List lst)
   {
      this.list = lst;
   }


   /**
    *  The number of <code>XSObjects</code> in the list. The range of valid
    * child object indices is 0 to <code>length-1</code> inclusive.
    */
   public int getLength()
   {
      int len = list.size();
      return len;
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
      return  (XSObject)list.get(index);
   }

   public void addItem(XSObject xsobj)
   {
      list.add(xsobj);
   }

   public void addItem(XSObject xsobj, boolean shouldSort)
   {
      if(shouldSort)
         addItem(xsobj);
      else
         list.add(xsobj);
   }

   //CUSTOM METHODS
   public void addObjects(XSObjectList lst)
   {
      if(lst instanceof JBossXSObjectList)
         list.addAll(((JBossXSObjectList)lst).toList());
      else
      {
         int len = lst != null ? lst.getLength() : 0 ;
         for(int i=0; i < len; i++)
         {
            list.add(lst.item(i));
         }
      }
   }

   public List toList()
   {
      return list;
   }
}
