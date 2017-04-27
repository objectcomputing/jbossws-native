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
package org.jboss.test.ws.tools.holders;

import java.io.File;
import java.io.Writer;

import org.jboss.test.ws.common.jbossxb.holders.HoldersServiceInterface;
import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.WSDL11Validator;
import org.jboss.test.ws.tools.validation.WSDLValidator;
import org.jboss.ws.Constants;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.JavaToWSDL;
import org.jboss.ws.tools.WSDLToJava;
import org.jboss.ws.tools.WSToolsConstants;
import org.jboss.ws.tools.exceptions.JBossWSToolsException;
import org.jboss.ws.tools.interfaces.WSDLToJavaIntf;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.ws.tools.wsdl.WSDLWriter;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.IOUtils;
import org.w3c.dom.Element;


/**
 *  Test Standard jaxrpc holders treatment
 *  by the javatowsdl and wsdltojava subsystems
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 2, 2005
 */
public class StandardHoldersTestCase extends WSToolsBase
{
   public void testStandardHoldersWsdlToJava() throws Exception
   {
      //Given a wsdl, generate the SEI incorporating the holders
      WSDLDefinitions wsdlDefinitions = getWSDLDefinitions("HolderService.wsdl");
      generateSEI(wsdlDefinitions);
      //Now validate the SEI
      String fname="HoldersServiceInterface.java";
      File file1 = getResourceFile("tools/holders/java/org/jboss/test/"+ fname);
      File file2 = getResourceFile("tools/org/jboss/test/" + fname);

      try
      {
         assertExactSourceFiles(file1, file2);
      }
      catch (Throwable e)
      {
         fail(e.getLocalizedMessage());
      }
   }

   public void testStandardHoldersJavaToWsdl() throws Exception
   {
      JavaToWSDL jwsdl = new JavaToWSDL(Constants.NS_WSDL11);
      jwsdl.setServiceName("HolderService");
      jwsdl.setTargetNamespace("http://jboss.org/test");
      jwsdl.addFeature(WSToolsConstants.WSTOOLS_FEATURE_RESTRICT_TO_TARGET_NS, true);
      jwsdl.setStyle(Style.RPC);
      WSDLDefinitions wsdl = jwsdl.generate(HoldersServiceInterface.class);

      //Generate the wsdl
      File wsdlDir = createResourceFile("tools/");
      wsdlDir.mkdirs();

      String wsdlPath = wsdlDir.getAbsolutePath()+ "/HolderService.wsdl";
      Writer fw = IOUtils.getCharsetFileWriter(new File(wsdlPath), Constants.DEFAULT_XML_CHARSET);
      new WSDLWriter(wsdl).write(fw, Constants.DEFAULT_XML_CHARSET);
      fw.close();

      //Now validate the wsdl file
      //First check if the wsdl files are well formed
      try
      {
         String fixturefile = getResourceFile("tools/holders/wsdl/HolderService.wsdl").getAbsolutePath();
         File wsdlfix = new File(fixturefile);
         Element exp = DOMUtils.parse(wsdlfix.toURL().openStream());
         File wsdlFile = getResourceFile(wsdlPath);
         assertNotNull("Generated WSDL File exists?", wsdlFile);
         Element was = DOMUtils.parse(wsdlFile.toURL().openStream());
         assertEquals(exp,was);

         //Now that we have figured out that the wsdl files are well formed,
         //lets do the semantic wsdl validation
         WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
         WSDLDefinitions wsdlExp = factory.parse(wsdlfix.toURL());

         WSDLDefinitions wsdlActual = factory.parse(wsdlFile.toURL());
         WSDLValidator validator = new WSDL11Validator();
         try
         {
           boolean bool = validator.validate(wsdlExp,wsdlActual);
           if(bool == false)
              fail("WSDL do not match");
         }catch(JBossWSToolsException e)
         {
            fail(e.getLocalizedMessage());
         }
         catch(Exception e)
         {
            fail("Unknown exception:"+e.getLocalizedMessage());
         }
      }catch(Exception e)
      {
         throw new Exception("Error::",e);
      }
   }

   //*************************************************************************
   //
   //                     PRIVATE METHODS
   //
   //*************************************************************************

   private WSDLDefinitions getWSDLDefinitions(String wsdlFileName) throws Exception
   {
      File wsdlFile = getResourceFile("tools/holders/wsdl/" + wsdlFileName);
      assertTrue("WSDL File exists?",wsdlFile.exists());

      WSDLDefinitionsFactory wsdlFactory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = wsdlFactory.parse(wsdlFile.toURL());
      return wsdlDefinitions;
   }

   private void generateSEI(WSDLDefinitions wsdl)
   {
      File seidir = createResourceFile("tools");
      seidir.mkdirs();
      WSDLToJavaIntf wsdljava = new WSDLToJava();

      //Generate the SEI
      try
      {
         wsdljava.setTypeMapping(new LiteralTypeMapping());
         wsdljava.generateSEI(wsdl, seidir);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Validate that two interfaces are identical
    * Check for imports is done as an extra step
    * @throws Exception
    */
   public void validateExactSourceFiles(File file1, File file2) throws Exception
   {

      JBossSourceComparator sc= new JBossSourceComparator(file1,file2);
      assertTrue("Source Files Match:",sc.validate());
      sc.validateImports();
   }

}
