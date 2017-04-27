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
package org.jboss.test.ws.jaxws.samples.wssecuritypolicy;

import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test WS-Security with RPC/Literal
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class SimpleEncryptTestCase extends JBossWSTest
{
   /** Construct the test case with a given name
    */

   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(SimpleEncryptTestCase.class, "jaxws-samples-wssecuritypolicy-encrypt.war");
   }

   /**
    * Test JSE endpoint
    */
   public void testEndpoint() throws Exception
   {
      Hello hello = getPort();

      UserType in0 = new UserType();
      in0.setMsg("Kermit");
      UserType retObj = hello.echoUserType(in0);
      assertEquals("Kermit", retObj.getMsg());
   }

   private Hello getPort() throws Exception
   {
      URL wsdlURL = getResourceURL("jaxws/samples/wssecuritypolicy/WEB-INF/wsdl/HelloService.wsdl");
      QName serviceName = new QName("http://org.jboss.ws/samples/wssecuritypolicy", "HelloService");

      Service service = Service.create(wsdlURL, serviceName);
      
      Hello port = (Hello)service.getPort(Hello.class);

      Map<String, Object> reqContext = ((BindingProvider)port).getRequestContext();
      reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://" + getServerHost() + ":8080/jaxws-samples-wssecuritypolicy-encrypt");

      return port;
   }
}
