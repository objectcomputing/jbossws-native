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
package org.jboss.test.ws.jaxws.samples.wseventing;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.addressing.AddressingProperties;
import javax.naming.InitialContext;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.ws.extensions.addressing.AddressingClientUtil;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.mgmt.EventDispatcher;
import org.jboss.ws.extensions.eventing.jaxws.DeliveryType;
import org.jboss.ws.extensions.eventing.jaxws.EventSourceEndpoint;
import org.jboss.ws.extensions.eventing.jaxws.FilterType;
import org.jboss.ws.extensions.eventing.jaxws.GetStatus;
import org.jboss.ws.extensions.eventing.jaxws.Subscribe;
import org.jboss.ws.extensions.eventing.jaxws.SubscribeResponse;
import org.jboss.ws.extensions.eventing.jaxws.SubscriptionManagerEndpoint;
import org.jboss.ws.extensions.eventing.jaxws.Unsubscribe;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 * Test the eventing example service.
 *
 * @author heiko@openj.net
 * @since 29-Apr-2005
 */
public class SysmonTestCase extends JBossWSTest
{

   // event source endpoint
   private EventSourceEndpoint subscriptionPort = null;

   // subscription manager endpoint
   private SubscriptionManagerEndpoint managementPort = null;

   // the logical event source name
   private final static String eventSourceURI = "http://www.jboss.org/sysmon/SystemInfo";

   public static Test suite()
   {
      return new JBossWSTestSetup(SysmonTestCase.class, "jaxws-samples-wseventing.war, jaxws-samples-wseventing-sink.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      if (subscriptionPort == null || managementPort == null)
      {
         URL wsdlURL = getResourceURL("jaxws/samples/wseventing/WEB-INF/wsdl/sysmon.wsdl");
         QName defaultServiceName = new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing", "EventingService");

         Service service = Service.create(wsdlURL, defaultServiceName);
         subscriptionPort = (EventSourceEndpoint)service.getPort(EventSourceEndpoint.class);
         managementPort = (SubscriptionManagerEndpoint)service.getPort(SubscriptionManagerEndpoint.class);

         ((StubExt)subscriptionPort).setConfigName("Standard WSAddressing Client");
         ((StubExt)managementPort).setConfigName("Standard WSAddressing Client");
      }
   }

   /**
    * Subscribe using a custom notification filter.
    */
   public void testSubscribe() throws Exception
   {
      SubscribeResponse subscribeResponse = doSubscribe("/SystemStatus/HostName/text()='localhost'");
      SysmonUtil.printSubscriptionDetails(subscribeResponse);
   }

   /**
    * Cancel subscription and check if it was really removed.
    */
   public void testUnsubscribe() throws Exception
   {
      SubscribeResponse subscribeResponse = doSubscribe(null);
      //SysmonUtil.printSubscriptionDetails(subscribeResponse);

      // addressing correlation
      AddressingProperties unsubscribeProps = SysmonUtil.buildFollowupProperties(subscribeResponse, EventingConstants.UNSUBSCRIBE_ACTION, eventSourceURI);
      SysmonUtil.setRequestProperties((BindingProvider)managementPort, unsubscribeProps);
      managementPort.unsubscribeOp(new Unsubscribe());

      AddressingProperties getStatusProps = SysmonUtil.buildFollowupProperties(subscribeResponse, EventingConstants.GET_STATUS_ACTION, eventSourceURI);
      SysmonUtil.setRequestProperties((BindingProvider)managementPort, getStatusProps);

      try
      {
         managementPort.getStatusOp(new GetStatus());
      }
      catch (Exception e)
      {
         // Ignore
         return;
      }

      fail("Unsubscribe error! The subscription was not removed");
   }

   private SubscribeResponse doSubscribe(String filterString) throws URISyntaxException
   {
      AddressingProperties reqProps = AddressingClientUtil.createDefaultProps(EventingConstants.SUBSCRIBE_ACTION, eventSourceURI);
      reqProps.setMessageID(AddressingClientUtil.createMessageID());
      SysmonUtil.setRequestProperties((BindingProvider)subscriptionPort, reqProps);

      // build subscription payload
      Subscribe request = new Subscribe();

      // default delivery type with notification EPR
      DeliveryType delivery = SysmonUtil.getDefaultDelivery();
      request.setDelivery(delivery);

      // receive endTo messages at the same port
      request.setEndTo(delivery.getNotifyTo());

      if (filterString != null)
      {
         // custom filter that applies to a certain hostname only
         FilterType filter = SysmonUtil.wrapFilterString(filterString);
      }

      // invoke subscription request
      SubscribeResponse subscriptionTicket = subscriptionPort.subscribeOp(request);

      // check message constraints
      AddressingProperties resProps = SysmonUtil.getResponseProperties((BindingProvider)subscriptionPort);
      assertEquals(reqProps.getMessageID().getURI(), resProps.getRelatesTo()[0].getID());

      return subscriptionTicket;
   }

   public void testNotification() throws Exception {

      SubscribeResponse response = doSubscribe("/SystemStatus/HostName/text()='localhost'");
      assertNotNull(response);

      String notification =
        "<sys:newNotification xmlns:sys=\"http://www.jboss.org/sysmon\">" +        
        "         <arg0>" +
        "            <activeThreadCount>12</activeThreadCount>" +
        "            <freeMemory>60000</freeMemory>" +
        "            <hostAddress>localhost</hostAddress>" +
        "            <hostname>bigben</hostname>" +
        "            <maxMemory>120000</maxMemory>" +
        "            <time>2001-10-26T21:32:52</time>" +
        "         </arg0>" +
        "      </sys:newNotification>";

      Element payload = DOMUtils.parse(notification);
      try
      {
         InitialContext iniCtx = getInitialContext();
         EventDispatcher delegate = (EventDispatcher)
               iniCtx.lookup(EventingConstants.DISPATCHER_JNDI_NAME);
         delegate.dispatch(new URI("http://www.jboss.org/sysmon/SystemInfo"), payload);
         Thread.sleep(3000);
      }
      catch (Exception e)
      {
         throw e;
      }
   }
}
