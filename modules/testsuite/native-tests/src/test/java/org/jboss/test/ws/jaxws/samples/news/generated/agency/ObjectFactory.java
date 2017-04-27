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
package org.jboss.test.ws.jaxws.samples.news.generated.agency;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.jboss.test.ws.jaxws.samples.news.generated package. 
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

    private final static QName _SubmitPressRelease_QNAME = new QName("http://org.jboss.ws/samples/news", "submitPressRelease");
    private final static QName _SubmitPressReleaseResponse_QNAME = new QName("http://org.jboss.ws/samples/news", "submitPressReleaseResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jboss.test.ws.jaxws.samples.news.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SubmitPressReleaseResponse }
     * 
     */
    public SubmitPressReleaseResponse createSubmitPressReleaseResponse() {
        return new SubmitPressReleaseResponse();
    }

    /**
     * Create an instance of {@link PressRelease }
     * 
     */
    public PressRelease createPressRelease() {
        return new PressRelease();
    }

    /**
     * Create an instance of {@link SubmitPressRelease }
     * 
     */
    public SubmitPressRelease createSubmitPressRelease() {
        return new SubmitPressRelease();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitPressRelease }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.jboss.ws/samples/news", name = "submitPressRelease")
    public JAXBElement<SubmitPressRelease> createSubmitPressRelease(SubmitPressRelease value) {
        return new JAXBElement<SubmitPressRelease>(_SubmitPressRelease_QNAME, SubmitPressRelease.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitPressReleaseResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.jboss.ws/samples/news", name = "submitPressReleaseResponse")
    public JAXBElement<SubmitPressReleaseResponse> createSubmitPressReleaseResponse(SubmitPressReleaseResponse value) {
        return new JAXBElement<SubmitPressReleaseResponse>(_SubmitPressReleaseResponse_QNAME, SubmitPressReleaseResponse.class, null, value);
    }

}
