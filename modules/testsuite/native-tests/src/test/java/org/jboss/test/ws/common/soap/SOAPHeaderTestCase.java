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
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.Constants;
import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.core.soap.NameImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;

/**
 * Test the SOAPHeader
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Oct-2004
 */
public class SOAPHeaderTestCase extends JBossWSTest
{

   public void testAddHeaderElement() throws Exception
   {
      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMessage = factory.createMessage();
      SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();

      // Test if we see the right NS URI for the env prefix
      assertEquals(Constants.NS_SOAP11_ENV, soapHeader.getNamespaceURI("env"));

      assertFalse(soapHeader.getChildElements().hasNext());

      try
      {
         Name name = new NameImpl("Foo");
         soapHeader.addHeaderElement(name);
         fail("Invalid name: " + name);
      }
      catch (SOAPException e)
      {
         // ignore
      }

      Name name = new NameImpl("Foo", "ns1", "http://org.jboss.ws/header");
      soapHeader.addHeaderElement(name);
      SOAPHeaderElement shElement = (SOAPHeaderElement)soapHeader.getChildElements(name).next();
      shElement.setValue("SomeHeaderValue");
      assertEquals(name, shElement.getElementName());

      String expStr =
              "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
              " <env:Header>" +
              "  <ns1:Foo xmlns:ns1='http://org.jboss.ws/header'>SomeHeaderValue</ns1:Foo>" +
              " </env:Header>" +
              " <env:Body/>" +
              "</env:Envelope>";

      assertEquals(DOMUtils.parse(expStr), soapEnv);
   }

   public void testExamineAllHeaderElements() throws Exception
   {
      String envStr =
              "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
              " <env:Header>" +
              "  <ns1:Foo xmlns:ns1='http://org.jboss.ws/header'>SomeHeaderValue</ns1:Foo>" +
              "  <ns2:Bar xmlns:ns2='http://org.jboss.ws/header2'>SomeOtherValue</ns2:Bar>" +
              " </env:Header>" +
              " <env:Body/>" +
              "</env:Envelope>";

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
      SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();

      Iterator it = soapHeader.examineAllHeaderElements();
      SOAPHeaderElement foo = (SOAPHeaderElement)it.next();
      SOAPHeaderElement bar = (SOAPHeaderElement)it.next();
      assertFalse(it.hasNext());

      Name fooName = new NameImpl("Foo", "ns1", "http://org.jboss.ws/header");
      Name barName = new NameImpl("Bar", "ns2", "http://org.jboss.ws/header2");

      assertEquals(fooName, foo.getElementName());
      assertEquals(barName, bar.getElementName());

      assertEquals("SomeHeaderValue", foo.getValue());
      assertEquals("SomeOtherValue", bar.getValue());

      assertTrue(soapHeader.getChildElements().hasNext());
   }

   public void testExamineHeaderElements() throws Exception
   {
      String envStr =
              "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
              " <env:Header>" +
              "  <ns1:Foo xmlns:ns1='http://org.jboss.ws/header'>SomeHeaderValue</ns1:Foo>" +
              "  <ns2:Bar xmlns:ns2='http://org.jboss.ws/header2' env:actor='BradPitt'>SomeOtherValue</ns2:Bar>" +
              " </env:Header>" +
              " <env:Body/>" +
              "</env:Envelope>";

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
      SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();

      Iterator it = soapHeader.examineHeaderElements("BradPitt");
      SOAPHeaderElement bar = (SOAPHeaderElement)it.next();
      assertFalse(it.hasNext());

      Name barName = new NameImpl("Bar", "ns2", "http://org.jboss.ws/header2");
      assertEquals(barName, bar.getElementName());

      assertEquals("SomeOtherValue", bar.getValue());

      assertTrue(soapHeader.getChildElements().hasNext());
   }

   public void testExamineMustUnderstandHeaderElements() throws Exception
   {
      String envStr =
              "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
              " <env:Header>" +
              "  <ns1:Foo xmlns:ns1='http://org.jboss.ws/header' env:actor='BradPitt'>SomeHeaderValue</ns1:Foo>" +
              "  <ns2:Bar xmlns:ns2='http://org.jboss.ws/header2' env:actor='BradPitt' env:mustUnderstand='1'>SomeOtherValue</ns2:Bar>" +
              " </env:Header>" +
              " <env:Body/>" +
              "</env:Envelope>";

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
      SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();

      Iterator it = soapHeader.examineMustUnderstandHeaderElements("BradPitt");
      SOAPHeaderElement bar = (SOAPHeaderElement)it.next();
      assertFalse(it.hasNext());

      Name barName = new NameImpl("Bar", "ns2", "http://org.jboss.ws/header2");
      assertEquals(barName, bar.getElementName());

      assertEquals("SomeOtherValue", bar.getValue());

      assertTrue(soapHeader.getChildElements().hasNext());
   }

   public void testExtractAllHeaderElements() throws Exception
   {
      String envStr =
              "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
              " <env:Header>" +
              "  <ns1:Foo xmlns:ns1='http://org.jboss.ws/header'>SomeHeaderValue</ns1:Foo>" +
              "  <ns2:Bar xmlns:ns2='http://org.jboss.ws/header2'>SomeOtherValue</ns2:Bar>" +
              " </env:Header>" +
              " <env:Body/>" +
              "</env:Envelope>";

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
      SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();

      Iterator it = soapHeader.extractAllHeaderElements();
      SOAPHeaderElement foo = (SOAPHeaderElement)it.next();
      SOAPHeaderElement bar = (SOAPHeaderElement)it.next();
      assertFalse(it.hasNext());

      Name fooName = new NameImpl("Foo", "ns1", "http://org.jboss.ws/header");
      Name barName = new NameImpl("Bar", "ns2", "http://org.jboss.ws/header2");

      assertEquals(fooName, foo.getElementName());
      assertEquals(barName, bar.getElementName());

      assertEquals("SomeHeaderValue", foo.getValue());
      assertEquals("SomeOtherValue", bar.getValue());

      assertFalse(soapHeader.getChildElements().hasNext());
   }

   public void testExtractHeaderElements() throws Exception
   {
      String envStr =
              "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
              " <env:Header>" +
              "  <ns1:Foo xmlns:ns1='http://org.jboss.ws/header'>SomeHeaderValue</ns1:Foo>" +
              "  <ns2:Bar xmlns:ns2='http://org.jboss.ws/header2' env:actor='BradPitt'>SomeOtherValue</ns2:Bar>" +
              " </env:Header>" +
              " <env:Body/>" +
              "</env:Envelope>";

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
      SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();

      Iterator it = soapHeader.extractHeaderElements("BradPitt");
      SOAPHeaderElement bar = (SOAPHeaderElement)it.next();
      assertFalse(it.hasNext());

      Name barName = new NameImpl("Bar", "ns2", "http://org.jboss.ws/header2");
      assertEquals(barName, bar.getElementName());

      assertEquals("SomeOtherValue", bar.getValue());

      Iterator childElements = soapHeader.getChildElements();
      SOAPHeaderElement foo = (SOAPHeaderElement)childElements.next();

      Name fooName = new NameImpl("Foo", "ns1", "http://org.jboss.ws/header");
      assertEquals(fooName, foo.getElementName());
      assertFalse(childElements.hasNext());
   }
}
