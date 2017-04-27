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
package org.jboss.ws.core.binding;

import org.jboss.wsf.spi.binding.BindingCustomization;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.core.soap.MessageContextAssociation;

/**
 * A Serializer that can handle complex types by delegating to JAXB.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public abstract class ComplexTypeSerializer extends SerializerSupport
{
   protected BindingCustomization getBindingCustomization()
   {
      BindingCustomization bindingCustomization = null;
      EndpointMetaData epMetaData = MessageContextAssociation.peekMessageContext().getEndpointMetaData();
      if(epMetaData instanceof ServerEndpointMetaData)
      {
         bindingCustomization = ((ServerEndpointMetaData)epMetaData).getEndpoint().getAttachment(BindingCustomization.class);
      }
      return bindingCustomization;
   }
}
