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
package org.jboss.ws.core.jaxws.handler;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.PortInfo;

/**
 * The PortInfo interface is used by a HandlerResolver to query information about the 
 * port it is being asked to create a handler chain for.
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 03-May-2006
 */
public class PortInfoImpl implements PortInfo
{
   private QName serviceName;
   private QName portName;
   private String bindingID;

   public PortInfoImpl()
   {
   }

   public PortInfoImpl(QName serviceName, QName portName, String bindingID)
   {
      this.serviceName = serviceName;
      this.portName = portName;
      this.bindingID = bindingID;
   }

   public String getBindingID()
   {
      return bindingID;
   }

   public QName getPortName()
   {
      return portName;
   }

   public QName getServiceName()
   {
      return serviceName;
   }

   public int hashCode()
   {
      return toString().hashCode();
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof PortInfoImpl))
         return false;
      return (obj != null ? toString().equals(obj.toString()) : false);
   }

   public String toString()
   {
      return "[service=" + serviceName + ",port=" + portName + ",binding=" + bindingID + "]";
   }
}
