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
package org.jboss.test.ws.tools.jbws1260;

import java.io.File;
import java.io.FilenameFilter;

import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.JaxrpcMappingValidator;
import org.jboss.ws.WSException;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;

/**
 * [JBWS-1260] - Test case to test various scenarios unwrapping parameters
 * when using wsdl-java.
 * 
 * @author darran.lofthouse@jboss.com
 * @since Nov 22, 2006
 */
public class JBWS1260TestCase extends JBossWSTest
{

   /**
    * Test scenario where the element referenced as the message
    * parts contains an anonymous complex type which contains
    * a single element.
    * 
    * <element name='lookupResponse'>
    *   <complexType>
    *     <sequence>
    *       <element name='number' nillable='true' type='string'/>
    *     </sequence>
    *   </complexType>   
    * </element>
    * 
    */
   public void testScenario_A() throws Exception
   {
      generateScenario("A");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts contains an anonymous complex type which contains
    * two elements.
    * 
    * <element name='lookupResponse'>
    *   <complexType>
    *     <sequence>
    *       <element name='areaCode' nillable='true' type='string'/> 
    *       <element name='number' nillable='true' type='string'/>
    *     </sequence>
    *   </complexType>   
    * </element>
    * 
    */
   public void testScenario_B() throws Exception
   {
      try
      {
         generateScenario("B");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains 
    * a single element.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    *  
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='number' nillable='true' type='string'/>
    *   </sequence>
    * </complexType> 
    * 
    */
   public void testScenario_C() throws Exception
   {
      generateScenario("C");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains two
    * elements.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='areaCode' nillable='true' type='string'/>
    *     <element name='number' nillable='true' type='string'/>
    *   </sequence>
    * </complexType>
    * 
    */
   public void testScenario_D() throws Exception
   {
      try
      {
         generateScenario("D");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the message part
    * references a complex type which contains an anonymous complex
    * type which contains one element.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='Number'>
    *       <complexType>
    *         <sequence>
    *           <element name='number' nillable='true' type='string'/>
    *         </sequence>
    *       </complexType>
    *     </element>
    *   </sequence>
    * </complexType>
    */
   public void testScenario_E() throws Exception
   {
      generateScenario("E");
   }

   /**
    * Test scenario where the element referenced as the message part
    * references a complex type which contains an anonymout complex
    * type which contains two elements.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='Number'>
    *       <complexType>
    *         <sequence>
    *           <element name='areaCode' nillable='true' type='string'/>
    *           <element name='number' nillable='true' type='string'/>
    *         </sequence>
    *       </complexType>
    *     </element>
    *   </sequence>
    * </complexType>
    * 
    */
   public void testScenario_F() throws Exception
   {
      generateScenario("F");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts contains an anonymous complex type which contains
    * a single element which is an array.
    * 
    * <element name='lookupResponse'>
    *   <complexType>
    *     <sequence>
    *       <element name='number' nillable='true' type='string' maxOccurs='unbounded'/>
    *     </sequence>
    *   </complexType>   
    * </element>
    */
   public void testScenario_G() throws Exception
   {
      generateScenario("G");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains a single 
    * element which is an array.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='number' nillable='true' type='string' maxOccurs='unbounded'/>
    *   </sequence>
    * </complexType>
    * 
    */
   public void testScenario_H() throws Exception
   {
      generateScenario("H");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains an anonymous 
    * complex type which contains a single element which is an array.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='Number'>
    *       <complexType>
    *         <sequence>
    *           <element name='number' nillable='true' type='string' maxOccurs='unbounded'/>
    *         </sequence>
    *       </complexType>
    *     </element>
    *   </sequence>
    * </complexType>
    * 
    */
   public void testScenario_I() throws Exception
   {
      generateScenario("I");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts contains an anonymous complex type which contains
    * an empty sequence.
    * 
    * <element name='lookupResponse'>
    *   <complexType>
    *     <sequence>       
    *     </sequence>
    *   </complexType>   
    * </element>
    * 
    */
   public void testScenario_J() throws Exception
   {
      generateScenario("J");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains
    * an empty sequence.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *   </sequence>
    * </complexType>   
    * 
    */
   public void testScenario_K() throws Exception
   {
      generateScenario("K");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains an anonymous
    * complex type which contains an empty sequence.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='Number'>
    *       <complexType>
    *         <sequence>
    *         </sequence>
    *       </complexType>
    *     </element>
    *   </sequence>
    * </complexType>   
    * 
    */
   public void testScenario_L() throws Exception
   {
      generateScenario("L");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts contains an anonymous complex type which contains
    * a single element and an attribute.
    * 
    * <element name='lookupResponse'>
    *   <complexType>
    *     <sequence>
    *       <element name='number' nillable='true' type='string'/>
    *     </sequence>
    *     <attribute name='postcode' type='string'/>
    *   </complexType>   
    * </element>
    * 
    */
   public void testScenario_M() throws Exception
   {
      try
      {
         generateScenario("M");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains
    * a single element and an attribute.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='number' nillable='true' type='string'/>
    *   </sequence>
    *   <attribute name='postcode' type='string'/>
    * </complexType>   
    * 
    */
   public void testScenario_N() throws Exception
   {
      try
      {
         generateScenario("N");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the message part
    * references a complex type which contains an anonymous complex
    * type which contains one element and one attribute.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='Number'>
    *       <complexType>
    *         <sequence>
    *           <element name='number' nillable='true' type='string'/>
    *         </sequence>
    *         <attribute name='postcode' type='string'/>
    *       </complexType>
    *     </element>
    *   </sequence>
    * </complexType>   
    *
    */
   public void testScenario_O() throws Exception
   {
      generateScenario("O");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts contains an anonymous complex type which contains
    * a single element which is an array and a single parameter.
    * 
    * <element name='lookupResponse'>
    *   <complexType>
    *     <sequence>
    *       <element name='number' nillable='true' type='string' maxOccurs='unbounded'/>
    *     </sequence>
    *     <attribute name='postcode' type='string'/>
    *   </complexType>   
    * </element>
    * 
    */
   public void testScenario_P() throws Exception
   {
      try
      {
         generateScenario("P");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains a single 
    * element which is an array and a single attribute.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='number' nillable='true' type='string' maxOccurs='unbounded'/>
    *   </sequence>
    *   <attribute name='postcode' type='string'/>
    * </complexType>   
    * 
    */
   public void testScenario_Q() throws Exception
   {
      try
      {
         generateScenario("Q");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains an anonymous 
    * complex type which contains a single element which is an array
    * and a single attribute.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='Number'>
    *       <complexType>
    *         <sequence>
    *           <element name='number' nillable='true' type='string' maxOccurs='unbounded'/>
    *         </sequence>
    *         <attribute name='postcode' type='string'/>
    *       </complexType>
    *     </element>
    *   </sequence>
    * </complexType>
    * 
    */
   public void testScenario_R() throws Exception
   {
      generateScenario("R");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts contains an anonymous complex type which contains
    * an empty sequence and a single attribute.
    * 
    * <element name='lookupResponse'>
    *   <complexType>
    *     <sequence>       
    *     </sequence>
    *     <attribute name='postcode' type='string'/>
    *   </complexType>   
    * </element>
    * 
    */
   public void testScenario_S() throws Exception
   {
      try
      {
         generateScenario("S");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains
    * an empty sequence and a single attribute.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *   </sequence>
    *   <attribute name='postcode' type='string'/>
    * </complexType>   
    * 
    */
   public void testScenario_T() throws Exception
   {
      try
      {
         generateScenario("T");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains an anonymous
    * complex type which contains an empty sequence and a single
    * attribute.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='Number'>
    *       <complexType>
    *         <sequence>
    *         </sequence>
    *         <attribute name='postcode' type='string'/>
    *       </complexType>
    *     </element>
    *   </sequence>
    * </complexType>   
    * 
    */
   public void testScenario_U() throws Exception
   {
      generateScenario("U");
   }

   /**
    * Test scenario where the element referenced as the message
    * parts contains an anonymous complex type which contains
    * a single attribute.
    * 
    * <element name='lookupResponse'>
    *   <complexType>
    *     <attribute name='postcode' type='string'/>
    *   </complexType>   
    * </element>
    * 
    */
   public void testScenario_V() throws Exception
   {
      try
      {
         generateScenario("V");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains
    * a single attribute.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <attribute name='postcode' type='string'/>
    * </complexType>   
    * 
    */
   public void testScenario_W() throws Exception
   {
      try
      {
         generateScenario("W");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the message
    * parts references a complex type which contains an anonymous
    * complex type which contains a single attribute.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='Number'>
    *       <complexType>
    *         <attribute name='postcode' type='string'/>
    *       </complexType>
    *     </element>
    *   </sequence>
    * </complexType>   
    * 
    */
   public void testScenario_X() throws Exception
   {
      generateScenario("X");
   }

   /**
    * Test case to test where the element referenced as the message 
    * parts contains an anonymous complex type which contains a 
    * single element which is an array (which is a complex type).
    * 
    * <complexType name='Telephone'>
    *   <sequence>
    *     <element name='digits' type='string'/>
    *   </sequence>
    * </complexType>   
    *
    * <element name='lookupResponse'>
    *   <complexType>
    *     <sequence>
    *       <element name='number' nillable='true' type='tns:Telephone' maxOccurs='unbounded'/>
    *     </sequence>
    *   </complexType>   
    * </element>
    * 
    */
   public void testScenario_Y() throws Exception
   {
      generateScenario("Y");
   }

   /**
    * Test case to test where the element referenced as the message 
    * parts references a complex type which contains a single element 
    * which is an array (which is a complex type).
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='Telephone'>
    *   <sequence>
    *     <element name='digits' type='string'/>
    *   </sequence>
    * </complexType>
    *
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='number' nillable='true' type='tns:Telephone' maxOccurs='unbounded'/>
    *   </sequence>
    * </complexType>
    * 
    */
   public void testScenario_Z() throws Exception
   {
      generateScenario("Z");
   }

   /**
    * Test case to test where the element referenced as the message 
    * parts references a complex type which contains an anonymous type 
    * which contains a single element which is an array (which is a comlex type).
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='Title'>
    *   <sequence>
    *     <element name='text' type='string'/>
    *   </sequence>
    * </complexType>
    *
    * <complexType name='Person'>
    *   <sequence>
    *     <element name='Name'>
    *       <complexType>
    *         <sequence>
    *           <element name='surname' nillable='true' type='tns:Title' maxOccurs='unbounded'/>
    *         </sequence>
    *       </complexType>
    *     </element>
    *   </sequence>
    * </complexType>     
    * 
    */
   public void testScenario_AA() throws Exception
   {
      generateScenario("AA");
   }

   /**
    * Test case to test where the element referenced as the message 
    * parts contains an anonymous simple type.
    * 
    * <element name='lookupResponse'>
    *   <simpleType>
    *     <restriction base='string'>
    *       <maxLength value="50"/>
    *     </restriction>
    *   </simpleType>
    * </element>    
    * 
    */
   public void testScenario_AB() throws Exception
   {
      try
      {
         generateScenario("AB");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test case to test where the element referenced as the message 
    * parts references a named simple type.
    * 
    * <element name='lookupResponse' type='tns:number'/>   
    * 
    * <simpleType name='number'>
    *   <restriction base='string'>
    *     <maxLength value="50"/>
    *   </restriction>
    * </simpleType>    
    * 
    */
   public void testScenario_AC() throws Exception
   {
      try
      {
         generateScenario("AC");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   /**
    * Test scenario where the element referenced as the input message
    * part contains an anonymous complex type which contains
    * two elements.
    * 
    * The element referenced as the output message part contains an 
    * anonymous complex type with a single element.
    * 
    * <element name='lookupResponse'>
    *   <complexType>
    *     <sequence> 
    *       <element name='number' nillable='true' type='string'/>
    *     </sequence>
    *   </complexType>   
    * </element>
    * 
    */
   public void testScenario_AD() throws Exception
   {
      generateScenario("AD");
   }

   /**
    * Test scenario where the element referenced as the input message
    * part references a complex type which contains two elements.
    * 
    * The element referenced as the output message part references a 
    * complex type with a single element.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='number' nillable='true' type='string'/>
    *   </sequence>
    * </complexType>
    * 
    */
   public void testScenario_AE() throws Exception
   {
      generateScenario("AE");
   }

   /**
    * Test scenario where the element referenced as the input message
    * part references a complex type which contains an element which
    * contains an anonymous complex type which contains two elements.
    * 
    * The element referenced as the output message part references a 
    * complex type which contains an element which contains an anonymous
    * complex type which contains a single element.
    * 
    * <element name='lookupResponse' type='tns:TelephoneNumber'/>
    * 
    * <complexType name='TelephoneNumber'>
    *   <sequence>
    *     <element name='Number'>
    *       <complexType>
    *         <sequence>
    *           <element name='number' nillable='true' type='string'/>
    *         </sequence>
    *       </complexType>
    *    </element>
    *  </sequence>
    * </complexType> 
    * 
    */
   public void testScenario_AF() throws Exception
   {
      generateScenario("AF");
   }

   /**
    * Test scenario where unwrapping should not occur as the element for the request
    * message does not match the name of the operation.
    * 
    * <operation name='lookup'>
    *   <input message='tns:PhoneBook_lookup'/>
    *   <output message='tns:PhoneBook_lookupResponse'/>
    * </operation> 
    * 
    * <message name='PhoneBook_lookup'>
    *   <part element='ns1:lookupPerson' name='parameters'/>
    * </message>
    */
   public void testScenario_AG() throws Exception
   {
      try
      {
         generateScenario("AG");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }
   
   /**
    * Test scenario where unwrapping should not occur as thre are 
    * multiple parts in each message. 
    * 
    * <message name='PhoneBook_lookup'>
    *  <part element='ns1:lookup' name='parameters'/>
    *  <part element='ns1:anotherPerson' name='anotherPerson'/>
    * </message>
    * 
    * <message name='PhoneBook_lookupResponse'>
    *  <part element='ns1:lookupResponse' name='result'/>
    *  <part element='ns1:anotherResponse' name='anotherTelephoneNumber'/>  
    * </message>
    * 
    */
   public void testScenario_AH() throws Exception
   {
      try
      {
         generateScenario("AH");
         fail("Expected exception not thrown.");
      }
      catch (WSException e)
      {
      }
   }

   protected void generateScenario(final String scenario) throws Exception
   {
      File resourceDir = createResourceFile("tools/jbws1260/scenario_" + scenario);
      resourceDir.mkdirs();
      String toolsDir = "target/wstools/jbws1260/scenario_" + scenario;
      String[] args = new String[] { "-dest", toolsDir, "-config", resourceDir + "/wstools-config.xml" };
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
            compareSource(resourceDir + "/" + currentFile, toolsDir + "/org/jboss/test/ws/jbws1260/" + currentFile);
         }
         catch (Exception e)
         {
            throw new Exception("Validation of '" + currentFile + "' failed.", e);
         }
      }

      File packageDir = new File(toolsDir + "/org/jboss/test/ws/jbws1260");
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
      mappingValidator.validate(resourceDir + "/wrapped-mapping.xml", toolsDir + "/wrapped-mapping.xml");
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
