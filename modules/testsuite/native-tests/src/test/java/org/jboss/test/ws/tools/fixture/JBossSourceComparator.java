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
package org.jboss.test.ws.tools.fixture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.logging.Logger;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

/**
 *  Compares whether two Java source files are identical
 *  Uses QDox.  [Based on APITestCase of QDox Testsuite]
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Mar 7, 2005
 */
public class JBossSourceComparator
{
   // provide logging
   private static Logger log = Logger.getLogger(JBossSourceComparator.class);
   private JavaDocBuilder builder = null;
   private JavaSource source1 = null;
   private JavaSource source2 = null;

   /*
    * Needed to sort JavaClass, JavaField and JavaMethod as they
    * don't implement Comparable
    */

   private static Comparator ENTITY_COMPARATOR = new Comparator() {
      public int compare(Object obj1, Object obj2)
      {
         AbstractJavaEntity entity1 = (AbstractJavaEntity)obj1;
         AbstractJavaEntity entity2 = (AbstractJavaEntity)obj2;
         return entity1.getName().compareTo(entity2.getName());
      }
   };

   public JBossSourceComparator()
   {
      if (builder == null)
         builder = new JavaDocBuilder();
   }

   public JBossSourceComparator(File dir)
   {
      this();
      builder.addSourceTree(dir);

   }

   public JBossSourceComparator(String file1, String file2) throws Exception
   {
      this();
      try
      {
         source1 = builder.addSource(new FileReader(file1));
         source2 = builder.addSource(new FileReader(file2));
      }
      catch (FileNotFoundException e)
      {
         log.debug("Validation Failed:", e);
         throw e;
      }
   }

   public JBossSourceComparator(File file1, File file2) throws Exception
   {
      this();
      try
      {
         source1 = builder.addSource(file1);
         source2 = builder.addSource(file2);
      }
      catch (FileNotFoundException e)
      {
         log.debug("Validation Failed:", e);
         throw e;
      }
      catch (UnsupportedEncodingException e)
      {
         log.debug("Validation Failed:", e);
         throw e;
      }
   }

   /**
    * Needs to be called seperately if you are interested in
    * checking the imports.
    * Only checks on the exact imports.
    * java.util.* and java.util.Vector are not exact types.
    * @throws Exception
    */
   public void validateImports() throws Exception
   {
      String[] imp1 = source1.getImports();
      String[] imp2 = source2.getImports();

      if (imp1 == null && imp2 == null)
         return;
      if (imp1 == null && imp2 != null)
         throw new Exception("Imports are not equal");
      if (imp1 != null && imp2 == null)
         throw new Exception("Imports are not equal");
      //Compare the arrays
      int lenA = imp1.length;
      int lenB = imp2.length;
      if (lenA != lenB)
         throw new Exception("Imports are not equal");
      if (!Arrays.equals(imp1, imp2))
         throw new Exception("Imports are not equal");
   }

   /**
    * Validates the equality of two source files.
    * Does not validate imports (@see validateImports())
    * @return
    * @throws Exception
    */
   public boolean validate() throws Exception
   {
      boolean result = true;
      if (source1 == null)
         throw new IllegalArgumentException("First Java Source file missing");

      if (source2 == null)
         throw new IllegalArgumentException("Second Java Source file missing");

      try
      {
         validateAPI(source1, source2);
      }
      catch (Exception e)
      {
         log.error("Validation Failed:", e);
         result = false;
         throw e;
      }
      return result;
   }

   public boolean validateAPI(JavaSource expected, JavaSource actual) throws Exception
   {
      boolean result = false;
      List expectedClasses = Arrays.asList(expected.getClasses());
      Collections.sort(expectedClasses, ENTITY_COMPARATOR);
      List actualClasses = Arrays.asList(actual.getClasses());
      Collections.sort(actualClasses, ENTITY_COMPARATOR);

      //Check number of classes
      if (expectedClasses.size() != actualClasses.size())
         return (result = false);

      for (int i = 0; i < expectedClasses.size(); i++)
      {
         checkClassesEqual((JavaClass)expectedClasses.get(i), (JavaClass)actualClasses.get(i));
      }

      return result;
   }

   private void checkClassesEqual(JavaClass expected, JavaClass actual) throws Exception
   {
      if (!expected.getPackage().equals(actual.getPackage()))
         throw new Exception("Package names should be equal");
      checkModifiersEquals("Class modifiers should be equal", expected, actual);
      if (!expected.getName().equals(actual.getName()))
         throw new Exception("Class names should be equal");
      if ((expected.getSuperJavaClass() != null) && (actual.getSuperJavaClass() != null))
      {
         String expSuperClassName = expected.getSuperJavaClass().getName();
         if (expected.getPackage().length() > 0 && expSuperClassName.startsWith(expected.getPackage()))
            expSuperClassName = expSuperClassName.substring(expected.getPackage().length() + 1);
         String actSuperClassName = actual.getSuperJavaClass().getName();
         if (actual.getPackage().length() > 0 && actSuperClassName.startsWith(actual.getPackage()))
            actSuperClassName = actSuperClassName.substring(actual.getPackage().length() + 1);
         if (!expSuperClassName.equals(actSuperClassName))
            throw new Exception("Super class should be equal");
      }
      if ((expected.getSuperJavaClass() == null) ^ (actual.getSuperJavaClass() == null))
      {
         throw new Exception("Super class should be equal");
      }
      checkInterfacesEqual(expected, actual);
      checkInnerClassesEquals(expected, actual);
      checkFieldsEqual(expected, actual);
      checkMethodsEqual(expected, actual);

   }

   private void checkFieldEquals(JavaField expected, JavaField actual) throws Exception
   {
      StringBuffer message = new StringBuffer("-> checkFieldEquals");
      message.append(" Expected : ");
      message.append(expected);
      message.append(" Actual : ");
      message.append(actual);

      if (!expected.getType().equals(actual.getType()))
         throw new Exception(message.toString() + "Field types should be equal");

      if (!expected.getName().equals(actual.getName()))
         throw new Exception(message.toString() + "Field names should be equal");

      checkModifiersEquals(message.toString() + "Field modifiers should be equal", expected, actual);
   }

   private void checkFieldsEqual(JavaClass expected, JavaClass actual) throws Exception
   {
      List expectedFields = Arrays.asList(expected.getFields());
      Collections.sort(expectedFields, ENTITY_COMPARATOR);
      List actualFields = Arrays.asList(actual.getFields());
      Collections.sort(actualFields, ENTITY_COMPARATOR);

      StringBuffer message = new StringBuffer("-> checkFieldsEqual");
      message.append(" Expected : ");
      message.append(expectedFields);
      message.append(" Actual : ");
      message.append(actualFields);

      if (expectedFields.size() != actualFields.size())
         throw new Exception(message.toString() + "Number of fields should be equal");

      for (int i = 0; i < expectedFields.size(); i++)
      {
         checkFieldEquals((JavaField)expectedFields.get(i), (JavaField)actualFields.get(i));
      }
   }

   private void checkInnerClassesEquals(JavaClass expected, JavaClass actual) throws Exception
   {
      List expectedInnerClasses = Arrays.asList(expected.getInnerClasses());
      Collections.sort(expectedInnerClasses, ENTITY_COMPARATOR);
      List actualInnerClasses = Arrays.asList(actual.getInnerClasses());
      Collections.sort(actualInnerClasses, ENTITY_COMPARATOR);

      StringBuffer message = new StringBuffer("-> checkInnerClassesEquals");
      message.append(" Expected : ");
      message.append(expectedInnerClasses);
      message.append(" Actual : ");
      message.append(actualInnerClasses);

      if (expectedInnerClasses.size() != actualInnerClasses.size())
         throw new Exception(message.toString() + "Number of inner classes should be equal");
      for (int i = 0; i < expectedInnerClasses.size(); i++)
      {
         checkClassesEqual((JavaClass)expectedInnerClasses.get(i), (JavaClass)actualInnerClasses.get(i));
      }

   }

   private void checkInterfacesEqual(JavaClass expected, JavaClass actual) throws Exception
   {
      List expectedImplements = Arrays.asList(expected.getImplements());
      Collections.sort(expectedImplements);
      List actualImplements = Arrays.asList(actual.getImplements());
      Collections.sort(actualImplements);
      StringBuffer message = new StringBuffer("-> checkInterfacesEqual");
      message.append(" Expected : ");
      message.append(expectedImplements);
      message.append(" Actual : ");
      message.append(actualImplements);

      if (expectedImplements.size() != actualImplements.size())
         throw new Exception(message.toString() + "Number of implemented interface should be equal");

      for (int i = 0; i < expectedImplements.size(); i++)
      {
         if (!expectedImplements.get(i).equals(actualImplements.get(i)))
            throw new Exception("Implemented interface should be equal");
      }
   }

   private void checkMethodsEqual(JavaClass expected, JavaClass actual) throws Exception
   {
      List expectedMethods = Arrays.asList(expected.getMethods());
      Collections.sort(expectedMethods, ENTITY_COMPARATOR);
      List actualMethods = Arrays.asList(actual.getMethods());
      Collections.sort(actualMethods, ENTITY_COMPARATOR);

      StringBuffer message = new StringBuffer("-> checkMethodsEqual");
      message.append(" Expected : ");
      message.append(expectedMethods);
      message.append(" Actual : ");
      message.append(actualMethods);

      if (expectedMethods.size() != actualMethods.size())
         throw new Exception(message.toString() + "Number of methods should be equal");
      for (int i = 0; i < expectedMethods.size(); i++)
      {
         JavaMethod expectedMethod = (JavaMethod)expectedMethods.get(i);
         JavaMethod actualMethod = (JavaMethod)actualMethods.get(i);

         if (expectedMethod.equals(actualMethod) == false)
         {
            throw new Exception("Method " + expectedMethod + "and " + actualMethod + " should be equal");
         }

         checkExceptionsEqual(expectedMethod, actualMethod);
      }
   }

   private void checkExceptionsEqual(JavaMethod expected, JavaMethod actual) throws Exception
   {
      List expectedExceptions = Arrays.asList(expected.getExceptions());
      Collections.sort(expectedExceptions);
      List actualExceptions = Arrays.asList(actual.getExceptions());
      Collections.sort(actualExceptions);

      StringBuffer message = new StringBuffer("-> checkExceptionsEqual");
      message.append(" Expected : ");
      message.append(expectedExceptions);
      message.append(" Actual : ");
      message.append(actualExceptions);

      if (expectedExceptions.size() != actualExceptions.size())
      {
         throw new Exception(message + " Number of exceptions should be equal");
      }

      for (int j = 0; j < expectedExceptions.size(); j++)
      {
         Type expectedException = (Type)expectedExceptions.get(j);
         Type actualException = (Type)actualExceptions.get(j);

         if (expectedException.equals(actualException) == false)
         {
            throw new Exception("Exception " + expectedException + " and " + actualException + " should be equal for method " + expected);
         }
      }
   }

   private void checkModifiersEquals(String msg, AbstractJavaEntity expected, AbstractJavaEntity actual) throws Exception
   {

      List expectedModifiers = Arrays.asList(expected.getModifiers());
      Collections.sort(expectedModifiers);
      List actualModifiers = Arrays.asList(actual.getModifiers());
      Collections.sort(actualModifiers);
      StringBuffer message = new StringBuffer("-> checkModifiersEquals");

      message.append(" Expected : ");
      message.append(expectedModifiers);
      message.append(" Actual : ");

      message.append(actualModifiers);
      if (expectedModifiers.size() != actualModifiers.size())
         throw new Exception(message.toString() + msg + " Number of modifiers should be equal ");

      for (int i = 0; i < expectedModifiers.size(); i++)
      {

         if (!expectedModifiers.get(i).equals(actualModifiers.get(i)))
            throw new Exception(msg + message.toString() + "Modifier should be equal");
      }
   }

}
