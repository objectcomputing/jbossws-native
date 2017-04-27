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

import javax.xml.namespace.QName;

/**
 * A Binding Fault component describes a concrete binding of a particular fault within an interface to a
 * particular concrete message format. A particular fault of an interface is uniquely identified by the target
 * namespace of the interface and the name of the fault within that interface.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public class WSDLBindingFault implements Serializable
{
   private static final long serialVersionUID = 6306975072558524200L;

   // The parent WSDL binding
   private WSDLBinding wsdlBinding;

   /** An REQUIRED Interface Fault component in the {faults} property of the Interface
    * component identified by the {interface} property of the parent Binding component. This is the
    * Interface Fault component for which binding information is being specified.*/
   private QName ref;

   public WSDLBindingFault(WSDLBinding wsdlBinding)
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
}
