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

import java.util.Map;
import java.util.HashMap;
import org.jboss.remoting.marshal.Marshaller;
import org.jboss.remoting.marshal.UnMarshaller;

/**
 * RM metadata heavily used by this RM transport
 *
 * @author richard.opalka@jboss.com
 */
public final class RMMetadata
{
   private Map<String, Map<String, Object>> contexts = new HashMap<String, Map<String, Object>>();
   
   public RMMetadata(
         String remotingVersion,
         String targetAddress,
         Marshaller marshaller,
         UnMarshaller unmarshaller,
         Map<String, Object> invocationContext,
         Map<String, Object> remotingInvocationContext,
         Map<String, Object> remotingConfigurationContext)
   {
      if (targetAddress == null)
         throw new IllegalArgumentException("Target address cannot be null");
      
      invocationContext.put(RMChannelConstants.TARGET_ADDRESS, targetAddress);
      invocationContext.put(RMChannelConstants.REMOTING_VERSION, remotingVersion);
      setContext(RMChannelConstants.INVOCATION_CONTEXT, invocationContext);
      
      if (marshaller == null || unmarshaller == null)
         throw new IllegalArgumentException("Unable to create de/serialization context");
      
      Map<String, Object> serializationContext = new HashMap<String, Object>();
      serializationContext.put(RMChannelConstants.MARSHALLER, marshaller);
      serializationContext.put(RMChannelConstants.UNMARSHALLER, unmarshaller);
      setContext(RMChannelConstants.SERIALIZATION_CONTEXT, serializationContext);
         
      if (remotingInvocationContext == null)
         throw new IllegalArgumentException("Remoting invocation context cannot be null");
      
      setContext(RMChannelConstants.REMOTING_INVOCATION_CONTEXT, remotingInvocationContext);

      if (remotingConfigurationContext == null)
         throw new IllegalArgumentException("Remoting configuraton context cannot be null");

      setContext(RMChannelConstants.REMOTING_CONFIGURATION_CONTEXT, remotingConfigurationContext);
   }
   
   public RMMetadata(Map<String, Object> remotingInvocationContext)
   {
      if (remotingInvocationContext == null)
         throw new IllegalArgumentException("Remoting invocation context cannot be null");
      
      setContext(RMChannelConstants.REMOTING_INVOCATION_CONTEXT, remotingInvocationContext);
   }
   
   void setContext(String key, Map<String, Object> ctx)
   {
      this.contexts.put(key, ctx);
   }
   
   Map<String, Object> getContext(String key)
   {
      return this.contexts.get(key);
   }
   
}
