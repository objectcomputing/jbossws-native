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
package org.jboss.test.ws.tools.jbws1079;

import java.io.File;

import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.JaxrpcMappingValidator;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;

/**
 * 
 * @author darran.lofthouse@jboss.com
 * @since Nov 5, 2006
 */
public class JBWS1079TestCase extends JBossWSTest
{

   public void testAnonymousType() throws Exception
   {
      File resourceFile = createResourceFile("tools/jbws1079");
      String resourceDir = resourceFile.getAbsolutePath();
      String toolsDir = "target/wstools/jbws1079/output";
      String[] args = new String[] { "-dest", toolsDir, "-config", resourceDir + "/wstools-config.xml" };
      new WSTools().generate(args);

      compareSource(resourceDir + "/LookupResponseNumber.java", toolsDir + "/org/jboss/test/ws/jbws1079/LookupResponseNumber.java");
      compareSource(resourceDir + "/Person.java", toolsDir + "/org/jboss/test/ws/jbws1079/Person.java");
      compareSource(resourceDir + "/PhoneBook_PortType.java", toolsDir + "/org/jboss/test/ws/jbws1079/PhoneBook_PortType.java");
      compareSource(resourceDir + "/TelephoneNumber.java", toolsDir + "/org/jboss/test/ws/jbws1079/TelephoneNumber.java");

      JaxrpcMappingValidator mappingValidator = new JaxrpcMappingValidator();
      mappingValidator.validate(resourceDir + "/anonymous-mapping.xml", toolsDir + "/anonymous-mapping.xml");
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
