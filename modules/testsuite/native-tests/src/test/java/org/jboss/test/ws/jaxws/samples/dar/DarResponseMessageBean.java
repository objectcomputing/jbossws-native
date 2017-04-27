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
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

/**
 * Receives DAR responses through JMS
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
@MessageDriven(activationConfig = { 
      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
      @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/DarResponseQueue")
  },
  messageListenerInterface = javax.jms.MessageListener.class
)
public class DarResponseMessageBean
{
   private Logger log = Logger.getLogger(DarResponseMessageBean.class);
   
   public void onMessage(Message arg0)
   {
      try
      {
         TextMessage textMessage = (TextMessage)arg0;
         String result = textMessage.getText();
         log.info("DAR response received: " + result);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
}
