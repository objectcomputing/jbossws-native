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
package org.jboss.ws.metadata.builder.jaxws;

import javax.jws.WebService;
import javax.xml.ws.WebServiceProvider;

import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.wsf.spi.annotation.WebContext;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.deployment.Deployment.DeploymentType;
import org.jboss.wsf.spi.metadata.j2ee.EJBArchiveMetaData;
import org.jboss.wsf.spi.metadata.j2ee.JSEArchiveMetaData;

/**
 * Builds ServiceEndpointMetaData for a JAX-WS endpoint.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @author Thomas.Diesler@jboss.com
 */
public abstract class JAXWSServerMetaDataBuilder extends JAXWSMetaDataBuilder
{
   static void setupProviderOrWebService(ArchiveDeployment dep, UnifiedMetaData umd, Class<?> beanClass, String beanName) throws Exception
   {
      if (beanClass.isAnnotationPresent(WebService.class))
      {
         JAXWSWebServiceMetaDataBuilder builder = new JAXWSWebServiceMetaDataBuilder();
         builder.buildWebServiceMetaData(dep, umd, beanClass, beanName);
      }
      else if (beanClass.isAnnotationPresent(WebServiceProvider.class))
      {
         JAXWSProviderMetaDataBuilder builder = new JAXWSProviderMetaDataBuilder();
         builder.buildProviderMetaData(dep, umd, beanClass, beanName);
      }
   }

   protected void processEndpointConfig(Deployment dep, ServerEndpointMetaData sepMetaData, Class<?> wsClass, String linkName)
   {
      String configName = null;
      String configFile = null;
      
      EndpointConfig anEndpointConfig = wsClass.getAnnotation(EndpointConfig.class);
      if (anEndpointConfig != null)
      {
         if (anEndpointConfig.configName().length() > 0)
            configName = anEndpointConfig.configName();

         if (anEndpointConfig.configFile().length() > 0)
            configFile = anEndpointConfig.configFile();
      }
      
      JSEArchiveMetaData jseMetaData = dep.getAttachment(JSEArchiveMetaData.class);
      if (jseMetaData != null)
      {
         if (jseMetaData.getConfigName() != null)
            configName = jseMetaData.getConfigName();
         if (jseMetaData.getConfigFile() != null)
            configFile = jseMetaData.getConfigFile();
      }
      
      EJBArchiveMetaData ejbMetaData = dep.getAttachment(EJBArchiveMetaData.class);
      if (ejbMetaData != null)
      {
         if (ejbMetaData.getConfigName() != null)
            configName = ejbMetaData.getConfigName();
         if (ejbMetaData.getConfigFile() != null)
            configFile = ejbMetaData.getConfigFile();
      }
      
      if (configName != null || configFile != null)
         sepMetaData.setConfigName(configName, configFile);
   }

   protected void processWebContext(Deployment dep, Class<?> wsClass, String linkName, ServerEndpointMetaData sepMetaData)
   {
      WebContext anWebContext = wsClass.getAnnotation(WebContext.class);

      if (anWebContext == null)
         return;

      boolean isJSEEndpoint = (dep.getType() == DeploymentType.JAXWS_JSE);

      // context-root
      if (anWebContext.contextRoot().length() > 0)
      {
         if (isJSEEndpoint)
         {
            log.warn("@WebContext.contextRoot is only valid on EJB endpoints");
         }
         else
         {
            String contextRoot = anWebContext.contextRoot();
            if (contextRoot.startsWith("/") == false)
               contextRoot = "/" + contextRoot;

            sepMetaData.setContextRoot(contextRoot);
         }
      }

      // url-pattern
      if (anWebContext.urlPattern().length() > 0)
      {
         if (isJSEEndpoint)
         {
            log.warn("@WebContext.urlPattern is only valid on EJB endpoints");
         }
         else
         {
            String urlPattern = anWebContext.urlPattern();
            sepMetaData.setURLPattern(urlPattern);
         }
      }

      // auth-method
      if (anWebContext.authMethod().length() > 0)
      {
         if (isJSEEndpoint)
         {
            log.warn("@WebContext.authMethod is only valid on EJB endpoints");
         }
         else
         {
            String authMethod = anWebContext.authMethod();
            sepMetaData.setAuthMethod(authMethod);
         }
      }

      // transport-guarantee
      if (anWebContext.transportGuarantee().length() > 0)
      {
         if (isJSEEndpoint)
         {
            log.warn("@WebContext.transportGuarantee is only valid on EJB endpoints");
         }
         else
         {
            String transportGuarantee = anWebContext.transportGuarantee();
            sepMetaData.setTransportGuarantee(transportGuarantee);
         }
      }

      // secure wsdl access
      sepMetaData.setSecureWSDLAccess(anWebContext.secureWSDLAccess());
   }
}
