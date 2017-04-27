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
package org.jboss.test.ws.jaxws.handlerlifecycle;

import java.io.ByteArrayInputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class ServerHandler2 extends LifecycleHandler
{
   protected boolean handleInboundMessage(MessageContext msgContext)
   {
      boolean doNext = true;
      if (getTestMethod(msgContext).startsWith("testServerInboundHandleMessageFalse"))
      {
         String resEnv = 
            "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
            "<env:Body>" +
            "  <ns1:runTestResponse xmlns:ns1='http://org.jboss.ws/jaxws/handlerlifecycle'>" +
            "   <return xmlns=''>testServerHandler2Response</return>" +
            "  </ns1:runTestResponse>" +
            " </env:Body>" +
            "</env:Envelope>";
         
         try
         {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage resMessage = factory.createMessage(null, new ByteArrayInputStream(resEnv.getBytes()));
            SOAPMessageContext soapContext = (SOAPMessageContext)msgContext;
            soapContext.setMessage(resMessage);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Cannot attach response message");
         }
         
         doNext = false;
      }
      else if (getTestMethod(msgContext).startsWith("testServerInboundHandleMessageThrowsRuntimeException"))
      {
         throw new WebServiceException("ErrorIn" + this);
      }
      return doNext;
   }

   protected boolean handleOutboundMessage(MessageContext msgContext)
   {
      boolean doNext = true;
      if (getTestMethod(msgContext).startsWith("testServerOutboundHandleMessageFalse"))
      {
         doNext = false;
      }
      else if (getTestMethod(msgContext).startsWith("testServerOutboundHandleMessageThrowsRuntimeException"))
      {
         throw new WebServiceException("ErrorIn" + this);
      }
      return doNext;
   }
}
