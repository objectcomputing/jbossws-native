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
package org.jboss.test.ws.jaxrpc.jbws456;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

public class TestHandler extends GenericHandler
{
   private static String returnParam;

   public static String getReturnParam()
   {
      return returnParam;
   }

   public QName[] getHeaders()
   {
      return new QName[] {};
   }

   public boolean handleResponse(MessageContext msgContext)
   {
      try
      {
         SOAPFactory factory = SOAPFactory.newInstance();

         SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
         SOAPBody soapBody = soapMessage.getSOAPBody();
         SOAPElement soapElement = (SOAPElement)soapBody.getChildElements().next();
         Name resName = factory.createName("result");
         soapElement = (SOAPElement)soapElement.getChildElements(resName).next();

         Iterator itParams = soapElement.getChildElements();
         soapElement = (SOAPElement)itParams.next();
         String value = soapElement.getValue();
         returnParam = value + " - Processed";

         if (itParams.hasNext())
         {
            soapElement = (SOAPElement)itParams.next();
            throw new IllegalStateException("Unexpected extra parameter: " + soapElement.getValue());
         }

         return true;
      }
      catch (SOAPException e)
      {
         throw new JAXRPCException(e.toString(), e);
      }
   }
}
