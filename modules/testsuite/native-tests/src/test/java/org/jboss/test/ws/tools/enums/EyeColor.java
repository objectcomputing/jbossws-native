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
package org.jboss.test.ws.tools.enums;

/**
 * A JAX-RPC 1.1 Enum Type
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class EyeColor
{
   private String value;

   protected EyeColor(String value)
   {
      this.value = value;
   }

   public static final EyeColor green = new EyeColor("green");
   public static final EyeColor blue = new EyeColor("blue");
   public static final EyeColor hazel = new EyeColor("hazel");
   public static final EyeColor brown = new EyeColor("brown");

   public String getValue()
   {
      return value;
   }

   public static EyeColor fromValue(String value)
   {
      if (green.getValue().equals(value))
         return green;
      else if (blue.getValue().equals(value))
         return blue;

      throw new IllegalArgumentException("Unknown color");
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof EyeColor))
         return false;

      return ((EyeColor) obj).value.equals(value);
   }

   public int hashCode()
   {
      return value.hashCode();
   }
}
