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
package org.jboss.ws.metadata.builder.jaxrpc;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMappingFactory;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceEndpointInterfaceMapping;
import org.jboss.ws.metadata.umdm.ClientEndpointMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.HandlerMetaDataJAXRPC;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.ws.metadata.wsse.WSSecurityOMFactory;
import org.jboss.wsf.common.ResourceLoaderAdapter;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedServiceRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * A client side meta data builder.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 19-May-2005
 */
public class JAXRPCClientMetaDataBuilder extends JAXRPCMetaDataBuilder
{
   // provide logging
   private final Logger log = Logger.getLogger(JAXRPCClientMetaDataBuilder.class);

   /** Build from WSDL and jaxrpc-mapping.xml
    */
   public ServiceMetaData buildMetaData(QName serviceQName, URL wsdlURL, URL mappingURL, URL securityURL,
         UnifiedServiceRefMetaData serviceRefMetaData, ClassLoader loader)
   {
      try
      {
         JavaWsdlMapping javaWsdlMapping = null;
         if (mappingURL != null)
         {
            JavaWsdlMappingFactory mappingFactory = JavaWsdlMappingFactory.newInstance();
            javaWsdlMapping = mappingFactory.parse(mappingURL);
         }

         WSSecurityConfiguration securityConfig = null;
         if (securityURL != null)
         {
            securityConfig = WSSecurityOMFactory.newInstance().parse(securityURL);
         }

         return buildMetaData(serviceQName, wsdlURL, javaWsdlMapping, securityConfig, serviceRefMetaData, loader);
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

   /** Build from WSDL and jaxrpc-mapping.xml
    */
   public ServiceMetaData buildMetaData(QName serviceQName, URL wsdlURL, JavaWsdlMapping javaWsdlMapping, WSSecurityConfiguration securityConfig,
         UnifiedServiceRefMetaData usrMetaData, ClassLoader loader)
   {
      if(log.isDebugEnabled()) log.debug("START buildMetaData: [service=" + serviceQName + "]");
      try
      {
         ResourceLoaderAdapter vfsRoot = new ResourceLoaderAdapter(loader);

         UnifiedMetaData wsMetaData = new UnifiedMetaData(vfsRoot);
         wsMetaData.setClassLoader(loader);

         ServiceMetaData serviceMetaData = new ServiceMetaData(wsMetaData, serviceQName);
         wsMetaData.addService(serviceMetaData);

         serviceMetaData.setWsdlLocation(wsdlURL);
         WSDLDefinitions wsdlDefinitions = serviceMetaData.getWsdlDefinitions();

         if (javaWsdlMapping != null)
         {
            URL mappingURL = new URL(Constants.NS_JBOSSWS_URI + "/dummy-mapping-file");
            if (usrMetaData != null && usrMetaData.getMappingLocation() != null)
            {
               mappingURL = usrMetaData.getMappingLocation();
            }
            wsMetaData.addMappingDefinition(mappingURL.toExternalForm(), javaWsdlMapping);
            serviceMetaData.setMappingLocation(mappingURL);
         }

         if (securityConfig != null)
         {
            serviceMetaData.setSecurityConfiguration(securityConfig);
            setupSecurity(securityConfig, wsMetaData.getRootFile());
         }

         buildMetaDataInternal(serviceMetaData, wsdlDefinitions, javaWsdlMapping, usrMetaData);

         // eagerly initialize
         wsMetaData.eagerInitialize();

         if(log.isDebugEnabled()) log.debug("END buildMetaData: " + wsMetaData);
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

   private void buildMetaDataInternal(ServiceMetaData serviceMetaData, WSDLDefinitions wsdlDefinitions, JavaWsdlMapping javaWsdlMapping,
         UnifiedServiceRefMetaData serviceRefMetaData) throws IOException
   {
      QName serviceQName = serviceMetaData.getServiceName();

      // Get the WSDL service
      WSDLService wsdlService = null;
      if (serviceQName == null)
      {
         if (wsdlDefinitions.getServices().length != 1)
            throw new IllegalArgumentException("Expected a single service element");

         wsdlService = wsdlDefinitions.getServices()[0];
         serviceMetaData.setServiceName(wsdlService.getName());
      }
      else
      {
         wsdlService = wsdlDefinitions.getService(serviceQName);
      }
      if (wsdlService == null)
         throw new IllegalArgumentException("Cannot obtain wsdl service: " + serviceQName);

      // Build type mapping meta data
      setupTypesMetaData(serviceMetaData);

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
            ClientEndpointMetaData epMetaData = new ClientEndpointMetaData(serviceMetaData, portName, interfaceQName, Type.JAXRPC);
            epMetaData.setEndpointAddress(wsdlEndpoint.getAddress());
            serviceMetaData.addEndpoint(epMetaData);

            // config-name, config-file
            if (serviceRefMetaData != null)
            {
               String configName= serviceRefMetaData.getConfigName();
               String configFile = serviceRefMetaData.getConfigFile();
               if (configName != null || configFile != null)
                  epMetaData.setConfigName(configName, configFile);
            }

            // Init the endpoint binding
            initEndpointBinding(wsdlBinding, epMetaData);

            // Init the service encoding style
            initEndpointEncodingStyle(epMetaData);

            ServiceEndpointInterfaceMapping seiMapping = null;
            if (javaWsdlMapping != null)
            {
               QName portType = wsdlEndpoint.getInterface().getName();
               seiMapping = javaWsdlMapping.getServiceEndpointInterfaceMappingByPortType(portType);
               if (seiMapping != null)
               {
                  epMetaData.setServiceEndpointInterfaceName(seiMapping.getServiceEndpointInterface());
               }
               else
               {
                  log.warn("Cannot obtain the SEI mapping for: " + portType);
               }
            }

            processEndpointMetaDataExtensions(epMetaData, wsdlDefinitions);
            setupOperationsFromWSDL(epMetaData, wsdlEndpoint, seiMapping);
            setupHandlers(serviceRefMetaData, portName, epMetaData);
         }
      }
   }

   private void setupHandlers(UnifiedServiceRefMetaData serviceRefMetaData, QName portName, EndpointMetaData epMetaData)
   {
      // Setup the endpoint handlers
      if (serviceRefMetaData != null)
      {
         for (UnifiedHandlerMetaData uhmd : serviceRefMetaData.getHandlers())
         {
            Set<String> portNames = uhmd.getPortNames();
            if (portNames.size() == 0 || portNames.contains(portName.getLocalPart()))
            {
               HandlerMetaDataJAXRPC hmd = HandlerMetaDataJAXRPC.newInstance(uhmd, HandlerType.ENDPOINT);
               epMetaData.addHandler(hmd);
            }
         }
      }
   }

   private void setupSecurity(WSSecurityConfiguration securityConfig, UnifiedVirtualFile vfsRoot)
   {
      if (securityConfig.getKeyStoreFile() != null)
      {
         try {
            UnifiedVirtualFile child = vfsRoot.findChild( securityConfig.getKeyStoreFile() );
            securityConfig.setKeyStoreURL(child.toURL());
         } catch (IOException e) {
            // ignore
         }
      }

      if (securityConfig.getTrustStoreFile() != null)
      {
         try {
            UnifiedVirtualFile child = vfsRoot.findChild( securityConfig.getTrustStoreFile() );
            securityConfig.setTrustStoreURL(child.toURL());
         } catch (IOException e) {
            // Ignore
         }
      }
   }
}
