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
package org.jboss.test.ws.jaxrpc.jbws79;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Cannot deploy a ws containing more than one service per wsdl
 *
 * http://jira.jboss.com/jira/browse/JBWS-79
 *
 * @author Thomas.Diesler@jboss.org
 * @since 08-Feb-2005
 */
public class JBWS79TestCase extends JBossWSTest
{
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS79TestCase.class, "jaxrpc-jbws79.war, jaxrpc-jbws79-client.jar");
   }

   /** Test endpoint one
    */
   public void testEndpointOne() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloOneService");
      HelloOne hello = (HelloOne)service.getPort(HelloOne.class);

      String in0 = "Kermit";
      assertEquals(in0, hello.echoString(in0));
   }

   /** Test endpoint two
    */
   public void testEndpointTwo() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloTwoService");
      HelloTwo hello = (HelloTwo)service.getPort(HelloTwo.class);

      QName in0 = new QName("http://somens", "Kermit");
      assertEquals(in0, hello.echoQName(in0));
   }
}
