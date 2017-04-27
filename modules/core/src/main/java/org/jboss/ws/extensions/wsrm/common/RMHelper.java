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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.extensions.addressing.AddressingPropertiesImpl;
import org.jboss.ws.extensions.addressing.metadata.AddressingOpMetaExt;
import org.jboss.ws.extensions.wsrm.RMAddressingConstants;
import org.jboss.ws.extensions.wsrm.RMConstant;
import org.jboss.ws.extensions.wsrm.RMClientSequence;
import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMAckRequested;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceAcknowledgement;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;

/**
 * RM utility library
 *
 * @author richard.opalka@jboss.com
 *
 * @since Nov 29, 2007
 */
public final class RMHelper
{
   
   private static final Logger logger = Logger.getLogger(RMHelper.class);
   private static final RMConstants rmConstants = RMProvider.get().getConstants();

   private RMHelper()
   {
      // no instances allowed
   }
   
   private static final DatatypeFactory factory;
   
   static
   {
      try
      {
         factory = DatatypeFactory.newInstance();
      }
      catch (DatatypeConfigurationException dce)
      {
         throw new RMException(dce.getMessage(), dce);
      }
   }
   
   public static boolean isCreateSequence(Map<String, Object> rmMsgContext)
   {
      List<QName> protocolMessages = (List<QName>)rmMsgContext.get(RMConstant.PROTOCOL_MESSAGES);
      return protocolMessages.contains(rmConstants.getCreateSequenceQName());
   }
   
   public static boolean isCloseSequence(Map<String, Object> rmMsgContext)
   {
      List<QName> protocolMessages = (List<QName>)rmMsgContext.get(RMConstant.PROTOCOL_MESSAGES);
      return protocolMessages.contains(rmConstants.getCloseSequenceQName());
   }
   
   public static boolean isTerminateSequence(Map<String, Object> rmMsgContext)
   {
      List<QName> protocolMessages = (List<QName>)rmMsgContext.get(RMConstant.PROTOCOL_MESSAGES);
      return protocolMessages.contains(rmConstants.getTerminateSequenceQName());
   }
   
   public static boolean isSequence(Map<String, Object> rmMsgContext)
   {
      List<QName> protocolMessages = (List<QName>)rmMsgContext.get(RMConstant.PROTOCOL_MESSAGES);
      return protocolMessages.contains(rmConstants.getSequenceQName());
   }
   
   public static boolean isSequenceAcknowledgement(Map<String, Object> rmMsgContext)
   {
      List<QName> protocolMessages = (List<QName>)rmMsgContext.get(RMConstant.PROTOCOL_MESSAGES);
      return protocolMessages.contains(rmConstants.getSequenceAcknowledgementQName());
   }
   
   public static Duration stringToDuration(String s)
   {
      return factory.newDuration(s);
   }
   
   public static Duration longToDuration(long l)
   {
      return factory.newDuration(l);
   }
   
   public static String durationToString(Duration d)
   {
      return d.toString();
   }
   
   public static long durationToLong(Duration d)
   {
      if (d == null)
         return -1L;
      
      return d.getTimeInMillis(new Date());
   }
   
   public static void handleSequenceAcknowledgementHeader(RMSequenceAcknowledgement seqAckHeader, RMClientSequence sequence)
   {
      String seqId = seqAckHeader.getIdentifier();
      if (sequence.getOutboundId().equals(seqId))
      {
         List<RMSequenceAcknowledgement.RMAcknowledgementRange> ranges = seqAckHeader.getAcknowledgementRanges();
         for (RMSequenceAcknowledgement.RMAcknowledgementRange range : ranges)
         {
            for (long i = range.getLower(); i <= range.getUpper(); i++)
            {
               sequence.addReceivedOutboundMessage(i);
            }
         }
         if (seqAckHeader.isFinal())
         {
            sequence.setFinal();
         }
      }
      else
      {
         logger.warn("Expected outbound sequenceId:" + sequence.getOutboundId() + " , but was: " + seqId);
         throw new RMException("Expected outbound sequenceId:" + sequence.getOutboundId() + " , but was: " + seqId);
      }
   }
   
   public static void handleAckRequestedHeader(RMAckRequested ackReqHeader, RMClientSequence sequence)
   {
      String inboundSeqId = ackReqHeader.getIdentifier();
      if (false == sequence.getInboundId().equals(inboundSeqId))
      {
         logger.warn("Expected inbound sequenceId:" + sequence.getInboundId() + " , but was: " + inboundSeqId);
         throw new RMException("Expected inbound sequenceId:" + sequence.getInboundId() + " , but was: " + inboundSeqId);
      }
      
      sequence.ackRequested(true);
   }
   
   public static void handleSequenceHeader(RMSequence seqHeader, RMClientSequence sequence)
   {
      String inboundSeqId = seqHeader.getIdentifier();
      if (null == sequence.getInboundId())
      {
         sequence.setInboundId(inboundSeqId);
      }
      else
      {
         if (false == sequence.getInboundId().equals(inboundSeqId))
         {
            logger.warn("Expected inbound sequenceId:" + sequence.getInboundId() + " , but was: " + inboundSeqId);
            throw new RMException("Expected inbound sequenceId:" + sequence.getInboundId() + " , but was: " + inboundSeqId);
         }
      }
      sequence.addReceivedInboundMessage(seqHeader.getMessageNumber());
   }
   
   public static void setupRMOperations(EndpointMetaData endpointMD)
   {
      RMProvider rmProvider = RMProvider.get();
      
      // register createSequence method
      QName createSequenceQName = rmProvider.getConstants().getCreateSequenceQName();
      OperationMetaData createSequenceMD = new OperationMetaData(endpointMD, createSequenceQName, "createSequence");
      createSequenceMD.setOneWay(false);
      // setup addressing related data
      AddressingOpMetaExt createSequenceAddrExt = new AddressingOpMetaExt(new AddressingPropertiesImpl().getNamespaceURI());
      createSequenceAddrExt.setInboundAction(RMAddressingConstants.CREATE_SEQUENCE_WSA_ACTION);
      createSequenceAddrExt.setOutboundAction(RMAddressingConstants.CREATE_SEQUENCE_RESPONSE_WSA_ACTION);
      createSequenceMD.addExtension(createSequenceAddrExt);
      // register operation metadata with endpoint metadata
      endpointMD.addOperation(createSequenceMD);
      
      // register sequenceAcknowledgement method
      QName sequenceAcknowledgementQName = rmProvider.getConstants().getSequenceAcknowledgementQName();
      OperationMetaData sequenceAcknowledgementMD = new OperationMetaData(endpointMD, sequenceAcknowledgementQName, "sequenceAcknowledgement");
      sequenceAcknowledgementMD.setOneWay(true);
      // setup addressing related data
      AddressingOpMetaExt sequenceAcknowledgementAddrExt = new AddressingOpMetaExt(new AddressingPropertiesImpl().getNamespaceURI());
      sequenceAcknowledgementAddrExt.setInboundAction(RMAddressingConstants.SEQUENCE_ACKNOWLEDGEMENT_WSA_ACTION);
      sequenceAcknowledgementMD.addExtension(sequenceAcknowledgementAddrExt);
      // register operation metadata with endpoint metadata
      endpointMD.addOperation(sequenceAcknowledgementMD);
      
      if (rmProvider.getMessageFactory().newCloseSequence() != null)
      {
         // register closeSequence method
         QName closeSequenceQName = rmProvider.getConstants().getCloseSequenceQName();
         OperationMetaData closeSequenceMD = new OperationMetaData(endpointMD, closeSequenceQName, "closeSequence");
         closeSequenceMD.setOneWay(false);
         // setup addressing related data
         AddressingOpMetaExt closeSequenceAddrExt = new AddressingOpMetaExt(new AddressingPropertiesImpl().getNamespaceURI());
         closeSequenceAddrExt.setInboundAction(RMAddressingConstants.CLOSE_SEQUENCE_WSA_ACTION);
         closeSequenceAddrExt.setOutboundAction(RMAddressingConstants.CLOSE_SEQUENCE_RESPONSE_WSA_ACTION);
         closeSequenceMD.addExtension(closeSequenceAddrExt);
         // register operation metadata with endpoint metadata
         endpointMD.addOperation(closeSequenceMD);
      }
      
      // register terminateSequence method
      QName terminateSequenceQName = rmProvider.getConstants().getTerminateSequenceQName();
      OperationMetaData terminateSequenceMD = new OperationMetaData(endpointMD, terminateSequenceQName, "terminateSequence");
      boolean isOneWay = rmProvider.getMessageFactory().newTerminateSequenceResponse() == null;
      terminateSequenceMD.setOneWay(isOneWay);
      // setup addressing related data
      AddressingOpMetaExt terminateSequenceAddrExt = new AddressingOpMetaExt(new AddressingPropertiesImpl().getNamespaceURI());
      terminateSequenceAddrExt.setInboundAction(RMAddressingConstants.TERMINATE_SEQUENCE_WSA_ACTION);
      if (!isOneWay)
      {
         terminateSequenceAddrExt.setOutboundAction(RMAddressingConstants.TERMINATE_SEQUENCE_RESPONSE_WSA_ACTION);
      }
      terminateSequenceMD.addExtension(terminateSequenceAddrExt);
      // register operation metadata with endpoint metadata
      endpointMD.addOperation(terminateSequenceMD);
   }
   
   public static boolean isRMOperation(QName operationQName)
   {
      return RMConstant.PROTOCOL_OPERATION_QNAMES.contains(operationQName);
   }
   
}
