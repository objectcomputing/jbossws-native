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
package org.jboss.test.ws.common.wsdl11;

import java.io.File;
import java.net.URL;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

import org.jboss.net.protocol.URLStreamHandlerFactory;
import org.jboss.ws.tools.wsdl.WSDL11DefinitionFactory;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Test a wsdl import functionality.
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 11-May-2004
 */
public class SimpleFileImportTestCase extends JBossWSTest
{
   private final String WSDL_LOCATION = getResourceFile("common/wsdl11/simplefile/SimpleFile.wsdl").getPath();

   public void testFileURL() throws Exception
   {
      File wsdlFile = new File(WSDL_LOCATION);
      assertTrue("File does not exist: " + wsdlFile.getCanonicalPath(), wsdlFile.exists());

      // Setting the URLStreamHandlerFactory simulates the behaviour in JBoss
      internalInitURLHandlers();

      URL wsdlURL = wsdlFile.toURL();
      Definition wsdlDefinition = WSDL11DefinitionFactory.newInstance().parse(wsdlURL);
      assertNotNull(wsdlDefinition);

      QName serviceName = (QName)wsdlDefinition.getServices().keySet().iterator().next();

      // test construction of the client service
      ServiceFactory serviceFactory = ServiceFactory.newInstance();
      Service service = serviceFactory.createService(wsdlURL, serviceName);
      assertNotNull(service);
   }

   /**
    * Set up our only URLStreamHandlerFactory.
    * This is needed to ensure Sun's version is not used (as it leaves files
    * locked on Win2K/WinXP platforms.
    */
   private void internalInitURLHandlers()
   {
      // Install a URLStreamHandlerFactory that uses the TCL
      URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory());

      // Preload JBoss URL handlers
      URLStreamHandlerFactory.preload();
   }
}
