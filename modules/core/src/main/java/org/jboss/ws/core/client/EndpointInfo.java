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
package org.jboss.ws.core.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.ws.WebServiceFeature;

import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.FeatureSet;

/** A wrapper object that associates the target address with some metadata
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 20-Jul-2005
 */
public class EndpointInfo
{
   private String targetAddress;
   private Map<String, Object> properties;
   private FeatureSet features;
   
   public EndpointInfo(EndpointMetaData epMetaData, String targetAddress, Map<String, Object> callProps)
   {
      this.features = epMetaData.getFeatures();
      this.targetAddress = this.lowerCaseProtocol(targetAddress);
      this.properties = callProps;

      // Add the service properties
      Properties serviceProps = epMetaData.getServiceMetaData().getProperties();
      if (serviceProps != null)
      {
         Iterator it = serviceProps.entrySet().iterator();
         while (it.hasNext())
         {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            Object val = entry.getValue();
            properties.put(key, val);
         }
      }

      // Add the endpoint properties
      Properties epProps = epMetaData.getProperties();
      Iterator it = epProps.entrySet().iterator();
      while (it.hasNext())
      {
         Map.Entry entry = (Map.Entry)it.next();
         String key = (String)entry.getKey();
         Object val = entry.getValue();
         properties.put(key, val);
      }

   }

   public Map<String, Object> getProperties()
   {
      return properties;
   }

   public String getTargetAddress()
   {
      return targetAddress;
   }
   
   public <T extends WebServiceFeature> boolean isFeatureEnabled(Class<T> key)
   {
      return features.isFeatureEnabled(key);
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof EndpointInfo))
         return false;
      return toString().equals(obj.toString());
   }

   public int hashCode()
   {
      return toString().hashCode();
   }

   public String toString()
   {
      return "[addr=" + targetAddress + ",props=" + properties + "]";
   }
   
   private String lowerCaseProtocol(String targetAddress)
   {
      int colonIndex = targetAddress.indexOf(':');
      String lowerCasedProtocol = targetAddress.substring(0, colonIndex).toLowerCase();
      return lowerCasedProtocol + targetAddress.substring(colonIndex);
   }
}
