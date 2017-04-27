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
package org.jboss.ws.extensions.wsrm.protocol.spec200702;

import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.common.serialization.RMAbstractSerializable;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequence;

/*
 * @author richard.opalka@jboss.com
 * @see org.jboss.ws.extensions.wsrm.spi.protocol.Sequence
 */
final class RMSequenceImpl extends RMAbstractSerializable implements RMSequence
{
   
   // provider used by de/serialization framework
   private static final RMProvider PROVIDER = RMProviderImpl.getInstance();
   // internal fields
   private String identifier;
   private long messageNumber;

   RMSequenceImpl()
   {
      // allow inside package use only
   }
   
   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.Sequence#getIdentifier()
    */
   public String getIdentifier()
   {
      return this.identifier;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.Sequence#getMessageNumber()
    */
   public long getMessageNumber()
   {
      return messageNumber;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.Sequence#isLastMessage()
    */
   public boolean isLastMessage()
   {
      return false; // always return false for this version of the RM protocol
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.Sequence#setIdentifier(java.lang.String)
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
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.Sequence#setLastMessage(boolean)
    */
   public void setLastMessage()
   {
      // do nothing for this version of the RM protocol
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.Sequence#setMessageNumber(long)
    */
   public void setMessageNumber(long messageNumber)
   {
      if (messageNumber <= 0)
         throw new IllegalArgumentException("Value must be greater than 0");
      if (this.messageNumber > 0)
         throw new UnsupportedOperationException("Value already set, cannot be overriden");
      
      this.messageNumber = messageNumber;
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
      result = prime * result + (int)(messageNumber ^ (messageNumber >>> 32));
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
      if (!(obj instanceof RMSequenceImpl))
         return false;
      final RMSequenceImpl other = (RMSequenceImpl)obj;
      if (identifier == null)
      {
         if (other.identifier != null)
            return false;
      }
      else if (!identifier.equals(other.identifier))
         return false;
      if (messageNumber != other.messageNumber)
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
         throw new RMException("Identifier must be set");
      if (this.messageNumber == 0)
         throw new RMException("MessageNumber must be set");
   }

}
