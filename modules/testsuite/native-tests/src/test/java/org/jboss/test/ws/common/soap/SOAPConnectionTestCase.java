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
package org.jboss.test.ws.common.soap;

import java.net.URL;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import junit.framework.Test;

import org.jboss.ws.core.soap.SOAPBodyElementRpc;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/** Test call on a SOAPConnection
 *
 * @author Jason T. Greene
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPConnectionTestCase extends JBossWSTest
{
   private final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxrpc-samples-jsr109pojo-rpc";
   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/samples/jsr109pojo";

   public static Test suite()
   {
      return new JBossWSTestSetup(SOAPConnectionTestCase.class, "jaxrpc-samples-jsr109pojo-rpc.war");
   }

   public void testConnectString() throws Exception
   {
      runEndpointTest(TARGET_ENDPOINT_ADDRESS);
   }

   public void testConnectURL() throws Exception
   {
      runEndpointTest(new URL(TARGET_ENDPOINT_ADDRESS));
   }

   // [JBWS-1802] RemotingConnectionImpl.addURLParameter() doesn't guarantee a URL as expected by the JBossRemoting InvokerLocator
   public void testNullContext() throws Exception
   {
      SOAPMessage request = buildValidMessage();
      SOAPConnection connection = SOAPConnectionFactory.newInstance().createConnection();
      try
      {
         connection.call(request, "http://" + getServerHost() + ":8080");
      }
      catch (Exception ex)
      {
         while (ex != null)
         {
            if(ex instanceof SOAPException && ex.getMessage().equals("Unsupported content type: text/html"))
               break;
            else
               ex = (Exception)ex.getCause();
         }
      }
   }

   private void runEndpointTest(Object endPoint) throws Exception
   {
      SOAPMessage request = buildValidMessage();
      SOAPConnection connection = SOAPConnectionFactory.newInstance().createConnection();
      SOAPMessage response = connection.call(request, endPoint);
      validateResponse(response);
   }

   private SOAPMessage buildValidMessage() throws Exception
   {
      SOAPMessage message = MessageFactory.newInstance().createMessage();
      SOAPPart sp = message.getSOAPPart();
      SOAPEnvelope envelope = sp.getEnvelope();
      SOAPBody bdy = envelope.getBody();

      SOAPElement sbe = bdy.addChildElement(new SOAPBodyElementRpc(envelope.createName("echoString", "ns1", TARGET_NAMESPACE)));
      sbe.addChildElement(envelope.createName("String_1")).addTextNode("Hello");
      sbe.addChildElement(envelope.createName("String_2")).addTextNode("world!");

      return message;
   }

   private void validateResponse(SOAPMessage response) throws Exception
   {
      SOAPBody body = response.getSOAPBody();
      SOAPEnvelope env = response.getSOAPPart().getEnvelope();

      Name rpcName = env.createName("echoStringResponse", "ns1", TARGET_NAMESPACE);
      Iterator childElements = body.getChildElements(rpcName);

      SOAPElement bodyChild = (SOAPElement)childElements.next();
      Name resName = env.createName("result");
      SOAPElement resElement = (SOAPElement)bodyChild.getChildElements(resName).next();
      String value = resElement.getValue();

      assertEquals("Helloworld!", value);
   }
}
