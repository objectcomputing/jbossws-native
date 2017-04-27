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
package org.jboss.ws.extensions.wsrm.transport;

/**
 * RM transport constants
 *
 * @author richard.opalka@jboss.com
 *
 * @since Dec 14, 2007
 */
public class RMChannelConstants
{

   public static final String TARGET_ADDRESS = "targetAddress";
   public static final String REMOTING_VERSION = "remotingVersion";
   public static final String INVOCATION_CONTEXT = "invocationContext";
   public static final String MARSHALLER = "marshaller";
   public static final String UNMARSHALLER = "unmarshaller";
   public static final String SERIALIZATION_CONTEXT = "serializationContext";
   public static final String REMOTING_INVOCATION_CONTEXT = "remotingInvocationContext";
   public static final String REMOTING_CONFIGURATION_CONTEXT = "remotingConfigurationContext";

   private RMChannelConstants()
   {
      // instances not allowed
   }
   
}
