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
package org.jboss.test.ws.jaxrpc.outparam;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.holders.BigDecimalHolder;
import javax.xml.rpc.holders.BigIntegerHolder;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.BooleanWrapperHolder;
import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.ByteHolder;
import javax.xml.rpc.holders.ByteWrapperHolder;
import javax.xml.rpc.holders.CalendarHolder;
import javax.xml.rpc.holders.DoubleHolder;
import javax.xml.rpc.holders.DoubleWrapperHolder;
import javax.xml.rpc.holders.FloatHolder;
import javax.xml.rpc.holders.FloatWrapperHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.IntegerWrapperHolder;
import javax.xml.rpc.holders.LongHolder;
import javax.xml.rpc.holders.LongWrapperHolder;
import javax.xml.rpc.holders.QNameHolder;
import javax.xml.rpc.holders.ShortHolder;
import javax.xml.rpc.holders.ShortWrapperHolder;
import javax.xml.rpc.holders.StringHolder;

import junit.framework.Test;

import org.jboss.ws.Constants;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test DII with Holders
 *
 * @author Thomas.Diesler@jboss.org
 * @since 22-Dec-2004
 */
public class OutParamDIITestCase extends JBossWSTest
{
   private final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxrpc-outparam";
   private static final String NAMESPACE_URI = "http://org.jboss.ws/outparam";

   public static Test suite()
   {
      return new JBossWSTestSetup(OutParamDIITestCase.class, "jaxrpc-outparam.war");
   }

   public void testEchoBigDecimal() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoBigDecimal"));
      call.addParameter("BigDecimal_1", Constants.TYPE_LITERAL_DECIMAL, ParameterMode.IN);
      call.addParameter("BigDecimal_2", Constants.TYPE_LITERAL_DECIMAL, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      BigDecimalHolder holder = new BigDecimalHolder();
      BigDecimal in = new BigDecimal("1000");
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      BigDecimal out = (BigDecimal)call.getOutputParams().get("BigDecimal_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }

   public void testEchoBigInteger() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoBigInteger"));
      call.addParameter("BigInteger_1", Constants.TYPE_LITERAL_INTEGER, ParameterMode.IN);
      call.addParameter("BigInteger_2", Constants.TYPE_LITERAL_INTEGER, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      BigIntegerHolder holder = new BigIntegerHolder();
      BigInteger in = new BigInteger("1001");
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      BigInteger out = (BigInteger)call.getOutputParams().get("BigInteger_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }

   public void testEchoBoolean() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoBoolean"));
      call.addParameter("boolean_1", Constants.TYPE_LITERAL_BOOLEAN, ParameterMode.IN);
      call.addParameter("boolean_2", Constants.TYPE_LITERAL_BOOLEAN, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      BooleanHolder holder = new BooleanHolder();
      Boolean in = new Boolean(true);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Boolean out = (Boolean)call.getOutputParams().get("boolean_2");
      assertEquals(in, out);
      assertEquals(true, holder.value);
   }

   public void testEchoBooleanWrapper() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoBooleanWrapper"));
      call.addParameter("Boolean_1", Constants.TYPE_LITERAL_BOOLEAN, ParameterMode.IN);
      call.addParameter("Boolean_2", Constants.TYPE_LITERAL_BOOLEAN, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      BooleanWrapperHolder holder = new BooleanWrapperHolder();
      Boolean in = new Boolean(true);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Boolean out = (Boolean)call.getOutputParams().get("Boolean_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }

   public void testEchoByteArray() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoByteArray"));
      call.addParameter("arrayOfbyte_1", Constants.TYPE_LITERAL_BASE64BINARY, ParameterMode.IN);
      call.addParameter("arrayOfbyte_2", Constants.TYPE_LITERAL_BASE64BINARY, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      ByteArrayHolder holder = new ByteArrayHolder();
      String in = new String("Some base64 msg");
      Object retObj = call.invoke(new Object[]{in.getBytes(), holder});
      assertNull(retObj);

      byte[] out = (byte[])call.getOutputParams().get("arrayOfbyte_2");
      assertEquals(in, new String(out));
      assertEquals(in, new String(holder.value));
   }

   public void testEchoByte() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoByte"));
      call.addParameter("byte_1", Constants.TYPE_LITERAL_BYTE, ParameterMode.IN);
      call.addParameter("byte_2", Constants.TYPE_LITERAL_BYTE, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      ByteHolder holder = new ByteHolder();
      Byte in = new Byte((byte)0x45);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Byte out = (Byte)call.getOutputParams().get("byte_2");
      assertEquals(in, out);
      assertEquals(in, new Byte(holder.value));
   }

   public void testEchoByteWrapper() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoByteWrapper"));
      call.addParameter("Byte_1", Constants.TYPE_LITERAL_BYTE, ParameterMode.IN);
      call.addParameter("Byte_2", Constants.TYPE_LITERAL_BYTE, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      ByteWrapperHolder holder = new ByteWrapperHolder();
      Byte in = new Byte((byte)0x45);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Byte out = (Byte)call.getOutputParams().get("Byte_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }

   public void testEchoCalendar() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoCalendar"));
      call.addParameter("Calendar_1", Constants.TYPE_LITERAL_DATETIME, ParameterMode.IN);
      call.addParameter("Calendar_2", Constants.TYPE_LITERAL_DATETIME, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      CalendarHolder holder = new CalendarHolder();
      Calendar in = new GregorianCalendar(2004, 11, 23, 11, 58, 23);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Calendar out = (Calendar)call.getOutputParams().get("Calendar_2");
      assertEquals(in.getTime().getTime(), out.getTime().getTime());
      assertEquals(in.getTime().getTime(), holder.value.getTime().getTime());
   }

   public void testEchoDouble() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoDouble"));
      call.addParameter("double_1", Constants.TYPE_LITERAL_DOUBLE, ParameterMode.IN);
      call.addParameter("double_2", Constants.TYPE_LITERAL_DOUBLE, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      DoubleHolder holder = new DoubleHolder();
      Double in = new Double(1.2);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Double out = (Double)call.getOutputParams().get("double_2");
      assertEquals(in, out);
      assertEquals(in, new Double(holder.value));
   }

   public void testEchoDoubleWrapper() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoDoubleWrapper"));
      call.addParameter("Double_1", Constants.TYPE_LITERAL_DOUBLE, ParameterMode.IN);
      call.addParameter("Double_2", Constants.TYPE_LITERAL_DOUBLE, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      DoubleWrapperHolder holder = new DoubleWrapperHolder();
      Double in = new Double(1.2);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Double out = (Double)call.getOutputParams().get("Double_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }

   public void testEchoFloat() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoFloat"));
      call.addParameter("float_1", Constants.TYPE_LITERAL_FLOAT, ParameterMode.IN);
      call.addParameter("float_2", Constants.TYPE_LITERAL_FLOAT, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      FloatHolder holder = new FloatHolder();
      Float in = new Float(1.2);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Float out = (Float)call.getOutputParams().get("float_2");
      assertEquals(in, out);
      assertEquals(in, new Float(holder.value));
   }

   public void testEchoFloatWrapper() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoFloatWrapper"));
      call.addParameter("Float_1", Constants.TYPE_LITERAL_FLOAT, ParameterMode.IN);
      call.addParameter("Float_2", Constants.TYPE_LITERAL_FLOAT, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      FloatWrapperHolder holder = new FloatWrapperHolder();
      Float in = new Float(1.2);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Float out = (Float)call.getOutputParams().get("Float_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }

   public void testEchoInt() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoInt"));
      call.addParameter("int_1", Constants.TYPE_LITERAL_INT, ParameterMode.IN);
      call.addParameter("int_2", Constants.TYPE_LITERAL_INT, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      IntHolder holder = new IntHolder();
      Integer in = new Integer(1);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Integer out = (Integer)call.getOutputParams().get("int_2");
      assertEquals(in, out);
      assertEquals(in, new Integer(holder.value));
   }

   public void testEchoIntegerWrapper() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoIntegerWrapper"));
      call.addParameter("Integer_1", Constants.TYPE_LITERAL_INT, ParameterMode.IN);
      call.addParameter("Integer_2", Constants.TYPE_LITERAL_INT, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      IntegerWrapperHolder holder = new IntegerWrapperHolder();
      Integer in = new Integer(1);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Integer out = (Integer)call.getOutputParams().get("Integer_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }

   public void testEchoLong() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoLong"));
      call.addParameter("long_1", Constants.TYPE_LITERAL_LONG, ParameterMode.IN);
      call.addParameter("long_2", Constants.TYPE_LITERAL_LONG, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      LongHolder holder = new LongHolder();
      Long in = new Long(1);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Long out = (Long)call.getOutputParams().get("long_2");
      assertEquals(in, out);
      assertEquals(in, new Long(holder.value));
   }

   public void testEchoLongWrapper() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoLongWrapper"));
      call.addParameter("Long_1", Constants.TYPE_LITERAL_LONG, ParameterMode.IN);
      call.addParameter("Long_2", Constants.TYPE_LITERAL_LONG, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      LongWrapperHolder holder = new LongWrapperHolder();
      Long in = new Long(1);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Long out = (Long)call.getOutputParams().get("Long_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }

   public void testEchoQName() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoQName"));
      call.addParameter("QName_1", Constants.TYPE_LITERAL_QNAME, ParameterMode.IN);
      call.addParameter("QName_2", Constants.TYPE_LITERAL_QNAME, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      QNameHolder holder = new QNameHolder();
      QName in = new QName("http://somens", "localPart", "ns1");
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      QName out = (QName)call.getOutputParams().get("QName_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }

   public void testEchoShort() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoShort"));
      call.addParameter("short_1", Constants.TYPE_LITERAL_SHORT, ParameterMode.IN);
      call.addParameter("short_2", Constants.TYPE_LITERAL_SHORT, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      ShortHolder holder = new ShortHolder();
      Short in = new Short((short)1);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Short out = (Short)call.getOutputParams().get("short_2");
      assertEquals(in, out);
      assertEquals(in, new Short(holder.value));
   }

   public void testEchoShortWrapper() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoShortWrapper"));
      call.addParameter("Short_1", Constants.TYPE_LITERAL_SHORT, ParameterMode.IN);
      call.addParameter("Short_2", Constants.TYPE_LITERAL_SHORT, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      ShortWrapperHolder holder = new ShortWrapperHolder();
      Short in = new Short((short)1);
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      Short out = (Short)call.getOutputParams().get("Short_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }

   public void testEchoString() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));

      Call call = service.createCall();
      call.setOperationName(new QName(NAMESPACE_URI, "echoString"));
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, ParameterMode.IN);
      call.addParameter("String_2", Constants.TYPE_LITERAL_STRING, ParameterMode.OUT);
      call.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

      StringHolder holder = new StringHolder();
      String in = new String("Hello world!");
      Object retObj = call.invoke(new Object[]{in, holder});
      assertNull(retObj);

      String out = (String)call.getOutputParams().get("String_2");
      assertEquals(in, out);
      assertEquals(in, holder.value);
   }
}
