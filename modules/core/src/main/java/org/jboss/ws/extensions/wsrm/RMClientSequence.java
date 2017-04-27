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
package org.jboss.ws.extensions.wsrm;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;

import org.jboss.logging.Logger;
import org.jboss.ws.core.jaxws.client.ClientImpl;
import org.jboss.ws.extensions.addressing.AddressingClientUtil;
import org.jboss.ws.extensions.wsrm.config.RMConfig;
import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMIncompleteSequenceBehavior;
import org.jboss.ws.extensions.wsrm.transport.RMUnassignedMessageListener;
import org.jboss.wsf.common.utils.UUIDGenerator;

/**
 * Client side implementation of the RM sequence
 *
 * @author richard.opalka@jboss.com
 *
 * @since Oct 25, 2007
 */
@SuppressWarnings("unchecked")
public final class RMClientSequence implements RMSequence, RMUnassignedMessageListener
{
   private static final Logger logger = Logger.getLogger(RMClientSequence.class);
   private static final String PATH_PREFIX = "/temporary_listen_address/";
   private static final RMConstants wsrmConstants = RMProvider.get().getConstants();
   
   private final RMConfig wsrmConfig;
   private final boolean addressableClient;
   private final Set<Long> acknowledgedOutboundMessages = new TreeSet<Long>();
   private final Set<Long> receivedInboundMessages = new TreeSet<Long>();
   private RMIncompleteSequenceBehavior behavior = RMIncompleteSequenceBehavior.NO_DISCARD;
   private String incomingSequenceId;
   private String outgoingSequenceId;
   private long duration = -1;
   private long creationTime;
   private URI backPort;
   private ClientImpl client;
   private boolean isFinal;
   private AtomicBoolean inboundMessageAckRequested = new AtomicBoolean();
   private AtomicLong messageNumber = new AtomicLong();
   private AtomicInteger countOfUnassignedMessagesAvailable = new AtomicInteger();
   private static final String ANONYMOUS_URI = AddressingBuilder.getAddressingBuilder().newAddressingConstants().getAnonymousURI();
   
   public RMClientSequence(RMConfig wsrmConfig)
   {
      super();
      if (wsrmConfig == null)
         throw new RMException("WS-RM configuration missing");
      
      this.wsrmConfig = wsrmConfig;
      this.addressableClient = wsrmConfig.getBackPortsServer() != null;
      if (this.addressableClient)
      {
         try
         {
            String host = wsrmConfig.getBackPortsServer().getHost();
            if (host == null)
            {
               host = InetAddress.getLocalHost().getCanonicalHostName();
               logger.debug("Backports server configuration omits host configuration - using autodetected " + host);
            }  
            String port = wsrmConfig.getBackPortsServer().getPort();
            String path = PATH_PREFIX + UUIDGenerator.generateRandomUUIDString();
            this.backPort = new URI("http://" + host + ":" + port + path);
         }
         catch (URISyntaxException use)
         {
            logger.warn(use);
            throw new RMException(use.getMessage(), use);
         }
         catch (UnknownHostException uhe)
         {
            logger.warn(uhe);
            throw new RMException(uhe.getMessage(), uhe);
         }
      }
   }
   
   public void unassignedMessageReceived()
   {
      // we can't use objectLock in the method - possible deadlock
      this.countOfUnassignedMessagesAvailable.addAndGet(1);
      logger.debug("Expected sequence expiration in " + ((System.currentTimeMillis() - this.creationTime) / 1000) + "seconds");
      logger.debug("Unassigned message available in callback handler");
   }
   
   public final RMConfig getRMConfig()
   {
      return this.wsrmConfig;
   }

   public final Set<Long> getReceivedInboundMessages()
   {
      return this.receivedInboundMessages;
   }
   
   public final BindingProvider getBindingProvider()
   {
      return (BindingProvider)this.client;
   }
   
   public final void setFinal()
   {
      this.isFinal = true;
      logger.debug("Sequence " + this.outgoingSequenceId + " state changed to final");
   }
   
   public final void ackRequested(boolean requested)
   {
      this.inboundMessageAckRequested.set(requested);
      logger.debug("Inbound Sequence: " + this.incomingSequenceId + ", ack requested. Messages in the queue: " + this.receivedInboundMessages);
   }
   
   public final boolean isAckRequested()
   {
      return this.inboundMessageAckRequested.get();
   }
   
   public final void addReceivedInboundMessage(long messageId)
   {
      this.receivedInboundMessages.add(messageId);
      logger.debug("Inbound Sequence: " + this.incomingSequenceId + ", received message no. " + messageId);
   }
   
   public final void addReceivedOutboundMessage(long messageId)
   {
      this.acknowledgedOutboundMessages.add(messageId);
      logger.debug("Outbound Sequence: " + this.outgoingSequenceId + ", message no. " + messageId + " acknowledged by server");
   }
   
   public final void setOutboundId(String outboundId)
   {
      this.outgoingSequenceId = outboundId;
   }
   
   public final void setInboundId(String inboundId)
   {
      this.incomingSequenceId = inboundId;
   }
   
   public final void setClient(ClientImpl client)
   {
      this.client = client;
   }
   
   public final void setDuration(long duration)
   {
      if (duration > 0)
      {
         this.creationTime = System.currentTimeMillis();
         this.duration = duration;
      }
   }
   
   public final long getDuration()
   {
      return this.duration;
   }
   
   public final URI getBackPort()
   {
      // no need for synchronization
      return (this.addressableClient) ? this.backPort : null;
   }
   
   public final String getAcksTo()
   {
      return (this.addressableClient) ? this.backPort.toString() : ANONYMOUS_URI;
   }

   public final long newMessageNumber()
   {
      // no need for synchronization
      return this.messageNumber.incrementAndGet();
   }
   
   public final long getLastMessageNumber()
   {
      // no need for synchronization
      return this.messageNumber.get();
   }
   
   public final void close() throws RMException
   {
      sendCloseMessage();
      sendTerminateMessage();
   }

   private void sendMessage(String action, QName operationQName, List protocolMessages) throws RMException
   {
      try
      {
         // set up addressing properties
         String address = client.getEndpointMetaData().getEndpointAddress();
         AddressingProperties props = null;
         if (this.client.getWSRMSequence().getBackPort() != null)
         {
            props = AddressingClientUtil.createDefaultProps(action, address);
            props.setReplyTo(AddressingBuilder.getAddressingBuilder().newEndpointReference(this.client.getWSRMSequence().getBackPort()));
         }
         else
         {
            props = AddressingClientUtil.createAnonymousProps(action, address);
         }
         // prepare WS-RM request context
         Map requestContext = client.getBindingProvider().getRequestContext(); 
         Map rmRequestContext = (Map)requestContext.get(RMConstant.REQUEST_CONTEXT);
         if (rmRequestContext == null)
         {
            rmRequestContext = new HashMap(); 
         }
         rmRequestContext.put(RMConstant.PROTOCOL_MESSAGES, protocolMessages);
         rmRequestContext.put(RMConstant.SEQUENCE_REFERENCE, this);
         // set up method invocation context
         requestContext.put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, props);
         requestContext.put(RMConstant.REQUEST_CONTEXT, rmRequestContext);
         // call stub method
         this.client.invoke(operationQName, new Object[] {}, client.getBindingProvider().getResponseContext());
      }
      catch (Exception e)
      {
         throw new RMException("Unable to terminate WSRM sequence", e);
      }
   }
   
   public final void sendCloseMessage()
   {
      if (RMProvider.get().getMessageFactory().newCloseSequence() != null) // e.g. WS-RM 1.0 doesn't support this protocol message
      {
         while (this.isAckRequested())
         {
            logger.debug("Waiting till all inbound sequence acknowledgements will be sent");
            sendSequenceAcknowledgementMessage();
         }
         Map<String, Object> wsrmReqCtx = new HashMap<String, Object>();
         wsrmReqCtx.put(RMConstant.ONE_WAY_OPERATION, false);
         this.getBindingProvider().getRequestContext().put(RMConstant.REQUEST_CONTEXT, wsrmReqCtx);
         List msgs = new LinkedList();
         msgs.add(wsrmConstants.getCloseSequenceQName());
         sendMessage(RMAddressingConstants.CLOSE_SEQUENCE_WSA_ACTION, wsrmConstants.getCloseSequenceQName(), msgs);
      }
   }
   
   public final void sendTerminateMessage()
   {
      List msgs = new LinkedList();
      msgs.add(wsrmConstants.getTerminateSequenceQName());
      if (this.getInboundId() != null)
      {
         msgs.add(wsrmConstants.getSequenceAcknowledgementQName());
      }
      Map<String, Object> wsrmReqCtx = new HashMap<String, Object>();
      boolean oneWayOperation = RMProvider.get().getMessageFactory().newTerminateSequenceResponse() == null;
      wsrmReqCtx.put(RMConstant.ONE_WAY_OPERATION, oneWayOperation);
      this.getBindingProvider().getRequestContext().put(RMConstant.REQUEST_CONTEXT, wsrmReqCtx);
      sendMessage(RMAddressingConstants.TERMINATE_SEQUENCE_WSA_ACTION, wsrmConstants.getTerminateSequenceQName(), msgs);
   }
   
   public final void sendSequenceAcknowledgementMessage()
   {
      Map<String, Object> wsrmReqCtx = new HashMap<String, Object>();
      wsrmReqCtx.put(RMConstant.ONE_WAY_OPERATION, true);
      this.getBindingProvider().getRequestContext().put(RMConstant.REQUEST_CONTEXT, wsrmReqCtx);
      ackRequested(false);
      List msgs = new LinkedList();
      msgs.add(wsrmConstants.getSequenceAcknowledgementQName());
      sendMessage(RMAddressingConstants.SEQUENCE_ACKNOWLEDGEMENT_WSA_ACTION, wsrmConstants.getSequenceAcknowledgementQName(), msgs);
   }
   
   public final void setBehavior(RMIncompleteSequenceBehavior behavior)
   {
      if (behavior != null)
      {
         this.behavior = behavior;
      }
   }

   public final String getOutboundId()
   {
      return outgoingSequenceId;
   }
   
   public final String getInboundId()
   {
      return incomingSequenceId;
   }

}
