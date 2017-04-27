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

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.deployment.Endpoint;

/**
 * A server side meta data builder that is based on JSR-181 annotations
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 23-Jul-2005
 */
public class JAXWSMetaDataBuilderJSE
{
   // provide logging
   private final Logger log = Logger.getLogger(JAXWSMetaDataBuilderJSE.class);

   /** Build from annotations
    */
   public UnifiedMetaData buildMetaData(ArchiveDeployment dep)
   {
      log.debug("START buildMetaData: [name=" + dep.getCanonicalName() + "]");
      try
      {
         UnifiedMetaData wsMetaData = new UnifiedMetaData(dep.getRootFile());
         wsMetaData.setDeploymentName(dep.getCanonicalName());
         ClassLoader runtimeClassLoader = dep.getRuntimeClassLoader();
         if(null == runtimeClassLoader)
            throw new IllegalArgumentException("Runtime classloader cannot be null");
         wsMetaData.setClassLoader(runtimeClassLoader);

         // For every bean
         for (Endpoint ep : dep.getService().getEndpoints())
         {
            String shortName = ep.getShortName();
            Class beanClass = ep.getTargetBeanClass();
            JAXWSServerMetaDataBuilder.setupProviderOrWebService(dep, wsMetaData, beanClass, shortName);
         }
         
         log.debug("END buildMetaData: " + wsMetaData);
         return wsMetaData;
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         throw new WSException("Cannot build meta data: " + ex.getMessage(), ex);
      }
   }
}
