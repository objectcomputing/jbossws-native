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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.logging.Logger;

public abstract class LifecycleHandler implements SOAPHandler
{
   private static Logger log = Logger.getLogger(LifecycleHandler.class);

   public LifecycleHandler()
   {
      log.debug("new " + this);
   }

   public Set getHeaders()
   {
      return new HashSet();
   }

   @PostConstruct
   public void postConstruct()
   {
      HandlerTracker.reportHandlePostConstruct(this);
   }

   @PreDestroy
   public void preDestroy()
   {
      HandlerTracker.reportHandlePreDestroy(this);
   }

   public boolean handleMessage(MessageContext msgContext)
   {
      boolean doNext = isOutbound(msgContext) ? handleOutboundMessageInternal(msgContext) : handleInboundMessageInternal(msgContext);
      return doNext;
   }

   private boolean handleOutboundMessageInternal(MessageContext msgContext)
   {
      String trackerMessage = "OutBound";

      boolean doNext;
      try
      {
         doNext = handleOutboundMessage(msgContext);
         if (doNext == false)
            trackerMessage += ":false";

         HandlerTracker.reportHandleMessage(this, trackerMessage);
      }
      catch (RuntimeException rte)
      {
         HandlerTracker.reportHandleMessage(this, trackerMessage + ":" + rte.getMessage());
         throw rte;
      }
      return doNext;
   }

   private boolean handleInboundMessageInternal(MessageContext msgContext)
   {
      String trackerMessage = "InBound";

      boolean doNext;
      try
      {
         doNext = handleInboundMessage(msgContext);
         if (doNext == false)
            trackerMessage += ":false";

         HandlerTracker.reportHandleMessage(this, trackerMessage);
      }
      catch (RuntimeException rte)
      {
         HandlerTracker.reportHandleMessage(this, trackerMessage + ":" + rte.getMessage());
         throw rte;
      }
      return doNext;
   }

   protected boolean handleOutboundMessage(MessageContext msgContext)
   {
      return true;
   }

   protected boolean handleInboundMessage(MessageContext msgContext)
   {
      return true;
   }

   public boolean handleFault(MessageContext msgContext)
   {
      boolean doNext = isOutbound(msgContext) ? handleOutboundFaultInternal(msgContext) : handleInboundFaultInternal(msgContext);
      return doNext;
   }

   private boolean handleOutboundFaultInternal(MessageContext msgContext)
   {
      String trackerMessage = "OutBound";

      boolean doNext;
      try
      {
         doNext = handleOutboundFault(msgContext);
         if (doNext == false)
            trackerMessage += ":false";

         HandlerTracker.reportHandleFault(this, trackerMessage);
      }
      catch (RuntimeException rte)
      {
         HandlerTracker.reportHandleFault(this, trackerMessage + ":" + rte.getMessage());
         throw rte;
      }
      return doNext;
   }

   private boolean handleInboundFaultInternal(MessageContext msgContext)
   {
      String trackerMessage = "InBound";

      boolean doNext;
      try
      {
         doNext = handleInboundFault(msgContext);
         if (doNext == false)
            trackerMessage += ":false";

         HandlerTracker.reportHandleFault(this, trackerMessage);
      }
      catch (RuntimeException rte)
      {
         HandlerTracker.reportHandleFault(this, trackerMessage + ":" + rte.getMessage());
         throw rte;
      }
      return doNext;
   }

   protected boolean handleOutboundFault(MessageContext msgContext)
   {
      return true;
   }

   protected boolean handleInboundFault(MessageContext msgContext)
   {
      return true;
   }

   public void close(MessageContext messageContext)
   {
      HandlerTracker.reportHandleClose(this);
   }

   protected String getTestMethod(MessageContext msgContext)
   {
      String testMethod;
      try
      {

         SOAPMessage message = ((SOAPMessageContext)msgContext).getMessage();
         SOAPElement soapElement = null;
         Iterator it = message.getSOAPBody().getChildElements();

         while (soapElement == null && it.hasNext())
         {
            Object current = it.next();
            if (current instanceof SOAPElement)
            {
               soapElement = (SOAPElement)current;
            }
         }

         it = soapElement.getChildElements();
         soapElement = null;

         while (soapElement == null && it.hasNext())
         {
            Object current = it.next();
            if (current instanceof SOAPElement)
            {
               soapElement = (SOAPElement)current;
            }
         }

         testMethod = soapElement.getValue();
      }
      catch (SOAPException e)
      {
         throw new IllegalStateException("Cannot obtain test method");
      }

      if (testMethod.startsWith("test") == false)
         throw new IllegalStateException("Cannot find test method: " + testMethod);

      return testMethod;
   }

   protected Boolean isOutbound(MessageContext msgContext)
   {
      Boolean outbound = (Boolean)msgContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (outbound == null)
         throw new IllegalStateException("Cannot find property: " + MessageContext.MESSAGE_OUTBOUND_PROPERTY);

      return outbound;
   }

   public String toString()
   {
      String handlerName = getClass().getName();
      return handlerName.substring(handlerName.lastIndexOf(".") + 1);
   }
}
