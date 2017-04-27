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
package org.jboss.ws.extensions.security.jaxws;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.WSSecurityDispatcher;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.wsse.WSSecurityConfigFactory;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.wsf.common.handler.GenericSOAPHandler;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;

/**
 * An abstract JAXWS handler that delegates to the WSSecurityDispatcher
 *
 * @author Thomas.Diesler@jboss.org
 * @since 12-Nov-2005
 */
public abstract class WSSecurityHandler extends GenericSOAPHandler
{
   // provide logging
   private static Logger log = Logger.getLogger(WSSecurityHandler.class);

   private static Set<QName> headers;

   static
   {
      HashSet<QName> set = new HashSet<QName>();
      set.add(Constants.WSSE_HEADER_QNAME);
      headers = Collections.unmodifiableSet(set);
   }

   public Set<QName> getHeaders()
   {
      return headers;
   }

   protected boolean handleInboundSecurity(MessageContext msgContext)
   {
      try
      {
         WSSecurityConfiguration configuration = getSecurityConfiguration(msgContext);
         if (configuration != null)
         {
            CommonMessageContext ctx = (CommonMessageContext)msgContext;
            SOAPMessageImpl soapMessage = (SOAPMessageImpl)ctx.getSOAPMessage();
            new WSSecurityDispatcher().decodeMessage(configuration, soapMessage, null);
         }
      }
      catch (SOAPException ex)
      {
         log.error("Cannot handle inbound ws-security", ex);
         return false;
      }
      return true;
   }

   protected boolean handleOutboundSecurity(MessageContext msgContext)
   {
      try
      {
         WSSecurityConfiguration configuration = getSecurityConfiguration(msgContext);
         if (configuration != null)
         {
            CommonMessageContext ctx = (CommonMessageContext)msgContext;
            SOAPMessageImpl soapMessage = (SOAPMessageImpl)ctx.getSOAPMessage();
            
            String user = (String)ctx.get(BindingProvider.USERNAME_PROPERTY);
            String pass = (String)ctx.get(BindingProvider.PASSWORD_PROPERTY);
            new WSSecurityDispatcher().encodeMessage(configuration, soapMessage, null, user, pass);
         }
      }
      catch (SOAPException ex)
      {
         log.error("Cannot handle outbound ws-security", ex);
         return false;
      }
      return true;
   }

   /**
    * Load security config from vfsRoot
    * @param msgContext
    */
   private WSSecurityConfiguration getSecurityConfiguration(MessageContext msgContext)
   {
      EndpointMetaData epMetaData = ((CommonMessageContext)msgContext).getEndpointMetaData();
      ServiceMetaData serviceMetaData = epMetaData.getServiceMetaData();

      if(serviceMetaData.getSecurityConfiguration() == null) // might be set through ServiceObjectFactory
      {
         UnifiedVirtualFile vfsRoot = serviceMetaData.getUnifiedMetaData().getRootFile();
         WSSecurityConfiguration config = null;
         try
         {
            WSSecurityConfigFactory wsseConfFactory = WSSecurityConfigFactory.newInstance();
            config = wsseConfFactory.createConfiguration(vfsRoot, getConfigResourceName());
         }
         catch (IOException ex)
         {
            WSException.rethrow("Cannot load ws-security config", ex);
         }

         // it's required further down the processing chain
         serviceMetaData.setSecurityConfiguration(config);

      }

      return serviceMetaData.getSecurityConfiguration();
   }

   protected abstract String getConfigResourceName();
}
