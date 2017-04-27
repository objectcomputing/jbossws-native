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
package org.jboss.test.ws.jaxws.wsdd;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;
import java.net.URL;

/**
 * @author Heiko.Braun@jboss.org
 * @since Mar 12, 2007
 */
public class TestDDOverrides extends JBossWSTest {

   public final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxws-wsdd";

   public static Test suite()
   {
      return new JBossWSTestSetup(TestDDOverrides.class, "jaxws-wsdd.war");
   }

   public void testHandlerChainOverride() throws Exception
   {
      QName serviceName = new QName("http://wsdd.jaxws.ws.test.jboss.org/", "WSDDEndpointImplService");
      URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");

      Service service = Service.create(wsdlURL, serviceName);
      WSDDEndpoint port = service.getPort(WSDDEndpoint.class);

      Message request = new Message("Hello");
      ResponseMessage response = port.echo(request);
      assertNotNull(response);
      assertEquals(response.msg, "HelloWorld");
   }

    public void testMTOMOverride() throws Exception
   {
      QName serviceName = new QName("http://wsdd.jaxws.ws.test.jboss.org/", "WSDDEndpointImplService");
      URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");

      Service service = Service.create(wsdlURL, serviceName);
      WSDDEndpoint port = service.getPort(WSDDEndpoint.class);

      assertTrue("MTOM should be enabled thorugh webservice.xml overrides", port.checkMTOMEnabled());

   }
}
