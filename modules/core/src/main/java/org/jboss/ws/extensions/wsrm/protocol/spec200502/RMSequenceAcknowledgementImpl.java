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

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.common.serialization.RMAbstractSerializable;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceAcknowledgement;

/*
 * @author richard.opalka@jboss.com
 * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement
 */
final class RMSequenceAcknowledgementImpl extends RMAbstractSerializable implements RMSequenceAcknowledgement
{
   
   // provider used by de/serialization framework
   private static final RMProvider PROVIDER = RMProviderImpl.getInstance();
   // internal fields
   private final List<Long> nacks = new LinkedList<Long>();
   private final List<RMAcknowledgementRange> acknowledgementRanges = new LinkedList<RMAcknowledgementRange>(); 
   private String identifier;
   
   RMSequenceAcknowledgementImpl()
   {
      // allow inside package use only
   }
   
   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#addAcknowledgementRange(org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement.AcknowledgementRange)
    */
   public void addAcknowledgementRange(RMAcknowledgementRange newAcknowledgementRange)
   {
      if ((newAcknowledgementRange == null) || (!(newAcknowledgementRange instanceof AcknowledgementRangeImpl)))
         throw new IllegalArgumentException();
      if (this.nacks.size() != 0)
         throw new IllegalStateException("There are already some nacks specified");
      if ((newAcknowledgementRange.getLower() == 0) || (newAcknowledgementRange.getUpper() == 0))
         throw new IllegalArgumentException("Both, lower and upper values must be specified");
      for (RMAcknowledgementRange alreadyAccepted : acknowledgementRanges)
         checkOverlap(alreadyAccepted, newAcknowledgementRange);
      
      this.acknowledgementRanges.add(newAcknowledgementRange);
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#addNack(long)
    */
   public void addNack(long messageNumber)
   {
      if (this.acknowledgementRanges.size() != 0)
         throw new IllegalStateException("There are already some acknowledgement ranges specified");
      if (this.nacks.contains(messageNumber))
         throw new IllegalArgumentException("There is already nack with value " + messageNumber + " specified");

      this.nacks.add(messageNumber);
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#getAcknowledgementRanges()
    */
   public List<RMAcknowledgementRange> getAcknowledgementRanges()
   {
      return Collections.unmodifiableList(acknowledgementRanges);
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#getIdentifier()
    */
   public String getIdentifier()
   {
      return this.identifier;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#getNacks()
    */
   public List<Long> getNacks()
   {
      return Collections.unmodifiableList(nacks);
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#isFinal()
    */
   public boolean isFinal()
   {
      return false; // always return false for this version of the RM protocol
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#isNone()
    */
   public boolean isNone()
   {
      return false; // always return false for this version of the RM protocol
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#newAcknowledgementRange()
    */
   public RMAcknowledgementRange newAcknowledgementRange()
   {
      return new AcknowledgementRangeImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#setFinal(boolean)
    */
   public void setFinal()
   {
      // do nothing for this version of the RM protocol
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#setIdentifier(java.lang.String)
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
    * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement#setNone(boolean)
    */
   public void setNone()
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
      result = prime * result + ((nacks == null) ? 0 : nacks.hashCode());
      result = prime * result + ((acknowledgementRanges == null) ? 0 : acknowledgementRanges.hashCode());
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
      if (!(obj instanceof RMSequenceAcknowledgementImpl))
         return false;
      final RMSequenceAcknowledgementImpl other = (RMSequenceAcknowledgementImpl)obj;
      if (acknowledgementRanges == null)
      {
         if (other.acknowledgementRanges != null)
            return false;
      }
      else if (!acknowledgementRanges.equals(other.acknowledgementRanges))
         return false;
      if (identifier == null)
      {
         if (other.identifier != null)
            return false;
      }
      else if (!identifier.equals(other.identifier))
         return false;
      if (nacks == null)
      {
         if (other.nacks != null)
            return false;
      }
      else if (!nacks.equals(other.nacks))
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
      if ((this.acknowledgementRanges.size() == 0) && (this.nacks.size() == 0))
         throw new RMException("AcknowledgementRange or Nack must be set");
   }

   private static void checkOverlap(RMAcknowledgementRange currentRange, RMAcknowledgementRange newRange)
   {
      if ((currentRange.getLower() <= newRange.getLower()) && (newRange.getLower() <= currentRange.getUpper()))
         throw new IllegalArgumentException(
            "Overlap detected: " + currentRange + " vs. " + newRange);
      if ((currentRange.getLower() <= newRange.getUpper()) && (newRange.getUpper() <= currentRange.getUpper()))
         throw new IllegalArgumentException(
            "Overlap detected: " + currentRange + " vs. " + newRange);
   }
   
   private static class AcknowledgementRangeImpl implements RMSequenceAcknowledgement.RMAcknowledgementRange
   {
      
      private long lower;
      private long upper;

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement.AcknowledgementRange#getLower()
       */
      public long getLower()
      {
         return this.lower;
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement.AcknowledgementRange#getUpper()
       */
      public long getUpper()
      {
         return this.upper;
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement.AcknowledgementRange#setLower(long)
       */
      public void setLower(long lower)
      {
         if (lower <= 0)
            throw new IllegalArgumentException("Value must be greater than 0");
         if (this.lower > 0)
            throw new UnsupportedOperationException("Value already set, cannot be overriden");
         if ((this.upper > 0) && (lower > this.upper))
            throw new IllegalArgumentException("Value must be lower or equal to " + this.upper);
         
         this.lower = lower;
      }

      /*
       * @see org.jboss.ws.extensions.wsrm.spi.protocol.SequenceAcknowledgement.AcknowledgementRange#setUpper(long)
       */
      public void setUpper(long upper)
      {
         if (upper <= 0)
            throw new IllegalArgumentException("Value must be greater than 0");
         if (this.upper > 0)
            throw new UnsupportedOperationException("Value already set, cannot be overriden");
         if ((this.lower > 0) && (this.lower > upper))
            throw new IllegalArgumentException("Value must be greater or equal to " + this.lower);

         this.upper = upper;
      }
      
      /*
       * @see java.lang.Object#hashCode()
       */
      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + (int)(lower ^ (lower >>> 32));
         result = prime * result + (int)(upper ^ (upper >>> 32));
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
         if (!(obj instanceof AcknowledgementRangeImpl))
            return false;
         final AcknowledgementRangeImpl other = (AcknowledgementRangeImpl)obj;
         if (lower != other.lower)
            return false;
         if (upper != other.upper)
            return false;
         return true;
      }

      public String toString()
      {
         return "<" + lower + "; " + upper + ">";
      }

   }

}
