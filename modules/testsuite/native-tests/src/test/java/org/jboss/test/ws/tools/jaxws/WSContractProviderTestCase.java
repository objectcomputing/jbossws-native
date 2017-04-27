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
package org.jboss.test.ws.tools.jaxws;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.spi.tools.WSContractProvider;
import org.jboss.wsf.common.JavaUtils;

/**
 * Tests the WSContractProvider API.
 * 
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class WSContractProviderTestCase extends JBossWSTest
{
   public void testBasic() throws Exception
   {
      WSContractProvider gen = getGenerator();
      File outputDir = createResourceFile("tools/jaxws/wscontractprovider/basic/out");
      gen.setOutputDirectory(outputDir);
      gen.provide(DocWrappedServiceImpl.class);
      
      checkWrapperClasses(outputDir);
   
      // There should be no source code
      checkWrapperSource(outputDir, false);
   }

   private WSContractProvider getGenerator()
   {
      return WSContractProvider.newInstance();
   }

   private void checkWrapperSource(File outputDir, boolean shouldExist)
   {
      File file1 = new File(outputDir, "org/jboss/test/ws/tools/jaxws/jaxws/SubmitPO.java");
      File file2 = createResourceFile(outputDir, "org/jboss/test/ws/tools/jaxws/jaxws/SubmitPOResponse.java");
      assertEquals(shouldExist, file1.exists());
      assertEquals(shouldExist, file2.exists());
   }

   private void checkWrapperClasses(File outputDir) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException
   {
      // Use a different loader each time to make sure the files exist
      URLClassLoader classLoader = new URLClassLoader(new URL[]{outputDir.toURL()}, Thread.currentThread().getContextClassLoader());
      
      // Check request wrapper
      Class wrapper = JavaUtils.loadJavaType("org.jboss.test.ws.tools.jaxws.jaxws.SubmitPO", classLoader);      
      wrapper.getMethod("setPurchaseOrder", PurchaseOrder.class);
      assertEquals(PurchaseOrder.class.getName(), wrapper.getMethod("getPurchaseOrder").getReturnType().getName());
      
      // Check response wrapper
      wrapper = JavaUtils.loadJavaType("org.jboss.test.ws.tools.jaxws.jaxws.SubmitPOResponse", classLoader);
      wrapper.getMethod("setPurchaseOrderAck", PurchaseOrderAck.class);
      assertEquals(PurchaseOrderAck.class.getName(), wrapper.getMethod("getPurchaseOrderAck").getReturnType().getName());
   }
   
   public void testSource() throws Exception
   {
      WSContractProvider gen = getGenerator();
      File outputDir = createResourceFile("tools/jaxws/wscontractprovider/source/out");
      gen.setOutputDirectory(outputDir);
      gen.setGenerateSource(true);
      gen.provide(DocWrappedServiceImpl.class);
      
      checkWrapperClasses(outputDir);
      checkWrapperSource(outputDir, true);
   }
   
   public void testSourceDir() throws Exception
   {
      WSContractProvider gen = getGenerator();
      File outputDir = createResourceFile("tools/jaxws/wscontractprovider/sourcedir/out");
      File sourceDir = createResourceFile("tools/jaxws/wscontractprovider/sourcedir/source");
      
      gen.setOutputDirectory(outputDir);
      gen.setSourceDirectory(sourceDir);
      gen.setGenerateSource(true);
      gen.provide(DocWrappedServiceImpl.class);
      
      checkWrapperClasses(outputDir);
      checkWrapperSource(outputDir, false);
      checkWrapperSource(sourceDir, true);
   }
   
   public void testWsdl() throws Exception
   {
      WSContractProvider gen = getGenerator();
      File outputDir = createResourceFile("tools/jaxws/wscontractprovider/wsdl/out");
      gen.setOutputDirectory(outputDir);
      gen.setGenerateWsdl(true);
      gen.provide(DocWrappedServiceImpl.class);
      
      checkWrapperClasses(outputDir);
   
      // There should be no source code
      checkWrapperSource(outputDir, false);
      
      File wsdlFile = createResourceFile(outputDir, "DocWrappedService.wsdl");
      WSDLDefinitionsFactory wsdlFactory = WSDLDefinitionsFactory.newInstance();
      wsdlFactory.parse(wsdlFile.toURL());
   }
   
   public void testResourceDir() throws Exception
   {
      WSContractProvider gen = getGenerator();
      File outputDir = createResourceFile("tools/jaxws/wscontractprovider/resourcedir/out");
      File wsdlDir = createResourceFile("tools/jaxws/wscontractprovider/resourcedir/wsdl");
      gen.setOutputDirectory(outputDir);
      gen.setResourceDirectory(wsdlDir);
      gen.setGenerateWsdl(true);
      gen.provide(DocWrappedServiceImpl.class);
      
      checkWrapperClasses(outputDir);
   
      // There should be no source code
      checkWrapperSource(outputDir, false);
      
      String wsdlName = "DocWrappedService.wsdl";
      File wsdlFile = createResourceFile(outputDir, wsdlName);
      assertFalse(wsdlFile.exists());
      
      wsdlFile = createResourceFile(wsdlDir, wsdlName);
      WSDLDefinitionsFactory wsdlFactory = WSDLDefinitionsFactory.newInstance();
      wsdlFactory.parse(wsdlFile.toURL());
   }
}
