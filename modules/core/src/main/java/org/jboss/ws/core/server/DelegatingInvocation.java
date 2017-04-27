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
package org.jboss.ws.core.server;

import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonBinding;
import org.jboss.ws.core.CommonBindingProvider;
import org.jboss.ws.core.EndpointInvocation;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.wsf.spi.invocation.Invocation;

import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPMessage;

/** An invocation that delegates to the jbossws-core EndpointInvocation
 *
 * @author Thomas.Diesler@jboss.org
 * @since 25-Apr-2007
 */
public class DelegatingInvocation extends Invocation
{
   private EndpointInvocation getEndpointInvocation()
   {
      EndpointInvocation epInv = getInvocationContext().getAttachment(EndpointInvocation.class);
      if (epInv == null)
         throw new IllegalStateException("Cannot obtain endpoint invocation");

      return epInv;
   }

   @Override
   public void setReturnValue(Object value)
   {
      EndpointInvocation epInv = getEndpointInvocation();
      epInv.setReturnValue(value);

      SOAPMessageContext msgContext = (SOAPMessageContext)getInvocationContext().getAttachment(javax.xml.rpc.handler.MessageContext.class);
      if (msgContext != null && msgContext.getMessage() == null)
      {
         try
         {
            // Bind the response message
            OperationMetaData opMetaData = epInv.getOperationMetaData();
            CommonBindingProvider bindingProvider = new CommonBindingProvider(opMetaData.getEndpointMetaData());
            CommonBinding binding = (CommonBinding)bindingProvider.getCommonBinding();
            SOAPMessage resMessage = (SOAPMessage)binding.bindResponseMessage(opMetaData, epInv);
            msgContext.setMessage(resMessage);
         }
         catch (BindingException ex)
         {
            WSException.rethrow(ex);
         }
      }
   }

   @Override
   public Object[] getArgs()
   {
      EndpointInvocation epInv = getEndpointInvocation();
      return epInv.getRequestPayload();
   }

   @Override
   public Object getReturnValue()
   {
      EndpointInvocation epInv = getEndpointInvocation();
      return epInv.getReturnValue();
   }

   @Override
   public void setArgs(Object[] args)
   {
      throw new IllegalArgumentException("Cannot set args on this invocation");
   }
}
