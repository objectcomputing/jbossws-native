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
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.test.ws.jaxrpc.marshall.types.JavaBean;

public class StandardTypesBean implements StandardTypes
{
   // provide logging
   private static final Logger log = Logger.getLogger(StandardTypesBean.class);

   public BigDecimal bigDecimalTest(BigDecimal param)
   {
      log.info("bigDecimalTest: " + param);
      return param;
   }

   public BigDecimal[] bigDecimalArrayTest(BigDecimal[] param)
   {
      log.info("bigDecimalArrayTest: " + Arrays.asList(param));
      return param;
   }

   public BigDecimal[][] bigDecimalMultiArrayTest(BigDecimal[][] param) throws RemoteException
   {
      log.info("bigDecimalMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public BigInteger bigIntegerTest(BigInteger param)
   {
      log.info("bigIntegerTest: " + param);
      return param;
   }

   public BigInteger[] bigIntegerArrayTest(BigInteger[] param)
   {
      log.info("bigIntegerArrayTest: " + Arrays.asList(param));
      return param;
   }

   public BigInteger[][] bigIntegerMultiArrayTest(BigInteger[][] param) throws RemoteException
   {
      log.info("bigIntegerMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public boolean booleanTest(boolean param)
   {
      log.info("booleanTest: " + param);
      return param;
   }

   public boolean[] booleanArrayTest(boolean[] param)
   {
      log.info("booleanArrayTest: " + param);
      return param;
   }

   public boolean[][] booleanMultiArrayTest(boolean[][] param) throws RemoteException
   {
      log.info("booleanMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Boolean booleanWrapperTest(Boolean param) throws RemoteException
   {
      log.info("booleanWrapperTest: " + param);
      return param;
   }

   public Boolean[] booleanWrapperArrayTest(Boolean[] param) throws RemoteException
   {
      log.info("booleanWrapperArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Boolean[][] booleanWrapperMultiArrayTest(Boolean[][] param) throws RemoteException
   {
      log.info("booleanWrapperMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public byte byteTest(byte param)
   {
      log.info("byteTest: " + param);
      return param;
   }

   public byte[] byteArrayTest(byte[] param)
   {
      log.info("byteArrayTest: " + param);
      return param;
   }

   public byte[][] byteMultiArrayTest(byte[][] param) throws RemoteException
   {
      log.info("byteMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Byte byteWrapperTest(Byte param) throws RemoteException
   {
      log.info("byteWrapperTest: " + param);
      return param;
   }

   public Byte[] byteWrapperArrayTest(Byte[] param) throws RemoteException
   {
      log.info("byteWrapperArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Byte[][] byteWrapperMultiArrayTest(Byte[][] param) throws RemoteException
   {
      log.info("byteWrapperMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public QName qnameTest(QName param)
   {
      log.info("qnameTest: " + param);
      return param;
   }

   public QName[] qnameArrayTest(QName[] param)
   {
      log.info("qnameArrayTest: " + Arrays.asList(param));
      return param;
   }

   public QName[][] qnameMultiArrayTest(QName[][] param) throws RemoteException
   {
      log.info("qnameMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public byte[] base64BinaryTest(byte[] param)
   {
      log.info("base64BinaryTest: " + (param != null ? new String(param) : null));
      return param;
   }

   public byte[] hexBinaryTest(byte[] param)
   {
      log.info("hexBinaryTest: " + (param != null ? new String(param) : null));
      return param;
   }

   public Calendar calendarTest(Calendar param)
   {
      log.info("calendarTest: " + param.getTime());
      return param;
   }

   public Calendar[] calendarArrayTest(Calendar[] param)
   {
      Date[] dates = new Date[param.length];
      for (int i=0; i < param.length; i++)
         dates[i] = param[i].getTime();
      
      log.info("calendarArrayTest: " + Arrays.asList(dates));
      return param;
   }

   public Calendar[][] calendarMultiArrayTest(Calendar[][] param) throws RemoteException
   {
      log.info("calendarMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public double doubleTest(double param)
   {
      log.info("doubleTest: " + param);
      return param;
   }

   public double[] doubleArrayTest(double[] param)
   {
      log.info("doubleArrayTest: " + param);
      return param;
   }

   public double[][] doubleMultiArrayTest(double[][] param) throws RemoteException
   {
      log.info("doubleMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Double doubleWrapperTest(Double param) throws RemoteException
   {
      log.info("doubleWrapperTest: " + param);
      return param;
   }

   public Double[] doubleWrapperArrayTest(Double[] param) throws RemoteException
   {
      log.info("doubleWrapperArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Double[][] doubleWrapperMultiArrayTest(Double[][] param) throws RemoteException
   {
      log.info("doubleWrapperMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public float floatTest(float param)
   {
      log.info("floatTest: " + param);
      return param;
   }

   public float[] floatArrayTest(float[] param)
   {
      log.info("floatArrayTest: " + param);
      return param;
   }

   public float[][] floatMultiArrayTest(float[][] param) throws RemoteException
   {
      log.info("floatMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Float floatWrapperTest(Float param) throws RemoteException
   {
      log.info("floatWrapperTest: " + param);
      return param;
   }

   public Float[] floatWrapperArrayTest(Float[] param) throws RemoteException
   {
      log.info("floatWrapperArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Float[][] floatWrapperMultiArrayTest(Float[][] param) throws RemoteException
   {
      log.info("floatWrapperMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public int intTest(int param)
   {
      log.info("intTest: " + param);
      return param;
   }

   public int[] intArrayTest(int[] param)
   {
      log.info("intArrayTest: " + param);
      return param;
   }

   public int[][] intMultiArrayTest(int[][] param) throws RemoteException
   {
      log.info("intMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Integer intWrapperTest(Integer param) throws RemoteException
   {
      log.info("intWrapperTest: " + param);
      return param;
   }

   public Integer[] intWrapperArrayTest(Integer[] param) throws RemoteException
   {
      log.info("intWrapperArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Integer[][] intWrapperMultiArrayTest(Integer[][] param) throws RemoteException
   {
      log.info("intWrapperMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public JavaBean javaBeanTest(JavaBean param)
   {
      log.info("javaBeanTest: " + param);
      return param;
   }

   public JavaBean[] javaBeanArrayTest(JavaBean[] param)
   {
      log.info("javaBeanArrayTest: " + Arrays.asList(param));
      return param;
   }

   public long longTest(long param)
   {
      log.info("longTest: " + param);
      return param;
   }

   public long[] longArrayTest(long[] param)
   {
      log.info("longArrayTest: " + param);
      return param;
   }

   public long[][] longMultiArrayTest(long[][] param) throws RemoteException
   {
      log.info("longMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Long longWrapperTest(Long param) throws RemoteException
   {
      log.info("longWrapperTest: " + param);
      return param;
   }

   public Long[] longWrapperArrayTest(Long[] param) throws RemoteException
   {
      log.info("longWrapperArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Long[][] longWrapperMultiArrayTest(Long[][] param) throws RemoteException
   {
      log.info("longWrapperMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public short shortTest(short param)
   {
      log.info("shortTest: " + param);
      return param;
   }

   public short[] shortArrayTest(short[] param)
   {
      log.info("shortArrayTest: " + param);
      return param;
   }

   public short[][] shortMultiArrayTest(short[][] param) throws RemoteException
   {
      log.info("shortMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Short shortWrapperTest(Short param) throws RemoteException
   {
      log.info("shortWrapperTest: " + param);
      return param;
   }

   public Short[] shortWrapperArrayTest(Short[] param) throws RemoteException
   {
      log.info("shortWrapperArrayTest: " + Arrays.asList(param));
      return param;
   }

   public Short[][] shortWrapperMultiArrayTest(Short[][] param) throws RemoteException
   {
      log.info("shortWrapperMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public String stringTest(String param)
   {
      log.info("stringTest: " + param);
      return param;
   }

   public String[] stringArrayTest(String[] param)
   {
      log.info("stringArrayTest: " + Arrays.asList(param));
      return param;
   }

   public String[][] stringMultiArrayTest(String[][] param) throws RemoteException
   {
      log.info("stringMultiArrayTest: " + Arrays.asList(param));
      return param;
   }

   public void voidTest()
   {
      log.info("voidTest");
   }
}
