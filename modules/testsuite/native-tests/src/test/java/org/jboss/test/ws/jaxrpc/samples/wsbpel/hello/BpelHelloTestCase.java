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
package org.jboss.test.ws.jaxrpc.samples.wsbpel.hello;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.rpc.Call;

import junit.framework.Test;

import org.jboss.test.ws.jaxrpc.samples.wsbpel.JbpmBpelTestSetup;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test business process behavior based on web services.
 * 
 * @author <a href="mailto:alex.guizar@jboss.com">Alejandro Guizar</a>
 */
public class BpelHelloTestCase extends JBossWSTest
{
   private HelloWorldService helloService;

   public static Test suite()
   {
      JBossWSTestSetup wsTestSetup = new JBossWSTestSetup(BpelHelloTestCase.class, "jaxrpc-samples-wsbpel-hello.war, jaxrpc-samples-wsbpel-hello-client.jar");
      return new JbpmBpelTestSetup(wsTestSetup, new String[] { "jaxrpc-samples-wsbpel-hello-process.zip" });
   }

   protected void setUp() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      helloService = (HelloWorldService)iniCtx.lookup("java:comp/env/service/BpelHello");
   }

   public void testSayHelloProxy() throws Exception
   {
      Greeter proxy = helloService.getGreeterPort();

      String greeting = proxy.sayHello("Popeye");
      assertEquals("Hello, Popeye!", greeting);
   }

   public void testSayHelloDII() throws Exception
   {
      String helloServiceNS = "http://jbpm.org/examples/hello";
      Call call = helloService.createCall(new QName(helloServiceNS, "GreeterPort"));
      call.setOperationName(new QName(helloServiceNS, "sayHello"));

      String greeting = (String)call.invoke(new Object[] { "Olive" });
      assertEquals("Hello, Olive!", greeting);
   }
}
