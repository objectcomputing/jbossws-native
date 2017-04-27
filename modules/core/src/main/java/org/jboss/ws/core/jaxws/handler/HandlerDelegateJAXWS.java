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
package org.jboss.ws.core.jaxws.handler;

import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;
import org.jboss.ws.core.server.ServerHandlerDelegate;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.metadata.umdm.EndpointConfigMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * Delegates to JAXWS handlers
 *
 * @author Thomas.Diesler@jboss.org
 * @since 19-Jan-2005
 */
public class HandlerDelegateJAXWS extends ServerHandlerDelegate
{
   // provide logging
   private static Logger log = Logger.getLogger(HandlerDelegateJAXWS.class);
   private HandlerResolverImpl resolver = new HandlerResolverImpl();
   
   private ThreadLocal<HandlerChainExecutor> preExecutor = new ThreadLocal<HandlerChainExecutor>();
   private ThreadLocal<HandlerChainExecutor> jaxwsExecutor = new ThreadLocal<HandlerChainExecutor>();
   private ThreadLocal<HandlerChainExecutor> postExecutor = new ThreadLocal<HandlerChainExecutor>();
   
   public HandlerDelegateJAXWS(ServerEndpointMetaData sepMetaData)
   {
      super(sepMetaData);
      sepMetaData.registerConfigObserver(this);
   }

   /**
    * For JAXWS PRE/POST are defined in the context of an outbound message
    */ 
   public HandlerType[] getHandlerTypeOrder()
   {
      return new HandlerType[] { HandlerType.POST, HandlerType.ENDPOINT, HandlerType.PRE };
   }

   public boolean callRequestHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type)
   {
      log.debug("callRequestHandlerChain: " + type);

      // Initialize the handler chain
      if (isInitialized() == false)
      {
         synchronized (resolver)
         {
            if (isInitialized() == false)
            {
               EndpointConfigMetaData ecmd = sepMetaData.getEndpointConfigMetaData();
               resolver.initHandlerChain(ecmd, HandlerType.PRE, true);
               resolver.initHandlerChain(ecmd, HandlerType.ENDPOINT, true);
               resolver.initHandlerChain(ecmd, HandlerType.POST, true);
               setInitialized(true);
            }
         }
      }

      HandlerChainExecutor executor = createExecutor(sepMetaData, type);
      MessageContext msgContext = (MessageContext)MessageContextAssociation.peekMessageContext();
      return executor.handleMessage(msgContext);
   }

   public boolean callResponseHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type)
   {
      log.debug("callResponseHandlerChain: " + type);
      HandlerChainExecutor executor =  getExecutor(type);
      MessageContext msgContext = (MessageContext)MessageContextAssociation.peekMessageContext();
      boolean status = (executor != null ? executor.handleMessage(msgContext) : true);

      MessageAbstraction msg = ((CommonMessageContext)msgContext).getMessageAbstraction();
      if (msg instanceof SOAPMessage)
         XOPContext.visitAndRestoreXOPData();
      
      return status;
   }

   public void closeHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type)
   {
      log.debug("closeHandlerChain");
      HandlerChainExecutor executor =  getExecutor(type);
      MessageContext msgContext = (MessageContext)MessageContextAssociation.peekMessageContext();
      if (executor != null)
      {
         executor.close(msgContext);
         removeExecutor(type);
      }
   }
   
   public boolean callFaultHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type, Exception ex)
   {
      log.debug("callFaultHandlerChain: " + type);
      HandlerChainExecutor executor =  getExecutor(type);
      MessageContext msgContext = (MessageContext)MessageContextAssociation.peekMessageContext();
      boolean status = (executor != null ? executor.handleFault(msgContext, ex) : true);

      MessageAbstraction msg = ((CommonMessageContext)msgContext).getMessageAbstraction();
      if (msg instanceof SOAPMessage)
         XOPContext.visitAndRestoreXOPData();
                  
      return status;
   }

   private List<Handler> getHandlerChain(EndpointMetaData epMetaData, HandlerType type)
   {
      PortInfo info = getPortInfo(epMetaData);
      return resolver.getHandlerChain(info, type);
   }

   private PortInfo getPortInfo(EndpointMetaData epMetaData)
   {
      QName serviceName = epMetaData.getServiceMetaData().getServiceName();
      QName portName = epMetaData.getPortName();
      String bindingId = epMetaData.getBindingId();
      PortInfo info = new PortInfoImpl(serviceName, portName, bindingId);
      return info;
   }

   public Set<QName> getHeaders()
   {
      return resolver.getHeaders();
   }

   private HandlerChainExecutor createExecutor(ServerEndpointMetaData sepMetaData, HandlerType type)
   {
      if (type == HandlerType.ALL)
         throw new IllegalArgumentException("Invalid handler type: " + type);

      HandlerChainExecutor executor = new HandlerChainExecutor(sepMetaData, getHandlerChain(sepMetaData, type), true);
      if (type == HandlerType.PRE)
         preExecutor.set(executor);
      else if (type == HandlerType.ENDPOINT)
         jaxwsExecutor.set(executor);
      else if (type == HandlerType.POST)
         postExecutor.set(executor);
      
      return executor;
   }

   private HandlerChainExecutor getExecutor(HandlerType type)
   {
      if (type == HandlerType.ALL)
         throw new IllegalArgumentException("Invalid handler type: " + type);
      
      HandlerChainExecutor executor = null;
      if (type == HandlerType.PRE)
         executor = preExecutor.get();
      else if (type == HandlerType.ENDPOINT)
         executor = jaxwsExecutor.get();
      else if (type == HandlerType.POST)
         executor = postExecutor.get();
      
      return executor;
   }

   private void removeExecutor(HandlerType type)
   {
      if (type == HandlerType.ALL)
         throw new IllegalArgumentException("Invalid handler type: " + type);
      
      if (type == HandlerType.PRE)
         preExecutor.remove();
      else if (type == HandlerType.ENDPOINT)
         jaxwsExecutor.remove();
      else if (type == HandlerType.POST)
         postExecutor.remove();
   }

   public void update(Observable observable, Object object)
   {
   }
}
