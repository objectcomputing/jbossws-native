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
package org.jboss.test.ws.jaxws.webserviceref;


import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0-b26-ea3
 * Generated source version: 2.0
 *
 */
@WebServiceClient(name = "TestEndpointService", targetNamespace = "http://org.jboss.ws/wsref", wsdlLocation = "file://bogus-location/jaxws-webserviceref?wsdl")
public class TestEndpointService
    extends Service
{

    private final static URL WSDL_LOCATION;
    private final static QName TESTENDPOINTSERVICE = new QName("http://org.jboss.ws/wsref", "TestEndpointService");
    private final static QName TESTENDPOINTPORT = new QName("http://org.jboss.ws/wsref", "TestEndpointPort");

    static {
        URL url = null;
        try {
            url = new URL("file://bogus-location/jaxws-webserviceref?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public TestEndpointService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public TestEndpointService() {
        super(org.jboss.test.ws.jaxws.webserviceref.TestEndpointService.WSDL_LOCATION, org.jboss.test.ws.jaxws.webserviceref.TestEndpointService.TESTENDPOINTSERVICE);
    }

    /**
     *
     * @return
     *     returns TestEndpoint
     */
    @WebEndpoint(name = "TestEndpointPort")
    public org.jboss.test.ws.jaxws.webserviceref.TestEndpoint getTestEndpointPort() {
        return (TestEndpoint)super.getPort(org.jboss.test.ws.jaxws.webserviceref.TestEndpointService.TESTENDPOINTPORT, TestEndpoint.class);
    }

}
