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
package org.jboss.test.ws.jaxrpc.jbws1186;

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
 * The prefix "xsi" for attribute "xsi:nil" is not bound
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1186
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 14-Sep-2006
 */
public class JBWS1186TestCase extends JBossWSTest
{

   private static TestEndpoint port;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1186TestCase.class, "jaxrpc-jbws1186.war");
   }

   public void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         ServiceFactoryImpl factory = new ServiceFactoryImpl();
         URL wsdlURL = getResourceURL("jaxrpc/jbws1186/WEB-INF/wsdl/TestService.wsdl");
         URL mappingURL = getResourceURL("jaxrpc/jbws1186/WEB-INF/jaxrpc-mapping.xml");
         QName qname = new QName("http://org.jboss.test.ws/jbws1186", "TestService");
         Service service = factory.createService(wsdlURL, qname, mappingURL);
         port = (TestEndpoint)service.getPort(TestEndpoint.class);
      }
   }

   public void testNotNull() throws Exception
   {
      UserType userType = new UserType("World");
      UserType retObj = port.echo("Hello", userType);
      assertEquals("Hello World", retObj.getName());
   }

   public void testNullMessage() throws Exception
   {
      UserType userType = new UserType("World");
      UserType retObj = port.echo(null, userType);
      assertEquals("null World", retObj.getName());
   }

   public void testNullComplexType() throws Exception
   {
      UserType userType = new UserType();
      UserType retObj = port.echo("Hello", userType);
      assertEquals("Hello null", retObj.getName());
   }

   public void testAllNull() throws Exception
   {
      UserType userType = new UserType();
      UserType retObj = port.echo(null, userType);
      assertNull("null expected", retObj);
   }

   public void testMessageAccess() throws Exception
   {
      MessageFactory factory = MessageFactory.newInstance();
      
      // The xsi namespace is declared on the envelope
      String reqStr = 
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
         " <env:Body>" +
         "  <ns1:echo xmlns:ns1='http://org.jboss.test.ws/jbws1186'>" +
         "   <String_1 xsi:nil='1'/>" +
         "   <UserType_2>" +
         "    <name xsi:nil='1'/>" +
         "   </UserType_2>" +
         "  </ns1:echo>" +
         " </env:Body>" +
         "</env:Envelope>";
      
      SOAPMessage reqMessage = factory.createMessage(null, new ByteArrayInputStream(reqStr.getBytes()));
      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
      SOAPMessage resMessage = con.call(reqMessage, "http://" + getServerHost() + ":8080/jaxrpc-jbws1186");
      SOAPElement soapElement = (SOAPElement)resMessage.getSOAPBody().getChildElements().next();
      soapElement = (SOAPElement)soapElement.getChildElements().next();
      assertEquals("1", soapElement.getAttribute("xsi:nil"));
   }
}
