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
import java.util.Properties;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.rpc.Stub;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test UsernameToken
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class UsernameTestCase extends JBossWSTest
{
   private static JaxRpcTestService port;
   private String keyStore;
   private String trustStore;
   private String keyStorePassword;
   private String trustStorePassword;
   private String keyStoreType;
   private String trustStoreType;

   public static Test suite()
   {
      return new JBossWSTestSetup(UsernameTestCase.class, "jaxrpc-wsse-username.jar, jaxrpc-wsse-username-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestServiceEJB");
         port = (JaxRpcTestService)service.getPort(JaxRpcTestService.class);
      }

      //Backup values
      keyStore = System.getProperty("org.jboss.ws.wsse.keyStore");
      keyStorePassword = System.getProperty("org.jboss.ws.wsse.keyStorePassword");
      keyStoreType = System.getProperty("org.jboss.ws.wsse.keyStoreType");
      trustStore = System.getProperty("org.jboss.ws.wsse.trustStore");
      trustStorePassword = System.getProperty("org.jboss.ws.wsse.trustStorePassword");
      trustStoreType = System.getProperty("org.jboss.ws.wsse.trustStoreType");
      //Clear environment
      Properties props = System.getProperties();
      props.remove("org.jboss.ws.wsse.keyStore");
      props.remove("org.jboss.ws.wsse.trustStore");
      props.remove("org.jboss.ws.wsse.keyStorePassword");
      props.remove("org.jboss.ws.wsse.trustStorePassword");
      props.remove("org.jboss.ws.wsse.keyStoreType");
      props.remove("org.jboss.ws.wsse.trustStoreType");
   }
   
   protected void tearDown() throws Exception
   {
      //Restore environment
      System.setProperty("org.jboss.ws.wsse.keyStore", keyStore);
      System.setProperty("org.jboss.ws.wsse.trustStore", trustStore);
      System.setProperty("org.jboss.ws.wsse.keyStorePassword", keyStorePassword);
      System.setProperty("org.jboss.ws.wsse.trustStorePassword", trustStorePassword);
      System.setProperty("org.jboss.ws.wsse.keyStoreType", keyStoreType);
      System.setProperty("org.jboss.ws.wsse.trustStoreType", trustStoreType);
      super.tearDown();
   }

   public void testEchoString() throws Exception
   {
      String hello = "Hello";
      String world = "world!";
      Object retObj = port.echoString(hello, world);
      assertEquals(hello + world, retObj);
   }

   public void testEchoSimpleUserType() throws Exception
   {
      String hello = "Hello";
      SimpleUserType userType = new SimpleUserType(1, 2);
      Object retObj = port.echoSimpleUserType(hello, userType);
      assertEquals(userType, retObj);
   }

   public void testEchoStringNoUsername() throws Exception
   {
      String hello = "Hello";
      String world = "world!";

      ((Stub)port)._setProperty(Stub.USERNAME_PROPERTY, null);
      ((Stub)port)._setProperty(Stub.PASSWORD_PROPERTY, null);

      try
      {
         port.echoString(hello, world);
         fail("Expected exception not thrown");
      }
      catch (RemoteException e)
      {
      }
   }
}
