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
package org.jboss.test.ws.common.jbossxb.multixsd;

import org.jboss.test.ws.common.jbossxb.multixsd.packa.Person;
import org.jboss.test.ws.common.jbossxb.multixsd.packb.Item;

public class Order
{
   private Person person;
   private Item item;

   public Order()
   {
   }

   public Order(Person person, Item item)
   {
      this.person = person;
      this.item = item;
   }


   public Item getItem()
   {
      return item;
   }

   public void setItem(Item item)
   {
      this.item = item;
   }

   public Person getPerson()
   {
      return person;
   }

   public void setPerson(Person person)
   {
      this.person = person;
   }

   public boolean equals(Object obj)
   {
      if (this == obj) return true;
      if (!(obj instanceof Order)) return false;

      final Order o = (Order)obj;
      return toString().equals(o.toString());
   }

   public int hashCode()
   {
      return toString().hashCode();
   }

   public String toString()
   {
      return "[person=" + person + ",item=" + item + "]";
   }
}
