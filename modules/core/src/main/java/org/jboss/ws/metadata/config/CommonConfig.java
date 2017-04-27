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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.ws.extensions.wsrm.config.RMConfig;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.HandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * A common configuration 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 18-Dec-2005
 */
public abstract class CommonConfig
{
   private String configName;
   private RMConfig wsrmCfg;
   private List<URI> features = new ArrayList<URI>();
   private List<EndpointProperty> properties = new ArrayList<EndpointProperty>();

   public String getConfigName()
   {
      return configName;
   }

   public void setConfigName(String configName)
   {
      this.configName = configName;
   }

   public abstract List<HandlerMetaData> getHandlers(EndpointMetaData epMetaData, HandlerType type);

   public boolean hasFeature(URI type) {
      return features.contains(type);
   }

   public boolean hasFeature(String uri)
   {
      return hasFeature(nameToURI(uri));
   }
   
   public void setFeature(String type, boolean enabled) {

      if(enabled) {
         features.add(nameToURI(type));
      }
      else
         features.remove(nameToURI(type));
   }

   public void setRMMetaData(RMConfig wsrmCfg)
   {
      this.wsrmCfg = wsrmCfg;
   }
   
   public RMConfig getRMMetaData()
   {
      return this.wsrmCfg;
   }

   public void addProperty(String name, String value)
   {
      EndpointProperty p = new EndpointProperty();
      p.name = nameToURI(name);
      p.value = value;
      properties.add(p);
   }

   public String getProperty(String name)
   {
      String value = null;
      URI uri = nameToURI(name);

      for(EndpointProperty wsp : properties)
      {
         if(wsp.name.equals(uri))
         {
            value = wsp.value;
            break;
         }
      }
      return value;
   }

   public List<EndpointProperty> getProperties() {
      return properties;
   }

   private static URI nameToURI(String name)
   {
      URI uri = null;
      try {
         uri = new URI(name);
      } catch (URISyntaxException e) {
         throw new IllegalArgumentException(e.getMessage());
      }
      return uri;
   }
}
