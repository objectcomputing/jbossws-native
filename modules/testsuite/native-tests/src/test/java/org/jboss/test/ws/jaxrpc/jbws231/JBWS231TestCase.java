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
package org.jboss.test.ws.jaxrpc.jbws231;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Handle Simple Types with Enumerated Values
 *
 * http://jira.jboss.com/jira/browse/JBWS-231
 *
 * @author Thomas.Diesler@jboss.org
 * @since 11-Apr-2006
 */
public class JBWS231TestCase extends JBossWSTest
{
   private static TestEndpoint port;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS231TestCase.class, "jaxrpc-jbws231.war, jaxrpc-jbws231-client.jar");
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

   public void testSimple() throws Exception
   {
      EyeColorType ret = port.echoSimple(EyeColorType.green);
      assertEquals(EyeColorType.green, ret);
   }
}
