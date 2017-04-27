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

import com.sun.tools.ws.wscompile.WsimportTool;

import org.jboss.ws.tools.io.NullPrintStream;
import org.jboss.wsf.spi.tools.WSContractConsumer;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * WSContractConsumer that delegates to the Sun CompileTool.
 *
 * @author <a href="jason.greene@jboss.com">Jason T. Greene</a>
 * @author <a href="heiko.braun@jboss.com">Heiko Braun</a>
 */
public class SunRIConsumerImpl extends WSContractConsumer
{
   private List<File> bindingFiles;
   private File catalog;
   private boolean extension;
   private boolean generateSource;
   private boolean nocompile;
   private File outputDir = new File("output");
   private File sourceDir;
   private String targetPackage;
   private PrintStream messageStream;
   private String wsdlLocation;
   private List<String> additionalCompilerClassPath = new ArrayList<String>();
   private String target = "2.0";

   @Override
   public void setBindingFiles(List<File> bindingFiles)
   {
      this.bindingFiles = bindingFiles;
   }

   @Override
   public void setCatalog(File catalog)
   {
      this.catalog = catalog;
   }

   @Override
   public void setExtension(boolean extension)
   {
      this.extension = extension;
   }

   @Override
   public void setGenerateSource(boolean generateSource)
   {
      this.generateSource = generateSource;
   }

   @Override
   public void setMessageStream(PrintStream messageStream)
   {
      this.messageStream = messageStream;
   }

   @Override
   public void setOutputDirectory(File directory)
   {
      outputDir = directory;
   }

   @Override
   public void setSourceDirectory(File directory)
   {
      sourceDir = directory;
   }

   @Override
   public void setTargetPackage(String targetPackage)
   {
      this.targetPackage = targetPackage;
   }

   @Override
   public void setWsdlLocation(String wsdlLocation)
   {
      this.wsdlLocation = wsdlLocation;
   }

   @Override
   public void setAdditionalCompilerClassPath(List<String> additionalCompilerClassPath)
   {
      this.additionalCompilerClassPath = additionalCompilerClassPath;
   }

   @Override
   public void setTarget(String target)
   {     
      this.target = target;
   }

   @Override
   public void setNoCompile(boolean nocompile)
   {
      this.nocompile = nocompile;
   }

   @Override
   public void consume(URL wsdl)
   {
      List<String> args = new ArrayList<String>();
      if (bindingFiles != null)
      {
         for (File file : bindingFiles)
         {
            args.add("-b");
            args.add(file.getAbsolutePath());

         }
      }

      if (catalog != null)
      {
         args.add("-catalog");
         args.add(catalog.getAbsolutePath());
      }

      if (extension)
      {
         args.add("-extension");
      }

      if (nocompile)
      {
         args.add("-Xnocompile");
      }

      if (generateSource)
      {
         args.add("-keep");
         if (sourceDir != null)
         {
            if (!sourceDir.exists() && !sourceDir.mkdirs())
               throw new IllegalStateException("Could not make directory: " + sourceDir.getName());

            args.add("-s");
            args.add(sourceDir.getAbsolutePath());
         }
      }

      if (targetPackage != null)
      {
         args.add("-p");
         args.add(targetPackage);
      }

      if (wsdlLocation != null)
      {
         args.add("-wsdllocation");
         args.add(wsdlLocation);
      }

      PrintStream stream = messageStream;
      if (stream != null)
      {
         args.add("-verbose");
      }
      else
      {
         stream = NullPrintStream.getInstance();
      }

      if (!outputDir.exists() && !outputDir.mkdirs())
         throw new IllegalStateException("Could not make directory: " + outputDir.getName());

      // Always add the output directory and the wsdl location
      args.add("-d");
      args.add(outputDir.getAbsolutePath());

      // Always set the target
       if(!target.equals("2.0") && !target.equals("2.1"))
         throw new IllegalArgumentException("WSConsume (native) only supports JAX-WS 2.0 and 2.1");
      
      args.add("-target");
      args.add(target);

      // finally the WSDL file
      args.add(wsdl.toString());

      // See WsimportTool#compileGeneratedClasses()
      String javaClassPath = System.getProperty("java.class.path");
      if(additionalCompilerClassPath.isEmpty() == false)
      {
         StringBuffer javaCP = new StringBuffer();
         for(String s : additionalCompilerClassPath)
         {
            javaCP.append(s).append(File.pathSeparator);
         }
         System.setProperty("java.class.path", javaCP.toString());
      }

      // enforce woodstox
      String xmlInputFactory = System.getProperty("javax.xml.stream.XMLInputFactory");
      if (xmlInputFactory == null)
         System.setProperty("javax.xml.stream.XMLInputFactory", "com.ctc.wstx.stax.WstxInputFactory");

      try
      {
         WsimportTool compileTool = new WsimportTool(stream);
         boolean success = compileTool.run(args.toArray(new String[args.size()]));

         if (!success)
            throw new IllegalStateException("WsImport invocation failed. Try the verbose switch for more information");
      }
      catch (RuntimeException rte)
      {
         if (messageStream != null)
         {
            messageStream.println("Failed to invoke WsImport");
            rte.printStackTrace(messageStream);
         }
         else
         {
            rte.printStackTrace();
         }
         
         // Investigate, why this cannot be thrown
         throw rte;
      }
      finally
      {
         resetSystemProperty("java.class.path", javaClassPath);
         resetSystemProperty("javax.xml.stream.XMLInputFactory", xmlInputFactory);
      }
   }

   private void resetSystemProperty(String key, String value)
   {
      if (value != null)
         System.setProperty(key, value);
      else
         System.clearProperty(key);
   }
}
