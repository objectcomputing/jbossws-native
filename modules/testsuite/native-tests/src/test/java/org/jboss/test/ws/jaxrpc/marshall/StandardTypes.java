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
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;

import javax.xml.namespace.QName;

import org.jboss.test.ws.jaxrpc.marshall.types.JavaBean;

public interface StandardTypes extends Remote
{
   public BigDecimal bigDecimalTest(BigDecimal param) throws RemoteException;

   public BigDecimal[] bigDecimalArrayTest(BigDecimal[] param) throws RemoteException;

   public BigDecimal[][] bigDecimalMultiArrayTest(BigDecimal[][] param) throws RemoteException;

   public BigInteger bigIntegerTest(BigInteger param) throws RemoteException;

   public BigInteger[] bigIntegerArrayTest(BigInteger[] param) throws RemoteException;

   public BigInteger[][] bigIntegerMultiArrayTest(BigInteger[][] param) throws RemoteException;

   public boolean booleanTest(boolean param) throws RemoteException;

   public boolean[] booleanArrayTest(boolean[] param) throws RemoteException;

   public boolean[][] booleanMultiArrayTest(boolean[][] param) throws RemoteException;

   public Boolean booleanWrapperTest(Boolean param) throws RemoteException;

   public Boolean[] booleanWrapperArrayTest(Boolean[] param) throws RemoteException;

   public Boolean[][] booleanWrapperMultiArrayTest(Boolean[][] param) throws RemoteException;

   public byte byteTest(byte param) throws RemoteException;

   public byte[] byteArrayTest(byte[] param) throws RemoteException;

   public byte[][] byteMultiArrayTest(byte[][] param) throws RemoteException;

   public Byte byteWrapperTest(Byte param) throws RemoteException;

   public Byte[] byteWrapperArrayTest(Byte[] param) throws RemoteException;

   public Byte[][] byteWrapperMultiArrayTest(Byte[][] param) throws RemoteException;

   public QName qnameTest(QName param) throws RemoteException;

   public QName[] qnameArrayTest(QName[] param) throws RemoteException;

   public QName[][] qnameMultiArrayTest(QName[][] param) throws RemoteException;

   public byte[] base64BinaryTest(byte[] param) throws RemoteException;

   public byte[] hexBinaryTest(byte[] param) throws RemoteException;

   public Calendar calendarTest(Calendar param) throws RemoteException;

   public Calendar[] calendarArrayTest(Calendar[] param) throws RemoteException;

   public Calendar[][] calendarMultiArrayTest(Calendar[][] param) throws RemoteException;

   public double doubleTest(double param) throws RemoteException;

   public double[] doubleArrayTest(double[] param) throws RemoteException;

   public double[][] doubleMultiArrayTest(double[][] param) throws RemoteException;

   public Double doubleWrapperTest(Double param) throws RemoteException;

   public Double[] doubleWrapperArrayTest(Double[] param) throws RemoteException;

   public Double[][] doubleWrapperMultiArrayTest(Double[][] param) throws RemoteException;

   public float floatTest(float param) throws RemoteException;

   public float[] floatArrayTest(float[] param) throws RemoteException;

   public float[][] floatMultiArrayTest(float[][] param) throws RemoteException;

   public Float floatWrapperTest(Float param) throws RemoteException;

   public Float[] floatWrapperArrayTest(Float[] param) throws RemoteException;

   public Float[][] floatWrapperMultiArrayTest(Float[][] param) throws RemoteException;

   public int intTest(int param) throws RemoteException;

   public int[] intArrayTest(int[] param) throws RemoteException;

   public int[][] intMultiArrayTest(int[][] param) throws RemoteException;

   public Integer intWrapperTest(Integer param) throws RemoteException;

   public Integer[] intWrapperArrayTest(Integer[] param) throws RemoteException;

   public Integer[][] intWrapperMultiArrayTest(Integer[][] param) throws RemoteException;

   public JavaBean javaBeanTest(JavaBean param) throws RemoteException;

   public JavaBean[] javaBeanArrayTest(JavaBean[] param) throws RemoteException;

   public long longTest(long param) throws RemoteException;

   public long[] longArrayTest(long[] param) throws RemoteException;

   public long[][] longMultiArrayTest(long[][] param) throws RemoteException;

   public Long longWrapperTest(Long param) throws RemoteException;

   public Long[] longWrapperArrayTest(Long[] param) throws RemoteException;

   public Long[][] longWrapperMultiArrayTest(Long[][] param) throws RemoteException;

   public short shortTest(short param) throws RemoteException;

   public short[] shortArrayTest(short[] param) throws RemoteException;

   public short[][] shortMultiArrayTest(short[][] param) throws RemoteException;

   public Short shortWrapperTest(Short param) throws RemoteException;

   public Short[] shortWrapperArrayTest(Short[] param) throws RemoteException;

   public Short[][] shortWrapperMultiArrayTest(Short[][] param) throws RemoteException;

   public String stringTest(String param) throws RemoteException;

   public String[] stringArrayTest(String[] param) throws RemoteException;

   public String[][] stringMultiArrayTest(String[][] param) throws RemoteException;

   public void voidTest() throws RemoteException;
}
