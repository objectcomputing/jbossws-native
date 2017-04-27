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
package org.jboss.ws.core.jaxrpc.handler;

import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.core.CommonMessageContext;

/**
 * Provides access to the SOAP message for either RPC request or response.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Dec-2004
 */
public class SOAPMessageContextJAXRPC extends MessageContextJAXRPC implements SOAPMessageContext
{
   /** Default constuctor
    */
   public SOAPMessageContextJAXRPC()
   {
   }

   public SOAPMessageContextJAXRPC(CommonMessageContext msgContext)
   {
      super(msgContext);
   }

   public SOAPMessage getMessage()
   {
      return getSOAPMessage();
   }

   public void setMessage(SOAPMessage message)
   {
      setSOAPMessage(message);
   }

   /**
    * Gets the SOAP actor roles associated with an execution of the HandlerChain and its contained Handler instances.
    * Note that SOAP actor roles apply to the SOAP node and are managed using HandlerChain.setRoles and HandlerChain.getRoles.
    * Handler instances in the HandlerChain use this information about the SOAP actor roles to process the SOAP header blocks.
    * Note that the SOAP actor roles are invariant during the processing of SOAP message through the HandlerChain.
    *
    * @return Array of URIs for SOAP actor roles
    */
   public String[] getRoles()
   {
      return new String[0];
   }
}
