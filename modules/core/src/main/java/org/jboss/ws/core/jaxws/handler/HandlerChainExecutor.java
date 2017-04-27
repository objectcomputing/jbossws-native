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
package org.jboss.ws.core.jaxws.handler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.logging.Logger;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.jaxws.SOAPFaultHelperJAXWS;
import org.jboss.ws.core.soap.SOAPEnvelopeImpl;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.wsf.common.DOMWriter;

/**
 * Executes a list of JAXWS handlers.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 06-May-2004
 */
public class HandlerChainExecutor
{
   private static Logger log = Logger.getLogger(HandlerChainExecutor.class);

   // The endpoint meta data
   private EndpointMetaData epMetaData;
   // The list of handlers 
   protected List<Handler> handlers = new ArrayList<Handler>();
   // The list of executed handlers
   protected List<Handler> executedHandlers = new ArrayList<Handler>();
   // The index of the first handler that returned false during processing
   protected int falseIndex = -1;
   // True if the current direction is outbound
   protected Boolean isOutbound;
   // True if this is for the server/endpoint side, used to determine client side specific
   // conformance requirements.
   private boolean serverSide;

   public HandlerChainExecutor(EndpointMetaData epMetaData, List<Handler> unsortedChain, boolean serverSide)
   {
      this.epMetaData = epMetaData;
      this.serverSide = serverSide;

      // Sort handler logical handlers first
      List<Handler> sortedChain = new ArrayList<Handler>();
      for (Handler handler : unsortedChain)
      {
         if (handler instanceof LogicalHandler)
            sortedChain.add(handler);
      }
      for (Handler handler : unsortedChain)
      {
         if ((handler instanceof LogicalHandler) == false)
            sortedChain.add(handler);
      }

      log.debug("Create a handler executor: " + sortedChain);
      for (Handler handler : sortedChain)
      {
         handlers.add(handler);
      }
   }

   /**
    * Indicates the end of lifecycle for a HandlerChain.
    */
   public void close(MessageContext msgContext)
   {
      log.debug("close");
      MessageContextJAXWS context = (MessageContextJAXWS)msgContext;
      for (int index = 1; index <= executedHandlers.size(); index++)
      {
         Handler currHandler = executedHandlers.get(executedHandlers.size() - index);
         try
         {
            context.setCurrentScope(Scope.HANDLER);
            currHandler.close(msgContext);
         }
         finally
         {
            context.setCurrentScope(Scope.APPLICATION);
         }
      }
   }

   public boolean handleMessage(MessageContext msgContext)
   {
      isOutbound = (Boolean)msgContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (isOutbound == null)
         throw new IllegalStateException("Cannot find property: " + MessageContext.MESSAGE_OUTBOUND_PROPERTY);

      boolean doNext = true;

      if (handlers.size() > 0)
      {
         log.debug("Enter: handle" + (isOutbound ? "Out" : "In ") + "BoundMessage");

         int index = getFirstHandler();
         Handler currHandler = null;
         try
         {
            String lastMessageTrace = null;
            while (doNext && index >= 0)
            {
               currHandler = handlers.get(index);

               if (log.isTraceEnabled() && msgContext instanceof SOAPMessageContext)
               {
                  SOAPPart soapPart = ((SOAPMessageContext)msgContext).getMessage().getSOAPPart();
                  lastMessageTrace = traceSOAPPart("BEFORE handleRequest - " + currHandler, soapPart, lastMessageTrace);
               }

               doNext = handleMessage(currHandler, msgContext);

               if (log.isTraceEnabled() && msgContext instanceof SOAPMessageContext)
               {
                  SOAPPart soapPart = ((SOAPMessageContext)msgContext).getMessage().getSOAPPart();
                  lastMessageTrace = traceSOAPPart("AFTER handleRequest - " + currHandler, soapPart, lastMessageTrace);
               }

               if (doNext)
                  index = getNextIndex(index);
            }
         }
         catch (RuntimeException ex)
         {
            doNext = false;
            processHandlerFailure(ex);
         }
         finally
         {
            // we start at this index in the response chain
            if (doNext == false)
               falseIndex = index;

            log.debug("Exit: handle" + (isOutbound ? "Out" : "In ") + "BoundMessage with status: " + doNext);
         }
      }

      return doNext;
   }

   public boolean handleFault(MessageContext msgContext, Exception ex)
   {
      isOutbound = (Boolean)msgContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (isOutbound == null)
         throw new IllegalStateException("Cannot find property: " + MessageContext.MESSAGE_OUTBOUND_PROPERTY);

      boolean doNext = true;

      if (handlers.size() > 0)
      {
         log.debug("Enter: handle" + (isOutbound ? "Out" : "In ") + "BoundFault");

         if (msgContext instanceof SOAPMessageContext)
         {
            SOAPMessageContext soapContext = (SOAPMessageContext)msgContext;
            SOAPMessage soapMessage = soapContext.getMessage();

            // If the message is not already a fault message then it is replaced with a fault message
            try
            {
               if (soapMessage == null || soapMessage.getSOAPBody().getFault() == null)
               {
                  soapMessage = SOAPFaultHelperJAXWS.exceptionToFaultMessage(ex);
                  soapContext.setMessage(soapMessage);
               }
            }
            catch (SOAPException se)
            {
               throw new WebServiceException("Cannot convert exception to fault message", ex);
            }
         }

         int index = getFirstHandler();
         
         Handler currHandler = null;
         try
         {
            String lastMessageTrace = null;
            while (doNext && index >= 0)
            {
               currHandler = handlers.get(index);

               if (log.isTraceEnabled() && msgContext instanceof SOAPMessageContext)
               {
                  SOAPPart soapPart = ((SOAPMessageContext)msgContext).getMessage().getSOAPPart();
                  lastMessageTrace = traceSOAPPart("BEFORE handleFault - " + currHandler, soapPart, lastMessageTrace);
               }

               doNext = handleFault(currHandler, msgContext);

               if (log.isTraceEnabled() && msgContext instanceof SOAPMessageContext)
               {
                  SOAPPart soapPart = ((SOAPMessageContext)msgContext).getMessage().getSOAPPart();
                  lastMessageTrace = traceSOAPPart("AFTER handleFault - " + currHandler, soapPart, lastMessageTrace);
               }

               index = getNextIndex(index);
            }
         }
         catch (RuntimeException rte)
         {
            doNext = false;
            processHandlerFailure(rte);
         }
         finally
         {
            log.debug("Exit: handle" + (isOutbound ? "Out" : "In ") + "BoundFault with status: " + doNext);
         }
      }

      return doNext;
   }

   private int getFirstHandler()
   {
      int index;
      if (falseIndex == -1)
      {
         index = (isOutbound ? 0 : handlers.size() - 1);
      }
      else
      {
         index = getNextIndex(falseIndex);
      }
      return index;
   }

   private int getNextIndex(int prevIndex)
   {
      int nextIndex = (isOutbound ? prevIndex + 1 : prevIndex - 1);
      if (nextIndex >= handlers.size())
         nextIndex = -1;
      return nextIndex;
   }

   // 4.14 Conformance (Exceptions During Handler Processing): Exceptions thrown during handler processing on
   // the client MUST be passed on to the application. If the exception in question is a subclass of WebService-
   // Exception then an implementation MUST rethrow it as-is, without any additional wrapping, otherwise it
   // MUST throw a WebServiceException whose cause is set to the exception that was thrown during handler processing.
   private void processHandlerFailure(RuntimeException ex)
   {
      log.error("Exception during handler processing", ex);
      
      // If this call is server side then the conformance requirement specific to
      // clients can be avoided.
      if (serverSide == true)
      {
         throw ex;
      }
      
      if (ex instanceof WebServiceException)
      {
         throw (WebServiceException)ex;
      }
      throw new WebServiceException(ex);
   }

   private boolean handleMessage(Handler currHandler, MessageContext msgContext)
   {
      CommonMessageContext context = (CommonMessageContext)msgContext;
      if (currHandler instanceof LogicalHandler)
      {
         if (msgContext instanceof SOAPMessageContextJAXWS)
            msgContext = new LogicalMessageContextImpl((SOAPMessageContextJAXWS)msgContext);
      }

      if (executedHandlers.contains(currHandler) == false)
         executedHandlers.add(currHandler);

      try
      {
         context.put(MessageContextJAXWS.ALLOW_EXPAND_TO_DOM, Boolean.TRUE);
         context.setCurrentScope(Scope.HANDLER);
         return currHandler.handleMessage(msgContext);
      }
      finally
      {
         context.setCurrentScope(Scope.APPLICATION);
         context.remove(MessageContextJAXWS.ALLOW_EXPAND_TO_DOM);
      }
   }

   private boolean handleFault(Handler currHandler, MessageContext msgContext)
   {
      CommonMessageContext context = (CommonMessageContext)msgContext;
      if (currHandler instanceof LogicalHandler)
      {
         if (msgContext instanceof SOAPMessageContextJAXWS)
            msgContext = new LogicalMessageContextImpl((SOAPMessageContextJAXWS)msgContext);
      }

      if (executedHandlers.contains(currHandler) == false)
         executedHandlers.add(currHandler);

      try
      {
         context.put(MessageContextJAXWS.ALLOW_EXPAND_TO_DOM, Boolean.TRUE);
         context.setCurrentScope(Scope.HANDLER);
         return currHandler.handleFault(msgContext);
      }
      finally
      {
         context.setCurrentScope(Scope.APPLICATION);
         context.remove(MessageContextJAXWS.ALLOW_EXPAND_TO_DOM);
      }
   }

   /**
    * Trace the SOAPPart, do nothing if the String representation is equal to the last one.
    */
   protected String traceSOAPPart(String logMsg, SOAPPart soapPart, String lastMessageTrace)
   {
      try
      {
         SOAPEnvelopeImpl soapEnv = (SOAPEnvelopeImpl)soapPart.getEnvelope();
         String envString = DOMWriter.printNode(soapEnv, true);
         if (envString.equals(lastMessageTrace))
         {
            log.trace(logMsg + ": unchanged");
         }
         else
         {
            log.trace(logMsg + "\n" + envString);
            lastMessageTrace = envString;
         }
         return lastMessageTrace;
      }
      catch (SOAPException e)
      {
         log.error("Cannot get SOAPEnvelope", e);
         return null;
      }
   }
}
