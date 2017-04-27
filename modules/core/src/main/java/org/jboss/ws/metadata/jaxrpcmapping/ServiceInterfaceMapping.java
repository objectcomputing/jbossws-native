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
 * XML mapping of the java-wsdl-mapping/service-interface-mapping element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class ServiceInterfaceMapping implements Serializable
{
   private static final long serialVersionUID = -447051823681281236L;

   // The parent <java-wsdl-mapping> element
   private JavaWsdlMapping javaWsdlMapping;

   // The required <service-interface> element
   private String serviceInterface;
   // The required <wsdl-service-name> element
   private QName wsdlServiceName;
   // Zero or more <port-mapping> elements
   private List  portMappings = new ArrayList();

   public ServiceInterfaceMapping(JavaWsdlMapping javaWsdlMapping)
   {
      this.javaWsdlMapping = javaWsdlMapping;
   }

   public JavaWsdlMapping getJavaWsdlMapping()
   {
      return javaWsdlMapping;
   }

   public String getServiceInterface()
   {
      return serviceInterface;
   }

   public void setServiceInterface(String serviceInterface)
   {
      this.serviceInterface = serviceInterface;
   }

   public QName getWsdlServiceName()
   {
      return wsdlServiceName;
   }

   public void setWsdlServiceName(QName wsdlServiceName)
   {
      this.wsdlServiceName = wsdlServiceName;
   }

   public PortMapping[] getPortMappings()
   {
      PortMapping[] arr = new PortMapping[portMappings.size()];
      portMappings.toArray(arr);
      return arr;
   }

   public void addPortMapping(PortMapping portMapping)
   {
      portMappings.add(portMapping);
   }
   
   public String serialize()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<service-interface-mapping>");
      sb.append("<service-interface>").append(serviceInterface).append("</service-interface>");
      sb.append("<wsdl-service-name xmlns:").append(wsdlServiceName.getPrefix()).append("='");
      sb.append(wsdlServiceName.getNamespaceURI()).append("'>");
      sb.append(wsdlServiceName.getPrefix()).append(":").append(wsdlServiceName.getLocalPart());
      sb.append("</wsdl-service-name>");
      
      Iterator iter = portMappings.iterator();
      while(iter != null && iter.hasNext())
         sb.append(((PortMapping)iter.next()).serialize()); 
      sb.append("</service-interface-mapping>");
      return sb.toString();
   }
}
