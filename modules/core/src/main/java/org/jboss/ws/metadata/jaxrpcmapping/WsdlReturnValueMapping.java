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

import javax.xml.namespace.QName;

/**
 * XML mapping of the java-wsdl-mapping/service-endpoint-interface-mapping/service-endpoint-method-mapping/wsdl-return-value-mapping element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class WsdlReturnValueMapping implements Serializable
{
   private static final long serialVersionUID = -6910106650463959774L;

   // The parent <service-endpoint-method-mapping> element
   private ServiceEndpointMethodMapping serviceEndpointMethodMapping;

   // The required <method-return-value> element
   private String methodReturnValue;
   // The required <wsdl-message> element
   private QName wsdlMessage;
   // The optional <wsdl-message> element
   private String wsdlMessagePartName;

   public WsdlReturnValueMapping(ServiceEndpointMethodMapping serviceEndpointMethodMapping)
   {
      this.serviceEndpointMethodMapping = serviceEndpointMethodMapping;
   }

   public ServiceEndpointMethodMapping getServiceEndpointMethodMapping()
   {
      return serviceEndpointMethodMapping;
   }

   public String getMethodReturnValue()
   {
      return methodReturnValue;
   }

   public void setMethodReturnValue(String methodReturnValue)
   {
      this.methodReturnValue = methodReturnValue;
   }

   public QName getWsdlMessage()
   {
      return wsdlMessage;
   }

   public void setWsdlMessage(QName wsdlMessage)
   {
      this.wsdlMessage = wsdlMessage;
   }

   public String getWsdlMessagePartName()
   {
      return wsdlMessagePartName;
   }

   public void setWsdlMessagePartName(String wsdlMessagePartName)
   {
      this.wsdlMessagePartName = wsdlMessagePartName;
   }
   
   public String serialize()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<wsdl-return-value-mapping><method-return-value>").append(methodReturnValue).append("</method-return-value>");
      sb.append("<wsdl-message xmlns:").append(wsdlMessage.getPrefix()).append("='").append(wsdlMessage.getNamespaceURI());
      sb.append("'>").append(wsdlMessage.getPrefix()).append(":").append(wsdlMessage.getLocalPart()).append("</wsdl-message>");
      sb.append("<wsdl-message-part-name>").append(wsdlMessagePartName).append("</wsdl-message-part-name>");
      sb.append("</wsdl-return-value-mapping>");
      
      return sb.toString(); 
   }
}
