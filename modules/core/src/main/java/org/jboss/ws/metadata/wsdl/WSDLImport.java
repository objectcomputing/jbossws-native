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

/**
 * The WSDL import element information item, like the include element information item (see 4.1 Including Descriptions [p.73] )
 * also allows for the separation of the different components of a WSDL description into independent descriptions,
 * but in this case with different target namespaces, which can be imported as needed. This technique helps writing
 * clearer WSDL descriptions by separating the definitions according to their level of abstraction, and maximizes reusability.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public class WSDLImport implements Serializable
{
   private static final long serialVersionUID = -2641009286158029207L;

   // The parent WSDL definitions
   private WSDLDefinitions wsdlDefinitions;

   /** The REQUIRED namespace attribute information item is of type xs:anyURI . Its actual value indicates that the
    * containing WSDL document MAY contain qualified references to WSDL definitions in that namespace
    * (via one or more prefixes declared with namespace declarations in the normal way). This value MUST
    * NOT match the actual value of the enclosing WSDL document targetNamespace attribute
    * information item. If the import statement results in the import of a WSDL document then the actual value
    * of the namespace attribute information item MUST be identical to the actual value of the imported
    * WSDL document's targetNamespace attribute information item. */
   private String namespace;

   /** The OPTIONAL location attribute information item is of type xs:anyURI . Its actual value is the location of
    * some information about the namespace identified by the namespace attribute information item.
    * The location attribute information item is optional. This allows WSDL components to be constructed
    * from information other than serialized XML 1.0. It also allows the development of WSDL processors that
    * have a priori (i.e., built-in) knowledge of certain namespaces. */
   private String location;

   public WSDLImport(WSDLDefinitions wsdlDefinitions)
   {
      this.wsdlDefinitions = wsdlDefinitions;
   }

   public WSDLDefinitions getWsdlDefinitions()
   {
      return wsdlDefinitions;
   }

   public String getNamespace()
   {
      return namespace;
   }

   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }

   public String getLocation()
   {
      return location;
   }

   public void setLocation(String location)
   {
      this.location = location;
   }
}
