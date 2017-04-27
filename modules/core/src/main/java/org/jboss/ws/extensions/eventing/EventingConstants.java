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
package org.jboss.ws.extensions.eventing;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingConstants;

import org.jboss.ws.WSException;

/**
 * @author Heiko Braun, <heiko@openj.net>
 * @since 19-Dec-2005
 */
public class EventingConstants
{
   private static AddressingBuilder WSA_BUILDER = AddressingBuilder.getAddressingBuilder();
   private static AddressingConstants WSA_CONSTANTS = WSA_BUILDER.newAddressingConstants();

   // common namespaces

   public final static String NS_EVENTING = "http://schemas.xmlsoap.org/ws/2004/08/eventing";
   public final static String PREFIX_EVENTING = "wse";
   public final static String NS_ADDRESSING = WSA_CONSTANTS.getNamespaceURI();

   // request /response action from specification

   public final static String SUBSCRIBE_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/eventing/Subscribe";
   public final static String SUBSCRIBE_RESPONSE_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/eventing/SubscribeResponse";
   public final static String UNSUBSCRIBE_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/eventing/Unsubscribe";
   public final static String UNSUBSCRIBE_RESPONSE_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/eventing/UnsubscribeResponse";
   public final static String GET_STATUS_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/eventing/GetStatus";
   public final static String GET_STATUS_RESPONSE_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/eventing/GetStatusResponse";
   public final static String RENEW_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/eventing/Renew";
   public final static String RENEW_RESPONSE_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/eventing/RenewResponse";
   public final static String SUBSCRIPTION_END_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/eventing/SubscriptionEnd";

   public final static String WSA_FAULT_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/addressing/fault";
   public final static String WSA_ANONYMOUS_URI = WSA_CONSTANTS.getAnonymousURI();

   // failures

   public final static String DELIVERY_FAILURE = "http://schemas.xmlsoap.org/ws/2004/08/eventing/DeliveryFailure";
   public final static String SOURCE_SHUTTING_DOWN = "http://schemas.xmlsoap.org/ws/2004/08/eventing/SourceShuttingDown";
   public final static String SOURCE_CANCELING = "http://schemas.xmlsoap.org/ws/2004/08/eventing/SourceCanceling";

   public final static String CODE_UNABLE_TO_PROCESS = "EventSourceUnableToProcess";
   public final static String CODE_FILTER_NOT_SUPPORTED = "FilteringNotSupported";
   public final static String CODE_REQUESTED_FILTER_UNAVAILABLE = "FilteringRequestedUnavailable";
   public final static String CODE_INVALID_EXPIRATION_TIME = "InvalidExpirationTime";
   public final static String CODE_UNABLE_TO_RENEW = "UnableToRenew";
   public final static String CODE_INVALID_MESSAGE = "InvalidMessage";

   // default config

   public final static String DISPATCHER_JNDI_NAME = "EventDispatcher";
   public final static long MAX_LEASE_TIME = 1000 * 60 * 10L;
   public final static long DEFAULT_LEASE = 1000 * 60 * 5L;

   private static URI DELIVERY_PUSH_URI = null;
   private static URI DIALECT_XPATH_URI = null;

   private static URI SUBSCRIBE_ACTION_URI = null;   

   public final static URI buildURI(String uri)
   {
      try
      {
         return new URI(uri);
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   public static final URI getDefaultFilterDialect()
   {
      try
      {
         return new URI("http://www.w3.org/TR/1999/REC-xpath-19991116");
      }
      catch (URISyntaxException e)
      {
         throw new WSException(e.getMessage());
      }
   }

   public static final URI getDeliveryPush()
   {
      if (null == DELIVERY_PUSH_URI)
      {
         try
         {
            DELIVERY_PUSH_URI = new URI("http://schemas.xmlsoap.org/ws/2004/08/eventing/DeliveryModes/Push");
         }
         catch (URISyntaxException e)
         {
            //
         }
      }
      return DELIVERY_PUSH_URI;
   }

   public static URI getDialectXPath()
   {
      if (null == DIALECT_XPATH_URI)
      {
         try
         {
            DIALECT_XPATH_URI = new URI("http://www.w3.org/TR/1999/REC-xpath-19991116");
         }
         catch (URISyntaxException e)
         {
            //
         }
      }

      return DIALECT_XPATH_URI;
   }

   public static URI getSubscribeAction()
   {
      if (null == SUBSCRIBE_ACTION_URI)
      {
         try
         {
            SUBSCRIBE_ACTION_URI = new URI(SUBSCRIBE_ACTION);
         }
         catch (URISyntaxException e)
         {
            //
         }
      }

      return SUBSCRIBE_ACTION_URI;
   }
}
