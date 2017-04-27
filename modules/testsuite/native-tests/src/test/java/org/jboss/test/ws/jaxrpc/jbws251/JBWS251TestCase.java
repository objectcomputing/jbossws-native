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
package org.jboss.test.ws.jaxrpc.jbws251;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/** 
 * SOAP fault inheritance
 *
 * http://jira.jboss.com/jira/browse/JBWS-251
 *
 * @author Thomas.Diesler@jboss.org
 * @since 06-Dec-2005
 */
public class JBWS251TestCase extends JBossWSTest
{
   private static Hello hello;

   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS251TestCase.class, "jaxrpc-jbws251.war, jaxrpc-jbws251-client.jar");
   }

   public void setUp() throws Exception
   {
      super.setUp();
      if (hello == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
         hello = (Hello)service.getPort(Hello.class);
      }
   }

   public void testNoException() throws Exception
   {
      String inStr = "Kermit";
      String retStr = hello.hello(inStr);
      assertEquals(inStr, retStr);
   }

   public void testUserExceptionOne() throws Exception
   {
      try
      {
         hello.hello("UserExceptionOne");
         fail("UserExceptionOne expected");
      }
      catch (UserExceptionOne ex)
      {
         // ignore expected exception
      }
   }

   public void testUserExceptionTwo() throws Exception
   {
      try
      {
         hello.hello("UserExceptionTwo");
         fail("UserExceptionTwo expected");
      }
      catch (UserExceptionTwo ex)
      {
         // ignore expected exception
      }
   }
}
