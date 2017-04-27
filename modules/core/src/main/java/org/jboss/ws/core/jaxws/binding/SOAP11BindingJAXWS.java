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
package org.jboss.ws.core.jaxws.binding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.SOAPBinding;

import org.jboss.ws.Constants;
import org.jboss.ws.core.CommonSOAP11Binding;
import org.jboss.ws.core.jaxws.SOAPFaultHelperJAXWS;
import org.jboss.ws.core.soap.SOAPFaultImpl;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * The JAXWS SOAP11Binding
 *
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jul-2006
 */
public class SOAP11BindingJAXWS extends CommonSOAP11Binding implements BindingExt, SOAPBinding
{
   // Delegate to JAXWS SOAP binding
   private SOAPBindingJAXWS delegate = new SOAPBindingJAXWS();

   public SOAP11BindingJAXWS()
   {
      setMTOMEnabled(false);
   }

   public SOAP11BindingJAXWS(boolean mtomEnabled)
   {
      setMTOMEnabled(mtomEnabled);
   }

   public void setSOAPActionHeader(OperationMetaData opMetaData, SOAPMessage reqMessage)
   {
      delegate.setSOAPActionHeader(opMetaData, reqMessage);
   }

   public Set<String> getRoles()
   {
      // 10.3 Conformance (Default role visibility): An implementation MUST include the required next and ultimate
      // receiver roles in the Set returned from SOAPBinding.getRoles.
      // In SOAP 1.1 the ultimate receiver role is identified by omission of the actor attribute from a SOAP header.
      Set<String> soap11Roles = new HashSet<String>(delegate.getRoles());
      soap11Roles.add(Constants.URI_SOAP11_NEXT_ACTOR);
      return soap11Roles;
   }

   public void setRoles(Set<String> roles)
   {
      delegate.setRoles(roles);
   }

   public List<Handler> getHandlerChain()
   {
      return delegate.getHandlerChain();
   }

   public List<Handler> getHandlerChain(HandlerType handlerType)
   {
      return delegate.getHandlerChain(handlerType);
   }

   public void setHandlerChain(List<Handler> handlerChain)
   {
      delegate.setHandlerChain(handlerChain);
   }

   public void setHandlerChain(List<Handler> handlerChain, HandlerType handlerType)
   {
      delegate.setHandlerChain(handlerChain, handlerType);
   }

   public SOAPMessage createFaultMessageFromException(Exception ex)
   {
      return SOAPFaultHelperJAXWS.exceptionToFaultMessage(ex);
   }

   protected void throwFaultException(SOAPFaultImpl fault) throws Exception
   {
      throw SOAPFaultHelperJAXWS.getSOAPFaultException(fault);
   }

   public String getBindingID()
   {
      return isMTOMEnabled() ? SOAPBinding.SOAP11HTTP_MTOM_BINDING : SOAPBinding.SOAP11HTTP_BINDING;
   }
}
