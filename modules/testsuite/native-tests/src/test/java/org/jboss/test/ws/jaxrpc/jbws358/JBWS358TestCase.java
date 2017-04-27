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
package org.jboss.test.ws.jaxrpc.jbws358;

import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import org.jboss.wsf.test.JBossWSTest;



/**
 * Unpackaged ear deployments with unpackaged jars inside them do not undeploy WS correctly
 * 
 * http://jira.jboss.com/jira/browse/JBWS-358
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 30-Nov-2005
 */
public class JBWS358TestCase extends JBossWSTest
{
   public void testClientAccess() throws Exception
   {
      Hello endpoint = null;
      try
      {
         deploy("jaxrpc-jbws358.ear");
         
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
         endpoint = (Hello)service.getPort(Hello.class);
         
         String retStr = endpoint.hello("Hello Server");
         assertEquals("Hello Server", retStr);
      }
      finally
      {
         undeploy("jaxrpc-jbws358.ear");
      }

      try
      {
         endpoint.hello("Hello Server");
         fail("Endpoint should not be available");
      }
      catch (RemoteException ex)
      {
         // expected
      }
   }

}
