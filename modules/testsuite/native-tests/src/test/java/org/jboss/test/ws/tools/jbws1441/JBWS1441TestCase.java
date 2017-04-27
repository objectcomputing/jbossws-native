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
package org.jboss.test.ws.tools.jbws1441;

import java.io.File;

import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.JaxrpcMappingValidator;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;

/**
 * 
 * @author darran.lofthouse@jboss.com
 * @since 5 Jan 2007
 */
public class JBWS1441TestCase extends JBossWSTest
{
   private String resourceDir = createResourceFile("tools/jbws1441").getAbsolutePath();
   private String toolsDir = "target/wstools/jbws1441/output";

   public void testGenerate() throws Exception
   {
      String[] args = new String[] { "-dest", toolsDir, "-config", resourceDir + "/wstools-config.xml" };
      new WSTools().generate(args);

      compareSource("AbstractType.java");
      compareSource("Person.java");
      compareSource("PhoneBook_PortType.java");
      compareSource("TelephoneNumber.java");

      JaxrpcMappingValidator mappingValidator = new JaxrpcMappingValidator();
      mappingValidator.validate(resourceDir + "/jaxrpc-mapping.xml", toolsDir + "/jaxrpc-mapping.xml");
   }

   private void compareSource(final String fileName) throws Exception
   {
      File expected = new File(resourceDir + "/" + fileName);
      File generated = new File(toolsDir + "/org/jboss/test/ws/jbws1441/" + fileName);

      JBossSourceComparator sc = new JBossSourceComparator(expected, generated);
      sc.validate();
      sc.validateImports();
   }
}
