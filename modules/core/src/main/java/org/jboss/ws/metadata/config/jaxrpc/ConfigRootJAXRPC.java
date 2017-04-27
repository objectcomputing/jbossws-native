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
package org.jboss.ws.metadata.config.jaxrpc;

import java.util.ArrayList;
import java.util.List;

/** 
 * A JBossWS configuration 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 18-Dec-2005
 */
public class ConfigRootJAXRPC
{
   private List<ClientConfigJAXRPC> clientConfigList = new ArrayList<ClientConfigJAXRPC>();
   private List<EndpointConfigJAXRPC> endpointConfigList = new ArrayList<EndpointConfigJAXRPC>();

   public List<ClientConfigJAXRPC> getClientConfig()
   {
      return clientConfigList;
   }

   public void setClientConfig(List<ClientConfigJAXRPC> clientConfig)
   {
      this.clientConfigList = clientConfig;
   }

   public List<EndpointConfigJAXRPC> getEndpointConfig()
   {
      return endpointConfigList;
   }

   public void setEndpointConfig(List<EndpointConfigJAXRPC> endpointConfig)
   {
      this.endpointConfigList = endpointConfig;
   }
   
   public ClientConfigJAXRPC getClientConfigByName(String configName)
   {
      ClientConfigJAXRPC config = null;
      for(ClientConfigJAXRPC aux : clientConfigList)
      {
         if (aux.getConfigName().equals(configName))
         {
            config = aux;
            break;
         }
      }
      
      if (config == null && clientConfigList.size() == 1)
         config = clientConfigList.get(0);
      
      return config;
   }
   
   public EndpointConfigJAXRPC getEndpointConfigByName(String configName)
   {
      EndpointConfigJAXRPC config = null;
      for(EndpointConfigJAXRPC aux : endpointConfigList)
      {
         if (aux.getConfigName().equals(configName))
         {
            config = aux;
            break;
         }
      }
      
      if (config == null && endpointConfigList.size() == 1)
         config = endpointConfigList.get(0);
      
      return config;
   }

   public CommonConfigJAXRPC getConfigByName(String name)
   {
      CommonConfigJAXRPC config = getClientConfigByName(name);
      if(null == config)
         config = getEndpointConfigByName(name);
      return config;
   }
}
