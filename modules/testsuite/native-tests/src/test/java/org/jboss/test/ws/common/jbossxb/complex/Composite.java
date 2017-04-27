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
package org.jboss.test.ws.common.jbossxb.complex;

import java.math.BigInteger;
import java.util.Calendar;

import javax.xml.namespace.QName;

/**
 * The composite complexType
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Oct-2004
 */
public class Composite
{
   public BigInteger integer;
   public Calendar dateTime;
   public String string;
   public QName qname;
   public Composite composite;

   public Composite()
   {
   }

   public String toString()
   {
      return "[composite integer=" + integer + ", dateTime=" + dateTime + ", string=" + string + ", qname=" + qname +
         ", composite=" + composite + "]";
   }

   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (!(o instanceof Composite))
      {
         return false;
      }

      final Composite composite1 = (Composite)o;

      if (composite != null ? !composite.equals(composite1.composite) : composite1.composite != null)
      {
         return false;
      }
      if (dateTime != null ? !dateTime.equals(composite1.dateTime) : composite1.dateTime != null)
      {
         return false;
      }
      if (integer != null ? !integer.equals(composite1.integer) : composite1.integer != null)
      {
         return false;
      }
      if (qname != null ? !qname.equals(composite1.qname) : composite1.qname != null)
      {
         return false;
      }
      if (string != null ? !string.equals(composite1.string) : composite1.string != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (integer != null ? integer.hashCode() : 0);
      result = 29 * result + (dateTime != null ? dateTime.hashCode() : 0);
      result = 29 * result + (string != null ? string.hashCode() : 0);
      result = 29 * result + (qname != null ? qname.hashCode() : 0);
      result = 29 * result + (composite != null ? composite.hashCode() : 0);
      return result;
   }
}
