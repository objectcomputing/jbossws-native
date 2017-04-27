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
package org.jboss.test.ws.jaxrpc.enventry;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test env entry access
 *
 * @author Thomas.Diesler@jboss.org
 * @since 15-Sep-2005
 */
public class EnvEntryJSETestCase extends JBossWSTest
{
   private static EnvEntryTestService port;

   public static Test suite()
   {
      return new JBossWSTestSetup(EnvEntryJSETestCase.class, "jaxrpc-enventry.war, jaxrpc-enventry-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (EnvEntryTestService)service.getPort(EnvEntryTestService.class);
      }
   }

   public void testEndpoint() throws Exception
   {
      String res = port.helloEnvEntry("InitalMessage");
      assertEquals("InitalMessage:ClientSideHandler:appclient:8:ServerSideHandler:web:8:endpoint:web:8:ServerSideHandler:web:8:ClientSideHandler:appclient:8", res);
   }
}
