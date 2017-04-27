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

import java.util.HashSet;
import java.util.Set;

import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * The JAXRPC metdata data for a handler element
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 05-May-2006
 */
public class HandlerMetaDataJAXRPC extends HandlerMetaData
{
   private static final long serialVersionUID = -5232305815202943509L;

   // The optional <soap-role> elements
   private Set<String> soapRoles = new HashSet<String>();
   // The optional <port-name> elements
   private Set<String> portNames = new HashSet<String>();

   public static HandlerMetaDataJAXRPC newInstance(UnifiedHandlerMetaData uhmd, HandlerType type)
   {
      HandlerMetaDataJAXRPC hmd = new HandlerMetaDataJAXRPC(type);
      hmd.setHandlerName(uhmd.getHandlerName());
      hmd.setHandlerClassName(uhmd.getHandlerClass());
      hmd.setInitParams(uhmd.getInitParams());
      hmd.setSoapHeaders(uhmd.getSoapHeaders());
      hmd.setSoapRoles(uhmd.getSoapRoles());
      hmd.setPortNames(uhmd.getPortNames());
      return hmd;
   }

   public HandlerMetaDataJAXRPC(HandlerType type)
   {
      super(type);
   }

   public void setSoapRoles(Set<String> soapRoles)
   {
      this.soapRoles = soapRoles;
   }

   public Set<String> getSoapRoles()
   {
      return soapRoles;
   }

   public void setPortNames(Set<String> portNames)
   {
      this.portNames = portNames;
   }

   public Set<String> getPortNames()
   {
      return portNames;
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer("\nHandlerMetaDataJAXRPC:");
      buffer.append("\n type=" + getHandlerType());
      buffer.append("\n name=" + getHandlerName());
      buffer.append("\n class=" + getHandlerClassName());
      buffer.append("\n params=" + getInitParams());
      buffer.append("\n headers=" + getSoapHeaders());
      buffer.append("\n roles=" + getSoapRoles());
      buffer.append("\n ports=" + getPortNames());
      return buffer.toString();
   }
}
