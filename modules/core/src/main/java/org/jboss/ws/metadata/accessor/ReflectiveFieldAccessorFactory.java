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
package org.jboss.ws.metadata.accessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jboss.ws.WSException;
import org.jboss.ws.metadata.umdm.WrappedParameter;

final class ReflectiveFieldAccessorFactory implements AccessorFactory
{
   private final Class clazz;

   ReflectiveFieldAccessorFactory(Class clazz)
   {
      this.clazz = clazz;
   }

   public Accessor create(WrappedParameter parameter)
   {
      String fieldName = parameter.getVariable();
      try
      {
         Field field;
   
         try
         {
            field = clazz.getField(fieldName);
         }
         catch (NoSuchFieldException e)
         {
            // Search for a private field
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
         }
   
         if (Modifier.isStatic(field.getModifiers()))
            throw new WSException("Field can not be static: " + fieldName);
   
         return new ReflectiveFieldAccessor(field);
      }
      catch (Throwable t)
      {
         WSException ex = new WSException("Error accessing field: " + fieldName + t.getClass().getSimpleName() + ": " + t.getMessage());
         ex.setStackTrace(t.getStackTrace());
         throw ex;
      }
   }
}
