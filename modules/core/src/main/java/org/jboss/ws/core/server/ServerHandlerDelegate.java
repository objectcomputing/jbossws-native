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
package org.jboss.ws.core.server;

import org.jboss.ws.core.HeaderSource;
import org.jboss.ws.metadata.config.Configurable;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/** 
 * @author Thomas.Diesler@jboss.org
 * @since 19-Jan-2005
 */
public abstract class ServerHandlerDelegate implements Configurable, HeaderSource 
{
   private ServerEndpointMetaData sepMetaData;
   
   public ServerHandlerDelegate(ServerEndpointMetaData sepMetaData)
   {
      this.sepMetaData = sepMetaData;
   }

   // Get the order of pre/post handlerchains 
   public abstract HandlerType[] getHandlerTypeOrder();

   public abstract boolean callRequestHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type);

   public abstract boolean callResponseHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type);
   
   public abstract boolean callFaultHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type, Exception ex);

   public abstract void closeHandlerChain(ServerEndpointMetaData sepMetaData, HandlerType type);

   protected boolean isInitialized()
   {
      return sepMetaData.isHandlersInitialized();
   }

   protected void setInitialized(boolean flag)
   {
      sepMetaData.setHandlersInitialized(flag);
   }
}
