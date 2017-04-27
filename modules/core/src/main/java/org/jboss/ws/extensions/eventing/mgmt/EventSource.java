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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun, <heiko@openj.net>
 * @since 02-Dec-2005
 */
class EventSource implements java.io.Serializable
{
   enum State
   {
      CREATED, STARTED, STOPPED, DESTROYED
   }

   private State state;
   private long maxExpirationTime = -1;

   private String name;
   private URI nameSpace;

   private URI managerAddress;
   private List<URI> supportedFilter = new ArrayList<URI>();

   private String[] notificationSchema;
   private String notificationRootElementNS;

   public EventSource(String name, URI nameSpace) {
      this.name = name;
      this.nameSpace = nameSpace;
      this.state = State.CREATED;
   }

   public EventSource(String name, URI nameSpace, String[] schema, String notificationRootElementNS)
   {
      this.name = name;
      this.nameSpace = nameSpace;
      this.notificationSchema = schema;
      this.notificationRootElementNS = notificationRootElementNS;
      this.state = State.CREATED;
   }

   State getState()
   {
      return state;
   }

   void setState(State state)
   {
      if(state == EventSource.State.STARTED)
      {
         assertConfiguration();
      }
      this.state = state;
   }

   private void assertConfiguration() {
      if(this.getManagerAddress() == null)
         throw new IllegalArgumentException("SubscriptionManager address unknown. Unable to start event source.");
   }

   long getMaxExpirationTime()
   {
      return maxExpirationTime;
   }

   void setMaxExpirationTime(long maxExpirationTime)
   {
      this.maxExpirationTime = maxExpirationTime;
   }

   public List<URI> getSupportedFilterDialects()
   {
      return supportedFilter;
   }

   public String[] getNotificationSchema() {
      return notificationSchema;
   }

   public String getName() {
      return name;
   }

   public URI getNameSpace() {
      return nameSpace;
   }

   public URI getManagerAddress() {
      return managerAddress;
   }

   public String getNotificationRootElementNS() {
      return notificationRootElementNS;
   }

   public void setManagerAddress(String managerAddress) {
      try
      {
         if(managerAddress!=null)
            this.managerAddress = new URI(managerAddress);
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException("Illegal subscription manager endpoint address: " + e.getMessage());
      }
   }
   public String toString()
   {
      return "EventSource {" + "nameSpace=" + nameSpace + ", state=" + state + "}";
   }
}
