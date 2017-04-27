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
package org.jboss.ws.core.jaxrpc;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.binding.TypeMappingImpl;
import org.jboss.ws.core.jaxrpc.binding.JBossXBDeserializerFactory;
import org.jboss.ws.core.jaxrpc.binding.JBossXBSerializerFactory;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.core.utils.HolderUtils;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.TypeMappingMetaData;
import org.jboss.ws.metadata.umdm.TypesMetaData;
import org.jboss.ws.metadata.umdm.WrappedParameter;
import org.jboss.wsf.common.JavaUtils;

/** A helper class to wrap/unwrap ducument style request/response structures.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 06-Jun-2005
 */
public class ParameterWrapping
{
   // provide logging
   private static Logger log = Logger.getLogger(ParameterWrapping.class);

   // This assertion should probably be moved somewhere earlier
   private static void assertOperationMetaData(OperationMetaData opMetaData)
   {
      if (opMetaData.getStyle() != Style.DOCUMENT)
         throw new WSException("Unexpected style: " + opMetaData.getStyle());

      if (opMetaData.getParameterStyle() != ParameterStyle.WRAPPED)
         throw new WSException("Unexpected parameter style: " + opMetaData.getParameterStyle());
   }

   private static Object holderValue(Object holder)
   {
      if (holder == null)
         return null;
      if (! HolderUtils.isHolderType(holder.getClass()))
         return holder;

      return HolderUtils.getHolderValue(holder);
   }

   public static Class getWrappedType(String variable, Class wrapperType)
   {
      try
      {
         PropertyDescriptor pd = new PropertyDescriptor(variable, wrapperType);
         Method method = pd.getWriteMethod();
         return method.getParameterTypes()[0];
      }
      catch (Exception ex)
      {
         if(log.isDebugEnabled()) log.debug("Invalid request wrapper: " + ex);
         return null;
      }
   }

   public static Object wrapRequestParameters(ParameterMetaData request, Object[] methodParams)
   {
      assertOperationMetaData(request.getOperationMetaData());

      Class reqStructType = request.getJavaType();
      if(log.isDebugEnabled()) log.debug("wrapRequestParameters: " + reqStructType.getName());
      List<WrappedParameter> wrappedParameters = request.getWrappedParameters();
      try
      {
         Object reqStruct = reqStructType.newInstance();
         for (WrappedParameter param : wrappedParameters)
         {
            Object value = holderValue(methodParams[param.getIndex()]);
            param.accessor().set(reqStruct, value);
         }

         return reqStruct;
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception e)
      {
         throw new WSException("Cannot wrap request structure: " + e);
      }
   }

   public static Map<Integer, Object> unwrapRequestParameters(ParameterMetaData request, Object reqStruct, Object[] methodParams)
   {
      OperationMetaData opMetaData = request.getOperationMetaData();
      assertOperationMetaData(opMetaData);

      if (reqStruct == null)
         throw new IllegalArgumentException("Request struct cannot be null");

      Class[] targetParameterTypes = opMetaData.getJavaMethod().getParameterTypes();
      Map<Integer, Object> outParameters = new HashMap<Integer, Object>(targetParameterTypes.length);
      List<WrappedParameter> wrappedParameters = request.getWrappedParameters();
      Class reqStructType = reqStruct.getClass();

      if(log.isDebugEnabled()) log.debug("unwrapRequestParameters: " + reqStructType.getName());
      try
      {
         for (WrappedParameter param : wrappedParameters)
         {
            Class targetType = targetParameterTypes[param.getIndex()];
            Object value = param.accessor().get(reqStruct);

            // INOUT Parameter
            if (HolderUtils.isHolderType(targetType))
            {
               value = HolderUtils.createHolderInstance(value, targetType);
               outParameters.put(param.getIndex(), value);
            }

            methodParams[param.getIndex()] = value;
         }
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("Cannot unwrap request structure: " + e);
      }

      return outParameters;
   }

   public static Object wrapResponseParameters(ParameterMetaData returnMetaData, Object returnValue, Map<Integer, Object> outParameters)
   {
      assertOperationMetaData(returnMetaData.getOperationMetaData());

      Class resStructType = returnMetaData.getJavaType();
      if (returnValue != null && returnValue.getClass() == resStructType)
      {
         if(log.isDebugEnabled()) log.debug("Response parameter already wrapped" + resStructType.getName());
         return returnValue;
      }

      if(log.isDebugEnabled()) log.debug("wrapResponseParameter: " + resStructType.getName());
      List<WrappedParameter> wrappedParameters = returnMetaData.getWrappedParameters();
      try
      {
         Object resStruct = resStructType.newInstance();

         for (WrappedParameter param : wrappedParameters)
         {
            Object value = (param.getIndex() < 0) ? returnValue : holderValue(outParameters.get(param.getIndex()));          
            param.accessor().set(resStruct, value);
         }
         return resStruct;
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception e)
      {
         throw new WSException("Cannot wrap response structure: " + e);
      }
   }

   public static Object unwrapResponseParameters(ParameterMetaData retMetaData, Object resStruct, Object methodParams[])
   {
      OperationMetaData operationMetaData = retMetaData.getOperationMetaData();
      assertOperationMetaData(operationMetaData);

      Object retValue = null;
      if (resStruct != null)
      {
         Class resStructType = resStruct.getClass();

         if(log.isDebugEnabled()) log.debug("unwrapResponseParameter: " + resStructType.getName());
         List<WrappedParameter> wrappedParameters = retMetaData.getWrappedParameters();
         Class[] targetTypes = operationMetaData.getJavaMethod().getParameterTypes();
         try
         {
            for (WrappedParameter param : wrappedParameters)
            {
               Object value = param.accessor().get(resStruct);
               if (param.getIndex() < 0)
               {
                  retValue = value;
               }
               else
               {
                  Class targetType = targetTypes[param.getIndex()];
                  if (HolderUtils.isHolderType(targetType))
                     HolderUtils.setHolderValue(methodParams[param.getIndex()], value);
                  methodParams[param.getIndex()] = value;
               }
            }
         }
         catch (RuntimeException rte)
         {
            throw rte;
         }
         catch (Exception e)
         {
            throw new IllegalArgumentException("Cannot unwrap request structure: " + e);
         }
      }
      return retValue;
   }

   /**
    * This is just a dummy marker class used to identify a generated
    * document/literal wrapped type
    *
    * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
    */
   public static class WrapperType
   {
   }

   /**
    * Generates a wrapper type and assigns it to the passed ParameterMetaData
    * object. This routine requires the pmd to contain completed wrappedTypes
    * and wrappedVariables properties of the passed ParameterMetaData object.
    *
    * @param pmd a document/literal wrapped parameter
    */
   public static void generateWrapper(ParameterMetaData pmd, boolean addTypeMapping)
   {
      List<WrappedParameter> wrappedParameters = pmd.getWrappedParameters();
      OperationMetaData operationMetaData = pmd.getOperationMetaData();
      EndpointMetaData endpointMetaData = operationMetaData.getEndpointMetaData();
      ServiceMetaData serviceMetaData = endpointMetaData.getServiceMetaData();
      ClassLoader loader = serviceMetaData.getUnifiedMetaData().getClassLoader();

      if (operationMetaData.isDocumentWrapped() == false)
         throw new WSException("Operation is not document/literal (wrapped)");

      if (wrappedParameters == null)
         throw new WSException("Cannot generate a type when their is no wrapped parameters");

      String serviceName = serviceMetaData.getServiceName().getLocalPart();
      String parameterName = pmd.getXmlName().getLocalPart();
      String endpointName = endpointMetaData.getPortName().getLocalPart();
      String packageName = endpointMetaData.getServiceEndpointInterface().getPackage().getName();

      String wrapperName = packageName + "._JBossWS_" + serviceName + "_" + endpointName + "_" + parameterName;
      if(log.isDebugEnabled()) log.debug("Generating wrapper: " + wrapperName);

      Class wrapperType;
      try
      {
         ClassPool pool = new ClassPool(true);
         pool.appendClassPath(new LoaderClassPath(loader));
         CtClass clazz = pool.makeClass(wrapperName);
         clazz.setSuperclass(pool.get(WrapperType.class.getName()));

         for (WrappedParameter param : wrappedParameters)
         {
            CtField field = new CtField(pool.get(param.getType()), param.getVariable(), clazz);
            field.setModifiers(Modifier.PRIVATE);
            clazz.addField(field);
            clazz.addMethod(CtNewMethod.getter("get" + JavaUtils.capitalize(param.getVariable()), field));
            clazz.addMethod(CtNewMethod.setter("set" + JavaUtils.capitalize(param.getVariable()), field));
         }

         wrapperType = (Class)pool.toClass(clazz, loader);
      }
      catch (Exception e)
      {
         throw new WSException("Could not generate wrapper type: " + wrapperName, e);
      }

      // Register type mapping if needed
      if (addTypeMapping)
      {
         QName xmlType = pmd.getXmlType();

         TypesMetaData typesMetaData = serviceMetaData.getTypesMetaData();
         TypeMappingMetaData tmMetaData = new TypeMappingMetaData(typesMetaData, xmlType, wrapperName);
         typesMetaData.addTypeMapping(tmMetaData);

         TypeMappingImpl typeMapping = serviceMetaData.getTypeMapping();
         typeMapping.register(wrapperType, xmlType, new JBossXBSerializerFactory(), new JBossXBDeserializerFactory());
      }

      pmd.setJavaTypeName(wrapperName);
   }
}
