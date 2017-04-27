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
package org.jboss.ws.metadata.umdm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMappingRegistry;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.binding.TypeMappingImpl;
import org.jboss.ws.core.jaxrpc.TypeMappingRegistryImpl;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.SchemaBindingBuilder;
import org.jboss.ws.core.soap.Use;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMappingFactory;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;

/**
 * A Service component describes a set of endpoints.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 12-May-2005
 */
public class ServiceMetaData implements InitalizableMetaData
{
   // provide logging
   private static final Logger log = Logger.getLogger(ServiceMetaData.class);

   // The parent meta data.
   private UnifiedMetaData wsMetaData;

   // The service endpoints
   private Map<QName, EndpointMetaData> endpoints = new LinkedHashMap<QName, EndpointMetaData>();

   private QName serviceName;
   private String serviceRefName;
   private String wsdName;
   private URL wsdlLocation;
   private String wsdlFile;
   private URL mappingLocation;
   private String wsdlPublishLocation;
   
   // The optional service handlers
   private List<HandlerMetaDataJAXWS> handlers = new ArrayList<HandlerMetaDataJAXWS>();

   // The type mapping that is maintained by this service
   private TypesMetaData types;
   private TypeMappingRegistry tmRegistry = new TypeMappingRegistryImpl();
   private SchemaBinding schemaBinding;

   // Arbitrary properties given by <call-property>
   private Properties properties;
   
   // derived cached encoding style
   private Use encStyle;
   
   // The security configuration
   private WSSecurityConfiguration securityConfig;
   
   // The key to the wsdl cache
   private String wsdlCacheKey;
   
   public ServiceMetaData(UnifiedMetaData wsMetaData, QName serviceName)
   {
      this.wsMetaData = wsMetaData;
      this.serviceName = serviceName;
      this.types = new TypesMetaData(this);
   }

   public UnifiedMetaData getUnifiedMetaData()
   {
      return wsMetaData;
   }

   public void setServiceName(QName serviceName)
   {
      this.serviceName = serviceName;
   }

   public QName getServiceName()
   {
      return serviceName;
   }

   public String getServiceRefName()
   {
      return serviceRefName;
   }

   public void setServiceRefName(String serviceRefName)
   {
      this.serviceRefName = serviceRefName;
   }

   public String getWebserviceDescriptionName()
   {
      return wsdName;
   }

   public void setWebserviceDescriptionName(String wsdName)
   {
      this.wsdName = wsdName;
   }

   public String getWsdlFile()
   {
      return wsdlFile;
   }

   public void setWsdlFile(String wsdlFile)
   {
      this.wsdlFile = wsdlFile;
   }

   public URL getWsdlLocation()
   {
      return wsdlLocation;
   }

   public void setWsdlLocation(URL wsdlLocation)
   {
      this.wsdlLocation = wsdlLocation;
   }

   public String getWsdlPublishLocation()
   {
      return wsdlPublishLocation;
   }

   public void setWsdlPublishLocation(String wsdlPublishLocation)
   {
      this.wsdlPublishLocation = wsdlPublishLocation;
   }

   public Properties getProperties()
   {
      return properties;
   }

   public void setProperties(Properties properties)
   {
      this.properties = properties;
   }

   public TypesMetaData getTypesMetaData()
   {
      return types;
   }

   public void addHandler(HandlerMetaDataJAXWS handler)
   {
      handlers.add(handler);
   }
   
   public List<HandlerMetaDataJAXWS> getHandlerMetaData()
   {
      return Collections.unmodifiableList(handlers);
   }

   public List<EndpointMetaData> getEndpoints()
   {
      return new ArrayList<EndpointMetaData>(endpoints.values());
   }

   public EndpointMetaData getEndpoint(QName portName)
   {
      return endpoints.get(portName);
   }

   public EndpointMetaData removeEndpoint(QName portName)
   {
      return endpoints.remove(portName);
   }

   public EndpointMetaData getEndpointByServiceEndpointInterface(String seiName)
   {
      EndpointMetaData epMetaData = null;
      for (EndpointMetaData epmd : endpoints.values())
      {
         if (seiName.equals(epmd.getServiceEndpointInterfaceName()))
         {
            if (epMetaData != null)
            {
               // The CTS uses Service.getPort(Class) with multiple endpoints implementing the same SEI
               log.warn("Multiple possible endpoints implementing SEI: " + seiName);
            }
            epMetaData = epmd;
         }
      }
      return epMetaData;
   }

   public void addEndpoint(EndpointMetaData epMetaData)
   {
      QName portName = epMetaData.getPortName();

      // This happends when we have multiple port components in sharing the same wsdl port
      // The EndpointMetaData name is the wsdl port, so we cannot have multiple meta data for the same port.
      if (endpoints.get(portName) != null)
         throw new WSException("EndpointMetaData name must be unique: " + portName);

      endpoints.put(portName, epMetaData);
   }

   public URL getMappingLocation()
   {
      return mappingLocation;
   }

   public void setMappingLocation(URL mappingLocation)
   {
      this.mappingLocation = mappingLocation;
   }

   public JavaWsdlMapping getJavaWsdlMapping()
   {
      JavaWsdlMapping javaWsdlMapping = null;
      if (mappingLocation != null)
      {
         javaWsdlMapping = (JavaWsdlMapping)wsMetaData.getMappingDefinition(mappingLocation.toExternalForm());
         if (javaWsdlMapping == null)
         {
            try
            {
               JavaWsdlMappingFactory mappingFactory = JavaWsdlMappingFactory.newInstance();
               javaWsdlMapping = mappingFactory.parse(mappingLocation);
               wsMetaData.addMappingDefinition(mappingLocation.toExternalForm(), javaWsdlMapping);
            }
            catch (IOException e)
            {
               throw new WSException("Cannot parse jaxrpc-mapping.xml", e);
            }
         }
      }
      return javaWsdlMapping;
   }

   /**
    * Get the wsdl definition that corresponds to the wsdl-file element.
    */
   public WSDLDefinitions getWsdlDefinitions()
   {
      WSDLDefinitions wsdlDefinitions = null;
      
      URL wsdlURL = getWsdlFileOrLocation();
      if (wsdlURL != null)
      {
         // The key should not after it is assigned
         if (wsdlCacheKey == null)
            wsdlCacheKey = "#" + (wsdlLocation != null ? wsdlLocation : wsdlFile);
         
         wsdlDefinitions = (WSDLDefinitions)wsMetaData.getWsdlDefinition(wsdlCacheKey);
         if (wsdlDefinitions == null)
         {
            WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
            wsdlDefinitions = factory.parse(wsdlURL);
            wsMetaData.addWsdlDefinition(wsdlCacheKey, wsdlDefinitions);
         }
      }
      return wsdlDefinitions;
   }

   public URL getWsdlFileOrLocation()
   {
      URL wsdlURL = wsdlLocation;
      if (wsdlURL == null && wsdlFile != null)
      {
         // Try wsdlFile as URL
         try
         {
            wsdlURL = new URL(wsdlFile);
         }
         catch (MalformedURLException e)
         {
            // ignore
         }
         
         // Try wsdlFile as child from root 
         if (wsdlURL == null)
         {
            try
            {
               UnifiedVirtualFile vfsRoot = getUnifiedMetaData().getRootFile();
               wsdlURL = vfsRoot.findChild(wsdlFile).toURL();
            }
            catch (IOException ex)
            {
               throw new IllegalStateException("Cannot find wsdl: " + wsdlFile);
            }
         }
      }
      return wsdlURL;
   }

   public TypeMappingImpl getTypeMapping()
   {
      Use encStyle = getEncodingStyle();
      TypeMappingImpl typeMapping = (TypeMappingImpl)tmRegistry.getTypeMapping(encStyle.toURI());
      if (typeMapping == null)
         throw new WSException("No type mapping for encoding style: " + encStyle);

      return typeMapping;
   }

   public WSSecurityConfiguration getSecurityConfiguration()
   {
      return securityConfig;
   }

   public void setSecurityConfiguration(WSSecurityConfiguration securityConfiguration)
   {
      this.securityConfig = securityConfiguration;
   }

   public Use getEncodingStyle()
   {
      if (encStyle == null)
      {
         if (endpoints.size() > 0)
         {
            for (EndpointMetaData epMetaData : endpoints.values())
            {
               if (encStyle == null)
               {
                  encStyle = epMetaData.getEncodingStyle();
               }
               else if (encStyle.equals(epMetaData.getEncodingStyle()) == false)
               {
                  throw new WSException("Conflicting encoding styles not supported");
               }
            }
         }
         else
         {
            encStyle = Use.LITERAL;
         }
      }
      return encStyle;
   }

   public SchemaBinding getSchemaBinding()
   {
      JavaWsdlMapping wsdlMapping = getJavaWsdlMapping();
      if (schemaBinding == null && getEncodingStyle() == Use.LITERAL && wsdlMapping != null)
      {
         JBossXSModel xsModel = types.getSchemaModel();
         SchemaBindingBuilder bindingBuilder = new SchemaBindingBuilder();
         schemaBinding = bindingBuilder.buildSchemaBinding(xsModel, wsdlMapping);
      }
      return schemaBinding;
   }

   public void validate()
   {
      // Validate that there is at least one handler configured
      // if we have a security configuration
      if (securityConfig != null)
      {
         int handlerCount = 0;
         for (EndpointMetaData epMetaData : endpoints.values())
         {
            handlerCount += epMetaData.getHandlerMetaData(HandlerType.ALL).size();
         }
         if (handlerCount == 0)
            log.warn("WS-Security requires a security handler to be configured");
      }

      // Validate endpoints
      for (EndpointMetaData epMetaData : endpoints.values())
         epMetaData.validate();
   }

   /**
    * @see UnifiedMetaData#eagerInitialize()
    */
   public void eagerInitialize()
   {
      // Initialize all wsdl definitions and schema objects
      WSDLDefinitions definitions = getWsdlDefinitions();
      if (definitions != null)
      {
         WSDLTypes types = definitions.getWsdlTypes();
         if (types != null)
         {
            JBossXSModel model = WSDLUtils.getSchemaModel(types);
            if (model != null)
               model.eagerInitialize();
         }
      }

      // Initialize jaxrpc-mapping data
      getJavaWsdlMapping();

      // Initialize endpoints
      for (EndpointMetaData epMetaData : endpoints.values())
         epMetaData.eagerInitialize();

      // Initialize schema binding
      getSchemaBinding();
   }

   /** Assert that the given namespace is the WSDL's target namespace */
   public void assertTargetNamespace(String targetNS)
   {
      if (getServiceName().getNamespaceURI().equals(targetNS) == false)
         throw new WSException("Requested namespace is not WSDL target namespace: " + targetNS);
   }

   public String toString()
   {
      StringBuilder buffer = new StringBuilder("\nServiceMetaData:");
      buffer.append("\n qname=" + serviceName);
      buffer.append("\n refName=" + serviceRefName);
      buffer.append("\n wsdName=" + wsdName);
      buffer.append("\n wsdlFile=" + wsdlFile);
      buffer.append("\n wsdlLocation=" + wsdlLocation);
      buffer.append("\n jaxrpcMapping=" + mappingLocation);
      buffer.append("\n publishLocation=" + wsdlPublishLocation);
      buffer.append("\n securityConfig=" + (securityConfig != null ? "found" : null));
      buffer.append("\n properties=" + properties);
      buffer.append("\n" + types);
      buffer.append("\n");
      for (EndpointMetaData epMetaData : endpoints.values())
      {
         buffer.append(epMetaData);
      }
      return buffer.toString();
   }
}
