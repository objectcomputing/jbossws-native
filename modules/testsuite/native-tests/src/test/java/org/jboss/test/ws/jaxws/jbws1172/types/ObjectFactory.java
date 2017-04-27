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
package org.jboss.test.ws.jaxws.jbws1172.types;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.jboss.test.ws.jaxws.jbws1172.types package. 
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

    private final static QName _PerformTest_QNAME = new QName("http://www.my-company.it/ws/my-test", "performTest");
    private final static QName _MyWSException_QNAME = new QName("http://www.my-company.it/ws/my-test", "MyWSException");
    private final static QName _PerformTestResponse_QNAME = new QName("http://www.my-company.it/ws/my-test", "performTestResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jboss.test.ws.jaxws.jbws1172.types
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MyWSException }
     * 
     */
    public MyWSException createMyWSException() {
        return new MyWSException();
    }

    /**
     * Create an instance of {@link PerformTest }
     * 
     */
    public PerformTest createPerformTest() {
        return new PerformTest();
    }

    /**
     * Create an instance of {@link PerformTestResponse }
     * 
     */
    public PerformTestResponse createPerformTestResponse() {
        return new PerformTestResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformTest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.my-company.it/ws/my-test", name = "performTest")
    public JAXBElement<PerformTest> createPerformTest(PerformTest value) {
        return new JAXBElement<PerformTest>(_PerformTest_QNAME, PerformTest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MyWSException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.my-company.it/ws/my-test", name = "MyWSException")
    public JAXBElement<MyWSException> createMyWSException(MyWSException value) {
        return new JAXBElement<MyWSException>(_MyWSException_QNAME, MyWSException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformTestResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.my-company.it/ws/my-test", name = "performTestResponse")
    public JAXBElement<PerformTestResponse> createPerformTestResponse(PerformTestResponse value) {
        return new JAXBElement<PerformTestResponse>(_PerformTestResponse_QNAME, PerformTestResponse.class, null, value);
    }

}
