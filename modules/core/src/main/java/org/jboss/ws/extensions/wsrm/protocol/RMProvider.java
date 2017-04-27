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
package org.jboss.ws.extensions.wsrm.protocol;

import org.jboss.wsf.spi.util.ServiceLoader;

/**
 * WS-RM Provider SPI facade. Each WS-RM provider must override this class.
 *
 * @author richard.opalka@jboss.com
 */
public abstract class RMProvider
{
   
   private static final RMProvider rmProviderImpl = (RMProvider) ServiceLoader.loadService(
      RMProvider.class.getName(),
         org.jboss.ws.extensions.wsrm.protocol.spec200702.RMProviderImpl.class.getName());

   /**
    * Must be overriden in subclasses
    * @param targetNamespace
    */
   protected RMProvider()
   {
   }
   
   /**
    * Returns the namespace associated with current WS-RM provider implementation
    * @return
    */
   public abstract String getNamespaceURI();
   
   /**
    * Returns WS-RM provider specific message factory
    * @return message factory
    */
   public abstract RMMessageFactory getMessageFactory();
   
   /**
    * Returns WS-RM provider specific constants
    * @return constants
    */
   public abstract RMConstants getConstants();
   
   public static final RMProvider get()
   {
      return rmProviderImpl;
   }
   
}
