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
package org.jboss.ws.extensions.wsrm.transport.backchannel;

import java.net.URI;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Callback factory
 *
 * @author richard.opalka@jboss.com
 *
 * @since Nov 21, 2007
 */
public final class RMCallbackHandlerFactory
{
   private static RMBackPortsServer server;
   private static Lock lock = new ReentrantLock();
   
   private RMCallbackHandlerFactory()
   {
      // no instances
   }
   
   public static RMCallbackHandler getCallbackHandler(URI backPort)
   {
      lock.lock();
      try
      {
         if (server == null)
         {
            server = RMBackPortsServer.getInstance(backPort.getScheme(), backPort.getHost(), backPort.getPort());
         }
         RMCallbackHandler callbackHandler = server.getCallback(backPort.getPath());
         if (callbackHandler == null)
         {
            callbackHandler = new RMCallbackHandlerImpl(backPort.getPath());
            server.registerCallback(callbackHandler);
         }

         return callbackHandler;
      }
      finally
      {
         lock.unlock();
      }
   }
}
