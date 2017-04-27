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

import java.io.IOException;
import java.io.PrintStream;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

/**
 * DAR client; invokes the DAR JMS endpoint
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
public class JMSClient extends HttpServlet
{
   private Logger log = Logger.getLogger(JMSClient.class);
   
   protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
      runMessageClient(new PrintStream(httpServletResponse.getOutputStream()));
   }
   
   private void runMessageClient(PrintStream ps)
   {
      String reqMessage = "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
         "<env:Header></env:Header>" +
           "<env:Body>" +
             "<ns1:process xmlns:ns1='http://org.jboss.ws/samples/dar'>" +
               "<arg0>" +
                 "<buses>" +
                   "<capacity>10</capacity>" +
                   "<id>Bus0</id>" +
                 "</buses>" +
                 "<buses>" +
                   "<capacity>10</capacity>" +
                   "<id>Bus1</id>" +
                 "</buses>" +
                 "<mapId>map1234</mapId>" +
                 "<requests>" +
                   "<from><node>667</node><time>2008-02-01T14:25:12.114+01:00</time></from>" +
                   "<id>Req0</id>" +
                   "<people>2</people>" +
                   "<to><node>17</node><time>2008-02-01T14:25:12.114+01:00</time></to>" +
                 "</requests>" +
                 "<requests>" +
                   "<from><node>18</node><time>2008-02-01T14:25:12.114+01:00</time></from>" +
                   "<id>Req1</id>" +
                   "<people>1</people>" +
                   "<to><node>575</node><time>2008-02-01T14:25:12.114+01:00</time></to>" +
                 "</requests>" +
                 "<requests>" +
                   "<from><node>713</node><time>2008-02-01T14:25:12.114+01:00</time></from>" +
                   "<id>Req2</id>" +
                   "<people>2</people>" +
                   "<to><node>845</node><time>2008-02-01T14:25:12.114+01:00</time></to>" +
                 "</requests>" +
                 "<requests>" +
                   "<from><node>117</node><time>2008-02-01z...zT14:25:12.114+01:00</time></from>" +
                   "<id>Req3</id>" +
                   "<people>2</people>" +
                   "<to><node>140</node><time>2008-02-01T14:25:12.114+01:00</time></to>" +
                   "</requests>" +
                 "<requests>" +
                   "<from><node>318</node><time>2008-02-01T14:25:12.114+01:00</time></from>" +
                   "<id>Req4</id>" +
                   "<people>1</people>" +
                   "<to><node>57</node><time>2008-02-01T14:25:12.114+01:00</time></to>" +
                 "</requests>" +
               "</arg0>" +
             "</ns1:process>" +
           "</env:Body>" +
         "</env:Envelope>";
      
      QueueConnection con = null;
      QueueSession session = null;
      try
      {
         InitialContext context = new InitialContext();
         QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("ConnectionFactory");
         Queue reqQueue = (Queue)context.lookup("queue/DarRequestQueue");
         con = connectionFactory.createQueueConnection();
         session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
         Queue resQueue = (Queue)context.lookup("queue/DarResponseQueue");
         con.start();
         TextMessage message = session.createTextMessage(reqMessage);
         message.setJMSReplyTo(resQueue);
         QueueSender sender = session.createSender(reqQueue);
         sender.send(message);
         sender.close();
         ps.println("Request message sent, doing something interesting in the mean time... ;-) ");
         con.stop();
      }
      catch (Exception e)
      {
         e.printStackTrace(ps);
      }
      finally
      {
         try
         {
            session.close();
         }
         catch(Exception e1) {}
         try
         {
            con.close();
         }
         catch(Exception e1) {}
      }
   }
   

   protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
      doGet(httpServletRequest,httpServletResponse);
   }
}
