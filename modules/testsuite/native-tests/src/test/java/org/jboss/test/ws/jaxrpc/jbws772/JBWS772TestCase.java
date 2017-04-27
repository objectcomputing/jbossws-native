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
package org.jboss.test.ws.jaxrpc.jbws772;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/**
 * Web services deployment can fail when deploying multiple EJB JARs
 * 
 * http://jira.jboss.com/jira/browse/JBWS-772
 *
 * @author Thomas.Diesler@jboss.org
 * @since 24-Mar-2006
 */
public class JBWS772TestCase extends JBossWSTest
{
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS772TestCase.class, "jaxrpc-jbws772.ear");
   }

   public void testEndpointAccess() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
      Hello port = (Hello)service.getPort(Hello.class);
      
      String resStr = port.sayHello("Hello");
      assertEquals("'Hello' to you too!", resStr);
   }
   
   public void testRemoteAccess() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      HelloHome home = (HelloHome)iniCtx.lookup("java:comp/env/ejb/HelloEJBTwo");
      HelloRemote remote = home.create();
      
      String resStr = remote.sayHello("Hello");
      assertEquals("'Hello' to you too!", resStr);
   }
}
