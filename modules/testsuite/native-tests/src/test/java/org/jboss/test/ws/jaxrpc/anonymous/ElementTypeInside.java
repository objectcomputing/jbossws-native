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
package org.jboss.test.ws.jaxrpc.anonymous;

public class ElementTypeInside
{
   private String data2;

   public ElementTypeInside()
   {
   }

   public ElementTypeInside(String data2)
   {
      this.data2 = data2;
   }

   public String getData2()
   {
      return data2;
   }

   public void setData2(String data2)
   {
      this.data2 = data2;
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof ElementTypeInside))
         return false;

      ElementTypeInside other = (ElementTypeInside)obj;
      return toString().equals(other.toString());
   }

   public int hashCode()
   {
      return toString().hashCode();
   }

   public String toString()
   {
      return "ElementTypeInside[data=" + data2 + "]";
   }
}
