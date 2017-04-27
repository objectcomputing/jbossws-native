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
import java.net.URL;
import java.util.*;

import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxws.client.ServiceObjectFactoryJAXWS;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.extensions.policy.metadata.PolicyMetaDataBuilder;
import org.jboss.ws.extensions.wsrm.common.RMHelper;
import org.jboss.ws.metadata.umdm.ClientEndpointMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.wsf.common.ResourceLoaderAdapter;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedCallPropertyMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedPortComponentRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedServiceRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedStubPropertyMetaData;
import org.jboss.ws.annotation.EndpointConfig;

/**
 * A client side meta data builder.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 19-May-2005
 */
public class JAXWSClientMetaDataBuilder extends JAXWSMetaDataBuilder
{

   public ServiceMetaData buildMetaData(QName serviceName, URL wsdlURL, UnifiedVirtualFile vfsRoot)
   {
      if (wsdlURL == null)
         throw new IllegalArgumentException("Invalid wsdlURL: " + wsdlURL);

      log.debug("START buildMetaData: [service=" + serviceName + "]");
      try
      {
         UnifiedMetaData wsMetaData = new UnifiedMetaData(vfsRoot);

         ServiceMetaData serviceMetaData = new ServiceMetaData(wsMetaData, serviceName);
         wsMetaData.addService(serviceMetaData);

         serviceMetaData.setWsdlLocation(wsdlURL);
         WSDLDefinitions wsdlDefinitions = serviceMetaData.getWsdlDefinitions();

         buildMetaDataInternal(serviceMetaData, wsdlDefinitions);

         //Setup policies for each endpoint
         for (EndpointMetaData epMetaData : serviceMetaData.getEndpoints())
         {
            PolicyMetaDataBuilder policyBuilder = PolicyMetaDataBuilder.getClientSidePolicyMetaDataBuilder();
            policyBuilder.processPolicyExtensions(epMetaData, wsdlDefinitions);
         }
         
         // Read the WSDL and initialize the schema model
         // This should only be needed for debuging purposes of the UMDM
         JBossXSModel schemaModel = WSDLUtils.getSchemaModel(wsdlDefinitions.getWsdlTypes());
         serviceMetaData.getTypesMetaData().setSchemaModel(schemaModel);

         log.debug("END buildMetaData: " + wsMetaData);
         return serviceMetaData;
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         throw new WSException("Cannot build meta data: " + ex.getMessage(), ex);
      }
   }

   /** Build from WSDL and service name
    */
   public ServiceMetaData buildMetaData(QName serviceName, URL wsdlURL)
   {
      return buildMetaData(serviceName, wsdlURL, new ResourceLoaderAdapter());
   }

   private void buildMetaDataInternal(ServiceMetaData serviceMetaData, WSDLDefinitions wsdlDefinitions) throws IOException
   {
      QName serviceName = serviceMetaData.getServiceName();

      // Get the WSDL service
      WSDLService wsdlService = null;
      if (serviceName == null)
      {
         if (wsdlDefinitions.getServices().length != 1)
            throw new IllegalArgumentException("Expected a single service element");

         wsdlService = wsdlDefinitions.getServices()[0];
         serviceMetaData.setServiceName(wsdlService.getName());
      }
      else
      {
         wsdlService = wsdlDefinitions.getService(serviceName);
      }
      if (wsdlService == null)
      {
         List<QName> serviceNames = new ArrayList<QName>();
         for (WSDLService wsdls : wsdlDefinitions.getServices())
            serviceNames.add(wsdls.getName());
         
         throw new IllegalArgumentException("Cannot obtain wsdl service: " + serviceName + " we have " + serviceNames);
      }

      // Build endpoint meta data

      for (WSDLEndpoint wsdlEndpoint : wsdlService.getEndpoints())
      {
         QName bindingName = wsdlEndpoint.getBinding();
         WSDLBinding wsdlBinding = wsdlEndpoint.getWsdlService().getWsdlDefinitions().getBinding(bindingName);
         String bindingType = wsdlBinding.getType();
         if (Constants.NS_SOAP11.equals(bindingType) || Constants.NS_SOAP12.equals(bindingType))
         {
            QName portName = wsdlEndpoint.getName();
            QName interfaceQName = wsdlEndpoint.getInterface().getName();
            ClientEndpointMetaData epMetaData = new ClientEndpointMetaData(serviceMetaData, portName, interfaceQName, Type.JAXWS);
            epMetaData.setEndpointAddress(wsdlEndpoint.getAddress());
            serviceMetaData.addEndpoint(epMetaData);

            // Init the endpoint binding
            initEndpointBinding(wsdlBinding, epMetaData);

            // Init the service encoding style
            initEndpointEncodingStyle(epMetaData);

            setupOperationsFromWSDL(epMetaData, wsdlEndpoint);

            // service-ref contributions
            bufferServiceRefContributions(epMetaData);
         }
      }
   }

   /**
    * Buffer portComponent information that it can be reused
    * when rebuild is called (actually getPort(...))
    * @param epMetaData
    */
   private void bufferServiceRefContributions(EndpointMetaData epMetaData)
   {
      UnifiedServiceRefMetaData serviceRefMetaData = ServiceObjectFactoryJAXWS.getServiceRefAssociation();

      if(serviceRefMetaData!=null)
      {
         for(UnifiedPortComponentRefMetaData portComp : serviceRefMetaData.getPortComponentRefs())
         {
            epMetaData.getServiceRefContrib().add(portComp);
         }
      }
   }

   /**
    * ServiceRef deployment descriptor elements may override the endpoint metadata.
    * @param epMetaData
    */
   private void processServiceRefContributions(EndpointMetaData epMetaData)
   {

      Iterator<UnifiedPortComponentRefMetaData> it = epMetaData.getServiceRefContrib().iterator();

      while(it.hasNext())
      {
         UnifiedPortComponentRefMetaData portComp = it.next();

         if(epMetaData.matches(portComp))
         {
            log.debug("Processing service-ref contribution on portType: "+epMetaData.getPortTypeName());

            // process MTOM overrides
            if(portComp.getEnableMTOM())
            {
               String bindingId = epMetaData.getBindingId();
               if(bindingId.equals(Constants.SOAP11HTTP_BINDING))
                  epMetaData.setBindingId(Constants.SOAP11HTTP_MTOM_BINDING);
               else if(bindingId.equals(Constants.SOAP12HTTP_BINDING))
                  epMetaData.setBindingId(Constants.SOAP12HTTP_MTOM_BINDING);
            }

            // process stub properties
            for(UnifiedStubPropertyMetaData stubProp: portComp.getStubProperties())
            {
               epMetaData.getProperties().put(stubProp.getPropName(), stubProp.getPropValue());  
            }

            // process call properties
            for(UnifiedCallPropertyMetaData callProp: portComp.getCallProperties())
            {
               epMetaData.getProperties().put(callProp.getPropName(), callProp.getPropValue());
            }
            
         }

      }

   }

   protected void setupOperationsFromWSDL(EndpointMetaData epMetaData, WSDLEndpoint wsdlEndpoint)
   {
      WSDLDefinitions wsdlDefinitions = wsdlEndpoint.getInterface().getWsdlDefinitions();

      // For every WSDL interface operation build the OperationMetaData
      WSDLInterface wsdlInterface = wsdlEndpoint.getInterface();
      for (WSDLInterfaceOperation wsdlOperation : wsdlInterface.getOperations())
      {
         String opName = wsdlOperation.getName().toString();
         QName opQName = wsdlOperation.getName();

         // Set java method name
         String javaName = opName.substring(0, 1).toLowerCase() + opName.substring(1);

         OperationMetaData opMetaData = new OperationMetaData(epMetaData, opQName, javaName);
         epMetaData.addOperation(opMetaData);

         // Set the operation style
         String style = wsdlOperation.getStyle();
         epMetaData.setStyle((Constants.URI_STYLE_DOCUMENT.equals(style) ? Style.DOCUMENT : Style.RPC));

         // Set the operation MEP
         if (Constants.WSDL20_PATTERN_IN_ONLY.equals(wsdlOperation.getPattern()))
            opMetaData.setOneWay(true);

         // Set the operation SOAPAction
         WSDLBinding wsdlBinding = wsdlDefinitions.getBindingByInterfaceName(wsdlInterface.getName());
         WSDLBindingOperation wsdlBindingOperation = wsdlBinding.getOperationByRef(opQName);
         if (wsdlBindingOperation != null)
            opMetaData.setSOAPAction(wsdlBindingOperation.getSOAPAction());
      }
   }

   public void rebuildEndpointMetaData(EndpointMetaData epMetaData, Class<?> wsClass)
   {
      if(log.isDebugEnabled()) log.debug("START: rebuildMetaData");

      // Clear the java types, etc.
      resetMetaDataBuilder(epMetaData.getClassLoader());

      // Nuke parameterStyle
      epMetaData.setParameterStyle(null);

      // Process an optional @BindingType annotation
      if (wsClass.isAnnotationPresent(BindingType.class))
         processBindingType(epMetaData, wsClass);

      // Process @SOAPBinding
      if (wsClass.isAnnotationPresent(SOAPBinding.class))
         processSOAPBinding(epMetaData, wsClass);

      // process config, this will as well setup the handler
      processEndpointConfig(epMetaData, wsClass);
      epMetaData.initEndpointConfig();

      // Process an optional @HandlerChain annotation
      processHandlerChain(epMetaData, wsClass);

      // Process @WebMethod
      processWebMethods(epMetaData, wsClass);

      // Initialize types
      createJAXBContext(epMetaData);
      populateXmlTypes(epMetaData);

      // Set SEI name
      epMetaData.setServiceEndpointInterfaceName(wsClass.getName());

      // service-ref contributions
      processServiceRefContributions(epMetaData);
      //epMetaData.getServiceRefContrib().clear();

      // Eager initialization
      epMetaData.eagerInitialize();

      // wsrm initialization
      if (epMetaData.getConfig().getRMMetaData() != null)
      {
         RMHelper.setupRMOperations(epMetaData);
      }

      if(log.isDebugEnabled()) log.debug("END: rebuildMetaData\n" + epMetaData.getServiceMetaData());
   }

   /**
    * Process config contribution through service endpoint interfaces
    * @param epMetaData
    * @param wsClass -  the service endpoint interface
    */
   private void processEndpointConfig(EndpointMetaData epMetaData, Class<?> wsClass)
   {      
      if (wsClass.isAnnotationPresent(EndpointConfig.class))
      {
         EndpointConfig anConfig = wsClass.getAnnotation(EndpointConfig.class);
         epMetaData.setConfigName(anConfig.configName(), anConfig.configFile());
      }
   }
}
