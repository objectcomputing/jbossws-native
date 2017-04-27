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
package org.jboss.test.ws.jaxrpc.samples.jsr109ejb;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.ws.core.jaxrpc.client.ServiceImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test the DynamicProxy Call
 *
 * @author Thomas.Diesler@jboss.org
 * @since 06-Jan-2005
 */
public class RpcProxyTestCase extends JBossWSTest
{
   private final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxrpc-samples-jsr109ejb-rpc";
   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/samples/jsr109ejb";

   private static JaxRpcTestService port;

   public static Test suite()
   {
      return new JBossWSTestSetup(RpcProxyTestCase.class, "jaxrpc-samples-jsr109ejb-rpc.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      if (port == null)
      {
         File javaWsdlMappingFile = getResourceFile("jaxrpc/samples/jsr109ejb/rpclit/META-INF/jaxrpc-mapping.xml");
         assertTrue(javaWsdlMappingFile.exists());

         QName serviceName = new QName(TARGET_NAMESPACE, "TestService");
         ServiceFactoryImpl factory = new ServiceFactoryImpl();
         URL wsdlLocation = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");
         ServiceImpl service = (ServiceImpl)factory.createService(wsdlLocation, serviceName, javaWsdlMappingFile.toURL());
         port = (JaxRpcTestService)service.getPort(JaxRpcTestService.class);
      }
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
}
