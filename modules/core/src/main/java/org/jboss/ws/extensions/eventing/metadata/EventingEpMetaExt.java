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
package org.jboss.ws.extensions.eventing.metadata;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.ws.metadata.umdm.MetaDataExtension;

/**
 * Eventing specific endpoint meta data extensions.
 *
 * @author Heiko Braun, <heiko@openj.net>
 * @since 21-Mar-2006
 */
public class EventingEpMetaExt extends MetaDataExtension {

   private boolean isEventSource = true;
   private String eventSourceNS;

   private String[] notificationSchema;
   private String notificationRootElementNS;

   public EventingEpMetaExt(String extensionNameSpace) {
      super(extensionNameSpace);
   }

   public boolean isEventSource() {
      return isEventSource;
   }

   public void setEventSource(boolean eventSource) {
      isEventSource = eventSource;
   }

   public String getEventSourceNS() {
      return eventSourceNS;
   }

   public void setEventSourceNS(String eventSourceNS) {
      this.eventSourceNS = eventSourceNS;
   }

   public URI getEventSourceURI()
   {
      try
      {
         return new URI(eventSourceNS);
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException("Illegal event source URI: " + eventSourceNS);
      }
   }

   public String[] getNotificationSchema() {
      return this.notificationSchema;
   }

   public void setNotificationSchema(String[] notificationSchema) {
      this.notificationSchema = notificationSchema;
   }

   public String getNotificationRootElementNS() {
      return notificationRootElementNS;
   }

   public void setNotificationRootElementNS(String notificationRootElementNS) {
      this.notificationRootElementNS = notificationRootElementNS;
   }
}
