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
package org.jboss.test.ws.jaxrpc.jbws68;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * The type associated with a message part references a complex schema type and not a schema element that uses the schema type.
 *
 * <message name="ItolEndPoint_getAccountResponse">
 *   <part name="result" type="ns2:AccountTO"/>
 * </message>
 *
 * For the element name jboss-4.0.1 defaults to the schema type name instead of the wsdl part name, which indeed is probably incorrect.
 *
 * http://jira.jboss.com/jira/browse/JBWS-68
 *
 * @author Thomas.Diesler@jboss.org
 * @since 08-Feb-2005
 */
public class JBWS68TestCase extends JBossWSTest
{
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS68TestCase.class, "jaxrpc-jbws68.war, jaxrpc-jbws68-client.jar");
   }

   /**
    * Test JSE endpoint
    */
   public void testEndpoint() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
      Hello hello = (Hello)service.getPort(Hello.class);

      UserType in0 = new UserType("Kermit");
      UserType retObj = hello.echoUserType(in0);
      assertEquals(in0, retObj);
   }
}
