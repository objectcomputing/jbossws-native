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
package org.jboss.test.ws.jaxrpc.jbws1010;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * [JBWS-1010] - Support for inherited service endpoint interfaces - JAX-RPC 5.5.4
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1010
 * 
 * @author darran.lofthouse@jboss.com
 * @since 27-June-2006
 */
public class JBWS1010TestCase extends JBossWSTest
{

   private static InheritenceChildInterface port;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1010TestCase.class, "jaxrpc-jbws1010.war, jaxrpc-jbws1010-client.jar");
   }

   public void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (InheritenceChildInterface)service.getPort(InheritenceChildInterface.class);
      }
   }

   public void testGetA() throws Exception
   {
      String response = port.getA();
      assertEquals("A", response);
   }

   public void testGetB() throws Exception
   {
      String response = port.getB();
      assertEquals("B", response);
   }
}
