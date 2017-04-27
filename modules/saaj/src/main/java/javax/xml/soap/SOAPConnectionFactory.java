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
package javax.xml.soap;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

/** A factory for creating SOAPConnection objects. Implementation of this class
 * is optional. If SOAPConnectionFactory.newInstance() throws an
 * UnsupportedOperationException then the implementation does not support the
 * SAAJ communication infrastructure. Otherwise SOAPConnection objects can be
 * created by calling createConnection() on the newly created
 * SOAPConnectionFactory object.
 *  
 * @author Scott.Stark@jboss.org
 * @author Thomas.Diesler@jboss.org
 */
public abstract class SOAPConnectionFactory
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPConnectionFactory.class.getName());

   private static final String DEFAULT_SOAP_CONNECTION_FACTORY = "org.jboss.ws.core.soap.SOAPConnectionFactoryImpl";
   private static final String[] alternativeFactories = new String[] { "org.jboss.webservice.soap.SOAPConnectionFactoryImpl",
         "org.jboss.axis.soap.SOAPConnectionFactoryImpl" };

   /** Creates an instance of the default SOAPConnectionFactory object.
    * 
    * @return
    * @throws SOAPException
    * @throws UnsupportedOperationException
    */
   public static SOAPConnectionFactory newInstance() throws SOAPException, UnsupportedOperationException
   {
      PrivilegedAction action = new PropertyAccessAction(SOAPConnectionFactory.class.getName(), DEFAULT_SOAP_CONNECTION_FACTORY);
      String factoryName = (String)AccessController.doPrivileged(action);

      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      try
      {
         try
         {
            Class factoryClass = loader.loadClass(factoryName);
            return (SOAPConnectionFactory)factoryClass.newInstance();
         }
         catch (ClassNotFoundException e)
         {
            // Throw the exception if the user asked for a specific factory
            if (factoryName.equals(DEFAULT_SOAP_CONNECTION_FACTORY) == false)
               throw e;

            for (int i = 0; i < alternativeFactories.length; i++)
            {
               factoryName = alternativeFactories[i];
               try
               {
                  Class factoryClass = loader.loadClass(factoryName);
                  return (SOAPConnectionFactory)factoryClass.newInstance();
               }
               catch (ClassNotFoundException e1)
               {
                  log.severe("Cannot load factory: " + factoryName);
               }
            }
         }
      }
      catch (Throwable t)
      {
         throw new SOAPException("Failed to create SOAPConnectionFactory: " + factoryName, t);
      }

      throw new SOAPException("Cannot find SOAPConnectionFactory implementation");
   }

   public abstract SOAPConnection createConnection() throws SOAPException;

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
