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
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.w3c.dom.Element;

/**
 * Event dispatch job implementation.
 */
public final class DispatchJob implements Runnable
{
   public Element event;
   public URI eventSourceNS;
   public ConcurrentMap<URI, List<Subscription>> mapping;

   public DispatchJob(URI eventSourceNS, Element event, ConcurrentMap<URI, List<Subscription>> mapping)
   {
      this.event = event;
      this.eventSourceNS = eventSourceNS;
      this.mapping = mapping;
   }

   public void run()
   {
      List<Subscription> subscriptions = mapping.get(eventSourceNS);
      for (Subscription s : subscriptions) // iterator is a snapshot
      {
         if (s.accepts(event) && !s.isExpired())
            s.notify(event);
      }
   }

   public String toString()
   {
      return "DispatchJob {" + "source=" + eventSourceNS + ", event=" + event + "}";
   }
}
