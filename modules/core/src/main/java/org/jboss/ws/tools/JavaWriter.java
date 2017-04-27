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
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.tools.XSDTypeToJava.VAR;

/**
 *  Handles writing of Java source files (XSD-> Java)
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   May 6, 2005
 */
public class JavaWriter
{
   private String newline = "\n";

   protected WSDLUtils utils = WSDLUtils.getInstance();

   public JavaWriter()
   {
   }

   /**
    * Creates a Java Class that represents a Simple Type restricted by enumeration
    * @param fname
    * @param lst List of enumerated values (we support Strings only)
    * @param loc  Location where the file has to be created
    * @param pkgname Package Name
    * @throws IOException
    */

   public void createJavaFileForEnumeratedValues(String fname, StringList lst, File loc, String pkgname, XSSimpleTypeDefinition type) throws IOException
   {
      List importList = new ArrayList();
      importList.add("java.util.Map");
      importList.add("java.util.HashMap");
      File sei = utils.createPhysicalFile(loc, fname);
      StringBuilder buf = utils.createClassBasicStructure(pkgname, fname, type, importList, null);

      buf.append("private java.lang.String value;" + newline);
      buf.append("private static Map valueMap = new HashMap();" + newline);

      //For now, we will support Strings only
      int lenOfArr = lst != null ? lst.getLength() : 0;
      for (int i = 0; i < lenOfArr; i++)
      {
         String str = lst.item(i);
         buf.append("public static final String _" + str + "String = \"" + str + "\";" + newline);
      }

      for (int i = 0; i < lenOfArr; i++)
      {
         String str = lst.item(i);
         buf.append("public static final java.lang.String _" + str + " = new java.lang.String(_" + str + "String);");
         buf.append(newline);
      }

      for (int i = 0; i < lenOfArr; i++)
      {
         String str = lst.item(i);
         buf.append("public static final " + fname + " " + str + " = new " + fname + "(_" + str + ");");
         buf.append(newline);
      }

      //Create CTR
      buf.append(newline + newline + "protected " + fname + "(java.lang.String value) { " + newline);

      buf.append("this.value = value;valueMap.put(this.toString(), this); " + newline + "}");

      buf.append(newline + newline);

      //Create getValue method
      buf.append(newline + " public java.lang.String getValue() {" + newline + "  return value;" + newline + "}");

      buf.append(newline + newline);

      //Create fromValue method
      buf.append(newline + "public static " + fname + "  fromValue(java.lang.String value)" + newline);

      buf.append(" throws java.lang.IllegalStateException {" + newline);

      for (int i = 0; i < lenOfArr; i++)
      {
         String str = lst.item(i);
         if (i > 0)
            buf.append("else ");

         buf.append("if (" + str + ".value.equals(value)) {" + newline);

         buf.append("return " + str + ";" + newline);

         buf.append("}" + newline);
      }

      buf.append(" throw new IllegalArgumentException();" + newline + "}" + newline + newline);

      //End- fromValue method
      //create- fromString method

      buf.append(newline + "public static " + fname + "  fromString(String value)" + newline);

      buf.append(" throws java.lang.IllegalStateException {" + newline);

      buf.append(fname + " ret = (" + fname + ")valueMap.get(value);" + newline);

      buf.append("if (ret != null) {" + newline + " return ret;" + newline + " }" + newline + newline);

      for (int i = 0; i < lenOfArr; i++)
      {
         String str = lst.item(i);
         if (i > 0)
            buf.append("else ");
         buf.append("if (value.equals(_" + str + "String)) {" + newline);
         buf.append("return " + str + ";" + newline + "}");
      }

      buf.append(newline + " throw new IllegalArgumentException();" + newline + "}" + newline + newline);

      //End- fromString method
      //create - toString method

      buf.append(newline + " public String toString() {" + newline + " return value.toString();" + newline + "}" + newline);

      //End - toString method

      //create -readResolve method

      buf.append(newline + "private Object readResolve()" + newline + "        throws java.io.ObjectStreamException {" + newline
            + "        return fromValue(getValue());" + newline + "    } " + newline);
      //End - readResolve method

      //create - equals method

      buf.append(newline + "private boolean equals(Object obj){" + newline + "         if (!(obj instanceof " + fname + ")) {" + newline + "         return false;"
            + newline + "    } " + newline);
      buf.append("return ((" + fname + ")obj).value.equals(value);" + newline + "}" + newline);
      //End - equals method

      //create - hashCode method

      buf.append(newline + " public int hashCode() { " + newline + "        return value.hashCode(); " + newline + "    }" + newline);
      //End - hashCode method
      buf.append("}" + newline); //end of class

      FileWriter writer = new FileWriter(sei);
      writer.write(buf.toString());
      writer.flush();
      writer.close();
   }

   /**
    * A simple utility method that just creates a Java source file
    *
    * @param location Location where the Java source file needs to be written
    * @param filename File Name of the Java source
    * @param packageName
    * @param vars
    * @param importList List of strings that represent imports
    * @param baseTypeName Name of base class
    * @param isExceptionType  Exception types need special treatment
    * @param typeNameToBaseVARList Needed if we are dealing with an exception type
    * @throws IOException
    */
   public void createJavaFile(File location, String filename, String packageName, List<VAR> vars, List<String> importList, String baseTypeName,
         boolean isExceptionType, boolean isSerializable, Map<String, List> typeNameToBaseVARList) throws IOException
   {
      File newLoc = null;
      if (needToCreatePackageStructure(location, packageName))
         newLoc = utils.createPackage(location.getPath(), packageName);
      else newLoc = location;
      String classname = ToolsUtils.convertInvalidCharacters(utils.chop(filename, ".java"));
      File sei = utils.createPhysicalFile(newLoc, classname);
      StringBuilder buffer = new StringBuilder();
      utils.writeJbossHeader(buffer);

      //Create the package Name
      buffer.append(newline).append("package ").append(packageName).append(";");

      if (importList != null)
      {
         for (String imp : importList)
         {
            buffer.append(newline).append("import ").append(imp).append(";");
         }
      }
      buffer.append(newline).append(newline);
      buffer.append(newline).append("public class  ").append(classname).append(newline);
      if (baseTypeName != null && baseTypeName.length() > 0)
         buffer.append(" extends ").append(baseTypeName);
      if (isSerializable)
         buffer.append(" implements java.io.Serializable");
      buffer.append("{").append(newline);
      createVariables(buffer, vars, isExceptionType);
      createCTR(buffer, classname, vars, isExceptionType, typeNameToBaseVARList);
      buffer.append(newline);
      createAccessors(buffer, vars, isExceptionType);
      buffer.append("}").append(newline);
      //Create a FileWriter
      FileWriter writer = new FileWriter(sei);
      writer.write(buffer.toString());
      writer.flush();
      writer.close();
   }

   //PRIVATE METHODS
   private void createCTR(StringBuilder buf, String cname, List vars, boolean isExceptionType, Map<String, List> typeNameToBaseVARList)
   {
      if (vars.size() > 0 && isExceptionType == false)
      {
         buf.append("public " + cname + "(){}"); //Empty CTR
         buf.append(newline);
         buf.append(newline);
      }

      StringBuilder ctrvarbuf = new StringBuilder();
      StringBuilder ctrintbuf = new StringBuilder();

      boolean calledSuper = false;
      if (isExceptionType)
      {
         List<VAR> baseList = typeNameToBaseVARList.get(cname);
         int baseLen = baseList != null ? baseList.size() : 0;
         String arrStr = "[]";

         if (baseLen > 0)
         {
            calledSuper = true;
            ctrintbuf.append("super(");
            for (int i = 0; i < baseLen; i++)
            {

               if (i > 0)
               {
                  ctrvarbuf.append(", ");
                  ctrintbuf.append(", ");
               }
               VAR v = baseList.get(i);
               ctrvarbuf.append(v.getVartype());
               if (v.isArrayType)
                  ctrvarbuf.append(arrStr);

               ctrvarbuf.append(" ").append(v.getVarname());
               ctrintbuf.append(v.getVarname());
            }
            ctrintbuf.append(");").append(newline);
         }
      }
      Iterator iter = vars.iterator();
      int index = 0;
      while (iter.hasNext())
      {
         if (index++ > 0 || calledSuper)
         {
            ctrvarbuf.append(", ");
         }
         VAR v = (VAR)iter.next();
         String name = v.getVarname();
         if (JavaKeywords.isJavaKeyword(name))
         {
            name = "_" + name;
         }

         String type = v.getVartype();
         boolean isArr = v.isArrayType();
         ctrvarbuf.append(type);
         if (isArr)
            ctrvarbuf.append("[]");
         ctrvarbuf.append(" " + name);
         if (isExceptionType && calledSuper == false && index == 1 && v.getVartype().equals("java.lang.String"))
         {
            ctrintbuf.append("super(").append(v.getVarname()).append(");").append(newline);
            calledSuper = true;
         }

         ctrintbuf.append("this." + name + "=" + name + ";");
         ctrintbuf.append(newline);
      }
      buf.append("public " + cname + "(" + ctrvarbuf.toString() + "){");
      buf.append(newline);
      buf.append(ctrintbuf.toString());
      buf.append("}");
   }

   private void createAccessors(StringBuilder buf, List vars, boolean isExceptionType)
   {
      Iterator iter = vars.iterator();
      while (iter.hasNext())
      {
         VAR v = (VAR)iter.next();
         String name = v.getVarname();
         String internalName = name;
         if (JavaKeywords.isJavaKeyword(internalName))
         {
            internalName = "_" + internalName;
         }

         String type = v.getVartype();
         boolean isArr = v.isArrayType();
         //Add getter/setter also
         buf.append("public " + type);
         if (isArr)
            buf.append("[] ");
         String str = " get";
         //boolean case
         if (type.equals("boolean"))
            str = " is";
         buf.append(str + utils.getMixedCase(name) + "() { return " + internalName + " ;}");
         buf.append(newline);
         buf.append(newline);
         if (isExceptionType == false)
            writeSetter(buf, name, internalName, type, isArr);
         buf.append(newline);
         buf.append(newline);
      }
   }

   private List createVariables(StringBuilder buf, List vars, boolean isExceptionType)
   {
      if (vars == null)
         return vars;
      Iterator iter = vars.iterator();
      while (iter.hasNext())
      {
         VAR v = (VAR)iter.next();
         String name = v.getVarname();
         // JBWS-1170 Convert characters which are illegal in Java identifiers
         name = ToolsUtils.convertInvalidCharacters(name);

         if (JavaKeywords.isJavaKeyword(name))
         {
            name = "_" + name;
         }

         String type = v.getVartype();
         boolean isArr = v.isArrayType();
         buf.append(newline);
         if (isExceptionType)
            buf.append("private " + type);
         else buf.append("protected " + type);
         if (isArr)
            buf.append("[] ");
         buf.append(" " + name).append(";").append(newline);
      }
      return vars;
   }

   private boolean needToCreatePackageStructure(File location, String packageName)
   {
      packageName = packageName.replace('.', '/');
      try
      {
         String externalForm = location.toURL().toExternalForm();
         return externalForm.indexOf(packageName) < 0;
      }
      catch (MalformedURLException e)
      {
         // ignore
         return false;
      }
   }

   private void writeSetter(StringBuilder buf, String name, String internalName, String type, boolean isArr)
   {
      buf.append("public void ");
      buf.append("set" + utils.getMixedCase(name) + "(" + type);
      if (isArr)
         buf.append("[]");

      buf.append(" " + internalName + "){ this." + internalName + "=" + internalName + "; }");
   }
}
