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
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.handler.Handler;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonBinding;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.EndpointInvocation;
import org.jboss.ws.core.HeaderSource;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.jaxws.SOAPFaultHelperJAXWS;
import org.jboss.ws.core.jaxws.handler.SOAPMessageContextJAXWS;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.core.soap.SOAPBodyElementDoc;
import org.jboss.ws.core.soap.SOAPBodyImpl;
import org.jboss.ws.core.soap.SOAPContentElement;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.core.soap.UnboundHeader;
import org.jboss.ws.core.soap.XMLFragment;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/** A BindingProvider for a JAXWS payload 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 27-Jun-2006
 */
public class PayloadBinding implements CommonBinding, BindingExt
{
   // provide logging
   private static final Logger log = Logger.getLogger(PayloadBinding.class);

   // Delegate to JAXWS binding
   private BindingImpl delegate = new BindingImpl();

   /** On the client side, generate the payload from IN parameters. */
   public MessageAbstraction bindRequestMessage(OperationMetaData opMetaData, EndpointInvocation epInv, Map<QName, UnboundHeader> unboundHeaders)
         throws BindingException
   {
      throw new NotImplementedException();
   }

   /** On the server side, extract the IN parameters from the payload and populate an Invocation object */
   public EndpointInvocation unbindRequestMessage(OperationMetaData opMetaData, MessageAbstraction payload) throws BindingException
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

         SOAPMessage reqMessage = (SOAPMessage)payload;
         SOAPBodyImpl body = (SOAPBodyImpl)reqMessage.getSOAPBody();
         
         SOAPContentElement bodyElement = (SOAPContentElement)body.getBodyElement();
         Source source = bodyElement.getXMLFragment().getSource();
         if (source == null)
            throw new IllegalStateException("Payload cannot be null");

         epInv.setRequestParamValue(xmlName, source);

         return epInv;
      }
      catch (Exception e)
      {
         handleException(e);
         return null;
      }
   }

   /** On the server side, generate the payload from OUT parameters. */
   public MessageAbstraction bindResponseMessage(OperationMetaData opMetaData, EndpointInvocation epInv) throws BindingException
   {
      log.debug("bindResponseMessage: " + opMetaData.getQName());

      try
      {
         SOAPMessageContextJAXWS msgContext = (SOAPMessageContextJAXWS)MessageContextAssociation.peekMessageContext();
         if (msgContext == null)
            throw new WSException("MessageContext not available");

         // Associate current message with message context
         MessageFactoryImpl factory = new MessageFactoryImpl();
         factory.setEnvNamespace(Constants.NS_SOAP11_ENV);
         SOAPMessageImpl resMessage = (SOAPMessageImpl)factory.createMessage();
         msgContext.setSOAPMessage(resMessage);

         ParameterMetaData retParameter = opMetaData.getReturnParameter();
         QName xmlName = retParameter.getXmlName();
         SOAPBodyImpl soapBody = (SOAPBodyImpl)resMessage.getSOAPBody();
         SOAPContentElement bodyElement = new SOAPBodyElementDoc(xmlName);
         bodyElement = (SOAPContentElement)soapBody.addChildElement(bodyElement);

         Source payload = (Source)epInv.getReturnValue();
         bodyElement.setXMLFragment(new XMLFragment(payload));

         return resMessage;
      }
      catch (Exception e)
      {
         handleException(e);
         return null;
      }
   }

   /** On the client side, extract the OUT parameters from the payload and return them to the client. */
   public void unbindResponseMessage(OperationMetaData opMetaData, MessageAbstraction resMessage, EndpointInvocation epInv, Map<QName, UnboundHeader> unboundHeaders)
         throws BindingException
   {
      throw new NotImplementedException();
   }

   public MessageAbstraction bindFaultMessage(Exception ex)
   {
      SOAPMessageImpl faultMessage = SOAPFaultHelperJAXWS.exceptionToFaultMessage(ex);
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext != null)
      {
         msgContext.setSOAPMessage(faultMessage);
      }
      else
      {
         log.warn("Cannot set fault message in message context");
      }
      return faultMessage;
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

   private void handleException(Exception ex) throws BindingException
   {
      if (ex instanceof RuntimeException)
         throw (RuntimeException)ex;

      if (ex instanceof BindingException)
         throw (BindingException)ex;

      throw new BindingException(ex);
   }

   public String getBindingID()
   {
      throw new NotImplementedException();
   }

   public void setHeaderSource(HeaderSource source)
   {
      // Not neeeded
   }
}
