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
 * 5.4 JAX-RPC Value Type
 *
 * This section specifies requirements for the JAX-RPC value types.
 * A JAX-RPC value type is a Java class whose value can be moved between a service
 * client and service endpoint. A Java class must follow these rules to be a JAX-RPC
 * conformant value type:
 *
 *    - Java class must have a public default constructor.
 * 
 *    - Java class must not implement (directly or indirectly) the java.rmi.Remote interface.
 *
 *    - Java class may implement any Java interface (except the java.rmi.Remote interface) or extend another Java class.
 *
 *    - Java class may contain public, private, protected, package-level fields. The Java type
 *      of a public field must be a supported JAX-RPC type as specified in the section 5.1,
 *      �JAX-RPC Supported Java Types�.
 *
 *    - Java class may contain methods. There are no specified restrictions on the nature of
 *      these methods. Refer to the later rule about the JavaBeans properties.
 *
 *    - Java class may contain static or transient fields.
 *
 *    - Java class for a JAX-RPC value type may be designed as a JavaBeans class. In this
 *      case, the bean properties (as defined by the JavaBeans introspection) are required to
 *      follow the JavaBeans design pattern of setter and getter methods. The Java type of a
 *      bean property must be a supported JAX-RPC type as specified in the section 5.1,
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Oct-2004
 */
public class Base
{
   public int a;
   private int b;
   private int c;

   public Base()
   {
   }

   public int getB()
   {
      return b;
   }

   public void setB(int b)
   {
      this.b = b;
   }

   public void someMethod()
   {
   }

   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (!(o instanceof Base)) return false;

      final Base base = (Base)o;

      if (a != base.a) return false;
      if (b != base.b) return false;
      if (c != base.c) return false;

      return true;
   }

   public int hashCode()
   {
      int result;
      result = a;
      result = 29 * result + b;
      result = 29 * result + c;
      return result;
   }

   public String toString()
   {
      return "[a=" + a + ",b=" + b + ",c=" + c + "]";
   }
}
