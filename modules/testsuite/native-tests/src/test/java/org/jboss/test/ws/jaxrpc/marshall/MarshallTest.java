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
package org.jboss.test.ws.jaxrpc.marshall;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.namespace.QName;

import org.jboss.test.ws.jaxrpc.marshall.types.JavaBean;
import org.jboss.test.ws.jaxrpc.marshall.types.JavaBean2;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.JavaUtils;

/**
 * Test standard JAX-RPC types.
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 15-Feb-2005
 */
public abstract class MarshallTest extends JBossWSTest
{
   public void testBigDecimal() throws Exception
   {
      BigDecimal in = new BigDecimal("100");
      BigDecimal out = bigDecimalTest(in);
      assertEquals(in, out);
   }

   public void testBigDecimalArray() throws Exception
   {
      BigDecimal[] in = new BigDecimal[] { new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300") };
      BigDecimal[] out = bigDecimalArrayTest(in);
      assertEquals(in, out);
   }

   public void testBigDecimalMultiArray() throws Exception
   {
      BigDecimal[] ar1 = new BigDecimal[] { new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300") };
      BigDecimal[] ar2 = new BigDecimal[] { new BigDecimal("400"), new BigDecimal("500"), new BigDecimal("600") };
      BigDecimal[][] in = new BigDecimal[][] { ar1, ar2 };
      BigDecimal[][] out = bigDecimalMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testBigInteger() throws Exception
   {
      BigInteger in = new BigInteger("100");
      BigInteger out = bigIntegerTest(in);
      assertEquals(in, out);
   }

   public void testBigIntegerArray() throws Exception
   {
      BigInteger[] in = new BigInteger[] { new BigInteger("100"), new BigInteger("200"), new BigInteger("300") };
      BigInteger[] out = bigIntegerArrayTest(in);
      assertEquals(in, out);
   }

   public void testBigIntegerMultiArray() throws Exception
   {
      BigInteger[] ar1 = new BigInteger[] { new BigInteger("100"), new BigInteger("200"), new BigInteger("300") };
      BigInteger[] ar2 = new BigInteger[] { new BigInteger("400"), new BigInteger("500"), new BigInteger("600") };
      BigInteger[][] in = new BigInteger[][] { ar1, ar2 };
      BigInteger[][] out = bigIntegerMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testBoolean() throws Exception
   {
      boolean in = true;
      boolean out = booleanTest(in);
      assertEquals(in, out);
   }

   public void testBooleanArray() throws Exception
   {
      boolean[] in = new boolean[] { true, false, true };
      boolean[] out = booleanArrayTest(in);
      assertEquals(in, out);
   }

   public void testBooleanMultiArray() throws Exception
   {
      boolean[] ar1 = new boolean[] { true, false, true };
      boolean[] ar2 = new boolean[] { false, true, false };
      boolean[][] in = new boolean[][] { ar1, ar2 };
      boolean[][] out = booleanMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testBooleanWrapper() throws Exception
   {
      Boolean in = new Boolean(true);
      Boolean out = booleanWrapperTest(in);
      assertEquals(in, out);
   }

   public void testBooleanWrapperArray() throws Exception
   {
      Boolean[] in = new Boolean[] { new Boolean(true), new Boolean(false), new Boolean(true) };
      Boolean[] out = booleanWrapperArrayTest(in);
      assertEquals(in, out);
   }

   public void testBooleanWrapperMultiArray() throws Exception
   {
      Boolean[] ar1 = new Boolean[] { new Boolean(true), new Boolean(false), new Boolean(true) };
      Boolean[] ar2 = new Boolean[] { new Boolean(false), new Boolean(true), new Boolean(false) };
      Boolean[][] in = new Boolean[][] { ar1, ar2 };
      Boolean[][] out = booleanWrapperMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testByte() throws Exception
   {
      byte in = (byte)8;
      byte out = byteTest(in);
      assertEquals(in, out);
   }

   public void testByteArray() throws Exception
   {
      byte[] in = "ByteArray".getBytes();
      byte[] out = byteArrayTest(in);
      assertEquals(in, out);
   }

   public void testByteMultiArray() throws Exception
   {
      byte[] ar1 = "ByteArray".getBytes();
      byte[] ar2 = "ArrayByte".getBytes();
      byte[][] in = new byte[][] { ar1, ar2 };
      byte[][] out = byteMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testByteWrapper() throws Exception
   {
      Byte in = new Byte((byte)8);
      Byte out = byteWrapperTest(in);
      assertEquals(in, out);
   }

   public void testByteWrapperArray() throws Exception
   {
      Byte[] in = (Byte[])JavaUtils.getWrapperValueArray("ByteArray".getBytes());
      Byte[] out = byteWrapperArrayTest(in);
      assertEquals(in, out);
   }

   public void testByteWrapperMultiArray() throws Exception
   {
      Byte[] ar1 = (Byte[])JavaUtils.getWrapperValueArray("ByteArray".getBytes());
      Byte[] ar2 = (Byte[])JavaUtils.getWrapperValueArray("ArrayByte".getBytes());
      Byte[][] in = new Byte[][] { ar1, ar2 };
      Byte[][] out = byteWrapperMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testQName() throws Exception
   {
      QName in = new QName("http://some-ns1", "lp1");
      QName out = qnameTest(in);
      assertEquals(in, out);
   }

   public void testQNameArray() throws Exception
   {
      QName[] in = new QName[] { new QName("http://some-ns1", "lp1"), new QName("http://some-ns2", "lp2"), new QName("http://some-ns3", "lp3") };
      QName[] out = qnameArrayTest(in);
      assertEquals(in, out);
   }

   public void testQNameMultiArray() throws Exception
   {
      QName[] ar1 = new QName[] { new QName("http://some-ns1", "lp1"), new QName("http://some-ns2", "lp2"), new QName("http://some-ns3", "lp3") };
      QName[] ar2 = new QName[] { new QName("http://some-ns1", "lp4"), new QName("http://some-ns2", "lp5"), new QName("http://some-ns3", "lp6") };
      QName[][] in = new QName[][] { ar1, ar2 };
      QName[][] out = qnameMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testBase64Binary() throws Exception
   {
      byte[] in = "base64Binary".getBytes();
      Object out = base64BinaryTest(in);
      assertEquals(new String(in), new String((byte[])out));

      out = base64BinaryTest(null);
      assertNull(out);
   }

   public void testHexBinary() throws Exception
   {
      byte[] in = "hexBinary".getBytes();
      byte[] out = hexBinaryTest(in);
      assertEquals(new String(in), new String((byte[])out));

      out = hexBinaryTest(null);
      assertNull(out);
   }

   public void testCalendar() throws Exception
   {
      Calendar in = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      /**
       * Will be a problem where the JVM is running in a TZ which has Daylight
       * Saving Time influence. So offset the TZ to GMT.
       */
      in.setTimeZone(TimeZone.getTimeZone("GMT-0"));
      Calendar out = calendarTest(in);
      assertEquals(in.getTime(), out.getTime());
   }

   public void testCalendarArray() throws Exception
   {
      Calendar cal1 = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal2 = new GregorianCalendar(1969, 6, 17, 15, 24, 56);
      cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal3 = new GregorianCalendar(1970, 7, 18, 16, 25, 57);
      cal3.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar[] in = new Calendar[] { cal1, cal2, cal3 };
      Calendar[] out = calendarArrayTest(in);
      assertEquals(in, out);
   }

   public void testCalendarMultiArray() throws Exception
   {
      Calendar cal1 = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal2 = new GregorianCalendar(1969, 6, 17, 15, 24, 56);
      cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal3 = new GregorianCalendar(1970, 7, 18, 16, 25, 57);
      cal3.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar[] ar1 = new Calendar[] { cal1, cal2, cal3 };
      Calendar[] ar2 = new Calendar[] { cal3, cal2, cal1 };
      Calendar[][] in = new Calendar[][] { ar1, ar2 };
      Calendar[][] out = calendarMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testDouble() throws Exception
   {
      double in = 1.2;
      double out = doubleTest(in);
      assertEquals(in, out, 0.1);
   }

   public void testDoubleArray() throws Exception
   {
      double[] in = new double[] { 1.1, 1.2, 1.3 };
      double[] out = doubleArrayTest(in);
      assertEquals(in, out);
   }

   public void testDoubleMultiArray() throws Exception
   {
      double[] ar1 = new double[] { 1.1, 1.2, 1.3 };
      double[] ar2 = new double[] { 2.1, 2.2, 2.3 };
      double[][] in = new double[][] { ar1, ar2 };
      double[][] out = doubleMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testDoubleWrapper() throws Exception
   {
      Double in = new Double(1.2);
      Double out = doubleWrapperTest(in);
      assertEquals(in, out);
   }

   public void testDoubleWrapperArray() throws Exception
   {
      Double[] in = new Double[] { new Double(1.1), new Double(1.2), new Double(1.3) };
      Double[] out = doubleWrapperArrayTest(in);
      assertEquals(in, out);
   }

   public void testDoubleWrapperMultiArray() throws Exception
   {
      Double[] ar1 = new Double[] { new Double(1.1), new Double(1.2), new Double(1.3) };
      Double[] ar2 = new Double[] { new Double(2.1), new Double(2.2), new Double(2.3) };
      Double[][] in = new Double[][] { ar1, ar2 };
      Double[][] out = doubleWrapperMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testFloat() throws Exception
   {
      float in = 1.2f;
      float out = floatTest(in);
      assertEquals(in, out, 0.1);
   }

   public void testFloatArray() throws Exception
   {
      float[] in = new float[] { 1.2f, 2.3f, 3.4f };
      float[] out = floatArrayTest(in);
      assertEquals(in, out);
   }

   public void testFloatMultiArray() throws Exception
   {
      float[] ar1 = new float[] { 1.2f, 2.3f, 3.4f };
      float[] ar2 = new float[] { 2.2f, 3.3f, 4.4f };
      float[][] in = new float[][] { ar1, ar2 };
      float[][] out = floatMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testFloatWrapper() throws Exception
   {
      Float in = new Float(1.2f);
      Float out = floatWrapperTest(in);
      assertEquals(in, out);
   }

   public void testFloatWrapperArray() throws Exception
   {
      Float[] in = new Float[] { new Float(1.2f), new Float(2.3f), new Float(3.4f) };
      Float[] out = floatWrapperArrayTest(in);
      assertEquals(in, out);
   }

   public void testFloatWrapperMultiArray() throws Exception
   {
      Float[] ar1 = new Float[] { new Float(1.2f), new Float(2.3f), new Float(3.4f) };
      Float[] ar2 = new Float[] { new Float(3.2f), new Float(5.3f), new Float(6.4f) };
      Float[][] in = new Float[][] { ar1, ar2 };
      Float[][] out = floatWrapperMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testInt() throws Exception
   {
      int in = 1;
      int out = intTest(in);
      assertEquals(in, out);
   }

   public void testIntArray() throws Exception
   {
      int[] in = new int[] { 1, 2, 3 };
      int[] out = intArrayTest(in);
      assertEquals(in, out);
   }

   public void testIntMultiArray() throws Exception
   {
      int[] ar1 = new int[] { 1, 2, 3 };
      int[] ar2 = new int[] { 4, 5, 6 };
      int[][] in = new int[][] { ar1, ar2 };
      int[][] out = intMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testIntWrapper() throws Exception
   {
      Integer in = new Integer(1);
      Integer out = intWrapperTest(in);
      assertEquals(in, out);
   }

   public void testIntWrapperArray() throws Exception
   {
      Integer[] in = new Integer[] { new Integer(1), new Integer(2), new Integer(3) };
      Integer[] out = intWrapperArrayTest(in);
      assertEquals(in, out);
   }

   public void testIntWrapperMultiArray() throws Exception
   {
      Integer[] ar1 = new Integer[] { new Integer(1), new Integer(2), new Integer(3) };
      Integer[] ar2 = new Integer[] { new Integer(4), new Integer(5), new Integer(6) };
      Integer[][] in = new Integer[][] { ar1, ar2 };
      Integer[][] out = intWrapperMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testJavaBean() throws Exception
   {
      GregorianCalendar cal1 = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
      GregorianCalendar cal2 = new GregorianCalendar(1970, 5, 16, 14, 23, 55);
      cal2.setTimeZone(TimeZone.getTimeZone("GMT"));

      JavaBean2 inner = new JavaBean2();
      inner.setMyBigDecimal(new BigDecimal("100"));
      inner.setMyBigInteger(new BigInteger("101"));
      inner.setMyBoolean(true);
      inner.setMyByte((byte)1);
      inner.setMyCalendar(cal1);
      inner.setMyDouble(1.1);
      inner.setMyFloat(1.2f);
      inner.setMyInt(1);
      inner.setMyLong(1);
      inner.setMyShort((short)1);
      inner.setMyString("one");

      JavaBean in = new JavaBean();
      in.setMyBigDecimal(new BigDecimal("200"));
      in.setMyBigInteger(new BigInteger("201"));
      in.setMyBoolean(true);
      in.setMyByte((byte)1);
      in.setMyCalendar(cal2);
      in.setMyDouble(2.1);
      in.setMyFloat(2.2f);
      in.setMyInt(2);
      in.setMyJavaBean(inner);
      in.setMyLong(2);
      in.setMyShort((short)2);
      in.setMyString("two");

      JavaBean out = javaBeanTest(in);
      assertEquals(in, out);
   }

   public void testJavaBeanArray() throws Exception
   {
      Calendar cal1 = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal2 = new GregorianCalendar(1969, 6, 17, 15, 24, 56);
      cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal3 = new GregorianCalendar(1970, 7, 18, 16, 25, 57);
      cal3.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal4 = new GregorianCalendar(1971, 8, 19, 17, 26, 58);
      cal4.setTimeZone(TimeZone.getTimeZone("GMT"));

      JavaBean2 inner = new JavaBean2();
      inner.setMyBigDecimal(new BigDecimal("100"));
      inner.setMyBigInteger(new BigInteger("101"));
      inner.setMyBoolean(true);
      inner.setMyByte((byte)1);
      inner.setMyCalendar(cal1);
      inner.setMyDouble(1.1);
      inner.setMyFloat(1.2f);
      inner.setMyInt(1);
      inner.setMyLong(1);
      inner.setMyShort((short)1);
      inner.setMyString("one");

      JavaBean b1 = new JavaBean();
      b1.setMyBigDecimal(new BigDecimal("200"));
      b1.setMyBigInteger(new BigInteger("201"));
      b1.setMyBoolean(true);
      b1.setMyByte((byte)1);
      b1.setMyCalendar(cal2);
      b1.setMyDouble(2.1);
      b1.setMyFloat(2.2f);
      b1.setMyInt(2);
      b1.setMyJavaBean(inner);
      b1.setMyLong(2);
      b1.setMyShort((short)2);
      b1.setMyString("two");

      JavaBean b2 = new JavaBean();
      b2.setMyBigDecimal(new BigDecimal("300"));
      b2.setMyBigInteger(new BigInteger("301"));
      b2.setMyBoolean(true);
      b2.setMyByte((byte)3);
      b2.setMyCalendar(cal3);
      b2.setMyDouble(3.1);
      b2.setMyFloat(3.2f);
      b2.setMyInt(3);
      b2.setMyJavaBean(inner);
      b2.setMyLong(3);
      b2.setMyShort((short)3);
      b2.setMyString("three");

      JavaBean b3 = new JavaBean();
      b3.setMyBigDecimal(new BigDecimal("400"));
      b3.setMyBigInteger(new BigInteger("401"));
      b3.setMyBoolean(true);
      b3.setMyByte((byte)4);
      b3.setMyCalendar(cal4);
      b3.setMyDouble(4.1);
      b3.setMyFloat(4.2f);
      b3.setMyInt(4);
      b3.setMyJavaBean(inner);
      b3.setMyLong(4);
      b3.setMyShort((short)4);
      b3.setMyString("four");

      JavaBean[] in = new JavaBean[] { b1, b2, b3 };
      JavaBean[] out = javaBeanArrayTest(in);
      assertEquals(in, out);
   }

   public void testLong() throws Exception
   {
      long in = 1L;
      long out = longTest(in);
      assertEquals(in, out);
   }

   public void testLongArray() throws Exception
   {
      long[] in = new long[] { 1, 2, 3 };
      long[] out = longArrayTest(in);
      assertEquals(in, out);
   }

   public void testLongMultiArray() throws Exception
   {
      long[] ar1 = new long[] { 1, 2, 3 };
      long[] ar2 = new long[] { 4, 5, 6 };
      long[][] in = new long[][] { ar1, ar2 };
      long[][] out = longMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testLongWrapper() throws Exception
   {
      Long in = new Long(1L);
      Long out = longWrapperTest(in);
      assertEquals(in, out);
   }

   public void testLongWrapperArray() throws Exception
   {
      Long[] in = new Long[] { new Long(1), new Long(2), new Long(3) };
      Long[] out = longWrapperArrayTest(in);
      assertEquals(in, out);
   }

   public void testLongWrapperMultiArray() throws Exception
   {
      Long[] ar1 = new Long[] { new Long(1), new Long(2), new Long(3) };
      Long[] ar2 = new Long[] { new Long(4), new Long(5), new Long(6) };
      Long[][] in = new Long[][] { ar1, ar2 };
      Long[][] out = longWrapperMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testShort() throws Exception
   {
      short in = (short)1;
      short out = shortTest(in);
      assertEquals(in, out);
   }

   public void testShortArray() throws Exception
   {
      short[] in = new short[] { (short)1, (short)2, (short)3 };
      short[] out = shortArrayTest(in);
      assertEquals(in, out);
   }

   public void testShortMultiArray() throws Exception
   {
      short[] ar1 = new short[] { (short)1, (short)2, (short)3 };
      short[] ar2 = new short[] { (short)4, (short)5, (short)6 };
      short[][] in = new short[][] { ar1, ar2 };
      short[][] out = shortMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testShortWrapper() throws Exception
   {
      Short in = new Short((short)1);
      Short out = shortWrapperTest(in);
      assertEquals(in, out);
   }

   public void testShortWrapperArray() throws Exception
   {
      Short[] in = new Short[] { new Short((short)1), new Short((short)2), new Short((short)3) };
      Short[] out = shortWrapperArrayTest(in);
      assertEquals(in, out);
   }

   public void testShortWrapperMultiArray() throws Exception
   {
      Short[] ar1 = new Short[] { new Short((short)1), new Short((short)2), new Short((short)3) };
      Short[] ar2 = new Short[] { new Short((short)2), new Short((short)3), new Short((short)4) };
      Short[][] in = new Short[][] { ar1, ar2 };
      Short[][] out = shortWrapperMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testString() throws Exception
   {
      String in = "one";
      String out = stringTest(in);
      assertEquals(in, out);
      
      in = "   ";
      out = stringTest(in);
      assertEquals(in, out);
   }

   public void testStringSpecialChars() throws Exception
   {
      String inStr = "&Test & this &";
      String outStr = stringTest(inStr);
      assertEquals(inStr, outStr);

      inStr = "<Test < this <";
      outStr = stringTest(inStr);
      assertEquals(inStr, outStr);

      inStr = ">Test > this >";
      outStr = stringTest(inStr);
      assertEquals(inStr, outStr);

      inStr = "\"Test \" this \"";
      outStr = stringTest(inStr);
      assertEquals(inStr, outStr);
   }

   public void testStringArray() throws Exception
   {
      String[] in = new String[] { "one", "two", "three" };
      String[] out = stringArrayTest(in);
      assertEquals(in, out);
   }

   public void testStringMultiArray() throws Exception
   {
      String[] ar1 = new String[] { "one", "two", "three" };
      String[] ar2 = new String[] { "four", "five", "six" };
      String[][] in = new String[][] { ar1, ar2 };
      String[][] out = stringMultiArrayTest(in);
      assertEquals(in, out);
   }

   public void testVoid() throws Exception
   {
      voidTest();
   }

   public abstract BigDecimal bigDecimalTest(BigDecimal param) throws Exception;

   public abstract BigDecimal[] bigDecimalArrayTest(BigDecimal[] params) throws Exception;

   public abstract BigDecimal[][] bigDecimalMultiArrayTest(BigDecimal[][] params) throws Exception;

   public abstract BigInteger bigIntegerTest(BigInteger param) throws Exception;

   public abstract BigInteger[] bigIntegerArrayTest(BigInteger[] params) throws Exception;

   public abstract BigInteger[][] bigIntegerMultiArrayTest(BigInteger[][] params) throws Exception;

   public abstract boolean booleanTest(boolean primParam) throws Exception;

   public abstract boolean[] booleanArrayTest(boolean[] primParams) throws Exception;

   public abstract boolean[][] booleanMultiArrayTest(boolean[][] primParams) throws Exception;

   public abstract Boolean booleanWrapperTest(Boolean param) throws Exception;

   public abstract Boolean[] booleanWrapperArrayTest(Boolean[] params) throws Exception;

   public abstract Boolean[][] booleanWrapperMultiArrayTest(Boolean[][] params) throws Exception;

   public abstract byte byteTest(byte primParam) throws Exception;

   public abstract byte[] byteArrayTest(byte[] primParams) throws Exception;

   public abstract byte[][] byteMultiArrayTest(byte[][] primParams) throws Exception;

   public abstract Byte byteWrapperTest(Byte param) throws Exception;

   public abstract Byte[] byteWrapperArrayTest(Byte[] params) throws Exception;

   public abstract Byte[][] byteWrapperMultiArrayTest(Byte[][] params) throws Exception;

   public abstract QName qnameTest(QName param) throws Exception;

   public abstract QName[] qnameArrayTest(QName[] params) throws Exception;

   public abstract QName[][] qnameMultiArrayTest(QName[][] params) throws Exception;

   public abstract byte[] base64BinaryTest(byte[] params) throws Exception;

   public abstract byte[] hexBinaryTest(byte[] params) throws Exception;

   public abstract Calendar calendarTest(Calendar param) throws Exception;

   public abstract Calendar[] calendarArrayTest(Calendar[] params) throws Exception;

   public abstract Calendar[][] calendarMultiArrayTest(Calendar[][] params) throws Exception;

   public abstract double doubleTest(double primParam) throws Exception;

   public abstract double[] doubleArrayTest(double[] primParams) throws Exception;

   public abstract double[][] doubleMultiArrayTest(double[][] primParams) throws Exception;

   public abstract Double doubleWrapperTest(Double param) throws Exception;

   public abstract Double[] doubleWrapperArrayTest(Double[] params) throws Exception;

   public abstract Double[][] doubleWrapperMultiArrayTest(Double[][] params) throws Exception;

   public abstract float floatTest(float param) throws Exception;

   public abstract float[] floatArrayTest(float[] params) throws Exception;

   public abstract float[][] floatMultiArrayTest(float[][] params) throws Exception;

   public abstract Float floatWrapperTest(Float param) throws Exception;

   public abstract Float[] floatWrapperArrayTest(Float[] params) throws Exception;

   public abstract Float[][] floatWrapperMultiArrayTest(Float[][] params) throws Exception;

   public abstract int intTest(int param) throws Exception;

   public abstract int[] intArrayTest(int[] params) throws Exception;

   public abstract int[][] intMultiArrayTest(int[][] params) throws Exception;

   public abstract Integer intWrapperTest(Integer param) throws Exception;

   public abstract Integer[] intWrapperArrayTest(Integer[] params) throws Exception;

   public abstract Integer[][] intWrapperMultiArrayTest(Integer[][] params) throws Exception;

   public abstract JavaBean javaBeanTest(JavaBean param) throws Exception;

   public abstract JavaBean[] javaBeanArrayTest(JavaBean[] params) throws Exception;

   public abstract long longTest(long param) throws Exception;

   public abstract long[] longArrayTest(long[] params) throws Exception;

   public abstract long[][] longMultiArrayTest(long[][] params) throws Exception;

   public abstract Long longWrapperTest(Long param) throws Exception;

   public abstract Long[] longWrapperArrayTest(Long[] params) throws Exception;

   public abstract Long[][] longWrapperMultiArrayTest(Long[][] params) throws Exception;

   public abstract short shortTest(short param) throws Exception;

   public abstract short[] shortArrayTest(short[] params) throws Exception;

   public abstract short[][] shortMultiArrayTest(short[][] params) throws Exception;

   public abstract Short shortWrapperTest(Short param) throws Exception;

   public abstract Short[] shortWrapperArrayTest(Short[] params) throws Exception;

   public abstract Short[][] shortWrapperMultiArrayTest(Short[][] params) throws Exception;

   public abstract String stringTest(String param) throws Exception;

   public abstract String[] stringArrayTest(String[] params) throws Exception;

   public abstract String[][] stringMultiArrayTest(String[][] params) throws Exception;

   public abstract void voidTest() throws Exception;
}
