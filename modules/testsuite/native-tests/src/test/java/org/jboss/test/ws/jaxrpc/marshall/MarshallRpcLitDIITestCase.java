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

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.test.ws.jaxrpc.marshall.types.JavaBean;
import org.jboss.ws.Constants;
import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test the Dynamic Invocation Interface (DII) on the Call
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Oct-2004
 */
public class MarshallRpcLitDIITestCase extends MarshallTest
{
   private final String TARGET_ADDRESS = "http://" + getServerHost() + ":8080/jaxrpc-marshall-rpclit";

   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/marshall/rpclit";
   private static final String SERVICE_NAME = "MarshallService";

   // The static service cache
   private static Service service;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(MarshallRpcLitDIITestCase.class, "jaxrpc-marshall-rpclit.war") {
         protected void setUp() throws Exception
         {
            super.setUp();
            ServiceFactoryImpl factory = new ServiceFactoryImpl();
            service = factory.createService(new QName(TARGET_NAMESPACE, SERVICE_NAME));
         }
      };
   }

   public BigDecimal bigDecimalTest(BigDecimal param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "bigDecimalTest"));
      call.addParameter("BigDecimal_1", Constants.TYPE_LITERAL_DECIMAL, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_DECIMAL, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (BigDecimal)call.invoke(new Object[] { param });
   }

   public BigDecimal[] bigDecimalArrayTest(BigDecimal[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "bigDecimalArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/java/math", "BigDecimalArray");
      call.addParameter("arrayOfBigDecimal_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (BigDecimal[])call.invoke(new Object[] { params });
   }

   public BigDecimal[][] bigDecimalMultiArrayTest(BigDecimal[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "bigDecimalMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/java/math", "BigDecimalArrayArray");
      call.addParameter("arrayOfarrayOfBigDecimal_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (BigDecimal[][])call.invoke(new Object[] { params });
   }

   public BigInteger bigIntegerTest(BigInteger param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "bigIntegerTest"));
      call.addParameter("BigInteger_1", Constants.TYPE_LITERAL_INTEGER, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_INTEGER, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (BigInteger)call.invoke(new Object[] { param });
   }

   public BigInteger[] bigIntegerArrayTest(BigInteger[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "bigIntegerArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/java/math", "BigIntegerArray");
      call.addParameter("arrayOfBigInteger_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (BigInteger[])call.invoke(new Object[] { params });
   }

   public BigInteger[][] bigIntegerMultiArrayTest(BigInteger[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "bigIntegerMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/java/math", "BigIntegerArrayArray");
      call.addParameter("arrayOfarrayOfBigInteger_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (BigInteger[][])call.invoke(new Object[] { params });
   }

   public boolean booleanTest(boolean primParam) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "booleanTest"));
      call.addParameter("boolean_1", Constants.TYPE_LITERAL_BOOLEAN, boolean.class, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_BOOLEAN, boolean.class);
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      Boolean param = new Boolean(primParam);
      param = (Boolean)call.invoke(new Object[] { param });
      primParam = param.booleanValue();
      return primParam;
   }

   public boolean[] booleanArrayTest(boolean[] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "booleanArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/", "booleanArray");
      call.addParameter("arrayOfboolean_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (boolean[])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public boolean[][] booleanMultiArrayTest(boolean[][] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "booleanMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/", "booleanArrayArray");
      call.addParameter("arrayOfarrayOfboolean_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (boolean[][])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public Boolean booleanWrapperTest(Boolean param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "booleanWrapperTest"));
      call.addParameter("Boolean_1", Constants.TYPE_LITERAL_BOOLEAN, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_BOOLEAN, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Boolean)call.invoke(new Object[] { param });
   }

   public Boolean[] booleanWrapperArrayTest(Boolean[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "booleanWrapperArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang", "BooleanArray");
      call.addParameter("arrayOfBoolean_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Boolean[])call.invoke(new Object[] { params });
   }

   public Boolean[][] booleanWrapperMultiArrayTest(Boolean[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "booleanWrapperMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang", "BooleanArrayArray");
      call.addParameter("arrayOfarrayOfBoolean_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Boolean[][])call.invoke(new Object[] { params });
   }

   public byte byteTest(byte primParam) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "byteTest"));
      call.addParameter("byte_1", Constants.TYPE_LITERAL_BYTE, byte.class, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_BYTE, byte.class);
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      Byte param = new Byte(primParam);
      param = (Byte)call.invoke(new Object[] { param });
      primParam = param.byteValue();
      return primParam;
   }

   public byte[] byteArrayTest(byte[] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "byteArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "byteArray");
      call.addParameter("arrayOfbyte_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (byte[])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public byte[][] byteMultiArrayTest(byte[][] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "byteMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "byteArrayArray");
      call.addParameter("arrayOfarrayOfbyte_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (byte[][])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public Byte byteWrapperTest(Byte param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "byteWrapperTest"));
      call.addParameter("Byte_1", Constants.TYPE_LITERAL_BYTE, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_BYTE, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Byte)call.invoke(new Object[] { param });
   }

   public Byte[] byteWrapperArrayTest(Byte[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "byteWrapperArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "ByteArray");
      call.addParameter("arrayOfByte_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Byte[])call.invoke(new Object[] { params });
   }

   public Byte[][] byteWrapperMultiArrayTest(Byte[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "byteWrapperMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "ByteArrayArray");
      call.addParameter("arrayOfarrayOfByte_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Byte[][])call.invoke(new Object[] { params });
   }

   public QName qnameTest(QName param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "qnameTest"));
      call.addParameter("QName_1", Constants.TYPE_LITERAL_QNAME, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_QNAME, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (QName)call.invoke(new Object[] { param });
   }

   public QName[] qnameArrayTest(QName[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "qnameArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "QNameArray");
      call.addParameter("arrayOfQName_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (QName[])call.invoke(new Object[] { params });
   }

   public QName[][] qnameMultiArrayTest(QName[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "qnameMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "QNameArrayArray");
      call.addParameter("arrayOfarrayOfQName_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (QName[][])call.invoke(new Object[] { params });
   }

   public byte[] base64BinaryTest(byte[] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "base64BinaryTest"));
      call.addParameter("arrayOfbyte_1", Constants.TYPE_LITERAL_BASE64BINARY, byte[].class, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_BASE64BINARY, byte[].class);
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (byte[])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public byte[] hexBinaryTest(byte[] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "hexBinaryTest"));
      call.addParameter("arrayOfbyte_1", Constants.TYPE_LITERAL_HEXBINARY, byte[].class, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_HEXBINARY, byte[].class);
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (byte[])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public Calendar calendarTest(Calendar param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "calendarTest"));
      call.addParameter("Calendar_1", Constants.TYPE_LITERAL_DATETIME, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_DATETIME, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Calendar)call.invoke(new Object[] { param });
   }

   public Calendar[] calendarArrayTest(Calendar[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "calendarArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "CalendarArray");
      call.addParameter("arrayOfCalendar_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Calendar[])call.invoke(new Object[] { params });
   }

   public Calendar[][] calendarMultiArrayTest(Calendar[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "calendarMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "CalendarArrayArray");
      call.addParameter("arrayOfarrayOfCalendar_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Calendar[][])call.invoke(new Object[] { params });
   }

   public double doubleTest(double primParam) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "doubleTest"));
      call.addParameter("double_1", Constants.TYPE_LITERAL_DOUBLE, double.class, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_DOUBLE, double.class);
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      Double param = new Double(primParam);
      param = (Double)call.invoke(new Object[] { param });
      primParam = param.doubleValue();
      return primParam;
   }

   public double[] doubleArrayTest(double[] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "doubleArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/", "doubleArray");
      call.addParameter("arrayOfdouble_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (double[])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public double[][] doubleMultiArrayTest(double[][] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "doubleMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/", "doubleArrayArray");
      call.addParameter("arrayOfarrayOfdouble_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (double[][])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public Double doubleWrapperTest(Double param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "doubleWrapperTest"));
      call.addParameter("Double_1", Constants.TYPE_LITERAL_DOUBLE, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_DOUBLE, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Double)call.invoke(new Object[] { param });
   }

   public Double[] doubleWrapperArrayTest(Double[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "doubleWrapperArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "DoubleArray");
      call.addParameter("arrayOfDouble_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Double[])call.invoke(new Object[] { params });
   }

   public Double[][] doubleWrapperMultiArrayTest(Double[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "doubleWrapperMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "DoubleArrayArray");
      call.addParameter("arrayOfarrayOfDouble_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Double[][])call.invoke(new Object[] { params });
   }

   public float floatTest(float primParam) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "floatTest"));
      call.addParameter("float_1", Constants.TYPE_LITERAL_FLOAT, float.class, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_FLOAT, float.class);
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      Float param = new Float (primParam);
      param = (Float)call.invoke(new Object[] { param });
      primParam = param.floatValue();
      return primParam;
   }

   public float[] floatArrayTest(float[] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "floatArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "floatArray");
      call.addParameter("arrayOffloat_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (float[])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public float[][] floatMultiArrayTest(float[][] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "floatMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "floatArrayArray");
      call.addParameter("arrayOfarrayOffloat_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (float[][])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public Float floatWrapperTest(Float param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "floatWrapperTest"));
      call.addParameter("Float_1", Constants.TYPE_LITERAL_FLOAT, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_FLOAT, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Float)call.invoke(new Object[] { param });
   }

   public Float[] floatWrapperArrayTest(Float[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "floatWrapperArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "FloatArray");
      call.addParameter("arrayOfFloat_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Float[])call.invoke(new Object[] { params });
   }

   public Float[][] floatWrapperMultiArrayTest(Float[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "floatWrapperMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "FloatArrayArray");
      call.addParameter("arrayOfarrayOfFloat_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Float[][])call.invoke(new Object[] { params });
   }

   public int intTest(int primParam) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "intTest"));
      call.addParameter("int_1", Constants.TYPE_LITERAL_INT, int.class, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_INT, int.class);
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      Integer param = new Integer(primParam);
      param = (Integer)call.invoke(new Object[] { param });
      primParam = param.intValue();
      return primParam;
   }

   public int[] intArrayTest(int[] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "intArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "intArray");
      call.addParameter("arrayOfint_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (int[])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public int[][] intMultiArrayTest(int[][] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "intMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "intArrayArray");
      call.addParameter("arrayOfarrayOfint_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (int[][])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public Integer intWrapperTest(Integer param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "intWrapperTest"));
      call.addParameter("Integer_1", Constants.TYPE_LITERAL_INT, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_INT, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Integer)call.invoke(new Object[] { param });
   }

   public Integer[] intWrapperArrayTest(Integer[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "intWrapperArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "IntegerArray");
      call.addParameter("arrayOfInteger_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Integer[])call.invoke(new Object[] { params });
   }

   public Integer[][] intWrapperMultiArrayTest(Integer[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "intWrapperMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "IntegerArrayArray");
      call.addParameter("arrayOfarrayOfInteger_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Integer[][])call.invoke(new Object[] { params });
   }

   public JavaBean javaBeanTest(JavaBean param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "javaBeanTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "JavaBean");
      call.addParameter("JavaBean_1", xmlType, param.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (JavaBean)call.invoke(new Object[] { param });
   }

   public JavaBean[] javaBeanArrayTest(JavaBean[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "javaBeanArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "JavaBeanArray");
      call.addParameter("arrayOfJavaBean_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (JavaBean[])call.invoke(new Object[] { params });
   }

   public long longTest(long primParam) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "longTest"));
      call.addParameter("long_1", Constants.TYPE_LITERAL_LONG, long.class, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_LONG, long.class);
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      Long param = new Long(primParam);
      param = (Long)call.invoke(new Object[] { param });
      primParam = param.longValue();
      return primParam;
   }

   public long[] longArrayTest(long[] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "longArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "longArray");
      call.addParameter("arrayOflong_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (long[])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public long[][] longMultiArrayTest(long[][] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "longMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "longArrayArray");
      call.addParameter("arrayOfarrayOflong_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (long[][])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public Long longWrapperTest(Long param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "longWrapperTest"));
      call.addParameter("Long_1", Constants.TYPE_LITERAL_LONG, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_LONG, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Long)call.invoke(new Object[] { param });
   }

   public Long[] longWrapperArrayTest(Long[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "longWrapperArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "LongArray");
      call.addParameter("arrayOfLong_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Long[])call.invoke(new Object[] { params });
   }

   public Long[][] longWrapperMultiArrayTest(Long[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "longWrapperMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "LongArrayArray");
      call.addParameter("arrayOfarrayOfLong_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Long[][])call.invoke(new Object[] { params });
   }

   public short shortTest(short primParam) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "shortTest"));
      call.addParameter("short_1", Constants.TYPE_LITERAL_SHORT, short.class, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_SHORT, short.class);
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      Short param = new Short(primParam);
      param = (Short)call.invoke(new Object[] { param });
      primParam = param.shortValue();
      return primParam;
   }

   public short[] shortArrayTest(short[] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "shortArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "shortArray");
      call.addParameter("arrayOfshort_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (short[])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public short[][] shortMultiArrayTest(short[][] primParams) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "shortMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "shortArrayArray");
      call.addParameter("arrayOfarrayOfshort_1", xmlType, primParams.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, primParams.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      primParams = (short[][])call.invoke(new Object[] { primParams });
      return primParams;
   }

   public Short shortWrapperTest(Short param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "shortWrapperTest"));
      call.addParameter("Short_1", Constants.TYPE_LITERAL_SHORT, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_SHORT, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Short)call.invoke(new Object[] { param });
   }

   public Short[] shortWrapperArrayTest(Short[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "shortWrapperArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "ShortArray");
      call.addParameter("arrayOfShort_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Short[])call.invoke(new Object[] { params });
   }

   public Short[][] shortWrapperMultiArrayTest(Short[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "shortWrapperMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types", "ShortArrayArray");
      call.addParameter("arrayOfarrayOfShort_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (Short[][])call.invoke(new Object[] { params });
   }

   public String stringTest(String param) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "stringTest"));
      call.addParameter("String_1", Constants.TYPE_LITERAL_STRING, param.getClass(), ParameterMode.IN);
      call.setReturnType(Constants.TYPE_LITERAL_STRING, param.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (String)call.invoke(new Object[] { param });
   }

   public String[] stringArrayTest(String[] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "stringArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang", "StringArray");
      call.addParameter("arrayOfString_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (String[])call.invoke(new Object[] { params });
   }

   public String[][] stringMultiArrayTest(String[][] params) throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "stringMultiArrayTest"));
      QName xmlType = new QName("http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang", "StringArrayArray");
      call.addParameter("arrayOfarrayOfString_1", xmlType, params.getClass(), ParameterMode.IN);
      call.setReturnType(xmlType, params.getClass());
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      return (String[][])call.invoke(new Object[] { params });
   }

   public void voidTest() throws Exception
   {
      Call call = service.createCall();
      call.setOperationName(new QName(TARGET_NAMESPACE, "voidTest"));
      call.setTargetEndpointAddress(TARGET_ADDRESS);
      call.invoke(new Object[] {});
   }
}
