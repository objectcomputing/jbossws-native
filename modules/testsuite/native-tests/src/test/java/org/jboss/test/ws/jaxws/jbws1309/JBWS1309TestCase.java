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
package org.jboss.test.ws.jaxws.jbws1309;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * http://jira.jboss.org/jira/browse/JBWS-1309
 * 
 * Testing WS-Security setting up the endpoint configuration
 * without using JBoss-specific annotations -> i.e. using jboss.xml descriptor
 *
 * @author alessio.soldano@jboss.com
 * @since 17-Mar-2009
 */
public class JBWS1309TestCase extends JBossWSTest
{
   private String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxws-jbws1309";

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1309TestCase.class, "jaxws-jbws1309-client.jar,jaxws-jbws1309.jar");
   }
   
   public void testAuth() throws Exception
   {
      EndpointInterface port = getPort();
      Dto dto = new Dto();
      dto.setPar1("first parameter");
      dto.setPar2("second parameter");
      try
      {
         Dto result = port.echo(dto);
         assertEquals(dto.getPar1(), result.getPar1());
         assertEquals(dto.getPar2(), result.getPar2());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   private EndpointInterface getPort() throws Exception
   {
      URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");
      QName serviceName = new QName("http://org.jboss.ws/jbws1309", "HelloService");
      EndpointInterface port = Service.create(wsdlURL, serviceName).getPort(EndpointInterface.class);
      URL securityURL = getResourceURL("jaxws/jbws1309/META-INF/jboss-wsse-client.xml");
      ((StubExt)port).setSecurityConfig(securityURL.toExternalForm());
      ((StubExt)port).setConfigName("Standard WSSecurity Client");
      return port;
   }
}
