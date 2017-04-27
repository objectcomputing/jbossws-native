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
package org.jboss.ws.metadata.jaxrpcmapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * XML mapping of the java-wsdl-mapping/service-endpoint-interface-mapping element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class ServiceEndpointInterfaceMapping implements Serializable
{
   private static final long serialVersionUID = 3336973427288868587L;

   // The parent <java-wsdl-mapping> element
   private JavaWsdlMapping javaWsdlMapping;

   // The required <service-endpoint-interface> element
   private String serviceEndpointInterface;
   // The required <wsdl-port-type> element
   private QName wsdlPortType;
   // The required <wsdl-binding> element
   private QName wsdlBinding;
   // Zero or more <service-endpoint-method-mapping> elements
   private List serviceEndpointMethodMappings = new ArrayList();

   public ServiceEndpointInterfaceMapping(JavaWsdlMapping javaWsdlMapping)
   {
      this.javaWsdlMapping = javaWsdlMapping;
   }

   public JavaWsdlMapping getJavaWsdlMapping()
   {
      return javaWsdlMapping;
   }

   public String getServiceEndpointInterface()
   {
      return serviceEndpointInterface;
   }

   public void setServiceEndpointInterface(String serviceEndpointInterface)
   {
      this.serviceEndpointInterface = serviceEndpointInterface;
   }

   public QName getWsdlPortType()
   {
      return wsdlPortType;
   }

   public void setWsdlPortType(QName wsdlPortType)
   {
      this.wsdlPortType = wsdlPortType;
   }

   public QName getWsdlBinding()
   {
      return wsdlBinding;
   }

   public void setWsdlBinding(QName wsdlBinding)
   {
      this.wsdlBinding = wsdlBinding;
   }

   public ServiceEndpointMethodMapping[] getServiceEndpointMethodMappings()
   {
      ServiceEndpointMethodMapping[] arr = new ServiceEndpointMethodMapping[serviceEndpointMethodMappings.size()];
      serviceEndpointMethodMappings.toArray(arr);
      return arr;
   }

   public void addServiceEndpointMethodMapping(ServiceEndpointMethodMapping serviceEndpointMethodMapping)
   {
      serviceEndpointMethodMappings.add(serviceEndpointMethodMapping);
   }

   public ServiceEndpointMethodMapping getServiceEndpointMethodMappingByWsdlOperation(String wsdlOperation)
   {
      ServiceEndpointMethodMapping semMapping = null;

      Iterator it = serviceEndpointMethodMappings.iterator();
      while (it.hasNext())
      {
         ServiceEndpointMethodMapping aux = (ServiceEndpointMethodMapping)it.next();
         if (aux.getWsdlOperation().equals(wsdlOperation))
            semMapping = aux;
      }
      return semMapping;
   }
   
   public String serialize()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<service-endpoint-interface-mapping><service-endpoint-interface>");
      sb.append(this.serviceEndpointInterface).append("</service-endpoint-interface>");
      sb.append("<wsdl-port-type xmlns:").append(wsdlPortType.getPrefix()).append("='");
      sb.append(wsdlPortType.getNamespaceURI()).append("'>").append(wsdlPortType.getPrefix());
      sb.append(":").append(wsdlPortType.getLocalPart()).append("</wsdl-port-type>");

      sb.append("<wsdl-binding xmlns:").append(wsdlBinding.getPrefix()).append("='");
      sb.append(wsdlBinding.getNamespaceURI()).append("'>").append(wsdlBinding.getPrefix());
      sb.append(":").append(wsdlBinding.getLocalPart()).append("</wsdl-binding>");
      
      Iterator iter = serviceEndpointMethodMappings.iterator();
      while(iter != null && iter.hasNext())
         sb.append(((ServiceEndpointMethodMapping)iter.next()).serialize()); 
      sb.append("</service-endpoint-interface-mapping>");
      return sb.toString();
   }
}
