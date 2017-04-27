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

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.naming.InitialContext;
import javax.xml.ws.Action;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;

import org.jboss.logging.Logger;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.AuthMethod;
import org.jboss.wsf.spi.annotation.TransportGuarantee;
import org.jboss.wsf.spi.annotation.WebContext;


/**
 * Addressing endpoint; performs sync and async DAR route optimization
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
@Stateless
@SOAPBinding
(
   style = SOAPBinding.Style.RPC,
   use = SOAPBinding.Use.LITERAL
)
@WebService
(
   name = "DarEndpoint",
   serviceName = "DarService",
   targetNamespace = "http://org.jboss.ws/samples/dar"
)
@WebContext
(
   contextRoot = "/dar",
   urlPattern = "/*",
   authMethod = AuthMethod.BASIC,
   transportGuarantee = TransportGuarantee.NONE,
   secureWSDLAccess = false
)
@EndpointConfig(configName = "Standard WSAddressing Endpoint")
public class DarAddressingEndpoint
{
   @Resource
   private WebServiceContext ctx;
   private static Logger log = Logger.getLogger(DarAddressingEndpoint.class);
   
   @WebMethod(operationName = "process", action = "http://org.jboss.test.ws.jaxws.samples.dar/action/processIn")
   @Action(input = "http://org.jboss.test.ws.jaxws.samples.dar/action/processIn", output = "http://org.jboss.test.ws.jaxws.samples.dar/action/processOut")
   public DarResponse process(DarRequest request)
   {
      DarProcessor processor = new DarProcessor();
      AddressingProperties props = (AddressingProperties)ctx.getMessageContext().get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
      if (props!=null && props.getReplyTo()!=null)
      {
         System.out.println(props.getReplyTo().getAddress().getURI());
      }
      return processor.process(request);
   }
   
   
   
   @WebMethod(operationName = "onewayProcess", action = "http://org.jboss.test.ws.jaxws.samples.dar/action/onewayProcessIn")
   @Action(input = "http://org.jboss.test.ws.jaxws.samples.dar/action/onewayProcessIn")
   @Oneway
   public void onewayProcess(DarRequest request)
   {
      QueueSession queueSession =null;
      QueueSender sender = null;
      try {
         InitialContext context = new InitialContext();
         QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("ConnectionFactory");
         QueueConnection con = connectionFactory.createQueueConnection();
         queueSession = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
         Queue queue = (Queue)context.lookup("queue/DarQueue");
         sender = queueSession.createSender(queue);
         AsyncProcessRequest asyncRequest = new AsyncProcessRequest();
         asyncRequest.setRequest(request);
         AddressingProperties props = (AddressingProperties)ctx.getMessageContext().get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
         asyncRequest.setReplyTo(props.getReplyTo().getAddress().getURI());
         asyncRequest.setMessageId(props.getMessageID().getURI());
         ObjectMessage message = queueSession.createObjectMessage(asyncRequest);
         sender.send(message);
         log.info("AsyncProcessRequest sent...");
      } catch (Exception e) {
         throw new WebServiceException(e);
      } finally {
         try
         {
            sender.close();
         }
         catch(Exception e1) {}
         try
         {
            queueSession.close();
         }
         catch(Exception e1) {}
      }
      
   }
}
