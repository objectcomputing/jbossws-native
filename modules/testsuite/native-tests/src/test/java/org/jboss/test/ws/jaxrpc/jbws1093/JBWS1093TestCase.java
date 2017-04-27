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
package org.jboss.test.ws.jaxrpc.jbws1093;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.jboss.wsf.common.DOMWriter;

/**
 * Deploying a war that also contains normal servlets the web.xml is modified as if they are all endpoints
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1093
 * 
 * @author darran.lofthouse@jboss.com
 * @since 17-October-2006
 */
public class JBWS1093TestCase extends JBossWSTest
{

   private static TestEndpoint port;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1093TestCase.class, "jaxrpc-jbws1093.war, jaxrpc-jbws1093-client.jar");
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

   public void testEnpointAccess() throws Exception
   {
      assertEquals(ServletTest.MESSAGE, port.echoString(ServletTest.MESSAGE));
   }

   public void testServletAccess() throws Exception
   {
      URL servletURL = new URL("http://" + getServerHost() + ":8080" + "/jaxrpc-jbws1093/ServletTest?type=txtMessage");

      InputStream is = servletURL.openStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);

      String line = br.readLine();

      assertEquals(ServletTest.MESSAGE, line);
   }

   /**
    * [JBWS-1706] SOAPConnection.get fails with ProtocolException
    * 
    * Gets a SOAP response message from a specific endpoint
    * and blocks until it has received the response. HTTP-GET
    * from a valid endpoint that contains a valid webservice
    * resource should succeed. The endpoint tested contains
    * a valid webservice resource that must return a SOAP 
    * response. HTTP-GET must succeed.
    *
    */
   public void testSOAPConnectionGet() throws Exception
   {
      URL servletURL = new URL("http://" + getServerHost() + ":8080" + "/jaxrpc-jbws1093/ServletTest?type=soapMessage");

      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
      SOAPMessage resMessage = con.get(servletURL);
      SOAPEnvelope env = resMessage.getSOAPPart().getEnvelope();

      String envStr = DOMWriter.printNode(env, false);
      String expStr = "<ztrade:GetLastTradePriceResponse xmlns:ztrade='http://wombat.ztrade.com'><Price>95.12</Price></ztrade:GetLastTradePriceResponse>";
      assertTrue(envStr.contains(expStr));
   }
}
