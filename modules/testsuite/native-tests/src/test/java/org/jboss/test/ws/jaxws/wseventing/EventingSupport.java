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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.EndpointReference;
import javax.xml.ws.addressing.JAXWSAConstants;

import org.jboss.ws.core.StubExt;
import org.jboss.ws.core.soap.SOAPElementImpl;
import org.jboss.ws.extensions.addressing.AddressingClientUtil;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.jaxws.AttributedURIType;
import org.jboss.ws.extensions.eventing.jaxws.DeliveryType;
import org.jboss.ws.extensions.eventing.jaxws.EndpointReferenceType;
import org.jboss.ws.extensions.eventing.jaxws.EventSourceEndpoint;
import org.jboss.ws.extensions.eventing.jaxws.FilterType;
import org.jboss.ws.extensions.eventing.jaxws.Subscribe;
import org.jboss.ws.extensions.eventing.jaxws.SubscribeResponse;
import org.jboss.ws.extensions.eventing.jaxws.SubscriptionManagerEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Eventing test case support.
 *
 * @author Heiko Braun, <heiko@openj.net>
 * @since 17-Jan-2006
 */
public class EventingSupport extends JBossWSTest
{

   static EventSourceEndpoint eventSourcePort;

   static SubscriptionManagerEndpoint managerPort;

   static URI eventSourceURI = null;

   private static int msgId = 0;

   static URL wsdlLocation;

   static String eventString =
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
      "        Should be a REMOTE notification\n" +
      "    </Comments>\n" +
      "</WindReport>";

   protected static AddressingBuilder addrBuilder = AddressingBuilder.getAddressingBuilder();

   // an event source name is build from the TNS+PortType name
   private static final String EVENT_SOURCE_NAME = "http://www.jboss.org/wind/Warnings";

   protected void setUp() throws Exception
   {
      if (eventSourcePort == null)
      {
         eventSourceURI = new URI(EVENT_SOURCE_NAME);
         wsdlLocation = new URL("http://" + getServerHost() + ":8080/jaxws-wseventing/subscribe?wsdl");

         Service service = Service.create(wsdlLocation, new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing","EventingService"));
         eventSourcePort = (EventSourceEndpoint)service.getPort(EventSourceEndpoint.class);
         managerPort = (SubscriptionManagerEndpoint)service.getPort(SubscriptionManagerEndpoint.class);

         ((StubExt)eventSourcePort).setConfigName("Standard WSAddressing Client");
         ((StubExt)managerPort).setConfigName("Standard WSAddressing Client");
      }
   }

   protected void tearDown() throws Exception
   {

      super.tearDown();

      // remove event source
      /*ObjectName oname = EventingConstants.getSubscriptionManagerName();
       getServer().invoke(oname, "removeEventSource", new Object[] { new URI(eventSourceURI) }, new String[] { "java.net.URI" });
       */
   }

   // ----------------------------------------------------------------------------
   protected SubscribeResponse doSubscribe()
   {

      try
      {
         // append message correlation headers
         AddressingProperties requestProps = AddressingClientUtil.createDefaultProps(
               EventingConstants.SUBSCRIBE_ACTION, eventSourceURI.toString()
         );
         requestProps.setMessageID(AddressingClientUtil.createMessageID());
         setRequestProperties((BindingProvider) eventSourcePort, requestProps);

         // subscription
         Subscribe request = new Subscribe();

         DeliveryType delivery = getDefaultDelivery();
         request.setDelivery(delivery);
         request.setEndTo(delivery.getNotifyTo());
         request.setFilter(getDefaultFilter());
         //request.setExpires(new Date());

         SubscribeResponse subscribeResponse = eventSourcePort.subscribeOp(request);
         assertNotNull(subscribeResponse);

         AddressingProperties responseProps = getResponseProperties((BindingProvider)eventSourcePort);
         assertEquals(EventingConstants.SUBSCRIBE_RESPONSE_ACTION, responseProps.getAction().getURI().toString());

         return subscribeResponse;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to create subscription: ", e);
      }

   }

   protected DeliveryType getDefaultDelivery()
   {
      try
      {
         DeliveryType delivery = new DeliveryType();
         delivery.setMode(EventingConstants.getDeliveryPush().toString());
         EndpointReferenceType notifyEPR = new EndpointReferenceType();
         AttributedURIType attURI = new AttributedURIType();
         attURI.setValue("http://" + getServerHost() + ":8080/jaxws-wseventing/eventSink");
         notifyEPR.setAddress(attURI);                        
         delivery.setNotifyTo(notifyEPR);
         return delivery;
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Failed to create delivery payload", e);
      }
   }

   protected FilterType getDefaultFilter()
   {
      FilterType filter = new FilterType();
      filter.setDialect(EventingConstants.getDialectXPath().toString());
      filter.getContent().add("/WindReport/State/text()='FL'");
      return filter;
   }

   protected void assertWSDLAccess() throws MalformedURLException
   {
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlLocation);
      assertNotNull(wsdlDefinitions);
   }

   public static void setRequestProperties(BindingProvider provider, AddressingProperties props) {
      provider.getRequestContext().put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, props);
   }

   protected AddressingProperties getResponseProperties(BindingProvider provider) {
      return (AddressingProperties)provider.getResponseContext().get(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_INBOUND);
   }

   public static AddressingProperties buildFollowupProperties(SubscribeResponse response, String wsaAction, String wsaTo) {
      try
      {
         AddressingProperties followupProps = addrBuilder.newAddressingProperties();

         //followupProps.setTo(addrBuilder.newURI(wsaTo));
         followupProps.setAction(addrBuilder.newURI(wsaAction));
         followupProps.setMessageID(AddressingClientUtil.createMessageID());

         // necessary in order to parse ref params
         EndpointReference epr = addrBuilder.newEndpointReference(new URI(wsaTo));

         String subscriptionID = response.getSubscriptionId();
         SOAPElementImpl element = new SOAPElementImpl(
            "Identifier",
            "wse", "http://schemas.xmlsoap.org/ws/2004/08/eventing"
         );
         element.setValue(subscriptionID);
         epr.getReferenceParameters().addElement(element);

         followupProps.initializeAsDestination(epr);

         return followupProps;
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }
}
