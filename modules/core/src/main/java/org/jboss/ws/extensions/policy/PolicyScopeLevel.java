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
package org.jboss.ws.extensions.policy;

/**
 * When attaching a Policy to a WSDL element, a Policy Scope is implied for that attachment.
 * PolicyScopeLevel enumerates all kind of element a Policy can be attached to in wsdl 1.1. 
 * 
 * @author Alessio Soldano, <alessio.soldano@javalinux.it>
 * @since 3-May-2007
 */
public enum PolicyScopeLevel
{
   WSDL_SERVICE,                 //wsdl:service
   WSDL_PORT,                    //wsdl:port
   WSDL_PORT_TYPE,               //wsdl:portType
   WSDL_BINDING,                 //wsdl:binding
   BINDING_OPERATION,            //wsdl:binding/wsdl:operation
   PORT_TYPE_OPERATION,          //wsdl:portType/wsdl:operation
   BINDING_OPERATION_INPUT,      //wsdl:binding/wsdl:operation/wsdl:input
   PORT_TYPE_OPERATION_INPUT,    //wsdl:portType/wsdl:operation/wsdl:input
   MESSAGE                       //wsdl:message
}
