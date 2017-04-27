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
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMIncompleteSequenceBehavior;

/*
 * @author richard.opalka@jboss.com
 * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence
 */
final class RMCreateSequenceImpl extends RMAbstractSerializable implements RMCreateSequence
{
   
   // provider used by de/serialization framework
   private static final RMProvider PROVIDER = RMProviderImpl.getInstance();
   // internal fields
   private String acksTo;
   private Duration expires;
   private RMOffer offer;

   RMCreateSequenceImpl()
   {
      // allow inside package use only
   }
   
   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence#getAcksTo()
    */
   public String getAcksTo()
   {
      return this.acksTo;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence#getExpires()
    */
   public Duration getExpires()
   {
      return this.expires;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence#getOffer()
    */
   public RMOffer getOffer()
   {
      return this.offer;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence#newOffer()
    */
   public RMOffer newOffer()
   {
      return new OfferImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence#setAcksTo(java.lang.String)
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
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence#setExpires(java.lang.String)
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
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence#setOffer(org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence.Offer)
    */
   public void setOffer(RMOffer offer)
   {
      if (offer == null)
         throw new IllegalArgumentException("Offer cannot be null");
      if (!(offer instanceof OfferImpl))
         throw new IllegalArgumentException();
      if (offer.getIdentifier() == null)
         throw new IllegalArgumentException("Offer identifier must be specified");
      if (offer.getEndpoint() == null)
         throw new IllegalArgumentException("Offer endpoint address must be specified");
      if (this.offer != null)
         throw new UnsupportedOperationException("Value already set, cannot be overriden");
      
      this.offer = offer;
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
      result = prime * result + ((expires == null) ? 0 : expires.hashCode());
      result = prime * result + ((offer == null) ? 0 : offer.hashCode());
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
      if (!(obj instanceof RMCreateSequenceImpl))
         return false;
      final RMCreateSequenceImpl other = (RMCreateSequenceImpl)obj;
      if (acksTo == null)
      {
         if (other.acksTo != null)
            return false;
      }
      else if (!acksTo.equals(other.acksTo))
         return false;
      if (expires == null)
      {
         if (other.expires != null)
            return false;
      }
      else if (!expires.equals(other.expires))
         return false;
      if (offer == null)
      {
         if (other.offer != null)
            return false;
      }
      else if (!offer.equals(other.offer))
         return false;
      return true;
   }

   public RMProvider getProvider()
   {
      return PROVIDER;
   }
   
   public void validate()
   {
      if (this.acksTo == null)
         throw new RMException("AcksTo must be set");
   }
   
   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence.Offer
    */
   private static class OfferImpl implements RMCreateSequence.RMOffer
   {
      
      private String endpoint;
      private String duration;
      private String identifier;
      private RMIncompleteSequenceBehavior incompleteSequenceBehavior;
      
      private OfferImpl()
      {
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence.Offer#getEndpoint()
       */
      public String getEndpoint()
      {
         return this.endpoint;
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence.Offer#getExpires()
       */
      public String getExpires()
      {
         return this.duration;
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence.Offer#getIdentifier()
       */
      public String getIdentifier()
      {
         return this.identifier;
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence.Offer#getIncompleteSequenceBehavior()
       */
      public RMIncompleteSequenceBehavior getIncompleteSequenceBehavior()
      {
         return this.incompleteSequenceBehavior;
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence.Offer#setEndpoint(java.lang.String)
       */
      public void setEndpoint(String address)
      {
         if ((address == null) || (address.trim().equals("")))
            throw new IllegalArgumentException("Address cannot be null nor empty string");
         if (this.endpoint != null)
            throw new UnsupportedOperationException("Value already set, cannot be overriden");
         
         this.endpoint = address;
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence.Offer#setExpires(java.lang.String)
       */
      public void setExpires(String duration)
      {
         if ((duration == null) || (duration.trim().equals("")))
            throw new IllegalArgumentException("Duration cannot be null nor empty string");
         if (this.duration != null)
            throw new UnsupportedOperationException("Value already set, cannot be overriden");
         
         this.duration = duration;
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence.Offer#setIdentifier(java.lang.String)
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
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.CreateSequence.Offer#setIncompleteSequenceBehavior(org.jboss.ws.extensions.wsrm.spi.protocol.IncompleteSequenceBehavior)
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
         result = prime * result + ((duration == null) ? 0 : duration.hashCode());
         result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
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
         if (!(obj instanceof OfferImpl))
            return false;
         final OfferImpl other = (OfferImpl)obj;
         if (duration == null)
         {
            if (other.duration != null)
               return false;
         }
         else if (!duration.equals(other.duration))
            return false;
         if (endpoint == null)
         {
            if (other.endpoint != null)
               return false;
         }
         else if (!endpoint.equals(other.endpoint))
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
      
   }
   
}
