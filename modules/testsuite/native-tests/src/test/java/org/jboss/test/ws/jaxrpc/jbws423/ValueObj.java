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
package org.jboss.test.ws.jaxrpc.jbws423;

public class ValueObj
{

   private String s1;
   private String s2;

   public ValueObj()
   {
   }

   public ValueObj(String s1, String s2)
   {
      setS1(s1);
      setS2(s2);
   }

   public String getS1()
   {
      return s1;
   }

   public void setS1(String s1)
   {
      this.s1 = s1;
   }

   public String getS2()
   {
      return s2;
   }

   public void setS2(String s2)
   {
      this.s2 = s2;
   }
   
   public int hashCode()
   {
      return toString().hashCode();
   }
   
   public boolean equals(Object obj)
   {
      if (!(obj instanceof ValueObj)) return false;
      return toString().equals(obj.toString());
      
   }

   public String toString()
   {
      return "[" + s1 + " " + s2 + "]";
   }
}
