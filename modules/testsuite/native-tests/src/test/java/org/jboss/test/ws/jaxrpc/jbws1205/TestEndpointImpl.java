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
package org.jboss.test.ws.jaxrpc.jbws1205;

import java.io.File;
import java.io.FilenameFilter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import org.jboss.wsf.spi.management.ServerConfig;
import org.jboss.wsf.spi.management.ServerConfigFactory;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;

/**
 * @author darran.lofthouse@jboss.com
 * @since 21-September-2006
 */
public class TestEndpointImpl implements TestEndpoint
{

   public void performTest() throws TestException
   {
      File[] baseFiles = null;
      File[] endFiles = null;
      String response = "";

      try
      {
         SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
         ServerConfig serverConfig = spiProvider.getSPI(ServerConfigFactory.class).getServerConfig();File tmpDir = serverConfig.getServerTempDir();
         tmpDir = new File(tmpDir.getCanonicalPath() + "/jbossws");

         baseFiles = getXsdTempFiles(tmpDir);

         SimpleEndpoint endpoint = getEndpoint();

         response = endpoint.echo("Hello");

         endFiles = getXsdTempFiles(tmpDir);
      }
      catch (Exception e)
      {
         throw new TestException("Test Failure", e);
      }

      if (response.equals("Hello") == false)
      {
         throw new TestException("Wrong response, expected 'Hello' got '" + response + "'");
      }

      if (endFiles.length > baseFiles.length)
      {
         throw new TestException("Call caused additional files to be created.");
      }
   }

   private SimpleEndpoint getEndpoint() throws Exception
   {
      Context ctx = new InitialContext();
      Service service = (Service)ctx.lookup("java:comp/env/service/SimpleService");

      return (SimpleEndpoint)service.getPort(SimpleEndpoint.class);
   }

   private File[] getXsdTempFiles(final File tmpDir)
   {
      return tmpDir.listFiles(new FilenameFilter() {
         public boolean accept(File dir, String name)
         {
            return name.endsWith(".xsd");
         }
      });
   }

}
