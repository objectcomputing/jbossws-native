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
package org.jboss.ws.core.jaxws.binding;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.http.HTTPBinding;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonBinding;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.EndpointInvocation;
import org.jboss.ws.core.HTTPMessageImpl;
import org.jboss.ws.core.HeaderSource;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.UnboundHeader;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * The HTTPBinding interface is an abstraction for the XML/HTTP binding. 
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jul-2006
 */
public class HTTPBindingJAXWS implements CommonBinding, BindingExt, HTTPBinding
{
   // provide logging
   private static final Logger log = Logger.getLogger(HTTPBindingJAXWS.class);

   private BindingImpl delegate = new BindingImpl();

   public MessageAbstraction bindRequestMessage(OperationMetaData opMetaData, EndpointInvocation epInv, Map<QName, UnboundHeader> unboundHeaders)
         throws BindingException
   {
      throw new NotImplementedException();
   }

   public EndpointInvocation unbindRequestMessage(OperationMetaData opMetaData, MessageAbstraction reqMessage) throws BindingException
   {
      log.debug("unbindRequestMessage: " + opMetaData.getQName());
      try
      {
         // Construct the endpoint invocation object
         EndpointInvocation epInv = new EndpointInvocation(opMetaData);

         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         if (msgContext == null)
            throw new WSException("MessageContext not available");

         ParameterMetaData paramMetaData = opMetaData.getParameters().get(0);
         QName xmlName = paramMetaData.getXmlName();

         HTTPMessageImpl httpMessage = (HTTPMessageImpl)reqMessage;
         Source source = httpMessage.getXmlFragment().getSource();

         epInv.setRequestParamValue(xmlName, source);

         return epInv;
      }
      catch (Exception e)
      {
         handleException(e);
         return null;
      }
   }

   public MessageAbstraction bindResponseMessage(OperationMetaData opMetaData, EndpointInvocation epInv) throws BindingException
   {
      log.debug("bindResponseMessage: " + opMetaData.getQName());
      try
      {
         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         if (msgContext == null)
            throw new WSException("MessageContext not available");

         // Associate current message with message context
         Source source = (Source)epInv.getReturnValue();
         HTTPMessageImpl resMessage = new HTTPMessageImpl(source);
         msgContext.setMessageAbstraction(resMessage);

         return resMessage;
      }
      catch (Exception e)
      {
         handleException(e);
         return null;
      }
   }

   public void unbindResponseMessage(OperationMetaData opMetaData, MessageAbstraction resMessage, EndpointInvocation epInv, Map<QName, UnboundHeader> unboundHeaders)
         throws BindingException
   {
      throw new NotImplementedException();
   }

   public MessageAbstraction bindFaultMessage(Exception ex)
   {
      throw new NotImplementedException();
   }

   public List<Handler> getHandlerChain()
   {
      return delegate.getHandlerChain();
   }

   public List<Handler> getHandlerChain(HandlerType handlerType)
   {
      return delegate.getHandlerChain(handlerType);
   }

   public void setHandlerChain(List<Handler> handlerChain)
   {
      delegate.setHandlerChain(handlerChain);
   }

   public void setHandlerChain(List<Handler> handlerChain, HandlerType handlerType)
   {
      delegate.setHandlerChain(handlerChain, handlerType);
   }

   public String getBindingID()
   {
      return HTTPBinding.HTTP_BINDING;
   }

   public void setHeaderSource(HeaderSource source)
   {
      // Not needed
   }

   private void handleException(Exception ex) throws BindingException
   {
      if (ex instanceof RuntimeException)
         throw (RuntimeException)ex;

      if (ex instanceof BindingException)
         throw (BindingException)ex;

      throw new BindingException(ex);
   }
}
