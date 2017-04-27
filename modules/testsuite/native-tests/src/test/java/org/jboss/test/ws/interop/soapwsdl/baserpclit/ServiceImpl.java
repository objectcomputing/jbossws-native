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
package org.jboss.test.ws.interop.soapwsdl.baserpclit;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebParam;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Heiko.Braun@jboss.org
 * @since 29.01.2007
 */
@WebService(
		endpointInterface = "org.jboss.test.ws.interop.soapwsdl.baserpclit.IBaseDataTypesRpcLit",
		serviceName = "BaseDataTypesRpcLitService",
		targetNamespace = "http://tempuri.org/"
)
public class ServiceImpl implements IBaseDataTypesRpcLit
{

	@WebMethod(operationName = "RetBool", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetBool")
	@WebResult(name = "RetBoolResult", partName = "RetBoolResult")
	public boolean retBool(@WebParam(name = "inBool", partName = "inBool") boolean inBool) {
		return inBool;
	}

	@WebMethod(operationName = "RetByte", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetByte")
	@WebResult(name = "RetByteResult", partName = "RetByteResult")
	public short retByte(@WebParam(name = "inByte", partName = "inByte") short inByte) {
		return inByte;
	}

	@WebMethod(operationName = "RetSByte", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetSByte")
	@WebResult(name = "RetSByteResult", partName = "RetSByteResult")
	public byte retSByte(@WebParam(name = "inSByte", partName = "inSByte") byte inSByte) {
		return inSByte;
	}

	@WebMethod(operationName = "RetByteArray", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetByteArray")
	@WebResult(name = "RetByteArrayResult", partName = "RetByteArrayResult")
	public byte[] retByteArray(@WebParam(name = "inByteArray", partName = "inByteArray") byte[] inByteArray) {
		return inByteArray;
	}

	@WebMethod(operationName = "RetChar", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetChar")
	@WebResult(name = "RetCharResult", partName = "RetCharResult")
	public int retChar(@WebParam(name = "inChar", partName = "inChar") int inChar) {
		return inChar;
	}

	@WebMethod(operationName = "RetDecimal", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetDecimal")
	@WebResult(name = "RetDecimalResult", partName = "RetDecimalResult")
	public BigDecimal retDecimal(@WebParam(name = "inDecimal", partName = "inDecimal") BigDecimal inDecimal) {
		return inDecimal;
	}

	@WebMethod(operationName = "RetFloat", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetFloat")
	@WebResult(name = "RetFloatResult", partName = "RetFloatResult")
	public float retFloat(@WebParam(name = "inFloat", partName = "inFloat") float inFloat) {
		return inFloat;
	}

	@WebMethod(operationName = "RetDouble", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetDouble")
	@WebResult(name = "RetDoubleResult", partName = "RetDoubleResult")
	public double retDouble(@WebParam(name = "inDouble", partName = "inDouble") double inDouble) {
		return inDouble;
	}

	@WebMethod(operationName = "RetSingle", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetSingle")
	@WebResult(name = "RetSingleResult", partName = "RetSingleResult")
	public float retSingle(@WebParam(name = "inSingle", partName = "inSingle") float inSingle) {
		return inSingle;
	}

	@WebMethod(operationName = "RetInt", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetInt")
	@WebResult(name = "RetIntResult", partName = "RetIntResult")
	public int retInt(@WebParam(name = "inInt", partName = "inInt") int inInt) {
		return inInt;
	}

	@WebMethod(operationName = "RetShort", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetShort")
	@WebResult(name = "RetShortResult", partName = "RetShortResult")
	public short retShort(@WebParam(name = "inShort", partName = "inShort") short inShort) {
		return inShort;
	}

	@WebMethod(operationName = "RetLong", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetLong")
	@WebResult(name = "RetLongResult", partName = "RetLongResult")
	public long retLong(@WebParam(name = "inLong", partName = "inLong") long inLong) {
		return inLong;
	}

	@WebMethod(operationName = "RetObject", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetObject")
	@WebResult(name = "RetObjectResult", partName = "RetObjectResult")
	public Object retObject(@WebParam(name = "inObject", partName = "inObject") Object inObject) {
		return inObject;
	}

	@WebMethod(operationName = "RetUInt", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetUInt")
	@WebResult(name = "RetUIntResult", partName = "RetUIntResult")
	public long retUInt(@WebParam(name = "inUInt", partName = "inUInt") long inUInt) {
		return inUInt;
	}

	@WebMethod(operationName = "RetUShort", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetUShort")
	@WebResult(name = "RetUShortResult", partName = "RetUShortResult")
	public int retUShort(@WebParam(name = "inUShort", partName = "inUShort") int inUShort) {
		return inUShort;
	}

	@WebMethod(operationName = "RetULong", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetULong")
	@WebResult(name = "RetULongResult", partName = "RetULongResult")
	public BigInteger retULong(@WebParam(name = "inULong", partName = "inULong") BigInteger inULong) {
		return inULong;
	}

	@WebMethod(operationName = "RetString", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetString")
	@WebResult(name = "RetStringResult", partName = "RetStringResult")
	public String retString(@WebParam(name = "inString", partName = "inString") String inString) {
		return inString;
	}

	@WebMethod(operationName = "RetGuid", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetGuid")
	@WebResult(name = "RetGuidResult", partName = "RetGuidResult")
	public String retGuid(@WebParam(name = "inGuid", partName = "inGuid") String inGuid) {
		return inGuid;
	}

	@WebMethod(operationName = "RetUri", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetUri")
	@WebResult(name = "RetUriResult", partName = "RetUriResult")
	public String retUri(@WebParam(name = "inUri", partName = "inUri") String inUri) {
		return inUri;
	}

	@WebMethod(operationName = "RetDateTime", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetDateTime")
	@WebResult(name = "RetDateTimeResult", partName = "RetDateTimeResult")
	public XMLGregorianCalendar retDateTime(@WebParam(name = "inDateTime", partName = "inDateTime") XMLGregorianCalendar inDateTime) {
		return inDateTime;
	}

	@WebMethod(operationName = "RetTimeSpan", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetTimeSpan")
	@WebResult(name = "RetTimeSpanResult", partName = "RetTimeSpanResult")
	public Duration retTimeSpan(@WebParam(name = "inTimeSpan", partName = "inTimeSpan") Duration inTimeSpan) {
		return inTimeSpan;
	}

	@WebMethod(operationName = "RetQName", action = "http://tempuri.org/IBaseDataTypesRpcLit/RetQName")
	@WebResult(name = "RetQNameResult", partName = "RetQNameResult")
	public QName retQName(@WebParam(name = "inQName", partName = "inQName") QName inQName) {
		return inQName;
	}
}
