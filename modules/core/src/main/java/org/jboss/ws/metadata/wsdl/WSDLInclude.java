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
 * The WSDL include element information item allows for the separation of different components of a
 * service definition, belonging the same target namespace, into independent WSDL documents which can be
 * merged as needed.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public class WSDLInclude implements Serializable
{
   private static final long serialVersionUID = 1210980063899094649L;

   // The parent WSDL definitions
   private WSDLDefinitions wsdlDefinitions;

   /** A location attribute information item is of type xs:anyURI . Its actual value is the location of some
    * information about the namespace identified by the targetNamespace attribute information item of the
    * containing definitions element information item.*/
   private String location;

   public WSDLInclude(WSDLDefinitions wsdlDefinitions)
   {
      this.wsdlDefinitions = wsdlDefinitions;
   }

   public WSDLDefinitions getWsdlDefinitions()
   {
      return wsdlDefinitions;
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
