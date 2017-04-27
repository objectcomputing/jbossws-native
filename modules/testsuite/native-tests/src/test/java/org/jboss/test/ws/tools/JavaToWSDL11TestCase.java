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
import java.io.Writer;
import java.io.FileWriter;

import org.jboss.test.ws.common.jbossxb.complex.ComplexTypes;
import org.jboss.test.ws.tools.sei.ArrayInterface;
import org.jboss.test.ws.tools.sei.CustomInterface;
import org.jboss.test.ws.tools.sei.InheritenceChildInterface;
import org.jboss.test.ws.tools.sei.PrimitiveArrayTypes;
import org.jboss.test.ws.tools.sei.PrimitiveTypes;
import org.jboss.test.ws.tools.sei.ServiceException;
import org.jboss.test.ws.tools.sei.StandardJavaTypes;
import org.jboss.ws.Constants;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.tools.JavaToWSDL;
import org.jboss.ws.tools.WSToolsConstants;
import org.jboss.ws.tools.wsdl.WSDLWriter;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.IOUtils;
import org.w3c.dom.Element;

/**
 * Test Case that tests Java SEI to WSDL11
 * @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 */
public class JavaToWSDL11TestCase extends JBossWSTest
{
   /** Test a SEI that contains JAXRPC primitive types */
   public void testPrimitiveTypes() throws Exception
   {
      Class seiClass = PrimitiveTypes.class;
      String fixturefile = getResourceFile("tools/wsdlfixture/PrimitiveTypesService_RPC_11.wsdl").getAbsolutePath();
      doWSDLTest(seiClass, fixturefile);
   }

   /** Test a SEI that contains JAXRPC java standard types */
   public void testStandardJavaTypes() throws Exception
   {
      Class seiClass = StandardJavaTypes.class;
      String fixturefile = getResourceFile("tools/wsdlfixture/StandardJavaTypesService_RPC_11.wsdl").getAbsolutePath();
      doWSDLTest(seiClass, fixturefile);
   }

   /** Test a SEI that contains custom types */
   public void testCustomTypes() throws Exception
   {
      Class seiClass = CustomInterface.class;
      String fixturefile = getResourceFile("tools/wsdlfixture/CustomInterfaceService_RPC_11.wsdl").getAbsolutePath();
      doWSDLTest(seiClass, fixturefile);
   }

   /** Test a SEI that contains custom exceptions */
   public void testExceptionTypes() throws Exception
   {
      Class seiClass = ServiceException.class;
      String fixturefile = getResourceFile("tools/wsdlfixture/ServiceExceptionService_RPC_11.wsdl").getAbsolutePath();
      doWSDLTest(seiClass, fixturefile);
   }

   /** Test a SEI that contains complex types */
   public void testComplexTypes() throws Exception
   {
      Class seiClass = ComplexTypes.class;
      String fixturefile = getResourceFile("tools/wsdlfixture/ComplexTypesService_RPC_11.wsdl").getAbsolutePath();
      doWSDLTest(seiClass, fixturefile);
   }

   /** Test a SEI that contains array types */
   public void testArrayTypes() throws Exception
   {
      Class seiClass = ArrayInterface.class;
      // String fixturefile = getResourceFile("tools/wsdlfixture/ArrayInterfaceService_RPC_11.wsdl").getAbsolutePath();
      String fixturefile = getResourceFile("tools/wsdlfixture/arrays/ArrayInterfaceService_RPC_11.wsdl").getAbsolutePath();
      doWSDLTest(seiClass, fixturefile);
   }
   
   /** Test a SEI that contains primitive array types */
   public void testPrimitiveArrayTypes() throws Exception
   {
      Class seiClass = PrimitiveArrayTypes.class;
      String fixturefile = getResourceFile("tools/wsdlfixture/arrays/PrimitiveArrayTypesService_RPC_11.wsdl").getAbsolutePath();
      doWSDLTest(seiClass, fixturefile);
   }
   
   /** Test a SEI that inherits a method from a super-interface. */
   public void testInterfaceInheritence() throws Exception
   {
      Class seiClass = InheritenceChildInterface.class;
      String fixturefile = getResourceFile("tools/wsdlfixture/InheritenceChildInterfaceService_RPC.wsdl").getAbsolutePath();
      doWSDLTest(seiClass, fixturefile);
   }

   private void doWSDLTest(Class seiClass, String fixturefile) throws Exception
   {
      File wsdlDir = createResourceFile("./tools/wsdl-out");
      wsdlDir.mkdirs();
      
      String sname = WSDLUtils.getInstance().getJustClassName(seiClass) + "Service";
      String wsdlPath = wsdlDir + "/" + sname + ".wsdl";
      JavaToWSDL jwsdl = new JavaToWSDL(Constants.NS_WSDL11);
      jwsdl.setServiceName(sname);
      jwsdl.setTargetNamespace("http://org.jboss.ws");
      jwsdl.addFeature(WSToolsConstants.WSTOOLS_FEATURE_RESTRICT_TO_TARGET_NS, true);
      jwsdl.setStyle(Style.RPC);
      WSDLDefinitions wsdl = jwsdl.generate(seiClass);

      Writer fw = IOUtils.getCharsetFileWriter(new File(wsdlPath), Constants.DEFAULT_XML_CHARSET);
      new WSDLWriter(wsdl).write(fw, Constants.DEFAULT_XML_CHARSET);
      fw.close();

      //Validate the generated WSDL
      File wsdlfix = new File(fixturefile);
      Element exp = DOMUtils.parse(wsdlfix.toURL().openStream());
      File wsdlFile = new File(wsdlPath);
      assertNotNull("Generated WSDL File exists?", wsdlFile);
      Element was = DOMUtils.parse(wsdlFile.toURL().openStream());

      assertEquals(exp, was);
      /*
       File wsdlFile = createResourceFile(config.getWsdlOutFile());
       WSDLDefinitionsFactory wsdlFactory = WSDLDefinitionsFactory.newInstance();
       WSDLDefinitions wsdl = wsdlFactory.parse(wsdlFile.toURL());

       WSDLValidator validator = new WSDLValidator();
       if (validator.validate(seiClass, wsdl) == false)
       fail(validator.getErrorList().toString());
       */
   }

}
