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
package org.jboss.ws.tools.wsdl;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.Extendable;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLBindingMessageReference;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLDocumentation;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLExtensibilityElement;
import org.jboss.ws.metadata.wsdl.WSDLImport;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceFault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceMessageReference;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutfault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLProperty;
import org.jboss.ws.metadata.wsdl.WSDLRPCPart;
import org.jboss.ws.metadata.wsdl.WSDLRPCSignatureItem;
import org.jboss.ws.metadata.wsdl.WSDLSOAPHeader;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLRPCSignatureItem.Direction;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A WSDL Writer that writes a WSDL 1.1 file. It works off
 * of the WSDL20 Object Graph.
 * @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 */
public class WSDL11Writer extends WSDLWriter
{
   //Used Internally
   private String wsdlStyle = Constants.RPC_LITERAL;

   // Used to prevent duplicates
   private HashSet<String> writtenFaultMessages = new HashSet<String>();

   /** Use WSDLDefinitions.writeWSDL instead. */
   public WSDL11Writer(WSDLDefinitions wsdl)
   {
      super(wsdl);
   }

   public void write(Writer writer) throws IOException
   {
      write(writer, null);
   }


   public void write(Writer writer, String charset) throws IOException
   {
      write(writer, charset, null);
   }

   public void write(Writer writer, String charset, WSDLWriterResolver resolver) throws IOException
   {
      // Write out the wsdl-1.1 represention (only path to obtain is from WSDL11Reader)
      if (wsdl.getWsdlOneOneDefinition() != null)
      {
        Definition wsdlDefinition = wsdl.getWsdlOneOneDefinition();
        try
        {
           WSDLFactory wsdlFactory = WSDLFactory.newInstance();
           javax.wsdl.xml.WSDLWriter wsdlWriter = wsdlFactory.newWSDLWriter();
           wsdlWriter.writeWSDL(wsdlDefinition, writer);
        }
        catch (WSDLException e)
        {
           this.logException(e);
           throw new IOException(e.toString());
        }
      }
      else
      {
         StringBuilder buffer = new StringBuilder();

         //Detect the WSDL Style early
         wsdlStyle = utils.getWSDLStyle(wsdl);

         StringBuilder importBuffer = new StringBuilder();
         for (WSDLImport wsdlImport : wsdl.getImports())
         {
            if (resolver == null)
               continue;

            WSDLWriterResolver resolved = resolver.resolve(wsdlImport.getLocation());
            if (resolved == null)
               continue;

            String namespace = wsdlImport.getNamespace();
            importBuffer.append("<import namespace='" + namespace + "' location='" + resolved.actualFile + "'/>");
            if (resolved != null)
            {
               StringBuilder builder = new StringBuilder();

               appendDefinitions(builder, namespace);
               appendBody(builder, namespace);
               try
               {
                  writeBuilder(builder, resolved.writer, resolved.charset);
               }
               finally
               {
                  resolved.writer.close();
               }
            }
         }

         appendDefinitions(buffer, wsdl.getTargetNamespace());
         if (importBuffer.length() > 0)
            buffer.append(importBuffer);

         appendBody(buffer, wsdl.getTargetNamespace());
         writeBuilder(buffer, writer, charset);
      }
   }

   private void writeBuilder(StringBuilder builder, Writer writer, String charset) throws IOException
   {
      Element element = DOMUtils.parse(builder.toString());

      if (charset != null)
         writer.write("<?xml version=\"1.0\" encoding=\"" + charset + "\"?>\n");
      new DOMWriter(writer).setPrettyprint(true).print(element);
   }

   protected void appendBody(StringBuilder builder, String namespace)
   {
      writtenFaultMessages.clear();

      appendTypes(builder, namespace);
      appendUnknownExtensibilityElements(builder, wsdl);
      appendMessages(builder, namespace);
      appendInterfaces(builder, namespace);
      appendBindings(builder, namespace);
      appendServices(builder, namespace);
      builder.append("</definitions>");
   }
   
   protected void appendUnknownExtensibilityElements(StringBuilder builder, Extendable extendable)
   {
      for (WSDLExtensibilityElement ext : extendable.getAllExtensibilityElements())
      {
         appendPolicyElements(builder, ext);
         appendJAXWSCustomizationElements(builder, ext);
         //add processing of further extensibility element types below
      }
   }
   
   private void appendPolicyElements(StringBuilder builder, WSDLExtensibilityElement extElem)
   {
      if (Constants.WSDL_ELEMENT_POLICY.equalsIgnoreCase(extElem.getUri()) ||
            Constants.WSDL_ELEMENT_POLICYREFERENCE.equalsIgnoreCase(extElem.getUri()))
      {
         appendElementSkippingKnownNs(builder, extElem.getElement());
      }
   }
   
   private void appendJAXWSCustomizationElements(StringBuilder builder, WSDLExtensibilityElement extElem)
   {
      if (Constants.URI_JAXWS_WSDL_CUSTOMIZATIONS.equalsIgnoreCase(extElem.getUri()))
      {
         appendElementSkippingKnownNs(builder, extElem.getElement());
      }
   }
   
   private void appendElementSkippingKnownNs(StringBuilder builder, Element el)
   {
      builder.append("<"+el.getNodeName());
      NamedNodeMap attributes = el.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++)
      {
         Attr attr = (Attr)attributes.item(i);
         if (attr.getName().startsWith("xmlns:") && attr.getValue()!=null)
         {
            String prefix = attr.getName().substring(6);
            if (attr.getValue().equalsIgnoreCase(wsdl.getNamespaceURI(prefix)))
               continue;
         }
         builder.append(" "+attr.getName()+"='"+attr.getValue()+"'");
      }
      builder.append(">");
      NodeList childrenList = el.getChildNodes();
      for (int i=0; i<childrenList.getLength(); i++)
      {
         Node node = childrenList.item(i);
         if (node instanceof Element)
         {
            appendElementSkippingKnownNs(builder, (Element)node);
         }
         else
         {
            builder.append(DOMWriter.printNode(node, false));
         }
      }
      builder.append("</"+el.getNodeName()+">");
   }
   
   protected void appendMessages(StringBuilder buffer, String namespace)
   {
      WSDLInterface[] interfaces = wsdl.getInterfaces();
      int len = interfaces != null ? interfaces.length : 0;
      for (int i = 0; i < len; i++)
      {
         WSDLInterface intf = interfaces[i];
         if (! namespace.equals(intf.getName().getNamespaceURI()))
            continue;

         WSDLInterfaceOperation[] operations = intf.getSortedOperations();
         int lenOps = operations.length;
         for (int j = 0; j < lenOps; j++)
         {
            appendMessage(buffer, operations[j]);
            appendMessagesForExceptions(buffer, operations[j]);
         }
      }
   }

   private void appendMessage(StringBuilder buffer, WSDLInterfaceOperation operation)
   {
      String opname = operation.getName().getLocalPart();
      //Determine the style of the wsdl
      if (Constants.URI_STYLE_RPC.equals(operation.getStyle()) == false)
         wsdlStyle = Constants.DOCUMENT_LITERAL; //Not RPC/Literal

      String interfaceName = operation.getWsdlInterface().getName().getLocalPart();
      buffer.append("<message name='" + interfaceName + "_" + opname + "' >");
      for (WSDLInterfaceOperationInput input : operation.getInputs())
      {
         appendUnknownExtensibilityElements(buffer, input); //only one may contain extensibility elements
         appendMessageParts(buffer, input);
      }
      buffer.append("</message>");

      if (! Constants.WSDL20_PATTERN_IN_ONLY.equals(operation.getPattern()))
      {
         buffer.append("<message name='" + interfaceName + "_" + opname + "Response' >");
         WSDLInterfaceOperationOutput[] outputs = operation.getOutputs();
         for (WSDLInterfaceOperationOutput output : outputs)
            appendMessageParts(buffer, output);
         buffer.append("</message>");
      }
   }

   private void appendMessagesForExceptions(StringBuilder buffer, WSDLInterfaceOperation operation)
   {
      //Get the outfaults
      WSDLInterfaceOperationOutfault[] faults = operation.getOutfaults();
      int len = faults != null ? faults.length : 0;

      for (int i = 0; i < len; i++)
      {
         WSDLInterfaceOperationOutfault fault = faults[i];
         String exceptionName = fault.getRef().getLocalPart();
         if (writtenFaultMessages.contains(exceptionName))
            continue;

         QName xmlName = fault.getRef();

         buffer.append("<message name='" + exceptionName + "' >");
         String prefix = wsdl.getPrefix(xmlName.getNamespaceURI());
         String xmlNameStr = prefix + ":" + xmlName.getLocalPart();
         buffer.append("<part name='" + exceptionName + "' element='" + xmlNameStr + "' />");
         buffer.append("</message>");

         writtenFaultMessages.add(exceptionName);
      }
   }

   private String getReferenceString(QName name)
   {
      String namespaceURI = name.getNamespaceURI();
      String prefix = wsdl.getPrefix(namespaceURI);
      if (prefix == null)
         throw new WSException("Prefix not bound for namespace: " + namespaceURI);

      return prefix + ":" + name.getLocalPart();
   }

   private void appendMessageParts(StringBuilder buffer, WSDLInterfaceMessageReference reference)
   {
      if (wsdlStyle.equals(Constants.RPC_LITERAL))
      {
         for (WSDLRPCPart part : reference.getChildParts())
         {
            buffer.append("<part name='" + part.getName()).append('\'');
            buffer.append(" type='" + getReferenceString(part.getType()) + "'/>");
         }
      }
      else
      {
         QName element = reference.getElement();
         // Null represents empty message
         if (element != null)
         {
            buffer.append("<part name='" + reference.getPartName() + '\'');
            buffer.append(" element='" + getReferenceString(element) + "'/>");
         }
      }

      WSDLBindingMessageReference bindingReference = getBindingReference(reference);
      if (bindingReference == null)
         return;

      for (WSDLSOAPHeader header : bindingReference.getSoapHeaders())
      {
         if (header.isIncludeInSignature());
         {
            QName element = header.getElement();
            buffer.append("<part name='" + header.getPartName() + '\'');
            buffer.append(" element='" + getReferenceString(element) + "'/>");
         }
      }
   }

   private WSDLBindingMessageReference getBindingReference(WSDLInterfaceMessageReference reference)
   {
      WSDLInterfaceOperation wsdlOperation = reference.getWsdlOperation();
      WSDLInterface wsdlInterface = wsdlOperation.getWsdlInterface();
      WSDLBinding binding = wsdlInterface.getWsdlDefinitions().getBindingByInterfaceName(wsdlInterface.getName());
      WSDLBindingOperation bindingOperation = binding.getOperationByRef(wsdlOperation.getName());
      WSDLBindingMessageReference[] bindingReferences;

      if (reference instanceof WSDLInterfaceOperationInput)
         bindingReferences = bindingOperation.getInputs();
      else
         bindingReferences = bindingOperation.getOutputs();

      if (bindingReferences.length > 1)
         throw new IllegalArgumentException("WSDl 1.1 only supports In-Only, and In-Out MEPS, more than reference input found");

      if (bindingReferences.length == 1)
         return bindingReferences[0];

      return null;
   }

   protected void appendInterfaces(StringBuilder buffer, String namespace)
   {
      WSDLInterface[] intfs = wsdl.getInterfaces();
      for (int i = 0; i < intfs.length; i++)
      {
         WSDLInterface intf = intfs[i];
         if (!namespace.equals(intf.getName().getNamespaceURI()))
            continue;

         buffer.append("<portType name='" + intf.getName().getLocalPart() + "'");
         WSDLProperty policyProp = intf.getProperty(Constants.WSDL_PROPERTY_POLICYURIS);
         if (policyProp != null)
         {
            String prefix = wsdl.getPrefix(Constants.URI_WS_POLICY);
            buffer.append(" ");
            buffer.append(prefix);
            buffer.append(":");
            buffer.append(Constants.WSDL_ATTRIBUTE_WSP_POLICYURIS.getLocalPart());
            buffer.append("='");
            buffer.append(policyProp.getValue());
            buffer.append("'");
         }
         buffer.append(">");
         appendDocumentation(buffer, intf.getDocumentationElement());
         appendUnknownExtensibilityElements(buffer, intf);
         appendPortOperations(buffer, intf);
         buffer.append("</portType>");
      }
   }

   private String getParameterOrder(WSDLInterfaceOperation operation)
   {
      StringBuilder builder = new StringBuilder();
      for (WSDLRPCSignatureItem item : operation.getRpcSignatureItems())
      {
         if (item.getDirection() != Direction.RETURN)
         {
            if (builder.length() > 0)
               builder.append(' ');
            builder.append(item.getName());
         }
      }

      return builder.toString();
   }

   protected void appendPortOperations(StringBuilder buffer, WSDLInterface intf)
   {
      String prefix = wsdl.getPrefix(intf.getName().getNamespaceURI());
      WSDLInterfaceOperation[] operations = intf.getSortedOperations();
      for (int i = 0; i < operations.length; i++)
      {
         WSDLInterfaceOperation operation = operations[i];
         buffer.append("<operation name='" + operation.getName().getLocalPart() + "'");

         // JBWS-1501 wsimport RI fails when processing parameterOrder on one-way operations
         if (! Constants.WSDL20_PATTERN_IN_ONLY.equals(operation.getPattern()))
         {
            String parameterOrder = getParameterOrder(operation);
            if (parameterOrder.length() > 0)
               buffer.append(" parameterOrder='").append(parameterOrder).append("'");
          
         }
         buffer.append(">");
         appendDocumentation(buffer, operation.getDocumentationElement());
         appendUnknownExtensibilityElements(buffer, operation);

         String opname = operation.getName().getLocalPart();
         String interfaceName = operation.getWsdlInterface().getName().getLocalPart();
         String msgEl = prefix + ":" + interfaceName + "_" + opname;

         buffer.append("<input message='" + msgEl + "'>").append("</input>");

         if (! Constants.WSDL20_PATTERN_IN_ONLY.equals(operation.getPattern()))
         {
            buffer.append("<output message='" + msgEl + "Response'>");
            buffer.append("</output>");
         }

         //Append the Faults
         for (WSDLInterfaceOperationOutfault fault : operation.getOutfaults())
         {
            QName element = fault.getRef();
            buffer.append("<fault  message='" + prefix + ":" + element.getLocalPart());
            buffer.append("' name='" + element.getLocalPart() + "'/>");
         }

         buffer.append("</operation>");
      }
   }
   
   protected void appendDocumentation(StringBuilder buffer, WSDLDocumentation documentation)
   {
      if (documentation != null && documentation.getContent() != null)
      {
         buffer.append("<documentation>");
         buffer.append(documentation.getContent());
         buffer.append("</documentation>");
      }
   }

   protected void appendBindings(StringBuilder buffer, String namespace)
   {
      WSDLBinding[] bindings = wsdl.getBindings();
      for (int i = 0; i < bindings.length; i++)
      {
         WSDLBinding binding = bindings[i];
         if (!namespace.equals(binding.getName().getNamespaceURI()))
            continue;
         buffer.append("<binding name='" + binding.getName().getLocalPart() + "' type='" + getQNameRef(binding.getInterfaceName()) + "'>");
         //TODO:Need to derive the WSDLStyle from the Style attribute of InterfaceOperation
         if (wsdlStyle == null)
            throw new IllegalArgumentException("WSDL Style is null (should be rpc or document");
         String style = "rpc";
         if (wsdlStyle.equals(Constants.DOCUMENT_LITERAL))
            style = "document";
         appendUnknownExtensibilityElements(buffer, binding);
         
         // The value of the REQUIRED transport attribute (of type xs:anyURI) indicates which transport of SOAP this binding corresponds to. 
         // The URI value "http://schemas.xmlsoap.org/soap/http" corresponds to the HTTP binding. 
         // Other URIs may be used here to indicate other transports (such as SMTP, FTP, etc.).
         
         buffer.append("<" + soapPrefix + ":binding transport='" + Constants.URI_SOAP_HTTP + "' style='" + style + "'/>");
         appendBindingOperations(buffer, binding);
         buffer.append("</binding>");
      }
   }

   protected void appendBindingOperations(StringBuilder buffer, WSDLBinding binding)
   {
      WSDLBindingOperation[] operations = binding.getOperations();
      Arrays.sort(operations);

      for (int i = 0; i < operations.length; i++)
      {
         WSDLBindingOperation operation = operations[i];
         QName interfaceName = operation.getWsdlBinding().getInterfaceName();

         WSDLInterface wsdlInterface = wsdl.getInterface(interfaceName);
         if (wsdlInterface == null)
            throw new WSException("WSDL Interface should not be null");
         WSDLInterfaceOperation interfaceOperation = wsdlInterface.getOperation(operation.getRef());

         buffer.append("<operation name='" + interfaceOperation.getName().getLocalPart() + "'>");
         String soapAction = (operation.getSOAPAction() != null ? operation.getSOAPAction() : "");
         appendUnknownExtensibilityElements(buffer, operation);
         buffer.append("<" + soapPrefix + ":operation soapAction=\"" + soapAction + "\"/>");

         WSDLBindingOperationInput[] inputs = operation.getInputs();
         if (inputs.length != 1)
            throw new WSException("WSDl 1.1 only supports In-Only, and In-Out MEPS.");

         buffer.append("<input>");
         appendUnknownExtensibilityElements(buffer, inputs[0]);
         appendSOAPBinding(buffer, wsdlInterface, operation, inputs);
         buffer.append("</input>");

         if (! Constants.WSDL20_PATTERN_IN_ONLY.equals(getBindingOperationPattern(operation)))
         {
            buffer.append("<output>");
            WSDLBindingOperationOutput[] outputs = operation.getOutputs();
            appendSOAPBinding(buffer, wsdlInterface, operation, outputs);
            buffer.append("</output>");
         }

         //Append faults
         WSDLInterfaceOperationOutfault[] faults = interfaceOperation.getOutfaults();
         if (faults != null)
         {
            for (WSDLInterfaceOperationOutfault fault : faults)
            {
               String n = "name='" + fault.getRef().getLocalPart() + "'";
               buffer.append("<fault  " + n + ">");
               buffer.append("<" + soapPrefix + ":fault  " + n + " use='literal' />");
               buffer.append("</fault>");
            }
            buffer.append("</operation>");
         }
      }
   }

   private void appendSOAPBinding(StringBuilder buffer, WSDLInterface wsdlInterface, WSDLBindingOperation operation, WSDLBindingMessageReference[] inputs)
   {
      String tns = wsdl.getTargetNamespace();
      WSDLInterfaceOperation interfaceOperation = wsdlInterface.getOperation(operation.getRef());
      WSDLInterfaceMessageReference reference = (inputs instanceof WSDLBindingOperationInput[]) ? interfaceOperation.getInputs()[0]
            : interfaceOperation.getOutputs()[0];

      StringBuilder bodyParts = new StringBuilder();
      if (Constants.DOCUMENT_LITERAL == wsdlStyle)
      {
         // Empty bare body
         if (reference.getPartName() != null)
            bodyParts.append(reference.getPartName());
      }
      else
      {
         for (WSDLRPCPart part : reference.getChildParts())
         {
            if (bodyParts.length() > 0)
               bodyParts.append(" ");
            bodyParts.append(part.getName());
         }
      }

      StringBuilder soapHeader = new StringBuilder();
      for (WSDLSOAPHeader header : inputs[0].getSoapHeaders())
      {
         if (header.isIncludeInSignature())
         {
            String messageName = wsdlInterface.getName().getLocalPart() + "_" + operation.getRef().getLocalPart();
            if (reference instanceof WSDLInterfaceOperationOutput)
               messageName += "Response";
            soapHeader.append("<").append(soapPrefix).append(":header use='literal' message='tns:").append(messageName);
            soapHeader.append("' part='").append(header.getPartName()).append("'/>");
         }
      }

      buffer.append("<" + soapPrefix + ":body use='literal'");
      if (wsdlStyle != Constants.DOCUMENT_LITERAL)
         buffer.append(" namespace='" + tns + "'");
      if (soapHeader.length() > 0)
      {
         buffer.append(" parts='").append(bodyParts).append("'/>");
         buffer.append(soapHeader);
      }
      else
      {
         buffer.append("/>");
      }
   }

   private String getBindingOperationPattern(WSDLBindingOperation operation)
   {
      WSDLBinding binding = operation.getWsdlBinding();
      String pattern = binding.getInterface().getOperation(operation.getRef()).getPattern();

      return pattern;
   }

   protected void appendServices(StringBuilder buffer, String namespace)
   {
      WSDLService[] services = wsdl.getServices();
      int len = services.length;
      for (int i = 0; i < len; i++)
      {

         WSDLService service = services[i];
         if (!namespace.equals(service.getName().getNamespaceURI()))
            continue;
         buffer.append("<service name='" + service.getName().getLocalPart() + "'>");
         appendUnknownExtensibilityElements(buffer, service);
         WSDLEndpoint[] endpoints = service.getEndpoints();
         int lenend = endpoints.length;
         for (int j = 0; j < lenend; j++)
         {
            WSDLEndpoint endpoint = endpoints[j];
            appendServicePort(buffer, endpoint);
         }

         buffer.append("</service>");
      }
   }

   protected void appendServicePort(StringBuilder buffer, WSDLEndpoint endpoint)
   {
      String name = endpoint.getName().getLocalPart();
      QName endpointBinding = endpoint.getBinding();
      String prefix = endpointBinding.getPrefix();
      prefix = wsdl.getPrefix(endpointBinding.getNamespaceURI());
      String ebname = prefix + ":" + endpointBinding.getLocalPart();
      buffer.append("<port name='" + name + "' binding='" + ebname + "'>");
      buffer.append("<" + soapPrefix + ":address location='" + endpoint.getAddress() + "'/>");
      appendUnknownExtensibilityElements(buffer, endpoint);
      buffer.append("</port>");
   }
}
