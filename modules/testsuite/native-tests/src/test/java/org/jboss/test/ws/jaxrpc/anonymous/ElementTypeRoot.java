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

import java.util.Arrays;

public class ElementTypeRoot
{
   private ElementTypeInside[] inside;
   private int[] someOtherElement;

   public ElementTypeRoot()
   {
   }

   public ElementTypeRoot(ElementTypeInside[] inside, int[] someOtherElement)
   {
      this.inside = inside;
      this.someOtherElement = someOtherElement;
   }

   public ElementTypeInside[] getInside()
   {
      return inside;
   }

   public void setInside(ElementTypeInside[] inside)
   {
      this.inside = inside;
   }

   public int[] getSomeOtherElement()
   {
      return someOtherElement;
   }

   public void setSomeOtherElement(int[] someOtherElement)
   {
      this.someOtherElement = someOtherElement;
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof ElementTypeRoot))
         return false;

      ElementTypeRoot other = (ElementTypeRoot)obj;
      return toString().equals(other.toString());
   }

   public int hashCode()
   {
      return toString().hashCode();
   }

   public String toString()
   {
      String insideStr = inside != null ? Arrays.asList(inside).toString() : null;
      String otherStr = "";
      if (someOtherElement != null)
      {
         for (int i = 0; i < someOtherElement.length; i++)
         {
            if (i > 0)
               otherStr += ", ";
            otherStr += i;
         }
      }
      else
      {
         otherStr = "null";
      }

      return "ElementTypeRoot[inside=" + insideStr + ",other=" + otherStr + "]";
   }
}
