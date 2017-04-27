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
package org.jboss.ws.core.jaxrpc.handler;

import java.util.List;
import java.util.Set;

import javax.xml.rpc.handler.MessageContext;

import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * Represents a list of handlers. All elements in the
 * HandlerChain are of the type javax.xml.rpc.handler.Handler.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 06-May-2004
 */
public class ServerHandlerChain extends HandlerChainBaseImpl
{
   // The required type of the handler
   private HandlerType type;
   
   public ServerHandlerChain(List infos, Set roles, HandlerType type)
   {
      super(infos, roles);
      this.type = type;
   }

   public boolean handleRequest(MessageContext msgContext)
   {
      boolean doNext = super.handleRequest(msgContext);
      return doNext;
   }

   public boolean handleResponse(MessageContext msgContext)
   {
      boolean doNext = super.handleResponse(msgContext);
      return doNext;
   }
}
