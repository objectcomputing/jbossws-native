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

/**
 * A Fault Reference component associates a defined type, specified by an Interface Fault component, to a
 * fault message exchanged in an operation.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public class WSDLInterfaceOperationOutfault extends Extendable
{
   private static final long serialVersionUID = 1365062002410904728L;

   // The parent interface operation
   private WSDLInterfaceOperation wsdlInterfaceOperation;

   /** A REQUIRED reference to an Interface Fault component in the {faults} property of the parent
    * Interface Operation component's parent Interface component. Identifying the Interface Fault
    * component therefore indirectly defines the actual content or payload of the fault message.
    */
   private QName ref;

   /** An OPTIONAL identifier of the message this fault relates to among those defined in the {message exchange
    * pattern} property of the Interface Operation component it is contained within. The value of this
    * property MUST match the name of a placeholder message defined by the message exchange pattern.
    */
   private String messageLabel;

   public WSDLInterfaceOperationOutfault(WSDLInterfaceOperation wsdlInterfaceOperation)
   {
      this.wsdlInterfaceOperation = wsdlInterfaceOperation;
   }

   public WSDLInterfaceOperation getWsdlInterfaceOperation()
   {
      return wsdlInterfaceOperation;
   }

   public QName getRef()
   {
      return ref;
   }

   public void setRef(QName ref)
   {
      this.ref = ref;
   }

   public String getMessageLabel()
   {
      return messageLabel;
   }

   public void setMessageLabel(String messageLabel)
   {
      this.messageLabel = messageLabel;
   }
}
