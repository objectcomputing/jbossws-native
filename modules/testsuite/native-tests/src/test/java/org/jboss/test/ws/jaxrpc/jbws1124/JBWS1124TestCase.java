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
package org.jboss.test.ws.jaxrpc.jbws1124;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Scoped class loading domains for WS endpoints
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1124
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 07-August-2006
 */
public class JBWS1124TestCase extends JBossWSTest
{
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1124TestCase.class, "jaxrpc-jbws1124one.war, jaxrpc-jbws1124two.war");
   }

   public void testEnpointOne() throws Exception
   {
      ServiceFactoryImpl factory = new ServiceFactoryImpl();
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jbws1124one/TestEndpoint?wsdl");
      URL mappingURL = getResourceURL("jaxrpc/jbws1124/WEB-INF/jaxrpc-mapping.xml");
      QName qname = new QName("http://org.jboss.test.ws/jbws1124", "TestService");
      Service service = factory.createService(wsdlURL, qname, mappingURL);
      TestEndpoint port = (TestEndpoint)service.getPort(TestEndpoint.class);
      assertEquals("jbws1124one", port.getResourceString());
   }


   public void testEnpointTwo() throws Exception
   {
      ServiceFactoryImpl factory = new ServiceFactoryImpl();
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jbws1124two/TestEndpoint?wsdl");
      URL mappingURL = getResourceURL("jaxrpc/jbws1124/WEB-INF/jaxrpc-mapping.xml");
      QName qname = new QName("http://org.jboss.test.ws/jbws1124", "TestService");
      Service service = factory.createService(wsdlURL, qname, mappingURL);
      TestEndpoint port = (TestEndpoint)service.getPort(TestEndpoint.class);
      assertEquals("jbws1124two", port.getResourceString());
   }

}
