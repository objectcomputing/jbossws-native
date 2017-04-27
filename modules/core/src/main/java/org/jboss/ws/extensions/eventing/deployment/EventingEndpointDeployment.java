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
package org.jboss.ws.extensions.eventing.deployment;

import javax.xml.namespace.QName;

/**
 * Eventsource endpoint deployment info.
 *
 * @author Heiko Braun, <heiko@openj.net>
 * @since 18-Jan-2006
 */
public class EventingEndpointDeployment
{
   /* event source URI */
   private String name;

   private QName portName;

   // event source endpoint address
   private String endpointAddress;

   /* notification schema */
   private String[] schema;

   private String notificationRootElementNS;

   public EventingEndpointDeployment(String name, String[] schema, String notificationRootElementNS)
   {
      this.name = name;
      this.schema = schema;
      this.notificationRootElementNS = notificationRootElementNS;
   }

   public QName getPortName()
   {
      return portName;
   }

   public void setPortName(QName portName)
   {
      this.portName = portName;
   }

   public String getName()
   {
      return name;
   }

   public String[] getSchema()
   {
      return schema;
   }

   public String getEndpointAddress()
   {
      return endpointAddress;
   }

   public void setEndpointAddress(String endpointAddress)
   {
      this.endpointAddress = endpointAddress;
   }

   public String getNotificationRootElementNS()
   {
      return notificationRootElementNS;
   }

   public void setNotificationRootElementNS(String notificationRootElementNS)
   {
      this.notificationRootElementNS = notificationRootElementNS;
   }

}
