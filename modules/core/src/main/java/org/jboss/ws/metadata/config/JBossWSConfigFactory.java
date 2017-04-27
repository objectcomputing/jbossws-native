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
package org.jboss.ws.metadata.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.utils.JBossWSEntityResolver;
import org.jboss.ws.metadata.config.binding.OMFactoryJAXRPC;
import org.jboss.ws.metadata.config.binding.OMFactoryJAXWS;
import org.jboss.ws.metadata.config.jaxrpc.ConfigRootJAXRPC;
import org.jboss.ws.metadata.config.jaxws.ConfigRootJAXWS;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.ResourceLoaderAdapter;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.w3c.dom.Element;

/**
 * A factory for the JBossWS endpoint/client configuration 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 18-Dec-2005
 */
public class JBossWSConfigFactory
{
   // provide logging
   private final Logger log = Logger.getLogger(JBossWSConfigFactory.class);

   private static String URN_JAXRPC_CONFIG = "urn:jboss:jaxrpc-config:2.0";
   private static String URN_JAXWS_CONFIG = "urn:jboss:jaxws-config:2.0";

   // Hide constructor
   private JBossWSConfigFactory()
   {
   }

   /** Create a new instance of the factory
    */
   public static JBossWSConfigFactory newInstance()
   {
      return new JBossWSConfigFactory();
   }

   public Object parse(URL configURL)
   {
      if(log.isDebugEnabled()) log.debug("parse: " + configURL);

      Object wsConfig;
      InputStream is = null;
      try
      {
         Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
         unmarshaller.setValidation(true);
         unmarshaller.setSchemaValidation(true);
         unmarshaller.setEntityResolver(new JBossWSEntityResolver());

         String nsURI = getNamespaceURI(configURL);
         is = configURL.openStream();
         if (URN_JAXRPC_CONFIG.equals(nsURI))
         {
            wsConfig = unmarshaller.unmarshal(is, new OMFactoryJAXRPC(), null);
         }
         else if (URN_JAXWS_CONFIG.equals(nsURI))
         {
            wsConfig = unmarshaller.unmarshal(is, new OMFactoryJAXWS(), null);
         }
         else
         {
            throw new WSException("Invalid config namespace: " + nsURI);
         }

      }
      catch (JBossXBException e)
      {
         throw new WSException("Error while parsing configuration", e);
      }
      catch (IOException e)
      {
         throw new WSException("Failed to read config file: " + configURL, e);
      }
      finally
      {
         if (is != null)
         {
            try
            {
               is.close();
            }
            catch (IOException ioe)
            {
               if(log.isDebugEnabled()) log.warn(ioe.getMessage(), ioe);
            }
         }
      }

      return wsConfig;
   }

   private String getNamespaceURI(URL configURL)
   {
      try
      {
         Element root = DOMUtils.parse(configURL.openStream());
         return root.getNamespaceURI();
      }
      catch (IOException ex)
      {
         throw new WSException(ex);
      }
   }

   /**
    * @return config - cannot be null
    */
   public CommonConfig getConfig(UnifiedVirtualFile vfsRoot, String configName, String configFile)
   {
      if(log.isDebugEnabled()) log.debug("getConfig: [name=" + configName + ",url=" + configFile + "]");

      if (configName == null)
         throw new IllegalArgumentException("Config name cannot be null");
      if (configFile == null)
         throw new IllegalArgumentException("Config file cannot be null");

      // Get the config root
      URL configURL = filenameToURL(vfsRoot, configFile);
      Object configRoot = parse(configURL);

      // Get the endpoint config
      CommonConfig config;
      if (configRoot instanceof ConfigRootJAXRPC)
      {
         config = ((ConfigRootJAXRPC)configRoot).getConfigByName(configName);
      }
      else
      {
         config = ((ConfigRootJAXWS)configRoot).getConfigByName(configName);
      }

      if (config == null)
         throw new WSException("Cannot obtain config: " + configName);

      return config;
   }

   private URL filenameToURL(UnifiedVirtualFile vfsRoot, String configFile)
   {
      URL configURL = null;
      try
      {
         configURL = vfsRoot.findChild(configFile).toURL();
      }
      catch (IOException ex)
      {
         // ignore
      }
      
      // Try to get the URL as resource
      if (configURL == null)
      {
         try
         {
            configURL = new ResourceLoaderAdapter().findChild(configFile).toURL();
         }
         catch (IOException ex)
         {
            // ignore
         }
      }
      
      if (configURL == null)
         throw new WSException("Cannot find configFile: " + configFile);
      
      return configURL;
   }
}
