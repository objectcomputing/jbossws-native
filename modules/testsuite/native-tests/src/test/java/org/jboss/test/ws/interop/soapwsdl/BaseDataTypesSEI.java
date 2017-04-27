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
package org.jboss.test.ws.interop.soapwsdl;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author Heiko.Braun@jboss.org
 * @since 29.01.2007
 */
public interface BaseDataTypesSEI {

   public boolean retBool(boolean inBool);

    public short retByte(short inByte);

    public byte[] retByteArray(byte[] inByteArray);

    public int retChar(int inChar);

    public XMLGregorianCalendar retDateTime(XMLGregorianCalendar inDateTime);

    public java.math.BigDecimal retDecimal(java.math.BigDecimal inDecimal);

    public double retDouble(double inDouble);

    public float retFloat(float inFloat);

    public java.lang.String retGuid(java.lang.String inGuid);

    public int retInt(int inInt);

    public long retLong(long inLong);

    public javax.xml.soap.SOAPElement retObject(javax.xml.soap.SOAPElement inObject);

    public javax.xml.namespace.QName retQName(javax.xml.namespace.QName inQName);

    public byte retSByte(byte inSByte);

    public short retShort(short inShort);

    public float retSingle(float inSingle);

    public java.lang.String retString(java.lang.String inString);

    public java.lang.String retTimeSpan(java.lang.String inTimeSpan);

    public long retUInt(long inUInt);

    public java.math.BigInteger retULong(java.math.BigInteger inULong);

    public int retUShort(int inUShort);

    public String retUri(String inUri);
         

}
