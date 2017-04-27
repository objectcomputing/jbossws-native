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
package org.jboss.ws.metadata.builder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.ObjectName;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Port;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.xml.namespace.QName;
import javax.xml.ws.addressing.AddressingProperties;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.soap.Use;
import org.jboss.ws.extensions.addressing.AddressingPropertiesImpl;
import org.jboss.ws.extensions.addressing.metadata.AddressingOpMetaExt;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.EventingUtils;
import org.jboss.ws.extensions.eventing.metadata.EventingEpMetaExt;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLProperty;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.wsf.common.ObjectNameFactory;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.management.ServerConfig;
import org.jboss.wsf.spi.management.ServerConfigFactory;
import org.jboss.wsf.spi.metadata.j2ee.EJBArchiveMetaData;
import org.jboss.wsf.spi.metadata.j2ee.EJBMetaData;
import org.jboss.wsf.spi.metadata.j2ee.MDBMetaData;
import org.jboss.wsf.spi.metadata.j2ee.JSEArchiveMetaData;
import org.jboss.wsf.spi.metadata.j2ee.JSESecurityMetaData;
import org.jboss.wsf.spi.metadata.j2ee.JSESecurityMetaData.JSEResourceCollection;

/** An abstract meta data builder.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 19-May-2005
 */
public abstract class MetaDataBuilder
{
   // provide logging
   private final static Logger log = Logger.getLogger(MetaDataBuilder.class);

   /** Inititialize the endpoint binding */
   protected void initEndpointBinding(WSDLEndpoint wsdlEndpoint, EndpointMetaData epMetaData)
   {
      WSDLDefinitions wsdlDefinitions = wsdlEndpoint.getWsdlService().getWsdlDefinitions();
      WSDLInterface wsdlInterface = wsdlEndpoint.getInterface();
      WSDLBinding wsdlBinding = wsdlDefinitions.getBindingByInterfaceName(wsdlInterface.getName());
      initEndpointBinding(wsdlBinding, epMetaData);
   }

   protected void initEndpointBinding(WSDLBinding wsdlBinding, EndpointMetaData epMetaData)
   {
      String bindingType = wsdlBinding.getType();
      if (Constants.NS_SOAP11.equals(bindingType))
         epMetaData.setBindingId(Constants.SOAP11HTTP_BINDING);
      else if (Constants.NS_SOAP12.equals(bindingType))
         epMetaData.setBindingId(Constants.SOAP12HTTP_BINDING);
   }

   /** Initialize the endpoint encoding style from the binding operations
    */
   protected void initEndpointEncodingStyle(EndpointMetaData epMetaData)
   {
      WSDLDefinitions wsdlDefinitions = epMetaData.getServiceMetaData().getWsdlDefinitions();
      for (WSDLService wsdlService : wsdlDefinitions.getServices())
      {
         for (WSDLEndpoint wsdlEndpoint : wsdlService.getEndpoints())
         {
            if (epMetaData.getPortName().equals(wsdlEndpoint.getName()))
            {
               QName bindQName = wsdlEndpoint.getBinding();
               WSDLBinding wsdlBinding = wsdlDefinitions.getBinding(bindQName);
               if (wsdlBinding == null)
                  throw new WSException("Cannot obtain binding: " + bindQName);

               for (WSDLBindingOperation wsdlBindingOperation : wsdlBinding.getOperations())
               {
                  String encStyle = wsdlBindingOperation.getEncodingStyle();
                  epMetaData.setEncodingStyle(Use.valueOf(encStyle));
               }
            }
         }
      }
   }

   protected void initEndpointAddress(Deployment dep, ServerEndpointMetaData sepMetaData)
   {
      String contextRoot = dep.getService().getContextRoot();
      String urlPattern = null;

      // Get the URL pattern from the endpoint
      String linkName = sepMetaData.getLinkName();
      if (linkName != null)
      {
         Endpoint endpoint = dep.getService().getEndpointByName(linkName);
         if (endpoint != null)
            urlPattern = endpoint.getURLPattern();
      }

      // If not, derive the context root from the deployment
      if (contextRoot == null)
      {
         String simpleName = dep.getSimpleName();
         contextRoot = simpleName.substring(0, simpleName.indexOf('.'));
         if (dep instanceof ArchiveDeployment)
         {
            if (((ArchiveDeployment)dep).getParent() != null)
            {
               simpleName = ((ArchiveDeployment)dep).getParent().getSimpleName();
               simpleName = simpleName.substring(0, simpleName.indexOf('.'));
               contextRoot = simpleName + "-" + contextRoot;
            }
         }
      }

      // Default to "/*" 
      if (urlPattern == null)
         urlPattern = "/*";

      if (contextRoot.startsWith("/") == false)
         contextRoot = "/" + contextRoot;
      if (urlPattern.startsWith("/") == false)
         urlPattern = "/" + urlPattern;

      sepMetaData.setContextRoot(contextRoot);
      sepMetaData.setURLPattern(urlPattern);

      String servicePath = contextRoot + urlPattern;
      sepMetaData.setEndpointAddress(getServiceEndpointAddress(null, servicePath));
   }

   public static ObjectName createServiceEndpointID(Deployment dep, ServerEndpointMetaData sepMetaData)
   {
      String linkName = sepMetaData.getLinkName();
      String context = sepMetaData.getContextRoot();
      if (context.startsWith("/"))
         context = context.substring(1);

      StringBuilder idstr = new StringBuilder(ServerEndpointMetaData.SEPID_DOMAIN + ":");
      idstr.append(ServerEndpointMetaData.SEPID_PROPERTY_CONTEXT + "=" + context);
      idstr.append("," + ServerEndpointMetaData.SEPID_PROPERTY_ENDPOINT + "=" + linkName);

      // Add JMS destination JNDI name for MDB endpoints
      EJBArchiveMetaData apMetaData = dep.getAttachment(EJBArchiveMetaData.class);
      if (apMetaData != null)
      {
         String ejbName = sepMetaData.getLinkName();
         if (ejbName == null)
            throw new WSException("Cannot obtain ejb-link from port component");

         EJBMetaData beanMetaData = (EJBMetaData)apMetaData.getBeanByEjbName(ejbName);
         if (beanMetaData == null)
            throw new WSException("Cannot obtain ejb meta data for: " + ejbName);

         if (beanMetaData instanceof MDBMetaData)
         {
            MDBMetaData mdMetaData = (MDBMetaData)beanMetaData;
            String jndiName = mdMetaData.getDestinationJndiName();
            idstr.append(",jms=" + jndiName);
         }
      }

      return ObjectNameFactory.create(idstr.toString());
   }

   /** Get the web service address for a given path
    */
   public static String getServiceEndpointAddress(String uriScheme, String servicePath)
   {
      if (servicePath == null || servicePath.length() == 0)
         throw new WSException("Service path cannot be null");

      if (servicePath.endsWith("/*"))
         servicePath = servicePath.substring(0, servicePath.length() - 2);

      if (uriScheme == null)
         uriScheme = "http";

      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      ServerConfig config = spiProvider.getSPI(ServerConfigFactory.class).getServerConfig();

      String host = config.getWebServiceHost();
      String port = "";
      if ("https".equals(uriScheme))
      {
         int portNo = config.getWebServiceSecurePort();
         if (portNo != 443)
         {
            port = ":" + portNo;
         }

      }
      else
      {
         int portNo = config.getWebServicePort();
         if (portNo != 80)
         {
            port = ":" + portNo;
         }
      }

      String urlStr = uriScheme + "://" + host + port + servicePath;
      try
      {
         return new URL(urlStr).toExternalForm();
      }
      catch (MalformedURLException e)
      {
         throw new WSException("Malformed URL: " + urlStr);
      }
   }

   /**
    * Read the transport guarantee from web.xml
    */
   protected void initTransportGuaranteeJSE(Deployment dep, ServerEndpointMetaData sepMetaData, String servletLink) throws IOException
   {
      String transportGuarantee = null;
      JSEArchiveMetaData webMetaData = dep.getAttachment(JSEArchiveMetaData.class);
      if (webMetaData != null)
      {
         Map<String, String> servletMappings = webMetaData.getServletMappings();
         String urlPattern = servletMappings.get(servletLink);

         if (urlPattern == null)
            throw new WSException("Cannot find <url-pattern> for servlet-name: " + servletLink);

         List<JSESecurityMetaData> securityList = webMetaData.getSecurityMetaData();
         for (JSESecurityMetaData currentSecurity : securityList)
         {
            if (currentSecurity.getTransportGuarantee() != null && currentSecurity.getTransportGuarantee().length() > 0)
            {
               for (JSEResourceCollection currentCollection : currentSecurity.getWebResources())
               {
                  for (String currentUrlPattern : currentCollection.getUrlPatterns())
                  {
                     if (urlPattern.equals(currentUrlPattern) || "/*".equals(currentUrlPattern))
                     {
                        transportGuarantee = currentSecurity.getTransportGuarantee();
                     }
                  }
               }
            }
         }
      }
      sepMetaData.setTransportGuarantee(transportGuarantee);
   }

   /** Replace the address locations for a given port component.
    */
   public static void replaceAddressLocation(ServerEndpointMetaData sepMetaData)
   {
      WSDLDefinitions wsdlDefinitions = sepMetaData.getServiceMetaData().getWsdlDefinitions();
      QName portName = sepMetaData.getPortName();

      boolean endpointFound = false;
      for (WSDLService wsdlService : wsdlDefinitions.getServices())
      {
         for (WSDLEndpoint wsdlEndpoint : wsdlService.getEndpoints())
         {
            QName wsdlPortName = wsdlEndpoint.getName();
            if (wsdlPortName.equals(portName))
            {
               endpointFound = true;

               String orgAddress = wsdlEndpoint.getAddress();
               String uriScheme = getUriScheme(orgAddress);

               String transportGuarantee = sepMetaData.getTransportGuarantee();
               if ("CONFIDENTIAL".equals(transportGuarantee))
                  uriScheme = "https";

               String servicePath = sepMetaData.getContextRoot() + sepMetaData.getURLPattern();
               String serviceEndpointURL = getServiceEndpointAddress(uriScheme, servicePath);

               SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
               ServerConfig config = spiProvider.getSPI(ServerConfigFactory.class).getServerConfig();
               boolean alwaysModify = config.isModifySOAPAddress();

               if (alwaysModify || uriScheme == null || orgAddress.indexOf("REPLACE_WITH_ACTUAL_URL") >= 0)
               {
                  log.debug("Replace service endpoint address '" + orgAddress + "' with '" + serviceEndpointURL + "'");
                  wsdlEndpoint.setAddress(serviceEndpointURL);
                  sepMetaData.setEndpointAddress(serviceEndpointURL);

                  // modify the wsdl-1.1 definition
                  if (wsdlDefinitions.getWsdlOneOneDefinition() != null)
                     replaceWSDL11PortAddress(wsdlDefinitions, portName, serviceEndpointURL);
               }
               else
               {
                  log.debug("Don't replace service endpoint address '" + orgAddress + "'");
                  try
                  {
                     sepMetaData.setEndpointAddress(new URL(orgAddress).toExternalForm());
                  }
                  catch (MalformedURLException e)
                  {
                     throw new WSException("Malformed URL: " + orgAddress);
                  }
               }
            }
         }
      }

      if (endpointFound == false)
         throw new WSException("Cannot find port in wsdl: " + portName);
   }

   private static void replaceWSDL11PortAddress(WSDLDefinitions wsdlDefinitions, QName portQName, String serviceEndpointURL)
   {
      Definition wsdlOneOneDefinition = wsdlDefinitions.getWsdlOneOneDefinition();
      String tnsURI = wsdlOneOneDefinition.getTargetNamespace();

      // search for matching portElement and replace the address URI
      Port wsdlOneOnePort = modifyPortAddress(tnsURI, portQName, serviceEndpointURL, wsdlOneOneDefinition.getServices());

      // recursivly process imports if none can be found
      if (wsdlOneOnePort == null && !wsdlOneOneDefinition.getImports().isEmpty())
      {

         Iterator imports = wsdlOneOneDefinition.getImports().values().iterator();
         while (imports.hasNext())
         {
            List l = (List)imports.next();
            Iterator importsByNS = l.iterator();
            while (importsByNS.hasNext())
            {
               Import anImport = (Import)importsByNS.next();
               wsdlOneOnePort = modifyPortAddress(anImport.getNamespaceURI(), portQName, serviceEndpointURL, anImport.getDefinition().getServices());
            }
         }
      }

      // if it still doesn't exist something is wrong
      if (wsdlOneOnePort == null)
         throw new IllegalArgumentException("Cannot find port with name '" + portQName + "' in wsdl document");
   }

   private static Port modifyPortAddress(String tnsURI, QName portQName, String serviceEndpointURL, Map services)
   {
      Port wsdlOneOnePort = null;
      Iterator itServices = services.values().iterator();
      while (itServices.hasNext())
      {
         javax.wsdl.Service wsdlOneOneService = (javax.wsdl.Service)itServices.next();
         Map wsdlOneOnePorts = wsdlOneOneService.getPorts();
         Iterator itPorts = wsdlOneOnePorts.keySet().iterator();
         while (itPorts.hasNext())
         {
            String portLocalName = (String)itPorts.next();
            if (portQName.equals(new QName(tnsURI, portLocalName)))
            {
               wsdlOneOnePort = (Port)wsdlOneOnePorts.get(portLocalName);
               List extElements = wsdlOneOnePort.getExtensibilityElements();
               for (Object extElement : extElements)
               {
                  if (extElement instanceof SOAPAddress)
                  {
                     SOAPAddress address = (SOAPAddress)extElement;
                     address.setLocationURI(serviceEndpointURL);
                  }
                  else if (extElement instanceof SOAP12Address)
                  {
                     SOAP12Address address = (SOAP12Address)extElement;
                     address.setLocationURI(serviceEndpointURL);
                  }
                  else if (extElement instanceof HTTPAddress)
                  {
                     HTTPAddress address = (HTTPAddress)extElement;
                     address.setLocationURI(serviceEndpointURL);
                  }
               }
            }
         }
      }

      return wsdlOneOnePort;
   }

   private static String getUriScheme(String addrStr)
   {
      try
      {
         URI addrURI = new URI(addrStr);
         String scheme = addrURI.getScheme();
         return scheme;
      }
      catch (URISyntaxException e)
      {
         return null;
      }
   }

   protected void processEndpointMetaDataExtensions(EndpointMetaData epMetaData, WSDLDefinitions wsdlDefinitions)
   {
      for (WSDLInterface wsdlInterface : wsdlDefinitions.getInterfaces())
      {
         WSDLProperty eventSourceProp = wsdlInterface.getProperty(Constants.WSDL_PROPERTY_EVENTSOURCE);
         if (eventSourceProp != null && epMetaData instanceof ServerEndpointMetaData)
         {
            ServerEndpointMetaData sepMetaData = (ServerEndpointMetaData)epMetaData;
            String eventSourceNS = wsdlInterface.getName().getNamespaceURI() + "/" + wsdlInterface.getName().getLocalPart();

            // extract the schema model
            JBossXSModel schemaModel = WSDLUtils.getSchemaModel(wsdlDefinitions.getWsdlTypes());
            String[] notificationSchema = EventingUtils.extractNotificationSchema(schemaModel);

            // extract the root element NS
            String notificationRootElementNS = null;
            WSDLInterfaceOperation wsdlInterfaceOperation = wsdlInterface.getOperations()[0];
            if (wsdlInterfaceOperation.getOutputs().length > 0)
            {
               WSDLInterfaceOperationOutput wsdlInterfaceOperationOutput = wsdlInterfaceOperation.getOutputs()[0];
               notificationRootElementNS = wsdlInterfaceOperationOutput.getElement().getNamespaceURI();
            }
            else
            {
               // WSDL operation of an WSDL interface that is marked as an event source
               // requires to carry an output message.
               throw new WSException("Unable to resolve eventing root element NS. No operation output found at " + wsdlInterfaceOperation.getName());
            }

            EventingEpMetaExt ext = new EventingEpMetaExt(EventingConstants.NS_EVENTING);
            ext.setEventSourceNS(eventSourceNS);
            ext.setNotificationSchema(notificationSchema);
            ext.setNotificationRootElementNS(notificationRootElementNS);
            sepMetaData.addExtension(ext);
         }
      }
   }

   /** Process operation meta data extensions. */
   protected void processOpMetaExtensions(OperationMetaData opMetaData, WSDLInterfaceOperation wsdlOperation)
   {

      String tns = wsdlOperation.getName().getNamespaceURI();
      String portTypeName = wsdlOperation.getName().getLocalPart();

      AddressingProperties ADDR = new AddressingPropertiesImpl();
      AddressingOpMetaExt addrExt = new AddressingOpMetaExt(ADDR.getNamespaceURI());

      // inbound action
      WSDLProperty wsaInAction = wsdlOperation.getProperty(Constants.WSDL_PROPERTY_ACTION_IN);
      if (wsaInAction != null)
      {
         addrExt.setInboundAction(wsaInAction.getValue());
      }
      else
      {
         WSDLProperty messageName = wsdlOperation.getProperty(Constants.WSDL_PROPERTY_MESSAGE_NAME_IN);
         if (messageName != null)
         {
            addrExt.setInboundAction(tns + "/" + portTypeName + "/" + messageName.getValue());
         }
         else
         {
            addrExt.setInboundAction(tns + "/" + portTypeName + "/IN");
         }
      }

      // outbound action
      WSDLProperty wsaOutAction = wsdlOperation.getProperty(Constants.WSDL_PROPERTY_ACTION_OUT);
      if (wsaOutAction != null)
      {
         addrExt.setOutboundAction(wsaOutAction.getValue());
      }
      else
      {
         WSDLProperty messageName = wsdlOperation.getProperty(Constants.WSDL_PROPERTY_MESSAGE_NAME_OUT);
         if (messageName != null)
         {
            addrExt.setOutboundAction(tns + "/" + portTypeName + "/" + messageName.getValue());
         }
         else
         {
            addrExt.setOutboundAction(tns + "/" + portTypeName + "/OUT");
         }
      }

      opMetaData.addExtension(addrExt);
   }
}
