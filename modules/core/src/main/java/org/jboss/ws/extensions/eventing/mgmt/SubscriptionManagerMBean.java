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
import java.util.Date;
import java.util.List;

import javax.management.ObjectName;

import org.jboss.ws.extensions.eventing.deployment.EventingEndpointDeployment;
import org.jboss.ws.extensions.eventing.jaxws.EndpointReferenceType;
import org.jboss.wsf.common.ObjectNameFactory;
import org.w3c.dom.Element;

/**
 * @author Heiko Braun, <heiko@openj.net>
 * @since 12-Dec-2005
 */
public interface SubscriptionManagerMBean
{
   static final ObjectName OBJECT_NAME = ObjectNameFactory.create("jboss.ws:service=SubscriptionManager,module=eventing");

   static final String BEAN_NAME = "WSSubscriptionManager";

   String getBindAddress();

   void setBindAddress(String bindAddress);
   
   /**
    * Returns the core number of threads.
    */
   int getCorePoolSize();

   /**
    * Returns the maximum allowed number of threads.
    */
   int getMaximumPoolSize();

   /**
    * Returns the largest number of threads that have ever simultaneously been in the pool.
    */
   int getLargestPoolSize();

   /**
    * Returns the approximate number of threads that are actively executing tasks.
    */
   int getActiveCount();

   /**
    * Returns the approximate total number of tasks that have completed execution.
    */
   long getCompletedTaskCount();

   public void setCorePoolSize(int corePoolSize);

   public void setMaxPoolSize(int maxPoolSize);

   public void setEventKeepAlive(long millies);

   // subscription EndpointReferenceType business
   SubscriptionTicket subscribe(URI eventSourceNS, EndpointReferenceType notifyTo, EndpointReferenceType endTo, Date expires, Filter filter) throws SubscriptionError;

   Date renew(URI identifier, Date lease) throws SubscriptionError;

   Date getStatus(URI identifier) throws SubscriptionError;

   void unsubscribe(URI identifier) throws SubscriptionError;

   // notification API
   void dispatch(URI eventSourceNS, Element payload);

   void registerEventSource(EventingEndpointDeployment deploymentInfo);

   void removeEventSource(URI eventSourceNS);

   String showSubscriptionTable();

   String showEventsourceTable();

   public void addNotificationFailure(NotificationFailure failure);

   public List<NotificationFailure> showNotificationFailures();

   public boolean isValidateNotifications();

   public void setValidateNotifications(boolean validateNotifications);
}
