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
package org.jboss.test.ws.jaxws.jbws2187;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * [JBWS-2187] Handler Chain Management Prevents Service Re-Use
 * 
 * @author darran.lofthouse@jboss.com
 * @since 18th July 2008
 * @see https://jira.jboss.org/jira/browse/JBWS-2187
 */
public class JBWS2187TestCase extends JBossWSTest
{

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS2187TestCase.class, "jaxws-jbws2187.war");
   }

   public void testSetBothPorts() throws Exception
   {
      Service service = getService();
      TestEndpoint original = service.getPort(TestEndpoint.class);

      setConfigName(original);
      performTest(original, 1);

      TestEndpoint subsequent = service.getPort(TestEndpoint.class);

      setConfigName(subsequent);
      performTest(subsequent, 1);

      performTest(original, 1);
   }

   public void testSetFirstPort() throws Exception
   {
      Service service = getService();
      TestEndpoint original = service.getPort(TestEndpoint.class);

      setConfigName(original);
      performTest(original, 1);

      TestEndpoint subsequent = service.getPort(TestEndpoint.class);

      //setConfigName(subsequent);
      performTest(subsequent, 0);

      performTest(original, 1);
   }

   public void testSetSecondPort() throws Exception
   {
      Service service = getService();
      TestEndpoint original = service.getPort(TestEndpoint.class);

      //setConfigName(original);
      performTest(original, 0);

      TestEndpoint subsequent = service.getPort(TestEndpoint.class);

      setConfigName(subsequent);
      performTest(subsequent, 1);

      performTest(original, 0);
   }

   void performTest(TestEndpoint port, int expected) throws Exception
   {
      TestHandler.clear();
      assertEquals("Av it", port.echo("Av it"));
      assertEquals("Call Count", expected, TestHandler.getCallCount());
   }

   void setConfigName(TestEndpoint port)
   {
      File config = getResourceFile("jaxws/jbws2187/META-INF/jbws2187-client-config.xml");
      ((StubExt)port).setConfigName("JBWS2187 Config", config.getAbsolutePath());
   }

   Service getService() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxws-jbws2187?wsdl");
      QName serviceName = new QName("http://ws.jboss.org/jbws2187", "TestService");

      Service service = Service.create(wsdlURL, serviceName);

      return service;
   }
}
