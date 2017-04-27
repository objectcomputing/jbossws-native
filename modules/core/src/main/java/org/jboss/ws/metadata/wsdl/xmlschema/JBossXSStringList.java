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

import org.apache.xerces.xs.StringList;

/**
 * Implements the StringList interface
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 20, 2005
 */
public class JBossXSStringList implements StringList
{
   protected List<String> strList = new ArrayList<String>();

   public JBossXSStringList()
   {
   }

   public JBossXSStringList(List<String> alist)
   {
      this.strList = alist;
   }

   public JBossXSStringList(Collection<String> col)
   {
      this.strList.addAll(col);
   }

   /**
    *  The number of <code>GenericString</code>s in the list. The range of
    * valid child object indices is 0 to <code>length-1</code> inclusive.
    */
   public int getLength()
   {
      return strList.size();
   }

   /**
    *  Checks if the <code>GenericString</code> <code>item</code> is a member
    * of this list.
    * @param item  <code>GenericString</code> whose presence in this list is
    *   to be tested.
    * @return  True if this list contains the <code>GenericString</code>
    *   <code>item</code>.
    */
   public boolean contains(String item)
   {
      return strList.contains(item);
   }

   /**
    *  Returns the <code>index</code>th item in the collection or
    * <code>null</code> if <code>index</code> is greater than or equal to
    * the number of objects in the list. The index starts at 0.
    * @param index  index into the collection.
    * @return  The <code>GenericString</code> at the <code>index</code>th
    *   position in the <code>StringList</code>, or <code>null</code> if
    *   the index specified is not valid.
    */
   public String item(int index)
   {
      return (String)strList.get(index);
   }

   public List<String> toList()
   {
      return strList;
   }

   public void addItem(String str)
   {
      strList.add(str);
   }

   public String toString()
   {
      return strList.toString();
   }
}
