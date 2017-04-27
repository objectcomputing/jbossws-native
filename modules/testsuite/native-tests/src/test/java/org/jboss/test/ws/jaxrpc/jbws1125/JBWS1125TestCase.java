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
package org.jboss.test.ws.jaxrpc.jbws1125;

import java.io.ByteArrayInputStream;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Support empty soap body elements
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1125
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 16-August-2006
 */
public class JBWS1125TestCase extends JBossWSTest
{
   private static TestEndpoint port;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1125TestCase.class, "jaxrpc-jbws1125.war, jaxrpc-jbws1125-client.jar");
   }

   public void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (TestEndpoint)service.getPort(TestEndpoint.class);
      }
   }

   public void testNoParamPart() throws Exception
   {
      String retObj = port.noParamPart();
      assertEquals("noParamPart", retObj);
   }
   
   public void testNoReturnPart() throws Exception
   {
      port.noReturnPart("hello");
   }
   
   public void testMessageAccess() throws Exception
   {
      String reqEnv =
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
         "  <env:Body/>" +
         "</env:Envelope>";
      
      MessageFactory msgFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = msgFactory.createMessage(null, new ByteArrayInputStream(reqEnv.getBytes()));
      
      SOAPConnectionFactory conFactory = SOAPConnectionFactory.newInstance();
      SOAPConnection con = conFactory.createConnection();
      SOAPMessage resMessage = con.call(soapMessage, "http://" + getServerHost() + ":8080/jaxrpc-jbws1125");
      SOAPElement soapElement = (SOAPElement)resMessage.getSOAPBody().getChildElements().next();
      assertEquals("noParamPartResponse", soapElement.getElementName().getLocalName());
      
   }
}
