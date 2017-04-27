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
package org.jboss.test.ws.jaxws.samples.wssecurity;

import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test WS-Security for Username Token
 *
 * @author <a href="mailto:mageshbk@jboss.com">Magesh Kumar B</a>
 * @author Thomas.Diesler@jboss.com
 * @since 15-Aug-2007
 */
public class SimpleUsernameTestCase extends JBossWSTest
{
   private static UsernameEndpoint port;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(SimpleUsernameTestCase.class, "jaxws-samples-wssecurity-username.war");
   }

   @Override
   protected void setUp() throws Exception
   {
      if (port == null)
      {
         URL wsdlURL = getResourceURL("jaxws/samples/wssecurity/simple-username/META-INF/wsdl/UsernameService.wsdl");
         URL securityURL = getResourceURL("jaxws/samples/wssecurity/simple-username/META-INF/jboss-wsse-client.xml");
         QName serviceName = new QName("http://org.jboss.ws/samples/wssecurity", "UsernameService");

         Service service = Service.create(wsdlURL, serviceName);

         port = (UsernameEndpoint)service.getPort(UsernameEndpoint.class);
         ((StubExt)port).setSecurityConfig(securityURL.toExternalForm());
         ((StubExt)port).setConfigName("Standard WSSecurity Client");
      }
   }

   public void testUsernameTokenNegative() throws Exception
   {
      try
      {
         port.getUsernameToken();
         fail("Server should respond with [401] - Unauthorized");
      }
      catch (Exception ex)
      {
         // this should be ok
      }
   }

   public void testUsernameToken() throws Exception
   {
      Map<String, Object> reqContext = ((BindingProvider)port).getRequestContext();
      reqContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
      reqContext.put(BindingProvider.PASSWORD_PROPERTY, "thefrog");

      String retObj = port.getUsernameToken();
      assertEquals("kermit", retObj);
   }
}
