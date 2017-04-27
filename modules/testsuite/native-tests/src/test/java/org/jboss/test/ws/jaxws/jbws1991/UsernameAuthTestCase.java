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
package org.jboss.test.ws.jaxws.jbws1991;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * http://jira.jboss.org/jira/browse/JBWS-1991
 *
 * @author alessio.soldano@jboss.com
 * @since 26-02-2008
 */
public class UsernameAuthTestCase extends JBossWSTest
{
   private String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxws-jbws1991";

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(UsernameAuthTestCase.class, "jaxws-jbws1991.jar");
   }
   
   public void testAuth() throws Exception
   {
      Hello port = getPort();
      ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "kermit");
      ((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "thefrog");
      String msg = "Hi!";
      try
      {
         String result = port.echo(msg);
         assertEquals(msg, result);
      }
      catch (Exception e)
      {
         fail();
      }
   }
   
   public void testWrongPasswordAuth() throws Exception
   {
      Hello port = getPort();
      ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "username");
      ((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "password");
      String msg = "Hi!";
      try
      {
         String result = port.echo(msg);
         fail();
      }
      catch (Exception e)
      {
         //OK
      }
   }

   private Hello getPort() throws Exception
   {
      URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");
      QName serviceName = new QName("http://org.jboss.ws/jbws1991", "HelloService");
      Hello port = Service.create(wsdlURL, serviceName).getPort(Hello.class);
      URL securityURL = getResourceURL("jaxws/jbws1991/META-INF/jboss-wsse-client.xml");
      ((StubExt)port).setSecurityConfig(securityURL.toExternalForm());
      ((StubExt)port).setConfigName("Standard WSSecurity Client");
      ((BindingProvider)port).getRequestContext().put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
      return port;
   }
}
