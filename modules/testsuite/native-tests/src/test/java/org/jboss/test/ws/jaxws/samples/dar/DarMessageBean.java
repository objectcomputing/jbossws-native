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

import java.net.URL;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.Relationship;

import org.jboss.logging.Logger;
import org.jboss.test.ws.jaxws.samples.dar.generated.reply.DarReplyEndpoint;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.extensions.addressing.AddressingClientUtil;
import org.jboss.ws.extensions.addressing.RelationshipImpl;

/**
 * Get DAR requests from the queue, delegate to the DarProcessor and
 * send responses back to the client endpoints.
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
@MessageDriven( name="DarListener", activationConfig= {
      @ActivationConfigProperty(propertyName="destinationType",propertyValue="javax.jms.Queue"),
      @ActivationConfigProperty(propertyName="destination",propertyValue="queue/DarQueue")}
)
public class DarMessageBean implements MessageListener
{
   private static Logger log = Logger.getLogger(DarMessageBean.class);
   private static final String WSA_ACTION = "http://org.jboss.test.ws.jaxws.samples.dar/action/receiveIn";
   
   public void onMessage(Message arg0)
   {
      try {
         ObjectMessage message = (ObjectMessage)arg0;
         AsyncProcessRequest asyncRequest = (AsyncProcessRequest)message.getObject();
         DarProcessor processor = new DarProcessor();
         DarResponse response = processor.process(asyncRequest.getRequest());
         
         //convert the response and send it to the client reply service
         org.jboss.test.ws.jaxws.samples.dar.generated.reply.DarResponse darResponse = ReplyConverter.convertResponse(response);
         String replyTo = asyncRequest.getReplyTo().toURL().toString();
         log.info("Response will be sent to: " + replyTo);
         QName serviceName = new QName("http://org.jboss.ws/samples/dar", "DarReplyService");
         Service service = Service.create(new URL(replyTo + "?wsdl"), serviceName);
         DarReplyEndpoint endpoint = (DarReplyEndpoint)service.getPort(DarReplyEndpoint.class);
         
         //setup addressing configuration and properties
         ((StubExt)endpoint).setConfigName("Standard WSAddressing Client");
         ((BindingProvider)endpoint).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, replyTo);
         AddressingProperties requestProps = AddressingClientUtil.createOneWayProps(WSA_ACTION, replyTo);
         requestProps.setMessageID(AddressingClientUtil.createMessageID());
         Relationship[] relationships = new Relationship[1];
         relationships[0] = new RelationshipImpl(asyncRequest.getMessageId());
         requestProps.setRelatesTo(relationships);
         ((BindingProvider)endpoint).getRequestContext().put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, requestProps);
         
         endpoint.receive(darResponse);
         log.info("Response sent.");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
