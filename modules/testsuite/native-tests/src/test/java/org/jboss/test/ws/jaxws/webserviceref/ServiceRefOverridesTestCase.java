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
package org.jboss.test.ws.jaxws.webserviceref;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.Test;

import org.jboss.ejb3.client.ClientLauncher;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test @WebServiceRef overrides in jboss-client.xml
 *
 * @author Thomas.Diesler@jboss.com
 * @since 18-Jan-2007
 */
public class ServiceRefOverridesTestCase extends JBossWSTest
{
   public final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxws-webserviceref";

   public static Test suite()
   {
      return new JBossWSTestSetup(ServiceRefOverridesTestCase.class, "jaxws-webserviceref.war, jaxws-webserviceref-override-client.jar");
   }

   public void testService1() throws Throwable
   {
      String resStr = invokeTest(getName());
      assertEquals(getName(), resStr);
   }

   public void testService2() throws Throwable
   {
      String resStr = invokeTest(getName());
      assertEquals(getName(), resStr);
   }

   public void testService3() throws Throwable
   {
      String resStr = invokeTest(getName());
      assertEquals(getName() + getName(), resStr);
   }

   public void testService4() throws Throwable
   {
      String resStr = invokeTest(getName());
      assertEquals(getName() + getName(), resStr);
   }

   public void testPort1() throws Throwable
   {
      String resStr = invokeTest(getName());
      assertEquals(getName(), resStr);
   }

   public void testPort2() throws Throwable
   {
      String resStr = invokeTest(getName());
      assertEquals(getName() + getName(), resStr);
   }

   public void testPort3() throws Throwable
   {
      String resStr = invokeTest(getName());
      assertEquals(getName() + getName(), resStr);
   }

   @SuppressWarnings("unchecked")
   private String invokeTest(String reqStr) throws Throwable
   {
      new ClientLauncher().launch(TestEndpointClientTwo.class.getName(), "jbossws-client", new String[] { reqStr });
      Class<?> empty[] = {};
      try
      {
         //Use reflection to compile on AS 5.0.0.CR1 too
         Method getMainClassMethod = ClientLauncher.class.getMethod("getTheMainClass", empty);
         //At least JBoss AS 5.0.0.CR2
         //Use reflection to prevent double loading of the client class
         Class<?> clientClass = (Class<?>)getMainClassMethod.invoke(null, empty);
         Field field = clientClass.getField("testResult");
         return ((Map<String, String>)field.get(clientClass)).get(reqStr);
      }
      catch (NoSuchMethodException e)
      {
         //JBoss AS 5.0.0.CR1
         return TestEndpointClientTwo.testResult.get(reqStr);
      }
   }
}
