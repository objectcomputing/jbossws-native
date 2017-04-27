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
package org.jboss.ws.extensions.xop.jaxws;

import org.jboss.wsf.common.JavaUtils;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.transform.Source;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans java beans for MTOM and swaRef declarations.<br>
 * It basically searches for
 * <ul>
 * <li><code>@XmlMimeType</code>
 * <li><code>@XmlAttachmentRef</code>
 * </ul>
 * and returns the appropriate mimetype.<br> 
 * In order to re-use an instance of this class you need to invoke <code>reset()</code>
 * in between scans.
 *
 * @author Heiko Braun <heiko.braun@jboss.com> 
 * @since 04.12.2006
 *
 */
public class ReflectiveAttachmentRefScanner {

   private static List<Class> SUPPORTED_TYPES = new ArrayList<Class>(5);

   public static enum ResultType {XOP, SWA_REF};

   static {
      SUPPORTED_TYPES.add(String.class);
      SUPPORTED_TYPES.add(byte[].class);
      SUPPORTED_TYPES.add(Image.class);
      SUPPORTED_TYPES.add(Source.class);
      SUPPORTED_TYPES.add(DataHandler.class);
   }

   private List<Field> scannedFields = new ArrayList<Field>();

   /**
    * Scan java types for MTOM declarations
    *
    * @param xmlRoot
    * @return the first matching XmlMimeType#value() or <code>null</code> if none found
    */
   public AttachmentScanResult scanBean(Class xmlRoot)
   {
      if( isJDKType(xmlRoot) )  return null;

      AttachmentScanResult result = null;

      for(Field field : xmlRoot.getDeclaredFields())
      {
         Class<?> type = field.getType();

         boolean exceptionToTheRule = isAttachmentDataType(type);
         if (! exceptionToTheRule) {
            type = getFieldComponentType(field);
         }
         // only non JDK types are inspected except for byte[] and java.lang.String
         if( !alreadyScanned(field) && (exceptionToTheRule || !isJDKType(type)) )
         {

            // Scan for swa:Ref type declarations first
            if(field.isAnnotationPresent(XmlAttachmentRef.class))
            {
               // arbitrary, it's not used
               result = new AttachmentScanResult("application/octet-stream", AttachmentScanResult.Type.SWA_REF);
            }

            // Scan for XOP field annotations
            else if(field.isAnnotationPresent(XmlMimeType.class))
            {
               XmlMimeType mimeTypeDecl = field.getAnnotation(XmlMimeType.class);
               result = new AttachmentScanResult(mimeTypeDecl.value(), AttachmentScanResult.Type.XOP);
            }

            if(null == result) // try getter methods
            {
               result = scanGetterAnnotation(xmlRoot, field);
            }

            // avoid recursive loops
            if(!isAttachmentDataType(type))
               scannedFields.add(field);

            // drill down if none found so far
            if(null == result)
               result = scanBean(type);

         }

      }

      return result;
   }

   public static List<AttachmentScanResult> scanMethod(Method method)
   {
      List<AttachmentScanResult> results = new ArrayList<AttachmentScanResult>();

      // return type
      if(method.getReturnType() != void.class)
      {

         AttachmentScanResult result = null;

         if(method.isAnnotationPresent(XmlAttachmentRef.class))
         {
            result = new AttachmentScanResult("application/octet-stream", AttachmentScanResult.Type.SWA_REF);
         }
         else if (method.isAnnotationPresent(XmlMimeType.class))
         {
            XmlMimeType mimeTypeDecl = method.getAnnotation(XmlMimeType.class);
            result = new AttachmentScanResult(mimeTypeDecl.value(), AttachmentScanResult.Type.XOP);
         }

         if(result!=null)
         {
            result.setIndex(-1); // default for return values
            results.add(result);
         }

      }

      // method parameter
      int index = 0;
      for (Annotation[] parameterAnnotations : method.getParameterAnnotations())
      {
         if (parameterAnnotations!=null)
         {
            for (Annotation annotation : parameterAnnotations)
            {
               AttachmentScanResult paramResult = null;

               if(XmlAttachmentRef.class == annotation.annotationType())
               {
                  paramResult = new AttachmentScanResult("application/octet-stream", AttachmentScanResult.Type.SWA_REF);
               }
               else if(XmlMimeType.class == annotation.annotationType())
               {
                  XmlMimeType mimeTypeDecl = ((XmlMimeType)annotation);
                  paramResult = new AttachmentScanResult(mimeTypeDecl.value(), AttachmentScanResult.Type.XOP);
               }

               if(paramResult!=null)
               {
                  paramResult.setIndex(index);                  
                  results.add(paramResult);

               }
				}
         }

			index++;
		}


      return results;
   }

   public static AttachmentScanResult getResultByIndex(List<AttachmentScanResult> results, int index)
   {
      AttachmentScanResult result = null;

      for(AttachmentScanResult asr : results)
      {
         if(asr.getIndex() == index)
         {
            result = asr;
            break;
         }
      }

      return result;
   }

   private boolean alreadyScanned(Field field)
   {

      for(Field f : scannedFields)
      {
         if(f.equals(field))
            return true;
      }

      return false;
   }

   public void reset()
   {
      scannedFields.clear();
   }

   /**
    * In the case of an array T[] or a List<T> returns T, else returns the field type
    *
    * @param clazz
    * @return the type of the field, if the field is an array returns the component type,
    * if the field is declared as List<T> returns T
    */
   private Class<?> getFieldComponentType(Field field) {
	   Class<?> fieldType = field.getType();
	   if (fieldType.isArray()) {
		   return fieldType.getComponentType();
	   } else if (List.class.isAssignableFrom(fieldType)) {
		   if (field.getGenericType() instanceof ParameterizedType) {
		   		ParameterizedType paramType = (ParameterizedType) field.getGenericType();
		   		if ((paramType.getRawType() instanceof Class) && List.class.isAssignableFrom((Class<?>) paramType.getRawType())) {
		   			Type[] actualTypes = paramType.getActualTypeArguments();
		   			if (actualTypes.length == 1 && (actualTypes[0] instanceof Class)) {
		   				return (Class<?>) actualTypes[0];
		   			}
		   		}
		   }
	   }
	   return fieldType;
   }

   private static boolean isAttachmentDataType(Class clazz) {
      for(Class cl : SUPPORTED_TYPES)
      {
         if(JavaUtils.isAssignableFrom(cl, clazz))
            return true;
      }

      return false;
   }

   private static boolean isJDKType(Class clazz)
   {
      return clazz.getCanonicalName().startsWith("java") || clazz.isPrimitive();
   }

   private static AttachmentScanResult scanGetterAnnotation(Class owner, Field field)
   {
      String getterMethodName = "get"+field.getName();
      for(Method method : owner.getDeclaredMethods())
      {
         if(method.getName().equalsIgnoreCase(getterMethodName))
         {
            if(method.isAnnotationPresent(XmlMimeType.class))
            {
               XmlMimeType mimeTypeDecl = method.getAnnotation(XmlMimeType.class);
               return new AttachmentScanResult(mimeTypeDecl.value(), AttachmentScanResult.Type.XOP);
            }
            else if(method.isAnnotationPresent(XmlAttachmentRef.class))
            {
               return new AttachmentScanResult("application/octet-stream", AttachmentScanResult.Type.SWA_REF);
            }
         }
      }

      return null;
   }
}
