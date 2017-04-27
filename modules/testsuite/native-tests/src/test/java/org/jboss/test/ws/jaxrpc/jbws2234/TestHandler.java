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
package org.jboss.test.ws.jaxrpc.jbws2234;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

/**
 * @author darran.lofthouse@jboss.com
 * @since June 21, 2008
 */
public class TestHandler extends GenericHandler
{

   private static final String SOAP_1_2 = "http://www.w3.org/2003/05/soap-envelope";

   @Override
   public QName[] getHeaders()
   {
      return null;
   }

   public boolean handleMessage(final MessageContext msgContext)
   {
      try
      {
         SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
         SOAPPart part = soapMessage.getSOAPPart();
         SOAPEnvelope envelope = part.getEnvelope();

         String namespace = envelope.getNamespaceURI();
         if (SOAP_1_2.equals(namespace) == false)
         {
            throw new RuntimeException("Expected '" + SOAP_1_2 + "' namespace, actual '" + namespace + "'");
         }
      }
      catch (SOAPException e)
      {
         throw new RuntimeException(e);
      }
      return true;
   }

   @Override
   public boolean handleFault(final MessageContext msgContext)
   {
      return handleMessage(msgContext);
   }

   @Override
   public boolean handleRequest(MessageContext msgContext)
   {
      return handleMessage(msgContext);
   }

   @Override
   public boolean handleResponse(MessageContext msgContext)
   {
      return handleMessage(msgContext);
   }

}
