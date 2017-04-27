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
package org.jboss.test.ws.jaxws.wseventing;

import java.net.URI;
import java.util.Date;

import javax.xml.namespace.QName;

import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.deployment.EventingEndpointDeployment;
import org.jboss.ws.extensions.eventing.jaxws.AttributedURIType;
import org.jboss.ws.extensions.eventing.jaxws.EndpointReferenceType;
import org.jboss.ws.extensions.eventing.mgmt.Filter;
import org.jboss.ws.extensions.eventing.mgmt.SubscriptionManager;
import org.jboss.ws.extensions.eventing.mgmt.SubscriptionTicket;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 * SubscriptionManagerEndpoint unit tests.
 * 
 * @author Heiko Braun, <heiko@openj.net>
 * @since 02-Dec-2005
 */
public class SubscriptionManagerTestCase extends JBossWSTest
{

   private SubscriptionManager subscriptionManager;
   private URI eventSourceNS;
   private EndpointReferenceType eventSinkEndpoint;

   private String eventString =
      "<WindReport type='critical'>\n" +
      "    <Date>030701</Date>\n" +
      "    <Time>0041</Time>\n" +
      "    <Speed>65</Speed>\n" +
      "    <Location>BRADENTON BEACH</Location>\n" +
      "    <County>MANATEE</County>\n" +
      "    <State>FL</State>\n" +
      "    <Lat>2746</Lat>\n" +
      "    <Long>8270</Long>\n" +
      "    <Comments xml:lang='en-US' >\n" +
      "        WINDS 55 WITH GUSTS TO 65. ROOF TORN OFF BOAT HOUSE. REPORTED\n" +
      "        BY STORM SPOTTER. (TBW)\n" +
      "    </Comments>\n" +
      "</WindReport>";

   /**
    * see http://www.w3.org/TR/xpath
    */
   private String filterExpr = "/WindReport/State/text()='FL'";

   protected void setUp() throws Exception {
      super.setUp();
      init();
   }

   private void init()
   {
      try
      {
         EventingEndpointDeployment deploymentInfo = new EventingEndpointDeployment("http://schemas.xmlsoap.org/ws/2004/08/eventing/Warnings", null, null);
         deploymentInfo.setEndpointAddress("http://localhost:8080/jaxws-wseventing/manage");
         deploymentInfo.setPortName(new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing","SubscriptionManagerPort"));

         subscriptionManager = new SubscriptionManager();
         subscriptionManager.registerEventSource(deploymentInfo);
         subscriptionManager.registerEventSource(deploymentInfo);
         subscriptionManager.start();

         eventSinkEndpoint = new EndpointReferenceType();
         AttributedURIType attrURI = new AttributedURIType();
         attrURI.setValue("http://www.other.example.com/OnStormWarning");
         eventSinkEndpoint.setAddress(attrURI);

         eventSourceNS = new URI("http://schemas.xmlsoap.org/ws/2004/08/eventing/Warnings");

      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException(e.getMessage());
      }
   }

   public void testSimpleSubscription() throws Exception
   {
      SubscriptionTicket subscriptionTicket = subscriptionManager.subscribe(
            eventSourceNS, eventSinkEndpoint, eventSinkEndpoint, null, null
      );

      assertNotNull(subscriptionTicket.getIdentifier());
      assertNotNull(subscriptionTicket.getExpires());
      assertTrue(subscriptionTicket.getExpires().getTime() > System.currentTimeMillis());
      assertNotNull(subscriptionTicket.getSubscriptionManager());
   }

   /**
    * If the expiration time is either a zero duration or a specific time that occurs in
    * the past according to the event source, then the request MUST fail,
    * and the event source MAY generate a wse:InvalidExpirationTime fault indicating
    * that an invalid expiration time was requested.
    */

   public void testInvalidLeasetime() throws Exception
   {
      try
      {
         Date expires = new Date(System.currentTimeMillis() - 5000);
         subscriptionManager.subscribe(eventSourceNS, eventSinkEndpoint, eventSinkEndpoint, expires, null);
         fail("Expiration time was invalid");
      }
      catch (Exception e)
      {
         // ignore expected exception
      }
   }

   public void testExceedsMaxLeaseTime() throws Exception
   {
      try
      {
         Date expires = new Date(System.currentTimeMillis() + EventingConstants.MAX_LEASE_TIME + 5000);
         subscriptionManager.subscribe(eventSourceNS, eventSinkEndpoint, eventSinkEndpoint, expires, null);
         fail("Expiration time exceeds lease limit");
      }
      catch (Exception e)
      {
         // ignore expected exception
      }
   }

   /**
    * If the expression evaluates to false for a notification,
    * the notification MUST NOT be sent to the event sink.
    * Implied value is an expression that always returns true.
    * If the event source does not support filtering, then a request that specifies a filter MUST fail,
    * and the event source MAY generate a wse:FilteringNotSupported fault indicating that filtering is not supported.
    * If the event source supports filtering but cannot honor the requested filtering,
    * the request MUST fail, and the event source MAY generate a wse:FilteringRequestedUnavailable
    * fault indicating that the requested filter dialect is not supported.
    */

   public void testFilterConstraints() throws Exception
   {
      try
      {
         Filter filter = new Filter(new URI("http://example.org/unkownFilter"), filterExpr);
         Date expires = new Date(System.currentTimeMillis() + 5000);
         subscriptionManager.subscribe(eventSourceNS, eventSinkEndpoint, eventSinkEndpoint, expires, filter);
         fail("Filtering should not be available");
      }
      catch (Exception e)
      {
         // ignore expected exception
      }
   }

   public void testDispatch() throws Exception
   {
      for (int i = 0; i < 5; i++)
      {
         Date expires = new Date(System.currentTimeMillis() + 5000);
         subscriptionManager.subscribe(eventSourceNS, eventSinkEndpoint, eventSinkEndpoint, expires, null);
      }

      Element payload = DOMUtils.parse(eventString);
      subscriptionManager.dispatch(eventSourceNS, payload);
   }

   public void testXPathFilter() throws Exception
   {

      Date expires = new Date(System.currentTimeMillis() + 5000);
      Filter filter = new Filter(new URI("http://www.w3.org/TR/1999/REC-xpath-19991116"), filterExpr);
      subscriptionManager.subscribe(eventSourceNS, eventSinkEndpoint, eventSinkEndpoint, expires, filter);

      Element event = DOMUtils.parse(eventString);
      subscriptionManager.dispatch(eventSourceNS, event);
   }
}
