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
package org.jboss.test.ws.jaxws.handlerlifecycle;

import java.util.Vector;

import javax.xml.ws.handler.Handler;

import org.jboss.logging.Logger;

/**
 * Tracks handler method invocations
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Mar-2007
 */
public final class HandlerTracker
{
   private static Logger log = Logger.getLogger(HandlerTracker.class);
   
   private static Vector<String> messages = new Vector<String>();

   public static void reportHandlePostConstruct(Handler handler)
   {
      String msg = handler + ":PostConstruct";
      log.debug(msg + " to vector: " + System.identityHashCode(messages));
      messages.add(msg);
   }

   public static void reportHandlePreDestroy(Handler handler)
   {
      String msg = handler + ":PreDestroy";
      log.debug(msg + " to vector " + System.identityHashCode(messages));
      messages.add(msg);
   }

   public static void reportHandleMessage(Handler handler, String direction)
   {
      String msg = handler + ":Message:" + direction;
      log.debug(msg + " to vector: " + System.identityHashCode(messages));
      messages.add(msg);
   }

   public static void reportHandleFault(Handler handler, String direction)
   {
      String msg = handler + ":Fault:" + direction;
      log.debug(msg + " to vector: " + System.identityHashCode(messages));
      messages.add(msg);
   }

   public static void reportHandleClose(Handler handler)
   {
      String msg = handler + ":Close";
      log.debug(msg + " to vector: " + System.identityHashCode(messages));
      messages.add(msg);
   }

   public static String getListMessages()
   {
      log.debug("getListMessages from vector: " + System.identityHashCode(messages));
      log.debug(messages.toString());
      return messages.toString();
   }

   public static void clearListMessages()
   {
      log.debug("clearListMessages from vector: " + System.identityHashCode(messages));
      messages.clear();
   }
}
