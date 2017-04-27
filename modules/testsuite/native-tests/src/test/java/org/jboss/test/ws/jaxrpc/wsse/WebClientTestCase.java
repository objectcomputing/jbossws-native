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
package org.jboss.test.ws.jaxrpc.wsse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test WS-Security with RPC/Literal from a web client.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class WebClientTestCase extends JBossWSTest
{
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(WebClientTestCase.class, "jaxrpc-wsse-web-client.war");
   }

   /**
    * Test servlet client access
    */
   public void testWebClient() throws Exception
   {
      URL url = new URL("http://" + getServerHost() + ":8080/jaxrpc-wsse-rpc/RpcTestClientServlet?input=Hello");
      BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
      String res = br.readLine();
      assertEquals("Hello", res);
   }
}
