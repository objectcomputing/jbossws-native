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
package org.jboss.ws.core.soap;

import org.jboss.ws.Constants;

/** A type-safe enumeration for encoding use.
 *  
 * @author Thomas.Diesler@jboss.org
 * @since 16-Oct-2005
 */
public class Use
{
   private String use;

   public static final Use LITERAL = new Use("literal");
   public static final Use ENCODED = new Use("encoded");

   private Use(String use)
   {
      this.use = use;
   }

   public static Use getDefaultUse()
   {
      return LITERAL;
   }

   public static Use valueOf(String encodingStyle)
   {
      if (Constants.URI_LITERAL_ENC.equals(encodingStyle) || LITERAL.use.equals(encodingStyle))
         return LITERAL;
      if (Constants.URI_SOAP11_ENC.equals(encodingStyle) || ENCODED.use.equals(encodingStyle))
         return ENCODED;
      
      throw new IllegalArgumentException("Unsupported encoding style: " + encodingStyle);
   }

   public String toURI()
   {
      String encURI = null;
      if (this == LITERAL)
         encURI = Constants.URI_LITERAL_ENC;
      else if (this == ENCODED)
         encURI = Constants.URI_SOAP11_ENC;
      return encURI;
   }
   
   public String toString()
   {
      return use;
   }
}
