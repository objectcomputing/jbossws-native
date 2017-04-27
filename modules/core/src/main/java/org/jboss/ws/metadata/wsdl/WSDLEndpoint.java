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

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;

/**
 * An Endpoint component defines the particulars of a specific endpoint at which a given service is available.
 * Endpoint components are local to a given Service component; they cannot be referred to by QName.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="jason.greene@jboss.com">Jason T. Greene</a>
 * @since 10-Oct-2004
 */
public class WSDLEndpoint extends Extendable 
{
   private static final long serialVersionUID = 4991302339046047865L;

   // provide logging
   private static final Logger log = Logger.getLogger(WSDLEndpoint.class);
   
   // The parent service
   private final WSDLService wsdlService;

   private final QName name;
   
   /** The REQUIRED binding attribute information item refers, by QName, to a Binding component */
   private QName binding;

   /** The OPTIONAL address attribute information item specifies the address of the endpoint. */
   private String address;

   public WSDLEndpoint(WSDLService wsdlService, QName name)
   {
      this.wsdlService = wsdlService;
      this.name = name;
   }
   
   public WSDLService getWsdlService()
   {
      return wsdlService;
   }

   /** Get the WSDLInteraface associated to this endpoint
    *
    * @return A WSDLInterface or null
    */
   public WSDLInterface getInterface()
   {
      WSDLInterface wsdlInterface = null;

      WSDLDefinitions wsdlDefinitions = wsdlService.getWsdlDefinitions();
      if (wsdlService.getInterfaceName() != null)
      {
         QName qname = wsdlService.getInterfaceName();
         wsdlInterface = wsdlDefinitions.getInterface(qname);
      }
      else
      {
         WSDLBinding wsdlBinding = wsdlDefinitions.getBinding(binding);
         if (wsdlBinding == null)
            throw new WSException("Cannot obtain the binding: " + binding);

         if (wsdlBinding.getInterfaceName() != null)
         {
            QName qname = wsdlBinding.getInterfaceName();
            wsdlInterface = wsdlDefinitions.getInterface(qname);
         }
      }

      if (wsdlInterface == null)
         throw new WSException("Cannot obtain the interface associated with this endpoint: " + name);

      return wsdlInterface;
   }


   public QName getName()
   {
      return name;
   }
   
   public QName getBinding()
   {
      return binding;
   }

   public void setBinding(QName binding)
   {
      log.trace("setBinding: " + binding);
      this.binding = binding;
   }

   public String getAddress()
   {
      return address;
   }

   public void setAddress(String address)
   {
      this.address = address;
   }
}
