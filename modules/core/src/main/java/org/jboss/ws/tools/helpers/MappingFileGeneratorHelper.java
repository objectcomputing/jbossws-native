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

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
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
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceFault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceMessageReference;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLProperty;
import org.jboss.ws.metadata.wsdl.WSDLRPCPart;
import org.jboss.ws.metadata.wsdl.WSDLSOAPHeader;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.ws.tools.HeaderUtil;
import org.jboss.ws.tools.NamespacePackageMapping;
import org.jboss.ws.tools.RPCSignature;
import org.jboss.ws.tools.ToolsUtils;
import org.jboss.ws.tools.WSToolsConstants;
import org.jboss.ws.tools.mapping.MappingFileGenerator;
import org.jboss.wsf.common.JavaUtils;
import org.w3c.dom.Element;

/**
 *  Helper class for MappingFileGenerator (only client of this class)
 *  @see MappingFileGenerator
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Sep 18, 2005
 */
public class MappingFileGeneratorHelper
{
   // provide logging
   private static final Logger log = Logger.getLogger(MappingFileGeneratorHelper.class);
   private WSDLDefinitions wsdlDefinitions = null;
   private String typeNamespace;
   private String serviceName = null;
   private String packageName = null;
   private Map<String, String> namespacePackageMap = new HashMap<String, String>();
   private Set<String> registeredTypes = new HashSet<String>();
   private Set<String> registeredExceptions = new HashSet<String>();

   private LiteralTypeMapping typeMapping = null;
   private String wsdlStyle;

   private WSDLUtils utils = WSDLUtils.getInstance();

   private String parameterStyle;

   public MappingFileGeneratorHelper(WSDLDefinitions wsdl, String sname, Map<String, String> map, Class seiClass, LiteralTypeMapping ltm, String paramStyle)
   {
      this.wsdlDefinitions = wsdl;
      this.serviceName = sname;
      String targetNS = wsdl.getTargetNamespace();
      packageName = NamespacePackageMapping.getJavaPackageName(targetNS);
      this.namespacePackageMap = map;
      this.typeMapping = ltm;

      this.wsdlStyle = utils.getWSDLStyle(wsdl);
      this.parameterStyle = paramStyle;
      checkEssentials();
   }

   /**
    * Returns the type namespace that was discovered during generation.
    */
   public String getTypeNamespace()
   {
      return typeNamespace;
   }

   public PackageMapping constructPackageMapping(JavaWsdlMapping jwm, String packageType, String ns)
   {
      PackageMapping pk = new PackageMapping(jwm);
      pk.setPackageType(packageType);
      pk.setNamespaceURI(ns);
      return pk;
   }

   public ServiceInterfaceMapping constructServiceInterfaceMapping(JavaWsdlMapping jwm, WSDLService ser)
   {
      serviceName = ser.getName().getLocalPart();
      String javaServiceName = serviceName;
      //Check if the serviceName conflicts with a portType or interface name
      if (wsdlDefinitions.getInterface(new QName(wsdlDefinitions.getTargetNamespace(), serviceName)) != null)
         javaServiceName += "_Service";

      if (this.serviceName == null || serviceName.length() == 0)
         throw new IllegalArgumentException("MappingFileGenerator:Service Name is null");

      String targetNS = wsdlDefinitions.getTargetNamespace();
      String prefix = WSToolsConstants.WSTOOLS_CONSTANT_MAPPING_SERVICE_PREFIX;
      ServiceInterfaceMapping sim = new ServiceInterfaceMapping(jwm);
      String className = ToolsUtils.firstLetterUpperCase(javaServiceName);
      sim.setServiceInterface(getPackageName(targetNS) + "." + className);
      sim.setWsdlServiceName(new QName(targetNS, serviceName, prefix));

      WSDLEndpoint[] endpoints = ser.getEndpoints();
      int lenendpoints = 0;
      if (endpoints != null)
         lenendpoints = endpoints.length;
      for (int j = 0; j < lenendpoints; j++)
      {
         WSDLEndpoint endpt = endpoints[j];
         String portname = endpt.getName().getLocalPart();
         //port mapping
         PortMapping pm = new PortMapping(sim);
         pm.setPortName(portname);
         pm.setJavaPortName(portname);
         sim.addPortMapping(pm);
      }
      return sim;
   }

   public void constructServiceEndpointInterfaceMapping(JavaWsdlMapping jwm, WSDLService ser)
   {
      serviceName = ser.getName().getLocalPart();
      if (this.serviceName == null || serviceName.length() == 0)
         throw new IllegalArgumentException("MappingFileGenerator:Service Name is null");

      String targetNS = wsdlDefinitions.getTargetNamespace();

      WSDLEndpoint[] endpoints = ser.getEndpoints();
      int lenendpoints = 0;
      if (endpoints != null)
         lenendpoints = endpoints.length;
      for (int j = 0; j < lenendpoints; j++)
      {
         WSDLEndpoint endpt = endpoints[j];
         QName binding = endpt.getBinding();
         WSDLBinding wsdlbind = wsdlDefinitions.getBinding(binding);
         String bindName = wsdlbind.getName().getLocalPart();
         QName portTypeName = wsdlbind.getInterfaceName();
         WSDLInterface wsdlintf = wsdlDefinitions.getInterface(portTypeName);
         String portName = wsdlintf.getName().getLocalPart();
         String javaPortName = utils.chopPortType(portName);
         if (wsdlDefinitions.getService(javaPortName) != null)
            javaPortName += "_PortType";
         javaPortName = ToolsUtils.convertInvalidCharacters(javaPortName);

         ServiceEndpointInterfaceMapping seim = new ServiceEndpointInterfaceMapping(jwm);
         String className = ToolsUtils.firstLetterUpperCase(javaPortName);
         seim.setServiceEndpointInterface(getPackageName(targetNS) + "." + className);
         seim.setWsdlPortType(new QName(targetNS, portName, "portTypeNS"));
         seim.setWsdlBinding(new QName(targetNS, bindName, "bindingNS"));
         constructServiceEndpointMethodMapping(seim, wsdlintf);

         jwm.addServiceEndpointInterfaceMappings(seim);
      }
   }

   public void constructServiceEndpointMethodMapping(ServiceEndpointInterfaceMapping seim, WSDLInterface intf)
   {
      WSDLInterfaceOperation[] wioparr = intf.getOperations();
      int len = 0;
      if (wioparr != null)
         len = wioparr.length;
      for (int j = 0; j < len; j++)
      {
         WSDLInterfaceOperation wiop = wioparr[j];
         String opname = wiop.getName().getLocalPart();
         ServiceEndpointMethodMapping semm = new ServiceEndpointMethodMapping(seim);
         semm.setJavaMethodName(ToolsUtils.firstLetterLowerCase(opname));
         semm.setWsdlOperation(opname);
         semm.setWrappedElement(isWrapped());

         WSDLBindingOperation bindingOperation = HeaderUtil.getWSDLBindingOperation(wsdlDefinitions, wiop);

         if (isDocStyle())
         {
            constructDOCParameters(semm, wiop, bindingOperation);
         }
         else
         {
            constructRPCParameters(semm, wiop, bindingOperation);
         }

         seim.addServiceEndpointMethodMapping(semm);
      }
   }

   private void constructDOCParameters(ServiceEndpointMethodMapping semm, WSDLInterfaceOperation wiop, WSDLBindingOperation bindingOperation)
   {
      WSDLInterfaceOperationInput win = WSDLUtils.getWsdl11Input(wiop);
      WSDLInterfaceOperationOutput output = WSDLUtils.getWsdl11Output(wiop);

      JBossXSModel schemaModel = WSDLUtils.getSchemaModel(wsdlDefinitions.getWsdlTypes());
      MethodParamPartsMapping mpin = null;

      boolean holder = false;

      if (win != null)
      {
         QName xmlName = win.getElement();
         QName xmlType = win.getXMLType();
         String partName = win.getPartName();
         String wsdlMessageName = win.getMessageName().getLocalPart();
         XSTypeDefinition xt = schemaModel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());

         boolean wrapped = isWrapped();

         if (wrapped)
         {
            wrapped = unwrapRequest(semm, wsdlMessageName, xmlName.getLocalPart(), xt);
         }

         if (wrapped == false)
         {
            if (xt instanceof XSSimpleTypeDefinition)
               xmlType = SchemaUtils.handleSimpleType((XSSimpleTypeDefinition)xt);
            String paramMode = "IN";
            holder = output != null && xmlName.equals(output.getElement());
            if (holder == true)
            {
               paramMode = "INOUT";
            }

            mpin = getMethodParamPartsMapping(semm, xmlName, xmlType, 0, wsdlMessageName, paramMode, partName, false, true);
            semm.addMethodParamPartsMapping(mpin);
         }
      }

      if (holder == false && output != null)
      {
         QName xmlName = output.getElement();
         QName xmlType = output.getXMLType();
         boolean primitive = true;

         String targetNS = wsdlDefinitions.getTargetNamespace();
         QName messageName = new QName(targetNS, output.getMessageName().getLocalPart(), WSToolsConstants.WSTOOLS_CONSTANT_MAPPING_WSDL_MESSAGE_NS);

         String partName = output.getPartName();
         String containingElement = xmlName.getLocalPart();
         boolean array = false;
         XSTypeDefinition xt = schemaModel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());

         ReturnTypeUnwrapper unwrapper = new ReturnTypeUnwrapper(xmlType, schemaModel, isWrapped());
         if (unwrapper.unwrap())
         {
            if (unwrapper.unwrappedElement != null)
            {
               XSElementDeclaration element = unwrapper.unwrappedElement;
               xt = element.getTypeDefinition();
               primitive = unwrapper.primitive;
               partName = element.getName();
               containingElement = containingElement + ToolsUtils.firstLetterUpperCase(unwrapper.unwrappedElement.getName());
               array = unwrapper.array;
               if (xt.getAnonymous())
               {
                  xmlType = new QName(containingElement);
               }
               else if (unwrapper.xmlType != null)
               {
                  xmlType = unwrapper.xmlType;
               }
            }
         }

         //Check it is a holder.
         if (wiop.getInputByPartName(xmlName.getLocalPart()) == null)
         {
            String nameSpace = null;
            if (xt != null)
            {
               nameSpace = xt.getNamespace();
            }
            if (xt instanceof XSSimpleTypeDefinition)
               xmlType = SchemaUtils.handleSimpleType((XSSimpleTypeDefinition)xt);

            String javaType = getJavaTypeAsString(xmlName, xmlType, nameSpace, array, primitive);

            if ((isDocStyle() == false && "void".equals(javaType)) == false)
            {
               WsdlReturnValueMapping wrvm = new WsdlReturnValueMapping(semm);
               wrvm.setMethodReturnValue(javaType);
               wrvm.setWsdlMessage(messageName);
               wrvm.setWsdlMessagePartName(partName);
               semm.setWsdlReturnValueMapping(wrvm);
            }
         }
      }

      if (bindingOperation != null)
      {
         constructHeaderParameters(semm, wiop, bindingOperation);
      }
   }

   private void constructHeaderParameters(ServiceEndpointMethodMapping semm, WSDLInterfaceOperation wiop, WSDLBindingOperation bindingOperation)
   {
      WSDLSOAPHeader[] inputHeaders = HeaderUtil.getSignatureHeaders(bindingOperation.getInputs());
      WSDLSOAPHeader[] outputHeaders = HeaderUtil.getSignatureHeaders(bindingOperation.getOutputs());

      String wsdlMessageName = bindingOperation.getWsdlBinding().getInterface().getName().getLocalPart();
      int paramPosition = 1;

      for (WSDLSOAPHeader currentInput : inputHeaders)
      {
         boolean inOutput = HeaderUtil.containsMatchingPart(outputHeaders, currentInput);
         String mode = getMode(true, inOutput);

         constructHeaderParameter(semm, currentInput, paramPosition++, wsdlMessageName, mode);
      }

      for (WSDLSOAPHeader currentOutput : outputHeaders)
      {
         boolean inInput = HeaderUtil.containsMatchingPart(inputHeaders, currentOutput);

         if (inInput == true)
            continue;

         constructHeaderParameter(semm, currentOutput, paramPosition++, wsdlMessageName, "OUT");
      }
   }

   private void constructHeaderParameter(ServiceEndpointMethodMapping semm, WSDLSOAPHeader header, int paramPosition, String wsdlMessageName, String mode)
   {
      QName elementName = header.getElement();

      JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdlDefinitions.getWsdlTypes());
      XSElementDeclaration xe = xsmodel.getElementDeclaration(elementName.getLocalPart(), elementName.getNamespaceURI());
      XSTypeDefinition xt = xe.getTypeDefinition();
      WSDLTypes wsdlTypes = wsdlDefinitions.getWsdlTypes();
      QName xmlType = wsdlTypes.getXMLType(header.getElement());

      // Replace the xt with the real type from the schema.
      xt = xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());
      if (xt instanceof XSSimpleTypeDefinition)
         xmlType = SchemaUtils.handleSimpleType((XSSimpleTypeDefinition)xt);
      String partName = header.getPartName();

      MethodParamPartsMapping mpin = getMethodParamPartsMapping(semm, elementName, xmlType, paramPosition, wsdlMessageName, mode, partName, false, true);
      semm.addMethodParamPartsMapping(mpin);
   }

   private String getMode(final boolean input, final boolean output)
   {
      if (input == true & output == true)
      {
         return "INOUT";
      }
      else if (input == true)
      {
         return "IN";
      }
      else if (output == true)
      {
         return "OUT";
      }

      return "";
   }

   private String getMode(WSDLInterfaceOperation op, String name)
   {
      WSDLInterfaceOperationInput in = WSDLUtils.getWsdl11Input(op);
      WSDLInterfaceOperationOutput out = WSDLUtils.getWsdl11Output(op);

      boolean i = false, o = false;
      if (in != null && in.getChildPart(name) != null)
         i = true;
      if (out != null && out.getChildPart(name) != null)
         o = true;

      if (i && o)
         return "INOUT";

      if (o)
         return "OUT";

      return "IN";
   }

   private void constructRPCParameters(ServiceEndpointMethodMapping semm, WSDLInterfaceOperation wiop, WSDLBindingOperation bindingOperation)
   {
      WSDLInterfaceOperationInput win = WSDLUtils.getWsdl11Input(wiop);
      if (win == null)
         throw new WSException("RPC endpoints require an input message");
      String wsdlMessageName = win.getMessageName().getLocalPart();
      JBossXSModel schemaModel = WSDLUtils.getSchemaModel(wsdlDefinitions.getWsdlTypes());

      RPCSignature signature = new RPCSignature(wiop);
      int i = 0;
      for (WSDLRPCPart part : signature.parameters())
      {
         String partName = part.getName();
         QName xmlName = new QName(partName);
         QName xmlType = part.getType();

         XSTypeDefinition xt = schemaModel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());
         if (xt instanceof XSSimpleTypeDefinition)
            xmlType = SchemaUtils.handleSimpleType((XSSimpleTypeDefinition)xt);

         MethodParamPartsMapping mpin = getMethodParamPartsMapping(semm, xmlName, xmlType, i++, wsdlMessageName, getMode(wiop, part.getName()), partName, false, true);

         semm.addMethodParamPartsMapping(mpin);
      }

      WSDLRPCPart returnParameter = signature.returnParameter();
      if (returnParameter != null)
      {
         String partName = returnParameter.getName();
         QName xmlName = new QName(partName);
         QName xmlType = returnParameter.getType();

         XSTypeDefinition xt = schemaModel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());
         String nameSpace = null;
         if (xt != null)
         {
            nameSpace = xt.getNamespace();
         }
         if (xt instanceof XSSimpleTypeDefinition)
         {
            xmlType = SchemaUtils.handleSimpleType((XSSimpleTypeDefinition)xt);
         }

         WsdlReturnValueMapping wrvm = new WsdlReturnValueMapping(semm);
         wrvm.setMethodReturnValue(getJavaTypeAsString(xmlName, xmlType, nameSpace, false, true));
         QName messageName = WSDLUtils.getWsdl11Output(wiop).getMessageName();

         wrvm.setWsdlMessage(new QName(messageName.getNamespaceURI(), messageName.getLocalPart(), WSToolsConstants.WSTOOLS_CONSTANT_MAPPING_WSDL_MESSAGE_NS));
         wrvm.setWsdlMessagePartName(partName);
         semm.setWsdlReturnValueMapping(wrvm);
      }

      if (bindingOperation != null)
      {
         constructHeaderParameters(semm, wiop, bindingOperation);
      }
   }

   public void constructJavaXmlTypeMapping(JavaWsdlMapping jwm)
   {
      WSDLInterface[] intfArr = wsdlDefinitions.getInterfaces();
      int len = intfArr != null ? intfArr.length : 0;
      for (int i = 0; i < len; i++)
      {
         WSDLInterface wi = intfArr[i];
         WSDLInterfaceOperation[] ops = wi.getOperations();
         int lenOps = ops.length;
         for (int j = 0; j < lenOps; j++)
         {
            WSDLInterfaceOperation op = ops[j];
            for (WSDLInterfaceOperationInput input : op.getInputs())
            {
               if (isDocStyle())
               {
                  XSTypeDefinition xt = getXSType(input);
                  // Don't unwrap the actual parameter.
                  addJavaXMLTypeMap(xt, input.getElement().getLocalPart(), "", "", jwm, false);
               }
               else
               {
                  for (WSDLRPCPart part : input.getChildParts())
                     addJavaXMLTypeMap(getXSType(part.getType()), input.getElement().getLocalPart(), "", "", jwm, true);
               }
            }

            for (WSDLInterfaceOperationOutput output : op.getOutputs())
            {
               if (isDocStyle())
               {
                  XSTypeDefinition xt = getXSType(output);
                  // Don't unwrap the response type.
                  addJavaXMLTypeMap(xt, output.getElement().getLocalPart(), "", "", jwm, false);
               }
               else
               {
                  for (WSDLRPCPart part : output.getChildParts())
                     addJavaXMLTypeMap(getXSType(part.getType()), output.getElement().getLocalPart(), "", "", jwm, true);
               }
            }

            for (WSDLInterfaceFault fault : wi.getFaults())
            {
               QName xmlType = fault.getXmlType();
               QName xmlName = fault.getElement();

               WSDLTypes types = wsdlDefinitions.getWsdlTypes();
               JBossXSModel xsmodel = WSDLUtils.getSchemaModel(types);
               XSTypeDefinition xt = xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());
               addJavaXMLTypeMap(xt, xmlName.getLocalPart(), "", "", jwm, true);

               String exceptionType = getJavaTypeAsString(null, xmlType, xt.getNamespace(), false, true);

               if (registeredExceptions.contains(exceptionType) == false)
               {
                  registeredExceptions.add(exceptionType);
                  ExceptionMapping exceptionMapping = new ExceptionMapping(jwm);
                  exceptionMapping.setExceptionType(exceptionType);
                  exceptionMapping.setWsdlMessage(fault.getName());
                  jwm.addExceptionMappings(exceptionMapping);
               }
            }

            WSDLBindingOperation bindingOperation = HeaderUtil.getWSDLBindingOperation(wsdlDefinitions, op);
            if (bindingOperation != null)
            {
               constructHeaderJavaXmlTypeMapping(jwm, HeaderUtil.getSignatureHeaders(bindingOperation.getInputs()));
               constructHeaderJavaXmlTypeMapping(jwm, HeaderUtil.getSignatureHeaders(bindingOperation.getOutputs()));
            }

         }//end for
      }
   }

   public void constructHeaderJavaXmlTypeMapping(JavaWsdlMapping jwm, WSDLSOAPHeader[] headers)
   {
      for (WSDLSOAPHeader current : headers)
      {
         QName elementName = current.getElement();

         JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdlDefinitions.getWsdlTypes());
         XSElementDeclaration xe = xsmodel.getElementDeclaration(elementName.getLocalPart(), elementName.getNamespaceURI());
         XSTypeDefinition xt = xe.getTypeDefinition();

         addJavaXMLTypeMap(xt, elementName.getLocalPart(), "", "", jwm, true);
      }
   }

   private boolean unwrapRequest(ServiceEndpointMethodMapping methodMapping, String messageName, String containingElement, XSTypeDefinition xt)
   {
      if (xt instanceof XSComplexTypeDefinition == false)
         throw new WSException("Tried to unwrap a non-complex type.");

      List<MethodParamPartsMapping> partsMappings = new ArrayList<MethodParamPartsMapping>();

      XSComplexTypeDefinition wrapper = (XSComplexTypeDefinition)xt;

      if (wrapper.getAttributeUses().getLength() > 0)
         return false;

      boolean unwrappedElement = false;

      XSParticle particle = wrapper.getParticle();
      if (particle == null)
      {
         return true;
      }
      else
      {
         XSTerm term = particle.getTerm();
         if (term instanceof XSModelGroup)
         {
            unwrappedElement = unwrapGroup(partsMappings, methodMapping, messageName, containingElement, (XSModelGroup)term);
         }
      }

      if (unwrappedElement)
      {
         addMethodParamPartsMappings(partsMappings, methodMapping);

         return true;
      }

      return false;
   }

   private void addMethodParamPartsMappings(List<MethodParamPartsMapping> partsMappings, ServiceEndpointMethodMapping methodMapping)
   {
      for (MethodParamPartsMapping current : partsMappings)
      {
         methodMapping.addMethodParamPartsMapping(current);
      }
   }

   private boolean unwrapGroup(List<MethodParamPartsMapping> partsMappings, ServiceEndpointMethodMapping methodMapping, String messageName, String containingElement,
         XSModelGroup group)
   {
      if (group.getCompositor() != XSModelGroup.COMPOSITOR_SEQUENCE)
         return false;

      XSObjectList particles = group.getParticles();
      for (int i = 0; i < particles.getLength(); i++)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         XSTerm term = particle.getTerm();
         if (term instanceof XSModelGroup)
         {
            if (unwrapGroup(partsMappings, methodMapping, messageName, containingElement, (XSModelGroup)term) == false)
               return false;
         }
         else if (term instanceof XSElementDeclaration)
         {
            XSElementDeclaration element = (XSElementDeclaration)term;
            XSTypeDefinition type = element.getTypeDefinition();

            QName xmlName = new QName(element.getNamespace(), element.getName());
            QName xmlType;
            if (type.getAnonymous())
            {
               String tempName = ToolsUtils.firstLetterUpperCase(containingElement) + ToolsUtils.firstLetterUpperCase(element.getName());
               xmlType = new QName(type.getNamespace(), tempName);
            }
            else
            {
               xmlType = new QName(type.getNamespace(), type.getName());
            }

            boolean array = particle.getMaxOccursUnbounded() || particle.getMaxOccurs() > 1;
            boolean primitive = !(element.getNillable() || (particle.getMinOccurs() == 0 && particle.getMaxOccurs() == 1));

            MethodParamPartsMapping part = getMethodParamPartsMapping(methodMapping, xmlName, xmlType, partsMappings.size(), messageName, "IN", xmlName.getLocalPart(),
                  array, primitive);
            partsMappings.add(part);
         }
      }

      //    If we reach here we must have successfully unwrapped the parameters.
      return true;
   }

   private void checkEssentials()
   {
      if (typeMapping == null)
         throw new WSException("typeMapping is null");
   }

   private XSTypeDefinition getXSType(WSDLInterfaceMessageReference part)
   {
      //Check if there are any custom properties
      WSDLInterfaceOperation op = part.getWsdlOperation();
      String zeroarg1 = null;
      String zeroarg2 = null;
      WSDLProperty prop1 = op.getProperty(Constants.WSDL_PROPERTY_ZERO_ARGS);
      if (prop1 != null)
         zeroarg1 = prop1.getValue();
      if (zeroarg1 != null && zeroarg2 != null && zeroarg1.equals(zeroarg2) == false)
         return null;
      if (zeroarg1 != null && "true".equals(zeroarg1))
         return null;

      QName xmlType = part.getXMLType();

      WSDLTypes types = wsdlDefinitions.getWsdlTypes();
      JBossXSModel xsmodel = WSDLUtils.getSchemaModel(types);
      return xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());
   }

   private XSTypeDefinition getXSType(QName xmlType)
   {
      WSDLTypes types = wsdlDefinitions.getWsdlTypes();
      JBossXSModel xsmodel = WSDLUtils.getSchemaModel(types);
      return xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());
   }

   private void addJavaXMLTypeMap(XSTypeDefinition xt, String name, String containingElement, String containingType, JavaWsdlMapping jwm, boolean skipWrapperArray)
   {
      JavaXmlTypeMapping jxtm = null;

      if (xt instanceof XSComplexTypeDefinition)
      {
         XSModelGroup xm = null;
         XSComplexTypeDefinition xc = (XSComplexTypeDefinition)xt;
         XSTypeDefinition baseType = xc.getBaseType();
         short der = xc.getDerivationMethod();
         String typeQName = "";

         if ((baseType != null) && !utils.isBaseTypeIgnorable(baseType, xc))
         {
            addJavaXMLTypeMap(baseType, baseType.getName(), "", "", jwm, skipWrapperArray); //Recurse for base types
         }

         // handleContentTypeElementsWithDerivationNone
         if (xc.getContentType() != XSComplexTypeDefinition.CONTENTTYPE_EMPTY)
         {
            XSParticle xp = xc.getParticle();
            if (xp != null)
            {
               XSTerm xterm = xp.getTerm();
               if (xterm instanceof XSModelGroup)
                  xm = (XSModelGroup)xterm;
            }
         }

         if ((skipWrapperArray && SchemaUtils.isWrapperArrayType(xt)) == false)
         {
            jxtm = new JavaXmlTypeMapping(jwm);
            String javaType;
            String localName = xt.getName();

            // Anonymous
            if (localName == null)
            {
               String tempName = containingElement + ToolsUtils.firstLetterUpperCase(name);
               javaType = getJavaTypeAsString(null, new QName(tempName), xt.getNamespace(), false, true);
               StringBuilder temp = new StringBuilder();
               if (containingType != null && containingType.length() > 0)
                  temp.append(containingType);
               temp.append(">").append(name);
               localName = temp.toString();
               jxtm.setAnonymousTypeQName(new QName(xt.getNamespace(), localName, "typeNS"));
               typeQName = localName;
            }
            else
            {
               javaType = getJavaTypeAsString(null, new QName(localName), xt.getNamespace(), false, true);
               jxtm.setRootTypeQName(new QName(xt.getNamespace(), xt.getName(), "typeNS"));
               typeQName = xc.getName();
            }

            if (typeNamespace == null)
            {
               typeNamespace = xt.getNamespace();
            }

            if (registeredTypes.contains(javaType))
               return;

            jxtm.setJavaType(javaType);
            jxtm.setQNameScope("complexType");

            registeredTypes.add(javaType);
            jwm.addJavaXmlTypeMappings(jxtm);
            // addJavaXMLTypeMapping(jwm, jxtm

            if (xm != null)
            {
               addVariableMappingMap(xm, jxtm, javaType);
            }

            // Add simple content if it exists
            XSSimpleTypeDefinition simple = xc.getSimpleType();
            if (simple != null)
            {
               addJavaXMLTypeMap(simple, xc.getName(), "", "", jwm, skipWrapperArray);
            }

            // Add attributes
            XSObjectList attributeUses = ((XSComplexTypeDefinition)xc).getAttributeUses();
            if (attributeUses != null)
               addAttributeMappings(attributeUses, jxtm);
         }

         if (xm != null)
         {
            String container = containingElement;
            if (container == null || container.length() == 0)
            {
               container = name;
            }

            addGroup(xm, container, typeQName, jwm);
         }
      }

      // Add enum simpleType support
   }

   private void addVariableMappingMap(XSModelGroup xm, JavaXmlTypeMapping jxtm, String javaType)
   {
      XSObjectList xo = xm.getParticles();
      int len = xo != null ? xo.getLength() : 0;
      for (int i = 0; i < len; i++)
      {
         XSTerm xsterm = ((XSParticle)xo.item(i)).getTerm();
         if (xsterm instanceof XSModelGroup)
         {
            addVariableMappingMap((XSModelGroup)xsterm, jxtm, javaType);
         }
         else if (xsterm instanceof XSElementDeclaration)
         {
            XSElementDeclaration xe = (XSElementDeclaration)xsterm;
            VariableMapping vm = new VariableMapping(jxtm);
            String name = xe.getName();

            // JBWS-1170 Convert characters which are illegal in Java identifiers
            vm.setJavaVariableName(ToolsUtils.convertInvalidCharacters(Introspector.decapitalize(name)));
            vm.setXmlElementName(name);
            jxtm.addVariableMapping(vm);
         }
      }
   }

   private void addAttributeMappings(XSObjectList attributes, JavaXmlTypeMapping jxtm)
   {
      for (int i = 0; i < attributes.getLength(); i++)
      {
         XSAttributeUse obj = (XSAttributeUse)attributes.item(i);
         XSAttributeDeclaration att = obj.getAttrDeclaration();
         XSSimpleTypeDefinition simple = att.getTypeDefinition();
         addJavaXMLTypeMap(simple, "none", "", "", jxtm.getJavaWsdlMapping(), true);
         VariableMapping vm = new VariableMapping(jxtm);
         String name = att.getName();
         vm.setXmlAttributeName(name);
         // JBWS-1170 Convert characters which are illegal in Java identifiers
         vm.setJavaVariableName(ToolsUtils.convertInvalidCharacters(Introspector.decapitalize(name)));
         jxtm.addVariableMapping(vm);
      }
   }

   private void addGroup(XSModelGroup xm, String containingElement, String containingType, JavaWsdlMapping jwm)
   {
      XSObjectList xo = xm.getParticles();
      int len = xo != null ? xo.getLength() : 0;
      for (int i = 0; i < len; i++)
      {
         XSTerm xsterm = ((XSParticle)xo.item(i)).getTerm();
         if (xsterm instanceof XSModelGroup)
         {
            addGroup((XSModelGroup)xsterm, containingElement, containingType, jwm);
         }
         else if (xsterm instanceof XSElementDeclaration)
         {
            XSElementDeclaration xe = (XSElementDeclaration)xsterm;
            XSTypeDefinition typeDefinition = xe.getTypeDefinition();
            String tempContainingElement = "";
            String tempContainingType = "";
            if (xe.getScope() != XSConstants.SCOPE_GLOBAL)
            {
               tempContainingElement = containingElement;
               tempContainingType = containingType;
            }

            addJavaXMLTypeMap(typeDefinition, xe.getName(), tempContainingElement, tempContainingType, jwm, !isWrapped());
         }
      }
   }

   private String getJavaTypeAsString(QName xmlName, QName xmlType, String targetNS, boolean array, boolean primitive)
   {
      String jtype = null;

      String arraySuffix = (array) ? "[]" : "";
      if (!isDocStyle())
      {
         JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdlDefinitions.getWsdlTypes());
         XSTypeDefinition xt = xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());

         XSElementDeclaration unwrapped = SchemaUtils.unwrapArrayType(xt);
         StringBuilder builder = new StringBuilder();

         while (unwrapped != null)
         {
            xt = unwrapped.getTypeDefinition();
            primitive = !unwrapped.getNillable();
            builder.append("[]");
            unwrapped = SchemaUtils.unwrapArrayType(xt);
         }
         if (builder.length() > 0)
         {
            xmlType = new QName(xt.getNamespace(), xt.getName());
            arraySuffix = builder.toString();
         }
      }

      //First try to get it from the typeMapping
      Class javaType = typeMapping.getJavaType(xmlType, primitive);
      /**
       * Special case - when qname=xsd:anyType && javaType == Element
       * then cls has to be javax.xml.soap.SOAPElement
       */
      if (xmlType.getNamespaceURI().equals(Constants.NS_SCHEMA_XSD) && "anyType".equals(xmlType.getLocalPart()) && javaType == Element.class)
         javaType = SOAPElement.class;
      javaType = this.makeCustomDecisions(javaType, xmlType);

      if (javaType == null)
      {
         log.debug("Typemapping lookup failed for " + xmlName);
         log.debug("Falling back to identifier generation");
         String className = xmlType.getLocalPart();
         if (className.charAt(0) == '>')
            className = className.substring(1);
         className = ToolsUtils.convertInvalidCharacters(className);
         jtype = getPackageName(targetNS) + "." + utils.firstLetterUpperCase(className);
      }
      else
      {
         //Handle arrays
         if (javaType.isArray())
         {
            jtype = JavaUtils.getSourceName(javaType);
         }
         else
            jtype = javaType.getName();
      }

      return jtype + arraySuffix;
   }

   private boolean isDocStyle()
   {
      return Constants.DOCUMENT_LITERAL.equals(wsdlStyle);
   }

   /**
    * Any custom decisions that need to be made will be done here
    *
    * @param javaType
    * @param xmlName
    * @param xmlType
    */
   private Class makeCustomDecisions(Class javaType, QName xmlType)
   {
      if (javaType != null && xmlType != null)
      {
         if (Byte[].class == javaType && Constants.NS_SCHEMA_XSD.equals(xmlType.getNamespaceURI()) && "base64Binary".equals(xmlType.getLocalPart()))
            javaType = byte[].class;
      }
      return javaType;
   }

   private boolean isWrapped()
   {
      return "wrapped".equals(parameterStyle) && Constants.DOCUMENT_LITERAL.equals(wsdlStyle);
   }

   private MethodParamPartsMapping getMethodParamPartsMapping(ServiceEndpointMethodMapping semm, QName xmlName, QName xmlType, int paramPosition,
         String wsdlMessageName, String paramMode, String wsdlMessagePartName, boolean array, boolean primitive)
   {
      String targetNS = wsdlDefinitions.getTargetNamespace();
      MethodParamPartsMapping mppm = new MethodParamPartsMapping(semm);
      mppm.setParamPosition(paramPosition);
      String javaType = getJavaTypeAsString(xmlName, xmlType, xmlType.getNamespaceURI(), array, primitive);
      mppm.setParamType(javaType);

      //WSDL Message Mapping
      WsdlMessageMapping wmm = new WsdlMessageMapping(mppm);
      wmm.setParameterMode(paramMode);
      String wsdlNS = WSToolsConstants.WSTOOLS_CONSTANT_MAPPING_WSDL_MESSAGE_NS;
      wmm.setWsdlMessage(new QName(targetNS, wsdlMessageName, wsdlNS));
      wmm.setWsdlMessagePartName(wsdlMessagePartName);
      mppm.setWsdlMessageMapping(wmm);
      return mppm;
   }

   private String getPackageName(String targetNamespace)
   {
      //Get it from global config
      if (namespacePackageMap != null)
      {
         String pkg = namespacePackageMap.get(targetNamespace);
         if (pkg != null)
         {
            return pkg;
         }
      }
      //Default behaviour will always generate all classes in the SEI package only
      return packageName;
   }
}
