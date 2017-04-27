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
package org.jboss.test.ws.tools.sourcecomp;
 
import java.io.File;

import org.jboss.logging.Logger;
import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.wsf.test.JBossWSTest;

/** Checks the fixture for Java Source Code Comparison
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Mar 7, 2005
 */
public class JBossSourceCompTestCase extends JBossWSTest
{
   private static Logger log = Logger.getLogger(JBossSourceCompTestCase.class);
   private JBossSourceComparator sc = null;
   
   /**
    * Validate that two interfaces are identical
    * Check for imports is done as an extra step
    * @throws Exception
    */
   public void testExactSourceFiles() throws Exception
   {
      String fname="PrimitiveTypes.java";
      File file1 = getResourceFile("tools/sourcecomp/expected/" + fname);
      File file2 = getResourceFile("tools/sourcecomp/actual/" + fname);
      sc= new JBossSourceComparator(file1,file2);
      assertTrue("Source Files Match:",sc.validate());
      sc.validateImports();
   }

   /**
    * Test two identical interfaces with one missing a method
    */
   public void testMissingMethod()
   {
      String fname="PrimitiveTypes.java";
      File file1 = getResourceFile("tools/sourcecomp/expected/" + fname);
      File file2 = getResourceFile("tools/sourcecomp/missingmethod/" + fname);
      try
      {
         sc= new JBossSourceComparator(file1,file2);
      }
      catch (Exception e)
      {
         fail(e.getLocalizedMessage());
      }
      try{
        sc.validate();
        fail("Test Should have failed");
      }catch(Throwable e)
      {
         log.debug("Test Passed:There should be an exception::",e);
         //We are fine
      }
   }

   /**
    * Test two identical interfaces with one missing a parameter to a method
    */
   public void testMissingParam()
   {
      String fname="PrimitiveTypes.java";
      File file1 = getResourceFile("tools/sourcecomp/expected/" + fname);
      File file2 = getResourceFile("tools/sourcecomp/missingparam/" + fname);
      try
      {
         sc= new JBossSourceComparator(file1,file2);
      }
      catch (Exception e)
      {
         fail(e.getLocalizedMessage());
      }
      try{
        sc.validate();
        fail("Test Should have failed");
      }catch(Throwable e)
      {
        log.debug("Test Passed:We got an exception::",e);
        //we are fine
      }
   }

   /**
    * Test two identical interfaces with one missing 1 or more imports
    */
   public void testMissingImports()
   {
      String fname="PrimitiveTypes.java";
      File file1 = getResourceFile("tools/sourcecomp/expected/" + fname);
      File file2 = getResourceFile("tools/sourcecomp/missingimport/" + fname);
      try
      {
         sc= new JBossSourceComparator(file1,file2);
      }
      catch (Exception e)
      {
         fail(e.getLocalizedMessage());
      }
      try{
        sc.validateImports();
        fail("Test Should have failed");
      }catch(Throwable e)
      {
         log.debug("Test Passed:We got an exception::",e);
         //We are fine
      }
   }

   /**
    * Test two interfaces that have the methods in different order
    */
   public void testDifferentMethodOrder()
   {
      String fname="PrimitiveTypes.java";
      File file1 = getResourceFile("tools/sourcecomp/expected/" + fname);
      File file2 = getResourceFile("tools/sourcecomp/diffmethodorder/" + fname);
      try
      {
         sc= new JBossSourceComparator(file1,file2);
         assertTrue("Source Files Match:",sc.validate());

      }
      catch (Exception e)
      {
         fail(e.getLocalizedMessage());
      }

   }

}
