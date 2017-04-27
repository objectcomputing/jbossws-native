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
package org.jboss.test.ws.jaxws.wseventing;

import javax.jws.WebService;

import org.jboss.logging.Logger;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.ws.extensions.eventing.jaxws.AbstractEventSourceEndpoint;

/**
 * @author Heiko.Braun@jboss.org
 * @since 16.01.2007
 */
@WebService(
   name = "EventSource",
   portName = "EventSourcePort",
   targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing",
   wsdlLocation = "/WEB-INF/wsdl/windreport.wsdl",
   endpointInterface = "org.jboss.ws.extensions.eventing.jaxws.EventSourceEndpoint")
@EndpointConfig(configName = "Standard WSAddressing Endpoint")
public class CustomEventSource extends AbstractEventSourceEndpoint {

   private static final Logger log = Logger.getLogger(CustomEventSource.class);

   protected Logger getLogger()
   {
      return log;
   }


}
