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
package org.jboss.test.ws.jaxrpc.jbws1619;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * ServletEndpointContext.getHttpSession has an incorrect implementation
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1619
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 23-Apr-2007
 */
public class JBWS1619TestCase extends JBossWSTest
{

   private static TestEndpoint port;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1619TestCase.class, "jaxrpc-jbws1619.war, jaxrpc-jbws1619-client.jar");
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

   public void testServletEndpointContext() throws Exception
   {
      ClientHandler.message = "Use ServletEndpointContext";
      String retStr = port.echoString(ClientHandler.message);
      assertEquals("httpSession: null", retStr);
   }

   public void testMessageContext() throws Exception
   {
      ClientHandler.message = "Use MessageContext";
      String retStr = port.echoString(ClientHandler.message);
      assertTrue("Expect a session", retStr.startsWith("httpSession") && !retStr.endsWith("null"));
      assertTrue("Expect a cookie", ClientHandler.cookie.startsWith("JSESSIONID"));
   }
}
