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
 * Test WS-Security with RPC/Literal
 *
 * @author Thomas.Diesler@jboss.com
 */
public class SimpleSignTestCase extends JBossWSTest
{
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(SimpleSignTestCase.class, "jaxws-samples-wssecurity-sign.war");
   }

   /**
    * Test JSE endpoint
    */
   public void testEndpoint() throws Exception
   {
      Hello hello = getPort();
      performTest(hello, "Kermit");
   }
   
   private void performTest(Hello hello, String msg) throws Exception
   {
      UserType in0 = new UserType();
      in0.setMsg(msg);
      UserType retObj = hello.echoUserType(in0);
      assertEquals(msg, retObj.getMsg());
   }

   private Hello getPort() throws Exception
   {
      URL wsdlURL = getResourceURL("wsprovide/jaxws/samples/wssecurity/HelloService.wsdl");
      URL securityURL = getResourceURL("jaxws/samples/wssecurity/simple-sign/META-INF/jboss-wsse-client.xml");
      QName serviceName = new QName("http://org.jboss.ws/samples/wssecurity", "HelloService");

      Service service = Service.create(wsdlURL, serviceName);
      
      Hello port = (Hello)service.getPort(Hello.class);
      ((StubExt)port).setSecurityConfig(securityURL.toExternalForm());
      ((StubExt)port).setConfigName("Standard WSSecurity Client");

      Map<String, Object> reqContext = ((BindingProvider)port).getRequestContext();
      reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://" + getServerHost() + ":8080/jaxws-samples-wssecurity-sign");

      return port;
   }
}
