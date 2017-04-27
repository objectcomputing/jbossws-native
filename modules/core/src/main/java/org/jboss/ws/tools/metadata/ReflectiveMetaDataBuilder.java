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
package org.jboss.ws.tools.metadata;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.holders.Holder;

import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.ParameterWrapping;
import org.jboss.ws.core.utils.HolderUtils;
import org.jboss.ws.metadata.umdm.FaultMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.umdm.WrappedParameter;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.tools.ToolsUtils;
import org.jboss.ws.tools.Configuration.OperationConfig;
import org.jboss.wsf.common.JavaUtils;

/**
 * Builds the Tools Endpoint Meta Data using Java Reflection
 *
 * @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 * @since Oct 19, 2005
 */
public class ReflectiveMetaDataBuilder
{
   private Class seiClass = null;

   private ToolsEndpointMetaData tmd = null;

   private String targetNamespace = null;

   private Map<String, List<OperationConfig>> operationMap = null;

   private Map<String, Integer> operationNameCount = new HashMap<String, Integer>();

   public ReflectiveMetaDataBuilder(ToolsEndpointMetaData tmd)
   {
      this.seiClass = tmd.getServiceEndpointInterface();
      checkServiceEndpointInterface();
      this.targetNamespace = tmd.getPortName().getNamespaceURI();
      this.tmd = tmd;
   }

   public void setOperationMap(Map<String, List<OperationConfig>> operationMap)
   {
      this.operationMap = operationMap;
   }

   public ToolsEndpointMetaData generate()
   {
      generateOperationMetaData(seiClass.getMethods());
      return tmd;
   }

   // PRIVATE METHODS
   private void checkServiceEndpointInterface()
   {
      if (seiClass == null)
         throw new IllegalArgumentException("Illegal Null Argument: seiClass");
      if (seiClass.isInterface() == false)
         throw new IllegalArgumentException("Illegal seiClass : not an interface");
      if (! Remote.class.isAssignableFrom(seiClass))
         throw new WSException("A service endpoint interface MUST extend java.rmi.Remote: " + seiClass.getName());
   }

   private FaultMetaData getFaultMetaData(Class exType, OperationMetaData om)
   {
      String exname = WSDLUtils.getInstance().getJustClassName(exType);
      QName xmlName = new QName(tmd.typeNamespace, exname);

      FaultMetaData fm = new FaultMetaData(om, xmlName, xmlName, exType.getName());
      return fm;
   }

   private OperationConfig getOperationConfig(String name, Class<?>[] types)
   {
      if (operationMap == null)
         return null;

      List<OperationConfig> configs = operationMap.get(name);
      if (configs == null)
         return null;

      for (OperationConfig config : configs)
      {
         if (config.params.size() == types.length)
         {
            int i;
            for (i = 0; i < types.length; i++)
            {
               String typeName = config.params.get(i).javaType;
               if (! JavaUtils.getSourceName(types[i]).equals(typeName))
                  break;
            }

            if (i == types.length)
               return config;
         }
      }

      return null;
   }

   private void generateOperationMetaData(Method[] marr)
   {
      if (marr == null)
         throw new WSException("Number of methods in the seiClass is zero");

      for (Method m : marr)
      {
         String methodname = m.getName();
         Class[] paramTypes = m.getParameterTypes();
         int len = paramTypes.length;

         OperationMetaData om = getOperationMetaData(m);
         OperationConfig opc = getOperationConfig(methodname, m.getParameterTypes());
         if (opc != null)
            om.setOneWay(opc.isOneWay);

         ParameterMetaData wrappedParameter = null;
         List<WrappedParameter> wrappedParameters = null;

         if (om.isDocumentWrapped())
         {
            QName xmlName = new QName(tmd.typeNamespace, om.getQName().getLocalPart());
            QName xmlType = xmlName;

            wrappedParameter = new ParameterMetaData(om, xmlName, xmlType, null);
            wrappedParameters = new ArrayList<WrappedParameter>(len);
            wrappedParameter.setWrappedParameters(wrappedParameters);

            om.addParameter(wrappedParameter);

            if (! om.isOneWay())
            {
               xmlType = xmlName = new QName(tmd.typeNamespace, om.getResponseName().getLocalPart());
               ParameterMetaData retMetaData = new ParameterMetaData(om, xmlName, xmlType, null);
               retMetaData.setWrappedParameters(new ArrayList<WrappedParameter>(0));
               om.setReturnParameter(retMetaData);
            }
         }

         for (int i = 0; i < len; i++)
         {
            Class paramType = paramTypes[i];
            /**
             * Test if method param extends java.rmi.Remote
             */
            if (Remote.class.isAssignableFrom(paramType))
               throw new WSException("Param Type " + paramType.getName() + " should not extend java.rmi.Remote");

            if (om.isDocumentWrapped() && !isHeaderParameter(opc, i))
            {
               QName xmlName = getXmlName(paramType, opc, i, null);
               wrappedParameters.add(new WrappedParameter(xmlName, paramType.getName(), convertToProperty(xmlName.getLocalPart()), i));
            }
            else
            {
               om.addParameter(getParameterMetaData(paramType, om, opc, i));
            }
         }

         // Handle return type
         Class returnType = m.getReturnType();
         if (void.class != returnType)
         {
            if (Remote.class.isAssignableFrom(returnType))
               throw new WSException("Return Type " + returnType.getName() + " should not extend java.rmi.Remote");

            if (om.isDocumentWrapped())
            {
               QName name = getReturnXmlName(opc, null);

               WrappedParameter wrapped = new WrappedParameter(name, returnType.getName(), convertToProperty(name.getLocalPart()), -1);
               ParameterMetaData retMetaData = om.getReturnParameter();
               retMetaData.getWrappedParameters().add(wrapped);
            }
            else
            {
               om.setReturnParameter(getParameterMetaDataForReturnType(returnType, om, opc));
            }
         }

         if (om.isDocumentWrapped())
         {
            ParameterWrapping.generateWrapper(wrappedParameter, false);
            if (! om.isOneWay())
               ParameterWrapping.generateWrapper(om.getReturnParameter(), false);
         }

         Class[] exceptionTypes = m.getExceptionTypes();
         boolean remoteExceptionFound = false;
         if (exceptionTypes != null)
         {
            for (int i = 0; i < exceptionTypes.length; i++)
            {
               if (RemoteException.class.isAssignableFrom(exceptionTypes[i]))
                  remoteExceptionFound = true;
               else
                  om.addFault(getFaultMetaData(exceptionTypes[i], om));
            }
         }

         if (! remoteExceptionFound)
            throw new WSException(m.getName() + " does not throw RemoteException.");

         om.assertDocumentBare();
         tmd.addOperation(om);
      }
   }

   private OperationMetaData getOperationMetaData(Method m)
   {
      String methodName = m.getName();
      int count = 0;
      if (operationNameCount.containsKey(methodName))
         count = operationNameCount.get(methodName);

      operationNameCount.put(methodName, ++count);

      String localName = (count > 1) ? methodName + count : methodName;
      OperationMetaData om = new OperationMetaData(tmd, new QName(targetNamespace, localName), methodName);
      om.setSOAPAction("");// FIXME:Default SOAP Action
      return om;
   }

   private boolean isHeaderParameter(OperationConfig config, int index)
   {
      if (config == null)
         return false;

      return config.params.get(index).header;
   }

   private ParameterMetaData getParameterMetaData(Class type, OperationMetaData om, OperationConfig config, int index)
   {
      QName xmlType = ToolsUtils.getXMLType(type, tmd.typeNamespace);
      QName xmlName = getXmlName(type, config, index, om.isDocumentBare() ? om.getQName().getLocalPart() : null);
      ParameterMetaData pm = new ParameterMetaData(om, xmlName, xmlType, type.getName());

      pm.setInHeader(isHeaderParameter(config, index));

      String mode = null;
      if (config != null)
         mode = config.params.get(index).mode;

      boolean holder = Holder.class.isAssignableFrom(type);

      if (holder)
      {
         pm.setJavaTypeName(HolderUtils.getValueType(type).getName());
         if (mode != null)
         {
            if (mode.equals("OUT"))
               pm.setMode(ParameterMode.OUT);
            else if (mode.equals("INOUT"))
               pm.setMode(ParameterMode.INOUT);
         }
         else
         {
            pm.setMode(ParameterMode.INOUT);
         }
      }

      return pm;
   }

   private QName getXmlName(Class type, OperationConfig config, int index, String defaultName)
   {
      if (config != null)
      {
         QName name = config.params.get(index).xmlName;
         if (name != null)
         {
            if ("".equals(name.getNamespaceURI()))
               name = new QName(tmd.typeNamespace, name.getLocalPart());

            return name;
         }
      }

      if (defaultName == null)
    	  defaultName = getDefaultName(type) + "_" + (index + 1);

      QName xmlName = new QName(tmd.typeNamespace, defaultName);
      return xmlName;
   }

   private QName getReturnXmlName(OperationConfig config, String defaultName)
   {
      if (config != null)
      {
         QName name = config.returnXmlName;
         if (name != null)
         {
            if ("".equals(name.getNamespaceURI()))
               name = new QName(tmd.typeNamespace, name.getLocalPart());

            return name;
         }
      }

      if (defaultName == null)
    	  defaultName = Constants.DEFAULT_RPC_RETURN_NAME;

      return new QName(tmd.typeNamespace, defaultName);
   }


   private ParameterMetaData getParameterMetaDataForReturnType(Class type, OperationMetaData om, OperationConfig config)
   {
      QName xmlType = ToolsUtils.getXMLType(type, tmd.typeNamespace);
      QName xmlName = getReturnXmlName(config, om.isDocumentBare() ? om.getResponseName().getLocalPart() : null);
      ParameterMetaData pm = new ParameterMetaData(om, xmlName, xmlType, type.getName());

      return pm;
   }

   private String getDefaultName(Class javaClass)
   {
      String name = "";
      WSDLUtils utils = WSDLUtils.getInstance();
      if (Holder.class.isAssignableFrom(javaClass))
         javaClass = utils.getJavaTypeForHolder(javaClass);
      if (javaClass.isArray())
      {
         name = utils.getMessagePartForArray(javaClass);
      }
      else
         name = utils.getJustClassName(javaClass);
      return name;
   }

   private String convertToProperty(String variable)
   {
      if (Character.isUpperCase(variable.charAt(0)))
      {
         char c = Character.toLowerCase(variable.charAt(0));
         StringBuilder builder = new StringBuilder(variable);
         builder.setCharAt(0, c);
         variable = builder.toString();
      }

      return variable;
   }
}
