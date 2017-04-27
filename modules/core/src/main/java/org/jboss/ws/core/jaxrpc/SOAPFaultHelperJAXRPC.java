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
package org.jboss.ws.core.jaxrpc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.soap.SOAPBinding;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.CommonSOAPFaultException;
import org.jboss.ws.core.binding.AbstractDeserializerFactory;
import org.jboss.ws.core.binding.AbstractSerializerFactory;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.DeserializerSupport;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.binding.SerializerSupport;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.core.soap.NameImpl;
import org.jboss.ws.core.soap.SOAPEnvelopeImpl;
import org.jboss.ws.core.soap.SOAPFactoryImpl;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.core.soap.XMLFragment;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.FaultMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.xb.binding.NamespaceRegistry;
import org.w3c.dom.Element;

/**
 * A Helper that translates between SOAPFaultException and SOAPFault.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 03-Feb-2005
 */
public class SOAPFaultHelperJAXRPC
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPFaultHelperJAXRPC.class);

   private static List<QName> allowedFaultCodes = new ArrayList<QName>();
   static
   {
      allowedFaultCodes.add(Constants.SOAP11_FAULT_CODE_CLIENT);
      allowedFaultCodes.add(Constants.SOAP11_FAULT_CODE_SERVER);
      allowedFaultCodes.add(Constants.SOAP11_FAULT_CODE_VERSION_MISMATCH);
      allowedFaultCodes.add(Constants.SOAP11_FAULT_CODE_MUST_UNDERSTAND);
   }

   /** Hide constructor */
   private SOAPFaultHelperJAXRPC()
   {
   }

   /** Factory method for FaultException for a given SOAPFault */
   public static SOAPFaultException getSOAPFaultException(SOAPFault soapFault)
   {
      QName faultCode = ((NameImpl)soapFault.getFaultCodeAsName()).toQName();
      String faultString = soapFault.getFaultString();
      String faultActor = soapFault.getFaultActor();
      Detail detail = soapFault.getDetail();

      SOAPFaultException faultEx = new SOAPFaultException(faultCode, faultString, faultActor, detail);

      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (detail != null && msgContext != null)
      {
         SerializationContext serContext = msgContext.getSerializationContext();
         TypeMapping typeMapping = serContext.getTypeMapping();

         Iterator it = detail.getDetailEntries();
         while (it.hasNext())
         {
            DetailEntry deElement = (DetailEntry)it.next();
            Name deName = deElement.getElementName();
            QName xmlName = new QName(deName.getURI(), deName.getLocalName());

            OperationMetaData opMetaData = msgContext.getOperationMetaData();
            FaultMetaData faultMetaData = opMetaData.getFault(xmlName);
            if (faultMetaData != null)
            {
               if (log.isDebugEnabled())
                  log.debug("Deserialize fault: " + faultMetaData);
               QName xmlType = faultMetaData.getXmlType();
               Class javaType = faultMetaData.getJavaType();

               // Get the deserializer from the type mapping
               AbstractDeserializerFactory desFactory = (AbstractDeserializerFactory)typeMapping.getDeserializer(javaType, xmlType);
               if (desFactory == null)
                  throw new JAXRPCException("Cannot obtain deserializer factory for: " + xmlType);

               // Try jaxb deserialization
               try
               {
                  // http://jira.jboss.org/jira/browse/JBWS-955
                  // Cannot deserialize fault detail
                  String prefix = deName.getPrefix();
                  if (prefix.length() > 0)
                  {
                     String nsURI = deName.getURI();
                     String attrValue = deElement.getAttribute("xmlns:" + prefix);
                     if (nsURI.length() > 0 && attrValue.length() == 0)
                        deElement.addNamespaceDeclaration(prefix, nsURI);
                  }

                  Source xmlFragment = new DOMSource(deElement);
                  DeserializerSupport des = (DeserializerSupport)desFactory.getDeserializer();
                  Object userEx = des.deserialize(xmlName, xmlType, xmlFragment, serContext);
                  if (userEx == null || (userEx instanceof Exception) == false)
                     throw new WSException("Invalid deserialization result: " + userEx);

                  faultEx.initCause((Exception)userEx);
               }
               catch (RuntimeException rte)
               {
                  throw rte;
               }
               catch (Exception ex)
               {
                  log.error("Cannot deserialize fault detail", ex);
               }
            }
            else
            {
               if (log.isDebugEnabled())
                  log.debug("Cannot find fault meta data for: " + xmlName);
            }
         }
      }

      return faultEx;
   }

   /** Translate the request exception into a SOAPFault message.
    */
   public static SOAPMessageImpl exceptionToFaultMessage(Exception reqEx)
   {
      // Get or create the SOAPFaultException
      SOAPFaultException faultEx;
      if (reqEx instanceof SOAPFaultException)
      {
         faultEx = (SOAPFaultException)reqEx;
      }
      else if (reqEx instanceof CommonSOAPFaultException)
      {
         CommonSOAPFaultException soapEx = (CommonSOAPFaultException)reqEx;
         QName faultCode = soapEx.getFaultCode();
         String faultString = soapEx.getFaultString();
         Throwable cause = soapEx.getCause();
         faultEx = new SOAPFaultException(faultCode, faultString, null, null);
         faultEx.initCause(cause);
      }
      else
      {
         QName faultCode;
         if (isSOAP12() == false)
         {
            faultCode = Constants.SOAP11_FAULT_CODE_SERVER;
         }
         else
         {
            faultCode = SOAPConstants.SOAP_RECEIVER_FAULT;
         }
         String faultString = (reqEx.getMessage() != null ? reqEx.getMessage() : reqEx.toString());
         faultEx = new SOAPFaultException(faultCode, faultString, null, null);
         faultEx.initCause(reqEx);
      }

      Throwable faultCause = faultEx.getCause();
      log.error("SOAP request exception", faultCause != null ? faultCause : faultEx);

      try
      {
         SOAPMessageImpl faultMessage = toSOAPMessage(faultEx);
         return faultMessage;
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         log.error("Error creating SOAPFault message", ex);
         throw new JAXRPCException("Cannot create SOAPFault message for: " + faultEx);
      }
   }

   private static SOAPMessageImpl toSOAPMessage(SOAPFaultException faultEx) throws SOAPException
   {
      assertFaultCode(faultEx.getFaultCode());

      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      SerializationContext serContext = (msgContext != null ? msgContext.getSerializationContext() : new SerializationContextJAXRPC());
      NamespaceRegistry nsRegistry = serContext.getNamespaceRegistry();

      SOAPMessageImpl soapMessage = createSOAPMessage();

      SOAPEnvelopeImpl soapEnvelope = (SOAPEnvelopeImpl)soapMessage.getSOAPPart().getEnvelope();
      SOAPBody soapBody = soapEnvelope.getBody();

      QName faultCode = faultEx.getFaultCode();
      if (faultCode.getNamespaceURI().length() > 0)
         faultCode = nsRegistry.registerQName(faultCode);

      String faultString = getValidFaultString(faultEx);
      SOAPFault soapFault = soapBody.addFault(new NameImpl(faultCode), faultString);

      String faultActor = faultEx.getFaultActor();
      if (faultActor != null)
      {
         SOAPElement soapElement = soapFault.addChildElement("faultactor");
         soapElement.addTextNode(faultActor);
      }

      Exception faultCause = (Exception)faultEx.getCause();
      Detail detail = faultEx.getDetail();
      if (detail != null)
      {
         soapFault.addChildElement(detail);
      }
      else if (faultCause != null && (faultCause instanceof RuntimeException) == false)
      {
         Class javaType = faultCause.getClass();

         TypeMapping typeMapping = serContext.getTypeMapping();

         OperationMetaData opMetaData = msgContext.getOperationMetaData();
         if (opMetaData != null && opMetaData.getFaultMetaData(javaType) != null)
         {
            FaultMetaData faultMetaData = opMetaData.getFaultMetaData(javaType);
            QName xmlName = faultMetaData.getXmlName();
            QName xmlType = faultMetaData.getXmlType();

            xmlName = nsRegistry.registerQName(xmlName);

            // Get the serializer from the type mapping
            AbstractSerializerFactory serFactory = (AbstractSerializerFactory)typeMapping.getSerializer(javaType, xmlType);
            if (serFactory == null)
               throw new JAXRPCException("Cannot obtain serializer factory for: " + xmlType);

            try
            {
               SerializerSupport ser = (SerializerSupport)serFactory.getSerializer();
               Result result = ser.serialize(xmlName, xmlType, faultCause, serContext, null);
               XMLFragment xmlFragment = new XMLFragment(result);

               Element domElement = xmlFragment.toElement();
               SOAPFactoryImpl soapFactory = new SOAPFactoryImpl();
               SOAPElement soapElement = soapFactory.createElement(domElement);

               detail = soapFault.addDetail();
               detail.addChildElement(soapElement);
            }
            catch (BindingException e)
            {
               throw new JAXRPCException(e);
            }
         }
         else
         {
            if (log.isDebugEnabled())
               log.debug("Cannot obtain fault meta data for: " + javaType);
         }
      }

      return soapMessage;
   }

   private static SOAPMessageImpl createSOAPMessage() throws SOAPException
   {
      MessageFactoryImpl factory = new MessageFactoryImpl();

      if (isSOAP12() == true)
      {
         factory.setEnvNamespace(Constants.NS_SOAP12_ENV);
      }

      return (SOAPMessageImpl)factory.createMessage();
   }

   private static boolean isSOAP12()
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext != null)
      {
         EndpointMetaData emd = msgContext.getEndpointMetaData();
         String bindingId = emd.getBindingId();
         if (SOAPBinding.SOAP12HTTP_BINDING.equals(bindingId) || SOAPBinding.SOAP12HTTP_MTOM_BINDING.equals(bindingId))
         {
            return true;
         }
      }

      return false;
   }

   private static String getValidFaultString(SOAPFaultException faultEx)
   {
      String faultString = faultEx.getFaultString();
      if (faultString == null || faultString.length() == 0)
         faultString = "Unqualified " + faultEx.getFaultCode() + " fault";

      return faultString;
   }

   private static void assertFaultCode(QName faultCode)
   {
      if (faultCode == null)
         throw new IllegalArgumentException("faultcode cannot be null");

      // For lazy folkes like the CTS that don't bother to give
      // a namesapce URI, assume they use a standard code
      String nsURI = faultCode.getNamespaceURI();
      if ("".equals(nsURI))
      {
         log.warn("Empty namespace URI with fault code '" + faultCode + "', assuming: " + Constants.NS_SOAP11_ENV);
         faultCode = new QName(Constants.NS_SOAP11_ENV, faultCode.getLocalPart());
      }

      // WS-I allows non custom faultcodes if you use a non soap namespace
      if (Constants.NS_SOAP11_ENV.equals(nsURI) && allowedFaultCodes.contains(faultCode) == false)
         throw new IllegalArgumentException("Illegal faultcode '" + faultCode + "', allowed values are: " + allowedFaultCodes);
   }
}
