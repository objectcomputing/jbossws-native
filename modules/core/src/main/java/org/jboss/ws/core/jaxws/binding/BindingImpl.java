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
package org.jboss.ws.core.jaxws.binding;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * The Binding interface is the base interface for JAXWS protocol bindings. 
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jul-2006
 */
public class BindingImpl implements BindingExt 
{
   // provide logging
   private static Logger log = Logger.getLogger(BindingImpl.class);

   private List<Handler> preHandlerChain = new ArrayList<Handler>();
   private List<Handler> jaxwsHandlerChain = new ArrayList<Handler>();
   private List<Handler> postHandlerChain = new ArrayList<Handler>();

   public List<Handler> getHandlerChain(HandlerType handlerType)
   {
      List<Handler> handlerChain = new ArrayList<Handler>();
      
      if (handlerType == HandlerType.PRE || handlerType == HandlerType.ALL)
         handlerChain.addAll(preHandlerChain);
      
      if (handlerType == HandlerType.ENDPOINT || handlerType == HandlerType.ALL)
         handlerChain.addAll(jaxwsHandlerChain);
      
      if (handlerType == HandlerType.POST || handlerType == HandlerType.ALL)
         handlerChain.addAll(postHandlerChain);
      
      return handlerChain;
   }

   public void setHandlerChain(List<Handler> handlerChain, HandlerType handlerType)
   {
      if (handlerType == HandlerType.PRE || handlerType == HandlerType.ALL)
      {
         preHandlerChain.clear();
         preHandlerChain.addAll(handlerChain);
      }
      
      if (handlerType == HandlerType.ENDPOINT || handlerType == HandlerType.ALL)
      {
         jaxwsHandlerChain.clear();
         jaxwsHandlerChain.addAll(handlerChain);
      }
      
      if (handlerType == HandlerType.POST || handlerType == HandlerType.ALL)
      {
         postHandlerChain.clear();
         postHandlerChain.addAll(handlerChain);
      }
   }

   public List<Handler> getHandlerChain()
   {
      return new ArrayList<Handler>(jaxwsHandlerChain);
   }

   public void setHandlerChain(List<Handler> handlerChain)
   {
      log.debug("setHandlerChain: " + handlerChain);
      jaxwsHandlerChain.clear();
      jaxwsHandlerChain.addAll(handlerChain);
   }

   public String getBindingID()
   {
      throw new NotImplementedException();
   }
}
