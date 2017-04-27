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
 * Test MTOM functionality with jaxrpc-handlers in place.<br>
 * This basically determines wether XB needs to handle base64 values directly
 * or uses callbacks to XOPMarshaller/Unmarshaller.
 *
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Sep 22, 2006
 */
public class XOPHandlerTestCase extends XOPBase  {

   private static XOPPing port;

   public static Test suite()
   {
      return new JBossWSTestSetup(XOPHandlerTestCase.class, "jaxrpc-xop-doclit_handler.war, jaxrpc-xop-doclit_handler-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/XOPHandlerTestCase");
         port = (XOPPing)service.getPort(XOPPing.class);
         ((StubExt)port).setConfigName("Standard MTOM client");
      }

      //((Stub)port)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8081/jaxrpc-xop-doclit_handler");
   }

   protected XOPPing getPort() {
      return port;
   }
}
