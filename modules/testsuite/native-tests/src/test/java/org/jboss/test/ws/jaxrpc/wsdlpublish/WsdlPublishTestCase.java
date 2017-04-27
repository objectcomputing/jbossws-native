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
package org.jboss.test.ws.jaxrpc.wsdlpublish;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/** 
 * Test <wsdl-publish-location>
 *
 * @author Thomas.Diesler@jboss.org
 * @since 31-Jan-2007
 */
public class WsdlPublishTestCase extends JBossWSTest
{
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(WsdlPublishTestCase.class, "jaxrpc-wsdlpublish.war");
   }

   public void testClientProxy() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxrpc-wsdlpublish?wsdl");
      URL mappingURL = getResourceURL("jaxrpc/wsdlpublish/WEB-INF/jaxrpc-mapping.xml");
      QName serviceName = new QName("http://org.jboss.test.ws/wsdlpublish", "TestService");
      Service service = new ServiceFactoryImpl().createService(wsdlURL, serviceName, mappingURL);
      
      TestEndpoint port = (TestEndpoint)service.getPort(TestEndpoint.class);
      
      String dir = System.getProperty("wsdl.publish.location");
      String resStr = port.echoSimple(new File(dir + "/some-wsdl-location/foo/bar/TestService.wsdl").getAbsolutePath());
      assertEquals("{http://org.jboss.test.ws/wsdlpublish}TestEndpoint", resStr);
   }
}
