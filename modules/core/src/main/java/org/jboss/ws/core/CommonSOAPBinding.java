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
package org.jboss.ws.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;

import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.jaxrpc.ParameterWrapping;
import org.jboss.ws.core.jaxws.handler.MessageContextJAXWS;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.core.soap.NameImpl;
import org.jboss.ws.core.soap.SOAPBodyElementDoc;
import org.jboss.ws.core.soap.SOAPBodyElementRpc;
import org.jboss.ws.core.soap.SOAPBodyImpl;
import org.jboss.ws.core.soap.SOAPContentElement;
import org.jboss.ws.core.soap.SOAPElementImpl;
import org.jboss.ws.core.soap.SOAPFactoryImpl;
import org.jboss.ws.core.soap.SOAPFaultImpl;
import org.jboss.ws.core.soap.SOAPHeaderElementImpl;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.core.soap.UnboundHeader;
import org.jboss.ws.core.soap.Use;
import org.jboss.ws.core.soap.attachment.AttachmentPartImpl;
import org.jboss.ws.core.soap.attachment.CIDGenerator;
import org.jboss.ws.core.utils.MimeUtils;
import org.jboss.ws.extensions.wsrm.RMConstant;
import org.jboss.ws.extensions.wsrm.common.RMHelper;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.umdm.TypesMetaData;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.JavaUtils;
import org.jboss.xb.binding.NamespaceRegistry;
import org.w3c.dom.Element;

/**
 * The SOAPBinding interface is an abstraction for the SOAP binding.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jul-2006
 */
public abstract class CommonSOAPBinding implements CommonBinding
{
   // provide logging
   protected Logger log = Logger.getLogger(getClass());

   private boolean mtomEnabled;

   protected HeaderSource headerSource;

   /** A constant representing the identity of the SOAP 1.1 over HTTP binding. */
   public static final String SOAP11HTTP_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http";
   /** A constant representing the identity of the SOAP 1.2 over HTTP binding. */
   public static final String SOAP12HTTP_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
   /** A constant representing the identity of the SOAP 1.1 over HTTP binding with MTOM enabled by default. */
   public static final String SOAP11HTTP_MTOM_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";
   /** A constant representing the identity of the SOAP 1.2 over HTTP binding with MTOM enabled by default. */
   public static final String SOAP12HTTP_MTOM_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";
   /** The SOAP encoded Array name */
   private static final Name SOAP_ARRAY_NAME = new NameImpl("Array", Constants.PREFIX_SOAP11_ENC, Constants.URI_SOAP11_ENC);

   public CommonSOAPBinding()
   {
   }

   public MessageFactory getMessageFactory()
   {
      return new MessageFactoryImpl();
   }

   public SOAPFactory getSOAPFactory()
   {
      return new SOAPFactoryImpl();
   }

   public boolean isMTOMEnabled()
   {
      return this.mtomEnabled;
   }

   public void setMTOMEnabled(boolean flag)
   {
      this.mtomEnabled = flag;
   }

   /** Create the message */
   protected abstract MessageAbstraction createMessage(OperationMetaData opMetaData) throws SOAPException;

   /** On the client side, generate the payload from IN parameters. */
   public MessageAbstraction bindRequestMessage(OperationMetaData opMetaData, EndpointInvocation epInv, Map<QName, UnboundHeader> unboundHeaders)
         throws BindingException
   {
      if (log.isDebugEnabled())
         log.debug("bindRequestMessage: " + opMetaData.getQName());

      try
      {
         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         if (msgContext == null)
            throw new WSException("MessageContext not available");

         // Disable MTOM for rpc/encoded
         if (opMetaData.isRPCEncoded())
            XOPContext.setMTOMEnabled(false);
         else
            XOPContext.setMTOMEnabled(isMTOMEnabled());

         // Associate current message with message context
         SOAPMessageImpl reqMessage = (SOAPMessageImpl)createMessage(opMetaData);
         msgContext.setSOAPMessage(reqMessage);

         SOAPEnvelope soapEnvelope = reqMessage.getSOAPPart().getEnvelope();
         SOAPBody soapBody = soapEnvelope.getBody();
         SOAPHeader soapHeader = soapEnvelope.getHeader();

         // Get the namespace registry
         NamespaceRegistry namespaceRegistry = msgContext.getNamespaceRegistry();

         Style style = opMetaData.getStyle();
         SOAPElement soapBodyElement = soapBody;
         if (style == Style.RPC)
         {
            boolean serialize = true;

            if (opMetaData.getEndpointMetaData().getConfig().getRMMetaData() != null)
            {
               // RM hack to JAX-RPC serialization
               if (RMHelper.isRMOperation(opMetaData.getQName()))
               {
                  serialize = false;
               }
            }

            if (serialize)
            {
               QName opQName = opMetaData.getQName();
               Name opName = new NameImpl(namespaceRegistry.registerQName(opQName));

               if (log.isDebugEnabled())
                  log.debug("Create RPC body element: " + opName);

               soapBodyElement = new SOAPBodyElementRpc(opName);
               soapBodyElement = (SOAPBodyElement)soapBody.addChildElement(soapBodyElement);

               // Add soap encodingStyle
               if (opMetaData.getUse() == Use.ENCODED)
               {
                  String envURI = soapEnvelope.getNamespaceURI();
                  String envPrefix = soapEnvelope.getPrefix();
                  soapBodyElement.setAttributeNS(envURI, envPrefix + ":encodingStyle", Constants.URI_SOAP11_ENC);
               }
            }
         }

         for (ParameterMetaData paramMetaData : opMetaData.getInputParameters())
         {
            QName xmlName = paramMetaData.getXmlName();
            Object value = epInv.getRequestParamValue(xmlName);

            if (paramMetaData.isSwA())
            {
               // NOTE: swa:ref is handled by the AttachmentMarshaller callback
               CIDGenerator cidGenerator = reqMessage.getCidGenerator();
               AttachmentPart part = createAttachmentPart(paramMetaData, value, cidGenerator);
               reqMessage.addAttachmentPart(part);

               // Add the attachment to the standard property
               if (value instanceof DataHandler && msgContext instanceof MessageContextJAXWS)
               {
                  DataHandler dataHandler = (DataHandler)value;
                  Map<String, DataHandler> attachments = (Map<String, DataHandler>)msgContext.get(MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS);
                  attachments.put(dataHandler.getContentType(), dataHandler);
               }
            }
            else
            {
               SOAPElement soapElement = paramMetaData.isInHeader() ? (SOAPElement)soapHeader : soapBodyElement;
               addParameterToMessage(paramMetaData, value, soapElement);
            }
         }

         // Add unbound headers
         if (unboundHeaders != null)
         {
            Iterator it = unboundHeaders.values().iterator();
            while (it.hasNext())
            {
               UnboundHeader unboundHeader = (UnboundHeader)it.next();
               if (unboundHeader.getMode() != ParameterMode.OUT)
               {
                  QName xmlName = unboundHeader.getXmlName();
                  Object value = unboundHeader.getHeaderValue();

                  xmlName = namespaceRegistry.registerQName(xmlName);
                  Name soapName = new NameImpl(xmlName.getLocalPart(), xmlName.getPrefix(), xmlName.getNamespaceURI());

                  log.debug("Add unboundHeader element: " + soapName);
                  SOAPContentElement contentElement = new SOAPHeaderElementImpl(soapName);
                  contentElement.setParamMetaData(unboundHeader.toParameterMetaData(opMetaData));

                  if (soapHeader == null)
                     soapHeader = soapEnvelope.addHeader();

                  soapHeader.addChildElement(contentElement);
                  contentElement.setObjectValue(value);
               }
            }
         }

         // Set the SOAPAction
         setSOAPActionHeader(opMetaData, reqMessage);

         return reqMessage;
      }
      catch (Exception e)
      {
         handleException(e);
         return null;
      }
   }

   /** Override to set the SOAPAction header */
   public abstract void setSOAPActionHeader(OperationMetaData opMetaData, SOAPMessage reqMessage);

   /** On the server side, extract the IN parameters from the payload and populate an Invocation object */
   public EndpointInvocation unbindRequestMessage(OperationMetaData opMetaData, MessageAbstraction payload) throws BindingException
   {
      if (log.isDebugEnabled())
         log.debug("unbindRequestMessage: " + opMetaData.getQName());

      try
      {
         // Read the SOAPEnvelope from the reqMessage
         SOAPMessageImpl reqMessage = (SOAPMessageImpl)payload;
         SOAPEnvelope soapEnvelope = reqMessage.getSOAPPart().getEnvelope();
         SOAPHeader soapHeader = soapEnvelope.getHeader();
         SOAPBody soapBody = soapEnvelope.getBody();

         // Verify the SOAP version
         verifySOAPVersion(opMetaData, soapEnvelope);

         // Construct the endpoint invocation object
         EndpointInvocation epInv = new EndpointInvocation(opMetaData);

         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         if (msgContext == null)
            throw new WSException("MessageContext not available");

         // Disable MTOM for rpc/encoded
         if (opMetaData.isRPCEncoded())
            msgContext.put(StubExt.PROPERTY_MTOM_ENABLED, Boolean.FALSE);

         // Get the namespace registry
         NamespaceRegistry namespaceRegistry = msgContext.getNamespaceRegistry();

         if (opMetaData.isMessageEndpoint() == false)
         {
            Style style = opMetaData.getStyle();
            SOAPElement payloadParent = soapBody;
            if (style == Style.RPC)
            {
               payloadParent = null;
               Iterator it = soapBody.getChildElements();
               while (payloadParent == null && it.hasNext())
               {
                  Object childNode = it.next();
                  if (childNode instanceof SOAPElement)
                  {
                     payloadParent = (SOAPElement)childNode;
                  }
               }

               if (RMHelper.isRMOperation(opMetaData.getQName()) == false) // RM hack
               {
                  if (payloadParent == null)
                     throw new SOAPException("Cannot find RPC element in");

                  QName elName = payloadParent.getElementQName();
                  elName = namespaceRegistry.registerQName(elName);
               }
            }

            int numParameters = 0;
            for (ParameterMetaData paramMetaData : opMetaData.getParameters())
            {
               QName xmlName = paramMetaData.getXmlName();
               if (paramMetaData.getMode() == ParameterMode.OUT)
               {
                  epInv.setRequestParamValue(xmlName, null);
               }
               else
               {
                  if (paramMetaData.isSwA())
                  {
                     AttachmentPart part = getAttachmentFromMessage(paramMetaData, reqMessage);
                     epInv.setRequestParamValue(xmlName, part);

                     // Add the attachment to the standard property
                     if (part.getDataHandler() != null && msgContext instanceof MessageContextJAXWS)
                     {
                        DataHandler dataHandler = part.getDataHandler();
                        Map<String, DataHandler> attachments = (Map<String, DataHandler>)msgContext.get(MessageContext.INBOUND_MESSAGE_ATTACHMENTS);
                        attachments.put(part.getContentId(), dataHandler);
                     }
                  }
                  else
                  {
                     boolean isHeader = paramMetaData.isInHeader();
                     SOAPElement element = isHeader ? soapHeader : payloadParent;
                     if (!isHeader)
                        numParameters++;

                     SOAPContentElement value = getParameterFromMessage(paramMetaData, element, false);
                     epInv.setRequestParamValue(xmlName, value);
                  }
               }
            }

            if (RMHelper.isRMOperation(opMetaData.getQName()) == false)
            {
               // Verify the numer of parameters matches the actual message payload
               int numChildElements = 0;
               Iterator itElements = payloadParent.getChildElements();
               while (itElements.hasNext())
               {
                  Node node = (Node)itElements.next();
                  if (node instanceof SOAPElement)
                     numChildElements++;
               }
               if (numChildElements != numParameters)
                  throw new WSException("Invalid number of payload elements: " + numChildElements);
            }
         }

         // Generic message endpoint
         else
         {
            for (ParameterMetaData paramMetaData : opMetaData.getParameters())
            {
               QName xmlName = paramMetaData.getXmlName();
               Object value = soapBody.getChildElements().next();
               epInv.setRequestParamValue(xmlName, value);
            }
         }

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
      if (log.isDebugEnabled())
         log.debug("bindResponseMessage: " + opMetaData.getQName());

      try
      {
         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         if (msgContext == null)
            throw new WSException("MessageContext not available");

         // Disable MTOM for rpc/encoded
         if (opMetaData.isRPCEncoded())
            XOPContext.setMTOMEnabled(false);
         else
            XOPContext.setMTOMEnabled(isMTOMEnabled());

         // Associate current message with message context
         SOAPMessageImpl resMessage = (SOAPMessageImpl)createMessage(opMetaData);
         msgContext.setSOAPMessage(resMessage);

         // R2714 For one-way operations, an INSTANCE MUST NOT return a HTTP response that contains a SOAP envelope.
         // Specifically, the HTTP response entity-body must be empty.
         boolean isWsrmMessage = msgContext.get(RMConstant.RESPONSE_CONTEXT) != null;
         if (opMetaData.isOneWay() && (false == isWsrmMessage))
         {
            resMessage.getSOAPPart().setContent(null);
            return resMessage;
         }

         SOAPEnvelope soapEnvelope = resMessage.getSOAPPart().getEnvelope();
         SOAPHeader soapHeader = soapEnvelope.getHeader();
         SOAPBody soapBody = soapEnvelope.getBody();

         // Get the namespace registry
         NamespaceRegistry namespaceRegistry = msgContext.getNamespaceRegistry();

         Style style = opMetaData.getStyle();
         SOAPElement soapBodyElement = soapBody;
         if (style == Style.RPC)
         {
            QName opQName = opMetaData.getResponseName();

            if (false == RMHelper.isRMOperation(opQName)) // RM hack
            {
               Name opName = new NameImpl(namespaceRegistry.registerQName(opQName));
               soapBodyElement = new SOAPBodyElementRpc(opName);
               soapBodyElement = (SOAPBodyElement)soapBody.addChildElement(soapBodyElement);

               // Add soap encodingStyle
               if (opMetaData.getUse() == Use.ENCODED)
               {
                  String envURI = soapEnvelope.getNamespaceURI();
                  String envPrefix = soapEnvelope.getPrefix();
                  soapBodyElement.setAttributeNS(envURI, envPrefix + ":encodingStyle", Constants.URI_SOAP11_ENC);
               }
            }
         }

         // Add the return to the message
         ParameterMetaData retMetaData = opMetaData.getReturnParameter();
         if (retMetaData != null)
         {
            Object value = epInv.getReturnValue();

            // TODO calls to ParameterWrapping should be elsewhere
            if (opMetaData.isDocumentWrapped())
               value = ParameterWrapping.wrapResponseParameters(retMetaData, value, epInv.getOutParameters());

            if (retMetaData.isSwA())
            {
               CIDGenerator cidGenerator = resMessage.getCidGenerator();
               AttachmentPart part = createAttachmentPart(retMetaData, value, cidGenerator);
               resMessage.addAttachmentPart(part);
               epInv.setReturnValue(part);

               // Add the attachment to the standard property
               if (part.getDataHandler() != null && msgContext instanceof MessageContextJAXWS)
               {
                  DataHandler dataHandler = part.getDataHandler();
                  Map<String, DataHandler> attachments = (Map<String, DataHandler>)msgContext.get(MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS);
                  attachments.put(part.getContentId(), dataHandler);
               }
            }
            else
            {
               SOAPContentElement soapElement = addParameterToMessage(retMetaData, value, soapBodyElement);
               epInv.setReturnValue(soapElement);
               soapElement.setObjectValue(value);
            }
         }

         // Add the out parameters to the message
         for (ParameterMetaData paramMetaData : opMetaData.getOutputParameters())
         {
            QName xmlName = paramMetaData.getXmlName();
            Object value = epInv.getResponseParamValue(xmlName);
            if (paramMetaData.isSwA())
            {
               CIDGenerator cidGenerator = resMessage.getCidGenerator();
               AttachmentPart part = createAttachmentPart(paramMetaData, value, cidGenerator);
               resMessage.addAttachmentPart(part);

               // Add the attachment to the standard property
               if (value instanceof DataHandler && msgContext instanceof MessageContextJAXWS)
               {
                  DataHandler dataHandler = (DataHandler)value;
                  Map<String, DataHandler> attachments = (Map<String, DataHandler>)msgContext.get(MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS);
                  attachments.put(dataHandler.getContentType(), dataHandler);
               }
            }
            else
            {
               if (paramMetaData.isInHeader())
               {
                  addParameterToMessage(paramMetaData, value, soapHeader);
               }
               else
               {
                  addParameterToMessage(paramMetaData, value, soapBodyElement);
               }
            }
         }

         return resMessage;
      }
      catch (Exception e)
      {
         handleException(e);
         return null;
      }
   }

   /** On the client side, extract the OUT parameters from the payload and return them to the client. */
   public void unbindResponseMessage(OperationMetaData opMetaData, MessageAbstraction payload, EndpointInvocation epInv, Map<QName, UnboundHeader> unboundHeaders)
         throws BindingException
   {
      if (log.isDebugEnabled())
         log.debug("unbindResponseMessage: " + opMetaData.getQName());

      try
      {
         // R2714 For one-way operations, an INSTANCE MUST NOT return a HTTP response that contains a SOAP envelope.
         // Specifically, the HTTP response entity-body must be empty.
         if (opMetaData.isOneWay() == true)
         {
            return;
         }

         // WS-Addressing might redirect the response, which results in an empty envelope
         SOAPMessageImpl resMessage = (SOAPMessageImpl)payload;
         SOAPEnvelope soapEnvelope = resMessage.getSOAPPart().getEnvelope();
         if (soapEnvelope == null)
         {
            return;
         }

         // Verify the SOAP version
         verifySOAPVersion(opMetaData, soapEnvelope);

         // Get the SOAP message context that is associated with the current thread
         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         if (msgContext == null)
            throw new WSException("MessageContext not available");

         // Disable MTOM for rpc/encoded
         if (opMetaData.isRPCEncoded())
            msgContext.put(StubExt.PROPERTY_MTOM_ENABLED, Boolean.FALSE);

         SOAPHeader soapHeader = soapEnvelope.getHeader();
         SOAPBodyImpl soapBody = (SOAPBodyImpl)soapEnvelope.getBody();
         SOAPBodyElement soapBodyElement = soapBody.getBodyElement();

         // Translate the SOAPFault to an exception and throw it
         if (soapBodyElement instanceof SOAPFaultImpl)
            throwFaultException((SOAPFaultImpl)soapBodyElement);

         // Extract unbound OUT headers
         if (unboundHeaders != null && soapHeader != null)
         {
            Map<QName, UnboundHeader> outHeaders = new HashMap<QName, UnboundHeader>();
            Iterator itHeaderElements = soapHeader.getChildElements();
            while (itHeaderElements.hasNext())
            {
               SOAPContentElement soapHeaderElement = (SOAPHeaderElementImpl)itHeaderElements.next();
               Name elName = soapHeaderElement.getElementName();
               QName xmlName = new QName(elName.getURI(), elName.getLocalName());

               UnboundHeader unboundHeader = (UnboundHeader)unboundHeaders.get(xmlName);
               if (unboundHeader != null)
               {
                  soapHeaderElement.setParamMetaData(unboundHeader.toParameterMetaData(opMetaData));

                  // Do the unmarshalling
                  Object value = soapHeaderElement.getObjectValue();
                  unboundHeader.setHeaderValue(value);
                  outHeaders.put(xmlName, unboundHeader);
               }
            }
            unboundHeaders.clear();
            unboundHeaders.putAll(outHeaders);
         }

         Style style = opMetaData.getStyle();
         SOAPElement soapElement = soapBody;
         if (style == Style.RPC)
         {
            if (soapBodyElement == null)
               throw new WSException("Cannot unbind response message with empty soap body");
            soapElement = soapBodyElement;
         }

         ParameterMetaData retMetaData = opMetaData.getReturnParameter();
         if (retMetaData != null)
         {
            if (retMetaData.isSwA())
            {
               AttachmentPart part = getAttachmentFromMessage(retMetaData, resMessage);
               epInv.setReturnValue(part);

               // Add the attachment to the standard property
               if (part.getDataHandler() != null && msgContext instanceof MessageContextJAXWS)
               {
                  DataHandler dataHandler = part.getDataHandler();
                  Map<String, DataHandler> attachments = (Map<String, DataHandler>)msgContext.get(MessageContext.INBOUND_MESSAGE_ATTACHMENTS);
                  attachments.put(part.getContentId(), dataHandler);
               }
            }
            else
            {
               SOAPContentElement value = getParameterFromMessage(retMetaData, soapElement, false);
               epInv.setReturnValue(value);
            }
         }

         for (ParameterMetaData paramMetaData : opMetaData.getOutputParameters())
         {
            QName xmlName = paramMetaData.getXmlName();
            if (paramMetaData.isSwA())
            {
               AttachmentPart part = getAttachmentFromMessage(paramMetaData, resMessage);
               epInv.setResponseParamValue(xmlName, part);

               // Add the attachment to the standard property
               if (part.getDataHandler() != null && msgContext instanceof MessageContextJAXWS)
               {
                  DataHandler dataHandler = part.getDataHandler();
                  Map<String, DataHandler> attachments = (Map<String, DataHandler>)msgContext.get(MessageContext.INBOUND_MESSAGE_ATTACHMENTS);
                  attachments.put(part.getContentId(), dataHandler);
               }
            }
            else
            {
               SOAPElement element = paramMetaData.isInHeader() ? soapHeader : soapElement;
               SOAPContentElement value = getParameterFromMessage(paramMetaData, element, false);
               epInv.setResponseParamValue(xmlName, value);
            }
         }
      }
      catch (Exception e)
      {
         handleException(e);
      }
   }

   public MessageAbstraction bindFaultMessage(Exception ex)
   {
      SOAPMessageImpl faultMessage = (SOAPMessageImpl)createFaultMessageFromException(ex);
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

   protected abstract SOAPMessage createFaultMessageFromException(Exception ex);

   private void verifySOAPVersion(OperationMetaData opMetaData, SOAPEnvelope soapEnvelope)
   {
      String envNS = soapEnvelope.getNamespaceURI();
      String bindingId = opMetaData.getEndpointMetaData().getBindingId();
      if (CommonSOAPBinding.SOAP11HTTP_BINDING.equals(bindingId) && Constants.NS_SOAP11_ENV.equals(envNS) == false)
         log.warn("Expected SOAP-1.1 envelope, but got: " + envNS);

      if (CommonSOAPBinding.SOAP12HTTP_BINDING.equals(bindingId) && Constants.NS_SOAP12_ENV.equals(envNS) == false)
         log.warn("Expected SOAP-1.2 envelope, but got: " + envNS);
   }

   private AttachmentPart createAttachmentPart(ParameterMetaData paramMetaData, Object value, CIDGenerator cidGenerator) throws SOAPException, BindingException
   {
      String partName = paramMetaData.getXmlName().getLocalPart();
      Set mimeTypes = paramMetaData.getMimeTypes();

      AttachmentPart part = new AttachmentPartImpl();
      if (value instanceof DataHandler)
      {
         DataHandler handler = (DataHandler)value;
         String mimeType = MimeUtils.getBaseMimeType(handler.getContentType());

         // JAX-WS 2.0, 2.6.3.1 MIME Content
         // Conformance (MIME type mismatch): On receipt of a message where the MIME type of a part does not
         // match that described in the WSDL an implementation SHOULD throw a WebServiceException.
         if (mimeTypes != null && !MimeUtils.isMemberOf(mimeType, mimeTypes))
            log.warn("Mime type " + mimeType + " not allowed for parameter " + partName + " allowed types are " + mimeTypes);

         part.setDataHandler((DataHandler)value);
      }
      else
      {
         String mimeType = null;
         if (mimeTypes != null && mimeTypes.size() > 0)
         {
            mimeType = (String)mimeTypes.iterator().next();
         }
         else
         {
            mimeType = MimeUtils.resolveMimeType(value);
         }

         if (mimeType == null)
            throw new BindingException("Could not determine mime type for attachment parameter: " + partName);

         part.setContent(value, mimeType);
      }

      if (paramMetaData.isSwA())
      {
         String swaCID = '<' + partName + "=" + cidGenerator.generateFromCount() + '>';
         part.setContentId(swaCID);
      }
      if (paramMetaData.isXOP())
      {
         String xopCID = '<' + cidGenerator.generateFromName(partName) + '>';
         part.setContentId(xopCID);
      }

      return part;
   }

   private AttachmentPart getAttachmentFromMessage(ParameterMetaData paramMetaData, SOAPMessage message) throws SOAPException, BindingException
   {
      QName xmlName = paramMetaData.getXmlName();

      AttachmentPart part = ((SOAPMessageImpl)message).getAttachmentByPartName(xmlName.getLocalPart());
      if (part == null)
         throw new BindingException("Could not locate attachment for parameter: " + paramMetaData.getXmlName());

      return part;
   }

   /** Marshall the given parameter and add it to the SOAPMessage */
   private SOAPContentElement addParameterToMessage(ParameterMetaData paramMetaData, Object value, SOAPElement soapElement) throws SOAPException, BindingException
   {
      QName xmlName = paramMetaData.getXmlName();
      Class javaType = paramMetaData.getJavaType();

      if (value != null && paramMetaData.isXOP() == false)
      {
         Class valueType = value.getClass();
         if (JavaUtils.isAssignableFrom(javaType, valueType) == false)
            throw new BindingException("javaType " + javaType.getName() + " is not assignable from: " + valueType.getName());
      }

      // Make sure we have a prefix on qualified names
      if (xmlName.getNamespaceURI().length() > 0)
      {
         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         NamespaceRegistry namespaceRegistry = msgContext.getNamespaceRegistry();
         xmlName = namespaceRegistry.registerQName(xmlName);
      }

      Name soapName = new NameImpl(xmlName.getLocalPart(), xmlName.getPrefix(), xmlName.getNamespaceURI());

      SOAPContentElement contentElement;
      if (soapElement instanceof SOAPHeader)
      {
         contentElement = new SOAPHeaderElementImpl(soapName);
         soapElement.addChildElement(contentElement);
      }
      else
      {
         Style style = paramMetaData.getOperationMetaData().getStyle();
         if (style == Style.DOCUMENT)
         {
            contentElement = new SOAPBodyElementDoc(soapName);
            soapElement.addChildElement(contentElement);
         }
         else
         {
            contentElement = new SOAPContentElement(soapName);
            soapElement.addChildElement(contentElement);
         }
      }

      contentElement.setParamMetaData(paramMetaData);

      if (paramMetaData.isSOAPArrayParam())
      {
         log.trace("Add parameter as SOAP encoded Array");
         contentElement.addNamespaceDeclaration(Constants.PREFIX_SOAP11_ENC, Constants.URI_SOAP11_ENC);
      }

      // When a potential xop parameter is detected and MTOM is enabled
      // we flag the SOAP message as a XOP package
      if (paramMetaData.isXOP() && XOPContext.isMTOMEnabled())
      {
         log.trace("Add parameter as XOP");
         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgContext.getSOAPMessage();
         soapMessage.setXOPMessage(true);
      }
      else if (paramMetaData.isSwaRef())
      {
         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgContext.getSOAPMessage();
         soapMessage.setSWARefMessage(true);
      }

      contentElement.setObjectValue(value);

      return contentElement;
   }

   /** Unmarshall a message element and add it to the parameter list */
   private SOAPContentElement getParameterFromMessage(ParameterMetaData paramMetaData, SOAPElement soapElement, boolean optional) throws BindingException
   {
      Name xmlName = new NameImpl(paramMetaData.getXmlName());

      SOAPContentElement soapContentElement = null;
      Iterator childElements = soapElement.getChildElements();
      while (childElements.hasNext())
      {
         Object childNode = childElements.next();
         if (childNode instanceof SOAPElement)
         {
            SOAPElementImpl childElement = (SOAPElementImpl)childNode;
            // If this message was manipulated by a handler the child may not be a content element
            if (!(childElement instanceof SOAPContentElement))
               childElement = (SOAPContentElement)soapElement.replaceChild(new SOAPContentElement(childElement), childElement);

            // The parameters are expected to be lazy
            SOAPContentElement aux = (SOAPContentElement)childElement;
            Name elName = aux.getElementName();

            if (xmlName.getLocalName().equals("") || xmlName.equals(elName))
            {
               soapContentElement = aux;
               soapContentElement.setParamMetaData(paramMetaData);
               break;
            }

            if (SOAP_ARRAY_NAME.equals(elName))
            {
               CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
               msgContext.put(CommonMessageContext.ALLOW_EXPAND_TO_DOM, Boolean.TRUE);
               try
               {
                  QName compXMLName = paramMetaData.getXmlName();
                  Element compElement = DOMUtils.getFirstChildElement(aux);
                  // NPE when the soap encoded array size is 0 on the return path
                  // http://jira.jboss.org/jira/browse/JBWS-1285
                  if (compElement == null || compElement.getNodeName().equals(compXMLName.getLocalPart()))
                  {
                     soapContentElement = aux;
                     soapContentElement.setParamMetaData(paramMetaData);
                     break;
                  }
               }
               finally
               {
                  msgContext.remove(CommonMessageContext.ALLOW_EXPAND_TO_DOM);
               }
            }
         }
      }

      // If matching by name fails, try to match by xmlType
      // This maybe necessary when wsa:Action dispatches to the operation
      if (soapContentElement == null)
      {
         childElements = soapElement.getChildElements();
         OperationMetaData opMetaData = paramMetaData.getOperationMetaData();
         TypesMetaData typesMetaData = opMetaData.getEndpointMetaData().getServiceMetaData().getTypesMetaData();
         if (childElements.hasNext() && opMetaData.getStyle() == Style.DOCUMENT)
         {
            SOAPElementImpl childElement = (SOAPElementImpl)childElements.next();

            // The parameters are expected to be lazy
            SOAPContentElement aux = (SOAPContentElement)childElement;
            Name elName = aux.getElementName();
            QName elType = null;

            XSElementDeclaration xsdElement = typesMetaData.getSchemaModel().getElementDeclaration(elName.getLocalName(), elName.getURI());
            if (xsdElement != null && xsdElement.getTypeDefinition() != null)
            {
               XSTypeDefinition xsdType = xsdElement.getTypeDefinition();
               elType = new QName(xsdType.getNamespace(), xsdType.getName());
            }

            if (paramMetaData.getXmlType().equals(elType))
            {
               soapContentElement = aux;
               soapContentElement.setParamMetaData(paramMetaData);
            }
         }
      }

      if (soapContentElement == null && optional == false)
         throw new WSException("Cannot find child element: " + xmlName);

      // When a potential XOP parameter is detected and
      // the incomming request is actuall XOP encoded we flag
      // the SOAP message a XOP packaged.
      if (paramMetaData.isXOP() && XOPContext.isXOPEncodedRequest())
      {
         SOAPMessageImpl soapMessage = (SOAPMessageImpl)MessageContextAssociation.peekMessageContext().getSOAPMessage();
         soapMessage.setXOPMessage(true);
      }
      else if (paramMetaData.isSwaRef())
      {
         SOAPMessageImpl soapMessage = (SOAPMessageImpl)MessageContextAssociation.peekMessageContext().getSOAPMessage();
         soapMessage.setSWARefMessage(true);
      }

      return soapContentElement;
   }

   abstract protected void throwFaultException(SOAPFaultImpl fault) throws Exception;

   abstract protected void verifyUnderstoodHeader(SOAPHeaderElement element) throws Exception;

   public void checkMustUnderstand(OperationMetaData opMetaData) throws Exception
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext == null)
         throw new WSException("MessageContext not available");

      SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgContext.getSOAPMessage();
      SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
      if (soapEnvelope == null || soapEnvelope.getHeader() == null)
         return;

      Iterator it = soapEnvelope.getHeader().examineAllHeaderElements();
      while (it.hasNext())
      {
         SOAPHeaderElement soapHeaderElement = (SOAPHeaderElement)it.next();
         Name name = soapHeaderElement.getElementName();
         QName xmlName = new QName(name.getURI(), name.getLocalName());

         ParameterMetaData paramMetaData = (opMetaData != null ? opMetaData.getParameter(xmlName) : null);
         boolean isBoundHeader = (paramMetaData != null && paramMetaData.isInHeader());

         if (!isBoundHeader && soapHeaderElement.getMustUnderstand())
            verifyUnderstoodHeader(soapHeaderElement);
      }
   }

   public void setHeaderSource(HeaderSource source)
   {
      headerSource = source;
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
