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
package org.jboss.ws.core.client;

import org.jboss.ws.core.jaxrpc.client.NativeServiceRefBinderJAXRPC;
import org.jboss.ws.core.jaxws.client.NativeServiceRefBinderJAXWS;
import org.jboss.wsf.spi.serviceref.ServiceRefBinder;
import org.jboss.wsf.spi.serviceref.ServiceRefBinderFactory;
import org.jboss.wsf.spi.serviceref.ServiceRefHandler.Type;

/**
 * Binds a JAXWS Service object in the client's ENC
 *
 * @author Thomas.Diesler@jboss.org
 * @since 17-Jan-2007
 */
public class ServiceRefBinderFactoryImpl implements ServiceRefBinderFactory
{
   public ServiceRefBinder newServiceRefBinder(Type type)
   {
      return (type == Type.JAXRPC ? new NativeServiceRefBinderJAXRPC() : new NativeServiceRefBinderJAXWS());
   }
}
