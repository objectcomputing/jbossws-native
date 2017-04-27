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
package org.jboss.test.ws.jaxrpc.jbws1179;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Each web services invocation causes a new HTTP session to be created.
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1179
 * 
 * @author darran.lofthouse@jboss.com
 * @since 18-October-2006
 */
public class JBWS1179TestCase extends JBossWSTest
{

   public static TestEndpoint port;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1179TestCase.class, "jaxrpc-jbws1179.war, jaxrpc-jbws1179-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (TestEndpoint)service.getPort(TestEndpoint.class);
      }
   }

   public void testCallCreateNoSession() throws Exception
   {
      int originalSessions = getActiveSessions();

      port.echoMessage("Hello");

      int finalSessions = getActiveSessions();

      assertEquals("activeSessions after call", originalSessions, finalSessions);
   }

   public void testAccessSession() throws Exception
   {
      int originalSessions = getActiveSessions();

      assertTrue("Can access session", port.canAccessSession());

      int finalSessions = getActiveSessions();

      assertEquals("activeSessions after call", originalSessions + 1, finalSessions);
   }

   private int getActiveSessions() throws Exception
   {
      MBeanServerConnection server = getServer();
      ObjectName objectName = new ObjectName("jboss.web:host=localhost,path=/jaxrpc-jbws1179,type=Manager");

      return ((Integer)server.getAttribute(objectName, "activeSessions")).intValue();
   }
}
