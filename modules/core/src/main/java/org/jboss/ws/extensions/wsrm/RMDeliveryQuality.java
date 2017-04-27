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
package org.jboss.ws.extensions.wsrm;

/**
 * Represents message delivery quality
 * 
 * @author richard.opalka@jboss.com
 */
public enum RMDeliveryQuality
{

   /**
    * Each message is to be delivered at most once. The RM Source MAY retry transmission of
    * unacknowledged messages, but is NOT REQUIRED to do so. The requirement on the RM
    * Destination is that it MUST filter out duplicate messages, i.e. that it MUST NOT
    * deliver a duplicate of a message that has already been delivered.
    */
   AT_MOST_ONCE("AtMostOnce"),
   
   /**
    * Each message is to be delivered exactly once; if a message cannot be delivered then an error
    * MUST be raised by the RM Source and/or RM Destination. The requirement on an RM Source is
    * that it SHOULD retry transmission of every message sent by the Application Source until it
    * receives an acknowledgement from the RM Destination. The requirement on the RM Destination
    * is that it SHOULD retry the transfer to the Application Destination of any message that it
    * accepts from the RM Source until that message has been successfully delivered, and that it
    * MUST NOT deliver a duplicate of a message that has already been delivered.
    */
   AT_LEAST_ONCE("AtLeastOnce"),
   
   /**
    * Each message is to be delivered at least once, or else an error MUST be raised by the RM
    * Source and/or RM Destination. The requirement on an RM Source is that it SHOULD retry
    * transmission of every message sent by the Application Source until it receives an
    * acknowledgement from the RM Destination. The requirement on the RM Destination is that it
    * SHOULD retry the transfer to the Application Destination of any message that it accepts
    * from the RM Source, until that message has been successfully delivered. There is no
    * requirement for the RM Destination to apply duplicate message filtering.
    */
   EXACTLY_ONCE("ExactlyOnce");
   
   // associated string representation
   private final String quality;
   
   RMDeliveryQuality(String quality)
   {
      this.quality = quality;
   }
   
   /**
    * Returns associated constant with passed <b>quality</b> string. Note this method is case sensitive.
    * Allowed values are: <b>AtMostOnce</b>, <b>AtLeastOnce</b>, <b>ExactlyOnce</b>.
    * @param quality to be parsed
    * @return associated constant
    * @throws IllegalArgumentException if <b>quality</b> string has no associated enumeration value
    */
   public static RMDeliveryQuality parseDeliveryQuality(String quality)
   {
      if (AT_MOST_ONCE.quality.equals(quality))
         return AT_MOST_ONCE;
      if (AT_LEAST_ONCE.quality.equals(quality))
         return AT_LEAST_ONCE;
      if (EXACTLY_ONCE.quality.equals(quality))
         return EXACTLY_ONCE;
      
      throw new IllegalArgumentException("Unrecognized string: " + quality);
   }
   
}
