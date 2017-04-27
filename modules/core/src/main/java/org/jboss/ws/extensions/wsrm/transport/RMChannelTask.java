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

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Callable;

import org.jboss.logging.Logger;
import org.jboss.remoting.CannotConnectException;
import org.jboss.remoting.Client;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.marshal.MarshalFactory;
import org.jboss.ws.core.MessageTrace;
import org.jboss.ws.extensions.wsrm.RMClientSequence;
import org.jboss.ws.extensions.wsrm.transport.backchannel.RMCallbackHandler;
import org.jboss.ws.extensions.wsrm.transport.backchannel.RMCallbackHandlerFactory;

/**
 * RM channel task to be executed
 * 
 * @author richard.opalka@jboss.com
 */
final class RMChannelTask implements Callable<RMChannelResponse>
{
   private static final Logger logger = Logger.getLogger(RMChannelTask.class);
   private static final String JBOSSWS_SUBSYSTEM = "jbossws-wsrm";
   private final RMMessage rmRequest;
   
   RMChannelTask(RMMessage rmRequest)
   {
      super();
      this.rmRequest = rmRequest;
   }
   
   public RMChannelResponse call()
   {
      try
      {
         String targetAddress = (String)rmRequest.getMetadata().getContext(RMChannelConstants.INVOCATION_CONTEXT).get(RMChannelConstants.TARGET_ADDRESS);
         String version = (String)rmRequest.getMetadata().getContext(RMChannelConstants.INVOCATION_CONTEXT).get(RMChannelConstants.REMOTING_VERSION);

         if (version.startsWith("1.4"))
         {
            MarshalFactory.addMarshaller("JBossWSMessage", RMMarshaller.getInstance(), RMUnMarshaller.getInstance());
         }

         InvokerLocator locator = new InvokerLocator(targetAddress);
         URI backPort = RMTransportHelper.getBackPortURI(rmRequest);
         String messageId = RMTransportHelper.getAddressingMessageId(rmRequest);
         
         logger.debug("[WS-RM] backport URI is: " + backPort);
         RMCallbackHandler callbackHandler = null;

         if (backPort != null)
         {
            callbackHandler = RMCallbackHandlerFactory.getCallbackHandler(backPort);
            RMClientSequence sequence = RMTransportHelper.getSequence(rmRequest);
            if (sequence != null)
            {
               callbackHandler.addUnassignedMessageListener(sequence);
            }
         }
         boolean oneWay = RMTransportHelper.isOneWayOperation(rmRequest);
         
         Client client = new Client(locator, JBOSSWS_SUBSYSTEM, rmRequest.getMetadata().getContext(RMChannelConstants.REMOTING_CONFIGURATION_CONTEXT));
         client.connect();

         client.setMarshaller(RMMarshaller.getInstance());

         if ((false == oneWay) && (null == backPort))  
            client.setUnMarshaller(RMUnMarshaller.getInstance());
      
         Map<String, Object> remotingInvocationContext = rmRequest.getMetadata().getContext(RMChannelConstants.REMOTING_INVOCATION_CONTEXT);

         // debug the outgoing request message
         MessageTrace.traceMessage("Outgoing RM Request Message", rmRequest.getPayload());
 
         RMMessage rmResponse = null;
         if (oneWay && (null == backPort))
         {
            client.invokeOneway(rmRequest.getPayload(), remotingInvocationContext, false);
         }
         else
         {
            Object retVal = null;
            try
            {
               retVal = client.invoke(rmRequest.getPayload(), remotingInvocationContext);
            }
            catch (CannotConnectException cce)
            {
               // remoting hack - ignore NullPointerException cause
               if (false == (cce.getCause() instanceof NullPointerException))
               {
                  throw cce;
               }
            }
            if ((null != retVal) && (false == (retVal instanceof RMMessage)))
            {
               String msg = retVal.getClass().getName() + ": '" + retVal + "'";
               logger.warn(msg);
               throw new IOException(msg);
            }
            rmResponse = (RMMessage)retVal;
         }

         // Disconnect the remoting client
         client.disconnect();

         // trace the incomming response message
         if ((rmResponse != null) && (backPort == null))
            MessageTrace.traceMessage("Incoming RM Response Message", rmResponse.getPayload());
         
         if (backPort != null) // TODO: backport support
         {
            if ((null != messageId) && (false == RMTransportHelper.isOneWayOperation(rmRequest)))
            {
               // register callbacks only for outbound messages with messageId
               return new RMChannelResponse(callbackHandler, messageId);
            }
         }

         return new RMChannelResponse(rmResponse);
      }
      catch (Throwable t)
      {
         return new RMChannelResponse(t);
      }
   }
}
