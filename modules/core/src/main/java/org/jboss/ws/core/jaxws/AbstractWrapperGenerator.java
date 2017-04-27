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
package org.jboss.ws.core.jaxws;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Base class for JAX-WS wrapper generation.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public abstract class AbstractWrapperGenerator implements WrapperGenerator
{
   private static Set<String> excludedGetters;
   protected ClassLoader loader;

   public AbstractWrapperGenerator(ClassLoader loader)
   {
      this.loader = loader;
   }
   
   public void reset(ClassLoader loader)
   {
      this.loader = loader;
   }
   
   static
   {
      excludedGetters = new HashSet<String>(4);
      excludedGetters.add("getCause");
      excludedGetters.add("getClass");
      excludedGetters.add("getLocalizedMessage");
      excludedGetters.add("getStackTrace");
   }

   protected SortedMap<String, ExceptionProperty> getExceptionProperties(Class<?> exception)
   {
      if (! Exception.class.isAssignableFrom(exception))
         throw new IllegalArgumentException("Not an exception");

      TreeMap<String, ExceptionProperty> sortedGetters = new TreeMap<String, ExceptionProperty>();
      
      for (Method method : exception.getMethods())
      {
         if (java.lang.reflect.Modifier.isStatic(method.getModifiers()))
            continue;

         Class<?> returnType = method.getReturnType();
         if (returnType == void.class)
            continue;

         String name = method.getName();
         if (excludedGetters.contains(name))
            continue;

         boolean isTransient = method.isAnnotationPresent(XmlTransient.class);
         
         int offset;
         if (name.startsWith("get"))
            offset = 3;
         else if (name.startsWith("is"))
            offset = 2;
         else
            continue;

         name = Introspector.decapitalize(name.substring(offset));
         
         if (!isTransient)
         {
            try
            {
               Field field = exception.getDeclaredField(name);
               isTransient = field.getAnnotation(XmlTransient.class) != null;
            }
            catch (Exception e) {}
         }
         
         sortedGetters.put(name, new ExceptionProperty(name, returnType, isTransient));
      }

      return sortedGetters;
   }
   
   protected class ExceptionProperty
   {
      private String name;
      private Class<?> returnType;
      private boolean transientAnnotated;
      
      public ExceptionProperty()
      {
         
      }
      
      public ExceptionProperty(String name, Class<?> returnType, boolean transientAnnotated)
      {
         this.name = name;
         this.returnType = returnType;
         this.transientAnnotated = transientAnnotated;
      }
      
      public String getName()
      {
         return name;
      }
      public void setName(String name)
      {
         this.name = name;
      }
      public Class<?> getReturnType()
      {
         return returnType;
      }
      public void setReturnType(Class<?> returnType)
      {
         this.returnType = returnType;
      }
      public boolean isTransientAnnotated()
      {
         return transientAnnotated;
      }
      public void setTransientAnnotated(boolean transientAnnotated)
      {
         this.transientAnnotated = transientAnnotated;
      }
   }
}
