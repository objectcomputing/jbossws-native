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
package org.jboss.ws.extensions.wsrm.server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.Relationship;

import org.jboss.logging.Logger;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.extensions.wsrm.RMAddressingConstants;
import org.jboss.ws.extensions.wsrm.RMConstant;
import org.jboss.ws.extensions.wsrm.RMFault;
import org.jboss.ws.extensions.wsrm.RMFaultCode;
import org.jboss.ws.extensions.wsrm.RMFaultConstant;
import org.jboss.ws.extensions.wsrm.common.RMHelper;
import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCloseSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceAcknowledgement;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMTerminateSequence;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.invocation.Invocation;
import org.jboss.wsf.spi.invocation.InvocationHandler;
import org.jboss.wsf.spi.management.ServerConfig;
import org.jboss.wsf.spi.management.ServerConfigFactory;

/**
 * RM Invocation Handler 
 *
 * @author richard.opalka@jboss.com
 *
 * @since Dec 11, 2007
 */
public final class RMInvocationHandler extends InvocationHandler
{

   private static final Logger logger = Logger.getLogger(RMInvocationHandler.class);
   private static final RMConstants rmConstants = RMProvider.get().getConstants();
   private ServerConfig serverConfig;
   private final InvocationHandler delegate;
   private final ArchiveDeployment dep;
   private final String dataDir;
   
   RMInvocationHandler(InvocationHandler delegate, ArchiveDeployment dep)
   {
      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      this.serverConfig = spiProvider.getSPI(ServerConfigFactory.class).getServerConfig();
      this.delegate = delegate;
      this.dep = dep;
      this.dataDir = getPersistLocation();
   }
   
   private String getPersistLocation()
   {
      try
      {
         String deploymentDir = (dep.getParent() != null ? dep.getParent().getSimpleName() : dep.getSimpleName());
         return serverConfig.getServerDataDir().getCanonicalPath() + "/wsrm/" + deploymentDir;
      }
      catch (IOException ioe)
      {
         throw new IllegalStateException();
      }
   }
   
   @Override
   public final Invocation createInvocation()
   {
      return this.delegate.createInvocation();
   }

   @Override
   public final void handleInvocationException(Throwable th) throws Exception
   {
      // TODO is it necessary to handle it specially in the case of WS-RM ?
      super.handleInvocationException(th);
   }

   @Override
   public final void init(Endpoint ep)
   {
      this.delegate.init(ep);
   }
   
   /**
    * Do RM staff before endpoint invocation
    * @param ep endpoint
    * @param inv invocation
    * @return RM response context to be set after target endpoint invocation
    */
   private static synchronized Map<String, Object> prepareResponseContext(Endpoint ep, Invocation inv, String dataDir)
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      
      Map<String, Object> rmReqProps = (Map<String, Object>)msgContext.get(RMConstant.REQUEST_CONTEXT);
      msgContext.remove(RMConstant.REQUEST_CONTEXT);
      if (rmReqProps == null)
      {
         throw new RMFault(RMFaultCode.WSRM_REQUIRED);
      }
      
      AddressingProperties addrProps = (AddressingProperties)msgContext.get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
      if (addrProps == null)
      {
         throw new IllegalStateException("WS-Addressing properties not found in server request");
      }
      
      List<QName> protocolMessages = new LinkedList<QName>();
      Map<String, Object> rmResponseContext = new HashMap<String, Object>();
      rmResponseContext.put(RMConstant.PROTOCOL_MESSAGES, protocolMessages);
      msgContext.remove(RMConstant.RESPONSE_CONTEXT);
      RMServerSequence sequence = null;
      boolean isOneWayOperation = true;
      
      if (RMHelper.isCreateSequence(rmReqProps))
      {
         sequence = new RMServerSequence();
         RMStore.serialize(dataDir, sequence);
         protocolMessages.add(rmConstants.getCreateSequenceResponseQName());
         rmResponseContext.put(RMConstant.SEQUENCE_REFERENCE, sequence);
         isOneWayOperation = false;
      }
      
      if (RMHelper.isCloseSequence(rmReqProps))
      {
         Map<QName, RMSerializable> data = (Map<QName, RMSerializable>)rmReqProps.get(RMConstant.PROTOCOL_MESSAGES_MAPPING);
         RMCloseSequence payload = (RMCloseSequence)data.get(rmConstants.getCloseSequenceQName());
         String seqIdentifier = payload.getIdentifier();
         sequence = RMStore.deserialize(dataDir, seqIdentifier, true);
         if (sequence == null)
         {
            throw getUnknownSequenceFault(seqIdentifier);
         }

         sequence.close();
         RMStore.serialize(dataDir, sequence);
         protocolMessages.add(rmConstants.getCloseSequenceResponseQName());
         protocolMessages.add(rmConstants.getSequenceAcknowledgementQName());
         rmResponseContext.put(RMConstant.SEQUENCE_REFERENCE, sequence);
         isOneWayOperation = false;
      }
         
      if (RMHelper.isSequenceAcknowledgement(rmReqProps))
      {
         Map<QName, RMSerializable> data = (Map<QName, RMSerializable>)rmReqProps.get(RMConstant.PROTOCOL_MESSAGES_MAPPING);
         RMSequenceAcknowledgement payload = (RMSequenceAcknowledgement)data.get(rmConstants.getSequenceAcknowledgementQName());
         String seqIdentifier = payload.getIdentifier();
         sequence = RMStore.deserialize(dataDir, seqIdentifier, false);
         if (sequence == null)
         {
            throw getUnknownSequenceFault(seqIdentifier);
         }

         for (RMSequenceAcknowledgement.RMAcknowledgementRange range : payload.getAcknowledgementRanges())
         {
            for (long i = range.getLower(); i <= range.getUpper(); i++)
            {
               if (i > sequence.getLastMessageNumber())
               {
                  // invalid acknowledgement - generating fault
                  RMStore.serialize(dataDir, sequence);
                  Map<String, Object> detailsMap = new HashMap<String, Object>(2);
                  detailsMap.put(RMFaultConstant.ACKNOWLEDGEMENT, range);
                  throw new RMFault(RMFaultCode.INVALID_ACKNOWLEDGEMENT, new HashMap<String, Object>(2));
               }

               sequence.addReceivedOutboundMessage(i);
            }
         }

         RMStore.serialize(dataDir, sequence);
      }
      
      if (RMHelper.isTerminateSequence(rmReqProps))
      {
         Map<QName, RMSerializable> data = (Map<QName, RMSerializable>)rmReqProps.get(RMConstant.PROTOCOL_MESSAGES_MAPPING);
         RMTerminateSequence payload = (RMTerminateSequence)data.get(rmConstants.getTerminateSequenceQName());
         String seqIdentifier = payload.getIdentifier();
         sequence = RMStore.deserialize(dataDir, seqIdentifier, true);
         if (sequence == null)
         {
            throw getUnknownSequenceFault(seqIdentifier);
         }

         RMStore.serialize(dataDir, sequence);
         if (RMProvider.get().getMessageFactory().newTerminateSequenceResponse() != null)
         {
            protocolMessages.add(rmConstants.getTerminateSequenceResponseQName());
            protocolMessages.add(rmConstants.getSequenceAcknowledgementQName());
            rmResponseContext.put(RMConstant.SEQUENCE_REFERENCE, sequence);
            isOneWayOperation = false;
         }
         else
         {
            return null; // no WS-RM context propagated - WS-RM 1.0
         }
      }
      
      if (RMHelper.isSequence(rmReqProps))
      {
         Map<QName, RMSerializable> data = (Map<QName, RMSerializable>)rmReqProps.get(RMConstant.PROTOCOL_MESSAGES_MAPPING);
         RMSequence payload = (RMSequence)data.get(rmConstants.getSequenceQName());
         String seqIdentifier = payload.getIdentifier();
         sequence = RMStore.deserialize(dataDir, seqIdentifier, true);
         if (sequence == null)
         {
            throw getUnknownSequenceFault(seqIdentifier);
         }

         try 
         {
            sequence.addReceivedInboundMessage(payload.getMessageNumber());
         }
         finally
         {
            RMStore.serialize(dataDir, sequence);
         }
         protocolMessages.add(rmConstants.getSequenceAcknowledgementQName());
         rmResponseContext.put(RMConstant.SEQUENCE_REFERENCE, sequence);
         
         boolean retTypeIsVoid = inv.getJavaMethod().getReturnType().equals(Void.class) || inv.getJavaMethod().getReturnType().equals(Void.TYPE);
         if (false == retTypeIsVoid)
         {
            try
            {
               sequence.newMessageNumber();
            }
            finally
            {
               RMStore.serialize(dataDir, sequence);
            }
            protocolMessages.add(rmConstants.getSequenceQName());
            protocolMessages.add(rmConstants.getAckRequestedQName());
         }
         else
         {
            AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
            AddressingProperties addressingProps = builder.newAddressingProperties();
            addressingProps.setTo(builder.newURI(addrProps.getReplyTo().getAddress().getURI()));
            addressingProps.setRelatesTo(new Relationship[] {builder.newRelationship(addrProps.getMessageID().getURI())});
            try
            {
               addressingProps.setAction(builder.newURI(RMAddressingConstants.SEQUENCE_ACKNOWLEDGEMENT_WSA_ACTION));
            }
            catch (URISyntaxException ignore)
            {
            }
            rmResponseContext.put(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_OUTBOUND, addressingProps);
         }
         isOneWayOperation = false;
      }
      
      rmResponseContext.put(RMConstant.ONE_WAY_OPERATION, isOneWayOperation);
      
      return rmResponseContext;
   }
   
   private static RMFault getUnknownSequenceFault(String sequenceId)
   {
      Map<String, Object> detailsMap = new HashMap<String, Object>(2);
      detailsMap.put(RMFaultConstant.IDENTIFIER, sequenceId);
      return new RMFault(RMFaultCode.UNKNOWN_SEQUENCE, detailsMap);
   }
   
   @Override
   public final void invoke(Endpoint ep, Invocation inv) throws Exception
   {
      Map<String, Object> rmResponseContext = null;
      try
      {
         rmResponseContext = prepareResponseContext(ep, inv, this.dataDir);
      }
      catch (RMFault fault)
      {
         rmResponseContext = new HashMap<String, Object>();
         List<QName> protocolMessages = new LinkedList<QName>();
         protocolMessages.add(rmConstants.getSequenceFaultQName());
         rmResponseContext.put(RMConstant.PROTOCOL_MESSAGES, protocolMessages);
         rmResponseContext.put(RMConstant.FAULT_REFERENCE, fault);
         rmResponseContext.put(RMConstant.ONE_WAY_OPERATION, false);
         CommonMessageContext msgCtx = MessageContextAssociation.peekMessageContext(); 
         msgCtx.put(RMConstant.RESPONSE_CONTEXT, rmResponseContext);
         throw fault; // rethrow
      }
      
      try
      {
         if (inv.getJavaMethod() != null)
         {
            logger.debug("Invoking method: " + inv.getJavaMethod().getName());
            this.delegate.invoke(ep, inv);
         }
         else
         {
            logger.debug("RM lifecycle protocol method detected");
         }
      }
      finally
      {
         setupResponseContext(rmResponseContext);
      }
   }
   
   private void setupResponseContext(Map<String, Object> rmResponseContext)
   {
      if (rmResponseContext != null)
      {
         CommonMessageContext msgCtx = MessageContextAssociation.peekMessageContext(); 
         msgCtx.put(RMConstant.RESPONSE_CONTEXT, rmResponseContext);
         msgCtx.put(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_OUTBOUND, rmResponseContext.get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_OUTBOUND));
      }
   }
   
   public final InvocationHandler getDelegate()
   {
      return this.delegate;
   }

}
