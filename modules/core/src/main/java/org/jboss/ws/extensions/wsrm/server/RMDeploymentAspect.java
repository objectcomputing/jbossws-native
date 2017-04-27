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
package org.jboss.ws.extensions.wsrm.server;

import org.jboss.ws.extensions.wsrm.common.RMHelper;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.deployment.DeploymentAspect;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.invocation.InvocationHandler;

/**
 * Registers RMInvocationHandler if WS-RM is detected
 *
 * @author richard.opalka@jboss.com
 *
 * @since Dec 11, 2007
 */
public final class RMDeploymentAspect extends DeploymentAspect
{

   @Override
   public final void start(Deployment dep)
   {
      for (Endpoint ep : dep.getService().getEndpoints())
      {
         ServerEndpointMetaData sepMetaData = ep.getAttachment(ServerEndpointMetaData.class);
         if (sepMetaData == null)
            throw new IllegalStateException("Cannot obtain endpoint meta data");
         
         if (sepMetaData.getConfig().getRMMetaData() != null)
         {
            InvocationHandler origInvHandler = ep.getInvocationHandler();
            InvocationHandler wsrmInvHandler = new RMInvocationHandler(origInvHandler, (ArchiveDeployment)dep);
            ep.setInvocationHandler(wsrmInvHandler);
            RMHelper.setupRMOperations(sepMetaData);
            log.info("WS-RM invocation handler associated with endpoint " + ep.getAddress());
         }
      }
   }
   
   @Override
   public final void destroy(Deployment dep)
   {
      for (Endpoint ep : dep.getService().getEndpoints())
      {
         InvocationHandler invHandler = ep.getInvocationHandler();
         if (invHandler instanceof RMInvocationHandler)
         {
            RMInvocationHandler rmInvHandler = (RMInvocationHandler)invHandler;
            ep.setInvocationHandler(rmInvHandler.getDelegate());
            log.info("WS-RM invocation handler removed for endpoint " + ep.getAddress());
         }
      }
   }

}
