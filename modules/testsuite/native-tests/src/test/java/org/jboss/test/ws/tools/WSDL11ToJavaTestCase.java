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
package org.jboss.test.ws.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.jboss.test.ws.tools.sei.PrimitiveArrayTypes;
import org.jboss.test.ws.tools.sei.PrimitiveTypes;
import org.jboss.test.ws.tools.sei.StandardJavaTypes;
import org.jboss.test.ws.tools.validation.WSDL11Validator;
import org.jboss.test.ws.tools.validation.WSDLValidator;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.WSDLToJava;
import org.jboss.ws.tools.interfaces.WSDLToJavaIntf;
import org.jboss.ws.tools.wsdl.WSDLWriter;

/**
 * Test jbossws WSDL11 -> Java
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Oct-2004
 */
public class WSDL11ToJavaTestCase extends WSToolsBase
{
   /** Test a SEI that contains JAXRPC primitive types */
   public void testPrimitiveTypes() throws Exception
   {
      Class seiClass = PrimitiveTypes.class;
      WSDLDefinitions wsdlDefinitions = getWSDLDefinitions(seiClass, "PrimitiveTypesService_RPC_11.wsdl");
      String wsdlString = getWSDLAsString(wsdlDefinitions);
      //System.out.println(wsdlString);
   }
   
   /** Test a SEI that contains primitive array types */
   public void testPrimitiveArrayTypes() throws Exception
   {
      Class seiClass = PrimitiveArrayTypes.class;
      WSDLDefinitions wsdlDefinitions = getWSDLDefinitions(seiClass, "arrays/PrimitiveArrayTypesService_RPC_11.wsdl");
      String wsdlString = getWSDLAsString(wsdlDefinitions);
      
      writeWSDL(wsdlDefinitions, "PrimitiveArrayTypesService_RPC_11.wsdl");          
   }

   /** Test a SEI that contains JAXRPC java standard types */
   public void testStandardJavaTypes() throws Exception
   {
      Class seiClass = StandardJavaTypes.class;
      WSDLDefinitions wsdlDefinitions = getWSDLDefinitions(seiClass, "StandardJavaTypesService_RPC_11.wsdl");
      String wsdlString = getWSDLAsString(wsdlDefinitions);
      //System.out.println(wsdlString);
      writeWSDL(wsdlDefinitions, "StandardJavaTypesService_RPC_11.wsdl");
   }

   public void testW3CSample() throws Exception
   {
      File wsdlFile = getResourceFile("tools/wsdlfixture/W3CExample_DOC_11.wsdl");
      assertTrue(wsdlFile.exists());
      WSDLDefinitions wsdlDefinitions = getWSDLDefinitions(wsdlFile);
   }

   private void writeWSDL(WSDLDefinitions wsdl, String fname) throws Exception
   {
      File wsdlDir = createResourceFile("./tools/wsdl-out");
      wsdlDir.mkdirs();
      FileWriter writer = new FileWriter(wsdlDir + "/" + fname);
      new WSDLWriter(wsdl).write(writer);
      writer.close();
   }

   private String getWSDLAsString(WSDLDefinitions wsdl) throws IOException
   {
      StringWriter strwr = new StringWriter();
      new WSDLWriter(wsdl).write(strwr);
      return strwr.toString();
   }

   private WSDLDefinitions getWSDLDefinitions(Class seiClass, String wsdlFileName) throws Exception
   {
      File wsdlFile = getResourceFile("tools/wsdlfixture/" + wsdlFileName);
      assertTrue(wsdlFile.exists());

      WSDLDefinitions wsdlDefinitions = getWSDLDefinitions(wsdlFile);
       generateSEI(wsdlDefinitions);
      
      WSDLValidator validator = new WSDL11Validator();
      if (validator.validate(seiClass, wsdlDefinitions) == false)
         System.err.println("FIXME " + validator.getErrorList().toString());

      return wsdlDefinitions;
   }

 

   private void generateSEI(WSDLDefinitions wsdl)
   {
      String seidir = "tools"; 

      WSDLToJavaIntf wsdljava = new WSDLToJava();

      //Generate the SEI
      try
      {
         wsdljava.setTypeMapping(new LiteralTypeMapping());
         wsdljava.generateSEI(wsdl, createResourceFile(seidir));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
