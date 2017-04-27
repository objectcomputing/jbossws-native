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
package org.jboss.ws.tools.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.holders.Holder;

import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.metadata.jaxrpcmapping.ExceptionMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
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
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.FaultMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.umdm.WrappedParameter;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.XSModelTypes;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSElementDeclaration;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xmlschema.WSSchemaUtils;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.ws.tools.JavaToXSD;
import org.jboss.ws.tools.interfaces.JavaToXSDIntf;
import org.jboss.ws.tools.interfaces.SchemaCreatorIntf;
import org.jboss.ws.tools.wsdl.WSDLGenerator;
import org.jboss.wsf.common.JavaUtils;

/**
 *  Java To WSDL Helper which uses UnifiedMetaData
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Oct 7, 2005
 */
public class JavaToWSDLHelper extends WSDLGenerator
{
   private JavaToXSDIntf javaToXSD = new JavaToXSD();
   private JavaWsdlMapping javaWsdlMapping = new JavaWsdlMapping();
   private Map<QName, JavaXmlTypeMapping> mappedTypes = new HashMap<QName, JavaXmlTypeMapping>();
   private Set<String> mappedPackages = new HashSet<String>();
   private Set<String> mappedExceptions = new HashSet<String>();

   protected void processTypes()
   {
      // All type processing is done in processService()
      wsdl.setWsdlTypes(new XSModelTypes());
   }

   @Override
   protected void processOperation(WSDLInterface wsdlInterface, WSDLBinding wsdlBinding, OperationMetaData operation)
   {
      super.processOperation(wsdlInterface, wsdlBinding, operation);

      try
      {
         //Generate Types for the individual parameters, faults and return value
         for (ParameterMetaData paramMetaData : operation.getParameters())
         {
            generateTypesForXSD(paramMetaData);
         }

         for (FaultMetaData faultMetaData : operation.getFaults())
         {
            generateTypesForXSD(faultMetaData);
         }

         ParameterMetaData returnMetaData = operation.getReturnParameter();
         if (returnMetaData != null)
         {
            generateTypesForXSD(returnMetaData);
         }
      }
      catch (IOException io)
      {
         throw new WSException(io);
      }
   }

   public void generateTypesForXSD(ParameterMetaData pmd) throws IOException
   {
      //Types always deals with TypeNamespace
      QName xmlType = pmd.getXmlType();
      if(xmlType.getNamespaceURI().equals(Constants.NS_SCHEMA_XSD) == false)
        generateType(xmlType, pmd.getJavaType(), buildElementNameMap(pmd));

      if (pmd.getOperationMetaData().getStyle() == Style.DOCUMENT || pmd.isInHeader())
         generateElement(pmd.getXmlName(), xmlType);

      //Attachment type
      if(pmd.isSwA())
         wsdl.registerNamespaceURI(Constants.NS_SWA_MIME, "mime");
   }

   private Map<String, QName> buildElementNameMap(ParameterMetaData pmd)
   {
      List<WrappedParameter> wrappedParameters = pmd.getWrappedParameters();

      if (wrappedParameters == null)
         return null;

      Map<String, QName> map = new LinkedHashMap<String, QName>(wrappedParameters.size());

      for (WrappedParameter param : wrappedParameters)
         map.put(param.getVariable(), param.getName());

      return map;
   }

   public void generateTypesForXSD(FaultMetaData fmd) throws IOException
   {
      //Types always deals with TypeNamespace
      SchemaCreatorIntf sc = javaToXSD.getSchemaCreator();
      //Look at the features
      QName xmlType = fmd.getXmlType();
      if(xmlType.getNamespaceURI().equals(Constants.NS_SCHEMA_XSD) == false)
        generateType(xmlType ,fmd.getJavaType(), null);
   }

   public void processEndpoint(WSDLService service, EndpointMetaData endpoint)
   {
      super.processEndpoint(service, endpoint);

      // build JAX-RPC mapping info
      buildServiceMapping(endpoint);
   }

   /*
    * Currently we only handle 1 endpoint on 1 service, this is the way everything
    * else is handled anyway.
    */
   private void buildServiceMapping(EndpointMetaData endpoint)
   {
      QName origQName = endpoint.getServiceMetaData().getServiceName();
      String serviceInterfaceName = endpoint.getServiceEndpointInterface().getPackage().getName() + "." + origQName.getLocalPart();
      QName serviceQName = new QName(origQName.getNamespaceURI(), origQName.getLocalPart(), "serviceNS");

      ServiceInterfaceMapping serviceMapping = new ServiceInterfaceMapping(javaWsdlMapping);
      serviceMapping.setServiceInterface(serviceInterfaceName);
      serviceMapping.setWsdlServiceName(serviceQName);

      String endpointName = endpoint.getPortName().getLocalPart();
      PortMapping portMapping = new PortMapping(serviceMapping);
      portMapping.setJavaPortName(endpointName);
      portMapping.setPortName(endpointName);
      serviceMapping.addPortMapping(portMapping);

      javaWsdlMapping.addServiceInterfaceMappings(serviceMapping);

      String interfaceName = endpoint.getPortTypeName().getLocalPart();
      ServiceEndpointInterfaceMapping seiMapping = new ServiceEndpointInterfaceMapping(javaWsdlMapping);
      seiMapping.setServiceEndpointInterface(endpoint.getServiceEndpointInterfaceName());
      seiMapping.setWsdlPortType(new QName(wsdl.getTargetNamespace(), interfaceName, "portTypeNS"));
      seiMapping.setWsdlBinding(new QName(wsdl.getTargetNamespace(), interfaceName + "Binding", "bindingNS"));
      for (OperationMetaData operation : endpoint.getOperations())
      {
         ServiceEndpointMethodMapping methodMapping = new ServiceEndpointMethodMapping(seiMapping);
         methodMapping.setJavaMethodName(operation.getJavaName());
         methodMapping.setWsdlOperation(operation.getQName().getLocalPart());
         boolean isWrapped = operation.isDocumentWrapped();
         methodMapping.setWrappedElement(isWrapped);
         int i = 0;
         for (ParameterMetaData param : operation.getParameters())
         {
            if (isWrapped && param.isInHeader() == false)
            {
               List<WrappedParameter> wrappedParameters = param.getWrappedParameters();
               for (WrappedParameter wrapped : wrappedParameters)
               {
                  String type = JavaUtils.convertJVMNameToSourceName(wrapped.getType(), endpoint.getClassLoader());
                  String name = wrapped.getName().getLocalPart();

                  buildParamMapping(methodMapping, interfaceName, operation, name, type, "IN", false, i++);
               }
            }
            else
            {
               String name = param.getXmlName().getLocalPart();
               String type = JavaUtils.convertJVMNameToSourceName(param.getJavaTypeName(), endpoint.getClassLoader());
               buildParamMapping(methodMapping, interfaceName, operation, name, type, param.getMode().toString(), param.isInHeader(), i++);
            }
         }

         ParameterMetaData returnParam = operation.getReturnParameter();
         if (returnParam != null && ((! isWrapped) || (! returnParam.getWrappedParameters().isEmpty())))
         {
            String name, type;
            if (isWrapped)
            {
               WrappedParameter wrappedParameter = returnParam.getWrappedParameters().get(0);
               name = wrappedParameter.getName().getLocalPart();
               type = wrappedParameter.getType();
            }
            else
            {
               name = returnParam.getXmlName().getLocalPart();
               type = returnParam.getJavaTypeName();
            }

            type = JavaUtils.convertJVMNameToSourceName(type, endpoint.getClassLoader());

            buildReturnParamMapping(methodMapping, interfaceName, operation, name, type);
         }
         seiMapping.addServiceEndpointMethodMapping(methodMapping);

         for(FaultMetaData fmd : operation.getFaults())
         {
            String ns = getNamespace(fmd.getJavaType(), fmd.getXmlType().getNamespaceURI());
            QName newXmlType = new QName(ns, fmd.getXmlType().getLocalPart());
            JavaXmlTypeMapping typeMapping = mappedTypes.get(newXmlType);
            if (typeMapping == null)
               continue;

            String javaTypeName = fmd.getJavaTypeName();
            if (mappedExceptions.contains(javaTypeName))
               continue;

            mappedExceptions.add(javaTypeName);

            ExceptionMapping mapping = new ExceptionMapping(javaWsdlMapping);

            mapping.setExceptionType(javaTypeName);
            QName name = new QName(wsdl.getTargetNamespace(), fmd.getXmlName().getLocalPart());
            mapping.setWsdlMessage(name);

            // Variable mappings generated from SchemaTypesCreater have their order preserved
            for (VariableMapping variableMapping : typeMapping.getVariableMappings())
               mapping.addConstructorParameter(variableMapping.getXmlElementName());

            javaWsdlMapping.addExceptionMappings(mapping);
         }
      }

      javaWsdlMapping.addServiceEndpointInterfaceMappings(seiMapping);

      // Add package mapping for SEI
      String name = endpoint.getServiceEndpointInterface().getPackage().getName();
      String namespace = getNamespace(name);
      if (namespace == null)
         namespace = WSDLUtils.getInstance().getTypeNamespace(name);
      addPackageMapping(name, namespace);
   }

   private void buildParamMapping(ServiceEndpointMethodMapping methodMapping, String interfaceName, OperationMetaData operation,
                                  String name, String type, String mode, boolean header, int position)
   {
      MethodParamPartsMapping paramMapping = new MethodParamPartsMapping(methodMapping);
      paramMapping.setParamPosition(position);
      paramMapping.setParamType(type);

      WsdlMessageMapping messageMapping = new WsdlMessageMapping(paramMapping);
      messageMapping.setWsdlMessagePartName(name);
      String messageName = interfaceName + "_" + operation.getQName().getLocalPart();
      if ("OUT".equals(mode))
         messageName += "Response";
      QName messageQName = new QName(wsdl.getTargetNamespace(), messageName, "wsdlMsgNS");

      messageMapping.setWsdlMessage(messageQName);
      messageMapping.setParameterMode(mode);
      messageMapping.setSoapHeader(header);
      paramMapping.setWsdlMessageMapping(messageMapping);
      methodMapping.addMethodParamPartsMapping(paramMapping);
   }

   private void buildReturnParamMapping(ServiceEndpointMethodMapping methodMapping, String interfaceName, OperationMetaData operation, String name, String type)
   {
      WsdlReturnValueMapping returnMapping = new WsdlReturnValueMapping(methodMapping);
      returnMapping.setMethodReturnValue(type);
      returnMapping.setWsdlMessagePartName(name);
      String messageName = interfaceName + "_" + operation.getQName().getLocalPart() + "Response";
      QName messageQName = new QName(wsdl.getTargetNamespace(), messageName, "wsdlMsgNS");
      returnMapping.setWsdlMessage(messageQName);
      methodMapping.setWsdlReturnValueMapping(returnMapping);
   }

   /**
    * During the WSDL generation process, a typeMapping will be
    * created that maps xml types -> java types
    *
    * @return  typeMapping
    */
   public TypeMapping getTypeMapping()
   {
      return this.javaToXSD.getSchemaCreator().getTypeMapping();
   }

   /**
    * A customized Package->Namespace map
    *
    * @param map
    */
   public void setPackageNamespaceMap(Map<String,String> map)
   {
      this.packageNamespaceMap = map;
      this.javaToXSD.setPackageNamespaceMap(map);
   }

   public void setJavaToXSD(JavaToXSDIntf jxsd)
   {
      this.javaToXSD = jxsd;
   }

   public JavaWsdlMapping getJavaWsdlMapping()
   {
      return javaWsdlMapping;
   }

   //************************************************************************
   //
   //**************************PRIVATE METHODS*****************************
   //
   //************************************************************************

   protected void generateType(QName xmlType, Class javaType, Map<String, QName> elementNames) throws IOException
   {
      if(Holder.class.isAssignableFrom(javaType))
         javaType = WSDLUtils.getInstance().getJavaTypeForHolder(javaType);
      JBossXSModel xsModel = javaToXSD.generateForSingleType(xmlType, javaType, elementNames);
      String namespace = getNamespace(javaType, xmlType.getNamespaceURI());
      //  Now that the schema object graph is built,
      //  ask JavaToXSD to provide a list of xsmodels to be plugged
      //  into WSDLTypes
      if (xsModel == null)
         throw new WSException("XSModel is null");

      WSDLTypes wsdlTypes = wsdl.getWsdlTypes();
      WSDLUtils.addSchemaModel(wsdlTypes, namespace, xsModel);
      wsdl.registerNamespaceURI(namespace, null);

      //Also get any custom namespaces
      SchemaCreatorIntf schemaCreator = javaToXSD.getSchemaCreator();
      mergeJavaWsdlMapping(schemaCreator.getJavaWsdlMapping());

      //Register the global config namespaces
      /*Map<String, String> nsmap = schemaCreator.getPackageNamespaceMap();
      Set keys = nsmap != null ? nsmap.keySet() : null;
      Iterator iter = (keys != null && !keys.isEmpty()) ? keys.iterator() : null;
      while (iter != null && iter.hasNext())
      {
         String pkg = (String)iter.next();
         String ns = nsmap.get(pkg);
         if (ns != null)
            wsdl.registerNamespaceURI(ns, null);
      }*/
      //Register the custom generated namespaces
      Map<String, String> nsmap = schemaCreator.getCustomNamespaceMap();
      Set keys = nsmap != null ? nsmap.keySet() : null;
      Iterator iter = (keys != null && !keys.isEmpty()) ? keys.iterator() : null;
      while (iter != null && iter.hasNext())
      {
         String prefix = (String)iter.next();
         String ns = nsmap.get(prefix);
         if (ns != null)
            wsdl.registerNamespaceURI(ns, null);
      }
   }

   private void mergeJavaWsdlMapping(JavaWsdlMapping source)
   {
      // For now we just merge types and packages
      for (PackageMapping packageMapping : source.getPackageMappings())
      {
         String name = packageMapping.getPackageType();
         String namespaceURI = getNamespace(name, packageMapping.getNamespaceURI());

         addPackageMapping(name, namespaceURI);
      }

      for (JavaXmlTypeMapping type : source.getJavaXmlTypeMappings())
      {
         QName name = type.getRootTypeQName();
         if (name == null)
            name = type.getAnonymousTypeQName();

         //override namespace from globalconfig
         String pkgName = getJustPackageName(type.getJavaType());
         String ns = getNamespace(pkgName, name.getNamespaceURI());
         name = new QName(ns, name.getLocalPart(), name.getPrefix());

         if (mappedTypes.containsKey(name))
            continue;

         addPackageMapping(pkgName, ns);
         mappedTypes.put(name, type);

         JavaXmlTypeMapping typeCopy = new JavaXmlTypeMapping(javaWsdlMapping);
         typeCopy.setQNameScope(type.getQnameScope());
         typeCopy.setAnonymousTypeQName(type.getAnonymousTypeQName());
         typeCopy.setJavaType(type.getJavaType());
         typeCopy.setRootTypeQName(name);
         for (VariableMapping variable : type.getVariableMappings())
         {
            VariableMapping variableCopy = new VariableMapping(typeCopy);
            variableCopy.setDataMember(variable.isDataMember());
            variableCopy.setJavaVariableName(variable.getJavaVariableName());
            variableCopy.setXmlAttributeName(variable.getXmlAttributeName());
            variableCopy.setXmlElementName(variable.getXmlElementName());
            variableCopy.setXmlWildcard(variable.getXmlWildcard());

            typeCopy.addVariableMapping(variableCopy);
         }

         javaWsdlMapping.addJavaXmlTypeMappings(typeCopy);
      }
   }

   private void addPackageMapping(String name, String namespaceURI)
   {
      if (mappedPackages.contains(name))
         return;

      mappedPackages.add(name);
      PackageMapping copy = new PackageMapping(javaWsdlMapping);
      copy.setPackageType(name);

      copy.setNamespaceURI(namespaceURI);
      javaWsdlMapping.addPackageMapping(copy);
   }

   protected void generateElement(QName xmlName, QName xmlType)
   {
      WSDLTypes types = wsdl.getWsdlTypes();
      String namespaceURI = xmlType.getNamespaceURI();
      JBossXSModel schemaModel = WSDLUtils.getSchemaModel(types);

      XSTypeDefinition type;
      if (Constants.NS_SCHEMA_XSD.equals(namespaceURI))
         type = SchemaUtils.getInstance().getSchemaBasicType(xmlType.getLocalPart());
      else
         type = schemaModel.getTypeDefinition(xmlType.getLocalPart(), namespaceURI);

      WSSchemaUtils utils = WSSchemaUtils.getInstance(schemaModel.getNamespaceRegistry(), null);
      JBossXSElementDeclaration element =
         utils.createGlobalXSElementDeclaration(xmlName.getLocalPart(), type, xmlName.getNamespaceURI());
      schemaModel.addXSElementDeclaration(element);

      wsdl.registerNamespaceURI(xmlName.getNamespaceURI(), null);
   }
}
