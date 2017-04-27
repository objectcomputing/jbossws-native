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
package org.jboss.test.ws.jaxws.jbws1909;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * [JBWS-1909] RequestHandler.handlerWSDLResquest has dependency on Servlet API
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1909
 *
 * @author Thomas.Diesler@jboss.com
 * @since 25-Feb-2007
 */
public class JBWS1909TestCase extends JBossWSTest
{
   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS1909TestCase.class, "jaxws-jbws1909.jar");
   }

   public void testWSDLSchema() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxws-jbws1909/TestEndpointImpl?wsdl");
      QName serviceName = new QName("http://org.jboss.ws/jbws1909", "TestEndpointService");
      Service service = Service.create(wsdlURL, serviceName);
      TestEndpoint port = service.getPort(TestEndpoint.class);
      String retStr = port.echo("hello");
      assertEquals("hello|TestEndpointService", retStr);
   }
}
