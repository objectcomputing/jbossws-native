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

import java.util.Observable;

import javax.xml.ws.EndpointReference;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.jboss.logging.Logger;
import org.jboss.ws.core.jaxrpc.SOAP11BindingJAXRPC;
import org.jboss.ws.core.jaxrpc.SOAP12BindingJAXRPC;
import org.jboss.ws.core.jaxws.binding.HTTPBindingJAXWS;
import org.jboss.ws.core.jaxws.binding.SOAP11BindingJAXWS;
import org.jboss.ws.core.jaxws.binding.SOAP12BindingJAXWS;
import org.jboss.ws.core.jaxws.wsaddressing.EndpointReferenceUtil;
import org.jboss.ws.metadata.config.Configurable;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;

/**
 * Provides access to the protocol binding.
 *
 * @author Thomas.Diesler@jboss.com
 * @author Heiko.Braun@jboss.com
 * @since 04-Jul-2006
 */
public class CommonBindingProvider implements Configurable
{
   private static Logger log = Logger.getLogger(CommonBindingProvider.class);

   protected EndpointMetaData epMetaData;
   protected CommonBinding binding;

   public CommonBindingProvider(EndpointMetaData epMetaData)
   {
      this.epMetaData = epMetaData;
      initBinding(epMetaData.getBindingId(), epMetaData.getType());

      this.epMetaData.registerConfigObserver(this);
      configure();
   }

   public CommonBindingProvider(String bindingId, Type type)
   {
      initBinding(bindingId, type);
      configure();
   }

   private void configure()
   {
      // process MTOM config elements
      if (epMetaData != null)
      {
         epMetaData.configure(this);
      }
   }

   protected void initBinding(String bindingId, Type type)
   {
      if (SOAPBinding.SOAP11HTTP_BINDING.equals(bindingId))
      {
         binding = (type == Type.JAXWS ? new SOAP11BindingJAXWS() : new SOAP11BindingJAXRPC());
      }
      else if (SOAPBinding.SOAP11HTTP_MTOM_BINDING.equals(bindingId))
      {
         binding = (type == Type.JAXWS ? new SOAP11BindingJAXWS(true) : new SOAP11BindingJAXRPC(true));
      }
      else if (SOAPBinding.SOAP12HTTP_BINDING.equals(bindingId))
      {
         binding = (type == Type.JAXWS ? new SOAP12BindingJAXWS() : new SOAP12BindingJAXRPC());
      }
      else if (SOAPBinding.SOAP12HTTP_MTOM_BINDING.equals(bindingId))
      {
         binding = (type == Type.JAXWS ? new SOAP12BindingJAXWS(true) : new SOAP12BindingJAXRPC(true));
      }
      else if (HTTPBinding.HTTP_BINDING.equals(bindingId))
      {
         binding = new HTTPBindingJAXWS();
      }
   }

   public CommonBinding getCommonBinding()
   {
      return binding;
   }

   public EndpointReference getEndpointReference()
   {
      if (binding instanceof HTTPBinding )
      {
         throw new UnsupportedOperationException("Cannot get EPR for BindingProvider instances using the XML/HTTP binding");
      }
      return getEndpointReference(W3CEndpointReference.class);
   }

   public <T extends EndpointReference> T getEndpointReference(Class<T> clazz)
   {
      // Conformance 4.5 (javax.xml.ws.BindingProvider.getEndpointReference): An implementation
      // MUST be able to return an javax.xml.ws.EndpointReference for the target endpoint if a SOAP binding
      // is being used. If the BindingProvider instance has a binding that is either SOAP 1.1/HTTP or
      // SOAP 1.2/HTTP, then a W3CEndpointReference MUST be returned. If the binding is XML/HTTP an
      // java.lang.UnsupportedOperationExceptionMUST be thrown.

      if (binding instanceof HTTPBinding )
      {
         throw new UnsupportedOperationException("Cannot get epr for BindingProvider instances using the XML/HTTP binding");
      }
      W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
      if (epMetaData != null)
      {
         builder.address(epMetaData.getEndpointAddress());
         builder.serviceName(epMetaData.getServiceMetaData().getServiceName());
         builder.endpointName(epMetaData.getPortName());
         builder.wsdlDocumentLocation(epMetaData.getEndpointAddress() + "?wsdl");
      }
      else
      {
         log.warn("Cannot get endpoint reference info from endpoint metadata!");
      }
      return EndpointReferenceUtil.transform(clazz, builder.build());
   }

   public void update(Observable observable, Object object)
   {
      if(log.isDebugEnabled()) log.debug("Update config: " + object);
      configure();
   }
}
