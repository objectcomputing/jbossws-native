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
package org.jboss.test.ws.jaxrpc.jbws331;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/**
 * EAR deployment of ejb.jar + ws-ejb.jar
 * 
 * http://jira.jboss.com/jira/browse/JBWS-331
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 30-Nov-2005
 */
public class JBWS331TestCase extends JBossWSTest
{
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS331TestCase.class, "jaxrpc-jbws331.ear");
   }

   public void testEndpointAccess() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
      Hello endpoint = (Hello)service.getPort(Hello.class);
      
      String retStr = endpoint.hello("Hello Server");
      assertEquals("Hello Server", retStr);
   }

   public void testRemoteAccess() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      HelloHome home = (HelloHome)iniCtx.lookup("java:comp/env/ejb/Hello");
      HelloRemote slsb = home.create();
      
      String retStr = slsb.hello("Hello Server");
      assertEquals("Hello Server", retStr);
   }
}
