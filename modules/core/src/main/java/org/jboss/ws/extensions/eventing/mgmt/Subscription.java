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

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import javax.xml.bind.JAXBElement;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.jboss.logging.Logger;
import org.jboss.ws.core.soap.SOAPConnectionImpl;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.jaxws.AttributedURIType;
import org.jboss.ws.extensions.eventing.jaxws.EndpointReferenceType;
import org.jboss.ws.Constants;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/**
 * Represents a subscription.
 *
 * @author Heiko Braun, <heiko@openj.net>
 * @since 05-Jan-2006
 */
class Subscription
{
   final private static Logger log = Logger.getLogger(Subscription.class);

   final private EndpointReferenceType notifyTo;
   final private EndpointReferenceType endTo;
   private Date expires;
   final private Filter filter;
   final private EndpointReferenceType endpointReference;
   final private URI eventSourceNS;

   private SubscriptionManagerFactory factory = SubscriptionManagerFactory.getInstance();

   public Subscription(URI eventSourceNS, EndpointReferenceType epr, EndpointReferenceType notifyTo, EndpointReferenceType endTo, Date expires, Filter filter)
   {
      this.eventSourceNS = eventSourceNS;
      this.notifyTo = notifyTo;
      this.endTo = endTo; // is optional, can be null
      this.expires = expires;
      this.filter = filter;
      this.endpointReference = epr;
   }

   public void notify(Element event)
   {
      if(log.isDebugEnabled()) log.debug(getIdentifier() + " dispatching " + event);

      try
      {
         String eventXML = DOMWriter.printNode(event, false);
         MessageFactory msgFactory = MessageFactory.newInstance();

         // notification elements need to declare their namespace locally
         StringBuilder sb = new StringBuilder();
         sb.append("<env:Envelope xmlns:env='"+ Constants.NS_SOAP11_ENV+"' ");
         sb.append("xmlns:wse='").append(EventingConstants.NS_EVENTING).append("' ");
         sb.append("xmlns:wsa='").append(EventingConstants.NS_ADDRESSING).append("'>");
         sb.append("<env:Header>");
         sb.append("<wsa:Action>").append(getNotificationAction()).append("</wsa:Action>");
         // todo: add reference parameters when wildcards are supported
         sb.append("<wsa:To>").append(notifyTo.getAddress().toString()).append("</wsa:To>");
         sb.append("</env:Header>");
         sb.append("<env:Body>");
         sb.append(eventXML);
         sb.append("</env:Body>");
         sb.append("</env:Envelope>");

         SOAPMessage reqMsg = msgFactory.createMessage(null, new ByteArrayInputStream(sb.toString().getBytes()));
         URL epURL = new URL(notifyTo.getAddress().getValue());
         new SOAPConnectionImpl().callOneWay(reqMsg, epURL);
      }
      catch (Exception e)
      {
         SubscriptionManagerMBean manager = factory.getSubscriptionManager();
         AttributedURIType address = this.endTo.getAddress();
         NotificationFailure failure = new NotificationFailure(address.getValue(), event, e);
         manager.addNotificationFailure(failure);
         log.error("Failed to send notification message", e);
      }
   }

   public boolean accepts(Element event)
   {

      boolean b = true;
      if (filter != null)
      {

         try
         {
            XObject o = XPathAPI.eval(event, filter.getExpression());
            b = o.bool();
         }
         catch (TransformerException e)
         {
            log.error("Failed to evalute xpath expression", e);
         }

      }
      return b;
   }

   public void end(String status)
   {
      if (null == endTo) // it's an optional field.
         return;

      if(log.isDebugEnabled()) log.debug("Ending subscription " + getIdentifier());

      StringBuffer sb = new StringBuffer();
      sb.append("<env:Envelope xmlns:env='http://www.w3.org/2003/05/soap-envelope' ");
      sb.append("xmlns:wse='").append(EventingConstants.NS_EVENTING).append("' ");
      sb.append("xmlns:wsa='").append(EventingConstants.NS_ADDRESSING).append("'>");
      sb.append("<env:Header>");
      sb.append("<wsa:Action>").append(EventingConstants.SUBSCRIPTION_END_ACTION).append("</wsa:Action>");
      sb.append("<wsa:To>").append(endTo.getAddress().toString()).append("</wsa:To>");
      sb.append("</env:Header>");
      sb.append("<env:Body>");

      sb.append("<wse:SubscriptionEnd>");
      sb.append("<wse:SubscriptionManager>");
      sb.append("<wsa:Address>");
      sb.append(endpointReference.getAddress().toString());
      sb.append("</wsa:Address>");
      sb.append("<wsa:ReferenceParameters>");
      sb.append("<wse:Identifier>");
      sb.append(getIdentifier().toString());
      sb.append("</wse:Identifier>");
      sb.append("</wsa:ReferenceParameters>");
      sb.append("</wse:SubscriptionManager>");

      sb.append("<wse:Status>").append(status).append("</wse:Status>");
      sb.append("<wse:Reason/>");
      sb.append("</wse:SubscriptionEnd>");

      sb.append("</env:Body>");
      sb.append("</env:Envelope>");

      try
      {
         MessageFactory msgFactory = MessageFactory.newInstance();
         SOAPMessage reqMsg = msgFactory.createMessage(null, new ByteArrayInputStream(sb.toString().getBytes()));
         URL epURL = new URL(endTo.getAddress().getValue());
         new SOAPConnectionImpl().callOneWay(reqMsg, epURL);
      }
      catch (Exception e)
      {
         log.warn("Failed to send subscription end message to: " + this.endTo + "("+e.getMessage()+")");
      }

   }

   private String getNotificationAction()
   {
      return this.eventSourceNS.toString() + "/Notification";
   }

   public boolean isExpired()
   {
      return System.currentTimeMillis() > expires.getTime();
   }

   public EndpointReferenceType getNotifyTo()
   {
      return notifyTo;
   }

   public EndpointReferenceType getEndTo()
   {
      return endTo;
   }

   public Date getExpires()
   {
      return expires;
   }

   public Filter getFilter()
   {
      return filter;
   }

   public EndpointReferenceType getEndpointReferenceType()
   {
      return endpointReference;
   }

   public URI getIdentifier()
   {
      try {
         JAXBElement<String> jaxbElement = (JAXBElement<String>)endpointReference.getReferenceParameters().getAny().get(0);
         return new URI(jaxbElement.getValue());
      } catch (URISyntaxException e) {
         throw new RuntimeException(e);
      }
   }

   public void setExpires(Date expires)
   {
      this.expires = expires;
   }
}
