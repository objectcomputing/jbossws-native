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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.spi.ObjectFactory;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedPortComponentRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedServiceRefMetaData;

/**
 * This ServiceObjectFactory reconstructs a service for a given WSDL when the webservice client does a JNDI lookup
 * <p/>
 * It uses the information provided by the service-ref element in application-client.xml
 *
 * @author Thomas.Diesler@jboss.org
 * @since 15-April-2004
 */
public abstract class ServiceObjectFactory implements ObjectFactory
{
   // provide logging
   private static final Logger log = Logger.getLogger(ServiceObjectFactory.class);

   /**
    * Narrow available endpoints by <port-component-ref> declarations. 
    * Service.getPort(SEI) must be able to retrieve a distinct port definition.
    */
   protected void narrowPortSelection(UnifiedServiceRefMetaData serviceRef, ServiceMetaData serviceMetaData)
   {
      if (serviceMetaData.getEndpoints().size() > 1)
      {
         Map<String, UnifiedPortComponentRefMetaData> pcrefs = new HashMap<String, UnifiedPortComponentRefMetaData>();
         for (UnifiedPortComponentRefMetaData pcref : serviceRef.getPortComponentRefs())
         {
            String seiName = pcref.getServiceEndpointInterface();

            // Constraint#1: within a service-ref it's not allowed to use a SEI across different pcref's
            if (pcrefs.get(seiName) != null)
               throw new WSException("Within a <service-ref> it's not allowed to use a SEI across different <port-component-ref>'s: " + seiName);
            
            pcrefs.put(seiName, pcref);
         }

         // Constraint#2: A pcref may only match one EndpointMetaData
         for (String sei : pcrefs.keySet())
         {
            // Narrow available endpoints by port-component-ref declaration
            List<QName> narrowedEndpoints = new ArrayList<QName>();

            UnifiedPortComponentRefMetaData pcref = pcrefs.get(sei);

            // Constraint#3: Port selection only applies when both SEI and QName are given
            if (pcref.getServiceEndpointInterface() != null && pcref.getPortQName() != null)
            {
               List<QName> pcRef2EndpointMapping = new ArrayList<QName>();
               for (EndpointMetaData epMetaData : serviceMetaData.getEndpoints())
               {
                  if (pcref.getServiceEndpointInterface().equals(epMetaData.getServiceEndpointInterfaceName()))
                  {
                     pcRef2EndpointMapping.add(epMetaData.getPortName());
                  }
               }

               for (QName q : pcRef2EndpointMapping)
               {
                  EndpointMetaData mappedEndpoint = serviceMetaData.getEndpoint(q);
                  if (!pcref.getPortQName().equals(mappedEndpoint.getPortName()))
                     narrowedEndpoints.add(q);
               }

               // Constraint: Dont exclude all of them ;)
               if (pcRef2EndpointMapping.size() > 0 && (pcRef2EndpointMapping.size() == narrowedEndpoints.size()))
                  throw new WSException("Failed to narrow available endpoints by <port-component-ref> declaration");

               for (QName q : narrowedEndpoints)
               {
                  EndpointMetaData removed = serviceMetaData.removeEndpoint(q);
                  log.debug("Narrowed endpoint " + q + "(" + removed + ")");
               }
            }
            else
            {
               // TODO: In case there is more then one EMPD this should cause an exception
               log.warn("Unable to narrow port selection for " + pcref);
            }
         }
      }
   }
}
