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

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;

import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.spi.management.ServerConfig;
import org.jboss.wsf.spi.management.ServerConfigFactory;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;

/**
 * 
 * @author darran.lofthouse@jboss.com
 * @since 19-October-2006
 */
public class TestEndpointImpl implements TestEndpoint
{

   public void testAddress(final String archive, final String service, final String scheme, final String port)
   {
      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      ServerConfig serverConfig = spiProvider.getSPI(ServerConfigFactory.class).getServerConfig();      

      File dataDir = serverConfig.getServerDataDir();
      File wsdlDir = new File(dataDir.getAbsolutePath() + File.separator + "wsdl" + File.separator + archive);

      if (wsdlDir.exists() == false)
      {
         throw new JBWS1190Exception(wsdlDir.getAbsolutePath() + " does not exist.");
      }

      File[] wsdls = wsdlDir.listFiles(new FilenameFilter() {
         public boolean accept(File dir, String name)
         {
            return name.startsWith(service);
         }
      });

      assertEquals("WSDL files found", 1, wsdls.length);

      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdl;
      try
      {
         wsdl = factory.parse(wsdls[0].toURL());
      }
      catch (MalformedURLException e)
      {
         throw new JBWS1190Exception("Error readin WSDL", e);
      }

      WSDLService[] services = wsdl.getServices();
      assertEquals("No of services", 1, services.length);

      WSDLEndpoint[] endpoints = services[0].getEndpoints();
      assertEquals("No of endpoints", 1, endpoints.length);

      String address = endpoints[0].getAddress();
      assertTrue("Expected Scheme '" + scheme + "' from address '" + address + "'", address.startsWith(scheme + "://"));
      assertTrue("Expected Port '" + port + "' from address '" + address + "'", address.indexOf(":" + port + "/") > -1);
   }

   private void assertEquals(final String message, final int expected, final int actual)
   {
      if (expected != actual)
      {
         throw new JBWS1190Exception(message + " expected=" + expected + " actual=" + actual);
      }
   }

   private void assertTrue(final String message, final boolean value)
   {
      if (value == false)
      {
         throw new JBWS1190Exception(message);
      }
   }
}
