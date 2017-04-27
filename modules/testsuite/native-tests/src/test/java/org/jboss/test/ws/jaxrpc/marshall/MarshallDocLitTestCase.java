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

import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import junit.framework.Test;

import org.jboss.test.ws.jaxrpc.marshall.types.JavaBean;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test standard JAX-RPC types.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 15-Feb-2005
 */
public class MarshallDocLitTestCase extends MarshallTest
{
   // The static endpoint cache
   private static StandardTypes port;

   /** Deploy the test ear */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(MarshallDocLitTestCase.class, "jaxrpc-marshall-doclit.war, jaxrpc-marshall-doclit-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/StandardTypes");
         port = (StandardTypes)service.getPort(StandardTypes.class);         
      }
   }

   public BigDecimal bigDecimalTest(BigDecimal param) throws Exception
   {
      return port.bigDecimalTest(param);
   }

   public BigDecimal[] bigDecimalArrayTest(BigDecimal[] params) throws Exception
   {
      return port.bigDecimalArrayTest(params);
   }

   public BigDecimal[][] bigDecimalMultiArrayTest(BigDecimal[][] params) throws Exception
   {
      return port.bigDecimalMultiArrayTest(params);
   }

   public BigInteger bigIntegerTest(BigInteger param) throws Exception
   {
      return port.bigIntegerTest(param);
   }

   public BigInteger[] bigIntegerArrayTest(BigInteger[] params) throws Exception
   {
      return port.bigIntegerArrayTest(params);
   }

   public BigInteger[][] bigIntegerMultiArrayTest(BigInteger[][] params) throws Exception
   {
      return port.bigIntegerMultiArrayTest(params);
   }

   public boolean booleanTest(boolean param) throws Exception
   {
      return port.booleanTest(param);
   }

   public boolean[] booleanArrayTest(boolean[] params) throws Exception
   {
      return port.booleanArrayTest(params);
   }

   public boolean[][] booleanMultiArrayTest(boolean[][] params) throws Exception
   {
      return port.booleanMultiArrayTest(params);
   }

   public Boolean booleanWrapperTest(Boolean param) throws Exception
   {
      return port.booleanWrapperTest(param);
   }

   public Boolean[] booleanWrapperArrayTest(Boolean[] params) throws Exception
   {
      return port.booleanWrapperArrayTest(params);
   }

   public Boolean[][] booleanWrapperMultiArrayTest(Boolean[][] params) throws Exception
   {
      return port.booleanWrapperMultiArrayTest(params);
   }

   public byte byteTest(byte param) throws Exception
   {
      return port.byteTest(param);
   }

   public byte[] byteArrayTest(byte[] params) throws Exception
   {
      return port.byteArrayTest(params);
   }

   public byte[][] byteMultiArrayTest(byte[][] params) throws Exception
   {
      return port.byteMultiArrayTest(params);
   }

   public Byte byteWrapperTest(Byte param) throws Exception
   {
      return port.byteWrapperTest(param);
   }

   public Byte[] byteWrapperArrayTest(Byte[] params) throws Exception
   {
      return port.byteWrapperArrayTest(params);
   }

   public Byte[][] byteWrapperMultiArrayTest(Byte[][] params) throws Exception
   {
      return port.byteWrapperMultiArrayTest(params);
   }

   public QName qnameTest(QName param) throws Exception
   {
      return port.qnameTest(param);
   }

   public QName[] qnameArrayTest(QName[] params) throws Exception
   {
      return port.qnameArrayTest(params);
   }

   public QName[][] qnameMultiArrayTest(QName[][] params) throws Exception
   {
      return port.qnameMultiArrayTest(params);
   }

   public byte[] base64BinaryTest(byte[] params) throws Exception
   {
      return port.base64BinaryTest(params);
   }

   public byte[] hexBinaryTest(byte[] params) throws Exception
   {
      return port.hexBinaryTest(params);
   }

   public Calendar calendarTest(Calendar param) throws Exception
   {
      return port.calendarTest(param);
   }

   public Calendar[] calendarArrayTest(Calendar[] params) throws Exception
   {
      return port.calendarArrayTest(params);
   }

   public Calendar[][] calendarMultiArrayTest(Calendar[][] params) throws Exception
   {
      return port.calendarMultiArrayTest(params);
   }

   public double doubleTest(double param) throws Exception
   {
      return port.doubleTest(param);
   }

   public double[] doubleArrayTest(double[] params) throws Exception
   {
      return port.doubleArrayTest(params);
   }

   public double[][] doubleMultiArrayTest(double[][] params) throws Exception
   {
      return port.doubleMultiArrayTest(params);
   }

   public Double doubleWrapperTest(Double param) throws Exception
   {
      return port.doubleWrapperTest(param);
   }

   public Double[] doubleWrapperArrayTest(Double[] params) throws Exception
   {
      return port.doubleWrapperArrayTest(params);
   }

   public Double[][] doubleWrapperMultiArrayTest(Double[][] params) throws Exception
   {
      return port.doubleWrapperMultiArrayTest(params);
   }

   public float floatTest(float param) throws Exception
   {
      return port.floatTest(param);
   }

   public float[] floatArrayTest(float[] params) throws Exception
   {
      return port.floatArrayTest(params);
   }

   public float[][] floatMultiArrayTest(float[][] params) throws Exception
   {
      return port.floatMultiArrayTest(params);
   }

   public Float floatWrapperTest(Float param) throws Exception
   {
      return port.floatWrapperTest(param);
   }

   public Float[] floatWrapperArrayTest(Float[] params) throws Exception
   {
      return port.floatWrapperArrayTest(params);
   }

   public Float[][] floatWrapperMultiArrayTest(Float[][] params) throws Exception
   {
      return port.floatWrapperMultiArrayTest(params);
   }

   public int intTest(int param) throws Exception
   {
      return port.intTest(param);
   }

   public int[] intArrayTest(int[] params) throws Exception
   {
      return port.intArrayTest(params);
   }

   public int[][] intMultiArrayTest(int[][] params) throws Exception
   {
     return port.intMultiArrayTest(params);
   }

   public Integer intWrapperTest(Integer param) throws Exception
   {
      return port.intWrapperTest(param);
   }

   public Integer[] intWrapperArrayTest(Integer[] params) throws Exception
   {
      return port.intWrapperArrayTest(params);
   }

   public Integer[][] intWrapperMultiArrayTest(Integer[][] params) throws Exception
   {
      return port.intWrapperMultiArrayTest(params);
   }

   public JavaBean javaBeanTest(JavaBean param) throws Exception
   {
      return port.javaBeanTest(param);
   }

   // [JBWS-1281] Marshalling exception with xsi:nil='1' but without xsi:type
   public void testJBWS1281() throws Exception
   {
      JavaBean reqBean = new JavaBean();
      JavaBean resBean = port.javaBeanTest(reqBean);
      assertEquals(reqBean, resBean);
   }

   public JavaBean[] javaBeanArrayTest(JavaBean[] params) throws Exception
   {
      return port.javaBeanArrayTest(params);
   }

   public long longTest(long param) throws Exception
   {
      return port.longTest(param);
   }

   public long[] longArrayTest(long[] params) throws Exception
   {
      return port.longArrayTest(params);
   }

   public long[][] longMultiArrayTest(long[][] params) throws Exception
   {
      return port.longMultiArrayTest(params);
   }

   public Long longWrapperTest(Long param) throws Exception
   {
      return port.longWrapperTest(param);
   }

   public Long[] longWrapperArrayTest(Long[] params) throws Exception
   {
      return port.longWrapperArrayTest(params);
   }

   public Long[][] longWrapperMultiArrayTest(Long[][] params) throws Exception
   {
      return port.longWrapperMultiArrayTest(params);
   }

   public short shortTest(short param) throws Exception
   {
      return port.shortTest(param);
   }

   public short[] shortArrayTest(short[] params) throws Exception
   {
      return port.shortArrayTest(params);
   }

   public short[][] shortMultiArrayTest(short[][] params) throws Exception
   {
      return port.shortMultiArrayTest(params);
   }

   public Short shortWrapperTest(Short param) throws Exception
   {
      return port.shortWrapperTest(param);
   }

   public Short[] shortWrapperArrayTest(Short[] params) throws Exception
   {
      return port.shortWrapperArrayTest(params);
   }

   public Short[][] shortWrapperMultiArrayTest(Short[][] params) throws Exception
   {
      return port.shortWrapperMultiArrayTest(params);
   }

   public String stringTest(String param) throws Exception
   {
      return port.stringTest(param);
   }

   public String[] stringArrayTest(String[] params) throws Exception
   {
      return port.stringArrayTest(params);
   }

   public String[][] stringMultiArrayTest(String[][] params) throws Exception
   {
      return port.stringMultiArrayTest(params);
   }

   public void voidTest() throws Exception
   {
      port.voidTest();
   }
}
