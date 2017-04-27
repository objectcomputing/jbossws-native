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
package org.jboss.test.ws.jaxrpc.wsse;

import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test WS-Security service handling of a non signed/encrypted client
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class NotEncodedTestCase extends JBossWSTest
{
   /** Construct the test case with a given name
    */

   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(NotEncodedTestCase.class, "jaxrpc-wsse-rpc.war, jaxrpc-wsse-rpc-none-client.jar");
   }

   /**
    * Test JSE endpoint
    */
   public void testEndpoint() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
      Hello hello = (Hello)service.getPort(Hello.class);

      try
      {
         UserType in0 = new UserType("Kermit");
         hello.echoUserType(in0);
         fail("RemoteException expected");
      }
      catch (RemoteException ex)
      {
         String message = ex.getMessage();
         assertTrue("Unexpected message: " + message, message.indexOf("requires <wsse:Security>") >= 0);
      }
   }
}
