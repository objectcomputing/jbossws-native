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
package org.jboss.test.ws.interop.soapwsdl.basedoclitb;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebParam;
import javax.jws.WebService;
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
		endpointInterface = "org.jboss.test.ws.interop.soapwsdl.basedoclitb.IBaseDataTypesDocLitB",
		serviceName = "BaseDataTypesDocLitBService",
		targetNamespace = "http://tempuri.org/"
)
public class ServiceImpl implements IBaseDataTypesDocLitB
{
   @WebMethod(operationName = "RetBool", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetBool")
   @WebResult(name = "RetBoolResult", targetNamespace = "http://tempuri.org/", partName = "RetBoolResult")
   public boolean retBool(@WebParam(name = "inBool", targetNamespace = "http://tempuri.org/", partName = "inBool") boolean inBool) {
      return inBool;
   }

   @WebMethod(operationName = "RetByte", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetByte")
   @WebResult(name = "RetByteResult", targetNamespace = "http://tempuri.org/", partName = "RetByteResult")
   public short retByte(@WebParam(name = "inByte", targetNamespace = "http://tempuri.org/", partName = "inByte") short inByte) {
      return inByte;
   }

   @WebMethod(operationName = "RetSByte", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetSByte")
   @WebResult(name = "RetSByteResult", targetNamespace = "http://tempuri.org/", partName = "RetSByteResult")
   public byte retSByte(@WebParam(name = "inSByte", targetNamespace = "http://tempuri.org/", partName = "inSByte") byte inSByte) {
      return inSByte;
   }

   @WebMethod(operationName = "RetByteArray", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetByteArray")
   @WebResult(name = "RetByteArrayResult", targetNamespace = "http://tempuri.org/", partName = "RetByteArrayResult")
   public byte[] retByteArray(@WebParam(name = "inByteArray", targetNamespace = "http://tempuri.org/", partName = "inByteArray") byte[] inByteArray) {
      return inByteArray;
   }

   @WebMethod(operationName = "RetChar", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetChar")
   @WebResult(name = "RetCharResult", targetNamespace = "http://tempuri.org/", partName = "RetCharResult")
   public int retChar(@WebParam(name = "inChar", targetNamespace = "http://tempuri.org/", partName = "inChar") int inChar) {
      return inChar;
   }

   @WebMethod(operationName = "RetDecimal", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetDecimal")
   @WebResult(name = "RetDecimalResult", targetNamespace = "http://tempuri.org/", partName = "RetDecimalResult")
   public BigDecimal retDecimal(@WebParam(name = "inDecimal", targetNamespace = "http://tempuri.org/", partName = "inDecimal") BigDecimal inDecimal) {
      return inDecimal;
   }

   @WebMethod(operationName = "RetFloat", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetFloat")
   @WebResult(name = "RetFloatResult", targetNamespace = "http://tempuri.org/", partName = "RetFloatResult")
   public float retFloat(@WebParam(name = "inFloat", targetNamespace = "http://tempuri.org/", partName = "inFloat") float inFloat) {
      return inFloat;
   }

   @WebMethod(operationName = "RetDouble", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetDouble")
   @WebResult(name = "RetDoubleResult", targetNamespace = "http://tempuri.org/", partName = "RetDoubleResult")
   public double retDouble(@WebParam(name = "inDouble", targetNamespace = "http://tempuri.org/", partName = "inDouble") double inDouble) {
      return inDouble;
   }

   @WebMethod(operationName = "RetSingle", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetSingle")
   @WebResult(name = "RetSingleResult", targetNamespace = "http://tempuri.org/", partName = "RetSingleResult")
   public float retSingle(@WebParam(name = "inSingle", targetNamespace = "http://tempuri.org/", partName = "inSingle") float inSingle) {
      return inSingle;
   }

   @WebMethod(operationName = "RetInt", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetInt")
   @WebResult(name = "RetIntResult", targetNamespace = "http://tempuri.org/", partName = "RetIntResult")
   public int retInt(@WebParam(name = "inInt", targetNamespace = "http://tempuri.org/", partName = "inInt") int inInt) {
      return inInt;
   }

   @WebMethod(operationName = "RetShort", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetShort")
   @WebResult(name = "RetShortResult", targetNamespace = "http://tempuri.org/", partName = "RetShortResult")
   public short retShort(@WebParam(name = "inShort", targetNamespace = "http://tempuri.org/", partName = "inShort") short inShort) {
      return inShort;
   }

   @WebMethod(operationName = "RetLong", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetLong")
   @WebResult(name = "RetLongResult", targetNamespace = "http://tempuri.org/", partName = "RetLongResult")
   public long retLong(@WebParam(name = "inLong", targetNamespace = "http://tempuri.org/", partName = "inLong") long inLong) {
      return inLong;
   }

   @WebMethod(operationName = "RetObject", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetObject")
   @WebResult(name = "RetObjectResult", targetNamespace = "http://tempuri.org/", partName = "RetObjectResult")
   public Object retObject(@WebParam(name = "inObject", targetNamespace = "http://tempuri.org/", partName = "inObject") Object inObject) {
      return inObject;
   }

   @WebMethod(operationName = "RetUInt", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetUInt")
   @WebResult(name = "RetUIntResult", targetNamespace = "http://tempuri.org/", partName = "RetUIntResult")
   public long retUInt(@WebParam(name = "inUInt", targetNamespace = "http://tempuri.org/", partName = "inUInt") long inUInt) {
      return inUInt;
   }

   @WebMethod(operationName = "RetUShort", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetUShort")
   @WebResult(name = "RetUShortResult", targetNamespace = "http://tempuri.org/", partName = "RetUShortResult")
   public int retUShort(@WebParam(name = "inUShort", targetNamespace = "http://tempuri.org/", partName = "inUShort") int inUShort) {
      return inUShort;
   }

   @WebMethod(operationName = "RetULong", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetULong")
   @WebResult(name = "RetULongResult", targetNamespace = "http://tempuri.org/", partName = "RetULongResult")
   public BigInteger retULong(@WebParam(name = "inULong", targetNamespace = "http://tempuri.org/", partName = "inULong") BigInteger inULong) {
      return inULong;
   }

   @WebMethod(operationName = "RetString", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetString")
   @WebResult(name = "RetStringResult", targetNamespace = "http://tempuri.org/", partName = "RetStringResult")
   public String retString(@WebParam(name = "inString", targetNamespace = "http://tempuri.org/", partName = "inString") String inString) {
      return inString;
   }

   @WebMethod(operationName = "RetGuid", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetGuid")
   @WebResult(name = "RetGuidResult", targetNamespace = "http://tempuri.org/", partName = "RetGuidResult")
   public String retGuid(@WebParam(name = "inGuid", targetNamespace = "http://tempuri.org/", partName = "inGuid") String inGuid) {
      return inGuid;
   }

   @WebMethod(operationName = "RetUri", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetUri")
   @WebResult(name = "RetUriResult", targetNamespace = "http://tempuri.org/", partName = "RetUriResult")
   public String retUri(@WebParam(name = "inUri", targetNamespace = "http://tempuri.org/", partName = "inUri") String inUri) {
      return inUri;
   }

   @WebMethod(operationName = "RetDateTime", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetDateTime")
   @WebResult(name = "RetDateTimeResult", targetNamespace = "http://tempuri.org/", partName = "RetDateTimeResult")
   public XMLGregorianCalendar retDateTime(@WebParam(name = "inDateTime", targetNamespace = "http://tempuri.org/", partName = "inDateTime") XMLGregorianCalendar inDateTime) {
      return inDateTime;
   }

   @WebMethod(operationName = "RetTimeSpan", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetTimeSpan")
   @WebResult(name = "RetTimeSpanResult", targetNamespace = "http://tempuri.org/", partName = "RetTimeSpanResult")
   public Duration retTimeSpan(@WebParam(name = "inTimeSpan", targetNamespace = "http://tempuri.org/", partName = "inTimeSpan") Duration inTimeSpan) {
      return inTimeSpan;
   }

   @WebMethod(operationName = "RetQName", action = "http://tempuri.org/IBaseDataTypesDocLitB/RetQName")
   @WebResult(name = "RetQNameResult", targetNamespace = "http://tempuri.org/", partName = "RetQNameResult")
   public QName retQName(@WebParam(name = "inQName", targetNamespace = "http://tempuri.org/", partName = "inQName") QName inQName) {
      return inQName;  
   }
}
