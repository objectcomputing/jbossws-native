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
package org.jboss.test.ws.jaxrpc.jbws710;

public class Hello_onewayRequest_RequestStruct
{

   protected java.lang.String string_1;

   protected java.lang.String string_2;

   protected java.lang.String string_3;

   public Hello_onewayRequest_RequestStruct()
   {
   }

   public Hello_onewayRequest_RequestStruct(java.lang.String string_1, java.lang.String string_2, java.lang.String string_3)
   {
      this.string_1 = string_1;
      this.string_2 = string_2;
      this.string_3 = string_3;
   }

   public java.lang.String getString_1()
   {
      return string_1;
   }

   public void setString_1(java.lang.String string_1)
   {
      this.string_1 = string_1;
   }

   public java.lang.String getString_2()
   {
      return string_2;
   }

   public void setString_2(java.lang.String string_2)
   {
      this.string_2 = string_2;
   }

   public java.lang.String getString_3()
   {
      return string_3;
   }

   public void setString_3(java.lang.String string_3)
   {
      this.string_3 = string_3;
   }

}
