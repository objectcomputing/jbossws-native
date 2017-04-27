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

import org.jboss.ws.core.server.ServiceEndpointInvoker;
import org.jboss.ws.core.server.ServiceEndpointInvokerEJB21;
import org.jboss.wsf.spi.deployment.DeploymentAspect;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.deployment.Deployment.DeploymentType;

/**
 * A deployer that associates the ServiceEndpointInvoker with the endpoint 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 25-Apr-2007
 */
public class ServiceEndpointInvokerDeploymentAspect extends DeploymentAspect
{
   @Override
   public void start(Deployment dep)
   {
      for (Endpoint ep : dep.getService().getEndpoints())
      {
         ServiceEndpointInvoker epInvoker = ep.getAttachment(ServiceEndpointInvoker.class);
         if (epInvoker == null)
         {
            DeploymentType depType = ep.getService().getDeployment().getType();
            if (depType == DeploymentType.JAXRPC_EJB21)
            {
               epInvoker = new ServiceEndpointInvokerEJB21();
            }
            else
            {
               epInvoker = new ServiceEndpointInvoker();
            }
            ep.addAttachment(ServiceEndpointInvoker.class, epInvoker);
            epInvoker.init(ep);
         }
      }
   }
}
