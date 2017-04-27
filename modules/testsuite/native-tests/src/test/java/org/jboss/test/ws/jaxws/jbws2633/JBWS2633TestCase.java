/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.jbws2633;

import java.io.File;

import org.jboss.wsf.test.JBossWSTest;

/**
 * [JBWS-2633] - Test case to test wscompile where a namespace contains 'Public'
 * 
 * @author darran.lofthouse@jboss.com
 * @since 28th April 2009
 */
public class JBWS2633TestCase extends JBossWSTest
{
   private static final String FS = System.getProperty("file.separator"); // '/' on unix, '\' on windows
   private static final String PS = System.getProperty("path.separator"); // ':' on unix, ';' on windows
   private static final String EXT = ":".equals(PS) ? ".sh" : ".bat";

   private String JBOSS_HOME = System.getProperty("jboss.home");

   public void testWSConsume() throws Exception
   {
      if (true)
      {
         System.out.println("FIXME [JBWS-2633] wscompile fails to create valid package name where namespace contains capitalised reserved keyword.");
         return;
      }

      // use absolute path for the output to be re-usable
      String absOutput = "target/wsconsume/jbws2633";
      String wsdlFile = createResourceFile("jaxws/jbws2633/PhoneBook.wsdl").getAbsolutePath();

      String command = JBOSS_HOME + FS + "bin" + FS + "wsconsume" + EXT + " -k -o " + absOutput + " " + wsdlFile;
      executeCommand(command);

      String packageDir = new File(absOutput + "/org/jboss/_public/test/ws/jbws2633").getAbsolutePath();

      checkFileExists(packageDir, "PhoneBook.java");
      checkFileExists(packageDir, "PhoneBook_Service.java");

      packageDir = new File(absOutput + "/org/jboss/_public/test/ws/jbws2633/types").getAbsolutePath();

      checkFileExists(packageDir, "Person.java");
      checkFileExists(packageDir, "TelephoneNumber.java");
   }

   private static void checkFileExists(String packageDir, String filename)
   {
      File expectedFile = new File(packageDir + FS + filename);
      assertTrue("File '" + filename + "' missing from folder '" + packageDir + "'", expectedFile.exists());
   }

}
