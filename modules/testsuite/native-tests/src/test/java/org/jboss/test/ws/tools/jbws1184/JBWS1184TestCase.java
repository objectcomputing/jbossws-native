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
package org.jboss.test.ws.tools.jbws1184;

import java.io.File;

import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.JaxrpcMappingValidator;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;

/**
 * JBWS-1184 - Test the use of wrapper jave type for generated source
 * with attributes with use of required or optional.
 * 
 * @author darran.lofthouse@jboss.com
 * @since 13 Jan 2007
 */
public class JBWS1184TestCase extends JBossWSTest
{

   /**
    * Test attributes with use set to required.
    * 
    * <complexType name='DataObject'>
    *  <attribute name='booleanValue' use='required' type='boolean'/>
    *  <attribute name='byteValue' use='required' type='byte'/>
    *  <attribute name='floatValue' use='required' type='float'/>
    *  <attribute name='integerValue' use='required' type='int'/>
    *  <attribute name='longValue' use='required' type='long'/>
    *  <attribute name='shortValue' use='required' type='short'/>        
    * </complexType>
    * 
    */
   public void testUseRequired() throws Exception
   {
      generateScenario("A");
   }

   /**
    * Test attributes with use set to optional.
    * 
    * <complexType name='DataObject'>
    *  <attribute name='booleanValue' use='optional' type='boolean'/>
    *  <attribute name='byteValue' use='optional' type='byte'/>
    *  <attribute name='floatValue' use='optional' type='float'/>
    *  <attribute name='integerValue' use='optional' type='int'/>
    *  <attribute name='longValue' use='optional' type='long'/>
    *  <attribute name='shortValue' use='optional' type='short'/>        
    * </complexType>
    * 
    */
   public void testUseOptional() throws Exception
   {
      generateScenario("B");
   }

   protected void generateScenario(final String scenario) throws Exception
   {
      File resourceFile = createResourceFile("tools/jbws1184/scenario_" + scenario);
      String resourceDir = resourceFile.getAbsolutePath();
      String toolsDir = "target/wstools/jbws1184/scenario_" + scenario;
      String[] args = new String[] { "-dest", toolsDir, "-config", resourceDir + "/wstools-config.xml" };
      new WSTools().generate(args);

      compareSource(resourceDir + "/DataObject.java", toolsDir + "/org/jboss/test/ws/jbws1184/DataObject.java");
      compareSource(resourceDir + "/TestEndpoint_PortType.java", toolsDir + "/org/jboss/test/ws/jbws1184/TestEndpoint_PortType.java");

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
