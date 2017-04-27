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
package org.jboss.test.ws.jaxws.samples.jmstransport;

import java.rmi.RemoteException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.logging.Logger;
import org.jboss.ws.core.transport.jms.JMSTransportSupportEJB3;
import org.jboss.wsf.spi.annotation.WebContext;

/**
 * An example of a MDB acting as a web service endpoint.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 09-Jan-2008
 */
@WebService (targetNamespace = "http://org.jboss.ws/samples/jmstransport")
@WebContext (contextRoot = "/jaxws-samples-jmstransport")
@SOAPBinding(style = SOAPBinding.Style.RPC)

@MessageDriven(activationConfig = { 
      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
      @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/RequestQueue")
  },
  messageListenerInterface = javax.jms.MessageListener.class
)
public class OrganizationJMSEndpoint extends JMSTransportSupportEJB3
{
   // provide logging
   private static final Logger log = Logger.getLogger(OrganizationJMSEndpoint.class);

   @WebMethod
   public String getContactInfo(String organization) throws RemoteException
   {
      log.info("getContactInfo: " + organization);
      return "The '" + organization + "' boss is currently out of office, please call again.";
   }

   @Override
   public void onMessage(Message message)
   {
      log.info("onMessage: " + message);
      super.onMessage(message);
   }
}
