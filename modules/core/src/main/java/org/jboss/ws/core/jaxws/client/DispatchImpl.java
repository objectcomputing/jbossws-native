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
package org.jboss.ws.core.jaxws.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.ConfigProvider;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.client.EndpointInfo;
import org.jboss.ws.core.client.HTTPProtocolConnection;
import org.jboss.ws.core.client.RemoteConnection;
import org.jboss.ws.core.client.SOAPProtocolConnectionHTTP;
import org.jboss.ws.core.jaxws.binding.BindingExt;
import org.jboss.ws.core.jaxws.binding.BindingProviderImpl;
import org.jboss.ws.core.jaxws.handler.HandlerChainExecutor;
import org.jboss.ws.core.jaxws.handler.HandlerResolverImpl;
import org.jboss.ws.core.jaxws.handler.MessageContextJAXWS;
import org.jboss.ws.core.jaxws.handler.SOAPMessageContextJAXWS;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.ws.metadata.config.ConfigurationProvider;
import org.jboss.ws.metadata.umdm.ClientEndpointMetaData;
import org.jboss.ws.metadata.umdm.EndpointConfigMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.wsse.WSSecurityConfigFactory;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;


/**
 * The Dispatch interface provides support for the dynamic invocation of a service endpoint operations. 
 * The javax.xml.ws.Service interface acts as a factory for the creation of Dispatch  instances.
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jul-2006
 */
public class DispatchImpl<T> implements Dispatch<T>, ConfigProvider
{
   // provide logging
   private final Logger log = Logger.getLogger(DispatchImpl.class);

   private BindingProvider bindingProvider;
   private HandlerResolverImpl handlerResolver;
   private ClientEndpointMetaData epMetaData;
   private JAXBContext jaxbContext;
   private ExecutorService executor;
   private String securityConfig;
   private Class type;
   private Mode mode;

   private Map<HandlerType, HandlerChainExecutor> executorMap = new HashMap<HandlerType, HandlerChainExecutor>();

   public DispatchImpl(ExecutorService executor, EndpointMetaData epMetaData, Class<T> type, Mode mode)
   {
      this.bindingProvider = new BindingProviderImpl(epMetaData);
      this.epMetaData = (ClientEndpointMetaData)epMetaData;
      this.executor = executor;
      this.type = type;
      this.mode = mode;
      initDispatch();
   }

   public DispatchImpl(ExecutorService executor, EndpointMetaData epMetaData, JAXBContext jbc, Mode mode)
   {
      this.bindingProvider = new BindingProviderImpl(epMetaData);
      this.epMetaData = (ClientEndpointMetaData)epMetaData;
      this.executor = executor;
      this.type = Object.class;
      this.jaxbContext = jbc;
      this.mode = mode;
      initDispatch();
   }

   public T invoke(T obj)
   {
      T retObj = null;
      try
      {
         retObj = (T)invokeInternal(obj, getResponseContext());
      }
      catch (Exception ex)
      {
         handleInvokeException(ex);
      }
      return retObj;
   }

   private Object invokeInternal(Object obj, Map<String, Object> resContext) throws Exception
   {
      Object retObj = null;

      BindingExt binding = (BindingExt)bindingProvider.getBinding();
      
      String bindingID = binding.getBindingID();
      if (bindingID.indexOf("soap") > 0)
      {
         // Init the handler chain
         if (handlerResolver == null)
         {
            handlerResolver = new HandlerResolverImpl();
            EndpointConfigMetaData ecmd = epMetaData.getEndpointConfigMetaData();
            handlerResolver.initHandlerChain(ecmd, HandlerType.PRE, true);
            handlerResolver.initHandlerChain(ecmd, HandlerType.ENDPOINT, true);
            handlerResolver.initHandlerChain(ecmd, HandlerType.POST, true);
            
            PortInfo portInfo = epMetaData.getPortInfo();
            List<Handler> preChain = handlerResolver.getHandlerChain(portInfo, HandlerType.PRE);
            List<Handler> epChain = handlerResolver.getHandlerChain(portInfo, HandlerType.ENDPOINT);
            List<Handler> postChain = handlerResolver.getHandlerChain(portInfo, HandlerType.POST);
            
            binding.setHandlerChain(preChain, HandlerType.PRE);
            binding.setHandlerChain(epChain, HandlerType.ENDPOINT);
            binding.setHandlerChain(postChain, HandlerType.POST);
         }
         
         retObj = invokeInternalSOAP(obj);
      }
      else
      {
         retObj = invokeInternalNonSOAP(obj);
      }
      return retObj;
   }

   private Object invokeInternalSOAP(Object obj) throws Exception
   {
      Object retObj = null;

      SOAPMessageImpl reqMsg = (SOAPMessageImpl)getRequestMessage(obj);
      String targetAddress = epMetaData.getEndpointAddress();

      // R2744 A HTTP request MESSAGE MUST contain a SOAPAction HTTP header field
      // with a quoted value equal to the value of the soapAction attribute of
      // soapbind:operation, if present in the corresponding WSDL description.

      // R2745 A HTTP request MESSAGE MUST contain a SOAPAction HTTP header field
      // with a quoted empty string value, if in the corresponding WSDL description,
      // the soapAction attribute of soapbind:operation is either not present, or
      // present with an empty string as its value.
      String soapAction = null;
      Map<String, Object> reqContext = getRequestContext();
      Boolean useSOAPAction = (Boolean)reqContext.get(BindingProvider.SOAPACTION_USE_PROPERTY);
      if (Boolean.TRUE.equals(useSOAPAction))
      {
         soapAction = (String)reqContext.get(BindingProvider.SOAPACTION_URI_PROPERTY);
         if (soapAction == null)
            throw new IllegalStateException("Cannot obtain: " + BindingProvider.SOAPACTION_URI_PROPERTY);
      }
      MimeHeaders mimeHeaders = reqMsg.getMimeHeaders();
      mimeHeaders.addHeader("SOAPAction", soapAction != null ? soapAction : "");

      // Get the order of pre/post handlerchains 
      HandlerType[] handlerType = new HandlerType[] { HandlerType.PRE, HandlerType.ENDPOINT, HandlerType.POST };
      HandlerType[] faultType = new HandlerType[] { HandlerType.PRE, HandlerType.ENDPOINT, HandlerType.POST };

      // Associate a message context with the current thread
      CommonMessageContext msgContext = new SOAPMessageContextJAXWS();
      MessageContextAssociation.pushMessageContext(msgContext);
      try
      {
         msgContext.setEndpointMetaData(epMetaData);
         msgContext.setSOAPMessage(reqMsg);
         msgContext.putAll(reqContext);
         // Try to find out the operation metadata corresponding to the message we're sending
         msgContext.setOperationMetaData(getOperationMetaData(epMetaData,reqMsg));

         // The contents of the request context are used to initialize the message context (see section 9.4.1)
         // prior to invoking any handlers (see chapter 9) for the outbound message. Each property within the
         // request context is copied to the message context with a scope of HANDLER.
         msgContext.put(MessageContextJAXWS.MESSAGE_OUTBOUND_PROPERTY, Boolean.TRUE);

         QName portName = epMetaData.getPortName();
         try
         {
            // Call the request handlers
            boolean handlerPass = callRequestHandlerChain(portName, handlerType[0]);
            handlerPass = handlerPass && callRequestHandlerChain(portName, handlerType[1]);
            handlerPass = handlerPass && callRequestHandlerChain(portName, handlerType[2]);

            XOPContext.visitAndRestoreXOPData();

            // Handlers might have replaced the message
            reqMsg = (SOAPMessageImpl)msgContext.getSOAPMessage();

            MessageAbstraction resMsg = null;
            if (handlerPass)
            {
               Map<String, Object> callProps = new HashMap<String, Object>(getRequestContext());
               if (callProps.containsKey(BindingProvider.ENDPOINT_ADDRESS_PROPERTY)) {
                  targetAddress = (String) callProps.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
               }
               EndpointInfo epInfo = new EndpointInfo(epMetaData, targetAddress, callProps);
               resMsg = getRemotingConnection().invoke(reqMsg, epInfo, false);

               //Pivot, switch to response ctx and save the response message there
               msgContext = MessageContextJAXWS.processPivot(msgContext);
               msgContext.setMessageAbstraction(resMsg);

               // Call the  response handler chain, removing the fault type entry will not call handleFault for that chain 
               handlerPass = callResponseHandlerChain(portName, handlerType[2]);
               faultType[2] = null;
               handlerPass = handlerPass && callResponseHandlerChain(portName, handlerType[1]);
               faultType[1] = null;
               handlerPass = handlerPass && callResponseHandlerChain(portName, handlerType[0]);
               faultType[0] = null;
            }

            if (handlerPass)
            {
               retObj = getReturnObject(resMsg);
            }
         }
         catch (Exception ex)
         {
            msgContext = MessageContextJAXWS.processPivot(msgContext);
            if (faultType[2] != null)
               callFaultHandlerChain(portName, faultType[2], ex);
            if (faultType[1] != null)
               callFaultHandlerChain(portName, faultType[1], ex);
            if (faultType[0] != null)
               callFaultHandlerChain(portName, faultType[0], ex);

            throw ex;
         }
         finally
         {
            closeHandlerChain(portName, handlerType[2]);
            closeHandlerChain(portName, handlerType[1]);
            closeHandlerChain(portName, handlerType[0]);
         }
      }
      finally
      {
          MessageContextAssociation.popMessageContext();
      }
      return retObj;
   }

   private Object invokeInternalNonSOAP(Object obj) throws IOException
   {
      MessageAbstraction reqMsg = getRequestMessage(obj);
      String targetAddress = epMetaData.getEndpointAddress();
      Map<String, Object> callProps = new HashMap<String, Object>(getRequestContext());
      if (callProps.containsKey(BindingProvider.ENDPOINT_ADDRESS_PROPERTY)) {
         targetAddress = (String) callProps.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
      }
      MessageAbstraction resMsg = getRemotingConnection().invoke(reqMsg, targetAddress, false);
      Object retObj = getReturnObject(resMsg);
      return retObj;
   }

   private RemoteConnection getRemotingConnection()
   {
      String bindingID = bindingProvider.getBinding().getBindingID();
      if (EndpointMetaData.SUPPORTED_BINDINGS.contains(bindingID) == false)
         throw new IllegalStateException("Unsupported binding: " + bindingID);

      RemoteConnection remotingConnection;
      if (HTTPBinding.HTTP_BINDING.equals(bindingID))
      {
         remotingConnection = new HTTPProtocolConnection();
      }
      else
      {
         remotingConnection = new SOAPProtocolConnectionHTTP();
      }
      return remotingConnection;
   }

   public Response<T> invokeAsync(T msg)
   {
      ResponseImpl response = new ResponseImpl();
      Runnable task = new AsyncRunnable(response, null, msg);
      Future future = executor.submit(task);
      response.setFuture(future);
      return response;
   }

   public Future invokeAsync(T obj, AsyncHandler<T> handler)
   {
      ResponseImpl response = new ResponseImpl();
      Runnable task = new AsyncRunnable(response, handler, obj);
      Future future = executor.submit(task);
      response.setFuture(future);
      return response;
   }

   public void invokeOneWay(T msg)
   {
      CommonMessageContext msgContext = new SOAPMessageContextJAXWS();
      MessageContextAssociation.pushMessageContext(msgContext);
      try
      {
         msgContext.setEndpointMetaData(epMetaData);
         MessageAbstraction reqMsg = getRequestMessage(msg);
         String targetAddress = epMetaData.getEndpointAddress();
         getRemotingConnection().invoke(reqMsg, targetAddress, true);
      }
      catch (Exception ex)
      {
         handleInvokeException(ex);
      }
      finally
      {
         MessageContextAssociation.popMessageContext();
      }
   }

   // 4.17. Conformance (Failed Dispatch.invoke): When an operation is invoked using an invoke method, an
   // implementation MUST throw a WebServiceException if there is any error in the configuration of the
   // Dispatch instance or a ProtocolException if an error occurs during the remote operation invocation.
   //
   // 4.19  Conformance (Failed Dispatch.invokeOneWay): When an operation is invoked using an invoke-
   // OneWay method, an implementation MUST throw a WebServiceException if there is any error in the
   // configuration of the Dispatch instance or if an error is detected1 during the remote operation invocation.
   private void handleInvokeException(Exception ex)
   {
      if (ex instanceof WebServiceException)
      {
         throw (WebServiceException)ex;
      }

      String msg = "Cannot dispatch message";
      log.error(msg, ex);
      throw new WebServiceException(msg, ex);
   }

   public Map<String, Object> getRequestContext()
   {
      return bindingProvider.getRequestContext();
   }

   public Map<String, Object> getResponseContext()
   {
      return bindingProvider.getResponseContext();
   }

   public Binding getBinding()
   {
      return bindingProvider.getBinding();
   }

   private void initDispatch()
   {
      if (SOAPMessage.class.isAssignableFrom(type) && mode == Mode.MESSAGE)
      {
         // accepted
      }
      else if (Source.class.isAssignableFrom(type))
      {
         // accepted
      }
      else if (jaxbContext != null && mode == Mode.PAYLOAD)
      {
         // accepted
      }
      else
      {
         throw new WebServiceException("Illegal argument combination [type=" + (type != null ? type.getName() : null) + ",mode=" + mode + "]");
      }
   }

   private MessageAbstraction getRequestMessage(Object obj)
   {
      // jaxws/api/javax_xml_ws/Dispatch/Client.java#invokeTestJAXBNull
      if (obj == null)
      {
         try
         {
            SOAPFactory factory = SOAPFactory.newInstance();
            SOAPFault fault = factory.createFault("Request object cannot be null", new QName("http://org.jboss.ws", "Dispatch"));
            fault.setFaultActor("client");
            throw new SOAPFaultException(fault);
         }
         catch (SOAPException e)
         {
            //
         }
      }

      String bindingID = bindingProvider.getBinding().getBindingID();
      if (EndpointMetaData.SUPPORTED_BINDINGS.contains(bindingID) == false)
         throw new IllegalStateException("Unsupported binding: " + bindingID);

      MessageAbstraction message;
      if (HTTPBinding.HTTP_BINDING.equals(bindingID))
      {
         DispatchHTTPBinding helper = new DispatchHTTPBinding(mode, type, jaxbContext);
         ((ConfigurationProvider)epMetaData).configure(helper);
         message = helper.getRequestMessage(obj);
      }
      else
      {
         DispatchSOAPBinding helper = new DispatchSOAPBinding(mode, type, jaxbContext);
         ((ConfigurationProvider)epMetaData).configure(helper);
         message = helper.getRequestMessage(obj);
      }
      return message;
   }

   private Object getReturnObject(MessageAbstraction resMsg)
   {
      String bindingID = bindingProvider.getBinding().getBindingID();
      if (EndpointMetaData.SUPPORTED_BINDINGS.contains(bindingID) == false)
         throw new IllegalStateException("Unsupported binding: " + bindingID);

      Object retObj = null;
      if (HTTPBinding.HTTP_BINDING.equals(bindingID))
      {
         DispatchHTTPBinding helper = new DispatchHTTPBinding(mode, type, jaxbContext);
         retObj = helper.getReturnObject(resMsg);
      }
      else
      {
         DispatchSOAPBinding helper = new DispatchSOAPBinding(mode, type, jaxbContext);
         retObj = helper.getReturnObject(resMsg);
      }
      return retObj;
   }

   class AsyncRunnable implements Runnable
   {
      private ResponseImpl response;
      private AsyncHandler handler;
      private Object payload;

      public AsyncRunnable(ResponseImpl response, AsyncHandler handler, Object payload)
      {
         if (response == null)
            throw new IllegalArgumentException("Async response cannot be null");
         if (payload == null)
            throw new IllegalArgumentException("Async payload cannot be null");

         this.response = response;
         this.handler = handler;
         this.payload = payload;
      }

      public void run()
      {
         try
         {
            Map<String, Object> resContext = response.getContext();
            Object result = invokeInternal(payload, resContext);
            response.set(result);
         }
         catch (Exception ex)
         {
            handleAsynInvokeException(ex);
         }
         finally
         {
            // Call the handler if available
            if (handler != null)
               handler.handleResponse(response);
         }
      }

      // 4.18 Conformance (Failed Dispatch.invokeAsync): When an operation is invoked using an invokeAsync
      // method, an implementation MUST throw a WebServiceException if there is any error in the configuration 
      // of the Dispatch instance. Errors that occur during the invocation are reported when the client
      // attempts to retrieve the results of the operation.
      private void handleAsynInvokeException(Exception ex)
      {
         String msg = "Cannot dispatch message";
         log.error(msg, ex);

         WebServiceException wsex;
         if (ex instanceof WebServiceException)
         {
            wsex = (WebServiceException)ex;
         }
         else
         {
            wsex = new WebServiceException(msg, ex);
         }
         response.setException(wsex);
      }
   }

   public EndpointReference getEndpointReference()
   {
      return bindingProvider.getEndpointReference();
   }

   public <T extends EndpointReference> T getEndpointReference(Class<T> clazz)
   {
      return bindingProvider.getEndpointReference(clazz);
   }

   public String getConfigFile()
   {
      return epMetaData.getConfigFile();
   }

   public String getConfigName()
   {
      return epMetaData.getConfigName();
   }

   public void setConfigName(String configName)
   {
      epMetaData.setConfigName(configName);
   }

   public void setConfigName(String configName, String configFile)
   {
      epMetaData.setConfigName(configName, configFile);
   }

   public String getSecurityConfig()
   {
      return securityConfig;
   }

   public void setSecurityConfig(String securityConfig)
   {
      this.securityConfig = securityConfig;

      if (securityConfig != null)
      {
         ServiceMetaData serviceMetaData = epMetaData.getServiceMetaData();
         if (serviceMetaData.getSecurityConfiguration() == null)
         {
            try
            {
               WSSecurityConfigFactory wsseConfFactory = WSSecurityConfigFactory.newInstance();
               UnifiedVirtualFile vfsRoot = serviceMetaData.getUnifiedMetaData().getRootFile();
               WSSecurityConfiguration config = wsseConfFactory.createConfiguration(vfsRoot, securityConfig);
               serviceMetaData.setSecurityConfiguration(config);
            }
            catch (IOException ex)
            {
               WSException.rethrow("Cannot set security config", ex);
            }
         }
      }
   }

   private boolean callRequestHandlerChain(QName portName, HandlerType type)
   {
      BindingExt binding = (BindingExt)bindingProvider.getBinding();
      HandlerChainExecutor executor = new HandlerChainExecutor(epMetaData, binding.getHandlerChain(type), false);
      executorMap.put(type, executor);

      MessageContext msgContext = (MessageContext)MessageContextAssociation.peekMessageContext();
      return executor.handleMessage(msgContext);
   }

   private boolean callResponseHandlerChain(QName portName, HandlerType type)
   {
      MessageContext msgContext = (MessageContext)MessageContextAssociation.peekMessageContext();
      HandlerChainExecutor executor = executorMap.get(type);
      return (executor != null ? executor.handleMessage(msgContext) : true);
   }

   private boolean callFaultHandlerChain(QName portName, HandlerType type, Exception ex)
   {
      MessageContext msgContext = (MessageContext)MessageContextAssociation.peekMessageContext();
      HandlerChainExecutor executor = executorMap.get(type);
      return (executor != null ? executor.handleFault(msgContext, ex) : true);
   }

   private void closeHandlerChain(QName portName, HandlerType type)
   {
      MessageContext msgContext = (MessageContext)MessageContextAssociation.peekMessageContext();
      HandlerChainExecutor executor = executorMap.get(type);
      if (executor != null)
         executor.close(msgContext);
   }
   
   private OperationMetaData getOperationMetaData(EndpointMetaData epMetaData, MessageAbstraction reqMessage) throws SOAPException
   {
      OperationMetaData opMetaData = null;
      if (HTTPBinding.HTTP_BINDING.equals(epMetaData.getBindingId()) && epMetaData.getOperations().size() == 1)
      {
         opMetaData = epMetaData.getOperations().get(0);
      }
      else if (reqMessage instanceof SOAPMessageImpl)
      {
         SOAPMessageImpl soapMessage = (SOAPMessageImpl)reqMessage;
         opMetaData = soapMessage.getOperationMetaData(epMetaData);
      }
      if (opMetaData == null)
         log.debug("Cannot find the right operation metadata!");
      return opMetaData;
   }
}
