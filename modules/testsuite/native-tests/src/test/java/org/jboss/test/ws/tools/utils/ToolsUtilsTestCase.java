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
package org.jboss.test.ws.tools.utils;
 
import junit.framework.TestCase;

import org.jboss.ws.tools.ToolsUtils;
 
/**
 * Test ToolsUtils class 
 * ToolsUtils class contains utility methods that deal with
 * JAXB 1.0/2.0 and JAXWS 2.0 Spec compliance
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jun 25, 2005 
 */

public class ToolsUtilsTestCase extends TestCase
{ 
   
   /**
    * Tests whether the character is a punctuation character
    */
   public void testIsPunctuation()
   {
      assertTrue(ToolsUtils.isPunctuation('_'));
      assertTrue(ToolsUtils.isPunctuation('-'));
      assertFalse(ToolsUtils.isPunctuation('A'));
      assertFalse(ToolsUtils.isPunctuation(' ')); //Whitespace
      assertFalse(ToolsUtils.isPunctuation('4'));
   }
   
   /**
    * Tests construction of a Java Identifier
    */
   public void testJavaIdentifier()
   {
      assertEquals("SomeIden", ToolsUtils.getJavaIdentifier("SomeIden_"));
      assertEquals("SomeIden", ToolsUtils.getJavaIdentifier("-SomeIden_"));
      assertEquals("SomeIden", ToolsUtils.getJavaIdentifier("-SomeIden-"));
      assertEquals("SomeIden", ToolsUtils.getJavaIdentifier("_SomeIden-"));
      assertEquals("SomeIden", ToolsUtils.getJavaIdentifier(" SomeIden "));
      assertEquals("SomeIden", ToolsUtils.getJavaIdentifier(" Some%Iden ")); 
   }
   
   /**
    * Tests construction of a Java Class Name 
    */
   public void testValidClassName()
   {
      assertEquals("SomeIden", ToolsUtils.getValidClassName("SomeIden_"));
      assertEquals("SomeIden", ToolsUtils.getValidClassName("someIden_"));
      assertEquals("SomeIden", ToolsUtils.getValidClassName("_someIden_"));
      assertEquals("SomeIden", ToolsUtils.getValidClassName("_some&&Iden_"));
   }
   
   /**
    * Tests construction of a Java Method Name 
    */
   public void testJavaMethodName()
   {
      assertEquals("setSomeIden", ToolsUtils.getJavaMethodName("SomeIden_", true));
      assertEquals("setSomeIden", ToolsUtils.getJavaMethodName("setSomeIden_", true));
      assertEquals("getSomeIden", ToolsUtils.getJavaMethodName("getSomeIden_", false));
      assertEquals("getSomeIden", ToolsUtils.getJavaMethodName("SomeIden_", false));
      assertEquals("setSomeIden", ToolsUtils.getJavaMethodName("someIden_",true));
      assertEquals("getSomeIden", ToolsUtils.getJavaMethodName("someIden_", false));
      assertEquals("setSomeIden", ToolsUtils.getJavaMethodName("_someIden_", true));
      assertEquals("getSomeIden", ToolsUtils.getJavaMethodName("_someIden_", false));
      assertEquals("setSomeIden", ToolsUtils.getJavaMethodName("_some&&Iden_",true)); 
      assertEquals("getSomeIden", ToolsUtils.getJavaMethodName("_some&&Iden_",false));
      assertEquals("setGetsomeIden", ToolsUtils.getJavaMethodName("_Getsome&&Iden_",true)); 
      assertEquals("getsomeIden", ToolsUtils.getJavaMethodName("_Getsome&&Iden_",false));
   } 
}
    

