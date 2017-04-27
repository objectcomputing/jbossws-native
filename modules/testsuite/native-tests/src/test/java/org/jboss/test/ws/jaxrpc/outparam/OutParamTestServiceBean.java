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

import javax.xml.namespace.QName;
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

import org.jboss.logging.Logger;

/**
 * A service endpoint for the HolderTestCase
 *
 * @author Thomas.Diesler@jboss.org
 * @since 22-Dec-2004
 */
public class OutParamTestServiceBean
{
   // Provide logging
   private static Logger log = Logger.getLogger(OutParamTestServiceBean.class);

   public void echoBigDecimal(BigDecimal in, BigDecimalHolder val)
   {
      log.info("echoBigDecimal: " + in);
      val.value = in;
   }

   public void echoBigInteger(BigInteger in, BigIntegerHolder val)
   {
      log.info("echoBigInteger: " + in);
      val.value = in;
   }

   public void echoBoolean(boolean in, BooleanHolder val)
   {
      log.info("echoBoolean: " + in);
      val.value = in;
   }

   public void echoBooleanWrapper(Boolean in, BooleanWrapperHolder val)
   {
      log.info("echoBooleanWrapper: " + in);
      val.value = in;
   }

   public void echoByteArray(byte[] in, ByteArrayHolder val)
   {
      log.info("echoByteArray: " + new String(in));
      val.value = in;
   }

   public void echoByte(byte in, ByteHolder val)
   {
      log.info("echoByte: " + in);
      val.value = in;
   }

   public void echoByteWrapper(Byte in, ByteWrapperHolder val)
   {
      log.info("echoByteWrapper: " + in);
      val.value = in;
   }

   public void echoCalendar(Calendar in, CalendarHolder val)
   {
      log.info("echoCalendar: " + in.getTime());
      val.value = in;
   }

   public void echoDouble(double in, DoubleHolder val)
   {
      log.info("echoDouble: " + in);
      val.value = in;
   }

   public void echoDoubleWrapper(Double in, DoubleWrapperHolder val)
   {
      log.info("echoDoubleWrapper: " + in);
      val.value = in;
   }

   public void echoFloat(float in, FloatHolder val)
   {
      log.info("echoFloat: " + in);
      val.value = in;
   }

   public void echoFloatWrapper(Float in, FloatWrapperHolder val)
   {
      log.info("echoFloatWrapper: " + in);
      val.value = in;
   }

   public void echoIntegerWrapper(Integer in, IntegerWrapperHolder val)
   {
      log.info("echoIntegerWrapper: " + in);
      val.value = in;
   }

   public void echoInt(int in, IntHolder val)
   {
      log.info("echoInt: " + in);
      val.value = in;
   }

   public void echoLong(long in, LongHolder val)
   {
      log.info("echoLong: " + in);
      val.value = in;
   }

   public void echoLongWrapper(Long in, LongWrapperHolder val)
   {
      log.info("echoLongWrapper: " + in);
      val.value = in;
   }

   /*
   public void echoObject(Object in, ObjectHolder val)
   {
      log.info("echoObject: " + in);
   }
   */

   public void echoQName(QName in, QNameHolder val)
   {
      log.info("echoQName: " + in);
      val.value = in;
   }

   public void echoShort(short in, ShortHolder val)
   {
      log.info("echoShort: " + in);
      val.value = in;
   }

   public void echoShortWrapper(Short in, ShortWrapperHolder val)
   {
      log.info("echoShortWrapper: " + in);
      val.value = in;
   }

   public void echoString(String in, StringHolder val)
   {
      log.info("echoString: " + in);
      val.value = in;
   }
}
