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
package org.jboss.test.ws.jaxws.samples.jmstransport;

import java.net.URL;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.jboss.wsf.common.DOMUtils;

import junit.framework.Test;

/**
 * A web service client that connects to a MDB endpoint.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 26-Apr-2004
 */
public class JMSTransportTestCase extends JBossWSTest
{
   private static boolean waitForResponse;
   
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JMSTransportTestCase.class, "jaxws-samples-jmstransport.sar");
   }

   public void testJMSEndpointPort() throws Exception
   {
      URL wsdlURL = getResourceURL("jaxws/samples/jmstransport/jmsservice.wsdl");
      QName serviceName = new QName("http://org.jboss.ws/samples/jmstransport", "OrganizationJMSEndpointService");
      QName portName = new QName("http://org.jboss.ws/samples/jmstransport", "JMSEndpointPort");
      
      Service service = Service.create(wsdlURL, serviceName);
      Organization port = service.getPort(portName, Organization.class);
      
      String res = port.getContactInfo("mafia");
      assertEquals("The 'mafia' boss is currently out of office, please call again.", res);
   }

   public void testHTTPEndpointPort() throws Exception
   {
      URL wsdlURL = getResourceURL("jaxws/samples/jmstransport/jmsservice.wsdl");
      QName serviceName = new QName("http://org.jboss.ws/samples/jmstransport", "OrganizationJMSEndpointService");
      QName portName = new QName("http://org.jboss.ws/samples/jmstransport", "HTTPEndpointPort");
      
      Service service = Service.create(wsdlURL, serviceName);
      Organization port = service.getPort(portName, Organization.class);
      
      String res = port.getContactInfo("mafia");
      assertEquals("The 'mafia' boss is currently out of office, please call again.", res);
   }

   public void testMessagingClient() throws Exception
   {
      String reqMessage =
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
          "<env:Body>" +
           "<ns1:getContactInfo xmlns:ns1='http://org.jboss.ws/samples/jmstransport'>" +
            "<arg0>mafia</arg0>" +
           "</ns1:getContactInfo>" +
          "</env:Body>" +
         "</env:Envelope>";

      String resMessage =
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
          "<env:Header/>" +
          "<env:Body>" +
           "<ns1:getContactInfoResponse xmlns:ns1='http://org.jboss.ws/samples/jmstransport'>" +
            "<return>The 'mafia' boss is currently out of office, please call again.</return>" +
           "</ns1:getContactInfoResponse>" +
          "</env:Body>" +
         "</env:Envelope>";

      InitialContext context = new InitialContext();
      QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("ConnectionFactory");
      Queue reqQueue = (Queue)context.lookup("queue/RequestQueue");
      Queue resQueue = (Queue)context.lookup("queue/ResponseQueue");

      QueueConnection con = connectionFactory.createQueueConnection();
      QueueSession session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
      QueueReceiver receiver = session.createReceiver(resQueue);
      ResponseListener responseListener = new ResponseListener();
      receiver.setMessageListener(responseListener);
      con.start();

      TextMessage message = session.createTextMessage(reqMessage);
      message.setJMSReplyTo(resQueue);

      waitForResponse = true;

      QueueSender sender = session.createSender(reqQueue);
      sender.send(message);
      sender.close();

      int timeout = 30000;
      while (waitForResponse && timeout > 0)
      {
         Thread.sleep(100);
         timeout -= 100;
      }

      assertNotNull("Expected response message", responseListener.resMessage);
      assertEquals(DOMUtils.parse(resMessage), DOMUtils.parse(responseListener.resMessage));

      con.stop();
      session.close();
      con.close();
   }

   public static class ResponseListener implements MessageListener
   {
      public String resMessage;

      public void onMessage(Message msg)
      {
         TextMessage textMessage = (TextMessage)msg;
         try
         {
            resMessage = textMessage.getText();
            waitForResponse = false;
         }
         catch (Throwable t)
         {
            t.printStackTrace();
         }
      }
   }
}
