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
package org.jboss.ws.core.jaxws.spi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.spi.ServiceDelegate;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.jboss.ws.core.jaxws.wsaddressing.EndpointReferenceUtil;
import org.jboss.ws.core.jaxws.wsaddressing.NativeEndpointReference;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 * Service provider for ServiceDelegate and Endpoint objects.
 *  
 * @author Thomas.Diesler@jboss.com
 * @since 03-May-2006
 */
public class ProviderImpl extends Provider
{
   // 6.2 Conformance (Concrete javax.xml.ws.spi.Provider required): An implementation MUST provide
   // a concrete class that extends javax.xml.ws.spi.Provider. Such a class MUST have a public constructor
   // which takes no arguments.
   public ProviderImpl()
   {
   }

   @Override
   public ServiceDelegate createServiceDelegate(URL wsdlLocation, QName serviceName, Class serviceClass)
   {
      ServiceDelegateImpl delegate = new ServiceDelegateImpl(wsdlLocation, serviceName, serviceClass);
      DOMUtils.clearThreadLocals();
      return delegate;
   }

   @Override
   public Endpoint createEndpoint(String bindingId, Object implementor)
   {
      EndpointImpl endpoint = new EndpointImpl(bindingId, implementor);
      return endpoint;
   }

   @Override
   public Endpoint createAndPublishEndpoint(String address, Object implementor)
   {
      // 6.3 Conformance (Provider createAndPublishEndpoint Method): The effect of invoking the createAnd-
      // PublishEndpoint method on a Provider MUST be the same as first invoking the createEndpoint
      // method with the binding ID appropriate to the URL scheme used by the address, then invoking the 
      // publish(String address) method on the resulting endpoint.

      String bindingId = getBindingFromAddress(address);
      EndpointImpl endpoint = (EndpointImpl)createEndpoint(bindingId, implementor);
      endpoint.publish(address);
      return endpoint;
   }

   private String getBindingFromAddress(String address)
   {
      String bindingId;
      try
      {
         URL url = new URL(address);
         String protocol = url.getProtocol();
         if (protocol.startsWith("http"))
         {
            bindingId = SOAPBinding.SOAP11HTTP_BINDING;
         }
         else
         {
            throw new IllegalArgumentException("Unsupported protocol: " + address);
         }
      }
      catch (MalformedURLException e)
      {
         throw new IllegalArgumentException("Invalid endpoint address: " + address);
      }
      return bindingId;
   }

   @Override
   public W3CEndpointReference createW3CEndpointReference(String address, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation,
         List<Element> referenceParameters)
   {
      NativeEndpointReference epr = new NativeEndpointReference();
      epr.setAddress(address);
      epr.setServiceName(serviceName);
      epr.setEndpointName(portName);
      epr.setMetadata(metadata);
      epr.setWsdlLocation(wsdlDocumentLocation);
      epr.setReferenceParameters(referenceParameters);
      return EndpointReferenceUtil.transform(W3CEndpointReference.class, epr);
   }

   @Override
   public <T> T getPort(EndpointReference epr, Class<T> sei, WebServiceFeature... features)
   {
      URL wsdlLocation = null;
      QName serviceName = null;
      NativeEndpointReference nepr = EndpointReferenceUtil.transform(NativeEndpointReference.class, epr);
      
      wsdlLocation = nepr.getWsdlLocation();
      serviceName = nepr.getServiceName();
      ServiceDelegate delegate = createServiceDelegate(wsdlLocation, serviceName, Service.class);
      return delegate.getPort(epr, sei, features);
   }

   @Override
   public EndpointReference readEndpointReference(Source eprInfoset)
   {
      if (eprInfoset == null)
         throw new NullPointerException("Provided eprInfoset cannot be null");
      try
      {
         //we currently support W3CEndpointReference only
         return new W3CEndpointReference(eprInfoset);
      }
      catch (Exception e)
      {
         throw new WebServiceException(e);
      }
   }
}
