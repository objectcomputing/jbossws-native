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
package org.jboss.ws.metadata.config.jaxws;

import java.util.ArrayList;
import java.util.List;

import org.jboss.ws.metadata.config.CommonConfig;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.HandlerMetaData;
import org.jboss.ws.metadata.umdm.HandlerMetaDataJAXWS;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/** 
 * A JBossWS client configuration 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 18-Dec-2005
 */
public abstract class CommonConfigJAXWS extends CommonConfig
{
   private HandlerChainsConfigJAXWS preHandlerChains;
   private HandlerChainsConfigJAXWS postHandlerChains;

   public HandlerChainsConfigJAXWS getPostHandlerChains()
   {
      return postHandlerChains;
   }

   public void setPostHandlerChains(HandlerChainsConfigJAXWS postHandlerChain)
   {
      this.postHandlerChains = postHandlerChain;
   }

   public HandlerChainsConfigJAXWS getPreHandlerChains()
   {
      return preHandlerChains;
   }

   public void setPreHandlerChains(HandlerChainsConfigJAXWS preHandlerChains)
   {
      this.preHandlerChains = preHandlerChains;
   }

   public List<HandlerMetaData> getHandlers(EndpointMetaData epMetaData, HandlerType type)
   {
      List<HandlerMetaData> handlers = new ArrayList<HandlerMetaData>();

      HandlerChainsConfigJAXWS handlerChains;
      if (type == HandlerType.PRE)
         handlerChains = getPreHandlerChains();
      else if (type == HandlerType.POST)
         handlerChains = getPostHandlerChains();
      else throw new IllegalArgumentException("Invalid handler type: " + type);

      if (handlerChains != null)
      {
         for (UnifiedHandlerChainMetaData handlerChain : handlerChains.getHandlerChains())
         {
            for (UnifiedHandlerMetaData uhmd : handlerChain.getHandlers())
            {
               HandlerMetaDataJAXWS hmd = HandlerMetaDataJAXWS.newInstance(uhmd, type);
               hmd.setEndpointMetaData(epMetaData);
               handlers.add(hmd);
            }
         }
      }

      return handlers;
   }
}
