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

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.Constants;
import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 * Test the SOAPEnvelope
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Oct-2004
 */
public class SOAPEnvelopeTestCase extends JBossWSTest
{
   /** Test that we can create the default envelope. */
   public void testCreateDefaultEnvelope() throws Exception
   {
      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMessage = factory.createMessage();

      SOAPEnvelope env = soapMessage.getSOAPPart().getEnvelope();
      assertEquals("env:Envelope", env.getNodeName());
      assertEquals(Constants.NS_SOAP11_ENV, env.getNamespaceURI());
      assertEquals(Constants.NS_SOAP11_ENV, env.getNamespaceURI("env"));

      SOAPHeader header = env.getHeader();
      SOAPBody body = env.getBody();

      Iterator it = env.getChildElements();
      assertEquals(header, it.next());
      assertEquals(body, it.next());
   }

   /** Test that we can build an envelope from InputStream */
   public void testEnvelopeBuilder() throws Exception
   {
      String envStr =
              "<env:Envelope xmlns:env='http://www.w3.org/2003/05/soap-envelope'>" +
              " <env:Header> " +
              "  <tns:someHeader xmlns:tns='http://org.jboss.ws/2004'>some header value</tns:someHeader>" +
              " </env:Header> " +
              " <env:Body>" +
              "  <tns:echoString xmlns:tns='http://org.jboss.ws/2004'>" +
              "   <string>Hello World!</string>" +
              "  </tns:echoString>" +
              " </env:Body>" +
              "</env:Envelope>";

      ByteArrayInputStream inputStream = new ByteArrayInputStream(envStr.getBytes());

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMsg = factory.createMessage(null, inputStream);
      SOAPEnvelope env = soapMsg.getSOAPPart().getEnvelope();

      assertEquals("env:Envelope", env.getNodeName());
      assertEquals(Constants.NS_SOAP12_ENV, env.getNamespaceURI());

      SOAPHeader header = env.getHeader();
      assertTrue("Header elements expected", header.hasChildNodes());

      SOAPBody body = env.getBody();
      assertTrue("Body elements expected", body.hasChildNodes());
      
      String reqMsgStr =
         "<soapenv:Envelope" +
         "    xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'" +
         "    xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "    xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
         "  <soapenv:Body>" +
         "    <publish xmlns='http://webservices.est.useme.etish.com'>" +
         "       <in0 xmlns=''>joel</in0>" +
         "       <in1 xmlns=''>secret</in1>" +
         "       <in2 xmlns=''>1</in2>" +
         "       <in3 xmlns=''>6</in3>" +
         "       <in4 xmlns=''>2</in4>" +
         "     </publish>" +
         "  </soapenv:Body>" +
         "</soapenv:Envelope>";

   }

   /** Test that we can build an envelope with a predefined prefix */
   public void testEnvelopePrefix() throws Exception
   {
      // The use of default namespace on the RPC element is tested as well
      String envStr =
         "<env:Envelope" +
         "    xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'" +
         "    xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "    xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
         "  <env:Header/>" +
         "  <env:Body>" +
         "    <publish xmlns='http://webservices.est.useme.etish.com'>" +
         "       <in0 xmlns=''>joel</in0>" +
         "       <in1 xmlns=''>secret</in1>" +
         "       <in2 xmlns=''>1</in2>" +
         "       <in3 xmlns=''>6</in3>" +
         "       <in4 xmlns=''>2</in4>" +
         "     </publish>" +
         "  </env:Body>" +
         "</env:Envelope>";

      ByteArrayInputStream inputStream = new ByteArrayInputStream(envStr.getBytes());

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMsg = factory.createMessage(null, inputStream);
      SOAPEnvelope wasEnv = soapMsg.getSOAPPart().getEnvelope();

      Element expEnv = DOMUtils.parse(envStr);
      assertEquals(expEnv, wasEnv);
   }
}
