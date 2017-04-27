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
import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.ws.Action;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.ReferenceParameters;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.common.EventingEndpointBase;
import org.jboss.ws.extensions.eventing.mgmt.SubscriptionError;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 * @author Heiko.Braun@jboss.org
 * @since 16.01.2007
 */
@WebService(
   name = "SubscriptionManager",
   portName = "SubscriptionManagerPort",
   targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing",
   wsdlLocation = "/WEB-INF/wsdl/wind.wsdl")
@EndpointConfig(configName = "Standard WSAddressing Endpoint")
public abstract class AbstractSubscriptionManagerEndpoint extends EventingEndpointBase implements SubscriptionManagerEndpoint {

   public static final QName IDQN = new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing", "Identifier", "ns1");

   @WebMethod(operationName = "GetStatusOp")
   @WebResult(name = "GetStatusResponse", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", partName = "body")
   @Action(
      input = "http://schemas.xmlsoap.org/ws/2004/08/eventing/GetStatus",
      output = "http://schemas.xmlsoap.org/ws/2004/08/eventing/GetStatusResponse"
   )
   public GetStatusResponse getStatusOp(@WebParam(name = "GetStatus", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", partName = "body") GetStatus body) {

      URI identifier = retrieveSubscriptionId();

      getLogger().debug("GetStatus request for subscriptionID: " + identifier);

      try
      {
         Date leaseTime = getSubscriptionManager().getStatus(identifier);
         GetStatusResponse response = new GetStatusResponse();
         response.setExpires(leaseTime);

         return response;
      }
      catch (SubscriptionError e)
      {
         throw new SOAPFaultException(buildFaultQName(e.getSubcode()), e.getReason(), null, null);
      }
   }

   @WebMethod(operationName = "RenewOp")
   @WebResult(name = "RenewResponse", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", partName = "body")
   @Action(
      input = "http://schemas.xmlsoap.org/ws/2004/08/eventing/Renew",
      output = "http://schemas.xmlsoap.org/ws/2004/08/eventing/RenewResponse"
   )
   public RenewResponse renewOp(@WebParam(name = "Renew", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", partName = "body") Renew request) {

      URI identifier = retrieveSubscriptionId();

      getLogger().debug("Renew request for subscriptionID: " + identifier);

      try
      {
         Date newLeaseTime = getSubscriptionManager().renew(identifier, request.getExpires());
         RenewResponse response = new RenewResponse();
         response.setExpires(newLeaseTime);

         return response;
      }
      catch (SubscriptionError e)
      {
         throw new SOAPFaultException(buildFaultQName(e.getSubcode()), e.getReason(), null, null);
      }
   }

   @WebMethod(operationName = "UnsubscribeOp")
   @Action(
      input = "http://schemas.xmlsoap.org/ws/2004/08/eventing/Unsubscribe",
      output = "http://schemas.xmlsoap.org/ws/2004/08/eventing/UnsubscribeResponse"
   )
   public void unsubscribeOp(@WebParam(name = "Unsubscribe", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/08/eventing", partName = "body") Unsubscribe body) {

      URI identifier = retrieveSubscriptionId();

      getLogger().debug("Unsubscribe request for subscriptionID: " + identifier);

      try
      {
         getSubscriptionManager().unsubscribe(identifier);
      }
      catch (SubscriptionError e)
      {
         throw new SOAPFaultException(buildFaultQName(e.getSubcode()), e.getReason(), null, null);
      }

   }

   private URI retrieveSubscriptionId()
   {     
      URI subscriptionId = null;
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      AddressingProperties addrProps = (AddressingProperties)msgContext.get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);

      if (null == addrProps)
      {
         throw new SOAPFaultException(
            Constants.SOAP11_FAULT_CODE_CLIENT,
            "The message is not valid and cannot be processed: " +
               "Cannot obtain addressing properties.",
            null, null
         );
      }

      ReferenceParameters refParams = addrProps.getReferenceParameters();
      if (refParams != null)
      {
         for (Object obj : refParams.getElements())
         {
            if (obj instanceof Element)
            {
               Element el = (Element)obj;
               QName qname = DOMUtils.getElementQName(el);
               if (qname.equals(IDQN))
               {
                  try
                  {
                     subscriptionId = new URI(DOMUtils.getTextContent(el));
                     break;
                  }
                  catch (URISyntaxException e)
                  {
                     throw new SOAPFaultException(
                        Constants.SOAP11_FAULT_CODE_CLIENT,
                        "The message is not valid and cannot be processed: " +
                           "Invalid subscription id.",
                        null, null
                     );
                  }
               }
            }
         }
      }

      if (null == subscriptionId)
      {
         throw new SOAPFaultException(
            buildFaultQName(EventingConstants.CODE_INVALID_MESSAGE),
            "The message is not valid and cannot be processed: "
               + "Cannot obtain subscription id.",
            null, null
         );
      }

      return subscriptionId;
   }

   /**
    * Subsclasses need to provide a logger for this endpoint
    * @return a custom {@link org.jboss.logging.Logger} instance
    */
   protected abstract Logger getLogger();
}
