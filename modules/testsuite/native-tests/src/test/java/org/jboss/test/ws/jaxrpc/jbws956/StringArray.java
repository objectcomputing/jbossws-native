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
package org.jboss.test.ws.jaxrpc.jbws956;

import java.util.Arrays;

/**
 * Wrapper class for a string array.
 * @author Frank Langelage
 */
public class StringArray
{
   /** The payload of this class. */
   private String[] value;

   /**
    * The default parameterless constructor.
    */
   public StringArray()
   {
   }

   /**
    * The constructor with a given payload.
    * @param value     the value to store.
    */
   public StringArray(final String[] value)
   {
      this.value = value;
   }

   /**
    * Get the current value.
    * @return  The current value.
    */
   public String[] getValue()
   {
      return this.value;
   }

   /**
    * Set the value attirbute to a new value.
    * @param value The new value.
    */
   public void setValue(final String[] value)
   {
      this.value = value;
   }

   /**
    * @see Object#toString()
    * @return a string representation of the object.
    */
   public String toString()
   {
      return (value != null ? Arrays.asList(value).toString() : null);
   }
}
