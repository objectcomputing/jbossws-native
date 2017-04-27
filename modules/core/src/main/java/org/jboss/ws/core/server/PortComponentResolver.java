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
package org.jboss.ws.core.server;

import org.jboss.logging.Logger;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.management.EndpointResolver;

import java.util.Iterator;

/**
 * @author Heiko.Braun@jboss.com
 *         Created: Jul 23, 2007
 */
public class PortComponentResolver implements EndpointResolver
{
   private static final Logger log = Logger.getLogger(PortComponentResolver.class);

   private String pcLink;
   private Endpoint result;

   public PortComponentResolver(String pcref)
   {
      this.pcLink = pcref;
   }

   public Endpoint query(Iterator<Endpoint> endpoints)
   {
      Endpoint endpoint = null;

      String pcName = this.pcLink;
      int hashIndex = this.pcLink.indexOf("#");
      if (hashIndex > 0)
      {
         pcName = pcLink.substring(hashIndex + 1);
      }

      while(endpoints.hasNext())
      {
         Endpoint auxEndpoint = endpoints.next();
         ServerEndpointMetaData sepMetaData = auxEndpoint.getAttachment(ServerEndpointMetaData.class);
         if (pcName.equals(sepMetaData.getPortComponentName()))
         {
            if (endpoint != null)
            {
               log.warn("Multiple service endoints found for: " + pcLink);
               endpoint = null;
               break;
            }
            endpoint = auxEndpoint;
         }

      }

      return endpoint;
   }

}
