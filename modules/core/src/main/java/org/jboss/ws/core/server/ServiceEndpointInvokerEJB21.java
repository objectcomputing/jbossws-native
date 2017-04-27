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
package org.jboss.ws.core.server;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonBinding;
import org.jboss.ws.core.CommonBindingProvider;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.EndpointInvocation;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.invocation.HandlerCallback;
import org.jboss.wsf.spi.invocation.Invocation;
import org.jboss.wsf.spi.invocation.InvocationContext;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * Handles invocations on EJB21 endpoints.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 25-Apr-2007
 */
public class ServiceEndpointInvokerEJB21 extends ServiceEndpointInvoker
{
   // provide logging
   private static final Logger log = Logger.getLogger(ServiceEndpointInvokerEJB21.class);
   
   @Override
   protected Invocation setupInvocation(Endpoint ep, EndpointInvocation epInv, InvocationContext invContext) throws Exception
   {
      Invocation inv = super.setupInvocation(ep, epInv, invContext);

      // Attach the handler callback
      ServerEndpointMetaData sepMetaData = endpoint.getAttachment(ServerEndpointMetaData.class);
      invContext.addAttachment(HandlerCallback.class, new HandlerCallbackImpl(sepMetaData));

      return inv;
   }

   /** Handlers are beeing called through the HandlerCallback from the EJB interceptor */
   @Override
   public boolean callRequestHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type)
   {
      if (type == HandlerType.PRE)
         return delegate.callRequestHandlerChain(sepMetaData, type);
      else return true;
   }

   /** Handlers are beeing called through the HandlerCallback from the EJB interceptor */
   @Override
   public boolean callResponseHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type)
   {
      if (type == HandlerType.PRE)
         return delegate.callResponseHandlerChain(sepMetaData, type);
      else return true;
   }

   /** Handlers are beeing called through the HandlerCallback from the EJB interceptor */
   @Override
   public boolean callFaultHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type, Exception ex)
   {
      if (type == HandlerType.PRE)
         return delegate.callFaultHandlerChain(sepMetaData, type, ex);
      else return true;
   }

   // The ServiceEndpointInterceptor calls the methods in this callback
   public class HandlerCallbackImpl implements HandlerCallback
   {
      private ServerEndpointMetaData sepMetaData;

      public HandlerCallbackImpl(ServerEndpointMetaData sepMetaData)
      {
         this.sepMetaData = sepMetaData;
      }

      /** Handlers are beeing called through the HandlerCallback from the EJB interceptor */
      public boolean callRequestHandlerChain(Invocation wsInv, HandlerType type)
      {
         boolean handlerPass = true;
         if (type == HandlerType.ENDPOINT)
         {
            handlerPass = delegate.callRequestHandlerChain(sepMetaData, type);
         }
         else if (type == HandlerType.POST)
         {
            handlerPass = delegate.callRequestHandlerChain(sepMetaData, type);
            
            // Verify that the the message has not been mofified
            CommonMessageContext messageContext = MessageContextAssociation.peekMessageContext();
            if(handlerPass && messageContext.isModified())
            {
               try
               {
                  OperationMetaData opMetaData = messageContext.getOperationMetaData();
                  CommonBindingProvider bindingProvider = new CommonBindingProvider(opMetaData.getEndpointMetaData());
                  CommonBinding binding = bindingProvider.getCommonBinding();
                  
                  log.debug("Handler modified payload, unbind message and update invocation args");
                  EndpointInvocation epInv = binding.unbindRequestMessage(opMetaData, messageContext.getMessageAbstraction());
                  wsInv.getInvocationContext().addAttachment(EndpointInvocation.class, epInv);
               }
               catch (BindingException ex)
               {
                  throw new WSException(ex);
               }
            }
         }
         return handlerPass;
      }

      /** Handlers are beeing called through the HandlerCallback from the EJB interceptor */
      public boolean callResponseHandlerChain(Invocation wsInv, HandlerType type)
      {
         if (type == HandlerType.PRE)
            return true;
         else return delegate.callResponseHandlerChain(sepMetaData, type);
      }

      /** Handlers are beeing called through the HandlerCallback from the EJB interceptor */
      public boolean callFaultHandlerChain(Invocation wsInv, HandlerType type, Exception ex)
      {
         if (type == HandlerType.PRE)
            return true;
         else return delegate.callFaultHandlerChain(sepMetaData, type, ex);
      }
   }
}
