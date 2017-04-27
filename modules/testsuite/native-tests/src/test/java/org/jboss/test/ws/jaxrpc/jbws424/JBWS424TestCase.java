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
package org.jboss.test.ws.jaxrpc.jbws424;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * JBoss ignores metadata supplied in a JAX-RPC Mapping DD
 *
 * http://jira.jboss.com/jira/browse/JBWS-424
 *
 * @author Thomas.Diesler@jboss.org
 * @since 21-Oct-2005
 */
public class JBWS424TestCase extends JBossWSTest
{
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS424TestCase.class, "jaxrpc-jbws424.war, jaxrpc-jbws424-client.jar");
   }

   public void testEndpoint() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
      TestSEI port = (TestSEI)service.getPort(TestSEI.class);

      try
      {
         port.doStuff("A more appropiate test string");
         fail("Expected Exception2");
      }
      catch (Exception2 ex)
      {
         assertEquals("Some exception description", ex.getDescription());
         assertEquals("bang", ex.getShortDescr());
      }
   }

}
