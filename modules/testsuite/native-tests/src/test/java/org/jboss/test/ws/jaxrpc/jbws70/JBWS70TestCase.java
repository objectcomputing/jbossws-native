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
package org.jboss.test.ws.jaxrpc.jbws70;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;


/**
 *  It appears that web services methods that take no parameters is incompatible with document literal style.
 *
 * http://jira.jboss.com/jira/browse/JBWS-70
 *
 * @author Thomas.Diesler@jboss.org
 * @since 08-Feb-2005
 */
public class JBWS70TestCase extends JBossWSTest
{
   private static Hello hello;
   
   public JBWS70TestCase(String name)
   {
      super(name);
   }

   /** Deploy the test */
   public static Test suite() throws Exception
   {
      // JBAS-3609, the execution order of tests in this test case is important
      // so it must be defined explicitly when running under some JVMs
      TestSuite suite = new TestSuite();
      suite.addTest(new JBWS70TestCase("testSetVersion"));
      suite.addTest(new JBWS70TestCase("testGetVersion"));
      
      return new JBossWSTestSetup(suite, "jaxrpc-jbws70.war, jaxrpc-jbws70-client.jar");  
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
   
   public void testSetVersion() throws Exception
   {
      hello.setVersion("Version-1.0");
   }
   
   public void testGetVersion() throws Exception
   {
      assertEquals("Version-1.0", hello.getVersion());
   }
}
