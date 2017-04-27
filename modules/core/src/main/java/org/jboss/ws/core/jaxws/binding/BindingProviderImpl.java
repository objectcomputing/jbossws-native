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

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.http.HTTPBinding;

import org.jboss.ws.core.CommonBindingProvider;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;

/**
 * The BindingProvider interface provides access to the protocol binding and associated context objects 
 * for request and response message processing.
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jul-2006
 */
public class BindingProviderImpl extends CommonBindingProvider implements BindingProvider
{
   private Map<String, Object> requestContext = new HashMap<String, Object>();
   private Map<String, Object> responseContext = new HashMap<String, Object>();

   public BindingProviderImpl(EndpointMetaData epMetaData)
   {
      super(epMetaData);
   }

   public BindingProviderImpl(String bindingId)
   {
      super(bindingId, Type.JAXWS);
   }

   @Override
   protected void initBinding(String bindingId, Type type)
   {
      super.initBinding(bindingId, type);
      
      if (HTTPBinding.HTTP_BINDING.equals(bindingId) == false)
      {
         Mode serviceMode = (epMetaData != null ?  epMetaData.getServiceMode() : null);
         if (serviceMode == Mode.MESSAGE)
         {
            binding = new MessageBinding();
         }
         else if (serviceMode == Mode.PAYLOAD)
         {
            binding = new PayloadBinding();
         }
      }
      
      if (binding == null)
         throw new WebServiceException("Unsupported binding: " + bindingId);
   }

   public Map<String, Object> getRequestContext()
   {
      return requestContext;
   }

   public Map<String, Object> getResponseContext()
   {
      return responseContext;
   }

   public Binding getBinding()
   {
      return (Binding)binding;
   }
}
