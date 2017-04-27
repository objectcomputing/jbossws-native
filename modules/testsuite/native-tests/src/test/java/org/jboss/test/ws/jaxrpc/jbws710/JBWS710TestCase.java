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
package org.jboss.test.ws.jaxrpc.jbws710;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Document/Literal webservices that are oneway
 *
 * http://jira.jboss.com/jira/browse/JBWS-710
 *
 * @author Thomas.Diesler@jboss.org
 * @since 15-Feb-2006
 */
public class JBWS710TestCase extends JBossWSTest
{
   private static Hello port;

   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS710TestCase.class, "jaxrpc-jbws710.war, jaxrpc-jbws710-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
         port = (Hello)service.getPort(Hello.class);
      }
   }

   public void testOnewayRequest() throws Exception
   {
      port.onewayRequest("str1", "str2", "str3");

      String retObj = null;
      for (int i = 0; retObj == null && i < 5; i++)
         retObj = port.onewayResponse();

      assertEquals("str1str2str3", retObj);
   }
}
