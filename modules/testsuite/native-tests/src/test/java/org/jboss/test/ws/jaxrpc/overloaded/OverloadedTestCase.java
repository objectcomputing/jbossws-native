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
package org.jboss.test.ws.jaxrpc.overloaded;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * A test that tests overloaded methods.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class OverloadedTestCase extends JBossWSTest
{
   private static Overloaded endpoint;

   public static Test suite()
   {
      return new JBossWSTestSetup(OverloadedTestCase.class, "jaxrpc-overloaded.war, jaxrpc-overloaded-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      if (endpoint == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/OverloadedTestService");
         endpoint = (Overloaded)service.getPort(Overloaded.class);
      }
   }

   public void testEcho1() throws Exception
   {
      String hello = "Hello";
      String world = "world!";

      Object retObj = endpoint.echo(hello, world);
      assertEquals(hello + world, retObj);
   }

   public void testEcho2() throws Exception
   {
      String hello = "Hello";
      String world = "world!";
      String extra = " take 2!";

      Object retObj = endpoint.echo(hello, world, extra);
      assertEquals(hello + world + extra, retObj);
   }

   public void testEcho3() throws Exception
   {
      String hello = "Hello";
      int num = 12;

      Object retObj = endpoint.echo(hello, num);
      assertEquals(hello + num, retObj);
   }
}
