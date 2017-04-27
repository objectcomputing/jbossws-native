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

import org.jboss.logging.Logger;
import org.jboss.ws.metadata.config.ConfigurationProvider;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;
import org.jboss.wsf.spi.binding.BindingCustomization;

import javax.management.ObjectName;
import javax.xml.namespace.QName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Client side endpoint meta data.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 12-May-2005
 */
public class ServerEndpointMetaData extends EndpointMetaData
{
   protected static final Logger log = Logger.getLogger(ServerEndpointMetaData.class);

   public static final String SEPID_DOMAIN = "jboss.ws";
   public static final String SEPID_PROPERTY_CONTEXT = "context";
   public static final String SEPID_PROPERTY_ENDPOINT = "endpoint";

   // The associated SPI endpoint
   private Endpoint endpoint;

   // The REQUIRED link name
   private String linkName;
   // Legacy JSR-109 port component name
   private String portComponentName;
   // The endpoint implementation bean
   private String implName;
   // The unique service endpointID
   private ObjectName sepID;
   // The HTTP context root
   private String contextRoot;
   // The HTTP url parttern
   private String urlPattern;
   // The optional transport guarantee
   private String transportGuarantee;
   // The optional secure wsdl access 
   private boolean secureWSDLAccess;

   public ServerEndpointMetaData(ServiceMetaData service, Endpoint endpoint, QName portName, QName portTypeName, Type type)
   {
      super(service, portName, portTypeName, type);
      this.endpoint = endpoint;

      String configName = ConfigurationProvider.DEFAULT_ENDPOINT_CONFIG_NAME;
      String configFile;
      if (type == Type.JAXRPC)
         configFile = ConfigurationProvider.DEFAULT_JAXRPC_ENDPOINT_CONFIG_FILE;
      else
         configFile = ConfigurationProvider.DEFAULT_JAXWS_ENDPOINT_CONFIG_FILE;

      EndpointConfigMetaData ecmd = getEndpointConfigMetaData();
      ecmd.setConfigName(configName);
      ecmd.setConfigFile(configFile);
   }

   public Endpoint getEndpoint()
   {
      return endpoint;
   }

   public void setEndpoint(Endpoint endpoint)
   {
      this.endpoint = endpoint;
   }

   public String getLinkName()
   {
      return linkName;
   }

   public void setLinkName(String linkName)
   {
      this.linkName = linkName;
   }

   public String getPortComponentName()
   {
      return portComponentName;
   }

   public void setPortComponentName(String portComponentName)
   {
      this.portComponentName = portComponentName;
   }

   public String getServiceEndpointImplName()
   {
      return implName;
   }

   public void setServiceEndpointImplName(String endpointImpl)
   {
      this.implName = endpointImpl;
   }

   public ObjectName getServiceEndpointID()
   {
      return sepID;
   }

   public void setServiceEndpointID(ObjectName endpointID)
   {
      this.sepID = endpointID;
   }

   public String getContextRoot()
   {
      return contextRoot;
   }

   public void setContextRoot(String contextRoot)
   {
      if (contextRoot != null && !(contextRoot.startsWith("/")))
         throw new IllegalArgumentException("context root should start with '/'");

      this.contextRoot = contextRoot;
   }

   public String getURLPattern()
   {
      return urlPattern;
   }

   public void setURLPattern(String urlPattern)
   {
      if (urlPattern != null && !urlPattern.startsWith("/"))
         throw new IllegalArgumentException("URL pattern should start with '/'");

      this.urlPattern = urlPattern;
   }

   public String getTransportGuarantee()
   {
      return transportGuarantee;
   }

   public void setTransportGuarantee(String transportGuarantee)
   {
      this.transportGuarantee = transportGuarantee;
   }

   public boolean isSecureWSDLAccess()
   {
      return secureWSDLAccess;
   }

   public void setSecureWSDLAccess(boolean secureWSDLAccess)
   {
      this.secureWSDLAccess = secureWSDLAccess;
   }

   @Override
   public String getEndpointAddress()
   {
      return endpoint != null ? endpoint.getAddress() : null;
   }

   @Override
   public void setEndpointAddress(String endpointAddress)
   {
      if (endpoint == null)
         throw new IllegalStateException("Endpoint not available");

      endpoint.setAddress(endpointAddress);
   }

   /**
    * Will be set through a deployment aspect
    * @return List<BindingCustomization> of available customizations
    */
   public Collection<BindingCustomization> getBindingCustomizations()
   {
      List<BindingCustomization> list = new ArrayList<BindingCustomization>();
      for (Object att : endpoint.getAttachments())
      {
         if (att instanceof BindingCustomization)
            list.add((BindingCustomization)att);
      }
      return list;
   }

   public String toString()
   {
      StringBuilder buffer = new StringBuilder("\nServerEndpointMetaData:");
      buffer.append("\n type=").append(getType());
      buffer.append("\n qname=").append(getPortName());
      buffer.append("\n id=").append(getServiceEndpointID().getCanonicalName());
      buffer.append("\n address=").append(getEndpointAddress());
      buffer.append("\n binding=").append(getBindingId());
      buffer.append("\n linkName=").append(getLinkName());
      buffer.append("\n implName=").append(getServiceEndpointImplName());
      buffer.append("\n seiName=").append(getServiceEndpointInterfaceName());
      buffer.append("\n serviceMode=").append(getServiceMode());
      buffer.append("\n portComponentName=").append(getPortComponentName());
      buffer.append("\n contextRoot=").append(getContextRoot());
      buffer.append("\n urlPattern=").append(getURLPattern());
      buffer.append("\n configFile=").append(getConfigFile());
      buffer.append("\n configName=").append(getConfigName());
      buffer.append("\n authMethod=").append(getAuthMethod());
      buffer.append("\n transportGuarantee=").append(getTransportGuarantee());
      buffer.append("\n secureWSDLAccess=").append(isSecureWSDLAccess());
      buffer.append("\n properties=").append(getProperties());

      for (OperationMetaData opMetaData : getOperations())
      {
         buffer.append("\n").append(opMetaData);
      }
      for (HandlerMetaData hdlMetaData : getHandlerMetaData(HandlerType.ALL))
      {
         buffer.append("\n").append(hdlMetaData);
      }
      return buffer.toString();
   }
}
