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
package org.jboss.test.ws.jaxrpc.encoded.marshalltest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test soap encoded marshalling
 *
 * @author Thomas.Diesler@jboss.org
 * @since 31-Oct-2005
 */
public class MarshallTestCase extends JBossWSTest
{
   private static MarshallTest port;

   public static Test suite()
   {
      return new JBossWSTestSetup(MarshallTestCase.class, "jaxrpc-encoded-marshalltest.war, jaxrpc-encoded-marshalltest-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (MarshallTest)service.getPort(MarshallTest.class);
      }
   }

   public void testBigDecimalArrayTest() throws Exception
   {
      BigDecimal[] inObj = new BigDecimal[] { new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300") };
      BigDecimal[] retObj = port.bigDecimalArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

  public void testBigDecimalMultiArrayTest() throws Exception
   {
      BigDecimal[] arr1 = new BigDecimal[] { new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300") };
      BigDecimal[] arr2 = new BigDecimal[] { new BigDecimal("400"), new BigDecimal("500"), new BigDecimal("600") };
      BigDecimal[][] inObj = new BigDecimal[][] { arr1, arr2 };
      BigDecimal[][] retObj = port.bigDecimalMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testBigDecimalTest() throws Exception
   {
      BigDecimal inObj = new BigDecimal("100");
      BigDecimal retObj = port.bigDecimalTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testBigIntegerArrayTest() throws Exception
   {
      BigInteger[] inObj = new BigInteger[] { new BigInteger("100"), new BigInteger("200"), new BigInteger("300") };
      BigInteger[] retObj = port.bigIntegerArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testBigIntegerMultiArrayTest() throws Exception
   {
      BigInteger[] arr1 = new BigInteger[] { new BigInteger("100"), new BigInteger("200"), new BigInteger("300") };
      BigInteger[] arr2 = new BigInteger[] { new BigInteger("400"), new BigInteger("500"), new BigInteger("600") };
      BigInteger[][] inObj = new BigInteger[][] { arr1, arr2 };
      BigInteger[][] retObj = port.bigIntegerMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testBigIntegerTest() throws Exception
   {
      BigInteger inObj = new BigInteger("100");
      BigInteger retObj = port.bigIntegerTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testBooleanArrayTest() throws Exception
   {
      boolean[] inObj = new boolean[] { true, false, true };
      boolean[] retObj = port.booleanArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testBooleanMultiArrayTest() throws Exception
   {
      boolean[] arr1 = new boolean[] { true, false, true };
      boolean[] arr2 = new boolean[] { false, true, false };
      boolean[][] inObj = new boolean[][] { arr1, arr2 };
      boolean[][] retObj = port.booleanMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testBooleanTest() throws Exception
   {
      boolean inObj = true;
      boolean retObj = port.booleanTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testByteArrayTest() throws Exception
   {
      byte[] inObj = new byte[] { 0x01, 0x02, 0x03 };
      byte[] retObj = port.byteArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testByteMultiArrayTest() throws Exception
   {
      byte[] arr1 = new byte[] { 0x01, 0x02, 0x03 };
      byte[] arr2 = new byte[] { 0x04, 0x05, 0x06 };
      byte[][] inObj = new byte[][] { arr1, arr2 };
      byte[][] retObj = port.byteMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testByteTest() throws Exception
   {
      byte inObj = 0x01;
      byte retObj = port.byteTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testCalendarArrayTest() throws Exception
   {
      Calendar cal1 = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal2 = new GregorianCalendar(1969, 6, 17, 15, 24, 56);
      cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal3 = new GregorianCalendar(1970, 7, 18, 16, 25, 57);
      cal3.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar[] inObj = new Calendar[] { cal1, cal2, cal3 };
      Calendar[] retObj = port.calendarArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testCalendarMultiArrayTest() throws Exception
   {
      Calendar cal1 = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal2 = new GregorianCalendar(1969, 6, 17, 15, 24, 56);
      cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal3 = new GregorianCalendar(1970, 7, 18, 16, 25, 57);
      cal3.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar[] arr1 = new Calendar[] { cal1, cal2, cal3 };
      Calendar[] arr2 = new Calendar[] { cal1, cal2, cal3 };
      Calendar[][] inObj = new Calendar[][] { arr1, arr2 };
      Calendar[][] retObj = port.calendarMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testCalendarTest() throws Exception
   {
      Calendar inObj = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      inObj.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar retObj = port.calendarTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testDoubleArrayTest() throws Exception
   {
      double[] inObj = new double[] { 1.1, 2.2, 3.3 };
      double[] retObj = port.doubleArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testDoubleMultiArrayTest() throws Exception
   {
      double[] arr1 = new double[] { 1.1, 2.2, 3.3 };
      double[] arr2 = new double[] { 4.4, 5.5, 6.6 };
      double[][] inObj = new double[][] { arr1, arr2 };
      double[][] retObj = port.doubleMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testDoubleTest() throws Exception
   {
      double inObj = 1.1;
      double retObj = port.doubleTest(inObj);
      assertEquals(inObj, retObj, 2);
   }

   public void testFloatArrayTest() throws Exception
   {
      float[] inObj = new float[] { 1.1f, 2.2f, 3.3f };
      float[] retObj = port.floatArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testFloatMultiArrayTest() throws Exception
   {
      float[] arr1 = new float[] { 1.1f, 2.2f, 3.3f };
      float[] arr2 = new float[] { 4.4f, 5.5f, 6.6f };
      float[][] inObj = new float[][] { arr1, arr2 };
      float[][] retObj = port.floatMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testFloatTest() throws Exception
   {
      float inObj = 1.1f;
      float retObj = port.floatTest(inObj);
      assertEquals(inObj, retObj, 2);
   }

   public void testIntArrayTest() throws Exception
   {
      int[] inObj = new int[] { 1, 2, 3 };
      int[] retObj = port.intArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testIntMultiArrayTest() throws Exception
   {
      int[] arr1 = new int[] { 1, 2, 3 };
      int[] arr2 = new int[] { 4, 5, 6 };
      int[][] inObj = new int[][] { arr1, arr2 };
      int[][] retObj = port.intMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testIntTest() throws Exception
   {
      int inObj = 1;
      int retObj = port.intTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testJavaBeanArrayTest() throws Exception
   {
      JavaBean[] inObj = new JavaBean[] { new JavaBean(), new JavaBean() };
      JavaBean[] retObj = port.javaBeanArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testJavaBeanMultiArrayTest() throws Exception
   {
      JavaBean[] arr1 = new JavaBean[] { new JavaBean(), new JavaBean() };
      JavaBean[] arr2 = new JavaBean[] { new JavaBean(), new JavaBean() };
      JavaBean[][] inObj = new JavaBean[][] { arr1, arr2 };
      JavaBean[][] retObj = port.javaBeanMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testJavaBeanTest() throws Exception
   {
      JavaBean inObj = new JavaBean();

      inObj.setMyBigDecimal(new BigDecimal("100"));
      inObj.setMyBigDecimalArray(new BigDecimal[] { new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300") });
      inObj.setMyBigInteger(new BigInteger("100"));
      inObj.setMyBigIntegerArray(new BigInteger[] { new BigInteger("100"), new BigInteger("200"), new BigInteger("300") });
      inObj.setMyBoolean(true);
      inObj.setMyBoolean1(new Boolean(true));
      inObj.setMyBoolean1Array(new Boolean[] { new Boolean(true), new Boolean(false), new Boolean(true) });
      inObj.setMyBooleanArray(new boolean[] { true, false, true });
      inObj.setMyByte((byte)0x01);
      inObj.setMyByte1(new Byte((byte)0x01));
      inObj.setMyByteArray("byteArray".getBytes());
      Calendar cal1 = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal2 = new GregorianCalendar(1969, 6, 17, 15, 24, 56);
      cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
      inObj.setMyCalendar(cal1);
      inObj.setMyCalendarArray(new Calendar[] { cal1, cal2 });
      inObj.setMyDouble(1.1);
      inObj.setMyDouble1(new Double(1.1));
      inObj.setMyDouble1Array(new Double[] { new Double(1.1), new Double(2.2) });
      inObj.setMyDoubleArray(new double[] { 1.1, 2.2 });
      inObj.setMyFloat(1.1f);
      inObj.setMyFloat1(new Float(1.1f));
      inObj.setMyFloat1Array(new Float[] { new Float(1.1f), new Float(2.2f) });
      inObj.setMyFloatArray(new float[] { 1.1f, 2.2f });
      inObj.setMyInt(1);
      inObj.setMyInt1(new Integer(1));
      inObj.setMyInt1Array(new Integer[] { new Integer(1), new Integer(2) });
      inObj.setMyIntArray(new int[] { 1, 2 });
      inObj.setMyLong(1);
      inObj.setMyLong1(new Long(1));
      inObj.setMyLong1Array(new Long[] { new Long(1), new Long(2) });
      inObj.setMyLongArray(new long[] { 1, 2 });
      inObj.setMyShort((short)1);
      inObj.setMyShort1(new Short((short)1));
      inObj.setMyShort1Array(new Short[] { new Short((short)1), new Short((short)2) });
      inObj.setMyShortArray(new short[] { (short)1, (short)2 });
      inObj.setMyString("String1");
      inObj.setMyStringArray(new String[] { "String1", "String2" });

      //Wont't fix: [JBWS-485] SOAP encoded arrays as bean properties
      //JavaBean retObj = port.javaBeanTest(inObj);
      //assertEquals(inObj, retObj);
   }

   public void testLongArrayTest() throws Exception
   {
      long[] inObj = new long[] { 1, 2, 3 };
      long[] retObj = port.longArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testLongMultiArrayTest() throws Exception
   {
      long[] arr1 = new long[] { 1, 2, 3 };
      long[] arr2 = new long[] { 4, 5, 6 };
      long[][] inObj = new long[][] { arr1, arr2 };
      long[][] retObj = port.longMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testLongTest() throws Exception
   {
      long inObj = 1;
      long retObj = port.longTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testMyServiceExceptionTest() throws Exception
   {
      try
      {
         port.myServiceExceptionTest();
         fail("MyServiceException expected");
      }
      catch (MyServiceException e)
      {
         // ignore
      }
   }

   public void testShortArrayTest() throws Exception
   {
      short[] inObj = new short[] { 1, 2, 3 };
      short[] retObj = port.shortArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testShortMultiArrayTest() throws Exception
   {
      short[] arr1 = new short[] { 1, 2, 3 };
      short[] arr2 = new short[] { 4, 5, 6 };
      short[][] inObj = new short[][] { arr1, arr2 };
      short[][] retObj = port.shortMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testShortTest() throws Exception
   {
      short inObj = 1;
      short retObj = port.shortTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testStringArrayTest() throws Exception
   {
      String[] inObj = new String[] { "String1", "String2", "" };
      String[] retObj = port.stringArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testStringMultiArrayTest() throws Exception
   {
      String[] arr1 = new String[] { "String1", "String2", "" };
      String[] arr2 = new String[] { "String1", "String2", "" };
      String[][] inObj = new String[][] { arr1, arr2 };
      String[][] retObj = port.stringMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testStringTest() throws Exception
   {
      String inObj = "String1";
      String retObj = port.stringTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testValueTypeArrayTest() throws Exception
   {
      ValueType[] inObj = new ValueType[] { new ValueType(), new ValueType() };
      ValueType[] retObj = port.valueTypeArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testValueTypeMultiArrayTest() throws Exception
   {
      ValueType[] arr1 = new ValueType[] { new ValueType(), new ValueType() };
      ValueType[] arr2 = new ValueType[] { new ValueType(), new ValueType() };
      ValueType[][] inObj = new ValueType[][] { arr1, arr2 };
      ValueType[][] retObj = port.valueTypeMultiArrayTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testValueTypeTest() throws Exception
   {
      ValueType inObj = new ValueType();

      inObj.setMyBigDecimal(new BigDecimal("100"));
      inObj.setMyBigDecimalArray(new BigDecimal[] { new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300") });
      inObj.setMyBigInteger(new BigInteger("100"));
      inObj.setMyBigIntegerArray(new BigInteger[] { new BigInteger("100"), new BigInteger("200"), new BigInteger("300") });
      inObj.setMyBoolean(true);
      inObj.setMyBoolean1(new Boolean(true));
      inObj.setMyBoolean1Array(new Boolean[] { new Boolean(true), new Boolean(false), new Boolean(true) });
      inObj.setMyBooleanArray(new boolean[] { true, false, true });
      inObj.setMyByte((byte)0x01);
      inObj.setMyByte1(new Byte((byte)0x01));
      inObj.setMyByteArray("byteArray".getBytes());
      Calendar cal1 = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal2 = new GregorianCalendar(1969, 6, 17, 15, 24, 56);
      cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
      inObj.setMyCalendar(cal1);
      inObj.setMyCalendarArray(new Calendar[] { cal1, cal2 });
      inObj.setMyDouble(1.1);
      inObj.setMyDouble1(new Double(1.1));
      inObj.setMyDouble1Array(new Double[] { new Double(1.1), new Double(2.2) });
      inObj.setMyDoubleArray(new double[] { 1.1, 2.2 });
      inObj.setMyFloat(1.1f);
      inObj.setMyFloat1(new Float(1.1f));
      inObj.setMyFloat1Array(new Float[] { new Float(1.1f), new Float(2.2f) });
      inObj.setMyFloatArray(new float[] { 1.1f, 2.2f });
      inObj.setMyInt(1);
      inObj.setMyInt1(new Integer(1));
      inObj.setMyInt1Array(new Integer[] { new Integer(1), new Integer(2) });
      inObj.setMyIntArray(new int[] { 1, 2 });
      inObj.setMyLong(1);
      inObj.setMyLong1(new Long(1));
      inObj.setMyLong1Array(new Long[] { new Long(1), new Long(2) });
      inObj.setMyLongArray(new long[] { 1, 2 });
      inObj.setMyShort((short)1);
      inObj.setMyShort1(new Short((short)1));
      inObj.setMyShort1Array(new Short[] { new Short((short)1), new Short((short)2) });
      inObj.setMyShortArray(new short[] { (short)1, (short)2 });
      inObj.setMyString("String1");
      inObj.setMyStringArray(new String[] { "String1", "String2" });

      //Wont't fix: [JBWS-485] SOAP encoded arrays as bean properties
      //ValueType retObj = port.valueTypeTest(inObj);
      //assertEquals(inObj, retObj);
   }

   public void testVoidTest() throws Exception
   {
      port.voidTest();
   }

   public void testWrapperBooleanTest() throws Exception
   {
      Boolean inObj = new Boolean("true");
      Boolean retObj = port.wrapperBooleanTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testWrapperByteTest() throws Exception
   {
      Byte inObj = new Byte((byte)0x01);
      Byte retObj = port.wrapperByteTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testWrapperDoubleTest() throws Exception
   {
      Double inObj = new Double("1.1");
      Double retObj = port.wrapperDoubleTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testWrapperFloatTest() throws Exception
   {
      Float inObj = new Float("1.1");
      Float retObj = port.wrapperFloatTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testWrapperIntegerTest() throws Exception
   {
      Integer inObj = new Integer("1");
      Integer retObj = port.wrapperIntegerTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testWrapperLongTest() throws Exception
   {
      Long inObj = new Long("1");
      Long retObj = port.wrapperLongTest(inObj);
      assertEquals(inObj, retObj);
   }

   public void testWrapperShortTest() throws Exception
   {
      Short inObj = new Short("1");
      Short retObj = port.wrapperShortTest(inObj);
      assertEquals(inObj, retObj);
   }   
}
