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
package org.jboss.test.ws.jaxrpc.anonymous;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test anonymous types
 *
 * @author Thomas.Diesler@jboss.org
 * @since 30-Aug-2005
 */
public class AnonymousTypesTestCase extends JBossWSTest
{
   private static AnonymousTypesTestService endpoint;

   public static Test suite()
   {
      return new JBossWSTestSetup(AnonymousTypesTestCase.class, "jaxrpc-anonymous.war, jaxrpc-anonymous-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      if (endpoint == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         endpoint = (AnonymousTypesTestService)service.getPort(AnonymousTypesTestService.class);
      }
   }

   public void testElementTypeRoot() throws Exception
   {
      ElementTypeInside ins1 = new ElementTypeInside("ins1");
      ElementTypeInside ins2 = new ElementTypeInside("ins2");
      ElementTypeInside[] insArr = new ElementTypeInside[] { ins1, ins2 };
      ElementTypeRoot param = new ElementTypeRoot(insArr, new int[] { 1, 2, 3 });
      ElementTypeRoot res = endpoint.testElementTypeRoot(param);
      assertEquals(param, res);
   }

   public void testElementSomeOtherElement() throws Exception
   {
      int res = endpoint.testElementSomeOtherElement(100);
      assertEquals(100, res);
   }
}
