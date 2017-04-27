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
package org.jboss.test.ws.interop.soapwsdl.basedoclitw;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
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
		endpointInterface = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.IBaseDataTypesDocLitW",
		serviceName = "BaseDataTypesDocLitWService",
		targetNamespace = "http://tempuri.org/"
)
public class ServiceImpl implements IBaseDataTypesDocLitW
{
   @WebMethod(operationName = "RetBool", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetBool")
   @WebResult(name = "RetBoolResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetBool", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetBool")
   @ResponseWrapper(localName = "RetBoolResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetBoolResponse")
   public Boolean retBool(@WebParam(name = "inBool", targetNamespace = "http://tempuri.org/") Boolean inBool) {
      return inBool;
   }

   @WebMethod(operationName = "RetByte", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetByte")
   @WebResult(name = "RetByteResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetByte", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetByte")
   @ResponseWrapper(localName = "RetByteResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetByteResponse")
   public Short retByte(@WebParam(name = "inByte", targetNamespace = "http://tempuri.org/") Short inByte) {
      return inByte;
   }

   @WebMethod(operationName = "RetSByte", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetSByte")
   @WebResult(name = "RetSByteResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetSByte", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetSByte")
   @ResponseWrapper(localName = "RetSByteResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetSByteResponse")
   public Byte retSByte(@WebParam(name = "inSByte", targetNamespace = "http://tempuri.org/") Byte inSByte) {
      return inSByte;
   }

   @WebMethod(operationName = "RetByteArray", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetByteArray")
   @WebResult(name = "RetByteArrayResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetByteArray", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetByteArray")
   @ResponseWrapper(localName = "RetByteArrayResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetByteArrayResponse")
   public byte[] retByteArray(@WebParam(name = "inByteArray", targetNamespace = "http://tempuri.org/") byte[] inByteArray) {
      return inByteArray;
   }

   @WebMethod(operationName = "RetChar", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetChar")
   @WebResult(name = "RetCharResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetChar", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetChar")
   @ResponseWrapper(localName = "RetCharResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetCharResponse")
   public Integer retChar(@WebParam(name = "inChar", targetNamespace = "http://tempuri.org/") Integer inChar) {
      return inChar;
   }

   @WebMethod(operationName = "RetDecimal", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetDecimal")
   @WebResult(name = "RetDecimalResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetDecimal", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetDecimal")
   @ResponseWrapper(localName = "RetDecimalResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetDecimalResponse")
   public BigDecimal retDecimal(@WebParam(name = "inDecimal", targetNamespace = "http://tempuri.org/") BigDecimal inDecimal) {
      return inDecimal;
   }

   @WebMethod(operationName = "RetFloat", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetFloat")
   @WebResult(name = "RetFloatResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetFloat", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetFloat")
   @ResponseWrapper(localName = "RetFloatResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetFloatResponse")
   public Float retFloat(@WebParam(name = "inFloat", targetNamespace = "http://tempuri.org/") Float inFloat) {
      return inFloat;
   }

   @WebMethod(operationName = "RetDouble", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetDouble")
   @WebResult(name = "RetDoubleResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetDouble", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetDouble")
   @ResponseWrapper(localName = "RetDoubleResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetDoubleResponse")
   public Double retDouble(@WebParam(name = "inDouble", targetNamespace = "http://tempuri.org/") Double inDouble) {
      return inDouble;
   }

   @WebMethod(operationName = "RetSingle", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetSingle")
   @WebResult(name = "RetSingleResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetSingle", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetSingle")
   @ResponseWrapper(localName = "RetSingleResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetSingleResponse")
   public Float retSingle(@WebParam(name = "inSingle", targetNamespace = "http://tempuri.org/") Float inSingle) {
      return inSingle;
   }

   @WebMethod(operationName = "RetInt", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetInt")
   @WebResult(name = "RetIntResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetInt", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetInt")
   @ResponseWrapper(localName = "RetIntResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetIntResponse")
   public Integer retInt(@WebParam(name = "inInt", targetNamespace = "http://tempuri.org/") Integer inInt) {
      return inInt;
   }

   @WebMethod(operationName = "RetShort", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetShort")
   @WebResult(name = "RetShortResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetShort", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetShort")
   @ResponseWrapper(localName = "RetShortResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetShortResponse")
   public Short retShort(@WebParam(name = "inShort", targetNamespace = "http://tempuri.org/") Short inShort) {
      return inShort;
   }

   @WebMethod(operationName = "RetLong", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetLong")
   @WebResult(name = "RetLongResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetLong", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetLong")
   @ResponseWrapper(localName = "RetLongResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetLongResponse")
   public Long retLong(@WebParam(name = "inLong", targetNamespace = "http://tempuri.org/") Long inLong) {
      return inLong;
   }

   @WebMethod(operationName = "RetObject", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetObject")
   @WebResult(name = "RetObjectResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetObject", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetObject")
   @ResponseWrapper(localName = "RetObjectResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetObjectResponse")
   public Object retObject(@WebParam(name = "inObject", targetNamespace = "http://tempuri.org/") Object inObject) {
      return inObject;
   }

   @WebMethod(operationName = "RetUInt", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetUInt")
   @WebResult(name = "RetUIntResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetUInt", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetUInt")
   @ResponseWrapper(localName = "RetUIntResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetUIntResponse")
   public Long retUInt(@WebParam(name = "inUInt", targetNamespace = "http://tempuri.org/") Long inUInt) {
      return inUInt;
   }

   @WebMethod(operationName = "RetUShort", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetUShort")
   @WebResult(name = "RetUShortResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetUShort", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetUShort")
   @ResponseWrapper(localName = "RetUShortResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetUShortResponse")
   public Integer retUShort(@WebParam(name = "inUShort", targetNamespace = "http://tempuri.org/") Integer inUShort) {
      return inUShort;
   }

   @WebMethod(operationName = "RetULong", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetULong")
   @WebResult(name = "RetULongResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetULong", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetULong")
   @ResponseWrapper(localName = "RetULongResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetULongResponse")
   public BigInteger retULong(@WebParam(name = "inULong", targetNamespace = "http://tempuri.org/") BigInteger inULong) {
      return inULong;
   }

   @WebMethod(operationName = "RetString", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetString")
   @WebResult(name = "RetStringResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetString", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetString")
   @ResponseWrapper(localName = "RetStringResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetStringResponse")
   public String retString(@WebParam(name = "inString", targetNamespace = "http://tempuri.org/") String inString) {
      return inString;
   }

   @WebMethod(operationName = "RetGuid", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetGuid")
   @WebResult(name = "RetGuidResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetGuid", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetGuid")
   @ResponseWrapper(localName = "RetGuidResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetGuidResponse")
   public String retGuid(@WebParam(name = "inGuid", targetNamespace = "http://tempuri.org/") String inGuid) {
      return inGuid;
   }

   @WebMethod(operationName = "RetUri", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetUri")
   @WebResult(name = "RetUriResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetUri", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetUri")
   @ResponseWrapper(localName = "RetUriResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetUriResponse")
   public String retUri(@WebParam(name = "inUri", targetNamespace = "http://tempuri.org/") String inUri) {
      return inUri;
   }

   @WebMethod(operationName = "RetDateTime", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetDateTime")
   @WebResult(name = "RetDateTimeResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetDateTime", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetDateTime")
   @ResponseWrapper(localName = "RetDateTimeResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetDateTimeResponse")
   public XMLGregorianCalendar retDateTime(@WebParam(name = "inDateTime", targetNamespace = "http://tempuri.org/") XMLGregorianCalendar inDateTime) {
      return inDateTime;
   }

   @WebMethod(operationName = "RetTimeSpan", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetTimeSpan")
   @WebResult(name = "RetTimeSpanResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetTimeSpan", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetTimeSpan")
   @ResponseWrapper(localName = "RetTimeSpanResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetTimeSpanResponse")
   public Duration retTimeSpan(@WebParam(name = "inTimeSpan", targetNamespace = "http://tempuri.org/") Duration inTimeSpan) {
      return inTimeSpan;
   }

   @WebMethod(operationName = "RetQName", action = "http://tempuri.org/IBaseDataTypesDocLitW/RetQName")
   @WebResult(name = "RetQNameResult", targetNamespace = "http://tempuri.org/")
   @RequestWrapper(localName = "RetQName", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetQName")
   @ResponseWrapper(localName = "RetQNameResponse", targetNamespace = "http://tempuri.org/", className = "org.jboss.test.ws.interop.soapwsdl.basedoclitw.RetQNameResponse")
   public QName retQName(@WebParam(name = "inQName", targetNamespace = "http://tempuri.org/") QName inQName) {
      return inQName;  
   }
}
