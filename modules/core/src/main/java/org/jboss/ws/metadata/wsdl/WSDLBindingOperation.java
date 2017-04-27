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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.ws.Constants;

/**
 * The Binding Operation component describes the concrete message format(s) and protocol interaction(s)
 * associated with a particular interface operation for a given endpoint. A particular operation of an interface
 * is uniquely identified by the target namespace of the interface and the name of the operation within that
 * interface.
 *
 * @author Thomas.Diesler@jboss.org
 * @author Anil.Saldhana@jboss.org
 * @since 10-Oct-2004
 */
public class WSDLBindingOperation extends Extendable implements Comparable, Serializable
{
   private static final long serialVersionUID = -1986624862746844610L;

   // The parent WSDL binding
   private WSDLBinding wsdlBinding;

   /** An Interface Operation component in the {operations} property of the Interface component
    * identified by the {interface} property of the parent Binding component.
    * This is the Interface Operation component for which binding information is being specified.*/
   private QName ref;

   /** WSDL-1.1, style attribute from the soap:binding element */
   private String encodingStyle = Constants.URI_LITERAL_ENC;
   /** WSDL-1.1, soapAction attribute from the soap:operation element */
   private String soapAction;
   /** WSDL-1.1, namespaceURI attribute from the soap:body element */
   private String namespaceURI;
   
   /** A OPTIONAL set of Binding Message Reference components */
   private List<WSDLBindingOperationInput> inputs = new ArrayList<WSDLBindingOperationInput>();
   /** A OPTIONAL set of Binding Message Reference components */
   private List<WSDLBindingOperationOutput> outputs = new ArrayList<WSDLBindingOperationOutput>();

   public WSDLBindingOperation(WSDLBinding wsdlBinding)
   {
      this.wsdlBinding = wsdlBinding;
   }

   public WSDLBinding getWsdlBinding()
   {
      return wsdlBinding;
   }

   public QName getRef()
   {
      return ref;
   }

   public void setRef(QName ref)
   {
      this.ref = ref;
   }

   public String getEncodingStyle()
   {
      return encodingStyle;
   }

   public void setEncodingStyle(String encodingStyle)
   {
      this.encodingStyle = encodingStyle;
   }

   
   public String getSOAPAction()
   {
      return soapAction;
   }

   public void setSOAPAction(String soapAction)
   {
      this.soapAction = soapAction;
   }

   public String getNamespaceURI()
   {
      return namespaceURI;
   }

   public void setNamespaceURI(String namespaceURI)
   {
      this.namespaceURI = namespaceURI;
   }

   public WSDLBindingOperationInput[] getInputs()
   {
      WSDLBindingOperationInput[] arr = new WSDLBindingOperationInput[inputs.size()];
      inputs.toArray(arr);
      return arr;
   }

   public void addInput(WSDLBindingOperationInput input)
   {
      inputs.add(input);
   }

   public WSDLBindingOperationOutput[] getOutputs()
   {
      WSDLBindingOperationOutput[] arr = new WSDLBindingOperationOutput[outputs.size()];
      outputs.toArray(arr);
      return arr;
   }

   public void addOutput(WSDLBindingOperationOutput output)
   {
      outputs.add(output);
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(Object obj)
   {
      int c = -1;
      if (obj instanceof WSDLBindingOperation)
      {
         WSDLBindingOperation w = (WSDLBindingOperation)obj;
         String oname = w.getRef().getLocalPart();
         String myname = ref.getLocalPart();
         c = myname.compareTo(oname);
      }
      return c;
   }
}
