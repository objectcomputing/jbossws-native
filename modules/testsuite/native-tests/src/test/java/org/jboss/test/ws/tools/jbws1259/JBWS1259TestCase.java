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
package org.jboss.test.ws.tools.jbws1259;

import java.io.File;
import java.io.FilenameFilter;

import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.JaxrpcMappingValidator;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;

/**
 * 
 * @author darran.lofthouse@jboss.com
 * @since 10 Jan 2007
 */
public class JBWS1259TestCase extends JBossWSTest
{

   /**
    * 
    * <element name='lookupResponse'>
    *  <complexType>
    *   <sequence>
    *    <element name='areaCode' nillable='true' type='string'/>
    *    <element name='number' nillable='true' type='string'/>
    *    <element ref='tns:lookupResponse'/>  
    *   </sequence>
    *  </complexType>      
    * </element>
    * 
    */
   public void testScenario_A() throws Exception
   {
      generateScenario("A");
   }

   /**
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *  <sequence>
    *   <element name='areaCode' nillable='true' type='string'/>
    *   <element name='number' nillable='true' type='string'/>
    *   <element ref='tns:lookupResponse'/>  
    *  </sequence>
    * </complexType>
    * 
    */
   public void testScenario_B() throws Exception
   {
      generateScenario("B");
   }

   /**
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *  <sequence>
    *   <element name='areaCode' nillable='true' type='string'/>
    *   <element name='number' nillable='true' type='string'/>
    *   <element ref='tns:comment'/>  
    *  </sequence>
    * </complexType>
    * 
    * <element name='comment'>
    *  <complexType>
    *   <sequence>
    *    <element name='test' type='string' />
    *    <element ref='tns:comment' />       
    *   </sequence>
    *  </complexType>
    * </element>
    * 
    */
   public void testScenario_C() throws Exception
   {
      generateScenario("C");
   }

   /**
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *  <sequence>
    *   <element name='areaCode' nillable='true' type='string'/>
    *   <element name='number' nillable='true' type='string'/>
    *   <element ref='tns:comment'/>  
    *  </sequence>
    * </complexType>
    *
    * <element name='comment' type='tns:Comment' />
    * 
    * <complexType name='Comment'>
    *  <sequence>
    *   <element name='test' type='string' />
    *   <element ref='tns:comment' />       
    *  </sequence>   
    * </complexType>
    * 
    */
   public void testScenario_D() throws Exception
   {
      generateScenario("D");
   }

   protected void generateScenario(final String scenario) throws Exception
   {
      File resourceDir= createResourceFile("tools/jbws1259/scenario_" + scenario);
      resourceDir.mkdirs();

      String toolsDir = "target/wstools/jbws1259/scenario_" + scenario; 
      String[] args = new String[] { "-dest", toolsDir, "-config", resourceDir.getAbsolutePath() + "/wstools-config.xml" };
      new WSTools().generate(args);

      String[] expectedFiles = resourceDir.list(new FilenameFilter() {
         public boolean accept(File dir, String name)
         {
            return name.endsWith(".java");
         }
      });

      for (int i = 0; i < expectedFiles.length; i++)
      {
         String currentFile = expectedFiles[i];

         try
         {
            compareSource(resourceDir + "/" + currentFile, toolsDir + "/org/jboss/test/ws/jbws1259/" + currentFile);
         }
         catch (Exception e)
         {
            throw new Exception("Validation of '" + currentFile + "' failed.", e);
         }
      }

      File packageDir = new File(toolsDir + "/org/jboss/test/ws/jbws1259");
      String[] generatedFiles = packageDir.list();
      for (int i = 0; i < generatedFiles.length; i++)
      {
         String currentFile = generatedFiles[i];

         boolean matched = "PhoneBookService.java".equals(currentFile);

         for (int j = 0; j < expectedFiles.length && (matched == false); j++)
            matched = currentFile.equals(expectedFiles[j]);

         assertTrue("File '" + currentFile + "' was not expected to be generated", matched);
      }

      JaxrpcMappingValidator mappingValidator = new JaxrpcMappingValidator();
      mappingValidator.validate(resourceDir + "/jaxrpc-mapping.xml", toolsDir + "/jaxrpc-mapping.xml");
   }

   private static void compareSource(final String expectedName, final String generatedName) throws Exception
   {
      File expected = new File(expectedName);
      File generated = new File(generatedName);

      JBossSourceComparator sc = new JBossSourceComparator(expected, generated);
      sc.validate();
      sc.validateImports();
   }
}
