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
package org.jboss.test.ws.jaxrpc.samples.jsr109pojo;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.ws.Constants;
import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test the Dynamic Invocation Interface (DII) on the Call
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Oct-2004
 */
public class RpcDIIUnconfiguredCallTestCase extends JBossWSTest
{
   private final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxrpc-samples-jsr109pojo-rpc";
   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/samples/jsr109pojo";

   public static Test suite()
   {
      return new JBossWSTestSetup(RpcDIIUnconfiguredCallTestCase.class, "jaxrpc-samples-jsr109pojo-rpc.war");
   }

   public void testEchoString() throws Exception
   {
      ServiceFactoryImpl factory = new ServiceFactoryImpl();
      Service service = factory.createService(new QName("ANY_SERVICE_NAME"));

      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "echoString"));
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);
      call.addParameter("String_2", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_STRING);

      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      String hello = "Hello";
      String world = "world!";
      Object retObj = call.invoke(new Object[]{hello, world});
      assertEquals(hello + world, retObj);

      List outValues = call.getOutputValues();
      assertEquals(0, outValues.size());

      Map outParams = call.getOutputParams();
      assertEquals(0, outParams.size());
   }

   public void testEchoSimpleUserType() throws Exception
   {
      ServiceFactoryImpl factory = new ServiceFactoryImpl();
      Service service = factory.createService(new QName("ANY_SERVICE_NAME"));

      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "echoSimpleUserType"));
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);
      call.addParameter("SimpleUserType_2", new QName(TARGET_NAMESPACE, "SimpleUserType"), SimpleUserType.class, ParameterMode.IN);
      call.setReturnType(new QName(TARGET_NAMESPACE, "SimpleUserType"), SimpleUserType.class);

      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      String hello = "Hello";
      SimpleUserType userType = new SimpleUserType(1, 2);
      Object retObj = call.invoke(new Object[]{hello, userType});
      assertEquals(userType, retObj);
   }
}
