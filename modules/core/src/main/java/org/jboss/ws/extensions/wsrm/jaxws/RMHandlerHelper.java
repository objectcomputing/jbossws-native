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
package org.jboss.ws.extensions.wsrm.jaxws;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.ws.extensions.wsrm.RMFault;
import org.jboss.ws.extensions.wsrm.RMSequence;
import org.jboss.ws.extensions.wsrm.common.RMHelper;
import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMMessageFactory;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMAckRequested;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCloseSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCloseSequenceResponse;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequenceResponse;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceAcknowledgement;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceFault;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMTerminateSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMTerminateSequenceResponse;

/**
 * Handler helper
 *
 * @author richard.opalka@jboss.com
 *
 * @since Dec 13, 2007
 */
public final class RMHandlerHelper
{

   private static final RMMessageFactory rmFactory = RMProvider.get().getMessageFactory();
   private static final RMConstants rmConstants = RMProvider.get().getConstants();
   
   public static RMSerializable getMessage(QName msgQN)
   {
      if (rmConstants.getCreateSequenceQName().equals(msgQN))
      {
         return rmFactory.newCreateSequence();
      }
      if (rmConstants.getCreateSequenceResponseQName().equals(msgQN))
      {
         return rmFactory.newCreateSequenceResponse();
      }
      if (rmConstants.getCloseSequenceQName().equals(msgQN))
      {
         return rmFactory.newCloseSequence();
      }
      if (rmConstants.getCloseSequenceResponseQName().equals(msgQN))
      {
         return rmFactory.newCloseSequenceResponse();
      }
      if (rmConstants.getTerminateSequenceQName().equals(msgQN))
      {
         return rmFactory.newTerminateSequence();
      }
      if (rmConstants.getTerminateSequenceResponseQName().equals(msgQN))
      {
         return rmFactory.newTerminateSequenceResponse();
      }
      if (rmConstants.getSequenceAcknowledgementQName().equals(msgQN))
      {
         return rmFactory.newSequenceAcknowledgement();
      }
      if (rmConstants.getSequenceQName().equals(msgQN))
      {
         return rmFactory.newSequence();
      }
      if (rmConstants.getAckRequestedQName().equals(msgQN))
      {
         return rmFactory.newAckRequested();
      }
            
      throw new IllegalArgumentException();
   }
   
   public static RMSerializable prepareData(QName msgQN, List<QName> outMsgs, RMSequence seq)
   {
      if (outMsgs.contains(msgQN))
      {
         if (rmConstants.getSequenceQName().equals(msgQN))
         {
            return newSequence(seq);
         }
         if (rmConstants.getSequenceAcknowledgementQName().equals(msgQN))
         {
            return newSequenceAcknowledgement(seq);
         }
         if (rmConstants.getTerminateSequenceQName().equals(msgQN))
         {
            return newTerminateSequence(seq);
         }
         if (rmConstants.getTerminateSequenceResponseQName().equals(msgQN))
         {
            return newTerminateSequenceResponse(seq);
         }
         if (rmConstants.getCloseSequenceQName().equals(msgQN))
         {
            return newCloseSequence(seq);
         }
         if (rmConstants.getCloseSequenceResponseQName().equals(msgQN))
         {
            return newCloseSequenceResponse(seq);
         }
         if (rmConstants.getAckRequestedQName().equals(msgQN))
         {
            return newAckRequested(seq);
         }
         if (rmConstants.getCreateSequenceQName().equals(msgQN))
         {
            return newCreateSequence(seq);
         }
         if (rmConstants.getCreateSequenceResponseQName().equals(msgQN))
         {
            return newCreateSequenceResponse(seq);
         }
         
         throw new IllegalArgumentException(msgQN.toString());
      }
      
      return null;
   }
   
   public static RMSerializable prepareData(QName msgQN, List<QName> outMsgs, RMFault fault)
   {
      if (outMsgs.contains(msgQN))
      {
         if (rmConstants.getSequenceFaultQName().equals(msgQN))
         {
            return newSequenceFault(fault);
         }
         
         throw new IllegalArgumentException(msgQN.toString());
      }
      
      return null;
   }
   
   private static RMSerializable newSequenceFault(RMFault fault)
   {
      RMSequenceFault sequenceFault = rmFactory.newSequenceFault();
      sequenceFault.setDetail(fault);
      sequenceFault.setFaultCode(fault.getFaultCode().getSubcode());
      return sequenceFault;
   }
   
   private static RMSerializable newCreateSequence(RMSequence seq)
   {
      RMCreateSequence createSequence = rmFactory.newCreateSequence();
      createSequence.setAcksTo(seq.getAcksTo());
      return createSequence;
   }
   
   private static RMSerializable newCreateSequenceResponse(RMSequence seq)
   {
      RMCreateSequenceResponse createSequenceResponse = rmFactory.newCreateSequenceResponse();
      createSequenceResponse.setIdentifier(seq.getInboundId());
      createSequenceResponse.setExpires(RMHelper.longToDuration(seq.getDuration()));
      return createSequenceResponse;
   }
   
   private static RMSerializable newCloseSequenceResponse(RMSequence seq)
   {
      // construct CloseSequenceResponse object
      RMCloseSequenceResponse closeSequenceResponse = rmFactory.newCloseSequenceResponse();
      closeSequenceResponse.setIdentifier(seq.getInboundId());
      return closeSequenceResponse;
   }
   
   private static RMSerializable newCloseSequence(RMSequence seq)
   {
      // construct CloseSequenceResponse object
      RMCloseSequence closeSequence = rmFactory.newCloseSequence();
      closeSequence.setIdentifier(seq.getOutboundId());
      if (seq.getLastMessageNumber() > 0)
      {
         closeSequence.setLastMsgNumber(seq.getLastMessageNumber());
      }
      return closeSequence;
   }
   
   private static RMSerializable newTerminateSequence(RMSequence seq)
   {
      // construct CloseSequenceResponse object
      RMTerminateSequence terminateSequence = rmFactory.newTerminateSequence();
      terminateSequence.setIdentifier(seq.getOutboundId());
      if (seq.getLastMessageNumber() > 0)
      {
         terminateSequence.setLastMsgNumber(seq.getLastMessageNumber());
      }
      return terminateSequence;
   }
   
   private static RMSerializable newTerminateSequenceResponse(RMSequence seq)
   {
      // construct TerminateSequenceResponse object
      RMTerminateSequenceResponse terminateSequenceResponse = rmFactory.newTerminateSequenceResponse();
      terminateSequenceResponse.setIdentifier(seq.getInboundId());
      return terminateSequenceResponse;
   }
   
   private static RMSerializable newSequenceAcknowledgement(RMSequence seq)
   {
      // construct SequenceAcknowledgement object
      RMSequenceAcknowledgement sequenceAcknowledgement = rmFactory.newSequenceAcknowledgement();
      sequenceAcknowledgement.setIdentifier(seq.getInboundId());
      Iterator<Long> receivedMessages = seq.getReceivedInboundMessages().iterator();
      if (false == receivedMessages.hasNext())
      {
         sequenceAcknowledgement.setNone();
      }
      else
      {
         while (receivedMessages.hasNext())
         {
            long messageNo = receivedMessages.next();
            RMSequenceAcknowledgement.RMAcknowledgementRange range = sequenceAcknowledgement.newAcknowledgementRange();
            range.setLower(messageNo);
            range.setUpper(messageNo);
            sequenceAcknowledgement.addAcknowledgementRange(range);
         }
      }
      
      return sequenceAcknowledgement;
   }

   private static RMSerializable newAckRequested(RMSequence seq)
   {
      // construct AckRequested object
      RMAckRequested wsrmMsg = rmFactory.newAckRequested();
      wsrmMsg.setIdentifier(seq.getOutboundId());
      wsrmMsg.setMessageNumber(seq.getLastMessageNumber());
      return wsrmMsg;
   }

   private static RMSerializable newSequence(RMSequence seq)
   {
      // construct Sequence object
      org.jboss.ws.extensions.wsrm.protocol.spi.RMSequence sequence = rmFactory.newSequence();
      sequence.setIdentifier(seq.getOutboundId());
      sequence.setMessageNumber(seq.getLastMessageNumber());
      return sequence;
   }

}
