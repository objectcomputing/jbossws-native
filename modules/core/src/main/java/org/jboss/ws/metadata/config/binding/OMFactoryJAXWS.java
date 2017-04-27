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

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.metadata.config.EndpointProperty;
import org.jboss.ws.metadata.config.jaxws.ClientConfigJAXWS;
import org.jboss.ws.metadata.config.jaxws.CommonConfigJAXWS;
import org.jboss.ws.metadata.config.jaxws.ConfigRootJAXWS;
import org.jboss.ws.metadata.config.jaxws.EndpointConfigJAXWS;
import org.jboss.ws.metadata.config.jaxws.HandlerChainsConfigJAXWS;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.HandlerChainsObjectFactory;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;
import org.jboss.ws.extensions.wsrm.config.RMBackPortsServerConfig;
import org.jboss.ws.extensions.wsrm.config.RMDeliveryAssuranceConfig;
import org.jboss.ws.extensions.wsrm.config.RMMessageRetransmissionConfig;
import org.jboss.ws.extensions.wsrm.config.RMConfig;
import org.jboss.ws.extensions.wsrm.config.RMPortConfig;

/**
 * ObjectModelFactory for JAXWS configurations.
 *
 * @author Thomas.Diesler@jboss.org
 * @author Heiko.Braun@jboss.org
 * @since 18-Dec-2005
 */
public class OMFactoryJAXWS extends HandlerChainsObjectFactory
{
   // provide logging
   private final Logger log = Logger.getLogger(OMFactoryJAXWS.class);

   public Object newRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName, Attributes attrs)
   {
      return new ConfigRootJAXWS();
   }

   public Object completeRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName)
   {
      return root;
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(ConfigRootJAXWS config, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("WSConfig newChild: " + localName);
      if ("endpoint-config".equals(localName))
      {
         EndpointConfigJAXWS wsEndpointConfig = new EndpointConfigJAXWS();
         config.getEndpointConfig().add(wsEndpointConfig);
         return wsEndpointConfig;
      }
      if ("client-config".equals(localName))
      {
         ClientConfigJAXWS clientConfig = new ClientConfigJAXWS();
         config.getClientConfig().add(clientConfig);
         return clientConfig;
      }
      return null;
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(CommonConfigJAXWS commonConfig, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
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
   public Object newChild(CommonConfigJAXWS commonConfig, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("CommonConfig newChild: " + localName);

      if ("pre-handler-chains".equals(localName))
      {
         HandlerChainsConfigJAXWS preHandlerChains = new HandlerChainsConfigJAXWS();
         commonConfig.setPreHandlerChains(preHandlerChains);
         return preHandlerChains;
      }
      if ("post-handler-chains".equals(localName))
      {
         HandlerChainsConfigJAXWS postHandlerChains = new HandlerChainsConfigJAXWS();
         commonConfig.setPostHandlerChains(postHandlerChains);
         return postHandlerChains;
      }
      if ("reliable-messaging".equals(localName))
      {
         RMConfig wsrmCfg = new RMConfig();
         commonConfig.setRMMetaData(wsrmCfg);
         return wsrmCfg;
      }

      return null;
   }
   
   public Object newChild(RMConfig wsrmConfig, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      int countOfAttributes = attrs.getLength();

      if (localName.equals("delivery-assurance"))
      {
         RMDeliveryAssuranceConfig deliveryAssurance = getDeliveryAssurance(attrs);
         wsrmConfig.setDeliveryAssurance(deliveryAssurance);
         return deliveryAssurance;
      }
      if (localName.equals("message-retransmission"))
      {
         int interval = 0, attempts = 0, timeout=0;
         for (int i = 0; i < countOfAttributes; i++)
         {
            String attrLocalName = attrs.getLocalName(i); 
            if (attrLocalName.equals("interval"))
               interval = Integer.valueOf(attrs.getValue(i));
            if (attrLocalName.equals("attempts"))
               attempts = Integer.valueOf(attrs.getValue(i));
            if (attrLocalName.equals("timeout"))
               timeout = Integer.valueOf(attrs.getValue(i));
         }
         
         RMMessageRetransmissionConfig retransmissionConfig = new RMMessageRetransmissionConfig();
         retransmissionConfig.setCountOfAttempts(attempts);
         retransmissionConfig.setRetransmissionInterval(interval);
         retransmissionConfig.setMessageTimeout(timeout);
         wsrmConfig.setMessageRetransmission(retransmissionConfig);
         return retransmissionConfig;
      }
      if (localName.equals("backports-server"))
      {
         String host = null, port = null;
         for (int i = 0; i < countOfAttributes && (host == null || port == null); i++)
         {
            String attrLocalName = attrs.getLocalName(i); 
            if (attrLocalName.equals("host"))
               host = attrs.getValue(i);
            if (attrLocalName.equals("port"))
               port = attrs.getValue(i);
         }
         
         RMBackPortsServerConfig backportsServer = new RMBackPortsServerConfig();
         backportsServer.setHost(host);
         backportsServer.setPort(port);
         wsrmConfig.setBackPortsServer(backportsServer);
         return backportsServer;
      }
      if (localName.equals("port"))
      {
         String portName = null;
         for (int i = 0; i < countOfAttributes; i++)
         {
            if (attrs.getLocalName(i).equals("name"))
            {
               portName = attrs.getValue(i);
               break;
            }
         }
         RMPortConfig port = new RMPortConfig();
         port.setPortName(QName.valueOf(portName));
         wsrmConfig.getPorts().add(port);
         return port;
      }
      
      return null;
   }
   
   public Object newChild(RMPortConfig port, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      if (localName.equals("delivery-assurance"))
      {
         RMDeliveryAssuranceConfig deliveryAssurance = getDeliveryAssurance(attrs);
         port.setDeliveryAssurance(deliveryAssurance);
         return deliveryAssurance;
      }
      
      return null;
   }
   
   private RMDeliveryAssuranceConfig getDeliveryAssurance(Attributes attrs)
   {
      String inOrder = null, quality = null;
      for (int i = 0; i < attrs.getLength() && (inOrder == null || quality == null); i++)
      {
         String attrLocalName = attrs.getLocalName(i); 
         if (attrLocalName.equals("inOrder"))
            inOrder = attrs.getValue(i);
         if (attrLocalName.equals("quality"))
            quality = attrs.getValue(i);
      }
      RMDeliveryAssuranceConfig deliveryAssurance = new RMDeliveryAssuranceConfig();
      deliveryAssurance.setQuality(quality);
      deliveryAssurance.setInOrder(inOrder);
      return deliveryAssurance;
   }
   
   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(HandlerChainsConfigJAXWS handlerChains, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("WSHandlerChainsConfig newChild: " + localName);

      if ("handler-chain".equals(localName))
      {
         UnifiedHandlerChainMetaData handlerChain = new UnifiedHandlerChainMetaData(null);
         handlerChains.getHandlerChains().add(handlerChain);
         return handlerChain;
      }
      return null;
   }
}
