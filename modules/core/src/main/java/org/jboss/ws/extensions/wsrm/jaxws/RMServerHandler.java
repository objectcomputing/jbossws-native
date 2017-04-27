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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.soap.SOAPAddressingProperties;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.extensions.wsrm.RMConstant;
import org.jboss.ws.extensions.wsrm.RMFault;
import org.jboss.ws.extensions.wsrm.RMSequence;
import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;

/**
 * Server WS-RM JAX-WS handler
 *
 * @author richard.opalka@jboss.com
 *
 * @since Dec 12, 2007
 */
public final class RMServerHandler extends RMHandlerAbstractBase
{

   private static final RMConstants rmConstants = RMProvider.get().getConstants();

   protected final boolean handleOutbound(MessageContext msgContext)
   {
      log.debug("handling outbound message");
      
      CommonMessageContext commonMsgContext = (CommonMessageContext)msgContext;
      SOAPAddressingProperties addrProps = (SOAPAddressingProperties)commonMsgContext.get(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND);
      Map<String, Object> rmOutboundContext = (Map<String, Object>)commonMsgContext.get(RMConstant.RESPONSE_CONTEXT);
      List<QName> outMsgs = (List<QName>)rmOutboundContext.get(RMConstant.PROTOCOL_MESSAGES);
      Map<QName, RMSerializable> data = new HashMap<QName, RMSerializable>();
      String optionalMessageId = (addrProps.getMessageID() != null) ? addrProps.getMessageID().getURI().toString() : null;
      rmOutboundContext.put(RMConstant.WSA_MESSAGE_ID, optionalMessageId);
      rmOutboundContext.put(RMConstant.PROTOCOL_MESSAGES_MAPPING, data);
      SOAPMessage soapMessage = ((SOAPMessageContext)commonMsgContext).getMessage();
      RMSequence sequenceImpl = (RMSequence)rmOutboundContext.get(RMConstant.SEQUENCE_REFERENCE);
      
      // try to serialize CreateSequenceResponse to message
      serialize(rmConstants.getCreateSequenceResponseQName(), outMsgs, data, soapMessage, sequenceImpl);
      
      // try to serialize Sequence to message
      serialize(rmConstants.getSequenceQName(), outMsgs, data, soapMessage, sequenceImpl);

      // try to serialize AckRequested to message
      serialize(rmConstants.getAckRequestedQName(), outMsgs, data, soapMessage, sequenceImpl);
         
      // try to serialize CloseSequenceResponse to message
      serialize(rmConstants.getCloseSequenceResponseQName(), outMsgs, data, soapMessage, sequenceImpl);
      
      // try to serialize TerminateSequenceResponse to message
      serialize(rmConstants.getTerminateSequenceResponseQName(), outMsgs, data, soapMessage, sequenceImpl);
      
      // try to serialize SequenceAcknowledgement to message
      serialize(rmConstants.getSequenceAcknowledgementQName(), outMsgs, data, soapMessage, sequenceImpl);
      
      if ((outMsgs.size() != 0) && (data.size() == 0))
         throw new IllegalStateException("RM handler have not serialized WS-RM message to the payload");

      return true;
   }

   protected final boolean handleInbound(MessageContext msgContext)
   {
      log.debug("handling inbound message");
      
      SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
      
      // initialize RM response context
      Map<String, Object> rmResponseContext = new HashMap<String, Object>();
      List<QName> messages = new LinkedList<QName>();
      rmResponseContext.put(RMConstant.PROTOCOL_MESSAGES, messages);
      Map<QName, RMSerializable> data = new HashMap<QName, RMSerializable>();
      rmResponseContext.put(RMConstant.PROTOCOL_MESSAGES_MAPPING, data);

      // try to read CreateSequence from message
      deserialize(rmConstants.getCreateSequenceQName(), soapMessage, messages, data);
      
      // try to read AckRequested from message
      deserialize(rmConstants.getAckRequestedQName(), soapMessage, messages, data);
      
      // try to read Sequence from message
      deserialize(rmConstants.getSequenceQName(), soapMessage, messages, data);
      
      // try to read SequenceAcknowledgement from message
      deserialize(rmConstants.getSequenceAcknowledgementQName(), soapMessage, messages, data);

      // try to read CloseSequence from message
      deserialize(rmConstants.getCloseSequenceQName(), soapMessage, messages, data);
      
      // try to read TerminateSequence from message
      deserialize(rmConstants.getTerminateSequenceQName(), soapMessage, messages, data);
      
      if (data.size() == 0)
         throw new IllegalStateException("RM handler was not able to find WS-RM message in the payload");
      
      // propagate RM response context to higher layers
      msgContext.put(RMConstant.REQUEST_CONTEXT, rmResponseContext);
      msgContext.setScope(RMConstant.REQUEST_CONTEXT, Scope.APPLICATION);
      
      return true;
   }

   @Override
   public boolean handleFault(MessageContext msgContext)
   {
      log.debug("handling fault message");
      CommonMessageContext commonMsgContext = (CommonMessageContext)msgContext;
      SOAPAddressingProperties addrProps = (SOAPAddressingProperties)commonMsgContext.get(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND);
      Map<String, Object> rmOutboundContext = (Map<String, Object>)commonMsgContext.get(RMConstant.RESPONSE_CONTEXT);
      List<QName> outMsgs = (List<QName>)rmOutboundContext.get(RMConstant.PROTOCOL_MESSAGES);
      Map<QName, RMSerializable> data = new HashMap<QName, RMSerializable>();
      String optionalMessageId = (addrProps.getMessageID() != null) ? addrProps.getMessageID().getURI().toString() : null;
      rmOutboundContext.put(RMConstant.WSA_MESSAGE_ID, optionalMessageId);
      rmOutboundContext.put(RMConstant.PROTOCOL_MESSAGES_MAPPING, data);
      SOAPMessage soapMessage = ((SOAPMessageContext)commonMsgContext).getMessage();
      RMFault sequenceFault = (RMFault)rmOutboundContext.get(RMConstant.FAULT_REFERENCE);

      // try to serialize SequenceFault to message
      serialize(rmConstants.getSequenceFaultQName(), outMsgs, data, soapMessage, sequenceFault);
      
      if ((outMsgs.size() != 0) && (data.size() == 0))
         throw new IllegalStateException("RM handler have not serialized WS-RM message to the payload");

      return true;
   }

}
