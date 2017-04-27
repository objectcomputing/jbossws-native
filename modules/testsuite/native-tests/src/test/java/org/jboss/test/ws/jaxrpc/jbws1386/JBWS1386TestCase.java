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
package org.jboss.test.ws.jaxrpc.jbws1386;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.Stub;

import junit.framework.Test;

import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * [JBWS-1386] - ComplexType with base64Binary property
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1386
 *
 * @author Thomas.Diesler@jboss.com
 * @since 09-Jan-2007
 */
public class JBWS1386TestCase extends JBossWSTest
{
   private static RequestService port;
   
   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS1386TestCase.class, "jaxrpc-jbws1386.war");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         ServiceFactoryImpl factory = (ServiceFactoryImpl)ServiceFactory.newInstance();
         URL wsdlURL = getResourceURL("jaxrpc/jbws1386/WEB-INF/wsdl/TestService.wsdl");
         URL mappingURL = getResourceURL("jaxrpc/jbws1386/WEB-INF/jaxrpc-mapping.xml");
         QName serviceName = new QName("http://org.jboss.test.ws/jbws1386", "TestService");
         Service service = factory.createService(wsdlURL, serviceName , mappingURL);
         port = (RequestService)service.getPort(RequestService.class);
         ((Stub)port)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, "http://" + getServerHost() + ":8080/jaxrpc-jbws1386");
      }
   }

   public final void testEndpointAccess() throws Exception
   {
      Message inObj = new Message("Kermit", "base64".getBytes());
      Message retObj = port.processClaim(inObj);
      assertEquals(inObj, retObj);
   }
}
