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
package org.jboss.test.ws.tools.scripts;

import java.io.File;
import org.jboss.wsf.test.JBossWSTest;

/**
 * JBWS-1793: Provide a test case for the tools scripts that reside under JBOSS_HOME/bin
 * 
 * @author Heiko.Braun@jboss.com
 */
public class ScriptTestCase extends JBossWSTest
{
   private static final String FS = System.getProperty("file.separator"); // '/' on unix, '\' on windows
   private static final String PS = System.getProperty("path.separator"); // ':' on unix, ';' on windows
   private String TOOLS_CONFIG = getResourceFile("tools" + FS + "scripts" + FS + "wstools-config.xml").getAbsolutePath();
   private static final String EXT = ":".equals( PS ) ? ".sh" : ".bat";

   private String JBOSS_HOME;

   protected void setUp() throws Exception
   {
      super.setUp();

      JBOSS_HOME = System.getProperty("jboss.home");
   }

   public void testWSToolsFromCommandLine() throws Exception
   {
      // use absolute path for the output to be re-usable      
      File dest = createResourceFile("wstools" + FS + "java");
      dest.mkdirs();

      String command = JBOSS_HOME + FS + "bin" + FS + "wstools" + EXT + " -config " + TOOLS_CONFIG + " -dest "+ dest.getAbsolutePath();
      executeCommand(command);

      File javaSource = getResourceFile("wstools" + FS + "java" + FS + "org" + FS + "jboss" + FS + "test" + FS + "ws" + FS + "jbws810" + FS + "PhoneBookService.java");

      assertTrue("Service endpoint interface not generated", javaSource.exists());
   }

}
