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
package javax.xml.rpc;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

/** The javax.xml.rpc.ServiceFactory is an abstract class that provides a
 * factory for the creation of instances of the type javax.xml.rpc.Service.
 * This abstract class follows the abstract static factory design pattern.
 * This enables a J2SE based client to create a Service instance in a portable
 * manner without using the constructor of the Service implementation class.
 * 
 * The ServiceFactory implementation class is set using the
 * javax.xml.rpc.ServiceFactory System property.
 *
 * @author Scott.Stark@jboss.org
 * @author Thomas.Diesler@jboss.org
 * @version 1.1
 */
public abstract class ServiceFactory
{
   // provide logging
   private static Logger log = Logger.getLogger(ServiceFactory.class.getName());

   // The singlton
   private static ServiceFactory factory;

   /** A constant representing the property used to lookup the name of a ServiceFactory implementation class. */
   public static final String SERVICEFACTORY_PROPERTY = "javax.xml.rpc.ServiceFactory";

   private static final String DEFAULT_SERVICE_FACTORY = "org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl";
   private static final String[] alternativeFactories = new String[] {};

   protected ServiceFactory()
   {
   }

   /** Gets an instance of the ServiceFactory
    * Only one copy of a factory exists and is returned to the application each time this method is called.
    *
    * The implementation class to be used can be overridden by setting the javax.xml.rpc.ServiceFactory system property.
    *
    * @return The ServiceFactory singleton
    * @throws ServiceException on failure to instantiate the ServiceFactory impl
    */
   public static ServiceFactory newInstance() throws ServiceException
   {
      // Create the factory singleton if needed
      if (factory == null)
      {
         PrivilegedAction action = new PropertyAccessAction(SERVICEFACTORY_PROPERTY, DEFAULT_SERVICE_FACTORY);
         String factoryName = (String)AccessController.doPrivileged(action);

         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         try
         {
            try
            {
               Class factoryClass = loader.loadClass(factoryName);
               factory = (ServiceFactory)factoryClass.newInstance();
            }
            catch (ClassNotFoundException e)
            {
               // Throw the exception if the user asked for a specific factory
               if (factoryName.equals(DEFAULT_SERVICE_FACTORY) == false)
                  throw e;

               for (int i = 0; i < alternativeFactories.length; i++)
               {
                  factoryName = alternativeFactories[i];
                  try
                  {
                     Class factoryClass = loader.loadClass(factoryName);
                     return (ServiceFactory)factoryClass.newInstance();
                  }
                  catch (ClassNotFoundException e1)
                  {
                     log.severe("Cannot load factory: " + factoryName);
                  }
               }
            }
         }
         catch (Throwable e)
         {
            throw new ServiceException("Failed to create factory: " + factoryName, e);
         }
      }

      if (factory == null)
         throw new ServiceException("Cannot find ServiceFactory implementation");

      return factory;
   }

   /**
    * Create a <code>Service</code> instance.
    *
    * @param   wsdlDocumentLocation URL for the WSDL document location
    * @param   serviceName  QName for the service.
    * @return  Service.
    * @throws  ServiceException If any error in creation of the
    *                specified service
    */
   public abstract Service createService(URL wsdlDocumentLocation, QName serviceName) throws ServiceException;

   /**
    * Create a <code>Service</code> instance.
    *
    * @param   serviceName QName for the service
    * @return  Service.
    * @throws  ServiceException If any error in creation of the specified service
    */
   public abstract Service createService(QName serviceName) throws ServiceException;

   /** Create an instance of the generated service implementation class for a given service interface, if available.
    * @param serviceInterface Service interface
    * @return A Service
    * @throws ServiceException If there is any error while creating the specified service, including the case where a
    * generated service implementation class cannot be located
    */
   public abstract Service loadService(Class serviceInterface) throws ServiceException;

   /**
    * Create an instance of the generated service implementation class for a given service interface, if available.
    * An implementation may use the provided wsdlDocumentLocation and properties to help locate the generated implementation class.
    * If no such class is present, a ServiceException will be thrown.
    *
    * @param wsdlDocumentLocation URL for the WSDL document location for the service or null
    * @param serviceInterface Service interface
    * @param props A set of implementation-specific properties to help locate the generated service implementation class
    * @return A Service
    * @throws ServiceException If there is any error while creating the specified service, including the case where a
    * generated service implementation class cannot be located
    */
   public abstract Service loadService(URL wsdlDocumentLocation, Class serviceInterface, Properties props) throws ServiceException;

   /**
    * Create an instance of the generated service implementation class for a given service, if available.
    * The service is uniquely identified by the wsdlDocumentLocation and serviceName arguments.
    * An implementation may use the provided properties to help locate the generated implementation class.
    * If no such class is present, a ServiceException will be thrown.
    *
    * @param wsdlDocumentLocation URL for the WSDL document location for the service or null
    * @param serviceName Qualified name for the service
    * @param props A set of implementation-specific properties to help locate the generated service implementation class
    * @return A Service
    * @throws ServiceException If there is any error while creating the specified service, including the case where a generated service implementation class cannot be located
    */
   public abstract Service loadService(URL wsdlDocumentLocation, QName serviceName, Properties props) throws ServiceException;

   private static class PropertyAccessAction implements PrivilegedAction
   {
      private String name;
      private String defaultValue;

      PropertyAccessAction(String name, String defaultValue)
      {
         this.name = name;
         this.defaultValue = defaultValue;
      }

      public Object run()
      {
         return System.getProperty(name, defaultValue);
      }
   }
}
