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
package org.jboss.ws.extensions.eventing.jaxws;

import java.net.URI;
import java.net.URISyntaxException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.ws.Action;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.AttributedURI;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.common.EventingEndpointBase;
import org.jboss.ws.extensions.eventing.mgmt.Filter;
import org.jboss.ws.extensions.eventing.mgmt.SubscriptionError;
import org.jboss.ws.extensions.eventing.mgmt.SubscriptionManagerMBean;
import org.jboss.ws.extensions.eventing.mgmt.SubscriptionTicket;

/**
 * @author Heiko.Braun@jboss.org
 * @since 16.01.2007
 */
@WebService(
   name = "EventSource",
   portName = "EventSourcePort",
   targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing",
   wsdlLocation = "/WEB-INF/wsdl/wind.wsdl")
@EndpointConfig(configName = "Standard WSAddressing Endpoint")
public abstract class AbstractEventSourceEndpoint extends EventingEndpointBase implements EventSourceEndpoint {

   @WebMethod(operationName = "SubscribeOp")
   @WebResult(name = "SubscribeResponse", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", partName = "body")
   @Action(
      input = "http://schemas.xmlsoap.org/ws/2004/08/eventing/Subscribe",
      output = "http://schemas.xmlsoap.org/ws/2004/08/eventing/SubscribeResponse"
   )
   public SubscribeResponse subscribeOp(@WebParam(name = "Subscribe", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", partName = "body") Subscribe request) {
      try
      {
         // retrieve addressing headers
         AddressingProperties inProps = getAddrProperties();
         AttributedURI eventSourceURI = inProps.getTo();
         getLogger().debug("Subscribe request for event source: " + eventSourceURI.getURI());

         assertSubscriberEndpoints(request);
         EndpointReferenceType notifyTo = request.getDelivery().getNotifyTo();
         EndpointReferenceType endTo = request.getEndTo();

         // adapt filter elements
         Filter filter = null;
         if (request.getFilter() != null)
         {
            try {
               filter = new Filter(
                  new URI(request.getFilter().getDialect()),
                  (String)request.getFilter().getContent().get(0));
            } catch (URISyntaxException e) {
               throw new WSException(e);
            }
         }

         // invoke subscription manager
         SubscriptionManagerMBean subscriptionManager = getSubscriptionManager();
         SubscriptionTicket ticket = subscriptionManager.subscribe(
            eventSourceURI.getURI(),
            notifyTo, endTo,
            request.getExpires(),
            filter
         );

         // create the response element
         SubscribeResponse res = new SubscribeResponse();
         res.setExpires(ticket.getExpires());
         res.setSubscriptionManager(ticket.getSubscriptionManager());

         return res;

      }
      catch (SubscriptionError e)
      {
         throw new SOAPFaultException(buildFaultQName(e.getSubcode()), e.getReason(), null, null);
      }
   }

   /**
    * Ensure that the subscriber endpoint information is supplied in request.
    * Namely NotifyTo and EndTo need to be set.
    * @param request
    */
   private void assertSubscriberEndpoints(Subscribe request) {
      if(null == request.getDelivery().getNotifyTo() ||  null == request.getEndTo() )
         throw new SOAPFaultException( buildFaultQName(EventingConstants.CODE_INVALID_MESSAGE) ,
            "Subcriber endpoint information missing from request",
            null, null
         );
   }

   /**
    * Subsclasses need to provide a logger for this endpoint
    * @return a custom {@link org.jboss.logging.Logger} instance
    */
   protected abstract Logger getLogger();
}
