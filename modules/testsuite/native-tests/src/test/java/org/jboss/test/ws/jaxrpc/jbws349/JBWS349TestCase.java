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
package org.jboss.test.ws.jaxrpc.jbws349;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Polymorphism in return types
 * 
 * http://jira.jboss.com/jira/browse/JBWS-349
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 30-Nov-2005
 */
public class JBWS349TestCase extends JBossWSTest
{
   private static ServiceFacadeEndpoint endpoint;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS349TestCase.class, "jaxrpc-jbws349.war, jaxrpc-jbws349-client.jar");
   }

   public void setUp() throws Exception
   {
      super.setUp();
      if (endpoint == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/ServiceFacade");
         endpoint = (ServiceFacadeEndpoint)service.getPort(ServiceFacadeEndpoint.class);
      }

   }

   public void testAssetCreate() throws Exception
   {
      Event event = new AssetCreate("AssetCreate", 1, "templ");
      AssetCreateResult retObj = (AssetCreateResult)endpoint.processEvent(event);
      assertEquals("AssetCreate", retObj.getId());
   }

   public void testAssetRead() throws Exception
   {
      Event event = new AssetRead("AssetRead");
      AssetReadResult retObj = (AssetReadResult)endpoint.processEvent(event);
      assertEquals("AssetRead", retObj.getId());
      assertEquals("body", retObj.getBody());
   }

   public void testEvent() throws Exception
   {
      try
      {
         Event event = new Event("Event");
         endpoint.processEvent(event);
         fail("EventException expected");
      }
      catch (EventException ex)
      {
         assertEquals("Invalid event", ex.getMessage());
      }
   }
}
