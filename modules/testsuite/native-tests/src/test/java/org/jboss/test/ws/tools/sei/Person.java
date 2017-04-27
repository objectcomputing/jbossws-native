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
package org.jboss.test.ws.tools.sei;

/**
 * @author Thomas.Diesler@jboss.org
 * @since 04-Jun-2005
 */
public class Person
{
   private String name;
   private String address;

   public Person()
   {
   }

   public Person(String address, String name)
   {
      this.address = address;
      this.name = name;
   }

   public String getAddress()
   {
      return address;
   }

   public void setAddress(String address)
   {
      this.address = address;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (!(o instanceof Person)) return false;

      final Person person = (Person)o;

      if (address != null ? !address.equals(person.address) : person.address != null) return false;
      if (name != null ? !name.equals(person.name) : person.name != null) return false;

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (name != null ? name.hashCode() : 0);
      result = 29 * result + (address != null ? address.hashCode() : 0);
      return result;
   }

   public String toString()
   {
      return "[name=" + name + ",address=" + address + "]";
   }
}
