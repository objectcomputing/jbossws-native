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
package org.jboss.ws.extensions.wsrm.protocol.spec200502;

import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.common.serialization.RMAbstractSerializable;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMTerminateSequence;

/*
 * @author richard.opalka@jboss.com
 * @see org.jboss.ws.extensions.wsrm.spi.protocol.TerminateSequence
 */
final class RMTerminateSequenceImpl extends RMAbstractSerializable implements RMTerminateSequence
{
   
   // provider used by de/serialization framework
   private static final RMProvider PROVIDER = RMProviderImpl.getInstance();
   // internal fields
   private String identifier;
   
   RMTerminateSequenceImpl()
   {
      // allow inside package use only
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.TerminateSequence#getIdentifier()
    */
   public String getIdentifier()
   {
      return this.identifier;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.TerminateSequence#getLastMsgNumber()
    */
   public long getLastMsgNumber()
   {
      return 0; // always return zero for this version of the RM protocol
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.TerminateSequence#setIdentifier(java.lang.String)
    */
   public void setIdentifier(String identifier)
   {
      if ((identifier == null) || (identifier.trim().equals("")))
         throw new IllegalArgumentException("Identifier cannot be null nor empty string");
      if (this.identifier != null)
         throw new UnsupportedOperationException("Value already set, cannot be overriden");
      
      this.identifier = identifier;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.TerminateSequence#setLastMsgNumber(long)
    */
   public void setLastMsgNumber(long lastMsgNumber)
   {
      // do nothing for this version of the RM protocol
   }

   /*
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
      return result;
   }

   /*
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof RMTerminateSequenceImpl))
         return false;
      final RMTerminateSequenceImpl other = (RMTerminateSequenceImpl)obj;
      if (identifier == null)
      {
         if (other.identifier != null)
            return false;
      }
      else if (!identifier.equals(other.identifier))
         return false;
      return true;
   }
   
   public RMProvider getProvider()
   {
      return PROVIDER;
   }

   public void validate()
   {
      if (this.identifier == null)
         throw new RMException("Identifier not set");
   }

}
