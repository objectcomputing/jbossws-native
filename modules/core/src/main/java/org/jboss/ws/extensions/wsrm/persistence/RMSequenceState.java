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
package org.jboss.ws.extensions.wsrm.persistence;

/**
 * Possible sequence states
 *
 * @author richard.opalka@jboss.com
 */
public enum RMSequenceState
{
   
   CREATED(0),
   CLOSED(1),
   TERMINATED(2);
   
   private final int state;
   
   private RMSequenceState(int state)
   {
      this.state = state;
   }
   
   /**
    * Returns integer representation of the enum instance
    * @return
    */
   public final int getState()
   {
      return this.state;
   }
   
   /**
    * Factory method for constructing this enum instance from its integer representation
    * @param state for which enum instance is required
    * @return enum instance
    */
   public final RMSequenceState valueOf(int state)
   {
      if (0 == state)
         return CREATED;
      if (1 == state)
         return CLOSED;
      if (2 == state)
         return TERMINATED;
      
      throw new RuntimeException();
   }
   
   /**
    * Returns string enum representation
    * @return string enum representation
    */
   @Override
   public final String toString()
   {
      if (CREATED == this)
         return "created";
      if (CLOSED == this)
         return "closed";
      if (TERMINATED == this)
         return "terminated";
      
      throw new RuntimeException();
   }
   
}
