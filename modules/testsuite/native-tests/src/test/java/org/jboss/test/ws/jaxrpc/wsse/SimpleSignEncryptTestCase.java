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

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test WS-Security with RPC/Literal
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class SimpleSignEncryptTestCase extends JBossWSTest
{
   private String keyStore;
   private String trustStore;
   private String keyStorePassword;
   private String trustStorePassword;
   private String keyStoreType;
   private String trustStoreType;
   
   /** Construct the test case with a given name
    */

   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(SimpleSignEncryptTestCase.class, "jaxrpc-wsse-simple-sign-encrypt.war, jaxrpc-wsse-simple-sign-encrypt-client.jar");
   }

   public void testEndpoint() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
      Hello hello = (Hello)service.getPort(Hello.class);

      UserType in0 = new UserType("Kermit");
      UserType retObj = hello.echoUserType(in0);
      assertEquals(in0, retObj);
   }

   public void testEndpointNoProperties() throws Exception
   {
      clearEnvironment();
      try
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
         Hello hello = (Hello)service.getPort(Hello.class);
   
         UserType in0 = new UserType("Kermit");
   
         try
         {
            hello.echoUserType(in0);
            fail("Expected exception not thrown");
         }
         catch (RemoteException e)
         {
         }
      }
      finally
      {
         restoreEnvironment();
      }
   }

   private void clearEnvironment()
   {
      //Backup values
      keyStore = System.getProperty("org.jboss.ws.wsse.keyStore");
      keyStorePassword = System.getProperty("org.jboss.ws.wsse.keyStorePassword");
      keyStoreType = System.getProperty("org.jboss.ws.wsse.keyStoreType");
      trustStore = System.getProperty("org.jboss.ws.wsse.trustStore");
      trustStorePassword = System.getProperty("org.jboss.ws.wsse.trustStorePassword");
      trustStoreType = System.getProperty("org.jboss.ws.wsse.trustStoreType");
      //Clear
      Properties props = System.getProperties();
      props.remove("org.jboss.ws.wsse.keyStore");
      props.remove("org.jboss.ws.wsse.trustStore");
      props.remove("org.jboss.ws.wsse.keyStorePassword");
      props.remove("org.jboss.ws.wsse.trustStorePassword");
      props.remove("org.jboss.ws.wsse.keyStoreType");
      props.remove("org.jboss.ws.wsse.trustStoreType");
   }

   private void restoreEnvironment()
   {
      System.setProperty("org.jboss.ws.wsse.keyStore", keyStore);
      System.setProperty("org.jboss.ws.wsse.trustStore", trustStore);
      System.setProperty("org.jboss.ws.wsse.keyStorePassword", keyStorePassword);
      System.setProperty("org.jboss.ws.wsse.trustStorePassword", trustStorePassword);
      System.setProperty("org.jboss.ws.wsse.keyStoreType", keyStoreType);
      System.setProperty("org.jboss.ws.wsse.trustStoreType", trustStoreType);
   }
}
