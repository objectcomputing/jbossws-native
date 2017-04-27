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
package org.jboss.ws.extensions.wsrm.config;

/**
 * Delivery assurance metadata
 * 
 * @author richard.opalka@jboss.com
 */
public final class RMDeliveryAssuranceConfig
{

   private String quality;
   private String inOrder;
   
   public final void setQuality(String quality)
   {
      if (quality == null)
         throw new IllegalArgumentException();
      
      this.quality = quality;
   }
   
   public final String getQuality()
   {
      return this.quality;
   }
   
   public final void setInOrder(String inOrder)
   {
      if (inOrder == null)
         throw new IllegalArgumentException();
      
      this.inOrder = inOrder;
   }
   
   public final String getInOrder()
   {
      return this.inOrder;
   }
   
   public final String toString()
   {
      return "inOrder=\"" + this.inOrder + "\", quality=\"" + this.quality + "\"";
   }

}
