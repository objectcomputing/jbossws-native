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
package org.jboss.test.ws.jaxrpc.jbws1410;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.Stub;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.ws.core.WSTimeoutException;
import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/** 
 * NumberFormatException From StubExt.PROPERTY_CLIENT_TIMEOUT
 *
 * http://jira.jboss.com/jira/browse/JBWS-1410
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Jan-2007
 */
public class JBWS1410TestCase extends JBossWSTest
{
   private static TestEndpoint port;
   
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1410TestCase.class, "jaxrpc-jbws1410.war");
   }

   public void setUp() throws Exception
   {
      if (port == null)
      {
         ServiceFactoryImpl factory = (ServiceFactoryImpl)ServiceFactory.newInstance();
         URL wsdlURL = getResourceURL("jaxrpc/jbws1410/WEB-INF/wsdl/TestService.wsdl");
         URL mappingURL = getResourceURL("jaxrpc/jbws1410/WEB-INF/jaxrpc-mapping.xml");
         QName serviceName = new QName("http://org.jboss.test.ws/jbws1410", "TestService");
         Service service = factory.createService(wsdlURL, serviceName, mappingURL);
         port = (TestEndpoint)service.getPort(TestEndpoint.class);
         
         ((Stub)port)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, "http://" + getServerHost() + ":8080/jaxrpc-jbws1410");
      }
   }
   
   public void testNoTimeout() throws Exception
   {
      String resStr = port.echoSimple("10");
      assertEquals("10", resStr);
   }
   
   public void testTimeout() throws Exception
   {
      ((Stub)port)._setProperty(StubExt.PROPERTY_CLIENT_TIMEOUT, "100");
      try
      {
         port.echoSimple("500");
         
         // Cannot set timeout (100) on http client transport as method not available with JDK 1.4 (only JDK 1.5 or higher)
         if (!"1.4".equals(System.getProperty("java.specification.version")))
            fail("WSTimeoutException expected");
      }
      catch (WSTimeoutException ex)
      {
         assertEquals(100, ex.getTimeout());
      }
   }
}
