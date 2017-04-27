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
package org.jboss.ws.tools.jaxws.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.ws.WSException;
import org.jboss.ws.metadata.builder.jaxws.JAXWSWebServiceMetaDataBuilder;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.tools.io.NullPrintStream;
import org.jboss.wsf.common.ResourceLoaderAdapter;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.deployment.DeploymentModelFactory;
import org.jboss.wsf.spi.tools.WSContractProvider;

/**
 * The default WSContractProvider implementation.
 * 
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
final class JBossWSProviderImpl extends WSContractProvider
{
   private ClassLoader loader;
   private boolean generateWsdl = false;
   private boolean generateSource = false;
   private File outputDir = new File("output");
   private File resourceDir = null;
   private File sourceDir = null;
   private PrintStream messageStream = NullPrintStream.getInstance();

   private void createDirectories(File resourceDir, File sourceDir)
   {
      if (!outputDir.exists())
         if (!outputDir.mkdirs())
            throw new WSException("Could not create directory: " + outputDir);

      if (generateWsdl && !resourceDir.exists())
         if (!resourceDir.mkdirs())
            throw new WSException("Could not create directory: " + resourceDir);

      if (generateSource && !sourceDir.exists())
         if (!sourceDir.mkdirs())
            throw new WSException("Could not create directory: " + sourceDir);
   }

   @Override
   public void provide(Class<?> endpointClass)
   {
      // Use the output directory as the default
      File resourceDir = (this.resourceDir != null) ? this.resourceDir : outputDir;
      File sourceDir = (this.sourceDir != null) ? this.sourceDir : outputDir;

      createDirectories(resourceDir, sourceDir);

      messageStream.println("Output directory: " + outputDir.getAbsolutePath());
      messageStream.println("Source directory: " + sourceDir.getAbsolutePath());

      // Create a dummy classloader to catch generated classes
      ClassLoader loader = new URLClassLoader(new URL[0], this.loader);
      UnifiedMetaData umd = new UnifiedMetaData(new ResourceLoaderAdapter(loader));
      umd.setClassLoader(loader);

      ChainedWritableWrapperGenerator generator = new ChainedWritableWrapperGenerator();
      if (generateSource)
         generator.add(new SourceWrapperGenerator(loader, messageStream), sourceDir);
      generator.add(new BytecodeWrapperGenerator(loader, messageStream), outputDir);

      JAXWSWebServiceMetaDataBuilder builder = new JAXWSWebServiceMetaDataBuilder();
      builder.setWrapperGenerator(generator);
      builder.setGenerateWsdl(generateWsdl);
      builder.setToolMode(true);
      builder.setWsdlDirectory(resourceDir);
      builder.setMessageStream(messageStream);      

      if (generateWsdl)
         messageStream.println("Generating WSDL:");

      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      DeploymentModelFactory factory = spiProvider.getSPI(DeploymentModelFactory.class);
      Deployment dep = factory.newDeployment("wsprovide-deployment", loader);
      dep.setRuntimeClassLoader(loader);

      builder.buildWebServiceMetaData(dep, umd, endpointClass, null);
      try
      {
         generator.write();
      }
      catch (IOException io)
      {
         throw new WSException("Could not write output files:", io);
      }
   }

   @Override
   public void provide(String endpointClass)
   {
      try
      {
         provide(loader.loadClass(endpointClass));
      }
      catch (ClassNotFoundException e)
      {
         throw new WSException("Class not found: " + endpointClass);
      }
   }

   @Override
   public void setClassLoader(ClassLoader loader)
   {
      this.loader = loader;
   }

   @Override
   public void setGenerateWsdl(boolean generateWsdl)
   {
      this.generateWsdl = generateWsdl;
   }

   @Override
   public void setOutputDirectory(File directory)
   {
      outputDir = directory;
   }

   @Override
   public void setGenerateSource(boolean generateSource)
   {
      this.generateSource = generateSource;
   }

   @Override
   public void setResourceDirectory(File directory)
   {
      resourceDir = directory;
   }

   @Override
   public void setSourceDirectory(File directory)
   {
      sourceDir = directory;
   }

   @Override
   public void setMessageStream(PrintStream messageStream)
   {
      this.messageStream = messageStream;
   }
}
