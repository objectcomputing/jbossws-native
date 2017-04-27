/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.jbws2319;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.ws.core.soap.NodeImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * [JBWS-2319] ClassCastException: org.jboss.ws.core.soap.TextImpl 
 * in SOAPBody.extractContentAsDocument();
 * 
 * @author darran.lofthouse@jboss.com
 * @since 23rd September 2008
 * @see https://jira.jboss.org/jira/browse/JBWS-2319
 */
public class JBWS2319TestCase extends JBossWSTest
{

   public final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxws-jbws2319/";

   private static Endpoint port;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS2319TestCase.class, "jaxws-jbws2319.war");
   }

   public void setUp() throws Exception
   {
      super.setUp();
      URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");
      QName serviceName = new QName("http://ws.jboss.org/jbws2319", "EndpointImplService");

      Service service = Service.create(wsdlURL, serviceName);
      port = service.getPort(Endpoint.class);
   }

   public void testCall() throws Exception
   {
      final String message = "Hello!!";
      String response = port.echo(message);

      // The logical handler should have replaced the incoming String.
      assertEquals("XXX", response);
   }

   public void testSOAPConnection() throws Exception
   {
      SOAPMessage reqMsg = getRequestMessage();
      URL epURL = new URL(TARGET_ENDPOINT_ADDRESS);
      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
      SOAPMessage resMsg = con.call(reqMsg, epURL);
      SOAPEnvelope resEnv = resMsg.getSOAPPart().getEnvelope();

      String response = "";

      SOAPBody body = resEnv.getBody();
      Iterator it = body.getChildElements(new QName("http://ws.jboss.org/jbws2319", "echoResponse"));
      Node node = (Node)it.next();
      NodeList nodes = node.getChildNodes();
      for (int i = 0; i < nodes.getLength(); i++)
      {
         Node current = nodes.item(i);
         if (current.getNodeName().equals("return"))
         {
            response = ((NodeImpl)current).getValue();
         }
      }

      // The logical handler should have replaced the incoming String.
      assertEquals("XXX", response);
   }

   private SOAPMessage getRequestMessage() throws SOAPException, IOException
   {
      URL reqMessage = getResourceFile("jaxws/jbws2319/request-message.xml").toURL();
      MessageFactory msgFactory = MessageFactory.newInstance();

      SOAPMessage reqMsg = msgFactory.createMessage(null, reqMessage.openStream());
      return reqMsg;
   }

}
