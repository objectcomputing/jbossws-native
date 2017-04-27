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
package org.jboss.test.ws.tools.validation;

import java.io.File;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.metadata.jaxrpcmapping.ExceptionMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMappingFactory;
import org.jboss.ws.metadata.jaxrpcmapping.JavaXmlTypeMapping;
import org.jboss.ws.metadata.jaxrpcmapping.MethodParamPartsMapping;
import org.jboss.ws.metadata.jaxrpcmapping.PackageMapping;
import org.jboss.ws.metadata.jaxrpcmapping.PortMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceEndpointInterfaceMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceEndpointMethodMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceInterfaceMapping;
import org.jboss.ws.metadata.jaxrpcmapping.VariableMapping;
import org.jboss.ws.metadata.jaxrpcmapping.WsdlMessageMapping;
import org.jboss.ws.metadata.jaxrpcmapping.WsdlReturnValueMapping;
import org.jboss.ws.metadata.wsdl.WSDLUtils;

/**
 *  Validates a JAXRPC Mapping File against another
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Sep 6, 2005
 */
public class JaxrpcMappingValidator
{
   public static Logger log = Logger.getLogger(JaxrpcMappingValidator.class);

   /**
    * Validates two jaxrpc mapping files given their location as strings
    * 
    * @param mappingFile1 location of the first mapping file
    * @param mappingFile2 location of the second mapping file
    * @return true - both are equal, false - Do not match
    * @throws Exception
    */
   public boolean validate(String mappingFile1, String mappingFile2) throws Exception
   {
      JavaWsdlMappingFactory mappingFactory = JavaWsdlMappingFactory.newInstance();
      JavaWsdlMapping javaWsdlMapping1 = mappingFactory.parse( new File(mappingFile1).toURL());
      JavaWsdlMapping javaWsdlMapping2 = mappingFactory.parse( new File(mappingFile2).toURL());
      return validate(javaWsdlMapping1, javaWsdlMapping2);
   }

   /**
    * Validates two mapping metadata models
    * 
    * @param jw1 metadata model for the first mapping file
    * @param jw2 metadata model for the second mapping file
    * @return
    */
   public boolean validate(JavaWsdlMapping jw1, JavaWsdlMapping jw2)
   {
      boolean bool = true;
      bool = validatePackageMappings(jw1.getPackageMappings(), jw2.getPackageMappings());
      checkBool(bool, "PackageMappings");

      bool = validateJavaXmlTypeMappings(jw1.getJavaXmlTypeMappings(), jw2.getJavaXmlTypeMappings());
      checkBool(bool, "JavaXmlTypeMappings");

      bool = validateExceptionMapping(jw1.getExceptionMappings(), jw2.getExceptionMappings());
      checkBool(bool, "ExceptionMappings");

      bool = validateServiceInterfaceMappings(jw1.getServiceInterfaceMappings(), jw2.getServiceInterfaceMappings());
      checkBool(bool, "ServiceInterfaceMappings");

      bool = validateServiceEndpointInterfaceMappings(jw1.getServiceEndpointInterfaceMappings(), jw2.getServiceEndpointInterfaceMappings());
      checkBool(bool, "ServiceEndpointInterfaceMappings");

      return bool;
   }

   private void checkBool(final boolean bool, final String check)
   {
      if (bool == false)
      {
         throw new IllegalStateException("Validation of " + check + " failed.");
      }
   }

   //PRIVATE METHODS
   private boolean validatePackageMappings(PackageMapping[] pm1, PackageMapping[] pm2)
   {
      boolean bool = true;
      int len1 = pm1 != null ? pm1.length : 0;
      int len2 = pm2 != null ? pm2.length : 0;
      if (len1 != len2)
         throw new IllegalStateException("Number of package mappings does not match expected=" + len1 + " actual=" + len2);

      for (int i = 0; i < len1; i++)
      {
         bool = validatePackageMapping(pm1[i], pm2[i]);
         if (bool == false)
            break;
      }
      return bool;
   }

   private boolean validatePackageMapping(PackageMapping pm1, PackageMapping pm2)
   {
      String expectedPackage = pm1.getPackageType();
      String actualPackage = pm2.getPackageType();

      if (checkStringEquality(expectedPackage, actualPackage) == false)
      {
         throw new IllegalStateException("Package type '" + expectedPackage + "' does not equal '" + actualPackage + "'");
      }

      String expectedNamespace = pm1.getNamespaceURI();
      String actualNamespace = pm2.getNamespaceURI();

      if (checkStringEquality(expectedPackage, actualPackage) == false)
      {
         throw new IllegalStateException("Namespace '" + expectedNamespace + "' does not equal '" + actualNamespace + "'");
      }

      return true;
   }

   private boolean validateJavaXmlTypeMapping(JavaXmlTypeMapping jm1, JavaXmlTypeMapping jm2)
   {
      boolean bool = true;
      bool = checkStringEquality(jm1.getJavaType(), jm2.getJavaType());
      if (bool)
         bool = checkStringEquality(jm1.getQnameScope(), jm2.getQnameScope());
      if (bool)
         bool = checkQNameEquality(jm1.getRootTypeQName(), jm2.getRootTypeQName());
      if (bool)
         bool = checkQNameEquality(jm1.getAnonymousTypeQName(), jm2.getAnonymousTypeQName());
      if (bool)
         bool = validateVariableMappings(jm1.getVariableMappings(), jm2.getVariableMappings());

      return bool;
   }

   private boolean validateJavaXmlTypeMappings(JavaXmlTypeMapping[] jm1, JavaXmlTypeMapping[] jm2)
   {
      int len1 = jm1 != null ? jm1.length : 0;
      int len2 = jm2 != null ? jm2.length : 0;
      if (len1 != len2)
      {
         throw new IllegalStateException("Length of JavaXmlTypeMapping[] do not match expected=" + len1 + " actual=" + len2);
      }

      // Don't need the order to be the same so cope with this.
      HashMap actualMappings = new HashMap(len1);

      for (int i = 0; i < len1; i++)
      {
         JavaXmlTypeMapping current = jm2[i];
         String name = current.getJavaType();
         if (actualMappings.containsKey(name))
         {
            throw new IllegalStateException("Type '" + name + "' registered more than once.");
         }

         actualMappings.put(name, current);
      }

      for (int i = 0; i < len1; i++)
      {
         JavaXmlTypeMapping expected = jm1[i];
         JavaXmlTypeMapping actual = (JavaXmlTypeMapping)actualMappings.get(expected.getJavaType());

         if (actual == null)
         {
            throw new IllegalStateException("Mapping not found for '" + expected.getJavaType() + "'");
         }

         if (validateJavaXmlTypeMapping(expected, actual) == false)
         {
            throw new IllegalStateException(expected + " does not match with other side " + actual);
         }
      }

      return true;
   }

   private boolean validateExceptionMapping(ExceptionMapping[] em1, ExceptionMapping[] em2)
   {
      int len1 = em1 != null ? em1.length : 0;
      int len2 = em2 != null ? em2.length : 0;
      if (len1 != len2)
      {
         throw new IllegalStateException("Length of ExceptionMapping[] do not match expected=" + len1 + " actual=" + len2);
      }

      HashMap actualMappings = new HashMap(len1);

      for (int i = 0; i < len1; i++)
      {
         ExceptionMapping current = em2[i];
         String name = current.getExceptionType();
         if (actualMappings.containsKey(name))
         {
            throw new IllegalStateException("Type '" + name + "' registered more than once.");
         }

         actualMappings.put(name, current);
      }

      for (int i = 0; i < len1; i++)
      {
         ExceptionMapping expected = em1[i];
         ExceptionMapping actual = (ExceptionMapping)actualMappings.get(expected.getExceptionType());

         if (actual == null)
         {
            throw new IllegalStateException("Mapping not found for '" + expected.getExceptionType() + "'");
         }

         if (validateExceptionMapping(expected, actual) == false)
         {
            throw new IllegalStateException(expected + " does not match with other side " + actual);
         }
      }

      return true;
   }

   private boolean validateExceptionMapping(ExceptionMapping em1, ExceptionMapping em2)
   {
      boolean bool = true;
      bool = checkStringEquality(em1.getExceptionType(), em2.getExceptionType());
      if (bool)
         bool = checkQNameEquality(em1.getWsdlMessage(), em2.getWsdlMessage());

      // Parameter order optional so don't enforce.
      //if (bool)
      //   bool = checkStringArrayEquality(em1.getConstructorParameterOrder(), em2.getConstructorParameterOrder());

      return bool;
   }

   private boolean validateVariableMappings(VariableMapping[] vm1, VariableMapping[] vm2)
   {
      boolean bool = true;
      int len1 = vm1 != null ? vm1.length : 0;
      int len2 = vm2 != null ? vm2.length : 0;
      if (len1 != len2)
      {
         throw new IllegalStateException("Length of VariableMapping[] do not match");
      }

      HashMap actualMappings = new HashMap(vm2.length);
      for (int i = 0; i < len1; i++)
      {
         VariableMapping current = vm2[i];
         if (actualMappings.containsKey(current.getJavaVariableName()))
            throw new IllegalStateException("Variable '" + current.getJavaVariableName() + "' mapped more than once!");

         actualMappings.put(current.getJavaVariableName(), current);
      }

      for (int i = 0; i < len1; i++)
      {
         VariableMapping expected = vm1[i];
         VariableMapping actual = (VariableMapping)actualMappings.get(expected.getJavaVariableName());

         if (actual == null)
            throw new IllegalStateException("Variable '" + expected.getJavaVariableName() + "' not found.");

         bool = validateVariableMapping(expected, actual);
         if (bool == false)
            throw new IllegalStateException("VariableMapping " + variableMappingToString(expected) + " does not match with " + variableMappingToString(actual));

      }
      return bool;
   }

   private String variableMappingToString(VariableMapping vm)
   {
      StringBuffer sb = new StringBuffer("[");
      sb.append("JavaVariableName=").append(vm.getJavaVariableName());
      sb.append(",XmlAttributeName=").append(vm.getXmlAttributeName());
      sb.append(",XmlElementName=").append(vm.getXmlElementName());
      sb.append(",XmlWildcard=").append(vm.getXmlWildcard());
      sb.append(",DataMember=").append(vm.isDataMember());
      sb.append("]");

      return sb.toString();
   }

   private boolean validateVariableMapping(VariableMapping vm1, VariableMapping vm2)
   {
      boolean bool = true;
      bool = checkStringEquality(vm1.getJavaVariableName(), vm2.getJavaVariableName());
      if (bool)
         bool = checkStringEquality(vm1.getXmlAttributeName(), vm2.getXmlAttributeName());
      if (bool)
         bool = checkStringEquality(vm1.getXmlElementName(), vm2.getXmlElementName());
      if (bool)
         bool = vm1.getXmlWildcard() == vm2.getXmlWildcard();
      if (bool)
         bool = vm1.isDataMember() == vm2.isDataMember();
      return bool;
   }

   private boolean validateServiceInterfaceMappings(ServiceInterfaceMapping[] sim1, ServiceInterfaceMapping[] sim2)
   {
      boolean bool = true;
      int len1 = sim1 != null ? sim1.length : 0;
      int len2 = sim2 != null ? sim2.length : 0;
      if (len1 != len2)
      {
         throw new IllegalStateException("Length of ServiceInterfaceMapping[] do not match");
      }

      for (int i = 0; i < len1; i++)
      {
         bool = validateServiceInterfaceMapping(sim1[i], sim2[i]);
         if (bool == false)
            break;
      }
      return bool;
   }

   private boolean validateServiceInterfaceMapping(ServiceInterfaceMapping sim1, ServiceInterfaceMapping sim2)
   {
      boolean bool = true;
      bool = checkStringEquality(sim1.getServiceInterface(), sim2.getServiceInterface());
      if (bool)
         bool = checkQNameEquality(sim1.getWsdlServiceName(), sim2.getWsdlServiceName());
      if (bool)
         bool = validatePortMappings(sim1.getPortMappings(), sim2.getPortMappings());
      return bool;
   }

   private boolean validatePortMappings(PortMapping[] pm1, PortMapping[] pm2)
   {
      boolean bool = true;

      int len1 = pm1 != null ? pm1.length : 0;
      int len2 = pm2 != null ? pm2.length : 0;
      if (len1 != len2)
      {
         throw new IllegalStateException("Length of PortMapping[] do not match");
      }

      for (int i = 0; i < len1; i++)
      {
         bool = validatePortMapping(pm1[i], pm2[i]);
         if (bool == false)
            break;
      }
      return bool;
   }

   private boolean validatePortMapping(PortMapping pm1, PortMapping pm2)
   {
      boolean bool = true;
      bool = checkStringEquality(pm1.getPortName(), pm2.getJavaPortName());
      if (bool)
         bool = checkStringEquality(pm1.getJavaPortName(), pm2.getJavaPortName());
      return bool;
   }

   private boolean validateServiceEndpointInterfaceMappings(ServiceEndpointInterfaceMapping[] sm1, ServiceEndpointInterfaceMapping[] sm2)
   {
      boolean bool = true;

      int len1 = sm1 != null ? sm1.length : 0;
      int len2 = sm2 != null ? sm2.length : 0;
      if (len1 != len2)
      {
         throw new IllegalStateException("Length of ServiceEndpointInterfaceMapping[] do not match");
      }

      for (int i = 0; i < len1; i++)
      {
         for (int j = 0; j < len1; j++)
         {
            bool = validateServiceEndpointInterfaceMapping(sm1[i], sm2[j]);
            if (bool)
               break;
         }
         if (bool == false)
            throw new IllegalStateException(sm1[i].getServiceEndpointInterface() + " has no match");
      }
      return bool;
   }

   private boolean validateServiceEndpointInterfaceMapping(ServiceEndpointInterfaceMapping sm1, ServiceEndpointInterfaceMapping sm2)
   {
      boolean bool = true;
      bool = checkStringEquality(sm1.getServiceEndpointInterface(), sm2.getServiceEndpointInterface());
      if (bool)
         bool = checkQNameEquality(sm1.getWsdlBinding(), sm2.getWsdlBinding());
      if (bool)
         bool = checkQNameEquality(sm1.getWsdlPortType(), sm2.getWsdlPortType());
      if (bool)
         bool = validateServiceEndpointMethodMappings(sm1.getServiceEndpointMethodMappings(), sm2.getServiceEndpointMethodMappings());
      return bool;
   }

   private boolean validateServiceEndpointMethodMappings(ServiceEndpointMethodMapping[] semm1, ServiceEndpointMethodMapping[] semm2)
   {
      boolean bool = true;

      int len1 = semm1 != null ? semm1.length : 0;
      int len2 = semm2 != null ? semm2.length : 0;
      if (len1 != len2)
      {
         throw new IllegalStateException("Length of ServiceEndpointMethodMapping[] do not match");
      }

      for (int i = 0; i < len1; i++)
      {
         for (int j = 0; j < len1; j++)
         {
            bool = validateServiceEndpointMethodMapping(semm1[i], semm2[j]);
            if (bool)
               break;
         }
         if (bool == false)
            throw new IllegalStateException(semm1[i].getJavaMethodName() + " do not match in" + " in ServiceEndpointMethod Mapping");

      }
      return bool;
   }

   private boolean validateServiceEndpointMethodMapping(ServiceEndpointMethodMapping semm1, ServiceEndpointMethodMapping semm2)
   {
      boolean bool = true;
      bool = checkStringEquality(semm1.getJavaMethodName(), semm2.getJavaMethodName());
      if (bool)
         bool = checkStringEquality(semm1.getWsdlOperation(), semm2.getWsdlOperation());
      else log.error("getJavaMethodName check failed");
      if (bool)
         bool = semm1.isWrappedElement() == semm2.isWrappedElement();
      else log.error("wsdloperation check failed");
      if (bool)
         bool = validateWsdlReturnValueMapping(semm1.getWsdlReturnValueMapping(), semm2.getWsdlReturnValueMapping());
      else log.error("isWrappedElement check failed");
      if (bool)
         bool = validateMethodParamPartsMappings(semm1.getMethodParamPartsMappings(), semm2.getMethodParamPartsMappings());
      else log.error("validateWsdlReturnValueMapping check failed");

      if (bool == false)
         log.error("validateMethodParamPartsMappings check failed");
      return bool;
   }

   private boolean validateWsdlReturnValueMapping(WsdlReturnValueMapping w1, WsdlReturnValueMapping w2)
   {
      checkNullParametersInconsistency(w1, w2, WsdlReturnValueMapping.class);
      boolean bool = true;
      if (w1 != null && w2 != null)
      {
         bool = checkStringEquality(w1.getMethodReturnValue(), w2.getMethodReturnValue());
         if (bool)
            bool = checkStringEquality(w1.getWsdlMessagePartName(), w2.getWsdlMessagePartName());
         if (bool)
            bool = checkQNameEquality(w1.getWsdlMessage(), w2.getWsdlMessage());
      }

      return bool;
   }

   private boolean validateMethodParamPartsMappings(MethodParamPartsMapping[] mppm1, MethodParamPartsMapping[] mppm2)
   {
      boolean bool = true;

      int len1 = mppm1 != null ? mppm1.length : 0;
      int len2 = mppm2 != null ? mppm2.length : 0;
      if (len1 != len2)
      {
         throw new IllegalStateException("Length of MethodParamPartsMapping[] do not match");
      }

      for (int i = 0; i < len1; i++)
      {
         bool = validateMethodParamPartsMapping(mppm1[i], mppm2[i]);
         if (bool == false)
            break;
      }
      return bool;
   }

   private boolean validateMethodParamPartsMapping(MethodParamPartsMapping mppm1, MethodParamPartsMapping mppm2)
   {
      boolean bool = true;
      bool = mppm1.getParamPosition() == mppm2.getParamPosition();
      if (bool)
         bool = checkStringEquality(mppm1.getParamType(), mppm2.getParamType());
      if (bool)
         bool = validateWsdlMessageMapping(mppm1.getWsdlMessageMapping(), mppm2.getWsdlMessageMapping());
      return bool;
   }

   private boolean validateWsdlMessageMapping(WsdlMessageMapping wmm1, WsdlMessageMapping wmm2)
   {
      String semmName = wmm1.getMethodParamPartsMapping().getServiceEndpointMethodMapping().getJavaMethodName();

      String path = "ServiceEndpointMethodMapping-" + semmName + "/methodparampartsmapping";
      boolean bool = true;
      if (bool)
         bool = checkStringEquality(wmm1.getParameterMode(), wmm2.getParameterMode());
      if (bool)
         bool = checkStringEquality(wmm1.getWsdlMessagePartName(), wmm2.getWsdlMessagePartName());
      else throw new IllegalStateException(path + "/parameterMode does not match");
      if (bool)
         bool = checkQNameEquality(wmm1.getWsdlMessage(), wmm2.getWsdlMessage());
      if (bool)
         bool = wmm1.isSoapHeader() == wmm2.isSoapHeader();
      else throw new IllegalStateException(path + "/wsdlMessage does not match");
      return bool;
   }

   //PRIVATE EQUALITY CHECKS

   private boolean checkStringEquality(String str1, String str2)
   {
      if (str1 == null && str2 == null)
         return true;
      if (str1 == null && str2 != null)
         return false;
      if (str1 != null && str2 == null)
         return false;
      return str1.equals(str2);
   }

   private boolean checkStringArrayEquality(String[] ar1, String[] ar2)
   {
      int len1 = ar1 != null ? ar1.length : 0;
      int len2 = ar2 != null ? ar2.length : 0;

      if (len1 != len2)
         return false;

      for (int i = 0; i < len1; i++)
      {
         if (checkStringEquality(ar1[i], ar2[i]) == false)
            return false;
      }

      return true;
   }

   private boolean checkQNameEquality(QName q1, QName q2)
   {
      if (q1 == null && q2 == null)
         return true;
      if (q1 == null && q2 != null)
         return false;
      if (q1 != null && q2 == null)
         return false;
      return q1.equals(q2);
   }

   private void checkNullParametersInconsistency(Object o1, Object o2, Class c)
   {
      WSDLUtils utils = WSDLUtils.getInstance();
      if ((o1 == null && o2 != null) || (o1 != null && o2 == null))
         throw new IllegalStateException(utils.getJustClassName(c) + " does not match");
   }
}
