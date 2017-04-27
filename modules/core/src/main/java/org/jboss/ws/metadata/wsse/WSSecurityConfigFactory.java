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
package org.jboss.ws.metadata.wsse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;

/**
 * Create a WSSecurityConfiguration
 *  
 * @author Heiko.Braun@jboss.com
 * @author Thomas.Diesler@jboss.com
 */
public class WSSecurityConfigFactory
{
   // provide logging
   final Logger log = Logger.getLogger(WSSecurityConfigFactory.class);

   public static WSSecurityConfigFactory newInstance()
   {
      return new WSSecurityConfigFactory();
   }

   public WSSecurityConfiguration createConfiguration(UnifiedVirtualFile vfsRoot, String resourceName) throws IOException
   {
      URL configLocation = null;
      try
      {
         configLocation = new URL(resourceName);
      }
      catch (MalformedURLException ex)
      {
         // ignore
      }
      
      if (configLocation == null)
         configLocation = getResource(vfsRoot, "WEB-INF/" + resourceName, false);
      
      if (configLocation == null)
         configLocation = getResource(vfsRoot, "META-INF/" + resourceName, false);

      WSSecurityConfiguration config = null;
      if (configLocation != null)
      {
         log.debug("createConfiguration from: " + configLocation);
         config = WSSecurityOMFactory.newInstance().parse(configLocation);
         
         initKeystorePath(vfsRoot, config);
      }
		else
		{
			// an exception might be better here...
			log.trace("Unable to load ws-security config ("+resourceName+"). Security processing will be disabled");
		}		

		return config;
   }
   
   public void initKeystorePath(UnifiedVirtualFile vfsRoot, WSSecurityConfiguration config)
   {
      // Get and set deployment path to the keystore file
      URL keystoreLocation = null;
      if (config.getKeyStoreFile() != null)
      {
         keystoreLocation = getResource(vfsRoot, config.getKeyStoreFile(), true);
         log.debug("Add keystore: " + keystoreLocation);
         config.setKeyStoreURL(keystoreLocation);
      }

      URL truststoreLocation = null;
      if (config.getTrustStoreFile() != null)
      {
         truststoreLocation = getResource(vfsRoot, config.getTrustStoreFile(), true);
         log.debug("Add truststore: " + truststoreLocation);
         config.setTrustStoreURL(truststoreLocation);
      }
   }
   
   private URL getResource(UnifiedVirtualFile vfsRoot, String resource, boolean failOnNotFound)
   {
      URL resourceURL = null;
      try
      {
         UnifiedVirtualFile child = vfsRoot.findChild(resource);
         resourceURL = child.toURL();
      }
      catch (IOException ex)
      {
         if (failOnNotFound)
            throw new WSException("Cannot find required security resource: " + resource);
      }
      return resourceURL;
   }
}
