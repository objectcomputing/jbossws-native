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
package org.jboss.ws.metadata.builder.jaxrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.TypeMappingRegistry;

import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.binding.TypeMappingImpl;
import org.jboss.ws.core.jaxrpc.EncodedTypeMapping;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.core.jaxrpc.TypeMappingRegistryImpl;
import org.jboss.ws.core.jaxrpc.UnqualifiedFaultException;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.core.soap.Use;
import org.jboss.ws.extensions.xop.jaxrpc.XOPScanner;
import org.jboss.ws.metadata.builder.MetaDataBuilder;
import org.jboss.ws.metadata.jaxrpcmapping.ExceptionMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaXmlTypeMapping;
import org.jboss.ws.metadata.jaxrpcmapping.MethodParamPartsMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceEndpointInterfaceMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceEndpointMethodMapping;
import org.jboss.ws.metadata.jaxrpcmapping.VariableMapping;
import org.jboss.ws.metadata.jaxrpcmapping.WsdlMessageMapping;
import org.jboss.ws.metadata.jaxrpcmapping.WsdlReturnValueMapping;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.FaultMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.TypeMappingMetaData;
import org.jboss.ws.metadata.umdm.TypesMetaData;
import org.jboss.ws.metadata.umdm.WrappedParameter;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceFault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutfault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLMIMEPart;
import org.jboss.ws.metadata.wsdl.WSDLProperty;
import org.jboss.ws.metadata.wsdl.WSDLRPCPart;
import org.jboss.ws.metadata.wsdl.WSDLRPCSignatureItem;
import org.jboss.ws.metadata.wsdl.WSDLSOAPHeader;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.WSDLRPCSignatureItem.Direction;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.wsf.common.JavaUtils;

/**
 * A meta data builder that is based on webservices.xml.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.org">Jason T. Greene</a>
 * @since 19-Oct-2005
 */
public abstract class JAXRPCMetaDataBuilder extends MetaDataBuilder
{
   // provide logging
   final Logger log = Logger.getLogger(JAXRPCMetaDataBuilder.class);

   protected QName lookupSchemaType(WSDLInterfaceOperation operation, QName element)
   {
      WSDLDefinitions wsdlDefinitions = operation.getWsdlInterface().getWsdlDefinitions();
      WSDLTypes wsdlTypes = wsdlDefinitions.getWsdlTypes();
      return wsdlTypes.getXMLType(element);
   }

   protected void setupTypesMetaData(ServiceMetaData serviceMetaData)
   {
      WSDLDefinitions wsdlDefinitions = serviceMetaData.getWsdlDefinitions();
      JavaWsdlMapping javaWsdlMapping = serviceMetaData.getJavaWsdlMapping();
      TypesMetaData typesMetaData = serviceMetaData.getTypesMetaData();

      // Copy the schema locations to the types meta data
      if (wsdlDefinitions != null)
      {
         WSDLTypes wsdlTypes = wsdlDefinitions.getWsdlTypes();
         typesMetaData.setSchemaModel(WSDLUtils.getSchemaModel(wsdlTypes));
      }

      // Copy the type mappings to the types meta data
      if (javaWsdlMapping != null)
      {
         for (JavaXmlTypeMapping xmlTypeMapping : javaWsdlMapping.getJavaXmlTypeMappings())
         {
            String javaTypeName = xmlTypeMapping.getJavaType();
            String qnameScope = xmlTypeMapping.getQnameScope();

            QName xmlType = xmlTypeMapping.getRootTypeQName();
            QName anonymousXMLType = xmlTypeMapping.getAnonymousTypeQName();
            if (xmlType == null && anonymousXMLType != null)
               xmlType = anonymousXMLType;

            String nsURI = xmlType.getNamespaceURI();
            if (Constants.NS_SCHEMA_XSD.equals(nsURI) == false && Constants.URI_SOAP11_ENC.equals(nsURI) == false)
            {
               TypeMappingMetaData tmMetaData = new TypeMappingMetaData(typesMetaData, xmlType, javaTypeName);
               tmMetaData.setQNameScope(qnameScope);
               typesMetaData.addTypeMapping(tmMetaData);
            }
         }

         for (ExceptionMapping exceptionMapping : javaWsdlMapping.getExceptionMappings())
         {
            QName xmlType = exceptionMapping.getWsdlMessage();
            String javaTypeName = exceptionMapping.getExceptionType();
            TypeMappingMetaData tmMetaData = new TypeMappingMetaData(typesMetaData, xmlType, javaTypeName);
            typesMetaData.addTypeMapping(tmMetaData);
         }
      }
   }

   protected void setupOperationsFromWSDL(EndpointMetaData epMetaData, WSDLEndpoint wsdlEndpoint, ServiceEndpointInterfaceMapping seiMapping)
   {
      WSDLDefinitions wsdlDefinitions = wsdlEndpoint.getInterface().getWsdlDefinitions();

      // For every WSDL interface operation build the OperationMetaData
      WSDLInterface wsdlInterface = wsdlEndpoint.getInterface();
      for (WSDLInterfaceOperation wsdlOperation : wsdlInterface.getOperations())
      {
         QName opQName = wsdlOperation.getName();
         String opName = opQName.getLocalPart();

         WSDLBindingOperation wsdlBindingOperation = wsdlOperation.getBindingOperation();
         if (wsdlBindingOperation == null)
            log.warn("Could not locate binding operation for:" + opQName);

         // Change operation according namespace defined on binding 
         // <soap:body use="encoded" namespace="http://MarshallTestW2J.org/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"/>
         String namespaceURI = wsdlBindingOperation.getNamespaceURI();
         if (namespaceURI != null)
            opQName = new QName(namespaceURI, opName);

         // Set java method name
         String javaName = opName.substring(0, 1).toLowerCase() + opName.substring(1);
         ServiceEndpointMethodMapping seiMethodMapping = null;
         if (seiMapping != null)
         {
            epMetaData.setServiceEndpointInterfaceName(seiMapping.getServiceEndpointInterface());

            seiMethodMapping = seiMapping.getServiceEndpointMethodMappingByWsdlOperation(opName);
            if (seiMethodMapping == null)
               throw new WSException("Cannot obtain method mapping for: " + opName);

            javaName = seiMethodMapping.getJavaMethodName();
         }

         OperationMetaData opMetaData = new OperationMetaData(epMetaData, opQName, javaName);
         epMetaData.addOperation(opMetaData);

         // Set the operation style
         String style = wsdlOperation.getStyle();
         epMetaData.setStyle((Constants.URI_STYLE_DOCUMENT.equals(style) ? Style.DOCUMENT : Style.RPC));

         // Set the operation MEP
         if (Constants.WSDL20_PATTERN_IN_ONLY.equals(wsdlOperation.getPattern()))
            opMetaData.setOneWay(true);

         // Set the operation SOAPAction
         if (wsdlBindingOperation != null)
            opMetaData.setSOAPAction(wsdlBindingOperation.getSOAPAction());

         // Get the type mapping for the encoding style
         String encStyle = epMetaData.getEncodingStyle().toURI();
         TypeMappingRegistry tmRegistry = new TypeMappingRegistryImpl();
         TypeMappingImpl typeMapping = (TypeMappingImpl)tmRegistry.getTypeMapping(encStyle);

         // Build the parameter meta data
         if (opMetaData.getStyle() == Style.RPC)
         {
            buildParameterMetaDataRpc(opMetaData, wsdlOperation, seiMethodMapping, typeMapping);
         }
         else
         {
            buildParameterMetaDataDoc(opMetaData, wsdlOperation, seiMethodMapping, typeMapping);
         }

         // Build operation faults
         buildFaultMetaData(opMetaData, wsdlOperation);

         // process further operation extensions
         processOpMetaExtensions(opMetaData, wsdlOperation);
      }
   }

   private ParameterMetaData buildInputParameter(OperationMetaData opMetaData, WSDLInterfaceOperation wsdlOperation, ServiceEndpointMethodMapping seiMethodMapping,
         TypeMappingImpl typeMapping, String partName, QName xmlName, QName xmlType, int pos, boolean optional)
   {
      WSDLRPCSignatureItem item = wsdlOperation.getRpcSignatureitem(partName);
      if (item != null)
         pos = item.getPosition();

      String javaTypeName = typeMapping.getJavaTypeName(xmlType);
      boolean mapped = false;
      if (seiMethodMapping != null)
      {
         MethodParamPartsMapping paramMapping = seiMethodMapping.getMethodParamPartsMappingByPartName(partName);
         if (paramMapping != null)
         {
            javaTypeName = paramMapping.getParamType();
            pos = paramMapping.getParamPosition();
            mapped = true;
         }
         else if (!optional)
         {
            throw new WSException("Cannot obtain method parameter mapping for message part '" + partName + "' in wsdl operation: "
                  + seiMethodMapping.getWsdlOperation());
         }
      }

      // For now we ignore unlisted headers with no mapping information
      if (!mapped && optional)
         return null;

      JavaWsdlMapping javaWsdlMapping = opMetaData.getEndpointMetaData().getServiceMetaData().getJavaWsdlMapping();
      if (javaTypeName == null && javaWsdlMapping != null)
      {
         String packageName = javaWsdlMapping.getPackageNameForNamespaceURI(xmlType.getNamespaceURI());
         if (packageName != null)
         {
            javaTypeName = packageName + "." + xmlType.getLocalPart();
            log.warn("Guess java type from package mapping: [xmlType=" + xmlType + ",javaType=" + javaTypeName + "]");
         }
      }

      if (javaTypeName == null)
         throw new WSException("Cannot obtain java type mapping for: " + xmlType);

      ParameterMetaData inMetaData = new ParameterMetaData(opMetaData, xmlName, xmlType, javaTypeName);
      inMetaData.setPartName(partName);
      inMetaData.setIndex(pos);
      opMetaData.addParameter(inMetaData);

      TypesMetaData typesMetaData = opMetaData.getEndpointMetaData().getServiceMetaData().getTypesMetaData();
      // In arrays of user types, wscompile does not generate a mapping in jaxrpc-mapping.xml
      if (typesMetaData.getTypeMappingByXMLType(xmlType) == null)
      {
         String nsURI = xmlType.getNamespaceURI();
         if (Constants.NS_SCHEMA_XSD.equals(nsURI) == false && Constants.URI_SOAP11_ENC.equals(nsURI) == false)
         {
            TypeMappingMetaData tmMetaData = new TypeMappingMetaData(typesMetaData, xmlType, javaTypeName);
            typesMetaData.addTypeMapping(tmMetaData);
         }
      }

      return inMetaData;
   }

   private ParameterMetaData buildOutputParameter(OperationMetaData opMetaData, WSDLInterfaceOperation wsdlOperation, ServiceEndpointMethodMapping seiMethodMapping,
         int pos, String partName, QName xmlName, QName xmlType, TypeMappingImpl typeMapping, boolean optional)
   {
      // Default is first listed output
      boolean hasReturnMapping = opMetaData.getReturnParameter() == null;

      WSDLRPCSignatureItem item = wsdlOperation.getRpcSignatureitem(partName);
      if (item != null)
      {
         hasReturnMapping = item.getDirection() == Direction.RETURN;
         pos = item.getPosition();
      }

      String javaTypeName = typeMapping.getJavaTypeName(xmlType);

      boolean mapped = false;
      if (seiMethodMapping != null)
      {
         MethodParamPartsMapping paramMapping = seiMethodMapping.getMethodParamPartsMappingByPartName(partName);
         if (paramMapping != null)
         {
            javaTypeName = paramMapping.getParamType();
            pos = paramMapping.getParamPosition();
            hasReturnMapping = false;
            mapped = true;
         }
         else
         {
            WsdlReturnValueMapping returnMapping = seiMethodMapping.getWsdlReturnValueMapping();
            if (returnMapping != null)
            {
               String mappingPart = returnMapping.getWsdlMessagePartName();
               if (mappingPart != null && partName.equals(mappingPart))
               {
                  javaTypeName = returnMapping.getMethodReturnValue();
                  hasReturnMapping = true;
                  mapped = true;
               }
            }
         }
      }

      // For now we ignore unlisted headers with no mapping information
      if (!mapped && optional)
         return null;

      JavaWsdlMapping javaWsdlMapping = opMetaData.getEndpointMetaData().getServiceMetaData().getJavaWsdlMapping();
      if (javaTypeName == null && javaWsdlMapping != null)
      {
         String packageName = javaWsdlMapping.getPackageNameForNamespaceURI(xmlType.getNamespaceURI());
         if (packageName != null)
         {
            javaTypeName = packageName + "." + xmlType.getLocalPart();
            log.warn("Guess java type from package mapping: [xmlType=" + xmlType + ",javaType=" + javaTypeName + "]");
         }
      }

      if (javaTypeName == null)
         throw new WSException("Cannot obtain java type mapping for: " + xmlType);

      ParameterMetaData outMetaData = new ParameterMetaData(opMetaData, xmlName, xmlType, javaTypeName);
      outMetaData.setPartName(partName);

      if (hasReturnMapping)
      {
         opMetaData.setReturnParameter(outMetaData);
      }
      else
      {
         outMetaData.setIndex(pos);
         outMetaData.setMode(ParameterMode.OUT);
         opMetaData.addParameter(outMetaData);
      }

      TypesMetaData typesMetaData = opMetaData.getEndpointMetaData().getServiceMetaData().getTypesMetaData();
      // In arrays of user types, wscompile does not generate a mapping in jaxrpc-mapping.xml
      if (typesMetaData.getTypeMappingByXMLType(xmlType) == null)
      {
         String nsURI = xmlType.getNamespaceURI();
         if (Constants.NS_SCHEMA_XSD.equals(nsURI) == false && Constants.URI_SOAP11_ENC.equals(nsURI) == false)
         {
            TypeMappingMetaData tmMetaData = new TypeMappingMetaData(typesMetaData, xmlType, javaTypeName);
            typesMetaData.addTypeMapping(tmMetaData);
         }
      }

      setupSOAPArrayParameter(outMetaData);
      return outMetaData;
   }

   private int processBindingParameters(OperationMetaData opMetaData, WSDLInterfaceOperation wsdlOperation, ServiceEndpointMethodMapping seiMethodMapping,
         TypeMappingImpl typeMapping, WSDLBindingOperation bindingOperation, int wsdlPosition)
   {
      WSDLBindingOperationInput bindingInput = bindingOperation.getInputs()[0];
      for (WSDLSOAPHeader header : bindingInput.getSoapHeaders())
      {
         QName xmlName = header.getElement();
         QName xmlType = lookupSchemaType(wsdlOperation, xmlName);
         String partName = header.getPartName();

         ParameterMetaData pmd = buildInputParameter(opMetaData, wsdlOperation, seiMethodMapping, typeMapping, partName, xmlName, xmlType, wsdlPosition++, !header
               .isIncludeInSignature());
         if (pmd != null)
            pmd.setInHeader(true);
      }
      for (WSDLMIMEPart mimePart : bindingInput.getMimeParts())
      {
         String partName = mimePart.getPartName();
         QName xmlName = new QName(partName);
         QName xmlType = mimePart.getXmlType();

         ParameterMetaData pmd = buildInputParameter(opMetaData, wsdlOperation, seiMethodMapping, typeMapping, partName, xmlName, xmlType, wsdlPosition++, false);
         pmd.setSwA(true);
         pmd.setMimeTypes(mimePart.getMimeTypes());
      }

      return wsdlPosition;
   }

   private int processBindingOutputParameters(OperationMetaData opMetaData, WSDLInterfaceOperation wsdlOperation, ServiceEndpointMethodMapping seiMethodMapping,
         TypeMappingImpl typeMapping, WSDLBindingOperation bindingOperation, int wsdlPosition)
   {
      WSDLBindingOperationOutput bindingOutput = bindingOperation.getOutputs()[0];
      for (WSDLSOAPHeader header : bindingOutput.getSoapHeaders())
      {
         String partName = header.getPartName();
         QName xmlName = header.getElement();

         ParameterMetaData outMetaData = opMetaData.getParameter(xmlName);
         if (outMetaData != null)
         {
            outMetaData.setMode(ParameterMode.INOUT);
         }
         else
         {
            QName xmlType = lookupSchemaType(wsdlOperation, xmlName);

            ParameterMetaData pmd = buildOutputParameter(opMetaData, wsdlOperation, seiMethodMapping, wsdlPosition, partName, xmlName, xmlType, typeMapping, !header
                  .isIncludeInSignature());
            if (pmd != null)
            {
               pmd.setInHeader(true);
               if (opMetaData.getReturnParameter() != pmd)
                  wsdlPosition++;
            }
         }
      }

      for (WSDLMIMEPart mimePart : bindingOutput.getMimeParts())
      {
         String partName = mimePart.getPartName();
         QName xmlName = new QName(partName);

         ParameterMetaData outMetaData = opMetaData.getParameter(xmlName);
         if (outMetaData != null)
         {
            outMetaData.setMode(ParameterMode.INOUT);
         }
         else
         {
            QName xmlType = mimePart.getXmlType();

            ParameterMetaData pmd = buildOutputParameter(opMetaData, wsdlOperation, seiMethodMapping, wsdlPosition, partName, xmlName, xmlType, typeMapping, false);
            pmd.setSwA(true);
            pmd.setMimeTypes(mimePart.getMimeTypes());

            if (opMetaData.getReturnParameter() != pmd)
               wsdlPosition++;
         }
      }

      return wsdlPosition;
   }

   /* SOAP-ENC:Array
    *
    * FIXME: This hack should be removed as soon as we can reliably get the
    * soapenc:arrayType from wsdl + schema.
    */
   private void setupSOAPArrayParameter(ParameterMetaData paramMetaData)
   {
      Use use = paramMetaData.getOperationMetaData().getUse();
      String xmlTypeLocalPart = paramMetaData.getXmlType().getLocalPart();
      if (use == Use.ENCODED && xmlTypeLocalPart.indexOf("ArrayOf") >= 0)
      {
         paramMetaData.setSOAPArrayParam(true);
         try
         {
            String javaTypeName = paramMetaData.getJavaTypeName();
            // This approach determins the array component type from the javaTypeName.
            // It will not work for user defined types, nor will the array dimension be
            // initialized properly. Ideally the array parameter meta data should be initialized
            // from the XSModel or wherever it is defined in WSDL.
            Class javaType = JavaUtils.loadJavaType(javaTypeName);
            Class compJavaType = javaType.getComponentType();

            if (xmlTypeLocalPart.indexOf("ArrayOfArrayOf") >= 0)
               compJavaType = compJavaType.getComponentType();

            boolean isSoapEnc = xmlTypeLocalPart.toLowerCase().indexOf("soapenc") > 0;
            TypeMappingImpl typeMapping = isSoapEnc ? new EncodedTypeMapping() : new LiteralTypeMapping();
            QName compXMLType = typeMapping.getXMLType(compJavaType);

            if (compXMLType != null)
            {
               boolean isBase64 = compXMLType.getLocalPart().startsWith("base64");
               if (isBase64 && xmlTypeLocalPart.toLowerCase().indexOf("hex") > 0)
                  compXMLType = isSoapEnc ? Constants.TYPE_SOAP11_HEXBINARY : Constants.TYPE_LITERAL_HEXBINARY;
            }
            
            paramMetaData.setSOAPArrayCompType(compXMLType);
         }
         catch (ClassNotFoundException e)
         {
            // ignore that user defined types cannot be loaded yet
         }
      }
   }

   private void setupXOPAttachmentParameter(WSDLInterfaceOperation operation, ParameterMetaData paramMetaData)
   {
      QName xmlType = paramMetaData.getXmlType();

      // An XOP parameter is detected if it is a complex type that derives from xsd:base64Binary
      WSDLTypes wsdlTypes = operation.getWsdlInterface().getWsdlDefinitions().getWsdlTypes();
      JBossXSModel schemaModel = WSDLUtils.getSchemaModel(wsdlTypes);
      String localPart = xmlType.getLocalPart() != null ? xmlType.getLocalPart() : "";
      String ns = xmlType.getNamespaceURI() != null ? xmlType.getNamespaceURI() : "";
      XSTypeDefinition xsType = schemaModel.getTypeDefinition(localPart, ns);
      XOPScanner scanner = new XOPScanner();
      if (scanner.findXOPTypeDef(xsType) != null | (localPart.equals("base64Binary") && ns.equals(Constants.NS_SCHEMA_XSD)))
      {
         // FIXME: read the xmime:contentType from the element declaration
         // See SchemaUtils#findXOPTypeDef(XSTypeDefinition typeDef) for details

         /*
          FIXME: the classloader is not set yet
          paramMetaData.setXopContentType(
          MimeUtils.resolveMimeType(paramMetaData.getJavaType())
          );
          */

         paramMetaData.setXOP(true);

      }
   }

   /*
    * Perhaps the JAX-RPC mapping model should be hash based. For now we optimize just this case.
    */
   private Map<String, String> createVariableMappingMap(VariableMapping[] mappings)
   {
      HashMap<String, String> map = new HashMap<String, String>();
      if (mappings != null)
         for (VariableMapping mapping : mappings)
            map.put(mapping.getXmlElementName(), mapping.getJavaVariableName());

      return map;
   }

   private void buildParameterMetaDataRpc(OperationMetaData opMetaData, WSDLInterfaceOperation wsdlOperation, ServiceEndpointMethodMapping seiMethodMapping,
         TypeMappingImpl typeMapping)
   {
      log.trace("buildParameterMetaDataRpc: " + opMetaData.getQName());

      WSDLBindingOperation bindingOperation = wsdlOperation.getBindingOperation();
      if (bindingOperation == null)
         throw new WSException("Could not locate binding operation for:" + opMetaData.getQName());

      // RPC has one input
      WSDLInterfaceOperationInput input = wsdlOperation.getInputs()[0];
      int wsdlPosition = 0;
      for (WSDLRPCPart part : input.getChildParts())
      {
         QName xmlType = part.getType();
         String partName = part.getName();
         QName xmlName = new QName(partName);

         ParameterMetaData pmd = buildInputParameter(opMetaData, wsdlOperation, seiMethodMapping, typeMapping, partName, xmlName, xmlType, wsdlPosition++, false);

         setupXOPAttachmentParameter(wsdlOperation, pmd);
         setupSOAPArrayParameter(pmd);
      }

      wsdlPosition = processBindingParameters(opMetaData, wsdlOperation, seiMethodMapping, typeMapping, bindingOperation, wsdlPosition);

      WSDLInterfaceOperationOutput[] outputs = wsdlOperation.getOutputs();
      if (outputs.length > 0)
      {
         WSDLInterfaceOperationOutput output = outputs[0];
         for (WSDLRPCPart part : output.getChildParts())
         {
            String partName = part.getName();

            ParameterMetaData outMetaData = opMetaData.getParameter(new QName(partName));
            if (outMetaData != null)
            {
               outMetaData.setMode(ParameterMode.INOUT);
            }
            else
            {
               QName xmlName = new QName(partName);
               QName xmlType = part.getType();

               ParameterMetaData pmd = buildOutputParameter(opMetaData, wsdlOperation, seiMethodMapping, wsdlPosition, partName, xmlName, xmlType, typeMapping, false);
               if (opMetaData.getReturnParameter() != pmd)
                  wsdlPosition++;

               setupXOPAttachmentParameter(wsdlOperation, pmd);
               setupSOAPArrayParameter(pmd);
            }
         }

         processBindingOutputParameters(opMetaData, wsdlOperation, seiMethodMapping, typeMapping, bindingOperation, wsdlPosition);
      }
      else if (wsdlOperation.getPattern() != Constants.WSDL20_PATTERN_IN_ONLY)
      {
         throw new WSException("RPC style was missing an output, and was not an IN-ONLY MEP.");
      }
   }

   private int processDocElement(OperationMetaData operation, WSDLInterfaceOperation wsdlOperation, WSDLBindingOperation bindingOperation,
         ServiceEndpointMethodMapping seiMethodMapping, TypeMappingImpl typeMapping, List<WrappedParameter> wrappedParameters,
         List<WrappedParameter> wrappedResponseParameters)
   {
      WSDLInterfaceOperationInput input = wsdlOperation.getInputs()[0];
      WSDLBindingOperationInput bindingInput = bindingOperation.getInputs()[0];
      int wsdlPosition;

      QName xmlName = input.getElement();
      QName xmlType = input.getXMLType();
      String javaTypeName = typeMapping.getJavaTypeName(xmlType);

      TypesMetaData typesMetaData = operation.getEndpointMetaData().getServiceMetaData().getTypesMetaData();
      TypeMappingMetaData typeMetaData = typesMetaData.getTypeMappingByXMLType(xmlType);
      if (typeMetaData != null)
         javaTypeName = typeMetaData.getJavaTypeName();

      if (javaTypeName == null)
         throw new WSException("Cannot obtain java type mapping for: " + xmlType);

      // Check if we need to wrap the parameters
      boolean isWrapped = isWrapped(seiMethodMapping, javaTypeName);
      operation.getEndpointMetaData().setParameterStyle(isWrapped ? ParameterStyle.WRAPPED : ParameterStyle.BARE);

      ParameterMetaData inMetaData = new ParameterMetaData(operation, xmlName, xmlType, javaTypeName);
      operation.addParameter(inMetaData);

      // Set the variable names
      if (inMetaData.getOperationMetaData().isDocumentWrapped())
      {
         if (seiMethodMapping == null)
            throw new IllegalArgumentException("Cannot wrap parameters without SEI method mapping");

         ServiceEndpointInterfaceMapping seiMapping = seiMethodMapping.getServiceEndpointInterfaceMapping();
         JavaXmlTypeMapping javaXmlTypeMapping = seiMapping.getJavaWsdlMapping().getTypeMappingForQName(xmlType);
         if (javaXmlTypeMapping == null)
            throw new WSException("Cannot obtain java/xml type mapping for: " + xmlType);

         Map<String, String> variableMap = createVariableMappingMap(javaXmlTypeMapping.getVariableMappings());
         for (MethodParamPartsMapping partMapping : seiMethodMapping.getMethodParamPartsMappings())
         {
            WsdlMessageMapping wsdlMessageMapping = partMapping.getWsdlMessageMapping();
            if (wsdlMessageMapping.isSoapHeader())
               continue;

            if (wsdlMessageMapping == null)
               throw new IllegalArgumentException("wsdl-message-message mapping required for document/literal wrapped");

            String elementName = wsdlMessageMapping.getWsdlMessagePartName();

            // Skip attachments
            if (bindingInput.getMimePart(elementName) != null)
               continue;

            String variable = variableMap.get(elementName);
            if (variable == null)
               throw new IllegalArgumentException("Could not determine variable name for element: " + elementName);

            WrappedParameter wrapped = new WrappedParameter(new QName(elementName), partMapping.getParamType(), variable, partMapping.getParamPosition());

            String parameterMode = wsdlMessageMapping.getParameterMode();
            if (parameterMode == null || parameterMode.length() < 2)
               throw new IllegalArgumentException("Invalid parameter mode for element: " + elementName);

            if (!"OUT".equals(parameterMode))
               wrappedParameters.add(wrapped);
            if (!"IN".equals(parameterMode))
            {
               wrapped.setHolder(true);
               // wrapped parameters can not be shared between request/response objects (accessors)
               if ("INOUT".equals(parameterMode))
                  wrapped = new WrappedParameter(wrapped);
               wrappedResponseParameters.add(wrapped);
            }
         }
         inMetaData.setWrappedParameters(wrappedParameters);
         wsdlPosition = wrappedParameters.size();
      }
      else
      {
         if (seiMethodMapping != null)
         {
            MethodParamPartsMapping part = seiMethodMapping.getMethodParamPartsMappingByPartName(input.getPartName());
            if (part != null)
            {
               inMetaData.setJavaTypeName(part.getParamType());
               inMetaData.setIndex(part.getParamPosition());
            }
         }

         setupXOPAttachmentParameter(wsdlOperation, inMetaData);
         wsdlPosition = 1;
      }

      return wsdlPosition;
   }

   private boolean isWrapped(ServiceEndpointMethodMapping seiMethodMapping, String javaTypeName)
   {
      boolean isWrapParameters = (seiMethodMapping != null ? seiMethodMapping.isWrappedElement() : false);
      log.trace("isWrapParameters based on wrapped-element: " + isWrapParameters);
      if (isWrapParameters == false && seiMethodMapping != null)
      {

         MethodParamPartsMapping[] partsMappings = seiMethodMapping.getMethodParamPartsMappings();
         if (partsMappings.length > 0)
         {
            List<String> anyTypes = new ArrayList<String>();
            anyTypes.add("javax.xml.soap.SOAPElement");
            anyTypes.add("org.w3c.dom.Element");

            boolean matchingPartFound = false;
            for (MethodParamPartsMapping partsMapping : partsMappings)
            {
               String methodMappingTypeName = partsMapping.getParamType();
               if (methodMappingTypeName.equals(javaTypeName))
               {
                  matchingPartFound = true;
                  break;
               }
               // Check xsd:anyType
               else if (anyTypes.contains(javaTypeName) && anyTypes.contains(methodMappingTypeName))
               {
                  matchingPartFound = true;
                  break;
               }
               // Check assignability,
               else
               {
                  try
                  {
                     Class paramType = JavaUtils.loadJavaType(methodMappingTypeName);
                     Class javaType = JavaUtils.loadJavaType(javaTypeName);

                     if (JavaUtils.isAssignableFrom(javaType, paramType))
                     {
                        matchingPartFound = true;
                        break;
                     }
                  }
                  catch (ClassNotFoundException e)
                  {
                     // Ignore. For simple types this should work, others should
                     // be lexically equal if it is not wrapped.
                  }
               }
            }
            // Do we really want to continue to handle invalid mappings?
            isWrapParameters = (matchingPartFound == false);
            log.trace("isWrapParameters based on matching parts: " + isWrapParameters);
         }
      }
      return isWrapParameters;
   }

   private int processOutputDocElement(OperationMetaData opMetaData, WSDLInterfaceOperation wsdlOperation, ServiceEndpointMethodMapping seiMethodMapping,
         TypeMappingImpl typeMapping, List<WrappedParameter> wrappedResponseParameters, int wsdlPosition)
   {
      WSDLInterfaceOperationOutput opOutput = wsdlOperation.getOutputs()[0];
      QName xmlName = opOutput.getElement();
      QName xmlType = opOutput.getXMLType();

      String javaTypeName = typeMapping.getJavaTypeName(xmlType);

      TypesMetaData typesMetaData = opMetaData.getEndpointMetaData().getServiceMetaData().getTypesMetaData();
      if (typesMetaData.getTypeMappingByXMLType(xmlType) != null)
         javaTypeName = typesMetaData.getTypeMappingByXMLType(xmlType).getJavaTypeName();

      if (javaTypeName == null)
         throw new WSException("Cannot obtain java/xml type mapping for: " + xmlType);

      ParameterMetaData outMetaData = new ParameterMetaData(opMetaData, xmlName, xmlType, javaTypeName);

      boolean hasReturnMapping = true;
      if (opMetaData.isDocumentWrapped())
      {
         if (seiMethodMapping == null)
            throw new IllegalArgumentException("Cannot wrap parameters without SEI method mapping");

         WsdlReturnValueMapping returnValueMapping = seiMethodMapping.getWsdlReturnValueMapping();
         if (returnValueMapping != null)
         {
            ServiceEndpointInterfaceMapping seiMapping = seiMethodMapping.getServiceEndpointInterfaceMapping();
            JavaWsdlMapping javaWsdlMapping = seiMapping.getJavaWsdlMapping();
            JavaXmlTypeMapping javaXmlTypeMapping = javaWsdlMapping.getTypeMappingForQName(xmlType);
            if (javaXmlTypeMapping == null)
               throw new WSException("Cannot obtain java/xml type mapping for: " + xmlType);

            Map<String, String> map = createVariableMappingMap(javaXmlTypeMapping.getVariableMappings());
            if (map.size() > 0)
            {
               String elementName = returnValueMapping.getWsdlMessagePartName();
               String variable = map.get(elementName);
               if (variable == null)
                  throw new IllegalArgumentException("Could not determine variable name for element: " + elementName);

               String wrappedType = returnValueMapping.getMethodReturnValue();
               QName element = new QName(elementName);
               WrappedParameter wrappedParameter = new WrappedParameter(element, wrappedType, variable, WrappedParameter.RETURN);
               wrappedResponseParameters.add(0, wrappedParameter);
            }
         }

         outMetaData.setWrappedParameters(wrappedResponseParameters);
      }
      else
      {
         if (seiMethodMapping != null)
         {
            MethodParamPartsMapping part = seiMethodMapping.getMethodParamPartsMappingByPartName(opOutput.getPartName());
            String mode = null;
            if (part != null)
            {
               WsdlMessageMapping wsdlMessageMapping = part.getWsdlMessageMapping();
               mode = wsdlMessageMapping.getParameterMode();
            }
            if ("INOUT".equals(mode))
            {
               ParameterMetaData inMetaData = opMetaData.getParameter(xmlName);
               if (inMetaData != null)
               {
                  inMetaData.setMode(ParameterMode.INOUT);
                  return wsdlPosition;
               }

               throw new WSException("Could not update IN parameter to be INOUT, as indicated in the mapping: " + opOutput.getPartName());
            }
            // It's potentialy possible that an input parameter could exist with the same part name
            else if ("OUT".equals(mode))
            {
               hasReturnMapping = false;
               javaTypeName = part.getParamType();
               outMetaData.setIndex(part.getParamPosition());
               outMetaData.setJavaTypeName(javaTypeName);
            }
            else
            {
               WsdlReturnValueMapping returnValueMapping = seiMethodMapping.getWsdlReturnValueMapping();
               if (returnValueMapping != null)
               {
                  javaTypeName = returnValueMapping.getMethodReturnValue();
                  outMetaData.setJavaTypeName(javaTypeName);
               }
            }
         }

         setupXOPAttachmentParameter(wsdlOperation, outMetaData);
         setupSOAPArrayParameter(outMetaData);
      }

      if (hasReturnMapping)
      {
         opMetaData.setReturnParameter(outMetaData);
      }
      else
      {
         opMetaData.addParameter(outMetaData);
         outMetaData.setMode(ParameterMode.OUT);
         wsdlPosition++;
      }

      return wsdlPosition;
   }

   private void buildParameterMetaDataDoc(OperationMetaData opMetaData, WSDLInterfaceOperation wsdlOperation, ServiceEndpointMethodMapping seiMethodMapping,
         TypeMappingImpl typeMapping)
   {
      log.trace("buildParameterMetaDataDoc: " + opMetaData.getQName());

      WSDLBindingOperation bindingOperation = wsdlOperation.getBindingOperation();
      if (bindingOperation == null)
         throw new WSException("Could not locate binding operation for:" + bindingOperation);

      List<WrappedParameter> wrappedParameters = new ArrayList<WrappedParameter>();
      List<WrappedParameter> wrappedResponseParameters = new ArrayList<WrappedParameter>();

      int wsdlPosition = 0;
      // WS-I BP 1.0 allows document/literal bare to have zero message parts
      if (wsdlOperation.getInputs().length > 0)
      {
         wsdlPosition = processDocElement(opMetaData, wsdlOperation, bindingOperation, seiMethodMapping, typeMapping, wrappedParameters, wrappedResponseParameters);
         wsdlPosition = processBindingParameters(opMetaData, wsdlOperation, seiMethodMapping, typeMapping, bindingOperation, wsdlPosition);
      }
      else
      {
         // Set the default to bare in case there isn't an input object, revisit this
         opMetaData.getEndpointMetaData().setParameterStyle(ParameterStyle.BARE);
      }

      if (wsdlOperation.getOutputs().length > 0)
      {
         wsdlPosition = processOutputDocElement(opMetaData, wsdlOperation, seiMethodMapping, typeMapping, wrappedResponseParameters, wsdlPosition);
         wsdlPosition = processBindingOutputParameters(opMetaData, wsdlOperation, seiMethodMapping, typeMapping, bindingOperation, wsdlPosition);
      }
   }

   /**
    * Build default action according to the pattern described in
    * http://www.w3.org/Submission/2004/SUBM-ws-addressing-20040810/
    * Section 3.3.2 'Default Action Pattern'<br>
    * [target namespace]/[port type name]/[input|output name]
    *
    * @param wsdlOperation
    * @return action value
    */
   private String buildWsaActionValue(WSDLInterfaceOperation wsdlOperation)
   {
      WSDLProperty wsaAction = wsdlOperation.getProperty(Constants.WSDL_ATTRIBUTE_WSA_ACTION.toString());
      String actionValue = null;

      if (null == wsaAction)
      {

         String tns = wsdlOperation.getName().getNamespaceURI();
         String portTypeName = wsdlOperation.getName().getLocalPart();
         WSDLProperty messageName = wsdlOperation.getProperty("http://www.jboss.org/jbossws/messagename/in");

         actionValue = new String(tns + "/" + portTypeName + "/" + messageName.getValue());
      }
      else
      {
         actionValue = wsaAction.getValue();
      }

      return actionValue;
   }

   protected void buildFaultMetaData(OperationMetaData opMetaData, WSDLInterfaceOperation wsdlOperation)
   {
      TypesMetaData typesMetaData = opMetaData.getEndpointMetaData().getServiceMetaData().getTypesMetaData();

      WSDLInterface wsdlInterface = wsdlOperation.getWsdlInterface();
      for (WSDLInterfaceOperationOutfault outFault : wsdlOperation.getOutfaults())
      {
         QName ref = outFault.getRef();

         WSDLInterfaceFault wsdlFault = wsdlInterface.getFault(ref);
         QName xmlName = wsdlFault.getElement();
         QName xmlType = wsdlFault.getXmlType();
         String javaTypeName = null;

         if (xmlType == null)
         {
            log.warn("Cannot obtain fault type for element: " + xmlName);
            xmlType = xmlName;
         }

         TypeMappingMetaData tmMetaData = typesMetaData.getTypeMappingByXMLType(xmlType);
         if (tmMetaData != null)
            javaTypeName = tmMetaData.getJavaTypeName();

         if (javaTypeName == null)
         {
            log.warn("Cannot obtain java type mapping for: " + xmlType);
            javaTypeName = new UnqualifiedFaultException(xmlType).getClass().getName();
         }

         FaultMetaData faultMetaData = new FaultMetaData(opMetaData, xmlName, xmlType, javaTypeName);
         opMetaData.addFault(faultMetaData);
      }
   }

}
