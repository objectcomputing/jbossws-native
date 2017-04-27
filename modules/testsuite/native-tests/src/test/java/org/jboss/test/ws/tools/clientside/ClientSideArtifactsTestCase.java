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
package org.jboss.test.ws.tools.clientside;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;

import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.JaxrpcMappingValidator;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.WSDLToJava;
import org.jboss.ws.tools.client.ServiceCreator;
import org.jboss.ws.tools.mapping.MappingFileGenerator;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/**
 *  JBWS-160: Test Harness for Client-Side Artifacts Generation
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jun 22, 2005 
 */
public class ClientSideArtifactsTestCase extends WSToolsBase
{
   //Set up the test
   protected void setUp()
   {
      mkdirs("tools/jbws-160/jbossws/simple");
      mkdirs("tools/jbws-160/jbossws/simple/sei");
      mkdirs("tools/jbws-160/jbossws/simple/mapping");
      mkdirs("tools/jbws-160/jbossws/custom");
      mkdirs("tools/jbws-160/jbossws/custom/sei");
      mkdirs("tools/jbws-160/jbossws/custom/mapping");
   }

   public void mkdirs(String path)
   {
      File file = createResourceFile(path);
      if (file.exists() == false)
         file.mkdirs();
   }   

   /** Test a simple SEI that uses primitives */
   public void testSimpleCase() throws Exception
   {
      WSDLToJava wsdlJava = new WSDLToJava();
      File wsdlFile = getResourceFile("tools/jbws-161/wscompile/simple/wsdl/HelloWsService.wsdl");
      wsdlJava.setTypeMapping(new LiteralTypeMapping());
      WSDLDefinitions wsdl = wsdlJava.convertWSDL2Java(wsdlFile.toURL());
      wsdlJava.generateSEI(wsdl, createResourceFile("tools/jbws-160/jbossws/simple/sei"));

      //Create the Service File
      //Generate the Service Interface
      ServiceCreator sc = new ServiceCreator();
      sc.setPackageName("org.jboss.types");
      //sc.setServiceName("HelloWsService");
      sc.setDirLocation(createResourceFile("tools/jbws-160/jbossws/simple/service"));
      sc.setWsdl(wsdl);
      //sc.setPortName( "HelloWs" );
      sc.createServiceDescriptor();

      //Generate the Mapping file
      MappingFileGenerator mgf = new MappingFileGenerator(wsdl, new LiteralTypeMapping());
      mgf.setPackageName("org.jboss.types");
      mgf.setServiceName("HelloWsService");
      JavaWsdlMapping jwm = mgf.generate();
      FileWriter fw = new FileWriter( createResourceFile("tools/jbws-160/jbossws/simple/mapping" + "/" + "jaxrpc-mapping.xml"));
      fw.write(jwm.serialize());
      fw.close();

      //Match the Service File
      String fname = "HelloWsService.java";
      File file1 = getResourceFile("tools/jbws-160/wscompile/simple/service/" + fname);
      File file2 = createResourceFile("tools/jbws-160/jbossws/simple/service/org/jboss/types/" + fname);

      try
      {
         assertExactSourceFiles(file1, file2);
      }
      catch (Throwable e)
      {
         fail(e.getLocalizedMessage());
      }

      //Match the SEI
      fname = "HelloWs.java";
      file1 = getResourceFile("tools/jbws-160/wscompile/simple/sei/" + fname);
      file2 = createResourceFile("tools/jbws-160/jbossws/simple/sei/org/jboss/types/" + fname);

      try
      {
         assertExactSourceFiles(file1, file2);
      }
      catch (Throwable e)
      {
         fail(e.getLocalizedMessage());
      }

      //Compare mapping files
      File expFile = getResourceFile("tools/jbws-160/wscompile/simple/mapping/jaxrpc-mapping.xml");
      File genFile = createResourceFile("tools/jbws-160/jbossws/simple/mapping/jaxrpc-mapping.xml");

      compareXMLFiles(expFile.toURL(), genFile.toURL());
   }

   /** Test a custom SEI that uses custom types */
   public void testCustomCase() throws Exception
   {
      WSDLToJava wsdlJava = new WSDLToJava();
      File wsdlFile = getResourceFile("tools/jbws-161/wscompile/custom/wsdl/HelloCustomService.wsdl");
      wsdlJava.setTypeMapping(new LiteralTypeMapping());
      WSDLDefinitions wsdl = wsdlJava.convertWSDL2Java(wsdlFile.toURL());
      wsdlJava.setTypeMapping(new LiteralTypeMapping());
      wsdlJava.generateSEI(wsdl, createResourceFile("tools/jbws-160/jbossws/custom/sei"));

      //Create the Service File
      //Generate the Service Interface
      ServiceCreator sc = new ServiceCreator();
      sc.setPackageName("org.jboss.types");
      //sc.setServiceName("HelloCustomService");
      sc.setDirLocation(createResourceFile("tools/jbws-160/jbossws/custom/service"));
      sc.setWsdl(wsdl);
      //sc.setPortName( "HelloCustomRemote" );
      sc.createServiceDescriptor();

      //Generate the Mapping file
      MappingFileGenerator mgf = new MappingFileGenerator(wsdl, new LiteralTypeMapping());
      mgf.setPackageName("org.jboss.types");
      mgf.setServiceName("HelloCustomService");
      //    mgf.generate(); 
      JavaWsdlMapping jwm = mgf.generate();
      FileWriter fw = new FileWriter( createResourceFile("tools/jbws-160/jbossws/custom/mapping" + "/" + "jaxrpc-mapping.xml")); 
      fw.write(DOMWriter.printNode(DOMUtils.parse(jwm.serialize()), true));
      fw.close();

      //Match the Service File
      String fname = "HelloCustomService.java";
      File file1 = getResourceFile("tools/jbws-160/wscompile/custom/service/" + fname);
      File file2 = createResourceFile("tools/jbws-160/jbossws/custom/service/org/jboss/types/" + fname);

      try
      {
         assertExactSourceFiles(file1, file2);
      }
      catch (Throwable e)
      {
         fail(e.getLocalizedMessage());
      }

      //Match the SEI
      fname = "HelloCustomRemote.java";
      file1 = getResourceFile("tools/jbws-160/wscompile/custom/sei/" + fname);
      file2 = createResourceFile("tools/jbws-160/jbossws/custom/sei/org/jboss/types/" + fname);

      /*try
       {
       assertExactSourceFiles(file1, file2);
       }
       catch (Throwable e)
       {
       fail(e.getLocalizedMessage());
       }*/

      //Compare mapping files 
      String fix = getResourceFile("tools/jbws-160/wscompile/custom/mapping/jaxrpc-mapping.xml").getAbsolutePath();
      String gen = "tools/jbws-160/jbossws/custom/mapping/jaxrpc-mapping.xml";

      JaxrpcMappingValidator mv = new JaxrpcMappingValidator();
      // [JBWS-1291] Fix JaxrpcMappingValidator
      //assertTrue(mv.validate(fix,gen));
   }

   /**
    * Method that compares the equivalence of two Jax-RPC mapping files
    */
   private void compareXMLFiles(URL expectedURL, URL actualURL) throws Exception
   {
      Element expEl = DOMUtils.parse(expectedURL.openStream());
      Element actEl = DOMUtils.parse(actualURL.openStream());

      assertEquals(expEl, actEl);
   }

   /**
    * Validate that two interfaces are identical
    * Check for imports is done as an extra step
    * @throws Exception
    */
   public void validateExactSourceFiles(File file1, File file2) throws Exception
   {
      JBossSourceComparator sc = new JBossSourceComparator(file1, file2);
      assertTrue("Source Files Match:", sc.validate());
      sc.validateImports();
   }
}
