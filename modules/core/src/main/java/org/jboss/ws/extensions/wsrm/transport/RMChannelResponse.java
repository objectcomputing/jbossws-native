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

import org.jboss.ws.extensions.wsrm.transport.backchannel.RMCallbackHandler;

/**
 * RM channel response represents response that goes from the RM channel
 *
 * @author richard.opalka@jboss.com
 */
final class RMChannelResponse
{
   private final Throwable fault;
   private final RMMessage result;
   private final RMCallbackHandler callback;
   private final String messageId; // WS-Addressing: MessageID
   
   public RMChannelResponse(RMCallbackHandler callback, String messageId)
   {
      this(null, null, callback, messageId);
   }
   
   public RMChannelResponse(Throwable fault)
   {
      this(null, fault, null, null);
   }
   
   public RMChannelResponse(RMMessage result)
   {
      this(result, null, null, null);
   }
   
   private RMChannelResponse(RMMessage result, Throwable fault, RMCallbackHandler callback, String messageId)
   {
      super();
      this.result = result;
      this.fault = fault;
      this.callback = callback;
      this.messageId = messageId;
   }
   
   public Throwable getFault()
   {
      return (this.callback != null) ? this.callback.getFault(this.messageId) : this.fault;
   }
   
   public RMMessage getResponse()
   {
      return (this.callback != null) ? this.callback.getMessage(this.messageId) : this.result;
   }
}
