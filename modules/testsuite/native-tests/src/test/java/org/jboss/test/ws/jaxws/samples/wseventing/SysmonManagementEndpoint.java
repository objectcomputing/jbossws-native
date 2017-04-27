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
package org.jboss.test.ws.jaxws.samples.wseventing;

import javax.jws.WebService;

import org.jboss.logging.Logger;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.ws.extensions.eventing.jaxws.AbstractSubscriptionManagerEndpoint;

/**
 * @author Heiko.Braun@jboss.org
 * @since 18.01.2007
 */
@WebService(
   name = "SubscriptionManager",
   portName = "SubscriptionManagerPort",
   targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing",
   wsdlLocation = "/WEB-INF/wsdl/sysmon.wsdl",
   endpointInterface = "org.jboss.ws.extensions.eventing.jaxws.SubscriptionManagerEndpoint")
@EndpointConfig(configName = "Standard WSAddressing Endpoint")
public class SysmonManagementEndpoint extends AbstractSubscriptionManagerEndpoint {

   private static final Logger log = Logger.getLogger(SysmonManagementEndpoint.class);

   protected Logger getLogger() {
      return log;
   }
}
