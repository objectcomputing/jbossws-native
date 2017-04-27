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
package org.jboss.ws.extensions.security;

import java.io.Serializable;
import java.security.Principal;

/** A simple String based implementation of Principal.
 *  
 * @author Thomas.Diesler@jboss.org
 * @since 05-May-2006
 */
public class SimplePrincipal implements Principal, Serializable
{
   private static final long serialVersionUID = 136345402844480211L;
   
   private String name;

   public SimplePrincipal(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof Principal))
         return false;

      return toString().equals(((Principal)obj).getName());
   }

   public int hashCode()
   {
      return (name == null ? 0 : name.hashCode());
   }

   public String toString()
   {
      return "" + name;
   }
}
