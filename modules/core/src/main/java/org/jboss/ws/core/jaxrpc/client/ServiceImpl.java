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
package org.jboss.ws.core.jaxrpc.client;

import java.lang.reflect.Proxy;
import java.net.URL;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.HandlerRegistry;

import org.jboss.logging.Logger;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.metadata.builder.jaxrpc.JAXRPCClientMetaDataBuilder;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.HandlerMetaData;
import org.jboss.ws.metadata.umdm.HandlerMetaDataJAXRPC;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.wsf.common.ResourceLoaderAdapter;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedCallPropertyMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedInitParamMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedPortComponentRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedServiceRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedStubPropertyMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * Service class acts as a factory for:
 * <ul>
 * <li>Dynamic proxy for the target service endpoint.
 * <li>Instance of the type javax.xml.rpc.Call for the dynamic invocation of a
 * remote operation on the target service endpoint.
 * <li>Instance of a generated stub class
 * </ul>
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public class ServiceImpl implements ServiceExt
{
   // provide logging
   private static final Logger log = Logger.getLogger(ServiceImpl.class);

   // The service meta data that is associated with this JAXRPC Service
   private ServiceMetaData serviceMetaData;
   // The optional WSDL location
   private URL wsdlLocation;
   // The <service-ref> meta data
   private UnifiedServiceRefMetaData usrMetaData;

   // The handler registry
   private HandlerRegistryImpl handlerRegistry;

   /**
    * Construct a Service without WSDL meta data
    */
   ServiceImpl(QName serviceName)
   {
      UnifiedMetaData wsMetaData = new UnifiedMetaData(new ResourceLoaderAdapter());
      serviceMetaData = new ServiceMetaData(wsMetaData, serviceName);
      handlerRegistry = new HandlerRegistryImpl(serviceMetaData);
   }

   /**
    * Construct a Service that has access to some WSDL meta data
    */
   ServiceImpl(QName serviceName, URL wsdlURL, URL mappingURL, URL securityURL)
   {
      this.wsdlLocation = wsdlURL;
      JAXRPCClientMetaDataBuilder builder = new JAXRPCClientMetaDataBuilder();

      ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();

      serviceMetaData = builder.buildMetaData(serviceName, wsdlURL, mappingURL, securityURL, null, ctxClassLoader);
      handlerRegistry = new HandlerRegistryImpl(serviceMetaData);
   }

   /**
    * Construct a Service that has access to some WSDL meta data
    */
   ServiceImpl(QName serviceName, URL wsdlURL, JavaWsdlMapping mappingURL, WSSecurityConfiguration securityConfig, UnifiedServiceRefMetaData usrMetaData)
   {
      this.wsdlLocation = wsdlURL;
      this.usrMetaData = usrMetaData;

      JAXRPCClientMetaDataBuilder builder = new JAXRPCClientMetaDataBuilder();
      ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();

      serviceMetaData = builder.buildMetaData(serviceName, wsdlURL, mappingURL, securityConfig, usrMetaData, ctxClassLoader);
      handlerRegistry = new HandlerRegistryImpl(serviceMetaData);
   }

   public ServiceMetaData getServiceMetaData()
   {
      return serviceMetaData;
   }

   /**
    * Gets the location of the WSDL document for this Service.
    *
    * @return URL for the location of the WSDL document for this service
    */
   public URL getWSDLDocumentLocation()
   {
      return wsdlLocation;
   }

   /**
    * Gets the name of this service.
    *
    * @return Qualified name of this service
    */
   public QName getServiceName()
   {
      return serviceMetaData.getServiceName();
   }

   /**
    * Creates a Call instance.
    *
    * @param portName
    *            Qualified name for the target service endpoint
    * @return Call instance
    * @throws javax.xml.rpc.ServiceException
    *             If any error in the creation of the Call object
    */
   public Call createCall(QName portName) throws ServiceException
   {
      String nsURI = portName.getNamespaceURI();
      serviceMetaData.assertTargetNamespace(nsURI);
      CallImpl call = new CallImpl(this, portName, null);
      initCallProperties(call, null);
      return call;
   }

   /**
    * Creates a Call instance.
    *
    * @param portName
    *            Qualified name for the target service endpoint
    * @param operationName
    *            Name of the operation for which this Call object is to be
    *            created.
    * @return Call instance
    * @throws javax.xml.rpc.ServiceException
    *             If any error in the creation of the Call object
    */
   public Call createCall(QName portName, String operationName) throws ServiceException
   {
      String nsURI = portName.getNamespaceURI();
      serviceMetaData.assertTargetNamespace(nsURI);
      QName opName = new QName(nsURI, operationName);
      CallImpl call = new CallImpl(this, portName, opName);
      initCallProperties(call, null);
      return call;
   }

   /**
    * Creates a Call instance.
    *
    * @param portName
    *            Qualified name for the target service endpoint
    * @param opName
    *            Qualified name of the operation for which this Call object is
    *            to be created.
    * @return Call instance
    * @throws javax.xml.rpc.ServiceException
    *             If any error in the creation of the Call object
    */
   public Call createCall(QName portName, QName opName) throws ServiceException
   {
      serviceMetaData.assertTargetNamespace(portName.getNamespaceURI());
      serviceMetaData.assertTargetNamespace(opName.getNamespaceURI());
      CallImpl call = new CallImpl(this, portName, opName);
      initCallProperties(call, null);
      return call;
   }

   /**
    * Creates a Call object not associated with specific operation or target
    * service endpoint. This Call object needs to be configured using the
    * setter methods on the Call interface.
    *
    * @return Call object
    * @throws javax.xml.rpc.ServiceException
    *             If any error in the creation of the Call object
    */
   public Call createCall() throws ServiceException
   {
      CallImpl call = new CallImpl(this);
      initCallProperties(call, null);
      return call;
   }

   /**
    * Gets an array of preconfigured Call objects for invoking operations on
    * the specified port. There is one Call object per operation that can be
    * invoked on the specified port. Each Call object is pre-configured and
    * does not need to be configured using the setter methods on Call
    * interface. <p/> Each invocation of the getCalls method returns a new
    * array of preconfigured Call objects <p/> This method requires the Service
    * implementation class to have access to the WSDL related metadata.
    *
    * @param portName
    *            Qualified name for the target service endpoint
    * @return Call[] Array of pre-configured Call objects
    * @throws javax.xml.rpc.ServiceException
    *             If this Service class does not have access to the required
    *             WSDL metadata or if an illegal endpointName is specified.
    */
   public Call[] getCalls(QName portName) throws ServiceException
   {
      EndpointMetaData epMetaData = serviceMetaData.getEndpoint(portName);
      if (epMetaData == null)
         throw new ServiceException("Cannot find endpoint for name: " + portName);

      List<Call> calls = new ArrayList<Call>();
      for (OperationMetaData opMetaData : epMetaData.getOperations())
      {
         Call call = createCall(portName, opMetaData.getQName());
         calls.add(call);
      }

      Call[] callArr = new Call[calls.size()];
      calls.toArray(callArr);

      return callArr;
   }

   /**
    * J2EE components should not use the getHandlerRegistry() method. A
    * container provider must throw a java.lang.UnsupportedOperationException
    * from the getHandlerRegistry() method of the Service Interface. Handler
    * support is documented in Chapter 6 Handlers.
    */
   public HandlerRegistry getHandlerRegistry()
   {
      throw new UnsupportedOperationException("Components should not use the getHandlerRegistry() method.");
   }

   /**
    * Get a HandlerRegistry that can be used to dynamically change the client
    * side handler chain associated with a given endpoint.
    */
   public HandlerRegistry getDynamicHandlerRegistry()
   {
      return handlerRegistry;
   }

   /**
    * J2EE components should not use the getTypeMappingRegistry() method. A
    * container provider must throw a java.lang.UnsupportedOperationException
    * from the getTypeMappingRegistry() method of the Service Interface.
    */
   public TypeMappingRegistry getTypeMappingRegistry()
   {
      throw new UnsupportedOperationException("Components should not use the getTypeMappingRegistry() method.");
   }

   /**
    * Returns an Iterator for the list of QNames of service endpoints grouped
    * by this service
    *
    * @return Returns java.util.Iterator with elements of type
    *         javax.xml.namespace.QName
    * @throws javax.xml.rpc.ServiceException
    *             If this Service class does not have access to the required
    *             WSDL metadata
    */
   public Iterator getPorts() throws ServiceException
   {
      ArrayList<QName> list = new ArrayList<QName>();
      if (serviceMetaData != null)
      {
         for (EndpointMetaData epMetaData : serviceMetaData.getEndpoints())
         {
            list.add(epMetaData.getPortName());
         }
      }
      return list.iterator();
   }

   /**
    * The getPort method returns either an instance of a generated stub
    * implementation class or a dynamic proxy. The parameter
    * serviceEndpointInterface specifies the service endpoint interface that is
    * supported by the returned stub or proxy. In the implementation of this
    * method, the JAX-RPC runtime system takes the responsibility of selecting
    * a protocol binding (and a port) and configuring the stub accordingly. The
    * returned Stub instance should not be reconfigured by the client.
    */
   public Remote getPort(Class seiClass) throws ServiceException
   {
      if (seiClass == null)
         throw new IllegalArgumentException("SEI class cannot be null");

      String seiName = seiClass.getName();
      if (Remote.class.isAssignableFrom(seiClass) == false)
         throw new ServiceException("SEI does not implement java.rmi.Remote: " + seiName);

      if (serviceMetaData == null)
         throw new ServiceException("Service meta data not available");

      try
      {
         EndpointMetaData epMetaData = serviceMetaData.getEndpointByServiceEndpointInterface(seiName);
         if (epMetaData == null && serviceMetaData.getEndpoints().size() == 1)
         {
            epMetaData = serviceMetaData.getEndpoints().get(0);
            epMetaData.setServiceEndpointInterfaceName(seiName);
         }

         if (epMetaData == null)
            throw new ServiceException("Cannot find endpoint meta data for: " + seiName);

         return createProxy(seiClass, epMetaData);
      }
      catch (ServiceException ex)
      {
         throw ex;
      }
      catch (Exception ex)
      {
         throw new ServiceException("Cannot create proxy", ex);
      }
   }

   /**
    * The getPort method returns either an instance of a generated stub
    * implementation class or a dynamic proxy. A service client uses this
    * dynamic proxy to invoke operations on the target service endpoint. The
    * serviceEndpointInterface specifies the service endpoint interface that is
    * supported by the created dynamic proxy or stub instance.
    */
   public Remote getPort(QName portName, Class seiClass) throws ServiceException
   {
      if (seiClass == null)
         throw new IllegalArgumentException("SEI class cannot be null");

      if (serviceMetaData == null)
         throw new ServiceException("Service meta data not available");

      String seiName = seiClass.getName();
      if (Remote.class.isAssignableFrom(seiClass) == false)
         throw new ServiceException("SEI does not implement java.rmi.Remote: " + seiName);

      EndpointMetaData epMetaData = serviceMetaData.getEndpoint(portName);
      if (epMetaData == null)
         throw new ServiceException("Cannot obtain endpoint meta data for: " + portName);

      try
      {
         if (epMetaData.getServiceEndpointInterfaceName() == null)
            epMetaData.setServiceEndpointInterfaceName(seiName);

         return createProxy(seiClass, epMetaData);
      }
      catch (ServiceException ex)
      {
         throw ex;
      }
      catch (Exception ex)
      {
         throw new ServiceException("Cannot create proxy", ex);
      }
   }

   private Remote createProxy(Class seiClass, EndpointMetaData epMetaData) throws Exception
   {
      CallImpl call = new CallImpl(this, epMetaData);
      initStubProperties(call, seiClass.getName());

      // JBoss-4.0.x does not support <stub-properties>
      if (initCallProperties(call, seiClass.getName()) > 0)
         log.info("Deprecated use of <call-properties> on JAXRPC Stub. Use <stub-properties>");

      PortProxy handler = new PortProxy(call);
      ClassLoader cl = epMetaData.getClassLoader();
      Remote proxy = (Remote)Proxy.newProxyInstance(cl, new Class[] { seiClass, Stub.class, StubExt.class }, handler);

      // Setup the handler chain
      setupHandlerChain(epMetaData);

      return proxy;
   }

   private int initStubProperties(CallImpl call, String seiName)
   {
      // nothing to do
      if (usrMetaData == null)
         return 0;

      int propCount = 0;
      for (UnifiedPortComponentRefMetaData upcRef : usrMetaData.getPortComponentRefs())
      {
         if (seiName.equals(upcRef.getServiceEndpointInterface()))
         {
            for (UnifiedStubPropertyMetaData prop : upcRef.getStubProperties())
            {
               call.setProperty(prop.getPropName(), prop.getPropValue());
               propCount++;
            }
         }
      }
      return propCount;
   }

   private int initCallProperties(CallImpl call, String seiName)
   {
      setupHandlerChain(call.getEndpointMetaData());

      // nothing to do
      if (usrMetaData == null)
         return 0;

      int propCount = 0;

      // General properties
      for (UnifiedCallPropertyMetaData prop : usrMetaData.getCallProperties())
      {
         call.setProperty(prop.getPropName(), prop.getPropValue());
         propCount++;
      }

      if (seiName != null)
      {
         for (UnifiedPortComponentRefMetaData upcRef : usrMetaData.getPortComponentRefs())
         {
            if (seiName.equals(upcRef.getServiceEndpointInterface()))
            {
               for (UnifiedCallPropertyMetaData prop : upcRef.getCallProperties())
               {
                  call.setProperty(prop.getPropName(), prop.getPropValue());
                  propCount++;
               }
            }
         }
      }

      return propCount;
   }

   /**
    * Get the handler chain for the given endpoint name, maybe null.
    */
   public HandlerChain getHandlerChain(QName portName)
   {
      return handlerRegistry.getHandlerChainInstance(portName);
   }

   /**
    * Register a handler chain for the given endpoint name
    */
   public void registerHandlerChain(QName portName, List infos, Set roles)
   {
      handlerRegistry.registerClientHandlerChain(portName, infos, roles);
   }

   void setupHandlerChain(EndpointMetaData epMetaData)
   {
      if (epMetaData.isHandlersInitialized() == false)
      {
         QName portName = epMetaData.getPortName();
         Set<String> handlerRoles = new HashSet<String>();
         List<HandlerInfo> handlerInfos = new ArrayList<HandlerInfo>();
         for (HandlerMetaData handlerMetaData : epMetaData.getHandlerMetaData(HandlerType.ALL))
         {
            HandlerMetaDataJAXRPC jaxrpcMetaData = (HandlerMetaDataJAXRPC)handlerMetaData;
            handlerRoles.addAll(jaxrpcMetaData.getSoapRoles());

            HashMap hConfig = new HashMap();
            for (UnifiedInitParamMetaData param : jaxrpcMetaData.getInitParams())
            {
               hConfig.put(param.getParamName(), param.getParamValue());
            }

            Set<QName> headers = jaxrpcMetaData.getSoapHeaders();
            QName[] headerArr = new QName[headers.size()];
            headers.toArray(headerArr);

            Class hClass = jaxrpcMetaData.getHandlerClass();
            hConfig.put(HandlerType.class.getName(), jaxrpcMetaData.getHandlerType());
            HandlerInfo info = new HandlerInfo(hClass, hConfig, headerArr);

            log.debug("Adding client side handler to endpoint '" + portName + "': " + info);
            handlerInfos.add(info);
         }

         // register the handlers with the client engine
         if (handlerInfos.size() > 0)
            registerHandlerChain(portName, handlerInfos, handlerRoles);

         epMetaData.setHandlersInitialized(true);
      }
   }
}
