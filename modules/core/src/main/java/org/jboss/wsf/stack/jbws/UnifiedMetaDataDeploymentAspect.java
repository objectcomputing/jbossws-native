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
package org.jboss.wsf.stack.jbws;

import org.jboss.ws.metadata.builder.jaxrpc.JAXRPCServerMetaDataBuilder;
import org.jboss.ws.metadata.builder.jaxws.JAXWSMetaDataBuilderEJB3;
import org.jboss.ws.metadata.builder.jaxws.JAXWSMetaDataBuilderJSE;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.deployment.DeploymentAspect;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.deployment.Deployment.DeploymentType;

/**
 * A deployer that builds the UnifiedDeploymentInfo 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 25-Apr-2007
 */
public class UnifiedMetaDataDeploymentAspect extends DeploymentAspect
{
   @Override
   public void start(Deployment dep)
   {
      UnifiedMetaData umd = dep.getAttachment(UnifiedMetaData.class);
      if (umd == null)
      {
         if (dep.getType() == DeploymentType.JAXRPC_JSE)
         {
            JAXRPCServerMetaDataBuilder builder = new JAXRPCServerMetaDataBuilder();
            umd = builder.buildMetaData((ArchiveDeployment)dep);
         }
         else if (dep.getType() == DeploymentType.JAXRPC_EJB21)
         {
            JAXRPCServerMetaDataBuilder builder = new JAXRPCServerMetaDataBuilder();
            umd = builder.buildMetaData((ArchiveDeployment)dep);
         }
         else if (dep.getType() == DeploymentType.JAXWS_JSE)
         {
            JAXWSMetaDataBuilderJSE builder = new JAXWSMetaDataBuilderJSE();
            umd = builder.buildMetaData((ArchiveDeployment)dep);
         }
         else if (dep.getType() == DeploymentType.JAXWS_EJB3)
         {
            JAXWSMetaDataBuilderEJB3 builder = new JAXWSMetaDataBuilderEJB3();
            umd = builder.buildMetaData((ArchiveDeployment)dep);
         }
         else
         {
            throw new IllegalStateException("Invalid deployment type:  " + dep.getType());
         }

         dep.addAttachment(UnifiedMetaData.class, umd);
      }

      for (Endpoint ep : dep.getService().getEndpoints())
      {
         ServerEndpointMetaData sepMetaData = ep.getAttachment(ServerEndpointMetaData.class);
         if (sepMetaData == null)
         {
            sepMetaData = getEndpointMetaData(umd, ep);
            sepMetaData.setEndpoint(ep);

            ep.addAttachment(ServerEndpointMetaData.class, sepMetaData);

            String targetBean = ep.getTargetBeanName();
            if (targetBean != null)
               sepMetaData.setServiceEndpointImplName(targetBean);
         }
      }
   }

   private ServerEndpointMetaData getEndpointMetaData(UnifiedMetaData umd, Endpoint ep)
   {
      String epName = ep.getShortName();

      ServerEndpointMetaData epMetaData = null;
      for (ServiceMetaData serviceMetaData : umd.getServices())
      {
         for (EndpointMetaData aux : serviceMetaData.getEndpoints())
         {
            String linkName = ((ServerEndpointMetaData)aux).getLinkName();
            if (epName.equals(linkName))
            {
               epMetaData = (ServerEndpointMetaData)aux;
               break;
            }
         }
      }

      if (epMetaData == null)
         throw new IllegalStateException("Cannot find endpoint meta data for: " + epName);

      return epMetaData;
   }
}
