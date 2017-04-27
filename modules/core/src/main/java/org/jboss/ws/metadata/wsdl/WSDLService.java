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
package org.jboss.ws.metadata.wsdl;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;

/**
 * A Service component describes a set of endpoints (see 2.14 Endpoint [p.62] ) at which a particular
 * deployed implementation of the service is provided. The endpoints thus are in effect alternate places at
 * which the service is provided.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="jason.greene@jboss.com">Jason T. Greene</a>
 * @since 10-Oct-2004
 */
public class WSDLService extends Extendable
{
   private static final long serialVersionUID = 1274166611190648479L;

   // provide logging
   private static final Logger log = Logger.getLogger(WSDLService.class);
   
   // The parent WSDL definitions element.
   private final WSDLDefinitions wsdlDefinitions;

   private final QName name;
   
   /** The interface attribute information item identifies the interface that the service is an instance of. */
   private QName interfaceName;

   /** One or more endpoint element information items */
   private ArrayList<WSDLEndpoint> endpoints = new ArrayList<WSDLEndpoint>();

   public WSDLService(WSDLDefinitions wsdlDefinitions, QName name)
   {
      this.wsdlDefinitions = wsdlDefinitions;
      this.name = name;
   }

   public WSDLDefinitions getWsdlDefinitions()
   {
      return wsdlDefinitions;
   }

   public QName getName()
   {
      return name;
   }
   
   public QName getInterfaceName()
   {
      return interfaceName;
   }

   public void setInterfaceName(QName interfaceName)
   {
      this.interfaceName = interfaceName;
   }

   public WSDLEndpoint[] getEndpoints()
   {
      WSDLEndpoint[] arr = new WSDLEndpoint[endpoints.size()];
      endpoints.toArray(arr);
      return arr;
   }

   public void addEndpoint(WSDLEndpoint endpoint)
   {
      endpoints.add(endpoint);
   }

   /** Get an endpoint for the given name
    */
   public WSDLEndpoint getEndpoint(QName portName)
   {
      Iterator it = endpoints.iterator();
      while (it.hasNext())
      {
         WSDLEndpoint wsdlEndpoint = (WSDLEndpoint)it.next();
         if (portName.equals(wsdlEndpoint.getName()))
            return wsdlEndpoint;
      }
      return null;
   }
}
