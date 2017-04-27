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
package org.jboss.test.ws.jaxws.samples.dar;

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
 * Performs DAR route optimization
 *
 * @author alessio.soldano@jboss.com
 * @since 01-Feb-2008
 */
@WebService (name = "DarEndpoint",
             targetNamespace = "http://org.jboss.ws/samples/dar",
             serviceName = "DarService")
@WebContext(contextRoot="/dar")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@MessageDriven(activationConfig = { 
      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
      @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/DarRequestQueue")
  },
  messageListenerInterface = javax.jms.MessageListener.class
)
public class DarJMSEndpoint extends JMSTransportSupportEJB3
{
   
   private static final Logger log = Logger.getLogger(DarJMSEndpoint.class);

   @WebMethod(operationName = "process", action = "http://org.jboss.test.ws.jaxws.samples.dar/action/processIn")
   public DarResponse process(DarRequest request)
   {
      DarProcessor processor = new DarProcessor();
      return processor.process(request);
   }

   @Override
   public void onMessage(Message message)
   {
      log.debug("onMessage: " + message);
      super.onMessage(message);
   }
}
