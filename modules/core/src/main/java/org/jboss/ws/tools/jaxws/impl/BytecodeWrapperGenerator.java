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

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxws.DynamicWrapperGenerator;
import org.jboss.ws.metadata.umdm.FaultMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class BytecodeWrapperGenerator extends DynamicWrapperGenerator implements WritableWrapperGenerator
{
   private List<String> typeNames = new ArrayList<String>();
   PrintStream stream;
   
   public BytecodeWrapperGenerator(ClassLoader loader, PrintStream stream)
   {
      super(loader);
      this.stream = stream;
      prune = false;
   }
   
   @Override
   public void reset(ClassLoader loader)
   {
      super.reset(loader);
      typeNames.clear();
   }

   @Override
   public void generate(FaultMetaData fmd)
   {
      super.generate(fmd);
   
      typeNames.add(fmd.getFaultBeanName());
   }

   @Override
   public void generate(ParameterMetaData pmd)
   {
      super.generate(pmd);
      
      typeNames.add(pmd.getJavaTypeName());
   }

   public void write(File directory) throws IOException
   {

		if(typeNames.isEmpty())
		{
			System.out.println("No Classes to generate...");
			return;
		}

		stream.println("Writing Classes:");
      for (String name : typeNames)
      {
         try
         {
            stream.println(name.replace('.', '/') + ".class");
            pool.get(name).writeFile(directory.getAbsolutePath());
         }
         catch (CannotCompileException e)
         {
            throw new WSException(e);
         }
         catch (NotFoundException e)
         {
            throw new WSException(e);
         }
      }
   }
}
