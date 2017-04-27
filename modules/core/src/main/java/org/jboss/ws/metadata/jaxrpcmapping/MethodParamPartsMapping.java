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

/**
 * XML mapping of the java-wsdl-mapping/service-endpoint-interface-mapping/service-endpoint-method-mapping/method-param-parts-mapping element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class MethodParamPartsMapping implements Serializable
{
   private static final long serialVersionUID = -1351920471783503813L;

   // The parent <service-endpoint-method-mapping> element
   private ServiceEndpointMethodMapping serviceEndpointMethodMapping;

   // The required <param-position> element
   private int paramPosition;
   // The required <param-type> element
   private String paramType;
   // The required <wsdl-message-mapping> element
   private WsdlMessageMapping wsdlMessageMapping;

   public MethodParamPartsMapping(ServiceEndpointMethodMapping serviceEndpointMethodMapping)
   {
      this.serviceEndpointMethodMapping = serviceEndpointMethodMapping;
   }

   public ServiceEndpointMethodMapping getServiceEndpointMethodMapping()
   {
      return serviceEndpointMethodMapping;
   }

   public int getParamPosition()
   {
      return paramPosition;
   }

   public void setParamPosition(int paramPosition)
   {
      this.paramPosition = paramPosition;
   }

   public String getParamType()
   {
      return paramType;
   }

   public void setParamType(String paramType)
   {
      this.paramType = paramType;
   }

   public WsdlMessageMapping getWsdlMessageMapping()
   {
      return wsdlMessageMapping;
   }

   public void setWsdlMessageMapping(WsdlMessageMapping wsdlMessageMapping)
   {
      this.wsdlMessageMapping = wsdlMessageMapping;
   }
   
   public String serialize()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<method-param-parts-mapping><param-position>").append(paramPosition).append("</param-position>");
      sb.append("<param-type>").append(paramType).append("</param-type>");
      if(wsdlMessageMapping == null)
         throw new IllegalStateException("wsdlMessageMapping is null");
      sb.append(wsdlMessageMapping.serialize()); 
      
      sb.append("</method-param-parts-mapping>");
      return sb.toString();
   }
}
