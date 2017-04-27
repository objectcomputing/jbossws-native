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

import java.util.Set;

import javax.management.ObjectName;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceEndpointInterfaceMapping;
import org.jboss.ws.metadata.umdm.HandlerMetaDataJAXRPC;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsse.WSSecurityConfigFactory;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.ws.metadata.wsse.WSSecurityOMFactory;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.metadata.j2ee.EJBMetaData;
import org.jboss.wsf.spi.metadata.j2ee.EJBArchiveMetaData;
import org.jboss.wsf.spi.metadata.j2ee.EJBSecurityMetaData;
import org.jboss.wsf.spi.metadata.j2ee.JSEArchiveMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;
import org.jboss.wsf.spi.metadata.webservices.PortComponentMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebserviceDescriptionMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;

/**
 * A server side meta data builder that is based on webservices.xml.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 19-May-2005
 */
public class JAXRPCServerMetaDataBuilder extends JAXRPCMetaDataBuilder
{
   // provide logging
   final Logger log = Logger.getLogger(JAXRPCServerMetaDataBuilder.class);

   /**
    * Build from webservices.xml
    */
   public UnifiedMetaData buildMetaData(ArchiveDeployment dep)
   {
      log.debug("START buildMetaData: [name=" + dep.getCanonicalName() + "]");
      try
      {
         // For every webservice-description build the ServiceMetaData
         UnifiedMetaData wsMetaData = new UnifiedMetaData(dep.getRootFile());
         wsMetaData.setDeploymentName(dep.getCanonicalName());
         ClassLoader runtimeClassLoader = dep.getRuntimeClassLoader();
         if(null == runtimeClassLoader)
            throw new IllegalArgumentException("Runtime loader cannot be null");
         wsMetaData.setClassLoader(runtimeClassLoader);

         WebservicesMetaData jaxrpcMapping = dep.getAttachment(WebservicesMetaData.class);
         WebserviceDescriptionMetaData[] wsDescriptionArr = jaxrpcMapping.getWebserviceDescriptions();
         for (WebserviceDescriptionMetaData wsdMetaData : wsDescriptionArr)
         {
            ServiceMetaData serviceMetaData = new ServiceMetaData(wsMetaData, null);
            serviceMetaData.setWebserviceDescriptionName(wsdMetaData.getWebserviceDescriptionName());
            wsMetaData.addService(serviceMetaData);

            // Set wsdl file
            String wsdlFile = wsdMetaData.getWsdlFile();
            serviceMetaData.setWsdlFile(wsdlFile);

            // Unmarshall the WSDL
            WSDLDefinitions wsdlDefinitions = serviceMetaData.getWsdlDefinitions();

            // Unmarshall the jaxrpc-mapping.xml
            String mappingFile = wsdMetaData.getJaxrpcMappingFile();
            serviceMetaData.setMappingLocation(dep.getMetaDataFileURL(mappingFile));
            JavaWsdlMapping javaWsdlMapping = serviceMetaData.getJavaWsdlMapping();
            if (javaWsdlMapping == null)
               throw new WSException("jaxrpc-mapping-file not configured from webservices.xml");

            // Build type mapping meta data
            setupTypesMetaData(serviceMetaData);

            // Assign the WS-Security configuration,
            WSSecurityConfigFactory wsseConfFactory = WSSecurityConfigFactory.newInstance();
            WSSecurityConfiguration securityConfiguration = wsseConfFactory.createConfiguration(wsMetaData.getRootFile(), WSSecurityOMFactory.SERVER_RESOURCE_NAME);
            serviceMetaData.setSecurityConfiguration(securityConfiguration);

            // For every port-component build the EndpointMetaData
            PortComponentMetaData[] pcMetaDataArr = wsdMetaData.getPortComponents();
            for (PortComponentMetaData pcMetaData : pcMetaDataArr)
            {
               String linkName = pcMetaData.getEjbLink() != null ? pcMetaData.getEjbLink() : pcMetaData.getServletLink();
               QName portName = pcMetaData.getWsdlPort();

               // JBWS-722
               // <wsdl-port> in webservices.xml should be qualified
               if (portName.getNamespaceURI().length() == 0)
               {
                  String nsURI = wsdlDefinitions.getTargetNamespace();
                  portName = new QName(nsURI, portName.getLocalPart());
                  log.warn("Adding wsdl targetNamespace to: " + portName);
                  pcMetaData.setWsdlPort(portName);
               }

               WSDLEndpoint wsdlEndpoint = getWsdlEndpoint(wsdlDefinitions, portName);
               if (wsdlEndpoint == null)
                  throw new WSException("Cannot find port in wsdl: " + portName);

               // set service name
               serviceMetaData.setServiceName(wsdlEndpoint.getWsdlService().getName());
               QName interfaceQName = wsdlEndpoint.getInterface().getName();

               Endpoint ep = dep.getService().getEndpointByName(linkName);
               ServerEndpointMetaData sepMetaData = new ServerEndpointMetaData(serviceMetaData, ep, portName, interfaceQName, Type.JAXRPC);
               sepMetaData.setPortComponentName(pcMetaData.getPortComponentName());
               sepMetaData.setLinkName(linkName);
               serviceMetaData.addEndpoint(sepMetaData);

               initEndpointEncodingStyle(sepMetaData);

               initEndpointAddress(dep, sepMetaData);
               initEndpointBinding(wsdlEndpoint, sepMetaData);

               EJBArchiveMetaData apMetaData = dep.getAttachment(EJBArchiveMetaData.class);
               JSEArchiveMetaData webMetaData = dep.getAttachment(JSEArchiveMetaData.class);
               if (apMetaData != null)
               {
                  wsMetaData.setSecurityDomain(apMetaData.getSecurityDomain());

                  // Copy the wsdl publish location from jboss.xml
                  String wsdName = serviceMetaData.getWebserviceDescriptionName();
                  String wsdlPublishLocation = apMetaData.getWsdlPublishLocationByName(wsdName);
                  serviceMetaData.setWsdlPublishLocation(wsdlPublishLocation);

                  // Copy <port-component> meta data
                  EJBMetaData bmd = apMetaData.getBeanByEjbName(linkName);
                  if (bmd == null)
                     throw new WSException("Cannot obtain UnifiedBeanMetaData for: " + linkName);

                  String configName = apMetaData.getConfigName();
                  String configFile = apMetaData.getConfigFile();
                  if (configName != null || configFile != null)
                     sepMetaData.setConfigName(configName, configFile);

                  EJBSecurityMetaData smd = bmd.getSecurityMetaData();
                  if (smd != null)
                  {
                     String authMethod = smd.getAuthMethod();
                     sepMetaData.setAuthMethod(authMethod);
                     String transportGuarantee = smd.getTransportGuarantee();
                     sepMetaData.setTransportGuarantee(transportGuarantee);
                     Boolean secureWSDLAccess = smd.getSecureWSDLAccess();
                     sepMetaData.setSecureWSDLAccess(secureWSDLAccess);
                  }
               }
               else if (webMetaData != null)
               {
                  wsMetaData.setSecurityDomain(webMetaData.getSecurityDomain());

                  String targetBean = webMetaData.getServletClassNames().get(linkName);
                  sepMetaData.setServiceEndpointImplName(targetBean);

                  // Copy the wsdl publish location from jboss-web.xml
                  String wsdName = serviceMetaData.getWebserviceDescriptionName();
                  String wsdlPublishLocation = webMetaData.getWsdlPublishLocationByName(wsdName);
                  serviceMetaData.setWsdlPublishLocation(wsdlPublishLocation);

                  String configName = webMetaData.getConfigName();
                  String configFile = webMetaData.getConfigFile();
                  if (configName != null || configFile != null)
                     sepMetaData.setConfigName(configName, configFile);

                  initTransportGuaranteeJSE(dep, sepMetaData, linkName);
               }

               // init service endpoint id
               ObjectName sepID = createServiceEndpointID(dep, sepMetaData);
               sepMetaData.setServiceEndpointID(sepID);

               replaceAddressLocation(sepMetaData);

               String seiName = pcMetaData.getServiceEndpointInterface();
               sepMetaData.setServiceEndpointInterfaceName(seiName);

               ServiceEndpointInterfaceMapping seiMapping = javaWsdlMapping.getServiceEndpointInterfaceMapping(seiName);
               if (seiMapping == null)
                  log.warn("Cannot obtain SEI mapping for: " + seiName);

               // process endpoint meta extension
               processEndpointMetaDataExtensions(sepMetaData, wsdlDefinitions);

               // Setup the endpoint operations
               setupOperationsFromWSDL(sepMetaData, wsdlEndpoint, seiMapping);

               // Setup the endpoint handlers
               for (UnifiedHandlerMetaData uhmd : pcMetaData.getHandlers())
               {
                  Set<String> portNames = uhmd.getPortNames();
                  if (portNames.size() == 0 || portNames.contains(portName.getLocalPart()))
                  {
                     HandlerMetaDataJAXRPC hmd = HandlerMetaDataJAXRPC.newInstance(uhmd, HandlerType.ENDPOINT);
                     sepMetaData.addHandler(hmd);
                  }
               }
            }
         }

         log.debug("END buildMetaData: " + wsMetaData);
         return wsMetaData;
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

   private WSDLEndpoint getWsdlEndpoint(WSDLDefinitions wsdlDefinitions, QName portName)
   {
      WSDLEndpoint wsdlEndpoint = null;
      for (WSDLService wsdlService : wsdlDefinitions.getServices())
      {
         WSDLEndpoint auxEndpoint = wsdlService.getEndpoint(portName);
         if (auxEndpoint != null)
         {
            wsdlEndpoint = auxEndpoint;
            break;
         }
      }
      return wsdlEndpoint;
   }
}
