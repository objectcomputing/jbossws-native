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
package org.jboss.test.ws.jaxrpc.jbws1093;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

/**
 * JBWS-1093 - This servlet is called ServletTest to check that we are
 * not identifying servlets by the classname ending in 'Servlet'.
 * 
 * @author darran.lofthouse@jboss.com
 * @since 17-October-2006
 */
public class ServletTest extends HttpServlet
{
   public static final String MESSAGE = "Success!!";

   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      String reqType = req.getParameter("type");
      if ("txtMessage".equals(reqType))
      {
         PrintWriter writer = res.getWriter();
         writer.println(MESSAGE);
      }
      else if ("soapMessage".equals(reqType))
      {
         try
         {
            // Create a SOAPMessage
            MessageFactory msgFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPMessage msg = msgFactory.createMessage();

            SOAPPart sp = msg.getSOAPPart();

            SOAPEnvelope env = sp.getEnvelope();

            SOAPBody body = env.getBody();

            Name elName = env.createName("GetLastTradePriceResponse", "ztrade", "http://wombat.ztrade.com");
            body.addBodyElement(elName).addChildElement("Price").addTextNode("95.12");

            msg.saveChanges();

            res.setStatus(HttpServletResponse.SC_OK);

            // Write out the message on the response stream.
            OutputStream os = res.getOutputStream();
            msg.writeTo(os);
            os.flush();
         }
         catch (SOAPException e)
         {
            throw new ServletException(e);
         }
      }
   }
}
