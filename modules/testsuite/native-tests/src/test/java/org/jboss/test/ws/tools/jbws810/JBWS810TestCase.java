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
package org.jboss.test.ws.tools.jbws810;

import java.io.File;

import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.JaxrpcMappingValidator;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;

/**
 * 
 * @author darran.lofthouse@jboss.com
 * @since 10 Jan 2007
 */
public class JBWS810TestCase extends JBossWSTest
{
   private File resourceDir;
   private String toolsDir;


   protected void setUp() throws Exception
   {
      resourceDir = createResourceFile("tools/jbws810");
      resourceDir.mkdirs();
      toolsDir = "target/wstools/jbws810";
   }

   public void testDocument() throws Exception
   {
      runTest("document");
   }

   public void testRPC() throws Exception
   {
      runTest("rpc");
   }

   public void runTest(final String type) throws Exception
   {
      String testToolsDir = toolsDir + "/" + type;
      String testResourceDir = resourceDir.getAbsolutePath() + "/" + type;

      String[] args = new String[] { "-dest", testToolsDir, "-config", testResourceDir + "/wstools-config.xml" };
      new WSTools().generate(args);

      compareSource(type, "PhoneBook_PortType.java");

      JaxrpcMappingValidator mappingValidator = new JaxrpcMappingValidator();
      mappingValidator.validate(testResourceDir + "/jaxrpc-mapping.xml", testToolsDir + "/jaxrpc-mapping.xml");
   }

   private void compareSource(final String type, final String fileName) throws Exception
   {
      File expected = new File(resourceDir + "/" + type + "/" + fileName);
      File generated = new File(toolsDir + "/" + type + "/org/jboss/test/ws/jbws810/" + fileName);

      JBossSourceComparator sc = new JBossSourceComparator(expected, generated);
      sc.validate();
      sc.validateImports();
   }

}
