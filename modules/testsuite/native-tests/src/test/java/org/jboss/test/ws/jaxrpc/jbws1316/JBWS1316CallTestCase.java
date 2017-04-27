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
package org.jboss.test.ws.jaxrpc.jbws1316;

import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.rpc.Stub;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test case to test reading the TimestampVerification configuration.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 * @since Aril 14 2008
 */
public class JBWS1316CallTestCase extends JBossWSTest
{

   private static final String MESSAGE = "Hello JBWS1316!!";

   private static TestEndpoint port;

   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS1316CallTestCase.class, "jaxrpc-jbws1316.war, jaxrpc-jbws1316-client.jar");
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

   /**
    * Test that a message without a wsse:Security header is rejected.
    * 
    * @throws Exception
    */
   public void testCall_NoSecurity() throws Exception
   {
      Stub stub = (Stub)port;
      stub._setProperty(TestSecurityHandler.JBWS1316_CREATED, null);
      stub._setProperty(TestSecurityHandler.JBWS1316_EXPIRES, null);

      try
      {
         port.echoMessage(MESSAGE);
         fail("Call should have failed due to no wsse:Security.");
      }
      catch (RemoteException e)
      {
         assertTrue(e.getMessage().contains("[InvalidSecurity]"));
      }
   }

   public void testCall_ValidTimestamp() throws Exception
   {
      long started = System.currentTimeMillis();

      Stub stub = (Stub)port;
      stub._setProperty(TestSecurityHandler.JBWS1316_CREATED, Integer.valueOf(0));
      stub._setProperty(TestSecurityHandler.JBWS1316_EXPIRES, Integer.valueOf(10));

      String response = port.echoMessage(MESSAGE);
      assertEquals("Expected Response", MESSAGE, response);

      long finished = System.currentTimeMillis();
      long duration = finished - started;
      assertTrue("Execution time less than 10s", duration < 10000);
   }

   public void testCall_FutureTimestamp() throws Exception
   {
      long started = System.currentTimeMillis();

      Stub stub = (Stub)port;
      stub._setProperty(TestSecurityHandler.JBWS1316_CREATED, Integer.valueOf(10));
      stub._setProperty(TestSecurityHandler.JBWS1316_EXPIRES, Integer.valueOf(20));

      String response = port.echoMessage(MESSAGE);
      assertEquals("Expected Response", MESSAGE, response);

      long finished = System.currentTimeMillis();
      long duration = finished - started;
      assertTrue("Execution time less than 10s", duration < 10000);
   }

   public void testCall_PastExpires() throws Exception
   {
      long started = System.currentTimeMillis();

      Stub stub = (Stub)port;
      stub._setProperty(TestSecurityHandler.JBWS1316_CREATED, Integer.valueOf(0));
      stub._setProperty(TestSecurityHandler.JBWS1316_EXPIRES, Integer.valueOf(-5));

      String response = port.echoMessage(MESSAGE);
      assertEquals("Expected Response", MESSAGE, response);

      long finished = System.currentTimeMillis();
      long duration = finished - started;
      assertTrue("Execution time less than 10s", duration < 10000);
   }

}
