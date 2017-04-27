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
package org.jboss.ws.tools.interfaces;

import java.io.File;
import java.io.IOException;

import org.apache.xerces.xs.XSModel;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
 
/**
 * Defines the contract for all Schema Types to Java Types converters
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 20, 2005 
 */

public interface XSDToJavaIntf
{ 
   /**
    * Generate the Java Types given the XSD Schema file
    * @param schemaFile String representing FQN of a schema file
    * @param dirloc location to place the generated java source files
    * @param packageName package name for the types
    * @param createPackageDir Should the package structure be created
    * @throws IOException
    */
   public void generateJavaSource(
         String schemaFile,
         File dirloc,
         String packageName,
         boolean createPackageDir) throws IOException;

   /**
    * Generate the Java Types given the XSD Schema object graph
    * @param xsmodel xml schema object graph based on Xerces Schema API
    * @param dirloc location to place the generated java source files
    * @param packageName package name for the types
    * @param createPackageDir Should the package structure be created
    * @throws IOException
    */
   public void generateJavaSource(
         XSModel xsmodel,
         File dirloc,
         String packageName,
         boolean createPackageDir) throws IOException;

   /**
    * Generate the Java Types given the XSD Schema object graph
    * @param xsmodel xml schema object graph based on Xerces Schema API
    * @param dirloc location to place the generated java source files
    * @param packageName package name for the types
    * @throws IOException
    */
   public void generateJavaSource(XSModel xsmodel, File dirloc, String packageName)
      throws IOException; 
   
   /**
    * Set the type mapping to be used in the generation
    * 
    * @param tm
    */
   public void setTypeMapping(LiteralTypeMapping tm);
}
