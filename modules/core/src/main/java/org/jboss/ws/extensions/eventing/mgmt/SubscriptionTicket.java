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

import javax.xml.bind.JAXBElement;

import org.jboss.ws.extensions.eventing.jaxws.EndpointReferenceType;

/**
 * @author Heiko Braun, <heiko@openj.net>
 * @since 02-Dec-2005
 */
public final class SubscriptionTicket
{
   private URI identifier;
   private EndpointReferenceType subscriptionManager;
   private Date expires;

   public SubscriptionTicket(EndpointReferenceType subscriptionManager, Date expires)
   {
      try {
         JAXBElement<String> jaxbElement = (JAXBElement<String>)subscriptionManager.getReferenceParameters().getAny().get(0);
         this.identifier = new URI(jaxbElement.getValue());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
      this.subscriptionManager = subscriptionManager;
      this.expires = expires;
   }

   public URI getIdentifier()
   {
      return identifier;
   }

   public EndpointReferenceType getSubscriptionManager()
   {
      return subscriptionManager;
   }

   public Date getExpires()
   {
      return expires;
   }
}
