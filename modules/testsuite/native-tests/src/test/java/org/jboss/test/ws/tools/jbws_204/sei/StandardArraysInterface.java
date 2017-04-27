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
package org.jboss.test.ws.tools.jbws_204.sei;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;

import javax.xml.namespace.QName;

import org.jboss.test.ws.jaxrpc.marshall.types.JavaBean;
 
/**
 *  Service Endpoint Interface that tests arrays of standard types
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 19, 2005 
 */ 
public interface StandardArraysInterface extends Remote
{ 
    public BigDecimal[] bigDecimalArrayTest(BigDecimal[] param) throws RemoteException;
    
    public BigInteger[] bigIntegerArrayTest(BigInteger[] param) throws RemoteException;

    public boolean[] booleanArrayTest(boolean[] param) throws RemoteException;

    public Boolean[] booleanWrapperArrayTest(Boolean[] param) throws RemoteException;
    
    public byte[] byteArrayTest(byte[] param) throws RemoteException;
    
    public Byte[] byteWrapperArrayTest(Byte[] param) throws RemoteException;

    public QName[] qnameArrayTest(QName[] param) throws RemoteException;

    public byte[] base64BinaryTest(byte[] param) throws RemoteException;

    public byte[] hexBinaryTest(byte[] param) throws RemoteException;
 
    public Calendar[] calendarArrayTest(Calendar[] param) throws RemoteException;
 
    public double[] doubleArrayTest(double[] param) throws RemoteException;
 
    public Double[] doubleWrapperArrayTest(Double[] param) throws RemoteException;
 
    public float[] floatArrayTest(float[] param) throws RemoteException;
 
    public Float[] floatWrapperArrayTest(Float[] param) throws RemoteException;
 
    public int[] intArrayTest(int[] param) throws RemoteException;
 
    public Integer[] intWrapperArrayTest(Integer[] param) throws RemoteException;
 
    public JavaBean[] javaBeanArrayTest(JavaBean[] param) throws RemoteException;
 
    public long[] longArrayTest(long[] param) throws RemoteException;
 
    public Long[] longWrapperArrayTest(Long[] param) throws RemoteException;
 
    public short[] shortArrayTest(short[] param) throws RemoteException;
 
    public Short[] shortWrapperArrayTest(Short[] param) throws RemoteException;
 
    public String[] stringArrayTest(String[] param) throws RemoteException; 
}

