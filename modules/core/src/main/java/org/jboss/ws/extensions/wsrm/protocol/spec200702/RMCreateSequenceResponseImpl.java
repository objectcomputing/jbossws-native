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

import javax.xml.datatype.Duration;

import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.common.serialization.RMAbstractSerializable;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequenceResponse;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMIncompleteSequenceBehavior;

/*
 * @author richard.opalka@jboss.com
 * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse
 */
final class RMCreateSequenceResponseImpl extends RMAbstractSerializable implements RMCreateSequenceResponse
{
   
   // provider used by de/serialization framework
   private static final RMProvider PROVIDER = RMProviderImpl.getInstance();
   // internal fields
   private String identifier;
   private Duration expires;
   private RMAccept accept;
   private RMIncompleteSequenceBehavior incompleteSequenceBehavior;

   RMCreateSequenceResponseImpl()
   {
      // allow inside package use only
   }
   
   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse#getAccept()
    */
   public RMAccept getAccept()
   {
      return this.accept;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse#getDuration()
    */
   public Duration getExpires()
   {
      return this.expires;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse#getIdentifier()
    */
   public String getIdentifier()
   {
      return this.identifier;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse#getIncompleteSequenceBehavior()
    */
   public RMIncompleteSequenceBehavior getIncompleteSequenceBehavior()
   {
      return this.incompleteSequenceBehavior;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse#newAccept()
    */
   public RMAccept newAccept()
   {
      return new AcceptImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse#setAccept(org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse.Accept)
    */
   public void setAccept(RMAccept accept)
   {
      if (accept == null)
         throw new IllegalArgumentException("Accept cannot be null");
      if (!(accept instanceof AcceptImpl))
         throw new IllegalArgumentException();
      if (accept.getAcksTo() == null)
         throw new IllegalArgumentException("Accept acksTo must be specified");
      if (this.accept != null)
         throw new UnsupportedOperationException("Value already set, cannot be overriden");
      
      this.accept = accept;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse#setExpires(java.lang.String)
    */
   public void setExpires(Duration duration)
   {
      if ((duration == null) || (duration.toString().equals("")))
         throw new IllegalArgumentException("Duration cannot be null nor empty string");
      if (this.expires != null)
         throw new UnsupportedOperationException("Value already set, cannot be overriden");
      
      this.expires = duration;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse#setIdentifier(java.lang.String)
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
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse#setIncompleteSequenceBehavior(org.jboss.ws.extensions.wsrm.spi.protocol.IncompleteSequenceBehavior)
    */
   public void setIncompleteSequenceBehavior(RMIncompleteSequenceBehavior incompleteSequenceBehavior)
   {
      if (incompleteSequenceBehavior == null)
         throw new IllegalArgumentException("Sequence behavior type cannot be null");
      if (this.incompleteSequenceBehavior != null)
         throw new UnsupportedOperationException("Value already set, cannot be overriden");
      
      this.incompleteSequenceBehavior = incompleteSequenceBehavior;
   }

   /*
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((accept == null) ? 0 : accept.hashCode());
      result = prime * result + ((expires == null) ? 0 : expires.hashCode());
      result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
      result = prime * result + ((incompleteSequenceBehavior == null) ? 0 : incompleteSequenceBehavior.hashCode());
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
      if (!(obj instanceof RMCreateSequenceResponseImpl))
         return false;
      final RMCreateSequenceResponseImpl other = (RMCreateSequenceResponseImpl)obj;
      if (accept == null)
      {
         if (other.accept != null)
            return false;
      }
      else if (!accept.equals(other.accept))
         return false;
      if (expires == null)
      {
         if (other.expires != null)
            return false;
      }
      else if (!expires.equals(other.expires))
         return false;
      if (identifier == null)
      {
         if (other.identifier != null)
            return false;
      }
      else if (!identifier.equals(other.identifier))
         return false;
      if (incompleteSequenceBehavior == null)
      {
         if (other.incompleteSequenceBehavior != null)
            return false;
      }
      else if (!incompleteSequenceBehavior.equals(other.incompleteSequenceBehavior))
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
   }
   
   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse.Accept
    */
   private static class AcceptImpl implements RMCreateSequenceResponse.RMAccept
   {

      private String acksTo;
      
      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse.Accept#getAcksTo()
       */
      public String getAcksTo()
      {
         return this.acksTo;
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequenceResponse.Accept#setAcksTo(java.lang.String)
       */
      public void setAcksTo(String address)
      {
         if ((address == null) || (address.trim().equals("")))
            throw new IllegalArgumentException("Address cannot be null nor empty string");
         if (this.acksTo != null)
            throw new UnsupportedOperationException("Value already set, cannot be overriden");
         
         this.acksTo = address;
      }

      /*
       * @see java.lang.Object#hashCode()
       */
      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((acksTo == null) ? 0 : acksTo.hashCode());
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
         if (!(obj instanceof AcceptImpl))
            return false;
         final AcceptImpl other = (AcceptImpl)obj;
         if (acksTo == null)
         {
            if (other.acksTo != null)
               return false;
         }
         else if (!acksTo.equals(other.acksTo))
            return false;
         return true;
      }

   }

}
