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
package org.jboss.ws.tools.metadata;

import org.jboss.ws.metadata.config.ConfigurationProvider;
import org.jboss.ws.metadata.umdm.EndpointConfigMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;

import javax.xml.namespace.QName;

/**
 *  Tools Endpoint Metadata
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Oct 6, 2005
 */
public class ToolsEndpointMetaData extends EndpointMetaData
{
   public String typeNamespace;
   private String endpointAddress;
   private String documentation;

   public ToolsEndpointMetaData(ServiceMetaData service, QName portName, QName portTypeName)
   {
      super(service, portName, portTypeName, Type.JAXRPC);
     
      EndpointConfigMetaData ecmd = getEndpointConfigMetaData();
      ecmd.setConfigFile(ConfigurationProvider.DEFAULT_JAXRPC_CLIENT_CONFIG_FILE);
      ecmd.setConfigName(ConfigurationProvider.DEFAULT_CLIENT_CONFIG_NAME);
   }
   
   public String getEndpointAddress()
   {
      return endpointAddress;
   }

   public void setEndpointAddress(String endpointAddress)
   {
      this.endpointAddress = endpointAddress;
   }
}
