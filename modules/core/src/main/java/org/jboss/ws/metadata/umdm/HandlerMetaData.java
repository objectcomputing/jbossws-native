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
package org.jboss.ws.metadata.umdm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedInitParamMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;

/**
 * The common metdata data for a handler element
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 05-May-2006
 */
public abstract class HandlerMetaData implements InitalizableMetaData, Serializable
{
   // provide logging
   private final Logger log = Logger.getLogger(HandlerMetaData.class);
   
   private transient EndpointMetaData epMetaData;

   // The required <handler-name> element
   private String handlerName;
   // The required <handler-class> element
   private String handlerClassName;
   // The required handler type
   private HandlerType handlerType;
   // The optional <soap-header> elements
   private Set<QName> soapHeaders = new HashSet<QName>();
   // The optional <init-param> elements
   private List<UnifiedInitParamMetaData> initParams = new ArrayList<UnifiedInitParamMetaData>();
   // The cached handler class
   private Class handlerClass;

   public HandlerMetaData(HandlerType type)
   {
      this.handlerType = type;
   }

   public void setEndpointMetaData(EndpointMetaData epMetaData)
   {
      this.epMetaData = epMetaData;
   }

   public EndpointMetaData getEndpointMetaData()
   {
      return epMetaData;
   }

   public void setHandlerName(String value)
   {
      this.handlerName = value;
   }

   public String getHandlerName()
   {
      return handlerName;
   }

   public void setHandlerClassName(String handlerClass)
   {
      this.handlerClassName = handlerClass;
   }

   public String getHandlerClassName()
   {
      return handlerClassName;
   }

   public Class getHandlerClass()
   {
      if (handlerClassName == null)
         throw new IllegalStateException("Handler class name cannot be null");
      
      Class localClass = handlerClass;
      if (localClass == null)
      {
         try
         {
            ClassLoader loader = getClassLoader();
            localClass = loader.loadClass(handlerClassName);
         }
         catch (ClassNotFoundException ex)
         {
            throw new WSException("Cannot load handler: " + handlerClassName, ex);
         }
      }
      return localClass;
   }

   private ClassLoader getClassLoader()
   {
      // The EndpointMetaData classloader is not availabel for a handler associaated with a JAXWS Service.handlerResolver
      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      return (epMetaData != null ? epMetaData.getClassLoader() : ctxLoader);
   }

   public HandlerType getHandlerType()
   {
      return handlerType;
   }

   public void setSoapHeaders(Set<QName> soapHeaders)
   {
      this.soapHeaders = soapHeaders;
   }

   public Set<QName> getSoapHeaders()
   {
      return soapHeaders;
   }

   public void setInitParams(List<UnifiedInitParamMetaData> initParams)
   {
      this.initParams = initParams;
   }

   public List<UnifiedInitParamMetaData> getInitParams()
   {
      return initParams;
   }

   public void validate()
   {
      List<String> securityHandlers = new ArrayList<String>();
      securityHandlers.add(org.jboss.ws.extensions.security.jaxrpc.WSSecurityHandlerInbound.class.getName());
      securityHandlers.add(org.jboss.ws.extensions.security.jaxrpc.WSSecurityHandlerOutbound.class.getName());
      securityHandlers.add(org.jboss.ws.extensions.security.jaxws.WSSecurityHandlerServer.class.getName());
      securityHandlers.add(org.jboss.ws.extensions.security.jaxws.WSSecurityHandlerClient.class.getName());
      
      if (securityHandlers.contains(handlerClassName) && epMetaData != null)
      {
         if (epMetaData.getServiceMetaData().getSecurityConfiguration() == null)
            log.warn("WS-Security requires security configuration");
      }
   }

   public void eagerInitialize()
   {
      handlerClass = getHandlerClass();
   }
}
