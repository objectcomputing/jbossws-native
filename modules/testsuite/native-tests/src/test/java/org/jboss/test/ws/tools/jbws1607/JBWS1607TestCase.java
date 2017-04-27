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
package org.jboss.test.ws.tools.jbws1607;

import java.io.File;
import java.io.FilenameFilter;

import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.JaxrpcMappingValidator;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Test case for http://jira.jboss.com/jira/browse/JBWS-1607
 * 
 * WSDL to Java, If an INOUT parameter has been mapped to a 
 * JAX-RPC holder it should not also be mapped to the return 
 * type.
 * 
 * @author darran.lofthouse@jboss.com
 * @since 11 Apr 2007
 */
public class JBWS1607TestCase extends JBossWSTest
{

   /**
    * Test generation of the SEI.
    * 
    * @throws Exception
    */
   public void testGenerate() throws Exception
   {
      File resourceDir = createResourceFile("tools/jbws1607");
      String toolsDir = "target/wstools/jbws1607/output";

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
            compareSource(resourceDir + "/" + currentFile, toolsDir + "/org/jboss/test/ws/jbws1607/" + currentFile);
         }
         catch (Exception e)
         {
            throw new Exception("Validation of '" + currentFile + "' failed.", e);
         }
      }

      File packageDir = new File(toolsDir + "/org/jboss/test/ws/jbws1607");
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
