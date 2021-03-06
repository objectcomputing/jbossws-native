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
package org.jboss.test.ws.jaxws.samples.wsrm.generated;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1-b03-
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "SimpleService", targetNamespace = "http://www.jboss.org/jbossws/ws-extensions/wsrm", wsdlLocation = "file:/home/opalka/TODO/TUTORIAL/native/step1/wsprovide/generated/wsdl/SimpleService.wsdl")
public class SimpleService_Service
    extends Service
{

    private final static URL SIMPLESERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("file:/home/opalka/TODO/TUTORIAL/native/step1/wsprovide/generated/wsdl/SimpleService.wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SIMPLESERVICE_WSDL_LOCATION = url;
    }

    public SimpleService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SimpleService_Service() {
        super(SIMPLESERVICE_WSDL_LOCATION, new QName("http://www.jboss.org/jbossws/ws-extensions/wsrm", "SimpleService"));
    }

    /**
     * 
     * @return
     *     returns SimpleService
     */
    @WebEndpoint(name = "SimpleServicePort")
    public SimpleService getSimpleServicePort() {
        return (SimpleService)super.getPort(new QName("http://www.jboss.org/jbossws/ws-extensions/wsrm", "SimpleServicePort"), SimpleService.class);
    }

}
