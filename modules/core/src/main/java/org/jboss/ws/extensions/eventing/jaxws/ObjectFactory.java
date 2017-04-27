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
package org.jboss.ws.extensions.eventing.jaxws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.jboss.ws.extensions.eventing package. 
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

    private final static QName _Metadata_QNAME = new QName("http://www.w3.org/2005/08/addressing", "Metadata");
    private final static QName _ProblemHeader_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ProblemHeader");
    private final static QName _EndpointReference_QNAME = new QName("http://www.w3.org/2005/08/addressing", "EndpointReference");
    private final static QName _ProblemIRI_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ProblemIRI");
    private final static QName _FaultTo_QNAME = new QName("http://www.w3.org/2005/08/addressing", "FaultTo");
    private final static QName _Identifier_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing", "Identifier");
    private final static QName _SupportedDialect_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing", "SupportedDialect");
    private final static QName _MessageID_QNAME = new QName("http://www.w3.org/2005/08/addressing", "MessageID");
    private final static QName _RetryAfter_QNAME = new QName("http://www.w3.org/2005/08/addressing", "RetryAfter");
    private final static QName _RelatesTo_QNAME = new QName("http://www.w3.org/2005/08/addressing", "RelatesTo");
    private final static QName _ReplyTo_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ReplyTo");
    private final static QName _SupportedDeliveryMode_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing", "SupportedDeliveryMode");
    private final static QName _Action_QNAME = new QName("http://www.w3.org/2005/08/addressing", "Action");
    private final static QName _ProblemHeaderQName_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ProblemHeaderQName");
    private final static QName _To_QNAME = new QName("http://www.w3.org/2005/08/addressing", "To");
    private final static QName _NotifyTo_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing", "NotifyTo");
    private final static QName _ProblemAction_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ProblemAction");
    private final static QName _From_QNAME = new QName("http://www.w3.org/2005/08/addressing", "From");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jboss.ws.extensions.eventing
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ReferenceParametersType }
     * 
     */
    public ReferenceParametersType createReferenceParametersType() {
        return new ReferenceParametersType();
    }

    /**
     * Create an instance of {@link AttributedURIType }
     * 
     */
    public AttributedURIType createAttributedURIType() {
        return new AttributedURIType();
    }

    /**
     * Create an instance of {@link SubscribeResponse }
     * 
     */
    public SubscribeResponse createSubscribeResponse() {
        return new SubscribeResponse();
    }

    /**
     * Create an instance of {@link SubscriptionEnd }
     * 
     */
    public SubscriptionEnd createSubscriptionEnd() {
        return new SubscriptionEnd();
    }

    /**
     * Create an instance of {@link RelatesToType }
     * 
     */
    public RelatesToType createRelatesToType() {
        return new RelatesToType();
    }

    /**
     * Create an instance of {@link ProblemActionType }
     * 
     */
    public ProblemActionType createProblemActionType() {
        return new ProblemActionType();
    }

    /**
     * Create an instance of {@link Renew }
     * 
     */
    public Renew createRenew() {
        return new Renew();
    }

    /**
     * Create an instance of {@link LanguageSpecificStringType }
     * 
     */
    public LanguageSpecificStringType createLanguageSpecificStringType() {
        return new LanguageSpecificStringType();
    }

    /**
     * Create an instance of {@link GetStatus }
     * 
     */
    public GetStatus createGetStatus() {
        return new GetStatus();
    }

    /**
     * Create an instance of {@link AttributedUnsignedLongType }
     * 
     */
    public AttributedUnsignedLongType createAttributedUnsignedLongType() {
        return new AttributedUnsignedLongType();
    }

    /**
     * Create an instance of {@link AttributedAnyType }
     * 
     */
    public AttributedAnyType createAttributedAnyType() {
        return new AttributedAnyType();
    }

    /**
     * Create an instance of {@link Subscribe }
     * 
     */
    public Subscribe createSubscribe() {
        return new Subscribe();
    }

    /**
     * Create an instance of {@link EndpointReferenceType }
     * 
     */
    public EndpointReferenceType createEndpointReferenceType() {
        return new EndpointReferenceType();
    }

    /**
     * Create an instance of {@link FilterType }
     * 
     */
    public FilterType createFilterType() {
        return new FilterType();
    }

    /**
     * Create an instance of {@link MetadataType }
     * 
     */
    public MetadataType createMetadataType() {
        return new MetadataType();
    }

    /**
     * Create an instance of {@link AttributedQNameType }
     * 
     */
    public AttributedQNameType createAttributedQNameType() {
        return new AttributedQNameType();
    }

    /**
     * Create an instance of {@link DeliveryType }
     * 
     */
    public DeliveryType createDeliveryType() {
        return new DeliveryType();
    }

    /**
     * Create an instance of {@link RenewResponse }
     * 
     */
    public RenewResponse createRenewResponse() {
        return new RenewResponse();
    }

    /**
     * Create an instance of {@link Unsubscribe }
     * 
     */
    public Unsubscribe createUnsubscribe() {
        return new Unsubscribe();
    }

    /**
     * Create an instance of {@link GetStatusResponse }
     * 
     */
    public GetStatusResponse createGetStatusResponse() {
        return new GetStatusResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "Metadata")
    public JAXBElement<MetadataType> createMetadata(MetadataType value) {
        return new JAXBElement<MetadataType>(_Metadata_QNAME, MetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedAnyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "ProblemHeader")
    public JAXBElement<AttributedAnyType> createProblemHeader(AttributedAnyType value) {
        return new JAXBElement<AttributedAnyType>(_ProblemHeader_QNAME, AttributedAnyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndpointReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "EndpointReference")
    public JAXBElement<EndpointReferenceType> createEndpointReference(EndpointReferenceType value) {
        return new JAXBElement<EndpointReferenceType>(_EndpointReference_QNAME, EndpointReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedURIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "ProblemIRI")
    public JAXBElement<AttributedURIType> createProblemIRI(AttributedURIType value) {
        return new JAXBElement<AttributedURIType>(_ProblemIRI_QNAME, AttributedURIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndpointReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "FaultTo")
    public JAXBElement<EndpointReferenceType> createFaultTo(EndpointReferenceType value) {
        return new JAXBElement<EndpointReferenceType>(_FaultTo_QNAME, EndpointReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", name = "Identifier")
    public JAXBElement<String> createIdentifier(String value) {
        return new JAXBElement<String>(_Identifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", name = "SupportedDialect")
    public JAXBElement<String> createSupportedDialect(String value) {
        return new JAXBElement<String>(_SupportedDialect_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedURIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "MessageID")
    public JAXBElement<AttributedURIType> createMessageID(AttributedURIType value) {
        return new JAXBElement<AttributedURIType>(_MessageID_QNAME, AttributedURIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedUnsignedLongType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "RetryAfter")
    public JAXBElement<AttributedUnsignedLongType> createRetryAfter(AttributedUnsignedLongType value) {
        return new JAXBElement<AttributedUnsignedLongType>(_RetryAfter_QNAME, AttributedUnsignedLongType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelatesToType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "RelatesTo")
    public JAXBElement<RelatesToType> createRelatesTo(RelatesToType value) {
        return new JAXBElement<RelatesToType>(_RelatesTo_QNAME, RelatesToType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndpointReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "ReplyTo")
    public JAXBElement<EndpointReferenceType> createReplyTo(EndpointReferenceType value) {
        return new JAXBElement<EndpointReferenceType>(_ReplyTo_QNAME, EndpointReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", name = "SupportedDeliveryMode")
    public JAXBElement<String> createSupportedDeliveryMode(String value) {
        return new JAXBElement<String>(_SupportedDeliveryMode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedURIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "Action")
    public JAXBElement<AttributedURIType> createAction(AttributedURIType value) {
        return new JAXBElement<AttributedURIType>(_Action_QNAME, AttributedURIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedQNameType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "ProblemHeaderQName")
    public JAXBElement<AttributedQNameType> createProblemHeaderQName(AttributedQNameType value) {
        return new JAXBElement<AttributedQNameType>(_ProblemHeaderQName_QNAME, AttributedQNameType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedURIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "To")
    public JAXBElement<AttributedURIType> createTo(AttributedURIType value) {
        return new JAXBElement<AttributedURIType>(_To_QNAME, AttributedURIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndpointReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", name = "NotifyTo")
    public JAXBElement<EndpointReferenceType> createNotifyTo(EndpointReferenceType value) {
        return new JAXBElement<EndpointReferenceType>(_NotifyTo_QNAME, EndpointReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProblemActionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "ProblemAction")
    public JAXBElement<ProblemActionType> createProblemAction(ProblemActionType value) {
        return new JAXBElement<ProblemActionType>(_ProblemAction_QNAME, ProblemActionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndpointReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/08/addressing", name = "From")
    public JAXBElement<EndpointReferenceType> createFrom(EndpointReferenceType value) {
        return new JAXBElement<EndpointReferenceType>(_From_QNAME, EndpointReferenceType.class, null, value);
    }

}
