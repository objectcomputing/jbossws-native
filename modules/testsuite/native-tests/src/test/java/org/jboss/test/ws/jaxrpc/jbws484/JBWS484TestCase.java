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
package org.jboss.test.ws.jaxrpc.jbws484;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/**
 * Parameter name cannot start from a single lower case letter.
 * 
 * http://jira.jboss.com/jira/browse/JBWS-484
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 05-Dec-2005
 */
public class JBWS484TestCase extends JBossWSTest
{
   private static TestService_PortType endpoint;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS484TestCase.class, "jaxrpc-jbws484.war, jaxrpc-jbws484-client.jar");
   }
   
   public void setUp() throws Exception
   {
      super.setUp();
      if (endpoint == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         endpoint = (TestService_PortType)service.getPort(TestService_PortType.class);
      }
   }
   
   public void testValidAccess() throws Exception
   {  
      int retObj = endpoint.testMethod("Hello", "Server");
      assertEquals("HelloServer".length(), retObj); 
   }
}
