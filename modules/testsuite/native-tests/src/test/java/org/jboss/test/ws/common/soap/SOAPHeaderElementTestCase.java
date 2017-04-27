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
import java.io.ByteArrayOutputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.core.soap.NameImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;

/**
 * Test the SOAPHeaderElement
 *
 * @author Thomas.Diesler@jboss.org
 * @since 02-Feb-2005
 */
public class SOAPHeaderElementTestCase extends JBossWSTest
{

   /** Test access to the actor attribute
    */
   public void testAttributeActor() throws Exception
   {
      String envStr =
              "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
              " <env:Header>" +
              "  <ns2:Bar xmlns:ns2='http://org.jboss.ws/header2' env:actor='BradPitt'>SomeOtherValue</ns2:Bar>" +
              " </env:Header>" +
              " <env:Body/>" +
              "</env:Envelope>";

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
      SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();
      SOAPHeaderElement shElement = (SOAPHeaderElement)soapHeader.getChildElements().next();

      Name name = new NameImpl("Bar", "ns2", "http://org.jboss.ws/header2");
      assertEquals(name, shElement.getElementName());

      assertEquals("BradPitt", shElement.getActor());
      assertEquals("SomeOtherValue", shElement.getValue());
   }

   /** Test access to the mustUnderstand attribute
    */
   public void testGetMustUnderstand() throws Exception
   {
      String envStr =
              "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
              " <env:Header>" +
              "  <ns2:Bar xmlns:ns2='http://org.jboss.ws/header2' env:mustUnderstand='1'>SomeOtherValue</ns2:Bar>" +
              " </env:Header>" +
              " <env:Body/>" +
              "</env:Envelope>";

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
      SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();
      SOAPHeaderElement shElement = (SOAPHeaderElement)soapHeader.getChildElements().next();

      Name name = new NameImpl("Bar", "ns2", "http://org.jboss.ws/header2");
      assertEquals(name, shElement.getElementName());

      assertTrue(shElement.getMustUnderstand());
      assertEquals("SomeOtherValue", shElement.getValue());
   }

   /** Test access to the mustUnderstand attribute
    */
   public void testSetMustUnderstand() throws Exception
   {
      String expEnv =
              "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
                "<env:Header>" +
                  "<ns2:Bar xmlns:ns2='http://org.jboss.ws/header2' env:mustUnderstand='1'>SomeOtherValue</ns2:Bar>" +
                "</env:Header>" +
                "<env:Body/>" +
              "</env:Envelope>";

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMessage = factory.createMessage();
      SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();

      Name name = new NameImpl("Bar", "ns2", "http://org.jboss.ws/header2");
      SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement(name);
      soapHeaderElement.setMustUnderstand(true);
      soapHeaderElement.addTextNode("SomeOtherValue");

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      soapMessage.writeTo(baos);
      String wasEnv = new String(baos.toByteArray());

      assertEquals(DOMUtils.parse(expEnv), DOMUtils.parse(wasEnv));
   }
}
