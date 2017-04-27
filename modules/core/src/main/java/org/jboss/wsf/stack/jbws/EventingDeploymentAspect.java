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
package org.jboss.wsf.stack.jbws;

import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.deployment.EventingEndpointDeployment;
import org.jboss.ws.extensions.eventing.metadata.EventingEpMetaExt;
import org.jboss.ws.extensions.eventing.mgmt.SubscriptionManagerFactory;
import org.jboss.ws.extensions.eventing.mgmt.SubscriptionManagerMBean;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.wsf.spi.deployment.DeploymentAspect;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.deployment.Endpoint;

/**
 * A deployer that creates event sources and register them with the 
 * subscripion manager when a service endpoint is created. 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 25-Apr-2007
 */
public class EventingDeploymentAspect extends DeploymentAspect
{
   @Override
   public void start(Deployment dep)
   {
      for (Endpoint ep : dep.getService().getEndpoints())
      {
         ServerEndpointMetaData sepMetaData = ep.getAttachment(ServerEndpointMetaData.class);
         if (sepMetaData == null)
            throw new IllegalStateException("Cannot obtain endpoint meta data");

         EventingEpMetaExt ext = (EventingEpMetaExt)sepMetaData.getExtension(EventingConstants.NS_EVENTING);
         if (ext != null)
         {
            // Currently several endpoints may belong to an event source deployment.
            // Therefore we have to avoid duplicate registrations
            // Actually there should be a 1:n mapping of event source NS to endpoints.
            // See also http://jira.jboss.org/jira/browse/JBWS-770

            // create pending incomplete event source
            EventingEndpointDeployment desc = new EventingEndpointDeployment(ext.getEventSourceNS(), ext.getNotificationSchema(), ext.getNotificationRootElementNS());
            desc.setEndpointAddress(sepMetaData.getEndpointAddress());
            desc.setPortName(sepMetaData.getPortName());

            SubscriptionManagerFactory factory = SubscriptionManagerFactory.getInstance();
            SubscriptionManagerMBean manager = factory.getSubscriptionManager();
            manager.registerEventSource(desc);
         }
      }
   }

   @Override
   public void destroy(Deployment dep)
   {
      for (Endpoint ep : dep.getService().getEndpoints())
      {
         ServerEndpointMetaData sepMetaData = ep.getAttachment(ServerEndpointMetaData.class);
         if (sepMetaData == null)
            throw new IllegalStateException("Cannot obtain endpoint meta data");

         EventingEpMetaExt ext = (EventingEpMetaExt)sepMetaData.getExtension(EventingConstants.NS_EVENTING);
         if (ext != null)
         {
            SubscriptionManagerFactory factory = SubscriptionManagerFactory.getInstance();
            SubscriptionManagerMBean manager = factory.getSubscriptionManager();
            manager.removeEventSource(ext.getEventSourceURI());
         }
      }
   }
}
