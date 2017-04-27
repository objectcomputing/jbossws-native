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
package org.jboss.test.ws.jaxrpc.xop.doclit;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test MTOM functionaly without any jaxrpc handlers in place.
 *
 * @author Heiko.Braun@jboss.org
 * @since Sep 22, 2006
 */
public class XOPTestCase extends XOPBase {

   private static XOPPing port;

   public static Test suite()
   {
      return new JBossWSTestSetup(XOPTestCase.class, "jaxrpc-xop-doclit.war, jaxrpc-xop-doclit-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/XOPTestCase");
         port = (XOPPing)service.getPort(XOPPing.class);
         ((StubExt)port).setConfigName("Standard MTOM client");
      }

      //((Stub)port)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8081/jaxrpc-xop-doclit");
   }

   protected XOPPing getPort()
   {
      return port;
   }

}
