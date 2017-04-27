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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.remoting.InvocationRequest;
import org.jboss.ws.core.MessageTrace;
import org.jboss.ws.extensions.wsrm.transport.RMMessage;
import org.jboss.ws.extensions.wsrm.transport.RMUnassignedMessageListener;

/**
 * Reliable messaging callback handler listens for incomming requests on specified path
 *
 * @author richard.opalka@jboss.com
 *
 * @since Nov 21, 2007
 */
public final class RMCallbackHandlerImpl implements RMCallbackHandler
{
   private static final Logger logger = Logger.getLogger(RMCallbackHandlerImpl.class);
   private final String handledPath;
   private final Object instanceLock = new Object();
   private Map<String, RMMessage> arrivedMessages = new HashMap<String, RMMessage>();
   private List<RMMessage> arrivedUnassignedMessages = new LinkedList<RMMessage>();
   private RMUnassignedMessageListener listener;
   
   /**
    * Request path to listen for incomming messages
    * @param handledPath
    */
   public RMCallbackHandlerImpl(String handledPath)
   {
      super();
      this.handledPath = handledPath;
      logger.debug("Registered callback handler listening on '" + handledPath + "' request path");
   }
   
   public Throwable getFault(String messageId)
   {
      // TODO implement
      return null;
   }

   public final String getHandledPath()
   {
      return this.handledPath;
   }

   public final void handle(InvocationRequest request)
   {
      RMMessage message = (RMMessage)request.getParameter();
      synchronized (instanceLock)
      {
         String requestMessage = new String(message.getPayload());
         MessageTrace.traceMessage("Incoming RM Response Message (callback)", requestMessage);
         String startPattern = "<wsa:RelatesTo>"; // TODO: remove this with XML content inspection
         String endPattern = "</wsa:RelatesTo>";
         int begin = requestMessage.indexOf(startPattern) + startPattern.length();
         int end = requestMessage.indexOf(endPattern); 
         if (begin != -1)
         {
            String messageId = requestMessage.substring(begin, end);
            logger.debug("Arrived message id: " + messageId);
            this.arrivedMessages.put(messageId, message); 
         }
         else
         {
            logger.debug("Arrived message has no id");
            this.arrivedUnassignedMessages.add(message);
            if (this.listener != null)
            {
               this.listener.unassignedMessageReceived();
            }
         }
      }
   }
   
   public void addUnassignedMessageListener(RMUnassignedMessageListener listener)
   {
      synchronized (instanceLock)
      {
         if (this.listener == null)
         {
            this.listener = listener;
         }
      }
   }

   public RMMessage getMessage(String messageId)
   {
      synchronized (instanceLock)
      {
         while (this.arrivedMessages.get(messageId) == null)
         {
            try
            {
               logger.debug("waiting for response with message id: " + messageId);
               instanceLock.wait(100);
            }
            catch (InterruptedException ie)
            {
               logger.warn(ie.getMessage(), ie);
            }
         }
         return this.arrivedMessages.get(messageId);
      }
   }
   
}
