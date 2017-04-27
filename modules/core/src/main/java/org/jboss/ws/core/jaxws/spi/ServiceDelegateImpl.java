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
package org.jboss.ws.core.jaxws.spi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.ReferenceParameters;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.spi.ServiceDelegate;

import org.jboss.logging.Logger;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.core.jaxws.client.ClientFeatureProcessor;
import org.jboss.ws.core.jaxws.client.ClientImpl;
import org.jboss.ws.core.jaxws.client.ClientProxy;
import org.jboss.ws.core.jaxws.client.DispatchImpl;
import org.jboss.ws.core.jaxws.client.ServiceObjectFactoryJAXWS;
import org.jboss.ws.core.jaxws.handler.HandlerResolverImpl;
import org.jboss.ws.core.jaxws.wsaddressing.EndpointReferenceUtil;
import org.jboss.ws.core.jaxws.wsaddressing.NativeEndpointReference;
import org.jboss.ws.extensions.wsrm.api.RMProvider;
import org.jboss.ws.metadata.builder.jaxws.JAXWSClientMetaDataBuilder;
import org.jboss.ws.metadata.builder.jaxws.JAXWSMetaDataBuilder;
import org.jboss.ws.metadata.umdm.ClientEndpointMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.HandlerMetaDataJAXWS;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.wsf.common.ResourceLoaderAdapter;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainsMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedPortComponentRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedServiceRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedStubPropertyMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;
import org.w3c.dom.Element;

/**
 * Service delegates are used internally by Service objects to allow pluggability of JAX-WS implementations.
 *
 * Every Service object has its own delegate, created using the javax.xml.ws.Provider#createServiceDelegate method.
 * A Service object delegates all of its instance methods to its delegate.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 03-May-2006
 */
public class ServiceDelegateImpl extends ServiceDelegate
{
   // provide logging
   private final Logger log = Logger.getLogger(ServiceDelegateImpl.class);

   // The executor service
   private static ExecutorService defaultExecutor = Executors.newCachedThreadPool();
   // The service meta data that is associated with this JAXWS Service
   private ServiceMetaData serviceMetaData;
   // The ServiceRefMetaData supplied by the ServiceObjectFactory 
   private UnifiedServiceRefMetaData usRef;
   // The handler resolver
   private HandlerResolver handlerResolver;
   // The executor service
   private ExecutorService executor;

   // A list of annotated ports
   private List<QName> annotatedPorts = new ArrayList<QName>();

   public ServiceDelegateImpl(URL wsdlURL, QName serviceName, Class serviceClass)
   {
      // If this Service was constructed through the ServiceObjectFactory
      // this thread local association should be available
      usRef = ServiceObjectFactoryJAXWS.getServiceRefAssociation();
      UnifiedVirtualFile vfsRoot = (usRef != null ? vfsRoot = usRef.getVfsRoot() : new ResourceLoaderAdapter());

      // Verify wsdl access if this is not a generic Service
      if (wsdlURL != null && serviceClass != Service.class)
      {
         try
         {
            InputStream is = wsdlURL.openStream();
            is.close();
         }
         catch (IOException e)
         {
            log.warn("Cannot access wsdlURL: " + wsdlURL);
            wsdlURL = null;
         }
      }

      if (wsdlURL != null)
      {
         JAXWSClientMetaDataBuilder builder = new JAXWSClientMetaDataBuilder();
         serviceMetaData = builder.buildMetaData(serviceName, wsdlURL, vfsRoot);
      }
      else
      {
         UnifiedMetaData wsMetaData = new UnifiedMetaData(vfsRoot);
         serviceMetaData = new ServiceMetaData(wsMetaData, serviceName);
         wsMetaData.addService(serviceMetaData);
      }

      handlerResolver = new HandlerResolverImpl();

      if (usRef != null)
      {
         serviceMetaData.setServiceRefName(usRef.getServiceRefName());

         // Setup the service handlers
         if (usRef.getHandlerChain() != null)
         {
            String filename = usRef.getHandlerChain();
            UnifiedHandlerChainsMetaData handlerChainsMetaData = JAXWSMetaDataBuilder.getHandlerChainsMetaData(serviceClass, filename);
            for (UnifiedHandlerChainMetaData UnifiedHandlerChainMetaData : handlerChainsMetaData.getHandlerChains())
            {
               for (UnifiedHandlerMetaData uhmd : UnifiedHandlerChainMetaData.getHandlers())
               {
                  HandlerMetaDataJAXWS hmd = HandlerMetaDataJAXWS.newInstance(uhmd, HandlerType.ENDPOINT);
                  serviceMetaData.addHandler(hmd);
               }
            }
            ((HandlerResolverImpl)handlerResolver).initServiceHandlerChain(serviceMetaData);
         }
      }
   }

   /**
    * The getPort method returns a stub. A service client uses this stub to invoke operations on the target service endpoint.
    * The serviceEndpointInterface specifies the service endpoint interface that is supported by the created dynamic proxy or stub instance.
    */
   @Override
   public <T> T getPort(QName portName, Class<T> seiClass)
   {
      assertSEIConstraints(seiClass);

      if (serviceMetaData == null)
         throw new WebServiceException("Service meta data not available");

      // com/sun/ts/tests/jaxws/api/javax_xml_ws/Service#GetPort1NegTest1WithWsdl
      EndpointMetaData epMetaData = serviceMetaData.getEndpoint(portName);
      if (serviceMetaData.getEndpoints().size() > 0 && epMetaData == null)
         throw new WebServiceException("Cannot get port meta data for: " + portName);

      // This is the case when the service could not be created from wsdl
      if (serviceMetaData.getEndpoints().size() == 0)
      {
         log.warn("Cannot get port meta data for: " + portName);

         QName portType = getPortTypeName(seiClass);
         epMetaData = new ClientEndpointMetaData(serviceMetaData, portName, portType, Type.JAXWS);
      }

      String seiClassName = seiClass.getName();
      epMetaData.setServiceEndpointInterfaceName(seiClassName);

      return getPortInternal(epMetaData, seiClass);
   }

   /**
    * The getPort method returns a stub. A service client uses this stub to invoke operations on the target service endpoint.
    * The serviceEndpointInterface specifies the service endpoint interface that is supported by the created dynamic proxy or stub instance.
    */
   @Override
   public <T> T getPort(Class<T> seiClass)
   {
      assertSEIConstraints(seiClass);

      if (serviceMetaData == null)
         throw new WebServiceException("Service meta data not available");

      String seiClassName = seiClass.getName();
      EndpointMetaData epMetaData = serviceMetaData.getEndpointByServiceEndpointInterface(seiClassName);

      if (epMetaData == null && serviceMetaData.getEndpoints().size() == 1)
      {
         epMetaData = serviceMetaData.getEndpoints().get(0);
         epMetaData.setServiceEndpointInterfaceName(seiClassName);
      }
      else
      {
         QName portTypeName = getPortTypeName(seiClass);
         for (EndpointMetaData epmd : serviceMetaData.getEndpoints())
         {
            if (portTypeName.equals(epmd.getPortTypeName()))
            {
               epmd.setServiceEndpointInterfaceName(seiClass.getName());
               epMetaData = epmd;
               break;
            }
         }
      }

      if (epMetaData == null)
         throw new WebServiceException("Cannot get port meta data for: " + seiClassName);

      return getPortInternal(epMetaData, seiClass);
   }

   private <T> QName getPortTypeName(Class<T> seiClass)
   {
      if (!seiClass.isAnnotationPresent(WebService.class))
         throw new IllegalArgumentException("Cannot find @WebService on: " + seiClass.getName());

      WebService anWebService = seiClass.getAnnotation(WebService.class);
      String localPart = anWebService.name();
      if (localPart.length() == 0)
         localPart = WSDLUtils.getJustClassName(seiClass);

      String nsURI = anWebService.targetNamespace();
      if (nsURI.length() == 0)
         nsURI = WSDLUtils.getTypeNamespace(seiClass);

      QName portType = new QName(nsURI, localPart);
      return portType;
   }

   private <T> T getPortInternal(EndpointMetaData epMetaData, Class<T> seiClass)
   {
      QName portName = epMetaData.getPortName();

      // Adjust the endpoint meta data according to the annotations
      if (annotatedPorts.contains(portName) == false)
      {
         JAXWSClientMetaDataBuilder metaDataBuilder = new JAXWSClientMetaDataBuilder();
         metaDataBuilder.rebuildEndpointMetaData(epMetaData, seiClass);
         annotatedPorts.add(portName);
      }

      return (T)createProxy(seiClass, epMetaData);
   }

   private void assertSEIConstraints(Class seiClass)
   {
      if (seiClass == null)
         throw new IllegalArgumentException("Service endpoint interface cannot be null");

      if (!seiClass.isAnnotationPresent(WebService.class))
         throw new WebServiceException("SEI is missing @WebService annotation: " + seiClass);
   }

   @Override
   /**
    * Creates a new port for the service.
    * Ports created in this way contain no WSDL port type information
    * and can only be used for creating Dispatchinstances.
    */
   public void addPort(QName portName, String bindingId, String epAddress)
   {
      EndpointMetaData epMetaData = serviceMetaData.getEndpoint(portName);
      if (epMetaData == null)
      {
         epMetaData = new ClientEndpointMetaData(serviceMetaData, portName, null, Type.JAXWS);
         serviceMetaData.addEndpoint(epMetaData);
      }
      epMetaData.setBindingId(bindingId);
      epMetaData.setEndpointAddress(epAddress);
   }

   @Override
   public <T> Dispatch<T> createDispatch(QName portName, Class<T> type, Mode mode)
   {
      ExecutorService executor = (ExecutorService)getExecutor();
      EndpointMetaData epMetaData = getEndpointMetaData(portName);
      DispatchImpl dispatch = new DispatchImpl(executor, epMetaData, type, mode);
      return dispatch;
   }

   @Override
   public Dispatch<Object> createDispatch(QName portName, JAXBContext jbc, Mode mode)
   {
      ExecutorService executor = (ExecutorService)getExecutor();
      EndpointMetaData epMetaData = getEndpointMetaData(portName);
      DispatchImpl dispatch = new DispatchImpl(executor, epMetaData, jbc, mode);
      return dispatch;
   }

   private EndpointMetaData getEndpointMetaData(QName portName)
   {
      EndpointMetaData epMetaData = serviceMetaData.getEndpoint(portName);
      if (epMetaData == null)
         throw new WebServiceException("Cannot find port: " + portName);

      return epMetaData;
   }

   /** Gets the name of this service. */
   @Override
   public QName getServiceName()
   {
      return serviceMetaData.getServiceName();
   }

   /** Returns an Iterator for the list of QNames of service endpoints grouped by this service */
   @Override
   public Iterator<QName> getPorts()
   {
      ArrayList<QName> portNames = new ArrayList<QName>();
      for (EndpointMetaData epMetaData : serviceMetaData.getEndpoints())
      {
         portNames.add(epMetaData.getPortName());
      }
      return portNames.iterator();
   }

   @Override
   public URL getWSDLDocumentLocation()
   {
      return serviceMetaData.getWsdlLocation();
   }

   @Override
   public HandlerResolver getHandlerResolver()
   {
      return handlerResolver;
   }

   @Override
   public void setHandlerResolver(HandlerResolver handlerResolver)
   {
      this.handlerResolver = handlerResolver;
   }

   @Override
   public Executor getExecutor()
   {
      if (executor == null)
      {
         executor = defaultExecutor;
      }
      return executor;
   }

   @Override
   public void setExecutor(Executor executor)
   {
      if ((executor instanceof ExecutorService) == false)
         throw new IllegalArgumentException("Supported executors must implement " + ExecutorService.class.getName());

      this.executor = (ExecutorService)executor;
   }

   private <T> T createProxy(Class<T> seiClass, EndpointMetaData epMetaData) throws WebServiceException
   {
      if (seiClass == null)
         throw new IllegalArgumentException("SEI class cannot be null");

      try
      {
         ExecutorService executor = (ExecutorService)getExecutor();
         ClientProxy handler = new ClientProxy(executor, new ClientImpl(epMetaData, handlerResolver));
         ClassLoader cl = epMetaData.getClassLoader();

         T proxy;
         try
         {
            proxy = (T)Proxy.newProxyInstance(cl, new Class[] { seiClass, RMProvider.class, BindingProvider.class, StubExt.class }, handler);
         }
         catch (RuntimeException rte)
         {
            URL codeLocation = seiClass.getProtectionDomain().getCodeSource().getLocation();
            log.error("Cannot create proxy for SEI " + seiClass.getName() + " from: " + codeLocation);
            throw rte;
         }

         // Configure the stub
         configureStub((StubExt)proxy);

         return proxy;
      }
      catch (WebServiceException ex)
      {
         throw ex;
      }
      catch (Exception ex)
      {
         throw new WebServiceException("Cannot create proxy", ex);
      }
   }

   private void configureStub(StubExt stub)
   {
      EndpointMetaData epMetaData = stub.getEndpointMetaData();
      String seiName = epMetaData.getServiceEndpointInterfaceName();
      QName portName = epMetaData.getPortName();

      if (usRef == null)
      {
         log.debug("No port configuration for: " + portName);
         return;
      }

      String configFile = usRef.getConfigFile();
      String configName = usRef.getConfigName();

      UnifiedPortComponentRefMetaData pcref = usRef.getPortComponentRef(seiName, portName);
      if (pcref != null)
      {
         if (pcref.getConfigFile() != null)
            configFile = pcref.getConfigFile();
         if (pcref.getConfigName() != null)
            configName = pcref.getConfigName();

         BindingProvider bp = (BindingProvider)stub;
         Map<String, Object> reqCtx = bp.getRequestContext();
         for (UnifiedStubPropertyMetaData prop : pcref.getStubProperties())
         {
            log.debug("Set stub property: " + prop);
            reqCtx.put(prop.getPropName(), prop.getPropValue());
         }
      }

      if (configName != null || configFile != null)
      {
         log.debug("Configure Stub: [configName=" + configName + ",configFile=" + configFile + "]");
         stub.setConfigName(configName, configFile);
      }
   }

   @Override
   public <T> Dispatch<T> createDispatch(QName portName, Class<T> type, Mode mode, WebServiceFeature... features)
   {
      Dispatch<T> dispatch = createDispatch(portName, type, mode);
      initWebserviceFeatures(dispatch, features);
      return dispatch;
   }

   @Override
   public <T> Dispatch<T> createDispatch(EndpointReference epr, Class<T> type, Mode mode, WebServiceFeature... features)
   {
      QName portName = null;
      NativeEndpointReference nepr = EndpointReferenceUtil.transform(NativeEndpointReference.class, epr);
      portName = nepr.getEndpointName();
      
      Dispatch<T> dispatch = createDispatch(portName, type, mode);
      initAddressingProperties(dispatch, epr);
      initWebserviceFeatures(dispatch, features);
      return dispatch;
   }

   @Override
   public Dispatch<Object> createDispatch(QName portName, JAXBContext context, Mode mode, WebServiceFeature... features)
   {
      Dispatch<Object> dispatch = createDispatch(portName, context, mode);
      initWebserviceFeatures(dispatch, features);
      return dispatch;
   }

   @Override
   public Dispatch<Object> createDispatch(EndpointReference epr, JAXBContext context, Mode mode, WebServiceFeature... features)
   {
      QName portName = null;
      NativeEndpointReference nepr = EndpointReferenceUtil.transform(NativeEndpointReference.class, epr);
      portName = nepr.getEndpointName();

      Dispatch<Object> dispatch = createDispatch(portName, context, mode);
      initAddressingProperties(dispatch, epr);
      initWebserviceFeatures(dispatch, features);
      return dispatch;
   }

   @Override
   public <T> T getPort(QName portName, Class<T> sei, WebServiceFeature... features)
   {
      T port = getPort(portName, sei);
      initWebserviceFeatures(port, features);
      return port;
   }

   @Override
   public <T> T getPort(EndpointReference epr, Class<T> sei, WebServiceFeature... features)
   {
      T port = getPort(sei);
      initAddressingProperties((BindingProvider)port, epr);
      initWebserviceFeatures(port, features);
      return port;
   }

   @Override
   public <T> T getPort(Class<T> sei, WebServiceFeature... features)
   {
      T port = getPort(sei);
      initWebserviceFeatures(port, features);
      return port;
   }

   private <T> void initWebserviceFeatures(T stub, WebServiceFeature... features)
   {
      if (features != null)
      {
         EndpointMetaData epMetaData = ((StubExt)stub).getEndpointMetaData();
         for (WebServiceFeature feature : features)
         {
            ClientFeatureProcessor.processFeature(feature, epMetaData, stub);
         }
      }
   }

   // Workaround for [JBWS-2015] Modify addressing handlers to work with the JAXWS-2.1 API
   private void initAddressingProperties(BindingProvider bindingProvider, EndpointReference epr)
   {
      Map<String, Object> reqContext = bindingProvider.getRequestContext();
      AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
      AddressingProperties addrProps = builder.newAddressingProperties();
      reqContext.put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, addrProps);
      
      NativeEndpointReference nepr = EndpointReferenceUtil.transform(NativeEndpointReference.class, epr);
      try
      {
         if (nepr.getAddress() != null)
            addrProps.setTo(builder.newURI(nepr.getAddress()));

         List<Element> w3cRefParams = nepr.getReferenceParameters();
         if (w3cRefParams != null)
         {
            ReferenceParameters refParams = addrProps.getReferenceParameters();
            for (Element w3cRefParam : w3cRefParams)
            {
               refParams.addElement(w3cRefParam);
            }
         }
      }
      catch (URISyntaxException ex)
      {
         throw new IllegalArgumentException(ex);
      }
   }
}
