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
package org.jboss.test.ws.jaxrpc.jbws381;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * JBoss creates SOAP messages that don't conform WS-I
 *
 * http://jira.jboss.com/jira/browse/JBWS-381
 *
 * @author Thomas.Diesler@jboss.org
 * @author Petr Blaha (blaha.petr1@volny.cz)
 * @since 26-Oct-2005
 */
public class JBWS381TestCase extends JBossWSTest
{
   private WeatherForecastSoap port;

   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS381TestCase.class, "jaxrpc-jbws381.war, jaxrpc-jbws381-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (WeatherForecastSoap)service.getPort(WeatherForecastSoap.class);
      }
   }

   public void testWeatherByPlaceName() throws Exception
   {
      GetWeatherByPlaceNameResponse retObj = port.getWeatherByPlaceName(new GetWeatherByPlaceName("Munich"));
      assertNotNull("Expected not null", retObj.getWeatherByPlaceNameResult);
   }

   public void testWeatherByZipCode() throws Exception
   {
      GetWeatherByZipCodeResponse retObj = port.getWeatherByZipCode(new GetWeatherByZipCode("80634"));
      assertNotNull("Expected not null", retObj);
   }
}
