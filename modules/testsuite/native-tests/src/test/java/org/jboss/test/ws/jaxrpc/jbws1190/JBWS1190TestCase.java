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
package org.jboss.test.ws.jaxrpc.jbws1190;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * WSDL generated for JSR-181 POJO does not take 'transport-guarantee' in web.xml into account
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1190
 * 
 * @author darran.lofthouse@jboss.com
 * @since Oct 22, 2006
 */
public class JBWS1190TestCase extends JBossWSTest
{
   private static final String ARCHIVE_NAME = "jaxrpc-jbws1190.war";

   private static TestEndpoint port;

   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS1190TestCase.class, ARCHIVE_NAME + ", jaxrpc-jbws1190-client.jar");
   }

   protected void setUp() throws Exception
   {
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (TestEndpoint)service.getPort(TestEndpoint.class);
      }
   }

   public void testTestEndpoint() throws Exception
   {
      port.testAddress(ARCHIVE_NAME, "TestService", "http", "8080");
   }

   public void testConfidentialEndpoint() throws Exception
   {
      port.testAddress(ARCHIVE_NAME, "ConfidentialService", "https", "8443");
   }
}
