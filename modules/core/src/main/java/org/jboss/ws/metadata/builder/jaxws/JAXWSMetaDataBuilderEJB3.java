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

import java.util.Iterator;

import javax.jws.WebService;
import javax.xml.ws.WebServiceProvider;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.metadata.j2ee.EJBArchiveMetaData;
import org.jboss.wsf.spi.metadata.j2ee.EJBMetaData;

/**
 * A server side meta data builder that is based on JSR-181 annotations
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 19-May-2005
 */
public class JAXWSMetaDataBuilderEJB3
{
   // provide logging
   private final Logger log = Logger.getLogger(JAXWSMetaDataBuilderEJB3.class);

   protected Class annotatedClass;

   /** Build from webservices.xml
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
            throw new IllegalArgumentException("Runtime loader cannot be null");
         wsMetaData.setClassLoader(runtimeClassLoader);

         // The container objects below provide access to all of the ejb metadata
         EJBArchiveMetaData apMetaData = dep.getAttachment(EJBArchiveMetaData.class);
         Iterator<EJBMetaData> it = apMetaData.getEnterpriseBeans();
         while (it.hasNext())
         {
            EJBMetaData beanMetaData = it.next();
            String ejbClassName = beanMetaData.getEjbClass();
            Class<?> beanClass = wsMetaData.getClassLoader().loadClass(ejbClassName);
            if (beanClass.isAnnotationPresent(WebService.class) || beanClass.isAnnotationPresent(WebServiceProvider.class))
            {
               String ejbLink = beanMetaData.getEjbName();
               JAXWSServerMetaDataBuilder.setupProviderOrWebService(dep, wsMetaData, beanClass, ejbLink);

               /* Resolve dependency on @SecurityDomain
                * http://jira.jboss.org/jira/browse/JBWS-2107
               if (beanClass.isAnnotationPresent(SecurityDomain.class))
               {
                  SecurityDomain anSecurityDomain = (SecurityDomain)beanClass.getAnnotation(SecurityDomain.class);
                  String lastDomain = wsMetaData.getSecurityDomain();
                  String securityDomain = anSecurityDomain.value();
                  if (lastDomain != null && lastDomain.equals(securityDomain) == false)
                     throw new IllegalStateException("Multiple security domains not supported: " + securityDomain);

                  wsMetaData.setSecurityDomain(securityDomain);
               }
               */
            }
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
