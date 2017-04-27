/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.test.ws.jaxrpc.jbws1862;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.holders.StringHolder;

import junit.framework.Test;

import org.jboss.ws.Constants;
import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * [JBWS-1862] http://jira.jboss.com/jira/browse/JBWS-1862
 * 
 * @author darran.lofthouse@jboss.com
 * @since Oct 22, 2007
 */
public class JBWS1862TestCase extends JBossWSTest
{

   private final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxrpc-jbws1862/TestEndpoint";
   private static final String NAMESPACE = "http://org.jboss.test.ws/jbws1862";

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1862TestCase.class, "jaxrpc-jbws1862.war");
   }

   public void testUnconfiguredCall_SetReturnType() throws Exception
   {
      QName serviceName = new QName(NAMESPACE, "TestService");
      QName operationName = new QName(NAMESPACE, "echo");

      ServiceFactory factory = ServiceFactory.newInstance();
      Service service = factory.createService(serviceName);

      Call call = (Call)service.createCall();
      call.setOperationName(operationName);
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);
      call.setReturnType(Constants.TYPE_LITERAL_STRING, String.class);

      String message = "Hello World!!";
      String response = (String)call.invoke(new Object[] { message });
      assertEquals(message, response);
   }

   public void testUnconfiguredCall_AddOutParam() throws Exception
   {
      QName serviceName = new QName(NAMESPACE, "TestService");
      QName operationName = new QName(NAMESPACE, "echo");

      ServiceFactory factory = ServiceFactory.newInstance();
      Service service = factory.createService(serviceName);

      Call call = (Call)service.createCall();
      call.setOperationName(operationName);
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);
      call.addParameter("String_2", Constants.TYPE_LITERAL_STRING, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      String message = "Hello World!!";
      StringHolder holder = new StringHolder();
      String response = (String)call.invoke(new Object[] { message, holder });
      assertNull(response);
      assertEquals(message, holder.value);
   }

   public void testConfiguredCall() throws Exception
   {
      QName serviceName = new QName(NAMESPACE, "TestService");
      QName operationName = new QName(NAMESPACE, "echo");
      URL jaxrpcMapping = getResourceURL("jaxrpc/jbws1862/WEB-INF/jaxrpc-mapping.xml");
      URL wsdlLocation = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");

      ServiceFactoryImpl factory = (ServiceFactoryImpl)ServiceFactoryImpl.newInstance();
      Service service = factory.createService(wsdlLocation, serviceName, jaxrpcMapping);

      Call call = (Call)service.createCall();
      call.setOperationName(operationName);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      String message = "Hello World!!";
      String response = (String)call.invoke(new Object[] { message });
      assertEquals(message, response);
   }

   public void testPartlyConfiguredCall() throws Exception
   {
      QName serviceName = new QName(NAMESPACE, "TestService");
      QName operationName = new QName(NAMESPACE, "echo");

      URL wsdlLocation = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");

      ServiceFactory factory = (ServiceFactory)ServiceFactory.newInstance();
      Service service = factory.createService(wsdlLocation, serviceName);

      Call call = (Call)service.createCall();
      call.setOperationName(operationName);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      String message = "Hello World!!";
      String response = (String)call.invoke(new Object[] { message });
      assertEquals(message, response);
   }
}
