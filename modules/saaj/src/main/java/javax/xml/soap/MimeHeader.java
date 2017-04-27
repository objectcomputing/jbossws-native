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
package javax.xml.soap;

/** An object that stores a MIME header name and its value. One or more
 * MimeHeader objects may be contained in a MimeHeaders object.
 *  
 * @author Scott.Stark@jboss.org
 */
public class MimeHeader
{
   private String name;
   private String value;

   public MimeHeader(String name, String value)
   {
      this.name = name;
      this.value = value;
   }

   public String getName()
   {
      return name;
   }

   public String getValue()
   {
      return value;
   }

   public int hashCode()
   {
      return toString().hashCode();
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof MimeHeader))
         return false;
      MimeHeader other = (MimeHeader)obj;
      return toString().equals(other.toString());
   }

   public String toString()
   {
      return "[" + name + "=" + value + "]";
   }
}
