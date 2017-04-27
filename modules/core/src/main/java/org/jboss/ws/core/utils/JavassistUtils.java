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
package org.jboss.ws.core.utils;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * Utility functions that simplify Javassist.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class JavassistUtils
{
   public static void addFieldAnnotation(CtField field, javassist.bytecode.annotation.Annotation annotation)
   {
      FieldInfo fieldInfo = field.getFieldInfo();
      AnnotationsAttribute attribute = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
      if (attribute == null)
         attribute = new AnnotationsAttribute(fieldInfo.getConstPool(), AnnotationsAttribute.visibleTag);
      attribute.addAnnotation(annotation);
      fieldInfo.addAttribute(attribute);
   }

   public static void addClassAnnotation(CtClass clazz, javassist.bytecode.annotation.Annotation annotation)
   {
      ClassFile classFile = clazz.getClassFile();
      AnnotationsAttribute attribute = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
      if (attribute == null)
         attribute = new AnnotationsAttribute(classFile.getConstPool(), AnnotationsAttribute.visibleTag);
      attribute.addAnnotation(annotation);
      classFile.addAttribute(attribute);
   }

   public static Annotation createAnnotation(Class<? extends java.lang.annotation.Annotation> annotation, ConstPool constPool)
   {
      return new Annotation(annotation, constPool);
   }

   public static void addSignature(CtField field, String signature)
   {
      FieldInfo fieldInfo = field.getFieldInfo();
      ConstPool constPool = fieldInfo.getConstPool();
      SignatureAttribute signatureAttribute = new SignatureAttribute(constPool, signature);
      fieldInfo.addAttribute(signatureAttribute);
   }

   public static void addSignature(CtMethod method, String signature)
   {
      MethodInfo methodInfo = method.getMethodInfo();
      ConstPool constPool = methodInfo.getConstPool();
      SignatureAttribute signatureAttribute = new SignatureAttribute(constPool, signature);
      methodInfo.addAttribute(signatureAttribute);
   }

   public static class Annotation
   {
      private javassist.bytecode.annotation.Annotation annotation;
      private ConstPool constPool;

      public Annotation(Class<? extends java.lang.annotation.Annotation> annotation, ConstPool constPool)
      {
         this.annotation = new javassist.bytecode.annotation.Annotation(annotation.getName(), constPool);
         this.constPool = constPool;
      }

      public void addParameter(String name, String value)
      {
         annotation.addMemberValue(name, new StringMemberValue(value, constPool));
      }

      public void addParameter(String name, Enum value)
      {
         EnumMemberValue enumValue = new EnumMemberValue(constPool);
         enumValue.setType(value.getClass().getName());
         enumValue.setValue(value.name());
         annotation.addMemberValue(name, enumValue);
      }
      
      public void addClassParameter(String name, String value)
      {
         ClassMemberValue classValue = new ClassMemberValue(value, constPool);
         annotation.addMemberValue(name, classValue);
      }

      public void addParameter(String name, String[] values)
      {
         ArrayMemberValue member = new ArrayMemberValue(constPool);
         StringMemberValue[] members = new StringMemberValue[values.length];
         for (int i = 0; i < values.length; i++)
            members[i] = new StringMemberValue(values[i], constPool);
         member.setValue(members);
         annotation.addMemberValue(name, member);
      }

      public void markClass(CtClass clazz)
      {
         addClassAnnotation(clazz, annotation);
      }

      public void markField(CtField field)
      {
         addFieldAnnotation(field, annotation);
      }
   }
}
