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
package org.jboss.ws.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLException;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceFault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutfault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLRPCPart;
import org.jboss.ws.metadata.wsdl.WSDLSOAPHeader;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.ws.tools.helpers.ReturnTypeUnwrapper;
import org.jboss.ws.tools.interfaces.WSDLToJavaIntf;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.common.JavaUtils;
import org.w3c.dom.Element;

/**
 * Class that acts as the front door to all wsdl2java needs<br>
 *
 * <br>Note: (Web Services Layer)<br>
 * Method to generate Java SEI is as follows<br>
 * <br>{@link #generateSEI(URL wsdlFile, File dir, boolean annotate)  generateSEI}
 * <br>
 * <br> Please also have a look at the features that can be passed via {@link #setFeature(String name, boolean value) setFeature}
 * <br>
 * <br>Features are:
 * <br>@see org.jboss.ws.Constants.USE_ANNOTATIONS : Should the generated Java Types use annotations
 * @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 * @since Dec 28, 2004
 */
public class WSDLToJava implements WSDLToJavaIntf
{
   private String newline = "\n";

   protected LiteralTypeMapping typeMapping = null;

   protected WSDLDefinitions wsdl = null;

   /**
    * Singleton class that handle many utility functions
    */
   protected WSDLUtils utils = WSDLUtils.getInstance();

   //Feature Set
   protected boolean annotate = false;

   protected Map<String, String> namespacePackageMap = null;
   protected boolean generateSerializableTypes = false;

   protected HolderWriter holderWriter = new HolderWriter();

   private String seiPkgName = "";

   private String directoryToGenerate = "";

   private String style;
   private String parameterStyle;

   public WSDLToJava()
   {
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.WSDLToJavaIntf#convertWSDL2Java(java.net.URL)
    */
   public WSDLDefinitions convertWSDL2Java(URL wsdlfileurl) throws WSDLException
   {
      checkTypeMapping();
      WSDLDefinitionsFactory wsdlFactory = WSDLDefinitionsFactory.newInstance();
      wsdl = wsdlFactory.parse(wsdlfileurl);

      return wsdl;
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.WSDLToJavaIntf#getFeature(java.lang.String)
    */
   public boolean getFeature(String name)
   {
      if (name == null)
         throw new IllegalArgumentException("Illegal null argument:name");

      if (name.equalsIgnoreCase(WSToolsConstants.WSTOOLS_FEATURE_USE_ANNOTATIONS))
         return annotate;

      throw new WSException("Feature:" + name + " not recognized");
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.WSDLToJavaIntf#setFeature(java.lang.String, boolean)
    */
   public void setFeature(String name, boolean value)
   {
      if (name == null)
         throw new IllegalArgumentException("Illegal null argument:name");

      if (name.equalsIgnoreCase(WSToolsConstants.WSTOOLS_FEATURE_USE_ANNOTATIONS))
         annotate = value;
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.WSDLToJavaIntf#generateSEI(java.net.URL, java.io.File, boolean)
    */
   public void generateSEI(URL wsdlFile, File dir, boolean annotate) throws IOException
   {
      checkTypeMapping();
      WSDLDefinitions wsdl = convertWSDL2Java(wsdlFile);
      this.annotate = annotate;
      this.directoryToGenerate = dir.getAbsolutePath();
      generateSEI(wsdl, dir);
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.WSDLToJavaIntf#generateSEI(org.jboss.ws.metadata.wsdl.WSDLDefinitions, java.io.File)
    */
   public void generateSEI(WSDLDefinitions wsdl, File dir) throws IOException
   {
      checkTypeMapping();
      this.directoryToGenerate = dir.getAbsolutePath();
      this.wsdl = wsdl;
      style = utils.getWSDLStyle(wsdl);

      //TODO: Handle annotations flag, as per JAX-WS 2.0 Spec.
      //Given the WSDL Object Tree, generate the SEI
      //Also take in the location where the SEI should be written
      // String typeNS = wsdl.getNamespaceURI(WSDLConstants.PREFIX_TNS);
      String targetNS = wsdl.getTargetNamespace();
      //Check if there is an user override
      String packageName = namespacePackageMap != null ? namespacePackageMap.get(targetNS) : null;
      if (packageName == null || packageName.length() == 0)
         packageName = NamespacePackageMapping.getJavaPackageName(targetNS);

      this.seiPkgName = packageName;

      File dirloc = utils.createPackage(dir.getAbsolutePath(), packageName);
      createSEI(dirloc, wsdl);
   }

   public Map<String, String> getNamespacePackageMap()
   {
      return namespacePackageMap;
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.WSDLToJavaIntf#setPackageNamespaceMap(java.util.Map)
    */
   public void setNamespacePackageMap(Map<String, String> map)
   {
      //Lets convert the package->namespace map to namespace->package map
      Set<String> keys = map.keySet();
      Iterator<String> iter = keys.iterator();
      while (iter != null && iter.hasNext())
      {
         if (namespacePackageMap == null)
            namespacePackageMap = new HashMap<String, String>();
         String pkg = iter.next();
         namespacePackageMap.put(pkg, map.get(pkg));
      }
   }
   
   public boolean isGenerateSerializableTypes()
   {
      return generateSerializableTypes;
   }
   
   public void setGenerateSerializableTypes(boolean generateSerializableTypes)
   {
      this.generateSerializableTypes = generateSerializableTypes;
   }

   public void setTypeMapping(LiteralTypeMapping tm)
   {
      this.typeMapping = tm;
   }

   private class WrappedArray
   {
      public QName xmlType;
      public XSTypeDefinition xt;
      public String suffix;
      public boolean nillable;

      public WrappedArray(XSTypeDefinition xt)
      {
         this.xt = xt;
      }

      public boolean unwrap()
      {
         if (!Constants.DOCUMENT_LITERAL.equals(style))
         {
            XSElementDeclaration unwrapped = SchemaUtils.unwrapArrayType(xt);
            StringBuilder builder = new StringBuilder();
            while (unwrapped != null)
            {
               xt = unwrapped.getTypeDefinition();
               nillable = unwrapped.getNillable();
               builder.append("[]");
               unwrapped = SchemaUtils.unwrapArrayType(xt);
            }
            if (builder.length() > 0)
            {
               xmlType = new QName(xt.getNamespace(), xt.getName());
               suffix = builder.toString();
               return true;
            }
         }

         return false;
      }
   }

   //***************************************************************************
   //                             PRIVATE METHODS
   //***************************************************************************

   private boolean isDocument()
   {
      return Constants.DOCUMENT_LITERAL.equals(style);
   }

   private boolean isWrapped()
   {
      return "wrapped".equals(parameterStyle) && Constants.DOCUMENT_LITERAL.equals(style);
   }

   private void appendMethods(WSDLInterface intf, StringBuilder buf) throws IOException
   {
      buf.append(newline);
      String itfname = intf.getName().getLocalPart();
      WSDLInterfaceOperation[] ops = intf.getOperations();
      if (ops == null || ops.length == 0)
         throw new IllegalArgumentException("Interface " + itfname + " doesn't have operations");
      int len = ops != null ? ops.length : 0;

      for (int i = 0; i < len; i++)
      {
         WSDLInterfaceOperation op = ops[i];

         WSDLBindingOperation bindingOperation = HeaderUtil.getWSDLBindingOperation(wsdl, op);

         //TODO: Take care of multiple outputs
         String returnType = null;

         StringBuilder paramBuffer = new StringBuilder();

         WSDLInterfaceOperationInput input = WSDLUtils.getWsdl11Input(op);
         WSDLInterfaceOperationOutput output = WSDLUtils.getWsdl11Output(op);
         if (isDocument())
         {
            returnType = appendDocParameters(paramBuffer, input, output, bindingOperation);
         }
         else
         {
            returnType = appendRpcParameters(paramBuffer, op, output, bindingOperation);
         }

         if (returnType == null)
            returnType = "void";

         buf.append("  public " + returnType + "  ");
         buf.append(ToolsUtils.firstLetterLowerCase(op.getName().getLocalPart()));
         buf.append("(").append(paramBuffer);

         buf.append(") throws ");
         //Generate the Exception Types
         WSDLInterfaceOperationOutfault[] outfaults = op.getOutfaults();
         for (int k = 0; k < outfaults.length; k++)
         {
            WSDLInterfaceOperationOutfault fault = outfaults[k];
            QName faultName = fault.getRef();

            //Get the main fault from the wsdlInterface
            WSDLInterfaceFault intfFault = fault.getWsdlInterfaceOperation().getWsdlInterface().getFault(faultName);
            JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdl.getWsdlTypes());
            QName faultXMLName = intfFault.getElement();
            QName faultXMLType = intfFault.getXmlType();
            XSElementDeclaration xe = xsmodel.getElementDeclaration(faultXMLName.getLocalPart(), faultXMLName.getNamespaceURI());
            XSTypeDefinition xt = xe.getTypeDefinition();
            if (!xt.getAnonymous())
               xt = xsmodel.getTypeDefinition(xt.getName(), xt.getNamespace());
            if (xt instanceof XSComplexTypeDefinition)
               generateJavaSource((XSComplexTypeDefinition)xt, xsmodel, faultXMLName.getLocalPart(), true);

            Class cl = getJavaType(faultXMLType, false);
            if (cl == null)
            {
               String faultTypeName = (!xt.getAnonymous()) ? faultXMLType.getLocalPart() : faultXMLName.getLocalPart();
               String packageName = getPackageName(xt.getNamespace());
               buf.append(packageName + "." + JavaUtils.capitalize(faultTypeName));
            }
            else buf.append(cl.getName());
            buf.append(",");
         }
         buf.append(" java.rmi.RemoteException");
         buf.append(";");
         buf.append(newline);
      }
   }

   private String appendRpcParameters(StringBuilder paramBuffer, WSDLInterfaceOperation op, WSDLInterfaceOperationOutput output, WSDLBindingOperation bindingOperation)
         throws IOException
   {
      String returnType = null;
      boolean first = true;

      RPCSignature signature = new RPCSignature(op);
      for (WSDLRPCPart part : signature.parameters())
      {

         if (first)
            first = false;
         else paramBuffer.append(", ");

         QName xmlName = new QName(part.getName());
         QName xmlType = part.getType();
         JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdl.getWsdlTypes());
         XSTypeDefinition xt = xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());

         boolean holder = output != null && output.getChildPart(part.getName()) != null;
         generateParameter(paramBuffer, xmlName.getLocalPart(), xmlType, xsmodel, xt, false, true, holder);
         paramBuffer.append(" ").append(getMethodParam(xmlName.getLocalPart()));
      }

      if (signature.returnParameter() != null)
      {
         QName xmlName = new QName(signature.returnParameter().getName());
         QName xmlType = signature.returnParameter().getType();
         JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdl.getWsdlTypes());
         XSTypeDefinition xt = xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());
         returnType = getReturnType(xmlName, xmlType, xt);
      }

      if (bindingOperation != null)
      {
         appendHeaderParameters(paramBuffer, bindingOperation);
      }

      return returnType;
   }

   private String appendDocParameters(StringBuilder paramBuffer, WSDLInterfaceOperationInput input, WSDLInterfaceOperationOutput output,
         WSDLBindingOperation bindingOperation) throws IOException
   {
      String returnType = null;
      boolean holder = false;
      if (input != null && input.getElement() != null)
      {
         QName xmlName = input.getElement();
         holder = output != null && xmlName.equals(output.getElement());

         appendParameters(paramBuffer, input, output, xmlName.getLocalPart());
      }

      if (!holder && output != null && output.getElement() != null)
      {
         QName xmlName = output.getElement();
         QName xmlType = output.getXMLType();
         JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdl.getWsdlTypes());
         XSTypeDefinition xt = xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());

         returnType = getReturnType(xmlName, xmlType, xt);
      }

      if (bindingOperation != null)
      {
         appendHeaderParameters(paramBuffer, bindingOperation);
      }

      return returnType;
   }

   private void appendHeaderParameters(StringBuilder buf, WSDLBindingOperation bindingOperation) throws IOException
   {
      WSDLSOAPHeader[] inputHeaders = HeaderUtil.getSignatureHeaders(bindingOperation.getInputs());
      WSDLSOAPHeader[] outputHeaders = HeaderUtil.getSignatureHeaders(bindingOperation.getOutputs());

      // Process Inputs First
      for (WSDLSOAPHeader currentInput : inputHeaders)
      {
         boolean holder = HeaderUtil.containsMatchingPart(outputHeaders, currentInput);
         appendHeaderParameter(buf, currentInput, holder);
      }

      for (WSDLSOAPHeader currentOutput : outputHeaders)
      {
         boolean input = HeaderUtil.containsMatchingPart(inputHeaders, currentOutput);

         if (input == true)
            continue;

         appendHeaderParameter(buf, currentOutput, true);
      }
   }

   private void appendHeaderParameter(StringBuilder buf, WSDLSOAPHeader header, boolean holder) throws IOException
   {
      QName elementName = header.getElement();

      JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdl.getWsdlTypes());
      XSElementDeclaration xe = xsmodel.getElementDeclaration(elementName.getLocalPart(), elementName.getNamespaceURI());
      XSTypeDefinition xt = xe.getTypeDefinition();
      WSDLTypes wsdlTypes = wsdl.getWsdlTypes();
      QName xmlType = wsdlTypes.getXMLType(header.getElement());

      // Replace the xt with the real type from the schema.
      xt = xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());

      if (buf.length() > 0)
      {
         buf.append(", ");
      }

      generateParameter(buf, xe.getName(), xmlType, xsmodel, xt, false, true, holder);
      buf.append(" ").append(header.getPartName());
   }

   private void appendParameters(StringBuilder buf, WSDLInterfaceOperationInput in, WSDLInterfaceOperationOutput output, String containingElement) throws IOException
   {

      QName xmlType = in.getXMLType();
      JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdl.getWsdlTypes());
      XSTypeDefinition xt = xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());

      boolean wrapped = isWrapped();

      if (wrapped)
      {
         int inputs = in.getWsdlOperation().getInputs().length;
         if (inputs > 1)
            throw new WSException("[JAX-RPC - 2.3.1.2] Can not unwrap parameters for operation with mutliple inputs. inputs=" + inputs);

         String operationName = in.getWsdlOperation().getName().getLocalPart();
         String elementName = in.getElement().getLocalPart();

         if (elementName.equals(operationName) == false)
            throw new WSException("[JAX-RPC - 2.3.1.2] Unable to unwrap parameters, wrapper element name must match operation name. operationName=" + operationName
                  + " elementName=" + elementName);

         wrapped = unwrapElementParameters(buf, containingElement, xt);
      }

      if (wrapped == false)
      {
         QName xmlName = in.getElement();
         boolean holder = output != null && xmlName.equals(output.getElement());
         generateParameter(buf, containingElement, xmlType, xsmodel, xt, false, true, holder);
         buf.append(" ").append(getMethodParam(containingElement));
      }

   }

   private boolean unwrapElementParameters(StringBuilder buf, String containingElement, XSTypeDefinition xt) throws IOException
   {
      // If at any point parameter unwrapping can not happen return false so we drop back to not unwrapping.

      if (xt instanceof XSComplexTypeDefinition == false)
         return false;

      StringBuilder tempBuf = new StringBuilder();
      XSComplexTypeDefinition wrapper = (XSComplexTypeDefinition)xt;

      boolean hasAttributes = wrapper.getAttributeUses().getLength() > 0;
      if (hasAttributes)
         throw new WSException("[JAX-RPC 2.3.1.2] Can not unwrap, complex type contains attributes.");

      boolean unwrappedElement = false;

      XSParticle particle = wrapper.getParticle();
      if (particle == null)
      {
         unwrappedElement = true;
      }
      else
      {
         XSTerm term = particle.getTerm();
         if (term instanceof XSModelGroup)
         {
            unwrappedElement = unwrapGroup(tempBuf, containingElement, (XSModelGroup)term);
         }
      }

      if (unwrappedElement)
      {
         buf.append(tempBuf);
         // We need a wrapper class generated
         generateJavaSource(wrapper, WSDLUtils.getSchemaModel(wsdl.getWsdlTypes()), containingElement);

         return true;
      }

      return false;
   }

   public boolean unwrapGroup(StringBuilder buf, String containingElement, XSModelGroup group) throws IOException
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
            if (unwrapGroup(buf, containingElement, (XSModelGroup)term) == false)
               return false;
         }
         else if (term instanceof XSElementDeclaration)
         {
            if (buf.length() > 0)
               buf.append(", ");

            XSElementDeclaration element = (XSElementDeclaration)term;
            XSTypeDefinition type = element.getTypeDefinition();
            String tempContainingElement = containingElement + ToolsUtils.firstLetterUpperCase(element.getName());

            QName xmlType = null;
            if (type.getAnonymous() == false)
               xmlType = new QName(type.getNamespace(), type.getName());

            JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdl.getWsdlTypes());
            boolean array = particle.getMaxOccursUnbounded() || particle.getMaxOccurs() > 1;
            boolean primitive = !(element.getNillable() || (particle.getMinOccurs() == 0 && particle.getMaxOccurs() == 1));
            generateParameter(buf, tempContainingElement, xmlType, xsmodel, type, array, primitive, false);

            String paramName;
            if (type.getAnonymous())
            {
               paramName = containingElement;
            }
            else
            {
               paramName = element.getName();
            }

            buf.append(" ").append(getMethodParam(paramName));
         }
      }

      // If we reach here we must have successfully unwrapped the parameters.
      return true;
   }

   private void generateParameter(StringBuilder buf, String containingElement, QName xmlType, JBossXSModel xsmodel, XSTypeDefinition xt, boolean array,
         boolean primitive, boolean holder) throws IOException
   {
      WrappedArray wrappedArray = new WrappedArray(xt);
      String arraySuffix = (array) ? "[]" : "";
      if (wrappedArray.unwrap())
      {
         xt = wrappedArray.xt;
         xmlType = wrappedArray.xmlType;
         primitive = !wrappedArray.nillable;
         arraySuffix = wrappedArray.suffix;
      }

      if (xt instanceof XSSimpleTypeDefinition)
         xmlType = SchemaUtils.handleSimpleType((XSSimpleTypeDefinition)xt);

      Class cl = null;

      if (xmlType != null)
         cl = getJavaType(xmlType, primitive);

      if (cl != null)
      {
         if (holder)
            cl = utils.getHolder(cl);

         buf.append(JavaUtils.getSourceName(cl) + arraySuffix);
      }
      else
      {
         String className;
         if (xt == null || xt.getAnonymous())
         {
            className = containingElement;
         }
         else
         {
            className = xmlType.getLocalPart();
         }

         if (className.charAt(0) == '>')
            className = className.substring(1);
         className = ToolsUtils.convertInvalidCharacters(className);
         className = utils.firstLetterUpperCase(className);

         String packageName = getPackageName(xt.getNamespace());
         className = packageName + "." + className + arraySuffix;
         if (holder)
         {
            className = holderWriter.getOrCreateHolder(className, getLocationForJavaGeneration(packageName));
         }

         buf.append(className);

         if (xt instanceof XSComplexTypeDefinition)
            generateJavaSource((XSComplexTypeDefinition)xt, xsmodel, containingElement);
      }
   }

   private void createSEIFile(WSDLInterface intf, File loc) throws IOException
   {
      String seiName = getServiceEndpointInterfaceName(intf);

      StringBuilder buf = new StringBuilder();
      utils.writeJbossHeader(buf);
      buf.append("package " + seiPkgName + ";" + newline);
      buf.append("public interface  " + seiName + " extends java.rmi.Remote" + newline + "{" + newline);
      appendMethods(intf, buf);
      buf.append("}" + newline);

      File sei = utils.createPhysicalFile(loc, seiName);
      FileWriter writer = new FileWriter(sei);
      writer.write(buf.toString());
      writer.flush();
      writer.close();
   }

   public String getServiceEndpointInterfaceName(WSDLInterface wsdlInterface)
   {
      String seiName = utils.chopPortType(wsdlInterface.getName().getLocalPart());

      //Check if the portType name conflicts with a service name
      if (wsdl.getService(seiName) != null)
         seiName += "_PortType";

      seiName = JavaUtils.capitalize(seiName);
      seiName = ToolsUtils.convertInvalidCharacters(seiName);

      return seiName;
   }

   private void createSEI(File loc, WSDLDefinitions wsdl) throws IOException
   {
      WSDLInterface[] intarr = wsdl.getInterfaces();
      if (intarr == null || intarr.length == 0)
         throw new IllegalArgumentException("Interfaces cannot be zero");
      int len = intarr.length;
      for (int i = 0; i < len; i++)
      {
         WSDLInterface intf = intarr[i];
         createSEIFile(intf, loc);
      }
   }

   private String getReturnType(QName xmlName, QName xmlType, XSTypeDefinition xt) throws IOException
   {
      String containingElement = xmlName.getLocalPart();
      String arraySuffix = "";
      boolean primitive = true;

      JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdl.getWsdlTypes());
      xt = xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());

      ReturnTypeUnwrapper unwrapper = new ReturnTypeUnwrapper(xmlType, xsmodel, isWrapped());
      if (unwrapper.unwrap())
      {
         // Need to generate wrapper class as well.
         if (xt instanceof XSComplexTypeDefinition)
            generateJavaSource((XSComplexTypeDefinition)xt, xsmodel, containingElement);

         if (unwrapper.unwrappedElement != null)
         {
            XSElementDeclaration element = unwrapper.unwrappedElement;
            xt = element.getTypeDefinition();
            primitive = unwrapper.primitive;

            if (unwrapper.xmlType != null)
               xmlType = unwrapper.xmlType;

            containingElement = containingElement + ToolsUtils.firstLetterUpperCase(unwrapper.unwrappedElement.getName());

            if (unwrapper.array)
               arraySuffix = "[]";
         }
      }

      WrappedArray wrappedArray = new WrappedArray(xt);
      if (wrappedArray.unwrap())
      {
         xt = wrappedArray.xt;
         xmlType = wrappedArray.xmlType;
         primitive = !wrappedArray.nillable;
         arraySuffix = wrappedArray.suffix;
      }

      if (xt instanceof XSSimpleTypeDefinition)
         xmlType = SchemaUtils.handleSimpleType((XSSimpleTypeDefinition)xt);

      Class cls = getJavaType(xmlType, primitive);

      if (xt instanceof XSComplexTypeDefinition)
         generateJavaSource((XSComplexTypeDefinition)xt, xsmodel, containingElement);

      if (cls == null)
      {
         String className;
         if (xt.getAnonymous() == true)
         {
            className = containingElement;
         }
         else
         {
            className = xmlType.getLocalPart();
         }

         if (className.charAt(0) == '>')
            className = className.substring(1);
         className = ToolsUtils.convertInvalidCharacters(className);
         className = utils.firstLetterUpperCase(className);
         String packageName = getPackageName(xt.getNamespace());
         return packageName + "." + className + arraySuffix;
      }

      if (cls.isArray())
         return JavaUtils.getSourceName(cls);
      return cls.getName() + arraySuffix;
   }

   private void checkTypeMapping()
   {
      if (typeMapping == null)
         throw new WSException("TypeMapping has not been set.");
   }

   private Class getJavaType(QName qname, boolean primitive)
   {
      Class cls = typeMapping.getJavaType(qname, primitive);
      /**
       * Special case - when qname=xsd:anyType && cls == Element
       * then cls has to be javax.xml.soap.SOAPElement
       */
      if (qname.getNamespaceURI().equals(Constants.NS_SCHEMA_XSD) && "anyType".equals(qname.getLocalPart()) && cls == Element.class)
         cls = SOAPElement.class;
      return cls;
   }

   /**
    * Make sure the first character is lower case and if the
    * parameter is a reserved word prefix it with '_'.
    * 
    * @param containingElement
    * @return
    */
   private String getMethodParam(String name)
   {
      String paramName = ToolsUtils.firstLetterLowerCase(name);
      if (JavaKeywords.isJavaKeyword(paramName))
      {
         paramName = "_" + paramName;
      }

      return paramName;
   }

   private File getLocationForJavaGeneration(String packageName)
   {
      return new File(this.directoryToGenerate + "/" + packageName.replace('.', '/'));
   }

   private void generateJavaSource(XSComplexTypeDefinition xt, JBossXSModel xsmodel, String containingElement) throws IOException
   {
      generateJavaSource(xt, xsmodel, containingElement, false);
   }

   private void generateJavaSource(XSComplexTypeDefinition xt, JBossXSModel xsmodel, String containingElement, boolean exception) throws IOException
   {
      XSDTypeToJava xtj = new XSDTypeToJava(namespacePackageMap, generateSerializableTypes);
      xtj.setTypeMapping(this.typeMapping);
      String targetNS = wsdl.getTargetNamespace();
      String tgtNS = xt.getNamespace();
      String packName = getPackageName(tgtNS);
      if(!tgtNS.equals(targetNS))
      {
          File dir = utils.createPackage(this.directoryToGenerate, packName);
      }
      xtj.createJavaFile((XSComplexTypeDefinition)xt, containingElement, this.directoryToGenerate, packName, xsmodel, exception);
   }

   public void setParameterStyle(String paramStyle)
   {
      this.parameterStyle = paramStyle;
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
     //return NamespacePackageMapping.getJavaPackageName(targetNamespace);
     //Default behaviour will always generate all classes in the SEI package only
     return seiPkgName;
   }
}

