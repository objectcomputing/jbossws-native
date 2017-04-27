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
package org.jboss.ws.core.jaxrpc;

import java.util.Set;

import javax.xml.soap.SOAPMessage;

import org.jboss.ws.core.CommonSOAP11Binding;
import org.jboss.ws.core.RoleSource;
import org.jboss.ws.core.soap.SOAPFaultImpl;
import org.jboss.ws.metadata.umdm.OperationMetaData;

/**
 * The JAXRPC SOAP11Binding
 *
 * @author Thomas.Diesler@jboss.com
 * @since 20-Sep-2006
 */
public class SOAP11BindingJAXRPC extends CommonSOAP11Binding
{
   // Delegate to JAXWS SOAP binding
   private SOAPBindingJAXRPC delegate = new SOAPBindingJAXRPC();

   public SOAP11BindingJAXRPC()
   {
      setMTOMEnabled(false);
   }

   public SOAP11BindingJAXRPC(boolean mtomEnabled)
   {
      setMTOMEnabled(mtomEnabled);
   }

   public void setSOAPActionHeader(OperationMetaData opMetaData, SOAPMessage reqMessage)
   {
      delegate.setSOAPActionHeader(opMetaData, reqMessage);
   }

   public SOAPMessage createFaultMessageFromException(Exception ex)
   {
      return SOAPFaultHelperJAXRPC.exceptionToFaultMessage(ex);
   }

   protected void throwFaultException(SOAPFaultImpl fault) throws Exception
   {
      throw SOAPFaultHelperJAXRPC.getSOAPFaultException(fault);
   }

   @Override
   public Set<String> getRoles()
   {
      if (!(headerSource instanceof RoleSource))
         throw new IllegalStateException("RoleSource was not available");

      return ((RoleSource)headerSource).getRoles();
   }
}
