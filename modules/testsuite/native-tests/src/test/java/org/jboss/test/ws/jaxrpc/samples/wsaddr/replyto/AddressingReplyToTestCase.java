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
package org.jboss.test.ws.jaxrpc.samples.wsaddr.replyto;

import java.io.ByteArrayInputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test endpoint using ws-addressing
 *
 * @author mageshbk@jboss.com
 * @since 12-Dec-2007
 */
public class AddressingReplyToTestCase extends JBossWSTest
{
   private static Hello initial;
   private static ReplyTo replyto;

   public static Test suite()
   {
      return new JBossWSTestSetup(AddressingReplyToTestCase.class,
            "jaxrpc-samples-wsaddr-hello.war, jaxrpc-samples-wsaddr-replyto.war," +
            "jaxrpc-samples-wsaddr-hello-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      if (initial == null)
      {
         Service replytoService = (Service)getInitialContext("jaxrpc-addressing-replyto-client").lookup("java:comp/env/service/ReplyToService");
         replyto = (ReplyTo)replytoService.getPort(ReplyTo.class);
         Service initialService = (Service)getInitialContext("jaxrpc-addressing-replyto-client").lookup("java:comp/env/service/HelloService");
         initial = (Hello)initialService.getPort(Hello.class);
      }
   }

   /** This sends a valid message to the ReplyTo endpoint and verfies whether we can read it of again.
    */
   public void testReplyToMessage() throws Exception
   {
      String reqEnv =
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
         "  <env:Header/>" +
         "  <env:Body>" +
         "    <ns1:sayHelloResponse xmlns:ns1='http://org.jboss.ws/jaxrpc/samples/wsaddr/replyto/types'>" +
         "      <result>ReplyTo</result>" +
         "    </ns1:sayHelloResponse>" +
         "  </env:Body>" +
         "</env:Envelope>";

      MessageFactory msgFactory = MessageFactory.newInstance();
      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
      SOAPMessage reqMsg = msgFactory.createMessage(null, new ByteArrayInputStream(reqEnv.getBytes()));

      URL epURL = new URL("http://" + getServerHost() + ":8080/jaxrpc-samples-wsaddr-replyto");
      con.call(reqMsg, epURL);

      assertEquals("ReplyTo", replyto.getLastMessage());
   }

   public void testReplyTo() throws Exception
   {
      String message = initial.sayHello("Addressing TestCase");
      assertNull("Expected null, but was: " + message, message);

      String reply = replyto.getLastMessage();
      assertEquals("Hello Addressing TestCase", reply);
   }
}
