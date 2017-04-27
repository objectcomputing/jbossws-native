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
package org.jboss.test.ws.tools.jbws1170;

import java.io.BufferedReader;
import java.io.FileReader;

import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;

/**
 * [JBWS-1170] Test elements with illegal characters for Java identifiers
 *
 * @author <a href="mailto:david.boeren@jboss.com">David Boeren</a>
 */
public class JBWS1170TestCase extends JBossWSTest
{
   public final void testHyphenatedElement() throws Exception
   {
      String resourceDir = createResourceFile("tools/jbws1170").getAbsolutePath();
      String toolsDir = resourceDir; //"tools/jbws1170";
      String[] args = new String[] { "-dest", toolsDir, "-config", resourceDir + "/wstools-config.xml" };
      new WSTools().generate(args);

      // With JDK6 these are not lexically equivalent
      //Element exp = DOMUtils.parse(new FileInputStream(resourceDir + "/wrapper-mapping.xml"));
      //Element was = DOMUtils.parse(new FileInputStream(toolsDir + "/wrapper-mapping.xml"));
      //assertEquals(exp, was);

      String expLine = "";
      BufferedReader br = new BufferedReader(new FileReader(resourceDir + "/ParsedAddress.java"));
      while (expLine.indexOf("postal") == -1)
         expLine = br.readLine();
      br.close();
      String wasLine = "";
      br = new BufferedReader(new FileReader(toolsDir + "/com/company/id/servicename_consumer/_1_0/ParsedAddress.java"));
      while (wasLine.indexOf("postal") == -1)
         wasLine = br.readLine();
      br.close();
   }
}
