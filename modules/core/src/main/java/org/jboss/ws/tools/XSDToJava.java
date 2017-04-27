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
package org.jboss.ws.tools;
 
import java.io.File;
import java.io.IOException;

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.Constants;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.ws.tools.interfaces.XSDToJavaIntf;

/**
 *  Generates Java Types given a schema file
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   May 11, 2005
 */

public class XSDToJava implements XSDToJavaIntf  
{
   /**
    * Singleton class that handle many utility functions
    */
   protected WSDLUtils utils = WSDLUtils.getInstance();

   /**
    * Utility class that converts a XSD Type into a Java class
    */
   protected final XSDTypeToJava xsdJava = new XSDTypeToJava(null, false);   


   /*  
    * @see org.jboss.ws.tools.interfaces.JavaToXSDIntf#generateSEI(java.lang.String, java.io.File, java.lang.String, boolean)
    */
   public void generateJavaSource(String schemaFile, File dirloc, String packageName,
                           boolean createPackageDir)
      throws IOException
   {
      XSLoader xsloader = SchemaUtils.getInstance().getXSLoader();
      XSModel xsmodel = xsloader.loadURI(schemaFile);
      generateJavaSource(xsmodel, dirloc, packageName, createPackageDir);
   }


   /*  
    * @see org.jboss.ws.tools.interfaces.JavaToXSDIntf#generateSEI(org.apache.xerces.xs.XSModel, java.io.File, java.lang.String, boolean)
    */
   public void generateJavaSource(XSModel xsmodel, File dirloc, String packageName,
                           boolean createPackageDir)
      throws IOException
   {
      if (createPackageDir)
      {
         utils.createPackage(dirloc.getAbsolutePath(), packageName);
      }
      generateJavaSource(xsmodel, dirloc, packageName);
   }

   /*  
    * @see org.jboss.ws.tools.interfaces.JavaToXSDIntf#generateSEI(org.apache.xerces.xs.XSModel, java.io.File, java.lang.String)
    */
   public void generateJavaSource(XSModel xsmodel, File dirloc, String packageName)
      throws IOException
   {
      //Now that we have the schema, lets build the types for the schema
      XSNamedMap xsnamedmap = xsmodel.getComponents(XSConstants.TYPE_DEFINITION);
      int len = xsnamedmap != null ? xsnamedmap.getLength() : 0;
      for (int i = 0; i < len; i++)
      {
         XSObject type = xsnamedmap.item(i);
         if (type instanceof XSComplexTypeDefinition)
         {
            XSComplexTypeDefinition ctype = (XSComplexTypeDefinition)type;
            //Ignore xsd:anyType
            String nsuri = type.getNamespace();
            String tname = type.getName();
            if (Constants.NS_SCHEMA_XSD.equals(nsuri) && "anyType".equals(tname)) continue;
            xsdJava.createJavaFile(ctype, dirloc.getPath(), packageName, xsmodel);
         }
         else if (type instanceof XSSimpleTypeDefinition)
         {
            XSSimpleTypeDefinition stype = (XSSimpleTypeDefinition)type;
            //Ignore xsd:anyType
            String nsuri = type.getNamespace();
            String tname = type.getName();
            if (Constants.NS_SCHEMA_XSD.equals(nsuri) && "anyType".equals(tname)) continue;
            xsdJava.createJavaFile(stype, dirloc.getPath(), packageName, xsmodel);
         }
      }

      //Consider Global Element Declarations that may have anonymous complex types
      xsnamedmap = xsmodel.getComponents(XSConstants.ELEMENT_DECLARATION);
      len = xsnamedmap != null ? xsnamedmap.getLength() : 0;
      for (int i = 0; i < len; i++)
      {
         XSElementDeclaration elm = (XSElementDeclaration)xsnamedmap.item(i);
         String elmname = elm.getName();
         XSTypeDefinition elmtype = elm.getTypeDefinition();
         if (elmtype != null && elmtype instanceof XSComplexTypeDefinition)
         {
            XSComplexTypeDefinition ctype = (XSComplexTypeDefinition)elmtype;
            String nsuri = elmtype.getNamespace();
            String tname = elmtype.getName();
            if (tname != null) continue;   //Consider only anon complex types
            
            createJavaFile(ctype, dirloc, packageName, xsmodel, elmname);
         }
      }
   }
   
   /**
    * Set the type mapping to be used in the generation
    * 
    * @param tm
    */
   public void setTypeMapping(LiteralTypeMapping tm)
   {
      xsdJava.setTypeMapping(tm);
   }
   
   private void createJavaFile(XSComplexTypeDefinition type, File loc,
         String pkgname, XSModel schema, String outerElementName)
   throws IOException
   {
      String str = "Method should be used for anon complex types only";
      if (type.getName() != null)
         throw new IllegalArgumentException(str);
      xsdJava.createJavaFile(type,outerElementName,loc.getPath(),pkgname,schema, false);
   }
   
}




