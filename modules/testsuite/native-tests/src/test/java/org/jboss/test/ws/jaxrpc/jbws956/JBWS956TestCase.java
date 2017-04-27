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
package org.jboss.test.ws.jaxrpc.jbws956;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

import junit.framework.Test;

import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Deserialization of complex bean containing an array of complex beans
 * 
 * http://jira.jboss.org/jira/browse/JBWS-956
 *
 * @author Thomas.Diesler@jboss.org
 * @since 30-May-2006
 */
public class JBWS956TestCase extends JBossWSTest
{
   private static TestEndpoint port;

   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS956TestCase.class, "jaxrpc-jbws956.war");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         ServiceFactoryImpl factory = (ServiceFactoryImpl)ServiceFactory.newInstance();
         URL wsdlURL = getResourceURL("jaxrpc/jbws956/WEB-INF/wsdl/TestService.wsdl");
         URL mappingURL = getResourceURL("jaxrpc/jbws956/WEB-INF/jaxrpc-mapping.xml");
         QName serviceName = new QName("http://org.jboss.test.ws/jbws956", "TestService");
         Service service = factory.createService(wsdlURL, serviceName, mappingURL);
         port = (TestEndpoint)service.getPort(TestEndpoint.class);
      }
   }

   public void testWebService() throws Exception
   {
      StringArray arr1 = new StringArray(new String[]{"str1", "str2"});
      StringArray arr2 = new StringArray(new String[]{"str3", "str4"});
      StringArrayArray arr3 = new StringArrayArray(new StringArray[]{arr1, arr2});
      String retStr = port.echo(arr1, arr3);
      assertEquals("[str1, str2][[str1, str2], [str3, str4]]", retStr);
   }
}
