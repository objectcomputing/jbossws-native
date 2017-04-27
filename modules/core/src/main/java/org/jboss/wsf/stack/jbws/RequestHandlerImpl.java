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
package org.jboss.wsf.stack.jbws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.http.HTTPBinding;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonBinding;
import org.jboss.ws.core.CommonBindingProvider;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.CommonSOAPFaultException;
import org.jboss.ws.core.HTTPMessageImpl;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.MessageTrace;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.jaxrpc.handler.MessageContextJAXRPC;
import org.jboss.ws.core.jaxrpc.handler.SOAPMessageContextJAXRPC;
import org.jboss.ws.core.jaxws.handler.MessageContextJAXWS;
import org.jboss.ws.core.jaxws.handler.SOAPMessageContextJAXWS;
import org.jboss.ws.core.server.MimeHeaderSource;
import org.jboss.ws.core.server.ServiceEndpointInvoker;
import org.jboss.ws.core.server.ServletHeaderSource;
import org.jboss.ws.core.server.ServletRequestContext;
import org.jboss.ws.core.server.WSDLRequestHandler;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.core.soap.SOAPBodyImpl;
import org.jboss.ws.core.soap.SOAPConnectionImpl;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.core.utils.ThreadLocalAssociation;
import org.jboss.ws.extensions.addressing.AddressingConstantsImpl;
import org.jboss.ws.extensions.json.BadgerFishDOMDocumentParser;
import org.jboss.ws.extensions.json.BadgerFishDOMDocumentSerializer;
import org.jboss.ws.extensions.wsrm.RMConstant;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.ws.feature.FastInfosetFeature;
import org.jboss.ws.feature.JsonEncodingFeature;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.wsf.common.IOUtils;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.deployment.Endpoint.EndpointState;
import org.jboss.wsf.spi.invocation.InvocationContext;
import org.jboss.wsf.spi.invocation.RequestHandler;
import org.jboss.wsf.spi.management.EndpointMetrics;
import org.jboss.wsf.spi.management.ServerConfig;
import org.jboss.wsf.spi.management.ServerConfigFactory;
import org.w3c.dom.Document;

import com.sun.xml.fastinfoset.dom.DOMDocumentSerializer;

/**
 * A request handler
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 25-Apr-2007
 */
public class RequestHandlerImpl implements RequestHandler
{
   // provide logging
   private static final Logger log = Logger.getLogger(RequestHandlerImpl.class);

   private ServerConfig serverConfig;
   private MessageFactoryImpl msgFactory;

   RequestHandlerImpl()
   {
      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      serverConfig = spiProvider.getSPI(ServerConfigFactory.class).getServerConfig();
      msgFactory = new MessageFactoryImpl();
   }

   public void handleHttpRequest(Endpoint endpoint, HttpServletRequest req, HttpServletResponse res, ServletContext context) throws ServletException, IOException
   {
      String method = req.getMethod();
      if (method.equals("POST"))
      {
         doPost(endpoint, req, res, context);
      }
      else if (method.equals("GET"))
      {
         doGet(endpoint, req, res, context);
      }
      else
      {
         throw new WSException("Unsupported method: " + method);
      }
   }

   private void doGet(Endpoint endpoint, HttpServletRequest req, HttpServletResponse res, ServletContext context) throws ServletException, IOException
   {
      // Process a WSDL request
      if (req.getParameter("wsdl") != null || req.getParameter("WSDL") != null)
      {
         res.setContentType("text/xml");
         ServletOutputStream out = res.getOutputStream();
         try
         {
            ServletRequestContext reqContext = new ServletRequestContext(context, req, res);
            handleWSDLRequest(endpoint, out, reqContext);
         }
         catch (Exception ex)
         {
            handleException(ex);
         }
         finally
         {
            try
            {
               out.close();
            }
            catch (IOException ioex)
            {
               if (log.isTraceEnabled() == true)
               {
                  log.trace("Cannot close output stream", ioex);
               }
               else
               {
                  log.debug("Cannot close output stream");
               }
            }
         }
      }
      else
      {
         res.setStatus(405);
         res.setContentType("text/plain");
         Writer out = res.getWriter();
         out.write("HTTP GET not supported");
         out.close();
      }
   }

   private void doPost(Endpoint endpoint, HttpServletRequest req, HttpServletResponse res, ServletContext context) throws ServletException, IOException
   {
      log.debug("doPost: " + req.getRequestURI());

      ServletInputStream in = req.getInputStream();
      ServletOutputStream out = res.getOutputStream();

      ClassLoader classLoader = endpoint.getService().getDeployment().getRuntimeClassLoader();
      if (classLoader == null)
         throw new IllegalStateException("Deployment has no classloader associated");

      // Set the thread context class loader
      ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(classLoader);
      try
      {
         ServletRequestContext reqContext = new ServletRequestContext(context, req, res);
         handleRequest(endpoint, in, out, reqContext);
      }
      catch (Exception ex)
      {
         handleException(ex);
      }
      finally
      {
         // Reset the thread context class loader
         Thread.currentThread().setContextClassLoader(ctxClassLoader);

         try
         {
            out.close();
         }
         catch (IOException ioex)
         {
            if (log.isTraceEnabled() == true)
            {
               log.trace("Cannot close output stream", ioex);
            }
            else
            {
               log.debug("Cannot close output stream");
            }
         }
      }
   }

   public void handleRequest(Endpoint endpoint, InputStream inStream, OutputStream outStream, InvocationContext invContext)
   {
      log.debug("handleRequest: " + endpoint.getName());

      ServerEndpointMetaData sepMetaData = endpoint.getAttachment(ServerEndpointMetaData.class);
      if (sepMetaData == null)
         throw new IllegalStateException("Cannot obtain endpoint meta data");

      Type type = sepMetaData.getType();

      // Build the message context
      CommonMessageContext msgContext;
      if (type == EndpointMetaData.Type.JAXRPC)
      {
         msgContext = new SOAPMessageContextJAXRPC();
         invContext.addAttachment(javax.xml.rpc.handler.MessageContext.class, msgContext);
      }
      else
      {
         msgContext = new SOAPMessageContextJAXWS();
         msgContext.put(MessageContextJAXWS.MESSAGE_OUTBOUND_PROPERTY, Boolean.valueOf(false));
         msgContext.put(MessageContextJAXWS.INBOUND_MESSAGE_ATTACHMENTS, new HashMap<String, DataHandler>());
         invContext.addAttachment(javax.xml.ws.handler.MessageContext.class, msgContext);
      }

      // Set servlet specific properties
      HttpServletResponse httpResponse = null;
      ServletHeaderSource headerSource = null;
      if (invContext instanceof ServletRequestContext)
      {
         ServletRequestContext reqContext = (ServletRequestContext)invContext;

         ServletContext servletContext = reqContext.getServletContext();
         HttpServletRequest httpRequest = reqContext.getHttpServletRequest();
         httpResponse = reqContext.getHttpServletResponse();
         headerSource = new ServletHeaderSource(httpRequest, httpResponse);

         if (type == EndpointMetaData.Type.JAXRPC)
         {
            msgContext.put(MessageContextJAXRPC.SERVLET_CONTEXT, servletContext);
            msgContext.put(MessageContextJAXRPC.SERVLET_REQUEST, httpRequest);
            msgContext.put(MessageContextJAXRPC.SERVLET_RESPONSE, httpResponse);
         }
         else
         {
            msgContext.put(MessageContextJAXWS.HTTP_REQUEST_HEADERS, headerSource.getHeaderMap());
            msgContext.put(MessageContextJAXWS.HTTP_REQUEST_METHOD, httpRequest.getMethod());
            msgContext.put(MessageContextJAXWS.QUERY_STRING, httpRequest.getQueryString());
            msgContext.put(MessageContextJAXWS.PATH_INFO, httpRequest.getPathInfo());
            msgContext.put(MessageContextJAXWS.SERVLET_CONTEXT, servletContext);
            msgContext.put(MessageContextJAXWS.SERVLET_REQUEST, httpRequest);
            msgContext.put(MessageContextJAXWS.SERVLET_RESPONSE, httpResponse);
         }
      }

      // Associate a message context with the current thread
      MessageContextAssociation.pushMessageContext(msgContext);

      try
      {
         msgContext.setEndpointMetaData(sepMetaData);
         MessageAbstraction resMessage = processRequest(endpoint, headerSource, invContext, inStream);

         // Replace the message context with the response context
         msgContext = MessageContextAssociation.peekMessageContext();

         Map<String, List<String>> headers = (Map<String, List<String>>)msgContext.get(MessageContextJAXWS.HTTP_RESPONSE_HEADERS);
         if (headerSource != null && headers != null)
            headerSource.setHeaderMap(headers);

         Integer code = (Integer)msgContext.get(MessageContextJAXWS.HTTP_RESPONSE_CODE);
         if (httpResponse != null && code != null)
            httpResponse.setStatus(code.intValue());

         boolean isFault = false;
         if (resMessage instanceof SOAPMessage)
         {
            SOAPPart part = ((SOAPMessage)resMessage).getSOAPPart();
            if (part == null)
               throw new SOAPException("Cannot obtain SOAPPart from response message");

            // R1126 An INSTANCE MUST return a "500 Internal Server Error" HTTP status code
            // if the response envelope is a Fault.
            //
            // Also, a one-way operation must show up as empty content, and can be detected
            // by a null envelope.
            SOAPEnvelope soapEnv = part.getEnvelope();
            isFault = soapEnv != null && soapEnv.getBody().hasFault();
            if (httpResponse != null && isFault)
            {
               httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
         }

         Map<String, Object> rmResCtx = (Map<String, Object>)msgContext.get(RMConstant.RESPONSE_CONTEXT);
         boolean isWsrmMessage = rmResCtx != null;
         boolean isWsrmOneWay = isWsrmMessage && (Boolean)rmResCtx.get(RMConstant.ONE_WAY_OPERATION);
         if ((outStream != null) && (isWsrmOneWay == false)) // RM hack
            sendResponse(endpoint, outStream, isFault);
      }
      catch (Exception ex)
      {
         WSException.rethrow(ex);
      }
      finally
      {
         // Cleanup outbound attachments
         CommonMessageContext.cleanupAttachments(MessageContextAssociation.peekMessageContext());

         // Reset the message context association
         MessageContextAssociation.popMessageContext();

         // clear thread local storage
         ThreadLocalAssociation.clear();
      }
   }

   private void sendResponse(Endpoint endpoint, OutputStream output, boolean isFault) throws SOAPException, IOException
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      EndpointMetaData epMetaData = msgContext.getEndpointMetaData();
      MessageAbstraction resMessage = msgContext.getMessageAbstraction();

      String wsaTo = null;

      // Get the destination from the AddressingProperties
      AddressingProperties outProps = (AddressingProperties)msgContext.get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_OUTBOUND);
      if (outProps != null && outProps.getTo() != null)
      {
         AddressingConstantsImpl ADDR = new AddressingConstantsImpl();
         wsaTo = outProps.getTo().getURI().toString();
         if (wsaTo.equals(ADDR.getAnonymousURI()))
            wsaTo = null;
      }
      if (wsaTo != null)
      {
         log.debug("Sending response to addressing destination: " + wsaTo);
         SOAPMessage soapMessage = (SOAPMessage)resMessage;
         new SOAPConnectionImpl().callOneWay(soapMessage, wsaTo);
      }
      else
      {
         // FastInfoset support
         if (epMetaData.isFeatureEnabled(FastInfosetFeature.class) && resMessage instanceof SOAPMessage)
         {
            SOAPMessage soapMessage = (SOAPMessage)resMessage;
            if (soapMessage.getAttachments().hasNext())
               throw new IllegalStateException("Attachments not supported with FastInfoset");

            SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
            DOMDocumentSerializer serializer = new DOMDocumentSerializer();
            serializer.setOutputStream(output);
            serializer.serialize(soapEnv);
         }
         // JSON support
         else if (epMetaData.isFeatureEnabled(JsonEncodingFeature.class) && resMessage instanceof SOAPMessage)
         {
            SOAPMessage soapMessage = (SOAPMessage)resMessage;
            if (soapMessage.getAttachments().hasNext())
               throw new IllegalStateException("Attachments not supported with JSON");

            SOAPBodyImpl soapBody = (SOAPBodyImpl)soapMessage.getSOAPBody();
            BadgerFishDOMDocumentSerializer serializer = new BadgerFishDOMDocumentSerializer(output);
            serializer.serialize(soapBody.getBodyElement());
         }
         else
         {
            resMessage.writeTo(output);
         }
      }
   }

   /**
    * Handle a request to this web service endpoint
    */
   private MessageAbstraction processRequest(Endpoint ep, MimeHeaderSource headerSource, InvocationContext reqContext, InputStream inputStream) throws BindingException
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();

      ServerEndpointMetaData sepMetaData = ep.getAttachment(ServerEndpointMetaData.class);
      if (sepMetaData == null)
         throw new IllegalStateException("Cannot obtain endpoint meta data");

      long beginProcessing = 0;
      ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         EndpointState state = ep.getState();
         if (state != EndpointState.STARTED)
         {
            QName faultCode = Constants.SOAP11_FAULT_CODE_SERVER;
            String faultString = "Endpoint cannot handle requests in state: " + state;
            throw new CommonSOAPFaultException(faultCode, faultString);
         }

         log.debug("BEGIN handleRequest: " + ep.getName());
         beginProcessing = initRequestMetrics(ep);

         MimeHeaders headers = (headerSource != null ? headerSource.getMimeHeaders() : null);

         MessageAbstraction reqMessage;

         String bindingID = sepMetaData.getBindingId();
         if (HTTPBinding.HTTP_BINDING.equals(bindingID))
         {
            reqMessage = new HTTPMessageImpl(headers, inputStream);
         }
         else if (sepMetaData.isFeatureEnabled(JsonEncodingFeature.class))
         {
            MessageFactoryImpl factory = new MessageFactoryImpl();
            SOAPMessageImpl soapMsg = (SOAPMessageImpl)factory.createMessage();
            Document doc = new BadgerFishDOMDocumentParser().parse(inputStream);
            soapMsg.getSOAPBody().addDocument(doc);
            reqMessage = soapMsg;
         }
         else
         {
            msgFactory.setServiceMode(sepMetaData.getServiceMode());
            msgFactory.setStyle(sepMetaData.getStyle());
            msgFactory.setFeatures(sepMetaData.getFeatures());

            reqMessage = (SOAPMessageImpl)msgFactory.createMessage(headers, inputStream);
         }

         // Associate current message with message context
         msgContext.setMessageAbstraction(reqMessage);

         // debug the incomming message
         MessageTrace.traceMessage("Incoming Request Message", reqMessage);

         // Set the thread context class loader
         ClassLoader classLoader = sepMetaData.getClassLoader();
         Thread.currentThread().setContextClassLoader(classLoader);

         // Get the Invoker
         ServiceEndpointInvoker epInvoker = ep.getAttachment(ServiceEndpointInvoker.class);
         if (epInvoker == null)
            throw new IllegalStateException("Cannot obtain ServiceEndpointInvoker");

         // Invoke the service endpoint
         epInvoker.invoke(reqContext);

         // Get the response message context
         msgContext = MessageContextAssociation.peekMessageContext();

         // Get the response message
         MessageAbstraction resMessage = msgContext.getMessageAbstraction();
         if (resMessage != null)
            postProcessResponse(headerSource, resMessage);

         return resMessage;
      }
      catch (Exception ex)
      {
         MessageAbstraction resMessage = MessageContextAssociation.peekMessageContext().getMessageAbstraction();

         // In case we have an exception before the invoker is called
         // we create the fault message here.
         if (resMessage == null || resMessage.isFaultMessage() == false)
         {
            CommonBindingProvider bindingProvider = new CommonBindingProvider(sepMetaData);
            CommonBinding binding = bindingProvider.getCommonBinding();
            resMessage = binding.bindFaultMessage(ex);
         }

         if (resMessage != null)
            postProcessResponse(headerSource, resMessage);

         return resMessage;
      }
      finally
      {
         try
         {
            MessageAbstraction resMessage = MessageContextAssociation.peekMessageContext().getMessageAbstraction();
            if (resMessage != null)
            {
               if (resMessage.isFaultMessage())
               {
                  processFaultMetrics(ep, beginProcessing);
               }
               else
               {
                  processResponseMetrics(ep, beginProcessing);
               }
            }
         }
         catch (Exception ex)
         {
            log.error("Cannot process metrics", ex);
         }

         // Reset the thread context class loader
         Thread.currentThread().setContextClassLoader(ctxClassLoader);
         log.debug("END handleRequest: " + ep.getName());
      }
   }

   private long initRequestMetrics(Endpoint endpoint)
   {
      long beginTime = 0;

      EndpointMetrics metrics = endpoint.getEndpointMetrics();
      if (metrics != null)
         beginTime = metrics.processRequestMessage();

      return beginTime;
   }

   private void processResponseMetrics(Endpoint endpoint, long beginTime)
   {
      EndpointMetrics metrics = endpoint.getEndpointMetrics();
      if (metrics != null)
         metrics.processResponseMessage(beginTime);
   }

   private void processFaultMetrics(Endpoint endpoint, long beginTime)
   {
      EndpointMetrics metrics = endpoint.getEndpointMetrics();
      if (metrics != null)
         metrics.processFaultMessage(beginTime);
   }

   /** Set response mime headers
    */
   private void postProcessResponse(MimeHeaderSource headerSource, MessageAbstraction resMessage)
   {
      try
      {
         // Set the outbound headers
         if (headerSource != null && resMessage instanceof SOAPMessage)
         {
            XOPContext.eagerlyCreateAttachments();
            ((SOAPMessage)resMessage).saveChanges();
            headerSource.setMimeHeaders(resMessage.getMimeHeaders());
         }

         // debug the outgoing message
         MessageTrace.traceMessage("Outgoing Response Message", resMessage);
      }
      catch (Exception ex)
      {
         WSException.rethrow("Faild to post process response message", ex);
      }
   }

   public void handleWSDLRequest(Endpoint endpoint, OutputStream outStream, InvocationContext context)
   {
      log.debug("handleWSDLRequest: " + endpoint.getName());

      try
      {
         if (context instanceof ServletRequestContext)
         {
            handleWSDLRequestFromServletContext(endpoint, outStream, context);
         }
         else
         {
            String epAddress = endpoint.getAddress();
            if (epAddress == null)
               throw new IllegalArgumentException("Invalid endpoint address: " + epAddress);

            URL wsdlUrl = new URL(epAddress + "?wsdl");
            IOUtils.copyStream(outStream, wsdlUrl.openStream());
         }
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (IOException ex)
      {
         throw new WSException(ex);
      }
   }

   private void handleWSDLRequestFromServletContext(Endpoint endpoint, OutputStream outputStream, InvocationContext context) throws MalformedURLException, IOException
   {
      ServerEndpointMetaData epMetaData = endpoint.getAttachment(ServerEndpointMetaData.class);
      if (epMetaData == null)
         throw new IllegalStateException("Cannot obtain endpoint meta data");

      ServletRequestContext reqContext = (ServletRequestContext)context;
      HttpServletRequest req = reqContext.getHttpServletRequest();

      // For the base document the resourcePath should be null
      String resPath = (String)req.getParameter("resource");
      URL reqURL = new URL(req.getRequestURL().toString());

      String wsdlHost = reqURL.getProtocol() + "://" + reqURL.getHost();
      if (reqURL.getPort() != -1)
         wsdlHost += ":" + reqURL.getPort();

      if (ServerConfig.UNDEFINED_HOSTNAME.equals(serverConfig.getWebServiceHost()) == false)
         wsdlHost = serverConfig.getWebServiceHost();

      log.debug("WSDL request, using host: " + wsdlHost);

      WSDLRequestHandler wsdlRequestHandler = new WSDLRequestHandler(epMetaData);
      Document document = wsdlRequestHandler.getDocumentForPath(reqURL, wsdlHost, resPath);

      OutputStreamWriter writer = new OutputStreamWriter(outputStream);
      new DOMWriter(writer).setPrettyprint(true).print(document.getDocumentElement());
   }

   private void handleException(Exception ex) throws ServletException
   {
      log.error("Error processing web service request", ex);

      if (ex instanceof JAXRPCException)
         throw (JAXRPCException)ex;

      throw new ServletException(ex);
   }
}
