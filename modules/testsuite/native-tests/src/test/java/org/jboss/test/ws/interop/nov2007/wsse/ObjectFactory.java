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
package org.jboss.test.ws.interop.nov2007.wsse;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.jboss.test.ws.interop.nov2007.wsse package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _EchoXmlRequest_QNAME = new QName("http://InteropBaseAddress/interop", "request");
    private final static QName _EchoDataSetResponseEchoDataSetResult_QNAME = new QName("http://InteropBaseAddress/interop", "echoDataSetResult");
    private final static QName _EchoXmlResponseEchoXmlResult_QNAME = new QName("http://InteropBaseAddress/interop", "echoXmlResult");
    private final static QName _CustomHeader_QNAME = new QName("http://InteropBaseAddress/interop", "CustomHeader");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedInt");
    private final static QName _Double_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "decimal");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "boolean");
    private final static QName _String_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyType");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _DataSet_QNAME = new QName("", "DataSet");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "dateTime");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _PingResponse_QNAME = new QName("http://xmlsoap.org/Ping", "PingResponse");
    private final static QName _Ping_QNAME = new QName("http://xmlsoap.org/Ping", "Ping");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedByte");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedShort");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _AnyURI_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "duration");
    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedLong");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "base64Binary");
    private final static QName _EchoResponseEchoResult_QNAME = new QName("http://InteropBaseAddress/interop", "echoResult");
    private final static QName _FaultResponseFaultResult_QNAME = new QName("http://InteropBaseAddress/interop", "faultResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jboss.test.ws.interop.nov2007.wsse
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EchoXmlResponse }
     * 
     */
    public EchoXmlResponse createEchoXmlResponse() {
        return new EchoXmlResponse();
    }

    /**
     * Create an instance of {@link Echo }
     * 
     */
    public Echo createEcho() {
        return new Echo();
    }

    /**
     * Create an instance of {@link Fault }
     * 
     */
    public Fault createFault() {
        return new Fault();
    }

    /**
     * Create an instance of {@link DataSet }
     * 
     */
    public DataSet createDataSet() {
        return new DataSet();
    }

    /**
     * Create an instance of {@link EchoDataSet.Request }
     * 
     */
    public EchoDataSet.Request createEchoDataSetRequest() {
        return new EchoDataSet.Request();
    }

    /**
     * Create an instance of {@link Header }
     * 
     */
    public Header createHeader() {
        return new Header();
    }

    /**
     * Create an instance of {@link FaultResponse }
     * 
     */
    public FaultResponse createFaultResponse() {
        return new FaultResponse();
    }

    /**
     * Create an instance of {@link EchoXmlResponse.EchoXmlResult }
     * 
     */
    public EchoXmlResponse.EchoXmlResult createEchoXmlResponseEchoXmlResult() {
        return new EchoXmlResponse.EchoXmlResult();
    }

    /**
     * Create an instance of {@link EchoXml.Request }
     * 
     */
    public EchoXml.Request createEchoXmlRequest() {
        return new EchoXml.Request();
    }

    /**
     * Create an instance of {@link EchoXml }
     * 
     */
    public EchoXml createEchoXml() {
        return new EchoXml();
    }

    /**
     * Create an instance of {@link PingResponseBody }
     * 
     */
    public PingResponseBody createPingResponseBody() {
        return new PingResponseBody();
    }

    /**
     * Create an instance of {@link EchoDataSetResponse }
     * 
     */
    public EchoDataSetResponse createEchoDataSetResponse() {
        return new EchoDataSetResponse();
    }

    /**
     * Create an instance of {@link EchoDataSet }
     * 
     */
    public EchoDataSet createEchoDataSet() {
        return new EchoDataSet();
    }

    /**
     * Create an instance of {@link PingRequest }
     * 
     */
    public PingRequest createPingRequest() {
        return new PingRequest();
    }

    /**
     * Create an instance of {@link HeaderResponse }
     * 
     */
    public HeaderResponse createHeaderResponse() {
        return new HeaderResponse();
    }

    /**
     * Create an instance of {@link CustomHeader }
     * 
     */
    public CustomHeader createCustomHeader() {
        return new CustomHeader();
    }

    /**
     * Create an instance of {@link Ping }
     * 
     */
    public Ping createPing() {
        return new Ping();
    }

    /**
     * Create an instance of {@link EchoResponse }
     * 
     */
    public EchoResponse createEchoResponse() {
        return new EchoResponse();
    }

    /**
     * Create an instance of {@link EchoDataSetResponse.EchoDataSetResult }
     * 
     */
    public EchoDataSetResponse.EchoDataSetResult createEchoDataSetResponseEchoDataSetResult() {
        return new EchoDataSetResponse.EchoDataSetResult();
    }

    /**
     * Create an instance of {@link PingResponse }
     * 
     */
    public PingResponse createPingResponse() {
        return new PingResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EchoXml.Request }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://InteropBaseAddress/interop", name = "request", scope = EchoXml.class)
    public JAXBElement<EchoXml.Request> createEchoXmlRequest(EchoXml.Request value) {
        return new JAXBElement<EchoXml.Request>(_EchoXmlRequest_QNAME, EchoXml.Request.class, EchoXml.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EchoDataSetResponse.EchoDataSetResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://InteropBaseAddress/interop", name = "echoDataSetResult", scope = EchoDataSetResponse.class)
    public JAXBElement<EchoDataSetResponse.EchoDataSetResult> createEchoDataSetResponseEchoDataSetResult(EchoDataSetResponse.EchoDataSetResult value) {
        return new JAXBElement<EchoDataSetResponse.EchoDataSetResult>(_EchoDataSetResponseEchoDataSetResult_QNAME, EchoDataSetResponse.EchoDataSetResult.class, EchoDataSetResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EchoDataSet.Request }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://InteropBaseAddress/interop", name = "request", scope = EchoDataSet.class)
    public JAXBElement<EchoDataSet.Request> createEchoDataSetRequest(EchoDataSet.Request value) {
        return new JAXBElement<EchoDataSet.Request>(_EchoXmlRequest_QNAME, EchoDataSet.Request.class, EchoDataSet.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EchoXmlResponse.EchoXmlResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://InteropBaseAddress/interop", name = "echoXmlResult", scope = EchoXmlResponse.class)
    public JAXBElement<EchoXmlResponse.EchoXmlResult> createEchoXmlResponseEchoXmlResult(EchoXmlResponse.EchoXmlResult value) {
        return new JAXBElement<EchoXmlResponse.EchoXmlResult>(_EchoXmlResponseEchoXmlResult_QNAME, EchoXmlResponse.EchoXmlResult.class, EchoXmlResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://InteropBaseAddress/interop", name = "request", scope = Echo.class)
    public JAXBElement<String> createEchoRequest(String value) {
        return new JAXBElement<String>(_EchoXmlRequest_QNAME, String.class, Echo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CustomHeader }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://InteropBaseAddress/interop", name = "CustomHeader")
    public JAXBElement<CustomHeader> createCustomHeader(CustomHeader value) {
        return new JAXBElement<CustomHeader>(_CustomHeader_QNAME, CustomHeader.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataSet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DataSet")
    public JAXBElement<DataSet> createDataSet(DataSet value) {
        return new JAXBElement<DataSet>(_DataSet_QNAME, DataSet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PingResponseBody }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://xmlsoap.org/Ping", name = "PingResponse")
    public JAXBElement<PingResponseBody> createPingResponse(PingResponseBody value) {
        return new JAXBElement<PingResponseBody>(_PingResponse_QNAME, PingResponseBody.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://xmlsoap.org/Ping", name = "Ping")
    public JAXBElement<Ping> createPing(Ping value) {
        return new JAXBElement<Ping>(_Ping_QNAME, Ping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://InteropBaseAddress/interop", name = "request", scope = Fault.class)
    public JAXBElement<String> createFaultRequest(String value) {
        return new JAXBElement<String>(_EchoXmlRequest_QNAME, String.class, Fault.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://InteropBaseAddress/interop", name = "echoResult", scope = EchoResponse.class)
    public JAXBElement<String> createEchoResponseEchoResult(String value) {
        return new JAXBElement<String>(_EchoResponseEchoResult_QNAME, String.class, EchoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://InteropBaseAddress/interop", name = "faultResult", scope = FaultResponse.class)
    public JAXBElement<String> createFaultResponseFaultResult(String value) {
        return new JAXBElement<String>(_FaultResponseFaultResult_QNAME, String.class, FaultResponse.class, value);
    }

}
