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
package org.jboss.test.ws.jaxrpc.samples.handler;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.holders.StringHolder;

import junit.framework.Test;

import org.jboss.ws.Constants;
import org.jboss.ws.core.jaxrpc.client.CallImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test the Dynamic Invocation Interface (DII) on the Call
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Jan-2005
 */
public class HeaderDIITestCase extends JBossWSTest
{
   public final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxrpc-samples-handler";
   private static final String NAMESPACE_URI = "http://org.jboss.ws/samples/handler";
   private static final String HEADER_NAMESPACE_URI = "http://org.jboss.ws/samples/handler/types";
   public static Test suite()
   {
      return new JBossWSTestSetup(HeaderDIITestCase.class, "jaxrpc-samples-handler.war");
   }

   public void testBoundInHeader() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "testInHeader"));
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);

      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      // Add a bound header to the call
      CallImpl mycall = (CallImpl)call;
      QName xmlName = new QName(HEADER_NAMESPACE_URI, "headerMsg");
      mycall.addParameter(xmlName, Constants.TYPE_LITERAL_STRING, String.class, ParameterMode.IN, true);

      Object retObj = call.invoke(new Object[]{"Hello world!", "IN header message"});
      assertNull(retObj);
   }

   public void testBoundInOutHeader() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "testInOutHeader"));
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);

      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      // Add a bound header to the call
      CallImpl mycall = (CallImpl)call;
      QName xmlName = new QName(HEADER_NAMESPACE_URI, "headerMsg");
      mycall.addParameter(xmlName, Constants.TYPE_LITERAL_STRING, String.class, ParameterMode.INOUT, true);

      StringHolder holder = new StringHolder("INOUT header message");
      Object retObj = call.invoke(new Object[]{"Hello world!", holder});
      assertNull(retObj);

      Map outputParams = call.getOutputParams();
      Object headerValue = outputParams.get(xmlName.getLocalPart());
      assertEquals("INOUT header message - response", headerValue);
      assertEquals("INOUT header message - response", holder.value);
   }

   public void testBoundOutHeader() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "testOutHeader"));
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);

      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      // Add a bound header to the call
      CallImpl mycall = (CallImpl)call;
      QName xmlName = new QName(HEADER_NAMESPACE_URI, "headerMsg");
      mycall.addParameter(xmlName, Constants.TYPE_LITERAL_STRING, String.class, ParameterMode.OUT, true);

      StringHolder holder = new StringHolder();
      Object retObj = call.invoke(new Object[]{"Hello world!", holder});
      assertNull(retObj);

      Map outputParams = call.getOutputParams();
      Object headerValue = outputParams.get(xmlName.getLocalPart());
      assertEquals("OUT header message", headerValue);
      assertEquals("OUT header message", holder.value);
   }

   public void testUnboundInHeader() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      CallImpl call = (CallImpl)service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "testInHeader"));
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);

      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      // Add a bound header to the call
      CallImpl mycall = (CallImpl)call;
      QName xmlName = new QName(HEADER_NAMESPACE_URI, "headerMsg");
      mycall.addParameter(xmlName, Constants.TYPE_LITERAL_STRING, String.class, ParameterMode.IN, true);

      // Add an unbound header to the call
      xmlName = new QName("http://otherns", "HeaderValue");
      call.addUnboundHeader(xmlName, Constants.TYPE_LITERAL_STRING, String.class, ParameterMode.IN);
      call.setUnboundHeaderValue(xmlName, "Unbound IN header message");

      Object retObj = call.invoke(new Object[]{"Hello world!", "IN header message"});
      assertNull(retObj);

      String unboundRet = (String)call.getUnboundHeaderValue(xmlName);
      assertEquals("Unbound OUT header message", unboundRet);
   }

   public void testUnboundInOutHeader() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      CallImpl call = (CallImpl)service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "testInOutHeader"));
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);

      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      // Add a bound header to the call
      CallImpl mycall = (CallImpl)call;
      QName xmlName = new QName(HEADER_NAMESPACE_URI, "headerMsg");
      mycall.addParameter(xmlName, Constants.TYPE_LITERAL_STRING, String.class, ParameterMode.INOUT, true);

      // Add an unbound header to the call
      xmlName = new QName("http://otherns", "HeaderValue");
      call.addUnboundHeader(xmlName, Constants.TYPE_LITERAL_STRING, String.class, ParameterMode.INOUT);
      call.setUnboundHeaderValue(xmlName, "Unbound INOUT header message");

      StringHolder boundHeader = new StringHolder("INOUT header message");
      Object retObj = call.invoke(new Object[]{"Hello world!", boundHeader});
      assertNull(retObj);

      assertEquals("INOUT header message - response", boundHeader.value);

      String unboundRet = (String)call.getUnboundHeaderValue(xmlName);
      assertEquals("Unbound OUT header message", unboundRet);
   }


   public void testUnboundOutHeader() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      CallImpl call = (CallImpl)service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "testOutHeader"));
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);

      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      // Add a bound header to the call
      CallImpl mycall = (CallImpl)call;
      QName xmlName = new QName(HEADER_NAMESPACE_URI, "headerMsg");
      mycall.addParameter(xmlName, Constants.TYPE_LITERAL_STRING, String.class, ParameterMode.OUT, true);

      // Add an unbound header to the call
      xmlName = new QName("http://otherns", "HeaderValue");
      call.addUnboundHeader(xmlName, Constants.TYPE_LITERAL_STRING, String.class, ParameterMode.OUT);

      StringHolder boundHeader = new StringHolder();
      Object retObj = call.invoke(new Object[]{"Hello world!", boundHeader});
      assertNull(retObj);

      assertEquals("OUT header message", boundHeader.value);

      String unboundRet = (String)call.getUnboundHeaderValue(xmlName);
      assertEquals("Unbound OUT header message", unboundRet);
   }
}
