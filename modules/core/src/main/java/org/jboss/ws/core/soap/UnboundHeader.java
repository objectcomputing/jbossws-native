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
package org.jboss.ws.core.soap;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;

/**
 * Represents an unbound SOAPHeaderElement
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Jan-2005
 */
public class UnboundHeader
{
   private QName xmlName;
   private QName xmlType;
   private Class javaType;
   private ParameterMode mode;
   private Object headerValue;

   public UnboundHeader(QName xmlName, QName xmlType, Class javaType, ParameterMode mode)
   {
      this.xmlName = xmlName;
      this.xmlType = xmlType;
      this.javaType = javaType;
      this.mode = mode;
   }

   public QName getXmlName()
   {
      return xmlName;
   }

   public QName getXmlType()
   {
      return xmlType;
   }

   public Class getJavaType()
   {
      return javaType;
   }

   public ParameterMode getMode()
   {
      return mode;
   }

   public Object getHeaderValue()
   {
      return headerValue;
   }

   public void setHeaderValue(Object headerValue)
   {
      this.headerValue = headerValue;
   }

   public ParameterMetaData toParameterMetaData(OperationMetaData opMetaData)
   {
      ParameterMetaData paramMetaData = new ParameterMetaData(opMetaData, xmlName, xmlType, javaType.getName());
      paramMetaData.setInHeader(true);
      return paramMetaData;
   }

   public String toString()
   {
      StringBuilder buffer = new StringBuilder("\nUnboundHeader:");
      buffer.append("\n xmlName=" + getXmlName());
      buffer.append("\n xmlType=" + getXmlType());
      buffer.append("\n javaType=" + getJavaType().getName());
      buffer.append("\n mode=" + getMode());
      return buffer.toString();
   }
}
