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
package javax.xml.ws.spi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

/**
 * Load the provider using the following algorithm.
 * 
 * 1. If a resource with the name of META-INF/services/javax.xml.ws.spi.Provider exists, then
 * its first line, if present, is used as the UTF-8 encoded name of the implementation class.
 * 
 * 2. If the ${java.home}/lib/jaxws.properties file exists and it is readable by the 
 * java.util.Properties.load(InputStream) method and it contains an entry whose key is 
 * javax.xml.ws.spi.Provider, then the value of that entry is used as the name of the implementation class.
 * 
 * 3. If a system property with the name javax.xml.ws.spi.Provider is defined, then its value is used
 * as the name of the implementation class.
 * 
 * 4. Finally, a default implementation class name is used.
 *  
 * @author Thomas.Diesler@jboss.com
 * @since 03-May-2006
 */
abstract class ProviderLoader
{
   /**
    * This method uses the algirithm described above. 
    * 
    * 1. If a resource with the name of META-INF/services/javax.xml.ws.spi.Provider exists, then
    * its first line, if present, is used as the UTF-8 encoded name of the implementation class.
    * 
    * 2. If the ${java.home}/lib/jaxws.properties file exists and it is readable by the 
    * java.util.Properties.load(InputStream) method and it contains an entry whose key is 
    * javax.xml.ws.spi.Provider, then the value of that entry is used as the name of the implementation class.
    * 
    * 3. If a system property with the name javax.xml.ws.spi.Provider is defined, then its value is used
    * as the name of the implementation class.
    * 
    * 4. Finally, a default implementation class name is used.
    */
   public static Provider loadProvider(String defaultFactory)
   {
      Object factory = null;
      String factoryName = null;
      ClassLoader loader = Thread.currentThread().getContextClassLoader();

      // Use the Services API (as detailed in the JAR specification), if available, to determine the classname.
      String propertyName = Provider.JAXWSPROVIDER_PROPERTY;
      String filename = "META-INF/services/" + propertyName;
      InputStream inStream = loader.getResourceAsStream(filename);
      if (inStream != null)
      {
         try
         {
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            factoryName = br.readLine();
            br.close();
            if (factoryName != null)
            {
               Class factoryClass = loader.loadClass(factoryName);
               factory = factoryClass.newInstance();
            }
         }
         catch (Throwable t)
         {
            throw new IllegalStateException("Failed to load " + propertyName + ": " + factoryName, t);
         }
      }

      // Use the properties file "lib/jaxws.properties" in the JRE directory.
      // This configuration file is in standard java.util.Properties format and contains the fully qualified name of the implementation class with the key being the system property defined above.
      if (factory == null)
      {
         PrivilegedAction action = new PropertyAccessAction("java.home");
         String javaHome = (String)AccessController.doPrivileged(action);
         File jaxmFile = new File(javaHome + "/lib/jaxws.properties");
         if (jaxmFile.exists())
         {
            try
            {
               action = new PropertyFileAccessAction(jaxmFile.getCanonicalPath());
               Properties jaxmProperties = (Properties)AccessController.doPrivileged(action);
               factoryName = jaxmProperties.getProperty(propertyName);
               if (factoryName != null)
               {
                  //if(log.isDebugEnabled()) log.debug("Load from " + jaxmFile + ": " + factoryName);
                  Class factoryClass = loader.loadClass(factoryName);
                  factory = factoryClass.newInstance();
               }
            }
            catch (Throwable t)
            {
               throw new IllegalStateException("Failed to load " + propertyName + ": " + factoryName, t);
            }
         }
      }

      // Use system property
      if (factory == null)
      {
         PrivilegedAction action = new PropertyAccessAction(propertyName);
         factoryName = (String)AccessController.doPrivileged(action);
         if (factoryName != null)
         {
            try
            {
               //if(log.isDebugEnabled()) log.debug("Load from system property: " + factoryName);
               Class factoryClass = loader.loadClass(factoryName);
               factory = factoryClass.newInstance();
            }
            catch (Throwable t)
            {
               throw new IllegalStateException("Failed to load " + propertyName + ": " + factoryName, t);
            }
         }
      }
      
      // Use the default factory implementation class.
      if (factory == null && defaultFactory != null)
      {
         try
         {
            //if(log.isDebugEnabled()) log.debug("Load from default: " + factoryName);
            Class factoryClass = loader.loadClass(defaultFactory);
            factory = factoryClass.newInstance();
         }
         catch (Throwable t)
         {
            throw new IllegalStateException("Failed to load: " + defaultFactory, t);
         }
      }

      return (Provider)factory;
   }

   private static class PropertyAccessAction implements PrivilegedAction
   {
      private String name;

      PropertyAccessAction(String name)
      {
         this.name = name;
      }

      public Object run()
      {
         return System.getProperty(name);
      }
   }

   private static class PropertyFileAccessAction implements PrivilegedAction
   {
      private String filename;

      PropertyFileAccessAction(String filename)
      {
         this.filename = filename;
      }

      public Object run()
      {
         try
         {
            InputStream inStream = new FileInputStream(filename);
            Properties props = new Properties();
            props.load(inStream);
            return props;
         }
         catch (IOException ex)
         {
            throw new SecurityException("Cannot load properties: " + filename, ex);
         }
      }
   }
}
