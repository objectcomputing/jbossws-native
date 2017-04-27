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

import javax.xml.namespace.QName;

/**
 * XML mapping of the java-wsdl-mapping/exception-mapping element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class ExceptionMapping implements Serializable
{
   private static final long serialVersionUID = 2964098233936811614L;

   // The parent <java-wsdl-mapping> element
   private JavaWsdlMapping javaWsdlMapping;

   // The required <exception-type> element
   private String exceptionType;
   // The required <wsdl-message> element
   private QName wsdlMessage;
   // The optional <constructor-parameter-order> element
   private ArrayList constructorParameterOrder = new ArrayList();

   public ExceptionMapping(JavaWsdlMapping javaWsdlMapping)
   {
      this.javaWsdlMapping = javaWsdlMapping;
   }

   public JavaWsdlMapping getJavaWsdlMapping()
   {
      return javaWsdlMapping;
   }

   public String getExceptionType()
   {
      return exceptionType;
   }

   public void setExceptionType(String exceptionType)
   {
      this.exceptionType = exceptionType;
   }

   public QName getWsdlMessage()
   {
      return wsdlMessage;
   }

   public void setWsdlMessage(QName wsdlMessage)
   {
      this.wsdlMessage = wsdlMessage;
   }

   public String[] getConstructorParameterOrder()
   {
      String[] arr = new String[constructorParameterOrder.size()];
      constructorParameterOrder.toArray(arr);
      return arr;
   }

   public void addConstructorParameter(String elementName)
   {
      constructorParameterOrder.add(elementName);
   }

   public String serialize()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<exception-mapping>");
      sb.append("<exception-type>").append(exceptionType).append("</exception-type>");
      sb.append("<wsdl-message xmlns:exMsgNS='").append(wsdlMessage.getNamespaceURI()).append("'>");
      sb.append("exMsgNS").append(":").append(wsdlMessage.getLocalPart());
      sb.append("</wsdl-message>");

      if (constructorParameterOrder.size() > 0)
      {
         sb.append("<constructor-parameter-order>");
         for (Iterator i = constructorParameterOrder.iterator(); i.hasNext();)
            sb.append("<element-name>").append(i.next()).append("</element-name>");
         sb.append("</constructor-parameter-order>");
      }
      sb.append("</exception-mapping>");
      return sb.toString();
   }
}
