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

/**
 * XML mapping of the java-wsdl-mapping/service-endpoint-interface-mapping/service-endpoint-method-mapping element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class ServiceEndpointMethodMapping implements Serializable
{
   private static final long serialVersionUID = 7857267872017006227L;

   // The parent <service-endpoint-interface-mapping> element
   private ServiceEndpointInterfaceMapping serviceEndpointInterfaceMapping;

   // The required <java-method-name> element
   private String javaMethodName;
   // The required <wsdl-operation> element
   private String wsdlOperation;
   // The optional <wrapped-element> element
   private boolean wrappedElement;
   // Zero or more <method-param-parts-mapping> elements
   private List methodParamPartsMappings = new ArrayList();
   // The optional <wsdl-return-value-mapping> element
   private WsdlReturnValueMapping wsdlReturnValueMapping;

   public ServiceEndpointMethodMapping(ServiceEndpointInterfaceMapping serviceEndpointInterfaceMapping)
   {
      this.serviceEndpointInterfaceMapping = serviceEndpointInterfaceMapping;
   }

   public ServiceEndpointInterfaceMapping getServiceEndpointInterfaceMapping()
   {
      return serviceEndpointInterfaceMapping;
   }

   public String getJavaMethodName()
   {
      return javaMethodName;
   }

   public void setJavaMethodName(String javaMethodName)
   {
      this.javaMethodName = javaMethodName;
   }

   public MethodParamPartsMapping[] getMethodParamPartsMappings()
   {
      MethodParamPartsMapping[] arr = new MethodParamPartsMapping[methodParamPartsMappings.size()];
      methodParamPartsMappings.toArray(arr);
      return arr;
   }

   public MethodParamPartsMapping getMethodParamPartsMappingByPartName(String partName)
   {
      MethodParamPartsMapping paramMapping = null;
      for (int i = 0; paramMapping == null && i < methodParamPartsMappings.size(); i++)
      {
         MethodParamPartsMapping aux = (MethodParamPartsMapping)methodParamPartsMappings.get(i);
         if (aux.getWsdlMessageMapping().getWsdlMessagePartName().equals(partName))
            paramMapping = aux;
      }
      return paramMapping;
   }

   public MethodParamPartsMapping getMethodParamPartsMappingByPosition(int pos)
   {
      MethodParamPartsMapping paramMapping = null;
      for (int i = 0; paramMapping == null && i < methodParamPartsMappings.size(); i++)
      {
         MethodParamPartsMapping aux = (MethodParamPartsMapping)methodParamPartsMappings.get(i);
         if (aux.getParamPosition() == pos)
            paramMapping = aux;
      }
      return paramMapping;
   }
   
   public void addMethodParamPartsMapping(MethodParamPartsMapping methodParamPartsMapping)
   {
      methodParamPartsMappings.add(methodParamPartsMapping);
   }

   public boolean isWrappedElement()
   {
      return wrappedElement;
   }

   public void setWrappedElement(boolean wrappedElement)
   {
      this.wrappedElement = wrappedElement;
   }

   public String getWsdlOperation()
   {
      return wsdlOperation;
   }

   public void setWsdlOperation(String wsdlOperation)
   {
      this.wsdlOperation = wsdlOperation;
   }

   public WsdlReturnValueMapping getWsdlReturnValueMapping()
   {
      return wsdlReturnValueMapping;
   }

   public void setWsdlReturnValueMapping(WsdlReturnValueMapping wsdlReturnValueMapping)
   {
      this.wsdlReturnValueMapping = wsdlReturnValueMapping;
   }

   public String serialize()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<service-endpoint-method-mapping><java-method-name>").append(javaMethodName).append("</java-method-name>");
      sb.append("<wsdl-operation>").append(wsdlOperation).append("</wsdl-operation>");
      if (wrappedElement)
         sb.append("<wrapped-element/>");
      Iterator iter = methodParamPartsMappings.iterator();
      while (iter != null && iter.hasNext())
         sb.append(((MethodParamPartsMapping)iter.next()).serialize());
      if (wsdlReturnValueMapping != null)
         sb.append(wsdlReturnValueMapping.serialize());
      sb.append("</service-endpoint-method-mapping>");
      return sb.toString();
   }
}
