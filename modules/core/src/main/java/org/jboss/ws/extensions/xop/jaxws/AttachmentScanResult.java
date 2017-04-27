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
package org.jboss.ws.extensions.xop.jaxws;

/**
 * @author Heiko.Braun@jboss.org
 * @since Mar 7, 2007
 */
public final class AttachmentScanResult {

   public enum Type {XOP, SWA_REF};

   private String mimeType;
   private Type type;

   // distinguish return value and method parameters 
   private int index = -1;
   
   public AttachmentScanResult(String mimeType, Type type)
   {
      this.mimeType = mimeType;
      this.type = type;
   }

   public String getMimeType()
   {
      return mimeType;
   }

   public Type getType()
   {
      return type;
   }

   /**
    * <code>
    * <pre>
    * -1 - return value
    * 0 - 1st method parameter
    * n - n'th method parameter
    * </pre>
    * </code>
    * 
    * @return
    */
   public int getIndex()
   {
      return index;
   }

   public void setIndex(int index)
   {
      this.index = index;
   }
}
