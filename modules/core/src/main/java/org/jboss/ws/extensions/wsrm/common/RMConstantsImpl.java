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
package org.jboss.ws.extensions.wsrm.common;

import javax.xml.namespace.QName;

import org.jboss.ws.extensions.wsrm.protocol.RMConstants;

/**
 * Utility class which should be used by all WS-RM protocol providers.
 * @author richard.opalka@jboss.com
 * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants
 */
public final class RMConstantsImpl implements RMConstants
{
   
   private final String prefix;
   private final String namespaceURI;
   private final QName acceptQName;
   private final QName ackRequestedQName;
   private final QName acknowledgementRangeQName;
   private final QName acksToQName;
   private final QName closeSequenceQName;
   private final QName closeSequenceResponseQName;
   private final QName createSequenceQName;
   private final QName createSequenceResponseQName;
   private final QName detailQName;
   private final QName endpointQName;
   private final QName expiresQName;
   private final QName faultCodeQName;
   private final QName finalQName;
   private final QName identifierQName;
   private final QName incompleteSequenceBehaviorQName;
   private final QName lastMessageNumberQName;
   private final QName lastMessageQName;
   private final QName lastMsgNumberQName;
   private final QName lowerQName;
   private final QName messageNumberQName;
   private final QName maxMessageNumberQName;
   private final QName nackQName;
   private final QName noneQName;
   private final QName offerQName;
   private final QName sequenceAcknowledgementQName;
   private final QName sequenceFaultQName;
   private final QName equenceQName;
   private final QName terminateSequenceQName;
   private final QName terminateSequenceResponseQName;
   private final QName upperQName;
   
   public RMConstantsImpl(String prefix, String namespaceURI)
   {
      this.prefix = prefix;
      this.namespaceURI = namespaceURI;
      this.acceptQName = new QName(namespaceURI, "Accept", prefix);
      this.ackRequestedQName = new QName(namespaceURI, "AckRequested", prefix);
      this.acknowledgementRangeQName = new QName(namespaceURI, "AcknowledgementRange", prefix);
      this.acksToQName = new QName(namespaceURI, "AcksTo", prefix);
      this.closeSequenceQName = new QName(namespaceURI, "CloseSequence", prefix);
      this.closeSequenceResponseQName = new QName(namespaceURI, "CloseSequenceResponse", prefix);
      this.createSequenceQName = new QName(namespaceURI, "CreateSequence", prefix);
      this.createSequenceResponseQName = new QName(namespaceURI, "CreateSequenceResponse", prefix);
      this.detailQName = new QName(namespaceURI, "Detail", prefix);
      this.endpointQName = new QName(namespaceURI, "Endpoint", prefix);
      this.expiresQName = new QName(namespaceURI, "Expires", prefix);
      this.faultCodeQName = new QName(namespaceURI, "FaultCode", prefix);
      this.finalQName = new QName(namespaceURI, "Final", prefix);
      this.identifierQName = new QName(namespaceURI, "Identifier", prefix);
      this.incompleteSequenceBehaviorQName = new QName(namespaceURI, "IncompleteSequenceBehavior", prefix);
      this.lastMessageNumberQName = new QName(namespaceURI, "LastMessageNumber", prefix);
      this.lastMessageQName = new QName(namespaceURI, "LastMessage", prefix);
      this.lastMsgNumberQName = new QName(namespaceURI, "LastMsgNumber", prefix);
      this.lowerQName = new QName(null, "Lower", "");
      this.messageNumberQName = new QName(namespaceURI, "MessageNumber", prefix);
      this.maxMessageNumberQName = new QName(namespaceURI, "MaxMessageNumber", prefix);
      this.nackQName = new QName(namespaceURI, "Nack", prefix);
      this.noneQName = new QName(namespaceURI, "None", prefix);
      this.offerQName = new QName(namespaceURI, "Offer", prefix);
      this.sequenceAcknowledgementQName = new QName(namespaceURI, "SequenceAcknowledgement", prefix);
      this.sequenceFaultQName = new QName(namespaceURI, "SequenceFault", prefix);
      this.equenceQName = new QName(namespaceURI, "Sequence", prefix);
      this.terminateSequenceQName = new QName(namespaceURI, "TerminateSequence", prefix);
      this.terminateSequenceResponseQName = new QName(namespaceURI, "TerminateSequenceResponse", prefix);
      this.upperQName = new QName(null, "Upper", "");
   }
   
   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getPrefix()
    */
   public final String getPrefix()
   {
      return this.prefix;
   }
   
   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getNamespaceURI()
    */
   public final String getNamespaceURI()
   {
      return this.namespaceURI;
   }
   
   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getAcceptQName()
    */
   public final QName getAcceptQName()
   {
      return this.acceptQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getAckRequestedQName()
    */
   public final QName getAckRequestedQName()
   {
      return this.ackRequestedQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getAcknowledgementRangeQName()
    */
   public final QName getAcknowledgementRangeQName()
   {
      return this.acknowledgementRangeQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getAcksToQName()
    */
   public final QName getAcksToQName()
   {
      return this.acksToQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getCloseSequenceQName()
    */
   public final QName getCloseSequenceQName()
   {
      return this.closeSequenceQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getCloseSequenceResponseQName()
    */
   public final QName getCloseSequenceResponseQName()
   {
      return this.closeSequenceResponseQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getCreateSequenceQName()
    */
   public final QName getCreateSequenceQName()
   {
      return this.createSequenceQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getCreateSequenceResponseQName()
    */
   public final QName getCreateSequenceResponseQName()
   {
      return this.createSequenceResponseQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getDetailQName()
    */
   public final QName getDetailQName()
   {
      return this.detailQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getEndpointQName()
    */
   public final QName getEndpointQName()
   {
      return this.endpointQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getExpiresQName()
    */
   public final QName getExpiresQName()
   {
      return this.expiresQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getFaultCodeQName()
    */
   public final QName getFaultCodeQName()
   {
      return this.faultCodeQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getFinalQName()
    */
   public final QName getFinalQName()
   {
      return this.finalQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getIdentifierQName()
    */
   public final QName getIdentifierQName()
   {
      return this.identifierQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getIncompleteSequenceBehaviorQName()
    */
   public final QName getIncompleteSequenceBehaviorQName()
   {
      return this.incompleteSequenceBehaviorQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getLastMessageNumberQName()
    */
   public final QName getLastMessageNumberQName()
   {
      return this.lastMessageNumberQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getLastMessageQName()
    */
   public final QName getLastMessageQName()
   {
      return this.lastMessageQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getLastMsgNumberQName()
    */
   public final QName getLastMsgNumberQName()
   {
      return this.lastMsgNumberQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getLowerQName()
    */
   public final QName getLowerQName()
   {
      return this.lowerQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getMessageNumberQName()
    */
   public final QName getMessageNumberQName()
   {
      return this.messageNumberQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getMaxMessageNumberQName()
    */
   public final QName getMaxMessageNumberQName()
   {
      return this.maxMessageNumberQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getNackQName()
    */
   public final QName getNackQName()
   {
      return this.nackQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getNoneQName()
    */
   public final QName getNoneQName()
   {
      return this.noneQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getOfferQName()
    */
   public final QName getOfferQName()
   {
      return this.offerQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getSequenceAcknowledgementQName()
    */
   public final QName getSequenceAcknowledgementQName()
   {
      return this.sequenceAcknowledgementQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getSequenceFaultQName()
    */
   public final QName getSequenceFaultQName()
   {
      return this.sequenceFaultQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getSequenceQName()
    */
   public final QName getSequenceQName()
   {
      return this.equenceQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getTerminateSequenceQName()
    */
   public final QName getTerminateSequenceQName()
   {
      return this.terminateSequenceQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getTerminateSequenceResponseQName()
    */
   public final QName getTerminateSequenceResponseQName()
   {
      return this.terminateSequenceResponseQName;
   }

   /**
    * @see org.jboss.ws.extensions.wsrm.protocol.RMConstants#getUpperQName()
    */
   public final QName getUpperQName()
   {
      return this.upperQName;
   }

}
