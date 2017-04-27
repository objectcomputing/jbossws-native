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
package org.jboss.ws.metadata.wsdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.WSDLRPCSignatureItem.Direction;

/**
 * An Interface Operation component describes an operation that a given interface supports. An operation is
 * an interaction with the service consisting of a set (ordinary and fault) messages exchanged between the
 * service and the other roles involved in the interaction, in particular the service requester. The sequencing
 * and cardinality of the messages involved in a particular interaction is governed by the message exchange
 * pattern used by the operation (see {message exchange pattern} property).
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com>Jason T. Greene</a>
 * @since 10-Oct-2004
 */
public class WSDLInterfaceOperation extends Extendable implements Comparable
{
   private static final long serialVersionUID = -5014918078197942626L;

   // The parent interface
   private WSDLInterface wsdlInterface;

   private final QName name;

   /** The OPTIONAL pattern attribute information item identifies the message exchange pattern a given operation uses.
    */
   private String pattern;

   /** The OPTIONAL style attribute information item indicates the rules that were used to construct the {element}
    * properties of the Message Reference components which are members of the {message references}
    * property of the [owner] operation.
    */
   private String style = Constants.URI_STYLE_RPC;

   /** The OPTIONAL safe attribute information item indicates whether the operation is safe or not.
    */
   private boolean safe;

   /** Zero or more input element information items */
   private Map<QName, WSDLInterfaceOperationInput> inputs = new LinkedHashMap<QName, WSDLInterfaceOperationInput>();
   /** Zero or more output element information items */
   private Map<QName, WSDLInterfaceOperationOutput> outputs = new LinkedHashMap<QName, WSDLInterfaceOperationOutput>();
   /** Zero or more infault element information items */
   private ArrayList<WSDLInterfaceOperationInfault> infaults = new ArrayList<WSDLInterfaceOperationInfault>();
   /** Zero or more outfault element information items */
   private ArrayList<WSDLInterfaceOperationOutfault> outfaults = new ArrayList<WSDLInterfaceOperationOutfault>();
   /** Zero or more signature items */
   private LinkedHashMap<String, WSDLRPCSignatureItem> rpcSignatureItems = new LinkedHashMap<String, WSDLRPCSignatureItem>();
   
   private WSDLDocumentation documentationElement;

   public WSDLInterfaceOperation(WSDLInterface wsdlInterface, QName name)
   {
      this.name = name;
      this.wsdlInterface = wsdlInterface;
   }
   
   public WSDLInterfaceOperation(WSDLInterface wsdlInterface, String localName)
   {
      this.wsdlInterface = wsdlInterface;
      name = new QName(wsdlInterface.getName().getNamespaceURI(), localName);
   }

   public WSDLInterface getWsdlInterface()
   {
      return wsdlInterface;
   }

   public QName getName()
   {
      return name;
   }

   public String getPattern()
   {
      return pattern;
   }

   public void setPattern(String pattern)
   {
      this.pattern = pattern;
   }

   public String getStyle()
   {
      return style;
   }

   public void setStyle(String style)
   {
      this.style = style;
   }

   public boolean isSafe()
   {
      return safe;
   }

   public void setSafe(boolean safe)
   {
      this.safe = safe;
   }

   public WSDLInterfaceOperationInput[] getInputs()
   {
      WSDLInterfaceOperationInput[] arr = new WSDLInterfaceOperationInput[inputs.size()];
      new ArrayList<WSDLInterfaceOperationInput>(inputs.values()).toArray(arr);
      return arr;
   }

   public void addInput(WSDLInterfaceOperationInput input)
   {
      QName xmlName = input.getElement();
      if (inputs.get(xmlName) != null)
         throw new WSException("Attempt to map multiple operation inputs to: " + xmlName);
      inputs.put(xmlName, input);
   }

   public WSDLInterfaceOperationInput getInput(QName qname)
   {
      WSDLInterfaceOperationInput opInput = (WSDLInterfaceOperationInput)inputs.get(qname);
      return opInput;
   }

   public void removeInput(QName element)
   {
      inputs.remove(element);
   }

   public WSDLInterfaceOperationInput getInputByPartName(String partName)
   {
      WSDLInterfaceOperationInput opInput = null;
      for (WSDLInterfaceOperationInput auxInput : inputs.values())
      {
         WSDLProperty property = auxInput.getProperty(Constants.WSDL_PROPERTY_PART_NAME);
         if (property != null && property.getValue().equals(partName))
            opInput = auxInput;
      }
      return opInput;
   }

   public WSDLInterfaceOperationOutput[] getOutputs()
   {
      WSDLInterfaceOperationOutput[] arr = new WSDLInterfaceOperationOutput[outputs.size()];
      new ArrayList<WSDLInterfaceOperationOutput>(outputs.values()).toArray(arr);
      return arr;
   }

   public void addOutput(WSDLInterfaceOperationOutput output)
   {
      QName xmlName = output.getElement();
      if (outputs.get(xmlName) != null)
         throw new WSException("Attempt to map multiple operation outputs to: " + xmlName);
      outputs.put(xmlName, output);
   }

   public WSDLInterfaceOperationOutput getOutput(QName qname)
   {
      WSDLInterfaceOperationOutput opOutput = (WSDLInterfaceOperationOutput)outputs.get(qname);
      return opOutput;
   }
   
   public void removeOutput(QName element)
   {
      outputs.remove(element);
   }

   public WSDLInterfaceOperationOutput getOutputByPartName(String partName)
   {
      WSDLInterfaceOperationOutput opOutput = null;
      for (WSDLInterfaceOperationOutput auxOutput : outputs.values())
      {
         WSDLProperty property = auxOutput.getProperty(Constants.WSDL_PROPERTY_PART_NAME);
         if (property != null && property.getValue().equals(partName))
            opOutput = auxOutput;
      }
      return opOutput;
   }

   public WSDLInterfaceOperationInfault[] getInfaults()
   {
      WSDLInterfaceOperationInfault[] arr = new WSDLInterfaceOperationInfault[infaults.size()];
      infaults.toArray(arr);
      return arr;
   }

   public void addInfault(WSDLInterfaceOperationInfault infault)
   {
      infaults.add(infault);
   }

   public WSDLInterfaceOperationOutfault[] getOutfaults()
   {
      WSDLInterfaceOperationOutfault[] arr = new WSDLInterfaceOperationOutfault[outfaults.size()];
      outfaults.toArray(arr);
      return arr;
   }

   public void addOutfault(WSDLInterfaceOperationOutfault outfault)
   {
      outfaults.add(outfault);
   }

   public Collection<WSDLRPCSignatureItem> getRpcSignatureItems()
   {
      return rpcSignatureItems.values();
   }


   public void addRpcSignatureItem(WSDLRPCSignatureItem item)
   {
      if (item.getDirection() != Direction.RETURN)
         item.setPosition(rpcSignatureItems.size());
      rpcSignatureItems.put(item.getName(), item);
   }

   public WSDLRPCSignatureItem getRpcSignatureitem(String name)
   {
      return rpcSignatureItems.get(name);
   }

   /**
    * Attempts to locate a binding operation for this interface operation.
    *
    * @return the binding operation, or null if not found;
    */
   public WSDLBindingOperation getBindingOperation()
   {
      WSDLInterface wsdlInterface = getWsdlInterface();
      WSDLBinding binding = wsdlInterface.getWsdlDefinitions().getBindingByInterfaceName(wsdlInterface.getName());
      if (binding == null)
         return null;

      WSDLBindingOperation bindingOperation = binding.getOperationByRef(getName());
      return bindingOperation;
   }

   public int compareTo(Object o)
   {
      int c = -1;
      if (o instanceof WSDLInterfaceOperation)
      {
         WSDLInterfaceOperation w = (WSDLInterfaceOperation)o;
         String oname = w.getName().getLocalPart();
         String myname = name.getLocalPart();
         c = myname.compareTo(oname);
      }
      return c;
   }

   public WSDLDocumentation getDocumentationElement()
   {
      return documentationElement;
   }

   public void setDocumentationElement(WSDLDocumentation documentationElement)
   {
      this.documentationElement = documentationElement;
   }
}
