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
package org.jboss.test.ws.interop.nov2007.wsse;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * WCF Interoperability Plug-fest - November 2007
 * 
 * Scenario 3.1: Username token Auth with HTTPS protection
 * Client authenticates by passing UsernameToken in Request. Request and Response are protected by HTTPS.
 * SOAP Version: 1.1
 * Addressing:	No
 * Timestamp:	Yes
 * Username:	Alice
 * Password:	abcd!1234
 * 
 * Notes:
 * - Username and password are not actually verified in the test implementation 
 * 
 * 
 * @author Alessio Soldano <alessio.soldano@jboss.com>
 * 
 * @since 26-Oct-2007
 */
public class UsernameTokenHTTPSTestCase extends AbstractWSSEBase
{
   public static Test suite()
   {
      return new JBossWSTestSetup
      (
         UsernameTokenHTTPSTestCase.class,
         "jbossws-interop-nov2007-wsseUsernameTokenHTTPS.war, jbossws-interop-nov2007-wsseUsernameTokenHTTPS-client.jar"
      );
   }

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
   }
   
   @Override
   protected void tearDown() throws Exception
   {
      super.tearDown();
   }

   @Override
   protected void scenarioSetup(IPingService port)
   {
      ((StubExt)port).setConfigName("Standard WSSecurity Client");
      //TODO!! read truststore conf for SSL from the scenario configuration
      System.setProperty("javax.net.ssl.trustStore", "/dati/jboss-4.2/server/default/truststore_ale");
      System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
      System.setProperty("javax.net.ssl.trustStoreType", "jks");
      System.setProperty("org.jboss.security.ignoreHttpsHost", "true");
      
      ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "Alice");
      ((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "ecilA");
   }

   @Override
   protected QName getScenarioPortQName()
   {
      return new QName("http://InteropBaseAddress/interop", "UserNameOverTransport_IPingService");
   }

}
