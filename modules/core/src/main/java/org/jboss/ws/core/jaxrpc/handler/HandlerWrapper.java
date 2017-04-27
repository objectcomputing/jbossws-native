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
package org.jboss.ws.core.jaxrpc.handler;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.soap.SOAPFaultException;

import org.jboss.logging.Logger;

/**
 * A wrapper arround a {@link javax.xml.rpc.handler.Handler} that takes care of its lifecycle.
 *
 * @author thomas.diesler@jboss.org
 */
public class HandlerWrapper implements Handler
{
   private static Logger log = Logger.getLogger(HandlerWrapper.class);

   public final static int DOES_NOT_EXIST = 0;
   public final static int METHOD_READY = 1;

   // The states as string
   private static String[] stateNames = new String[]{"DOES_NOT_EXIST", "METHOD_READY"};

   // The handler to delegate to
   private Handler delegate;
   // The handler state
   private int state;

   /**
    * Delegate to the given handler
    */
   public HandlerWrapper(Handler handler)
   {
      delegate = handler;
      state = DOES_NOT_EXIST; // this is somewhat a lie ;-)
   }

   /**
    * Get the current state
    */
   public int getState()
   {
      return state;
   }

   /**
    * Get the current state as string
    */
   public String getStateAsString()
   {
      return stateNames[state];
   }

   /**
    * Gets the header blocks processed by this Handler instance.
    */
   public QName[] getHeaders()
   {
      return delegate.getHeaders();
   }

   /**
    * The init method enables the Handler instance to initialize itself.
    */
   public void init(HandlerInfo config) throws JAXRPCException
   {
      if(log.isDebugEnabled()) log.debug("init: " + delegate);
      delegate.init(config);
      state = METHOD_READY;
   }

   /**
    * The destroy method indicates the end of lifecycle for a Handler instance.
    */
   public void destroy() throws JAXRPCException
   {
      if(log.isDebugEnabled()) log.debug("destroy: " + delegate);
      state = DOES_NOT_EXIST;
      delegate.destroy();
   }

   /**
    * The handleRequest method processes the request message.
    */
   public boolean handleRequest(MessageContext msgContext) throws JAXRPCException, SOAPFaultException
   {
      if (state == DOES_NOT_EXIST)
      {
         log.warn("Handler is in state DOES_NOT_EXIST, skipping Handler.handleRequest for: " + delegate);
         return true;
      }

      try
      {
         return delegate.handleRequest(msgContext);
      }
      catch (RuntimeException e)
      {
         return handleRuntimeException(e);
      }
   }

   /**
    * The handleResponse method processes the response SOAP message.
    */
   public boolean handleResponse(MessageContext msgContext)
   {
      if (state == DOES_NOT_EXIST)
      {
         log.warn("Handler is in state DOES_NOT_EXIST, skipping Handler.handleResponse for: " + delegate);
         return true;
      }

      try
      {
         return delegate.handleResponse(msgContext);
      }
      catch (RuntimeException e)
      {
         return handleRuntimeException(e);
      }
   }

   /**
    * The handleFault method processes the SOAP faults based on the SOAP message processing model.
    */
   public boolean handleFault(MessageContext msgContext)
   {
      if (state == DOES_NOT_EXIST)
      {
         log.warn("Handler is in state DOES_NOT_EXIST, skipping Handler.handleFault for: " + delegate);
         return true;
      }

      try
      {
         return delegate.handleFault(msgContext);
      }
      catch (RuntimeException e)
      {
         return handleRuntimeException(e);
      }
   }

   /**
    * As defined by JAX-RPC, a RuntimeException(other than SOAPFaultException) thrown from any method of
    * the Handler results in the destroymethod being invoked and transition to the �Does Not Exist� state.
    */
   private boolean handleRuntimeException(RuntimeException e)
   {
      if ((e instanceof SOAPFaultException) == false)
      {
         log.warn("RuntimeException in handler method, transition to DOES_NOT_EXIST");
         destroy();
      }

      throw e;
   }

   /**
    * Returns a hash code value for the object.
    */
   public int hashCode()
   {
      return delegate.hashCode();
   }

   /**
    * Returns a string representation of the object.
    */
   public String toString()
   {
      return "[state=" + getStateAsString() + ",handler=" + delegate + "]";
   }
}
