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
package org.jboss.test.ws.jaxws.jbws1850;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test wsdl documentation
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1850
 *
 * @author alessio.soldano@jboss.com
 * @since 15-Jan-2008
 */
public class JBWS1850TestCase extends JBossWSTest
{
   public final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxws-jbws1850";

   private static TestService port;

   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS1850TestCase.class, "jaxws-jbws1850.jar");
   }

   protected void setUp() throws Exception
   {
      if (port == null)
      {
         URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");
         QName serviceName = new QName("http://org.jboss.ws/jbws1850", "TestEndpointService");
         port = Service.create(wsdlURL, serviceName).getPort(TestService.class);
      }
   }

   public void testWsdl() throws Exception
   {
      URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlURL);
      WSDLService wsdlService = wsdlDefinitions.getServices()[0];
      WSDLEndpoint wsdlEndpoint = wsdlService.getEndpoints()[0];
      WSDLInterface wsdlInterface = wsdlEndpoint.getInterface();
      assertNotNull(wsdlInterface.getDocumentationElement());
      assertEquals("This is a test service doing nothing special", wsdlInterface.getDocumentationElement().getContent());
      WSDLInterfaceOperation wsdlOperation = wsdlInterface.getOperations()[0];
      assertNotNull(wsdlOperation.getDocumentationElement());
      assertEquals("This is the useless test operation of the test service", wsdlOperation.getDocumentationElement().getContent());
   }
   
   public void testInvocation()
   {
      String retObj = port.test("Hello", "World");
      assertEquals("World", retObj);

   }
}
