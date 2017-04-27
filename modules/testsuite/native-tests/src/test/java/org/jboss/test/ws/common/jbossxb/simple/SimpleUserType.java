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
package org.jboss.test.ws.common.jbossxb.simple;

import java.util.Date;

/**
 * @author Thomas.Diesler@jboss.org
 * @since 29-Apr-2005
 */
public class SimpleUserType
{
   public int a;
   private int b;
   private Date d;

   public SimpleUserType()
   {
   }

   public SimpleUserType(int a, int b, Date d)
   {
      this.a = a;
      this.b = b;
      this.d = d;
   }

   public int getB()
   {
      return b;
   }

   public void setB(int b)
   {
      this.b = b;
   }

   public Date getD()
   {
      return d;
   }

   public void setD(Date d)
   {
      this.d = d;
   }

   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (!(o instanceof SimpleUserType)) return false;

      SimpleUserType other = (SimpleUserType)o;

      if (a != other.a) return false;
      if (b != other.b) return false;
      if (d == null && other.d != null) return false;
      if (d != null && other.d == null) return false;
      if (d != null && other.d != null && d.getTime() != other.d.getTime()) return false;

      return toString().equals(other.toString());
   }

   public int hashCode()
   {
      return toString().hashCode();
   }

   public String toString()
   {
      long time = d != null ? d.getTime() : 0;
      return "[a=" + a + ",b=" + b + ",d=" + time + "]";
   }
}
