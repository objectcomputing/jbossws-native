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
package org.jboss.ws.metadata.umdm;

import javax.xml.namespace.QName;

import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * The JAXWS metdata data for a handler element
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 05-May-2006
 */
public class HandlerMetaDataJAXWS extends HandlerMetaData
{
   private static final long serialVersionUID = 7631133188974299826L;

   // The JAXWS service name pattern
   private QName serviceNamePattern;
   // The JAXWS port name pattern
   private QName portNamePattern;
   // The JAXWS protocol bindings
   private String protocolBindings;

   public static HandlerMetaDataJAXWS newInstance(UnifiedHandlerMetaData uhmd, HandlerType type)
   {
      HandlerMetaDataJAXWS hmd = new HandlerMetaDataJAXWS(type);
      hmd.setHandlerName(uhmd.getHandlerName());
      hmd.setHandlerClassName(uhmd.getHandlerClass());
      hmd.setInitParams(uhmd.getInitParams());
      UnifiedHandlerChainMetaData handlerChain = uhmd.getHandlerChain();
      if (handlerChain != null)
      {
         hmd.setProtocolBindings(handlerChain.getProtocolBindings());
         hmd.setServiceNamePattern(handlerChain.getServiceNamePattern());
         hmd.setPortNamePattern(handlerChain.getPortNamePattern());
      }
      return hmd;
   }

   public HandlerMetaDataJAXWS(HandlerType type)
   {
      super(type);
   }

   public QName getPortNamePattern()
   {
      return portNamePattern;
   }

   public void setPortNamePattern(QName portNamePattern)
   {
      this.portNamePattern = portNamePattern;
   }

   public String getProtocolBindings()
   {
      return protocolBindings;
   }

   public void setProtocolBindings(String protocolBindings)
   {
      this.protocolBindings = protocolBindings;
   }

   public QName getServiceNamePattern()
   {
      return serviceNamePattern;
   }

   public void setServiceNamePattern(QName serviceNamePattern)
   {
      this.serviceNamePattern = serviceNamePattern;
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer("\nHandlerMetaDataJAXWS:");
      buffer.append("\n type=" + getHandlerType());
      buffer.append("\n name=" + getHandlerName());
      buffer.append("\n class=" + getHandlerClass());
      buffer.append("\n params=" + getInitParams());
      buffer.append("\n protocols=" + getProtocolBindings());
      buffer.append("\n services=" + getServiceNamePattern());
      buffer.append("\n ports=" + getPortNamePattern());
      return buffer.toString();
   }

}
