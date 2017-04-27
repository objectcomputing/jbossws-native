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
package org.jboss.ws.core.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.StringTokenizer;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.xml.ws.addressing.EndpointReference;

import org.jboss.remoting.marshal.Marshaller;
import org.jboss.remoting.marshal.UnMarshaller;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.soap.SOAPMessageMarshaller;
import org.jboss.ws.core.soap.SOAPMessageUnMarshaller;

/**
 * A SOAPConnection over JMS
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Jan-2008
 */
public class SOAPProtocolConnectionJMS implements RemoteConnection
{
   private boolean waitForResponse;

   public UnMarshaller getUnmarshaller()
   {
      return new SOAPMessageUnMarshaller();
   }

   public Marshaller getMarshaller()
   {
      return new SOAPMessageMarshaller();
   }

   public MessageAbstraction invoke(MessageAbstraction reqMessage, Object endpoint, boolean oneway) throws IOException
   {
      if (endpoint == null)
         throw new IllegalArgumentException("Given endpoint cannot be null");

      // Get target address
      String targetAddress;
      if (endpoint instanceof EndpointInfo)
      {
         EndpointInfo epInfo = (EndpointInfo)endpoint;
         targetAddress = epInfo.getTargetAddress();
      }
      else if (endpoint instanceof EndpointReference)
      {
         EndpointReference epr = (EndpointReference)endpoint;
         targetAddress = epr.getAddress().toString();
      }
      else
      {
         targetAddress = endpoint.toString();
      }

      try
      {
         URI jmsURI = new URI(targetAddress);
         String uriHost = jmsURI.getHost();
         String uriPath = jmsURI.getPath();

         String reqQueueName = getURLProperty(jmsURI, "destinationName");
         if (reqQueueName == null)
         {
            reqQueueName = uriHost;
            if (uriPath != null && uriPath.length() > 0)
               reqQueueName += uriPath;
         }

         InitialContext context = new InitialContext();
         QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("ConnectionFactory");
         Queue reqQueue = (Queue)context.lookup(reqQueueName);

         QueueConnection con = connectionFactory.createQueueConnection();
         QueueSession session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
         con.start();

         ByteArrayOutputStream output = new ByteArrayOutputStream();
         getMarshaller().write(reqMessage, output);
         BytesMessage message = session.createBytesMessage();
         message.writeBytes(output.toByteArray());

         ResponseListener responseListener = null;
         if (oneway == false)
         {
            String resQueueName = getURLProperty(jmsURI, "replyToName");
            Queue resQueue = (Queue)context.lookup(resQueueName);
            QueueReceiver receiver = session.createReceiver(resQueue);
            responseListener = new ResponseListener();
            receiver.setMessageListener(responseListener);
            message.setJMSReplyTo(resQueue);
            waitForResponse = true;
         }

         QueueSender sender = session.createSender(reqQueue);
         sender.send(message);
         sender.close();

         MessageAbstraction resMessage = null;
         if (responseListener != null)
         {
            int timeout = 30000;
            while (waitForResponse && timeout > 0)
            {
               Thread.sleep(100);
               timeout -= 100;
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(responseListener.resMessage.getBytes());
            resMessage = (MessageAbstraction)getUnmarshaller().read(bais, null);
         }

         con.stop();
         session.close();
         con.close();

         return resMessage;
      }
      catch (RuntimeException ex)
      {
         throw ex;
      }
      catch (Exception ex)
      {
         IOException ioex = new IOException(ex.getMessage());
         ioex.initCause(ex);
         throw ioex;
      }
   }

   private String getURLProperty(URI uri, String key)
   {
      String retValue = null;
      String query = uri.getQuery();
      if (query != null)
      {
         StringTokenizer st = new StringTokenizer(query, "?:=");
         while (retValue == null && st.hasMoreTokens())
         {
            String propName = st.nextToken();
            String propValue = st.nextToken();
            if (propName.equals(key))
               retValue = propValue;
         }
      }
      return retValue;
   }

   public class ResponseListener implements MessageListener
   {
      public String resMessage;

      public void onMessage(Message msg)
      {
         TextMessage textMessage = (TextMessage)msg;
         try
         {
            resMessage = textMessage.getText();
            waitForResponse = false;
         }
         catch (Throwable t)
         {
            t.printStackTrace();
         }
      }
   }
}
