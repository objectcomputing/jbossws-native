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
package org.jboss.ws.core.jaxrpc.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerInfo;

import org.jboss.logging.Logger;
import org.jboss.ws.core.RoleSource;
import org.jboss.ws.core.server.ServerHandlerDelegate;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.ws.metadata.umdm.HandlerMetaData;
import org.jboss.ws.metadata.umdm.HandlerMetaDataJAXRPC;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedInitParamMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/** Delegates to JAXRPC handlers
 *
 * @author Thomas.Diesler@jboss.org
 * @since 19-Jan-2005
 */
public class HandlerDelegateJAXRPC extends ServerHandlerDelegate implements RoleSource
{
   // provide logging
   private static Logger log = Logger.getLogger(HandlerDelegateJAXRPC.class);

   // This endpoints handler chain
   private ServerHandlerChain preHandlerChain;
   // This endpoints handler chain
   private ServerHandlerChain jaxrpcHandlerChain;
   // This endpoints handler chain
   private ServerHandlerChain postHandlerChain;
   // Set of understood headers
   Set<QName> headers = new HashSet<QName>();
   // Set of roles
   Set<String> roles = new HashSet<String>();

   public HandlerDelegateJAXRPC(ServerEndpointMetaData sepMetaData)
   {
      super(sepMetaData);
      sepMetaData.registerConfigObserver(this);
   }

   /**
    * For JAXRPC PRE/POST are defined in the context of message origin.
    */ 
   public HandlerType[] getHandlerTypeOrder()
   {
      return new HandlerType[] { HandlerType.PRE, HandlerType.ENDPOINT, HandlerType.POST };
   }

   public boolean callRequestHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type)
   {
      SOAPMessageContextJAXRPC msgContext = (SOAPMessageContextJAXRPC)MessageContextAssociation.peekMessageContext();

      // Initialize the handler chain
      if (isInitialized() == false)
      {
         synchronized (sepMetaData)
         {
            if (isInitialized() == false)
            {
               initHandlerChain(sepMetaData, HandlerType.PRE);
               initHandlerChain(sepMetaData, HandlerType.ENDPOINT);
               initHandlerChain(sepMetaData, HandlerType.POST);
               setInitialized(true);
            }
         }
      }

      boolean status = true;

      HandlerChain handlerChain = null;
      if (type == HandlerType.PRE)
         handlerChain = preHandlerChain;
      else if (type == HandlerType.ENDPOINT)
         handlerChain = jaxrpcHandlerChain;
      else if (type == HandlerType.POST)
         handlerChain = postHandlerChain;

      if (handlerChain != null)
         status = handlerChain.handleRequest(msgContext);

      return status;
   }

   public boolean callResponseHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type)
   {
      SOAPMessageContextJAXRPC msgContext = (SOAPMessageContextJAXRPC)MessageContextAssociation.peekMessageContext();

      HandlerChain handlerChain = null;
      if (type == HandlerType.PRE)
         handlerChain = preHandlerChain;
      else if (type == HandlerType.ENDPOINT)
         handlerChain = jaxrpcHandlerChain;
      else if (type == HandlerType.POST)
         handlerChain = postHandlerChain;

      boolean status = (handlerChain != null ? handlerChain.handleResponse(msgContext) : true);

      if (type == HandlerType.ENDPOINT)
         XOPContext.visitAndRestoreXOPData();

      return status;
   }

   public boolean callFaultHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type, Exception ex)
   {
      SOAPMessageContextJAXRPC msgContext = (SOAPMessageContextJAXRPC)MessageContextAssociation.peekMessageContext();

      HandlerChain handlerChain = null;
      if (type == HandlerType.PRE)
         handlerChain = preHandlerChain;
      else if (type == HandlerType.ENDPOINT)
         handlerChain = jaxrpcHandlerChain;
      else if (type == HandlerType.POST)
         handlerChain = postHandlerChain;

      boolean status = (handlerChain != null ? handlerChain.handleFault(msgContext) : true);

      if (type == HandlerType.ENDPOINT)
         XOPContext.visitAndRestoreXOPData();

      return status;
   }

   public void closeHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type)
   {
      // nothing to do for JAXRPC
   }

   /**
    * Init the handler chain
    */
   private void initHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type)
   {
      Set<String> handlerRoles = new HashSet<String>();
      List<HandlerInfo> hInfos = new ArrayList<HandlerInfo>();

      for (HandlerMetaData handlerMetaData : sepMetaData.getHandlerMetaData(type))
      {
         HandlerMetaDataJAXRPC jaxrpcMetaData = (HandlerMetaDataJAXRPC)handlerMetaData;
         handlerRoles.addAll(jaxrpcMetaData.getSoapRoles());

         HashMap<String, Object> hConfig = new HashMap<String, Object>();
         for (UnifiedInitParamMetaData param : jaxrpcMetaData.getInitParams())
         {
            hConfig.put(param.getParamName(), param.getParamValue());
         }

         Set<QName> headers = jaxrpcMetaData.getSoapHeaders();
         QName[] headerArr = new QName[headers.size()];
         headers.toArray(headerArr);

         Class hClass = jaxrpcMetaData.getHandlerClass();
         hConfig.put(HandlerType.class.getName(), jaxrpcMetaData.getHandlerType());
         HandlerInfo info = new HandlerInfo(hClass, hConfig, headerArr);

         if (log.isDebugEnabled())
            log.debug("Adding server side handler to service '" + sepMetaData.getPortName() + "': " + info);
         hInfos.add(info);
      }

      initHandlerChain(sepMetaData, hInfos, handlerRoles, type);
   }

   private void initHandlerChain(ServerEndpointMetaData sepMetaData, List<HandlerInfo> hInfos, Set<String> handlerRoles, HandlerType type)
   {
      if (log.isDebugEnabled())
         log.debug("Init handler chain with [" + hInfos.size() + "] handlers");

      ServerHandlerChain handlerChain = new ServerHandlerChain(hInfos, handlerRoles, type);
      if (type == HandlerType.PRE)
         preHandlerChain = handlerChain;
      else if (type == HandlerType.ENDPOINT)
         jaxrpcHandlerChain = handlerChain;
      else if (type == HandlerType.POST)
         postHandlerChain = handlerChain;

      if (handlerChain.getState() == ServerHandlerChain.STATE_CREATED)
      {
         // what is the config for a handler chain?
         handlerChain.init(null);
      }
      handlerChain.pullHeaders(headers);
      Collections.addAll(roles, handlerChain.getRoles());
   }

   public Set<String> getRoles()
   {
      return roles;
   }

   public Set<QName> getHeaders()
   {
      return headers;
   }

   public void update(Observable observable, Object object)
   {
   }
}
