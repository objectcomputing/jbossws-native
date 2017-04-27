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
package org.jboss.ws.extensions.eventing.mgmt;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.ws.WSException;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.deployment.EventingEndpointDeployment;

/**
 * @author Heiko Braun, <heiko@openj.net>
 * @since 24-Jan-2006
 */
public class EventingBuilder
{

   private EventingBuilder()
   {
   }

   public static EventingBuilder createEventingBuilder()
   {
      return new EventingBuilder();
   }

   public EventSource newEventSource(EventingEndpointDeployment desc)
   {
      URI eventSourceNS = newEventSourceURI(desc.getName());
      EventSource eventSource = new EventSource(desc.getName(), eventSourceNS, desc.getSchema(), desc.getNotificationRootElementNS());
      eventSource.getSupportedFilterDialects().add(EventingConstants.getDefaultFilterDialect());
      return eventSource;
   }

   public URI newEventSourceURI(String name)
   {
      try
      {
         return new URI(name);
      }
      catch (URISyntaxException e)
      {
         throw new WSException("Failed to create eventsource URI: " + e.getMessage());
      }
   }

}
