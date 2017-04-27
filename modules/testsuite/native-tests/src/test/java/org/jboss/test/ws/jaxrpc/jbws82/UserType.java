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
package org.jboss.test.ws.jaxrpc.jbws82;


/** A custom data object class that needs to specify a custom serializer
 */
public class UserType implements java.io.Serializable
{
   static final long serialVersionUID = -4900436998131030624L;
   private String msg;

   public UserType()
   {
      this(null);
   }

   public UserType(String msg)
   {
      this.msg = msg;
   }

   public String getMsg()
   {
      return this.msg;
   }

   public void setMsg(String msg)
   {
      this.msg = msg;
   }

   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (!(o instanceof UserType)) return false;

      final UserType userType = (UserType)o;

      if (msg != null ? !msg.equals(userType.msg) : userType.msg != null) return false;

      return true;
   }

   public int hashCode()
   {
      return (msg != null ? msg.hashCode() : 0);
   }

   public String toString()
   {
      return "[msg=" + msg + "]";
   }
}
