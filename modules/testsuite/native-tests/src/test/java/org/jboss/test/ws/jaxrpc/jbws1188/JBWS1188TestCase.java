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
package org.jboss.test.ws.jaxrpc.jbws1188;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test case to test the UsernameToken can be sent without an id.
 * 
 * @author mageshbk@jboss.com
 */
public class JBWS1188TestCase extends JBossWSTest
{

   private static TestEndpoint port;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1188TestCase.class, "jaxrpc-jbws1188.war");
   }

   public void setUp() throws Exception
   {
      super.setUp();
   }

   public void testUsernameTokenNoID() throws Exception
   {
      MessageFactory factory = MessageFactory.newInstance();

      String reqStr = 
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
         " <env:Header>" +
         "  <wsse:Security xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd'>" +
         "   <wsse:UsernameToken>" +
         "    <wsse:Username>kermit</wsse:Username>" +
         "    <wsse:Password>thefrog</wsse:Password>" +
         "   </wsse:UsernameToken>" +
         "  </wsse:Security>" +
         " </env:Header>" +
         " <env:Body>" +
         "  <ns1:echoString xmlns:ns1='http://org.jboss.test.ws/jbws1188'>" +
         "   <String_1>" + "Jimbo!" + "</String_1>" +
         "  </ns1:echoString>" +
         " </env:Body>" +
         "</env:Envelope>";
      
      SOAPMessage reqMessage = factory.createMessage(null, new ByteArrayInputStream(reqStr.getBytes()));
      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
      SOAPMessage resMessage = con.call(reqMessage, "http://" + getServerHost() + ":8080/jaxrpc-jbws1188");
      SOAPElement soapElement = (SOAPElement)resMessage.getSOAPBody().getChildElements().next();
      soapElement = (SOAPElement)soapElement.getChildElements().next();
      assertEquals("Hello Jimbo!", soapElement.getValue());
   }

   public void testUsernameToken() throws Exception
   {
      MessageFactory factory = MessageFactory.newInstance();

      String reqStr = 
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
         " <env:Header>" +
         "  <wsse:Security xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd' xmlns:wsu='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd'>" +
         "   <wsse:UsernameToken wsu:Id='abcde-1a23bc4d-1234-1ab2'>" +
         "    <wsse:Username>kermit</wsse:Username>" +
         "    <wsse:Password>thefrog</wsse:Password>" +
         "   </wsse:UsernameToken>" +
         "  </wsse:Security>" +
         " </env:Header>" +
         " <env:Body>" +
         "  <ns1:echoString xmlns:ns1='http://org.jboss.test.ws/jbws1188'>" +
         "   <String_1>" + "Jimbo!" + "</String_1>" +
         "  </ns1:echoString>" +
         " </env:Body>" +
         "</env:Envelope>";
      
      SOAPMessage reqMessage = factory.createMessage(null, new ByteArrayInputStream(reqStr.getBytes()));
      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
      SOAPMessage resMessage = con.call(reqMessage, "http://" + getServerHost() + ":8080/jaxrpc-jbws1188");
      SOAPElement soapElement = (SOAPElement)resMessage.getSOAPBody().getChildElements().next();
      soapElement = (SOAPElement)soapElement.getChildElements().next();
      assertEquals("Hello Jimbo!", soapElement.getValue());
   }
}
