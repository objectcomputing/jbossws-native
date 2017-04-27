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

/**
 * Provides configuration for JBossWS components.<br>
 * Currently this is implemented by <code>EndpointMetaData</code>.
 *
 * @author Heiko.Braun@jboss.org
 * @since 15.12.2006
 *
 * @see org.jboss.ws.metadata.umdm.EndpointMetaData
 */
public interface ConfigurationProvider
{

   static final String DEFAULT_JAXRPC_ENDPOINT_CONFIG_FILE = "META-INF/standard-jaxrpc-endpoint-config.xml";
   static final String DEFAULT_JAXWS_ENDPOINT_CONFIG_FILE = "META-INF/standard-jaxws-endpoint-config.xml";
   static final String DEFAULT_ENDPOINT_CONFIG_NAME = "Standard Endpoint";

   static final String DEFAULT_JAXRPC_CLIENT_CONFIG_FILE = "META-INF/standard-jaxrpc-client-config.xml";
   static final String DEFAULT_JAXWS_CLIENT_CONFIG_FILE = "META-INF/standard-jaxws-client-config.xml";
   static final String DEFAULT_CLIENT_CONFIG_NAME = "Standard Client";

   /**
    * Callback for components that require configuration
    */
   void configure(Configurable configurable);

   void registerConfigObserver(Configurable configurable);

   String getConfigFile();

   String getConfigName();

   void setConfigName(String configName);

   void setConfigName(String configName, String configFile);
}
