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

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;

/**
 * A Binding component describes a concrete message format and transmission protocol which may be used
 * to define an endpoint (see 2.14 Endpoint [p.62] ). That is, a Binding component defines the
 * implementation details necessary to accessing the service.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="jason.greene@jboss.com">Jason T. Greene</a>
 * @since 10-Oct-2004
 */
public class WSDLBinding extends Extendable implements Serializable
{
   private static final long serialVersionUID = -7699953670233209811L;

   // provide logging
   private static final Logger log = Logger.getLogger(WSDLBinding.class);
   
   // The parent WSDL definitions element.
   private final WSDLDefinitions wsdlDefinitions;

   private final QName name;
   
   /** The OPTIONAL interface attribute information item refers, by QName, to an Interface component. */
   private QName interfaceName;

   /** The REQUIRED type attribute information item identifies the kind of binding details contained in the Binding
    * component. See wsdl20-bindings for valid values. */
   private String type;

   /** The set of Binding Fault components corresponding to the fault element
    * information items in [children], if any.*/
   private ArrayList<WSDLBindingFault> faults = new ArrayList<WSDLBindingFault>();

   /** The set of Binding Operation components corresponding to the operation element
    * information items in [children], if any.*/
   private ArrayList<WSDLBindingOperation> operations = new ArrayList<WSDLBindingOperation>();

   public WSDLBinding(WSDLDefinitions wsdlDefinitions, QName name)
   {
      this.wsdlDefinitions = wsdlDefinitions;
      this.name = name;
   }

   public WSDLDefinitions getWsdlDefinitions()
   {
      return wsdlDefinitions;
   }

   public QName getName()
   {
      return name;
   }

   public QName getInterfaceName()
   {
      return interfaceName;
   }

   public void setInterfaceName(QName interfaceName)
   {
      log.trace("setInterfaceName: " + name);
      this.interfaceName = interfaceName;
   }

   public WSDLInterface getInterface()
   {
      WSDLInterface wsdlInterface = wsdlDefinitions.getInterface(interfaceName);
      if (wsdlInterface == null)
         throw new WSException("Cannot get interface for name: " + interfaceName);
      return wsdlInterface;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public WSDLBindingFault[] getFaults()
   {
      WSDLBindingFault[] arr = new WSDLBindingFault[faults.size()];
      faults.toArray(arr);
      return arr;
   }

   public void addFault(WSDLBindingFault fault)
   {
      faults.add(fault);
   }

   public WSDLBindingOperation[] getOperations()
   {
      WSDLBindingOperation[] arr = new WSDLBindingOperation[operations.size()];
      operations.toArray(arr);
      return arr;
   }

   public WSDLBindingOperation getOperationByRef(QName qname)
   {
      WSDLBindingOperation wsdlBindingOperation =  null;
      for (WSDLBindingOperation aux : operations) 
      {
         if (aux.getRef().equals(qname))
         {
            if (wsdlBindingOperation != null)
               log.warn("Multiple binding operations reference: " + qname);
            wsdlBindingOperation = aux;
         }
      }
      
      if (wsdlBindingOperation == null)
         log.warn("Cannot obtain binding operation for ref: " + qname);
         
      return wsdlBindingOperation;
   }

   public void addOperation(WSDLBindingOperation operation)
   {
      operations.add(operation);
   }
}
