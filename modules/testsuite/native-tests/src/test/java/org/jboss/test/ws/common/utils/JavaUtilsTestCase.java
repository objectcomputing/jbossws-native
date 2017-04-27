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
package org.jboss.test.ws.common.utils;

import org.jboss.test.ws.tools.jbws_161.custom.HelloObj;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.JavaUtils;

/**
 * Test the JavaUtils
 *
 * @author Thomas.Diesler@jboss.org
 * @since 22-Jun-2005
 */
public class JavaUtilsTestCase extends JBossWSTest
{
   public void testLoadArrayType() throws Exception
   {
      String javaTypeName = HelloObj.class.getName();
      Class javaType = JavaUtils.loadJavaType(javaTypeName + "[]");
      assertNotNull("Unexpected null type", javaType);
      assertEquals("[L" + javaTypeName + ";", javaType.getName());
   }

   public void testGetMultipleArrayWrapperValue() throws Exception
   {
      int[][] primParams = new int[][] { { 0, 1, 2 }, { 1, 2, 3 }, { 2, 3, 4 } };
      Integer[][] params = (Integer[][])JavaUtils.getWrapperValueArray(primParams);
      assertNotNull("Unexpected null", params);
      assertEquals(primParams[0], (int[])JavaUtils.getPrimitiveValueArray(params[0]));
      assertEquals(primParams[1], (int[]) JavaUtils.getPrimitiveValueArray(params[1]));
      assertEquals(primParams[2], (int[])JavaUtils.getPrimitiveValueArray(params[2]));
   }

   public void testGetMultipleArrayPrimitiveValue() throws Exception
   {
      Integer[][] wrapParams = new Integer[][] { { new Integer(0), new Integer(1), new Integer(2) }, { new Integer(1), new Integer(2), new Integer(3) },
            { new Integer(2), new Integer(3), new Integer(4) } };
      int[][] params = (int[][])JavaUtils.getPrimitiveValueArray(wrapParams);
      assertNotNull("Unexpected null", params);
      assertEquals(wrapParams[0], (Integer[])JavaUtils.getWrapperValueArray(params[0]));
      assertEquals(wrapParams[1], (Integer[])JavaUtils.getWrapperValueArray(params[1]));
      assertEquals(wrapParams[2], (Integer[])JavaUtils.getWrapperValueArray(params[2]));
   }

   public void testGetPrimitiveType() throws Exception
   {
      assertEquals(int.class, JavaUtils.getPrimitiveType("int"));
      assertEquals(int[].class, JavaUtils.getPrimitiveType("int[]"));
      assertEquals(int[][].class, JavaUtils.getPrimitiveType("int[][]"));
      assertEquals(int[].class, JavaUtils.getPrimitiveType("[I"));
      assertEquals(int[][].class, JavaUtils.getPrimitiveType("[[I"));
   }
}
