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
package org.jboss.test.ws.jaxrpc.jbws68;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

public class ClientHandler extends GenericHandler
{
   private static final String TARGET_NAMESPACE = "http://org.jboss.test.webservice/jbws68";
   
   protected QName[] headers;

   public QName[] getHeaders()
   {
      return headers;
   }

   public boolean handleRequest(MessageContext msgContext)
   {
      try
      {
         SOAPMessageContext soapContext = (SOAPMessageContext)msgContext;
         SOAPMessage soapMessage = soapContext.getMessage();
         SOAPBody soapBody = soapMessage.getSOAPBody();
         SOAPElement rpcElement = (SOAPElement)soapBody.getChildElements().next();

         SOAPFactory factory = SOAPFactory.newInstance();
         Name rpcName = factory.createName("echoUserType", "ns1", TARGET_NAMESPACE);
         if (rpcName.equals(rpcElement.getElementName()) == false)
            throw new IllegalStateException("Invalid rpc name: " + rpcElement.getElementName());

         SOAPElement paramElement = (SOAPElement)rpcElement.getChildElements().next();
         Name paramName = factory.createName("UserType_1");
         if (paramName.equals(paramElement.getElementName()) == false)
            throw new IllegalStateException("Invalid param name: " + paramElement.getElementName());

         /* SOAPElement view of RPCParam not implemented

         SOAPElement msgElement = (SOAPElement)paramElement.getChildElements().next();
         Name msgName = factory.createName("msg");
         if (msgName.equals(msgElement.getElementName()) == false)
            throw new IllegalStateException("Invalid msg name: " + msgElement.getElementName());

         String value = msgElement.getValue();
         msgElement.setValue(value + "|req-ok");
         */

         return true;
      }
      catch (SOAPException e)
      {
         throw new IllegalArgumentException(e.toString());
      }
   }

   public boolean handleResponse(MessageContext msgContext)
   {
      try
      {
         SOAPMessageContext soapContext = (SOAPMessageContext)msgContext;
         SOAPMessage soapMessage = soapContext.getMessage();
         SOAPBody soapBody = soapMessage.getSOAPBody();
         SOAPElement rpcElement = (SOAPElement)soapBody.getChildElements().next();

         SOAPFactory factory = SOAPFactory.newInstance();
         Name rpcName = factory.createName("echoUserTypeResponse", "ns1", TARGET_NAMESPACE);
         if (rpcName.equals(rpcElement.getElementName()) == false)
            throw new IllegalStateException("Invalid rpc name: " + rpcElement.getElementName());

         SOAPElement paramElement = (SOAPElement)rpcElement.getChildElements().next();
         Name paramName = factory.createName("result");
         if (paramName.equals(paramElement.getElementName()) == false)
            throw new IllegalStateException("Invalid param name: " + paramElement.getElementName());

         /* SOAPElement view of RPCParam not implemented

         SOAPElement msgElement = (SOAPElement)paramElement.getChildElements().next();
         Name msgName = factory.createName("msg");
         if (msgName.equals(msgElement.getElementName()) == false)
            throw new IllegalStateException("Invalid msg name: " + msgElement.getElementName());

         String value = msgElement.getValue();
         msgElement.setValue(value + "|res-ok");
         */

         return true;
      }
      catch (SOAPException e)
      {
         throw new IllegalArgumentException(e.toString());
      }
   }
}
