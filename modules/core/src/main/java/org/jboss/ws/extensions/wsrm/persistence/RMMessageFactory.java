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
package org.jboss.ws.extensions.wsrm.persistence;

import org.jboss.util.NotImplementedException;

/**
 * Factory for creating storable message wrappers
 *
 * @author richard.opalka@jboss.com
 */
public final class RMMessageFactory
{

   private static final RMMessageFactory instance = new RMMessageFactory();
   
   private RMMessageFactory()
   {
      // forbidden inheritance
   }
   
   /**
    * Gets factory instance
    * @return factory instance
    */
   public static final RMMessageFactory getInstance()
   {
      return instance;
   }
   
   /**
    * Constructs new storable message wrapper
    * @param messageId specified in either <b>MessageID</b> or <b>RelatesTo</b> addressing header element
    * @param data bytes of SOAP payload without WS-Security and WS-RM elements present
    * @return storable message wrapper
    */
   public final RMMessage newMessage(String messageId, byte[] data)
   {
      throw new NotImplementedException();
   }
   
}
