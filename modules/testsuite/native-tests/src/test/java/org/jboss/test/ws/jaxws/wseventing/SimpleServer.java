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
package org.jboss.test.ws.jaxws.wseventing;

import java.util.HashMap;

import javax.management.MBeanServer;

import org.jboss.remoting.InvocationRequest;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.ServerInvocationHandler;
import org.jboss.remoting.ServerInvoker;
import org.jboss.remoting.callback.InvokerCallbackHandler;
import org.jboss.remoting.transport.Connector;

/**
 * @author Heiko.Braun@jboss.org
 * @since 19.01.2007
 */
public class SimpleServer implements Runnable {

   private String locatorURI;
   private boolean running = true;

   public SimpleServer(String locatorURI) {
      this.locatorURI = locatorURI;
   }

   public static void main(String[] args) throws Exception
   {
      SimpleServer server = new SimpleServer("socket://localhost:20000");
      Thread t = new Thread(server);
      t.start();

   }

   public void run() {
      try {
         String params = "/?clientLeasePeriod=10000";
         locatorURI += params;
         InvokerLocator locator = new InvokerLocator(locatorURI);
         HashMap config = new HashMap();
         //config.put(ServerInvoker.TIMEOUT, 120000);
         //config.put(ServerInvoker.SERVER_SOCKET_FACTORY, new MyServerSocketFactory());
         Connector connector = new Connector(locator, config);
         connector.create();

         connector.addInvocationHandler("eventing", new DebugHandler());
         connector.start();

         while(running)
         {
            Thread.currentThread().sleep(2000);
            System.out.println(".");
         }

      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   class DebugHandler implements ServerInvocationHandler
   {
      public void setMBeanServer(MBeanServer mBeanServer) {

      }

      public void setInvoker(ServerInvoker serverInvoker) {

      }

      public Object invoke(InvocationRequest invocationRequest) throws Throwable {
         System.out.println("Invocation on "+invocationRequest.getSubsystem());
         System.out.println(invocationRequest.getRequestPayload());
         return null;
      }

      public void addListener(InvokerCallbackHandler invokerCallbackHandler) {
         System.out.println("addListener: "+invokerCallbackHandler);
      }

      public void removeListener(InvokerCallbackHandler invokerCallbackHandler) {
         System.out.println("removeListener: "+invokerCallbackHandler);
      }
   }

}
