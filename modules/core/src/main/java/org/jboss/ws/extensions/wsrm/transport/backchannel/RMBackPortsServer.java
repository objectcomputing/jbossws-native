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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.logging.Logger;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.transport.Connector;
import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.transport.RMMarshaller;
import org.jboss.ws.extensions.wsrm.transport.RMUnMarshaller;

/**
 * Back ports server used by addressable clients
 *
 * @author richard.opalka@jboss.com
 *
 * @since Nov 20, 2007
 */
public final class RMBackPortsServer implements Runnable
{
   private static final Logger LOG = Logger.getLogger(RMBackPortsServer.class);
   private static final Lock CLASS_LOCK = new ReentrantLock();
   private static final long WAIT_PERIOD = 100;
   private static RMBackPortsServer INSTANCE;

   private final Object instanceLock = new Object();
   private final Connector connector;
   private final String scheme;
   private final String host;
   private final int port;
   private RMBackPortsInvocationHandler handler;
   private boolean started;
   private boolean stopped;
   private boolean terminated;
   
   public final void registerCallback(RMCallbackHandler callbackHandler)
   {
      this.handler.registerCallback(callbackHandler);
   }
   
   public final void unregisterCallback(RMCallbackHandler callbackHandler)
   {
      this.handler.unregisterCallback(callbackHandler);
   }
   
   public final RMCallbackHandler getCallback(String requestPath)
   {
      return this.handler.getCallback(requestPath);
   }
   
   private RMBackPortsServer(String scheme, String host, int port)
   throws RMException
   {
      super();
      this.scheme = scheme;
      this.host = host;
      this.port = port;
      try
      {
         // we have to use custom unmarshaller because default one removes CRNLs
         String customUnmarshaller = "/?unmarshaller=" + RMUnMarshaller.class.getName();
         InvokerLocator il = new InvokerLocator(this.scheme + "://" + this.host + ":" + this.port + customUnmarshaller);
         this.connector = new Connector();
         this.connector.setInvokerLocator(il.getLocatorURI());
         this.connector.create();
   
         this.handler = new RMBackPortsInvocationHandler();
         this.connector.addInvocationHandler("wsrmBackPortsHandler", this.handler);
         this.connector.start();
         LOG.debug("WS-RM Backports Server started on: " + il.getLocatorURI());
      }
      catch (Exception e)
      {
         LOG.warn(e.getMessage(), e);
         throw new RMException(e.getMessage(), e);
      }
   }
   
   public final String getScheme()
   {
      return this.scheme;
   }
   
   public final String getHost()
   {
      return this.host;
   }
   
   public final int getPort()
   {
      return this.port;
   }
   
   public final void run()
   {
      synchronized (this.instanceLock)
      {
         if (this.started)
            return;
         
         this.started = true;
         
         while (this.stopped == false)
         {
            try
            {
               this.instanceLock.wait(WAIT_PERIOD);
               LOG.debug("serving requests");
            }
            catch (InterruptedException ie)
            {
               LOG.warn(ie.getMessage(), ie);
            }
         }
         try
         {
            connector.stop();
         }
         finally
         {
            LOG.debug("terminated");
            this.terminated = true;
         }
      }
   }
   
   public final void terminate()
   {
      synchronized (this.instanceLock)
      {
         if (this.stopped == true)
            return;
         
         this.stopped = true;
         LOG.debug("termination forced");
         while (this.terminated == false)
         {
            try
            {
               LOG.debug("waiting for termination");
               this.instanceLock.wait(WAIT_PERIOD);
            }
            catch (InterruptedException ie)
            {
               LOG.warn(ie.getMessage(), ie);
            }
         }
      }
   }
   
   /**
    * Starts back ports server on the background if method is called for the first time
    * @param scheme protocol
    * @param host hostname
    * @param port port
    * @return WS-RM back ports server
    * @throws RMException
    */
   public static RMBackPortsServer getInstance(String scheme, String host, int port)
   throws RMException
   {
      CLASS_LOCK.lock();
      try
      {
         if (INSTANCE == null)
         {
            INSTANCE = new RMBackPortsServer(scheme, host, (port == -1) ? 80 : port);
            // forking back ports server
            Thread t  = new Thread(INSTANCE, "RMBackPortsServer");
            t.setDaemon(true);
            t.start();
            // registering shutdown hook
            final RMBackPortsServer server = INSTANCE;
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
               public void run()
               {
                  server.terminate();
               }
            }, "RMBackPortsServerShutdownHook"));
         }
         else
         {
            boolean schemeEquals = INSTANCE.getScheme().equals(scheme);
            boolean hostEquals = INSTANCE.getHost().equals(host);
            boolean portEquals = INSTANCE.getPort() == ((port == -1) ? 80 : port);
            if ((schemeEquals == false) || (hostEquals == false) || (portEquals == false))
               throw new IllegalArgumentException();
         }
         return INSTANCE;
      }
      finally
      {
         CLASS_LOCK.unlock();
      }
   }
   
}
