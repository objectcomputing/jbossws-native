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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.jboss.remoting.transport.http.HTTPMetadataConstants;
import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.config.RMMessageRetransmissionConfig;

/**
 * RM sender ensures reliable message delivery. The QoS is specified in the JAX-WS client Sconfiguration file.
 *
 * @author richard.opalka@jboss.com
 */
public final class RMSender
{
   
   private static final Logger logger = Logger.getLogger(RMSender.class);
   private static RMSender instance = new RMSender();
   private static final ThreadFactory rmThreadPool = new RMThreadPoolFactory();
   private static final int maxCountOfThreads = 5;
   private static final ExecutorService rmTasksQueue = Executors.newFixedThreadPool(maxCountOfThreads, rmThreadPool);
   
   /**
    * Generates worker threads (daemons)
    */
   private static final class RMThreadPoolFactory implements ThreadFactory
   {
      final ThreadGroup group;
      final AtomicInteger threadNumber = new AtomicInteger(1);
      final String namePrefix = "rm-pool-worker-thread-";
    
      private RMThreadPoolFactory()
      {
         SecurityManager sm = System.getSecurityManager();
         group = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
      }
      
      public Thread newThread(Runnable r)
      {
         Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
         if (false == t.isDaemon())
            t.setDaemon(true);
         if (Thread.NORM_PRIORITY != t.getPriority())
            t.setPriority(Thread.NORM_PRIORITY);
         return t;
      }
   }

   private RMSender()
   {
      // forbidden inheritance
   }
   
   public static final RMSender getInstance()
   {
      return instance;
   }

   public final RMMessage send(RMMessage request) throws Throwable
   {
      RMMessageRetransmissionConfig qos = RMTransportHelper.getSequence(request).getRMConfig().getMessageRetransmission();
      if (qos == null)
         throw new RMException("User must specify message retransmission configuration in JAX-WS WS-RM config");
      
      int countOfAttempts = qos.getCountOfAttempts();
      int inactivityTimeout = qos.getMessageTimeout();
      int retransmissionInterval = qos.getRetransmissionInterval();
      RMChannelResponse result = null;
      long startTime = 0L;
      long endTime = 0L;
      int attemptNumber = 1;
      
      for (int i = 0; i < countOfAttempts; i++)
      {
         logger.debug("Sending RM request - attempt no. " + attemptNumber++);
         Future<RMChannelResponse> futureResult = rmTasksQueue.submit(new RMChannelTask(request));
         try 
         {
            startTime = System.currentTimeMillis();
            result = futureResult.get(inactivityTimeout, TimeUnit.SECONDS);
            if (result != null)
            {
               Throwable t = result.getFault();
               if (t != null)
               {
                  logger.warn(result.getFault().getClass().getName(), result.getFault());
               }
               else
               {
                  endTime = System.currentTimeMillis();
                  if (result.getResponse() != null)
                  {
                     Map<String, Object> remotingCtx = result.getResponse().getMetadata().getContext(RMChannelConstants.REMOTING_INVOCATION_CONTEXT);
                     if (remotingCtx != null)
                     {
                        if (Integer.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).equals(remotingCtx.get(HTTPMetadataConstants.RESPONSE_CODE)))
                        {
                           logger.debug("Response message received in " + (endTime - startTime) + " miliseconds, but contains internal server code, going to resend the request message");
                           continue;
                        }
                     }
                  }
                  logger.debug("Response message received in " + (endTime - startTime) + " miliseconds");
                  break;
               }
               try
               {
                  Thread.sleep(retransmissionInterval * 1000);
               }
               catch (InterruptedException ie)
               {
                  logger.warn(ie.getMessage(), ie);
               }
            }
         }
         catch (TimeoutException te)
         {
            endTime = System.currentTimeMillis();
            logger.warn("Timeout - response message not received in " + (endTime - startTime) + " miliseconds");
            try
            {
               Thread.sleep(retransmissionInterval * 1000);
            }
            catch (InterruptedException ie)
            {
               logger.warn(ie.getMessage(), ie);
            }
         }
      }

      if (result == null)
         throw new RMException("Unable to deliver message with addressing id: " + RMTransportHelper.getAddressingMessageId(request) + ". Count of attempts to deliver the message was: " + countOfAttempts);
      
      Throwable fault = result.getFault();
      if (fault != null)
      {
         throw new RMException("Unable to deliver message with addressing id: " + RMTransportHelper.getAddressingMessageId(request) + ". Count of attempts to deliver the message was: " + countOfAttempts, fault);
      }
      else
      {
         return result.getResponse();
      }
   }
   
}
