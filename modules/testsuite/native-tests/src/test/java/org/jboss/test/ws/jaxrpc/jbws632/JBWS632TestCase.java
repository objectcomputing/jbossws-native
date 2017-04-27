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
package org.jboss.test.ws.jaxrpc.jbws632;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/**
 * String[][] deserialization error
 * 
 * http://jira.jboss.org/jira/browse/JBWS-632
 *
 * @author Thomas.Diesler@jboss.org
 * @since 21-Jan-2006
 */
public class JBWS632TestCase extends JBossWSTest
{
   private static TestSEI port;
   
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS632TestCase.class, "jaxrpc-jbws632.war, jaxrpc-jbws632-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (TestSEI)service.getPort(TestSEI.class);
      }
   }

   public void testStringArray() throws Exception
   {
      String[][] arr = new String[][]{new String[]{"a", "b"}, new String[]{"", null}, new String[]{"c", "d"}};
      String[][] retObj = port.echo(arr);
      assertEquals("a", retObj[0][0]);
      assertEquals("b", retObj[0][1]);
      assertEquals("", retObj[1][0]);
      assertEquals(null, retObj[1][1]);
      assertEquals("c", retObj[2][0]);
      assertEquals("d", retObj[2][1]);
   }
   
   public void testNullArray() throws Exception
   {
      String[][] retObj = port.echo(null);
      assertNull(retObj);
   }
   
   public void testEmptyArray() throws Exception
   {
      String[][] retObj = port.echo(new String[][]{});
      assertNotNull(retObj);
      assertEquals(0, retObj.length);
   }
}
