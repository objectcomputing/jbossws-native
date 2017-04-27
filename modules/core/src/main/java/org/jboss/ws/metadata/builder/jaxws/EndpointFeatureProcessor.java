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
package org.jboss.ws.metadata.builder.jaxws;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.List;

import javax.xml.ws.RespectBinding;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.annotation.FastInfoset;
import org.jboss.ws.annotation.JsonEncoding;
import org.jboss.ws.annotation.SchemaValidation;
import org.jboss.ws.extensions.addressing.jaxws.WSAddressingServerHandler;
import org.jboss.ws.feature.FastInfosetFeature;
import org.jboss.ws.feature.JsonEncodingFeature;
import org.jboss.ws.feature.SchemaValidationFeature;
import org.jboss.ws.metadata.umdm.HandlerMetaDataJAXWS;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.wsdl.Extendable;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLExtensibilityElement;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;
import org.xml.sax.ErrorHandler;

/**
 * Process EndpointFeature annotations
 *
 * @author Thomas.Diesler@jboss.com
 * @since 12-Mar-2008
 */
public class EndpointFeatureProcessor
{
   private static final Logger log = Logger.getLogger(EndpointFeatureProcessor.class);
   
   protected void processEndpointFeatures(Deployment dep, ServerEndpointMetaData sepMetaData, Class<?> sepClass)
   {
      for (Annotation an : sepClass.getAnnotations())
      {
         WebServiceFeatureAnnotation wsfa = an.annotationType().getAnnotation(WebServiceFeatureAnnotation.class);
         if (wsfa != null)
         {
            if (an.annotationType() == Addressing.class)
            {
               Addressing anFeature = sepClass.getAnnotation(Addressing.class);
               AddressingFeature feature = new AddressingFeature(anFeature.enabled(), anFeature.required());
               sepMetaData.addFeature(feature);
            }
            else if (an.annotationType() == MTOM.class)
            {
               MTOM anFeature = sepClass.getAnnotation(MTOM.class);
               MTOMFeature feature = new MTOMFeature(anFeature.enabled(), anFeature.threshold());
               sepMetaData.addFeature(feature);
            }
            else if (an.annotationType() == SchemaValidation.class)
            {
               processSchemaValidation(dep, sepMetaData, sepClass);
            }
            else if (an.annotationType() == FastInfoset.class)
            {
               FastInfoset anFeature = sepClass.getAnnotation(FastInfoset.class);
               FastInfosetFeature feature = new FastInfosetFeature(anFeature.enabled());
               sepMetaData.addFeature(feature);
            }
            else if (an.annotationType() == JsonEncoding.class)
            {
               JsonEncoding anFeature = sepClass.getAnnotation(JsonEncoding.class);
               JsonEncodingFeature feature = new JsonEncodingFeature(anFeature.enabled());
               sepMetaData.addFeature(feature);
            }
            else if (an.annotationType() == RespectBinding.class)
            {
               RespectBinding anFeature = sepClass.getAnnotation(RespectBinding.class);
               RespectBindingFeature feature = new RespectBindingFeature(anFeature.enabled());
               sepMetaData.addFeature(feature);
            }
            else
            {
               throw new WebServiceException("Unsupported feature: " + wsfa.bean());
            }
         }
      }
   }
   
   protected void setupEndpointFeatures(ServerEndpointMetaData sepMetaData)
   {
      setupAddressingFeature(sepMetaData);
      setupMTOMFeature(sepMetaData);
      setupRespectBindingFeature(sepMetaData); //this need to be processed last
   }
   
   private static void setupAddressingFeature(ServerEndpointMetaData sepMetaData)
   {
      AddressingFeature addressingFeature = sepMetaData.getFeature(AddressingFeature.class);
      if (addressingFeature != null && addressingFeature.isEnabled())
      {
         log.debug("AddressingFeature found, installing WS-Addressing post-handler");
         HandlerMetaDataJAXWS hmd = new HandlerMetaDataJAXWS(HandlerType.POST);
         hmd.setEndpointMetaData(sepMetaData);
         hmd.setHandlerClassName(WSAddressingServerHandler.class.getName());
         hmd.setHandlerName("WSAddressing Handler");
         hmd.setProtocolBindings("##SOAP11_HTTP ##SOAP12_HTTP ##SOAP11_HTTP_MTOM ##SOAP12_HTTP_MTOM");
         sepMetaData.addHandler(hmd);
      }
   }
   
   private static void setupMTOMFeature(ServerEndpointMetaData sepMetaData)
   {
      MTOMFeature mtomFeature = sepMetaData.getFeature(MTOMFeature.class);
      if (mtomFeature != null && mtomFeature.isEnabled())
      {
         String bindingId = sepMetaData.getBindingId();
         if (SOAPBinding.SOAP11HTTP_BINDING.equals(bindingId))
         {
            log.debug("MTOMFeature found, setting binding to " + SOAPBinding.SOAP11HTTP_MTOM_BINDING);
            sepMetaData.setBindingId(SOAPBinding.SOAP11HTTP_MTOM_BINDING);
         }
         else if (SOAPBinding.SOAP12HTTP_BINDING.equals(bindingId))
         {
            log.debug("MTOMFeature found, setting binding to " + SOAPBinding.SOAP12HTTP_MTOM_BINDING);
            sepMetaData.setBindingId(SOAPBinding.SOAP12HTTP_MTOM_BINDING);
         }
      }
   }
   
   private static void setupRespectBindingFeature(ServerEndpointMetaData sepMetaData)
   {
      RespectBindingFeature respectBindingFeature = sepMetaData.getFeature(RespectBindingFeature.class);
      if (respectBindingFeature != null && respectBindingFeature.isEnabled())
      {
         log.debug("RespectBindingFeature found, looking for required not understood extensibility elements...");
         ServiceMetaData serviceMetaData = sepMetaData.getServiceMetaData();
         WSDLDefinitions wsdlDefinitions = serviceMetaData.getWsdlDefinitions();
         
         WSDLService wsdlService = wsdlDefinitions.getService(serviceMetaData.getServiceName());
         if (wsdlService != null)
         {
            WSDLEndpoint wsdlEndpoint = wsdlService.getEndpoint(sepMetaData.getPortName());
            if (wsdlEndpoint != null)
            {
               // Conformance 6.11 (javax.xml.ws.RespectBindingFeature): When the javax.xml.ws.RespectBindingFeature
               // is enabled, a JAX-WS implementation MUST inspect the wsdl:binding at runtime to determine
               // result and parameter bindings as well as any wsdl:extensions that have the required=true attribute.
               // All required wsdl:extensions MUST be supported and honored by a JAX-WS implementation unless a
               // specific wsdl:extension has be explicitly disabled via a WebServiceFeature.
               checkNotUnderstoodExtElements(wsdlEndpoint, sepMetaData);
               WSDLBinding wsdlBinding = wsdlDefinitions.getBinding(wsdlEndpoint.getBinding());
               checkNotUnderstoodExtElements(wsdlBinding, sepMetaData);
            }
            else
            {
               log.warn("Cannot find port " + sepMetaData.getPortName());
            }
         }
      }
   }
   
   private static void checkNotUnderstoodExtElements(Extendable extendable, ServerEndpointMetaData sepMetaData)
   {
      List<WSDLExtensibilityElement> notUnderstoodList = extendable.getNotUnderstoodExtElements();
      for (WSDLExtensibilityElement el : notUnderstoodList)
      {
         boolean disabledByFeature = false; //TODO [JBWS-2459]
         if (el.isRequired() && !disabledByFeature)
         {
            String s = DOMWriter.printNode(el.getElement(), true);
            throw new WebServiceException("RespectBindingFeature enabled and a required not understood element was found: " + s);
         }
      }
   }

   private void processSchemaValidation(Deployment dep, ServerEndpointMetaData sepMetaData, Class<?> sepClass)
   {
      SchemaValidation anFeature = sepClass.getAnnotation(SchemaValidation.class);
      SchemaValidationFeature feature = new SchemaValidationFeature(anFeature.enabled());

      String xsdLoc = anFeature.schemaLocation();
      if (xsdLoc.length() > 0)
      {
         if (dep instanceof ArchiveDeployment)
         {
            try
            {
               URL xsdURL = ((ArchiveDeployment)dep).getMetaDataFileURL(xsdLoc);
               xsdLoc = xsdURL.toExternalForm();
            }
            catch (IOException ex)
            {
               throw new WSException("Cannot load schema: " + xsdLoc, ex);
            }
         }
         feature.setSchemaLocation(xsdLoc);
      }

      Class handlerClass = anFeature.errorHandler();
      if (handlerClass != null)
      {
         try
         {
            ErrorHandler errorHandler = (ErrorHandler)handlerClass.newInstance();
            feature.setErrorHandler(errorHandler);
         }
         catch (Exception ex)
         {
            throw new WSException("Cannot instanciate error handler: " + handlerClass, ex);
         }
      }
      sepMetaData.addFeature(feature);
   }
}
