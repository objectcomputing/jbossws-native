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

import java.net.URL;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;

/**
 * Service class acts as a factory for:
 * <ul>
 * <li>Dynamic proxy for the target service endpoint.
 * <li>Instance of the type javax.xml.rpc.Call for the dynamic invocation of a remote operation on the target service endpoint.
 * <li>Instance of a generated stub class
 * </ul>
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public class ServiceFactoryImpl extends ServiceFactory
{
   // provide logging
   private final Logger log = Logger.getLogger(ServiceFactoryImpl.class);
   
   /**
    * Create an instance of the generated service implementation class for a given service interface, if available.
    *
    * @param serviceInterface Service interface
    * @return A Service
    * @throws ServiceException If there is any error while creating the specified service, including the case where a
    *                          generated service implementation class cannot be located
    */
   public Service loadService(Class serviceInterface) throws ServiceException
   {
      throw new NotImplementedException();
   }

   /**
    * Create an instance of the generated service implementation class for a given service interface, if available.
    * An implementation may use the provided wsdlDocumentLocation and properties to help locate the generated implementation class.
    * If no such class is present, a ServiceException will be thrown.
    *
    * @param wsdlDocumentLocation URL for the WSDL document location for the service or null
    * @param serviceInterface     Service interface
    * @param props                A set of implementation-specific properties to help locate the generated service implementation class
    * @return A Service
    * @throws ServiceException If there is any error while creating the specified service, including the case where a
    *                          generated service implementation class cannot be located
    */
   public Service loadService(URL wsdlDocumentLocation, Class serviceInterface, Properties props) throws ServiceException
   {
      throw new NotImplementedException();
   }

   /**
    * Create an instance of the generated service implementation class for a given service, if available.
    * The service is uniquely identified by the wsdlDocumentLocation and serviceName arguments.
    * An implementation may use the provided properties to help locate the generated implementation class.
    * If no such class is present, a ServiceException will be thrown.
    *
    * @param wsdlDocumentLocation URL for the WSDL document location for the service or null
    * @param serviceName          Qualified name for the service
    * @param props                A set of implementation-specific properties to help locate the generated service implementation class
    * @return A Service
    * @throws ServiceException If there is any error while creating the specified service, including the case where a generated service implementation class cannot be located
    */
   public Service loadService(URL wsdlDocumentLocation, QName serviceName, Properties props) throws ServiceException
   {
      throw new NotImplementedException();
   }

   /**
    * Create a <code>Service</code> instance.
    *
    * @param serviceName QName for the service
    * @return Service.
    * @throws ServiceException If any error in creation of the specified service
    */
   public Service createService(QName serviceName) throws ServiceException
   {
      return new ServiceImpl(serviceName);
   }

   /**
    * Create a <code>Service</code> instance.
    *
    * @param wsdlURL URL for the WSDL document location
    * @param serviceName  QName for the service.
    * @return Service.
    * @throws ServiceException If any error in creation of the specified service
    */
   public Service createService(URL wsdlURL, QName serviceName) throws ServiceException
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      
      URL mappingURL = cl.getResource("META-INF/jaxrpc-mapping.xml");
      if (mappingURL != null)
         log.info("Use jaxrpc-mapping from: " + mappingURL);
      
      return createService(wsdlURL, serviceName, mappingURL, null);
   }

   /**
    * Create a <code>Service</code> instance.
    *
    * @param wsdlURL URL for the WSDL document location
    * @param serviceName  QName for the service.
    * @param mappingURL URL for the jaxrpc-mapping.xml document location
    * @return Service.
    * @throws ServiceException If any error in creation of the specified service
    */
   public Service createService(URL wsdlURL, QName serviceName, URL mappingURL) throws ServiceException
   {
      return createService(wsdlURL, serviceName, mappingURL, null);
   }

   /**
    * Create a <code>Service</code> instance.
    *
    * @param wsdlURL URL for the WSDL document location
    * @param serviceName  QName for the service.
    * @param mappingURL URL for the jaxrpc-mapping.xml document location
    * @param securityURL URL for the jboss-ws-security.xml file
    * @return Service.
    * @throws ServiceException If any error in creation of the specified service
    */
   public Service createService(URL wsdlURL, QName serviceName, URL mappingURL, URL securityURL) throws ServiceException
   {
      ServiceImpl service = new ServiceImpl(serviceName, wsdlURL, mappingURL, securityURL);
      return service;
   }
}
