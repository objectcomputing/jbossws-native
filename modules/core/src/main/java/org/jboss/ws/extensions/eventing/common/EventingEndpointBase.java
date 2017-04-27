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
package org.jboss.ws.extensions.eventing.common;

import javax.xml.namespace.QName;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;

import org.jboss.ws.Constants;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.mgmt.SubscriptionManagerFactory;
import org.jboss.ws.extensions.eventing.mgmt.SubscriptionManagerMBean;

/**
 * @author Heiko Braun, <heiko@openj.net>
 * @since 13-Jan-2006
 */
public abstract class EventingEndpointBase
{

   private AddressingBuilder addrBuilder;

   /**
    * Retrieve the addressing properties associated with the request
    * and verify them.
    */
   protected static AddressingProperties getAddrProperties()
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      AddressingProperties inProps = (AddressingProperties)msgContext.get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
      assertAddrProperties(inProps);
      return inProps;
   }

   /**
    * Access local subscription manager service.
    */
   protected SubscriptionManagerMBean getSubscriptionManager()
   {
      SubscriptionManagerFactory factory = SubscriptionManagerFactory.getInstance();
      SubscriptionManagerMBean subscriptionManager = factory.getSubscriptionManager();
      return subscriptionManager;
   }

   protected AddressingBuilder getAddrBuilder()
   {
      if (null == addrBuilder)
         addrBuilder = AddressingBuilder.getAddressingBuilder();
      return addrBuilder;
   }

   /**
    * Ensure that all required inbound properties are supplied in request.
    * @param inProps
    * @throws javax.xml.rpc.soap.SOAPFaultException
    */
   protected static void assertAddrProperties(AddressingProperties inProps) throws SOAPFaultException
   {
      if (null == inProps)
         throw new SOAPFaultException(Constants.SOAP11_FAULT_CODE_CLIENT, "Addressing headers missing from request", "wse:InvalidMessage", null);
      if(null == inProps.getTo())
         throw new SOAPFaultException(Constants.SOAP11_FAULT_CODE_CLIENT, "Event source URI missing from request (wsa:To)", "wse:InvalidMessage", null);
   }

   public QName buildFaultQName(String elementName)
   {
      return new QName(EventingConstants.NS_EVENTING, elementName, EventingConstants.PREFIX_EVENTING);
   }
}
