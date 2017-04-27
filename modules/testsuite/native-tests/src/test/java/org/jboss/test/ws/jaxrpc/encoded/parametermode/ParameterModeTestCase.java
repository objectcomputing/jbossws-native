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
package org.jboss.test.ws.jaxrpc.encoded.parametermode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
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
import javax.xml.rpc.holders.QNameHolder;
import javax.xml.rpc.holders.ShortHolder;
import javax.xml.rpc.holders.ShortWrapperHolder;
import javax.xml.rpc.holders.StringHolder;

import junit.framework.Test;

import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfBookHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfQNameHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfbooleanHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfbyteHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfdateTimeHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfdecimalHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfdoubleHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOffloatHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfintHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfintegerHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOflongHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfshortHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.ArrayOfstringHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.BookHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.EnumByteHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.EnumDecimalHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.EnumDoubleHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.EnumFloatHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.EnumIntHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.EnumIntegerHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.EnumLongHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.EnumShortHolder;
import org.jboss.test.ws.jaxrpc.encoded.parametermode.holders.EnumStringHolder;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test soap encoded parameters
 *
 * @author Thomas.Diesler@jboss.org
 * @since 31-Oct-2005
 */
public class ParameterModeTestCase extends JBossWSTest
{
   private static ParameterModeTest port;

   private static String _varString = "varString";
   private static BigInteger _varInteger = new BigInteger("100");
   private static int _varInt = 1;
   private static long _varLong = 2;
   private static short _varShort = 3;
   private static BigDecimal _varDecimal = new BigDecimal("200");
   private static float _varFloat = 1.1f;
   private static double _varDouble = 1.2;
   private static boolean _varBoolean = true;
   private static byte _varByte = 0x04;
   private static QName _varQName = new QName("http://nsuri", "name");
   private static Calendar _varDateTime = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
   private static String _varSoapString = "soapString";
   private static Boolean _varSoapBoolean = new Boolean(true);
   private static Float _varSoapFloat = new Float(2.1f);
   private static Double _varSoapDouble = new Double(2.2);
   private static BigDecimal _varSoapDecimal = new BigDecimal("300");
   private static Integer _varSoapInt = new Integer(3);
   private static Short _varSoapShort = new Short(_varShort);
   private static Byte _varSoapByte = new Byte(_varByte);
   private static byte[] _varBase64Binary = "varBase64Binary".getBytes();
   private static byte[] _varHexBinary = "varHexBinary".getBytes();
   private static byte[] _varSoapBase64 = "varSoapBase64".getBytes();

   private static EnumString _varEnumString = EnumString.String1;
   private static EnumInteger _varEnumInteger = EnumInteger.value1;
   private static EnumInt _varEnumInt = EnumInt.value1;
   private static EnumLong _varEnumLong = EnumLong.value1;
   private static EnumShort _varEnumShort = EnumShort.value1;
   private static EnumDecimal _varEnumDecimal = EnumDecimal.value1;
   private static EnumFloat _varEnumFloat = EnumFloat.value1;
   private static EnumDouble _varEnumDouble = EnumDouble.value1;
   private static EnumByte _varEnumByte = EnumByte.value1;

   static
   {
      //_varDateTime.setTimeZone(TimeZone.getTimeZone("GMT-0"));
   }

   public static Test suite()
   {
      return new JBossWSTestSetup(ParameterModeTestCase.class, "jaxrpc-encoded-parametermode.war, jaxrpc-encoded-parametermode-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (ParameterModeTest)service.getPort(ParameterModeTest.class);
      }
   }

   public void testEchoIn() throws Exception
   {
      port.echoIn("Kermit");
   }

   public void testEchoOut() throws Exception
   {
      StringHolder varString = new StringHolder();
      port.echoOut(varString);
      assertEquals("Kermit", varString.value);
   }

   public void testEchoInOut() throws Exception
   {
      StringHolder varString = new StringHolder("Ferrari");
      port.echoInOut(varString);
      assertEquals("Ferrari", varString.value);
   }

   public void testEchoMix() throws Exception
   {
      String varInString = "in";
      StringHolder varInOutString = new StringHolder("inout");
      StringHolder varOutString = new StringHolder();
      port.echoMix(varInString, varInOutString, varOutString);
      assertEquals("inout", varInOutString.value);
      assertEquals("in", varOutString.value);
   }

   public void testEchoInSimpleTypes() throws Exception
   {
      port.echoInSimpleTypes(_varString, _varInteger, _varInt, _varLong, _varShort, _varDecimal, _varFloat, _varDouble, _varBoolean, _varByte, _varQName, _varDateTime,
            _varSoapString, _varSoapBoolean, _varSoapFloat, _varSoapDouble, _varSoapDecimal, _varSoapInt, _varSoapShort, _varSoapByte, _varBase64Binary, _varHexBinary,
            _varSoapBase64);
   }

   public void testEchoOutSimpleTypes() throws Exception
   {
      StringHolder varString = new StringHolder();
      BigIntegerHolder varInteger = new BigIntegerHolder();
      IntHolder varInt = new IntHolder();
      LongHolder varLong = new LongHolder();
      ShortHolder varShort = new ShortHolder();
      BigDecimalHolder varDecimal = new BigDecimalHolder();
      FloatHolder varFloat = new FloatHolder();
      DoubleHolder varDouble = new DoubleHolder();
      BooleanHolder varBoolean = new BooleanHolder();
      ByteHolder varByte = new ByteHolder();
      QNameHolder varQName = new QNameHolder();
      CalendarHolder varDateTime = new CalendarHolder();
      StringHolder varSoapString = new StringHolder();
      BooleanWrapperHolder varSoapBoolean = new BooleanWrapperHolder();
      FloatWrapperHolder varSoapFloat = new FloatWrapperHolder();
      DoubleWrapperHolder varSoapDouble = new DoubleWrapperHolder();
      BigDecimalHolder varSoapDecimal = new BigDecimalHolder();
      IntegerWrapperHolder varSoapInt = new IntegerWrapperHolder();
      ShortWrapperHolder varSoapShort = new ShortWrapperHolder();
      ByteWrapperHolder varSoapByte = new ByteWrapperHolder();
      ByteArrayHolder varBase64Binary = new ByteArrayHolder();
      ByteArrayHolder varHexBinary = new ByteArrayHolder();
      ByteArrayHolder varSoapBase64 = new ByteArrayHolder();

      port.echoOutSimpleTypes(varString, varInteger, varInt, varLong, varShort, varDecimal, varFloat, varDouble, varBoolean, varByte, varQName, varDateTime,
            varSoapString, varSoapBoolean, varSoapFloat, varSoapDouble, varSoapDecimal, varSoapInt, varSoapShort, varSoapByte, varBase64Binary, varHexBinary,
            varSoapBase64);

      assertEquals(_varString, varString.value);
      assertEquals(_varInteger, varInteger.value);
      assertEquals(_varInt, varInt.value);
      assertEquals(_varLong, varLong.value);
      assertEquals(_varShort, varShort.value);
      assertEquals(_varDecimal, varDecimal.value);
      assertEquals(_varFloat, varFloat.value, 2);
      assertEquals(_varDouble, varDouble.value, 2);
      assertEquals(_varBoolean, varBoolean.value);
      assertEquals(_varByte, varByte.value);
      assertEquals(_varQName, varQName.value);
      assertEquals(_varDateTime.getTime(), varDateTime.value.getTime());
      assertEquals(_varSoapString, varSoapString.value);
      assertEquals(_varSoapBoolean, varSoapBoolean.value);
      assertEquals(_varSoapFloat, varSoapFloat.value);
      assertEquals(_varSoapDouble, varSoapDouble.value);
      assertEquals(_varSoapDecimal, varSoapDecimal.value);
      assertEquals(_varSoapInt, varSoapInt.value);
      assertEquals(_varSoapShort, varSoapShort.value);
      assertEquals(_varSoapByte, varSoapByte.value);
      assertEquals(_varBase64Binary, varBase64Binary.value);
      assertEquals(_varHexBinary, varHexBinary.value);
      assertEquals(_varSoapBase64, varSoapBase64.value);
   }

   public void testEchoInOutSimpleTypes() throws Exception
   {
      StringHolder varString = new StringHolder(_varString);
      BigIntegerHolder varInteger = new BigIntegerHolder(_varInteger);
      IntHolder varInt = new IntHolder(_varInt);
      LongHolder varLong = new LongHolder(_varLong);
      ShortHolder varShort = new ShortHolder(_varShort);
      BigDecimalHolder varDecimal = new BigDecimalHolder(_varDecimal);
      FloatHolder varFloat = new FloatHolder(_varFloat);
      DoubleHolder varDouble = new DoubleHolder(_varDouble);
      BooleanHolder varBoolean = new BooleanHolder(_varBoolean);
      ByteHolder varByte = new ByteHolder(_varByte);
      QNameHolder varQName = new QNameHolder(_varQName);
      CalendarHolder varDateTime = new CalendarHolder(_varDateTime);
      StringHolder varSoapString = new StringHolder(_varSoapString);
      BooleanWrapperHolder varSoapBoolean = new BooleanWrapperHolder(_varSoapBoolean);
      FloatWrapperHolder varSoapFloat = new FloatWrapperHolder(_varSoapFloat);
      DoubleWrapperHolder varSoapDouble = new DoubleWrapperHolder(_varSoapDouble);
      BigDecimalHolder varSoapDecimal = new BigDecimalHolder(_varSoapDecimal);
      IntegerWrapperHolder varSoapInt = new IntegerWrapperHolder(_varSoapInt);
      ShortWrapperHolder varSoapShort = new ShortWrapperHolder(_varSoapShort);
      ByteWrapperHolder varSoapByte = new ByteWrapperHolder(_varSoapByte);
      ByteArrayHolder varBase64Binary = new ByteArrayHolder(_varBase64Binary);
      ByteArrayHolder varHexBinary = new ByteArrayHolder(_varHexBinary);
      ByteArrayHolder varSoapBase64 = new ByteArrayHolder(_varSoapBase64);

      port.echoInOutSimpleTypes(varString, varInteger, varInt, varLong, varShort, varDecimal, varFloat, varDouble, varBoolean, varByte, varQName, varDateTime,
            varSoapString, varSoapBoolean, varSoapFloat, varSoapDouble, varSoapDecimal, varSoapInt, varSoapShort, varSoapByte, varBase64Binary, varHexBinary,
            varSoapBase64);

      assertEquals(_varString, varString.value);
      assertEquals(_varInteger, varInteger.value);
      assertEquals(_varInt, varInt.value);
      assertEquals(_varLong, varLong.value);
      assertEquals(_varShort, varShort.value);
      assertEquals(_varDecimal, varDecimal.value);
      assertEquals(_varFloat, varFloat.value, 2);
      assertEquals(_varDouble, varDouble.value, 2);
      assertEquals(_varBoolean, varBoolean.value);
      assertEquals(_varByte, varByte.value);
      assertEquals(_varQName, varQName.value);
      assertEquals(_varDateTime.getTime(), varDateTime.value.getTime());
      assertEquals(_varSoapString, varSoapString.value);
      assertEquals(_varSoapBoolean, varSoapBoolean.value);
      assertEquals(_varSoapFloat, varSoapFloat.value);
      assertEquals(_varSoapDouble, varSoapDouble.value);
      assertEquals(_varSoapDecimal, varSoapDecimal.value);
      assertEquals(_varSoapInt, varSoapInt.value);
      assertEquals(_varSoapShort, varSoapShort.value);
      assertEquals(_varSoapByte, varSoapByte.value);
      assertEquals(_varBase64Binary, varBase64Binary.value);
      assertEquals(_varHexBinary, varHexBinary.value);
      assertEquals(_varSoapBase64, varSoapBase64.value);
   }

   public void testEchoInEnum() throws Exception
   {
      port.echoInEnum(_varEnumString, _varEnumInteger, _varEnumInt, _varEnumLong, _varEnumShort, _varEnumDecimal, _varEnumFloat, _varEnumDouble, _varEnumByte);
   }

   public void testEchoOutEnum() throws Exception
   {
      EnumStringHolder varEnumString = new EnumStringHolder();
      EnumIntegerHolder varEnumInteger = new EnumIntegerHolder();
      EnumIntHolder varEnumInt = new EnumIntHolder();
      EnumLongHolder varEnumLong = new EnumLongHolder();
      EnumShortHolder varEnumShort = new EnumShortHolder();
      EnumDecimalHolder varEnumDecimal = new EnumDecimalHolder();
      EnumFloatHolder varEnumFloat = new EnumFloatHolder();
      EnumDoubleHolder varEnumDouble = new EnumDoubleHolder();
      EnumByteHolder varEnumByte = new EnumByteHolder();

      port.echoOutEnum(varEnumString, varEnumInteger, varEnumInt, varEnumLong, varEnumShort, varEnumDecimal, varEnumFloat, varEnumDouble, varEnumByte);

      assertEquals(_varEnumString, varEnumString.value);
      assertEquals(_varEnumInteger, varEnumInteger.value);
      assertEquals(_varEnumInt, varEnumInt.value);
      assertEquals(_varEnumLong, varEnumLong.value);
      assertEquals(_varEnumShort, varEnumShort.value);
      assertEquals(_varEnumDecimal, varEnumDecimal.value);
      assertEquals(_varEnumFloat, varEnumFloat.value);
      assertEquals(_varEnumDouble, varEnumDouble.value);
      assertEquals(_varEnumByte, varEnumByte.value);
   }

   public void testEchoInOutEnum() throws Exception
   {
      EnumStringHolder varEnumString = new EnumStringHolder(_varEnumString);
      EnumIntegerHolder varEnumInteger = new EnumIntegerHolder(_varEnumInteger);
      EnumIntHolder varEnumInt = new EnumIntHolder(_varEnumInt);
      EnumLongHolder varEnumLong = new EnumLongHolder(_varEnumLong);
      EnumShortHolder varEnumShort = new EnumShortHolder(_varEnumShort);
      EnumDecimalHolder varEnumDecimal = new EnumDecimalHolder(_varEnumDecimal);
      EnumFloatHolder varEnumFloat = new EnumFloatHolder(_varEnumFloat);
      EnumDoubleHolder varEnumDouble = new EnumDoubleHolder(_varEnumDouble);
      EnumByteHolder varEnumByte = new EnumByteHolder(_varEnumByte);

      port.echoOutEnum(varEnumString, varEnumInteger, varEnumInt, varEnumLong, varEnumShort, varEnumDecimal, varEnumFloat, varEnumDouble, varEnumByte);

      assertEquals(_varEnumString, varEnumString.value);
      assertEquals(_varEnumInteger, varEnumInteger.value);
      assertEquals(_varEnumInt, varEnumInt.value);
      assertEquals(_varEnumLong, varEnumLong.value);
      assertEquals(_varEnumShort, varEnumShort.value);
      assertEquals(_varEnumDecimal, varEnumDecimal.value);
      assertEquals(_varEnumFloat, varEnumFloat.value);
      assertEquals(_varEnumDouble, varEnumDouble.value);
      assertEquals(_varEnumByte, varEnumByte.value);
   }

   public void testEchoInStruct() throws Exception
   {
      AllStruct allStruct = new AllStruct(_varString, _varInteger, _varInt, _varLong, _varShort, _varDecimal, _varFloat, _varDouble, _varBoolean, _varByte, _varQName,
            _varDateTime, _varSoapString, _varSoapBoolean, _varSoapFloat, _varSoapDouble, _varSoapDecimal, _varSoapInt, _varSoapShort, _varSoapByte, _varBase64Binary,
            _varHexBinary, _varSoapBase64);
      
      port.echoInStruct(allStruct);
   }

   public void testEchoInOutSimpleTypesArray() throws Exception
   {
      ArrayOfstringHolder varString = new ArrayOfstringHolder(new String[] { _varString, _varString });
      ArrayOfintegerHolder varInteger = new ArrayOfintegerHolder(new BigInteger[] { _varInteger, _varInteger });
      ArrayOfintHolder varInt = new ArrayOfintHolder(new int[] { _varInt, _varInt });
      ArrayOflongHolder varLong = new ArrayOflongHolder(new long[] { _varLong, _varLong });
      ArrayOfshortHolder varShort = new ArrayOfshortHolder(new short[] { _varShort, _varShort });
      ArrayOfdecimalHolder varDecimal = new ArrayOfdecimalHolder(new BigDecimal[] { _varDecimal, _varDecimal });
      ArrayOffloatHolder varFloat = new ArrayOffloatHolder(new float[] { _varFloat, _varFloat });
      ArrayOfdoubleHolder varDouble = new ArrayOfdoubleHolder(new double[] { _varDouble, _varDouble });
      ArrayOfbooleanHolder varBoolean = new ArrayOfbooleanHolder(new boolean[] { _varBoolean, _varBoolean });
      ArrayOfbyteHolder varByte = new ArrayOfbyteHolder(new byte[] { _varByte, _varByte });
      ArrayOfQNameHolder varQName = new ArrayOfQNameHolder(new QName[] { _varQName, _varQName });
      ArrayOfdateTimeHolder varDateTime = new ArrayOfdateTimeHolder(new Calendar[] { _varDateTime, _varDateTime });

      port.echoInOutSimpleTypesArray(varString, varInteger, varInt, varLong, varShort, varDecimal, varFloat, varDouble, varBoolean, varByte, varQName, varDateTime);

      assertEquals(new String[] { _varString, _varString }, varString.value);
      assertEquals(new BigInteger[] { _varInteger, _varInteger }, varInteger.value);
      assertEquals(new int[] { _varInt, _varInt }, varInt.value);
      assertEquals(new long[] { _varLong, _varLong }, varLong.value);
      assertEquals(new short[] { _varShort, _varShort }, varShort.value);
      assertEquals(new BigDecimal[] { _varDecimal, _varDecimal }, varDecimal.value);
      assertEquals(new float[] { _varFloat, _varFloat }, varFloat.value);
      assertEquals(new double[] { _varDouble, _varDouble }, varDouble.value);
      assertEquals(new boolean[] { _varBoolean, _varBoolean }, varBoolean.value);
      assertEquals(new byte[] { _varByte, _varByte }, varByte.value);
      assertEquals(new QName[] { _varQName, _varQName }, varQName.value);
      // Although the XML fragments are equal, Calendar.equals fails ???
      //assertEquals(new Calendar[] { _varDateTime, _varDateTime }, varDateTime.value);
      assertEquals(new Date[] { _varDateTime.getTime(), _varDateTime.getTime() }, new Date[] { varDateTime.value[0].getTime(), varDateTime.value[1].getTime() });
   }

   public void testEchoInOutBook() throws Exception
   {
      Book book = new Book("author", "title", 12345);
      BookHolder holder = new BookHolder(book);
      port.echoInOutBook(holder);
      assertEquals(book, holder.value);
   }

   public void testEchoInOutBookArray() throws Exception
   {
      Book book1 = new Book("author1", "title1", 12345);
      Book book2 = new Book("author2", "title2", 67890);
      ArrayOfBookHolder holder = new ArrayOfBookHolder(new Book[] { book1, book2 });
      port.echoInOutBookArray(holder);
      assertEquals(new Book[] { book1, book2 }, holder.value);
   }
}
