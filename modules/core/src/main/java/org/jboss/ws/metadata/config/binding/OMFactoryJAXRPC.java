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
package org.jboss.ws.metadata.config.binding;

import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.ws.metadata.config.EndpointProperty;
import org.jboss.ws.metadata.config.jaxrpc.ClientConfigJAXRPC;
import org.jboss.ws.metadata.config.jaxrpc.CommonConfigJAXRPC;
import org.jboss.ws.metadata.config.jaxrpc.ConfigRootJAXRPC;
import org.jboss.ws.metadata.config.jaxrpc.EndpointConfigJAXRPC;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedInitParamMetaData;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;

/** 
 * ObjectModelFactory for JAXRPC configurations.
 *
 * @author Thomas.Diesler@jboss.org
 * @author Heiko.Braun@jboss.org
 * @since 18-Dec-2005
 */
public class OMFactoryJAXRPC implements ObjectModelFactory
{
   // provide logging
   private final Logger log = Logger.getLogger(OMFactoryJAXRPC.class);

   public Object newRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName, Attributes attrs)
   {
      return new ConfigRootJAXRPC();
   }

   public Object completeRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName)
   {
      return root;
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(ConfigRootJAXRPC config, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("WSConfig newChild: " + localName);
      if ("endpoint-config".equals(localName))
      {
         EndpointConfigJAXRPC wsEndpointConfig = new EndpointConfigJAXRPC();
         config.getEndpointConfig().add(wsEndpointConfig);
         return wsEndpointConfig;
      }
      if ("client-config".equals(localName))
      {
         ClientConfigJAXRPC clientConfig = new ClientConfigJAXRPC();
         config.getClientConfig().add(clientConfig);
         return clientConfig;
      }
      return null;
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(CommonConfigJAXRPC commonConfig, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      if (log.isTraceEnabled())
         log.trace("CommonConfig setValue: nuri=" + namespaceURI + " localName=" + localName + " value=" + value);

      if (localName.equals("config-name"))
         commonConfig.setConfigName(value);
      if(localName.equals("feature"))
         commonConfig.setFeature(value, true);

      if("property-name".equals(localName))
      {
         commonConfig.addProperty(value,  null);
      }
      else if("property-value".equals(localName))
      {
         int lastEntry = commonConfig.getProperties().isEmpty() ? 0 : commonConfig.getProperties().size()-1;
         EndpointProperty p = commonConfig.getProperties().get(lastEntry);
         p.value = value;
      }
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(CommonConfigJAXRPC commonConfig, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("CommonConfig newChild: " + localName);

      if ("pre-handler-chain".equals(localName))
      {
         UnifiedHandlerChainMetaData preHandlerChain = new UnifiedHandlerChainMetaData(null);
         commonConfig.setPreHandlerChain(preHandlerChain);
         return preHandlerChain;
      }
      if ("post-handler-chain".equals(localName))
      {
         UnifiedHandlerChainMetaData postHandlerChain = new UnifiedHandlerChainMetaData(null);
         commonConfig.setPostHandlerChain(postHandlerChain);
         return postHandlerChain;
      }
      return null;
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(UnifiedHandlerChainMetaData UnifiedHandlerChainMetaData, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("WSHandlerChainConfig newChild: " + localName);
      if ("handler".equals(localName))
      {
         UnifiedHandlerMetaData handler = new UnifiedHandlerMetaData(UnifiedHandlerChainMetaData);
         List<UnifiedHandlerMetaData> handlers = UnifiedHandlerChainMetaData.getHandlers();
         handlers.add(handler);
         return handler;
      }
      return null;
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(UnifiedHandlerMetaData handler, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      if ("init-param".equals(localName))
         return new UnifiedInitParamMetaData();
      else return null;
   }
   
   /**
    * Called when parsing character is complete.
    */
   public void addChild(UnifiedHandlerMetaData handler, UnifiedInitParamMetaData param, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      handler.addInitParam(param);
   }   
   
   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(UnifiedHandlerMetaData handler, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      if (log.isTraceEnabled())
         log.trace("UnifiedHandlerMetaData setValue: nuri=" + namespaceURI + " localName=" + localName + " value=" + value);

      if (localName.equals("handler-name"))
         handler.setHandlerName(value);
      else if (localName.equals("handler-class"))
         handler.setHandlerClass(value);
      else if (localName.equals("soap-header"))
         handler.addSoapHeader(navigator.resolveQName(value));
      else if (localName.equals("soap-role"))
         handler.addSoapRole(value);
      else if (localName.equals("port-name"))
         handler.addPortName(value);
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(UnifiedInitParamMetaData param, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      if (log.isTraceEnabled())
         log.trace("UnifiedInitParamMetaData setValue: nuri=" + namespaceURI + " localName=" + localName + " value=" + value);

      if (localName.equals("param-name"))
         param.setParamName(value);
      else if (localName.equals("param-value"))
         param.setParamValue(value);
   }
}
