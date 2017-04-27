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
 * Created by IntelliJ IDEA.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class WsdlMessageMapping implements Serializable
{
   private static final long serialVersionUID = -3212852147033081838L;

   // The parent <method-param-parts-mapping> element
   private MethodParamPartsMapping methodParamPartsMapping;
   // The required <wsdl-message> element
   private QName wsdlMessage;
   // The required <wsdl-message-part-name> element
   private String wsdlMessagePartName;
   // The required <parameter-mode> element
   private String parameterMode;
   // The optional <soap-header> element
   private boolean soapHeader;

   public WsdlMessageMapping(MethodParamPartsMapping methodParamPartsMapping)
   {
      this.methodParamPartsMapping = methodParamPartsMapping;
   }

   public MethodParamPartsMapping getMethodParamPartsMapping()
   {
      return methodParamPartsMapping;
   }

   public String getParameterMode()
   {
      return parameterMode;
   }

   public void setParameterMode(String parameterMode)
   {
      if ("IN".equals(parameterMode) == false && "OUT".equals(parameterMode) == false && "INOUT".equals(parameterMode) == false)
         throw new IllegalArgumentException("Invalid parameter mode: " + parameterMode);
      this.parameterMode = parameterMode;
   }

   public boolean isSoapHeader()
   {
      return soapHeader;
   }

   public void setSoapHeader(boolean soapHeader)
   {
      this.soapHeader = soapHeader;
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
      sb.append(" <wsdl-message-mapping> <wsdl-message xmlns:");
      sb.append(wsdlMessage.getPrefix()).append("='").append(wsdlMessage.getNamespaceURI()).append("'>");
      sb.append(wsdlMessage.getPrefix()).append(":").append(wsdlMessage.getLocalPart()).append("</wsdl-message>");
      sb.append("<wsdl-message-part-name>").append(wsdlMessagePartName).append("</wsdl-message-part-name>");
      sb.append("<parameter-mode>").append(parameterMode).append("</parameter-mode>");
      if (soapHeader)
         sb.append("<soap-header/>");
      sb.append("</wsdl-message-mapping>");
      return sb.toString();
   }
}
