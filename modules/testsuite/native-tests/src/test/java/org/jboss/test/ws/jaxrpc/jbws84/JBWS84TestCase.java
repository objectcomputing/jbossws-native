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
package org.jboss.test.ws.jaxrpc.jbws84;

import java.io.ByteArrayInputStream;

import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.Service;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.w3c.dom.Document;

/**
 * Test unstructured message processing
 *
 * @author Thomas.Diesler@jboss.org
 * @since 26-Nov-2004
 */
public class JBWS84TestCase extends JBossWSTest
{
   /** Deploy the test ear */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS84TestCase.class, "jaxrpc-jbws84.war, jaxrpc-jbws84-client.jar");
   }

   /** Use the JBoss generated dynamic proxy send the SOAP message.
    * This tests server/client side message handling.
    */
   public void testProcessSOAPElement() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/MessageService");
      Message port = (Message)service.getPort(Message.class);

      MessageFactory factory = MessageFactory.newInstance();
      SOAPMessage reqMessage = factory.createMessage();

      DocumentBuilder builder = getDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(Message.request.getBytes()));
      reqMessage.getSOAPBody().addDocument(doc);

      SOAPEnvelope soapEnvelope = reqMessage.getSOAPPart().getEnvelope();
      SOAPBody soapBody = soapEnvelope.getBody();
      SOAPElement reqElement = (SOAPElement)soapBody.getChildElements().next();

      SOAPElement resElement = port.processSOAPElement(reqElement);
      validateResponse(resElement);
   }

   private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException
   {
      // Setup document builder
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      docBuilderFactory.setNamespaceAware(true);

      DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
      return builder;
   }

   private void validateResponse(SOAPElement resElement)
           throws SOAPException
   {
      SOAPFactory factory = SOAPFactory.newInstance();

      Name expName = factory.createName("Response", Message.PREFIX, Message.NAMESPACE_URI);
      Name elementName = resElement.getElementName();
      assertEquals(expName, elementName);

      expName = factory.createName("POID");
      SOAPElement poidElement = (SOAPElement)resElement.getChildElements(expName).next();
      elementName = poidElement.getElementName();
      assertEquals(expName, elementName);

      String elementValue = poidElement.getValue();
      assertEquals("12345", elementValue);

      expName = factory.createName("Status");
      SOAPElement statusElement = (SOAPElement)resElement.getChildElements(expName).next();
      elementName = statusElement.getElementName();
      assertEquals(expName, elementName);

      elementValue = statusElement.getValue();
      assertEquals("ok", elementValue);
   }
}
