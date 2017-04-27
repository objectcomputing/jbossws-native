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
package org.jboss.test.ws.tools.jbws1725;

import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;

import java.io.File;

/**
 * Test case for http://jira.jboss.com/jira/browse/JBWS-1725
 * 
 * WSDL-JAVA Derivation of a complex type from a Simple type: bas64Binary
 * is not mapped to byte[]
 * 
 * @author mageshbk@jboss.com
 * @since Jul 19, 2007
 */
public class JBWS1725TestCase extends JBossWSTest
{

   public void testExtensionType() throws Exception
   {
      File resourceDir = createResourceFile("tools/jbws1725");
      resourceDir.mkdirs();

      String toolsDir = resourceDir.getAbsolutePath();
      String[] args = new String[] { "-dest", toolsDir, "-config", resourceDir.getAbsolutePath() + "/wstools-config.xml" };
      new WSTools().generate(args);

      compareSource(resourceDir + "/PasswordType.java", toolsDir + "/org/jboss/test/ws/jbws1725/PasswordType.java");
   }

   private static void compareSource(final String expectedName, final String generatedName) throws Exception
   {
      File expected = new File(expectedName);
      File generated = new File(generatedName);

      JBossSourceComparator sc = new JBossSourceComparator(expected, generated);
      sc.validate();
   }
}
