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
package org.jboss.ws.core;

import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.soap.UnboundHeader;
import org.jboss.ws.metadata.umdm.OperationMetaData;

/**
 * The base interface for protocol bindings. 
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jul-2006
 */
public interface CommonBinding
{
   /** On the client side, generate the Object from IN parameters. */
   MessageAbstraction bindRequestMessage(OperationMetaData opMetaData, EndpointInvocation epInv, Map<QName, UnboundHeader> unboundHeaders) throws BindingException;

   /** On the server side, extract the IN parameters from the Object and populate an Invocation object */
   EndpointInvocation unbindRequestMessage(OperationMetaData opMetaData, MessageAbstraction reqMessage) throws BindingException;

   /** On the server side, generate the Object from OUT parameters in the Invocation object. */
   MessageAbstraction bindResponseMessage(OperationMetaData opMetaData, EndpointInvocation epInv) throws BindingException;

   /** On the client side, extract the OUT parameters from the Object and return them to the client. */
   void unbindResponseMessage(OperationMetaData opMetaData, MessageAbstraction resMessage, EndpointInvocation epInv, Map<QName, UnboundHeader> unboundHeaders) throws BindingException;

   /** bind an exception to a fault message */
   MessageAbstraction bindFaultMessage(Exception ex);
   
   void setHeaderSource(HeaderSource source);
}
