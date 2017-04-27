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
package org.jboss.test.ws.jaxrpc.samples.dynamichandler;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;

/**
 * A server side handler for the HandlerTestCase
 *
 * @author Thomas.Diesler@jboss.org
 * @since 02-Feb-2005
 */
public class ServerSideHandler extends GenericHandler
{
   // Provide logging
   private static Logger log = Logger.getLogger(ServerSideHandler.class);

   private QName[] headers = new QName[]{};

   public QName[] getHeaders()
   {
      return headers;
   }

   public boolean handleRequest(MessageContext msgContext)
   {
      log.info("handleRequest");

      try
      {
         SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
         SOAPHeader soapHeader = soapMessage.getSOAPHeader();
         SOAPBody soapBody = soapMessage.getSOAPBody();

         SOAPFactory soapFactory = SOAPFactory.newInstance();
         Name headerName = soapFactory.createName("ServerRequest", "ns1", "http://somens");
         SOAPHeaderElement she = soapHeader.addHeaderElement(headerName);
         she.setValue("true");

         SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.getChildElements().next();
         SOAPElement soapElement = (SOAPElement)soapBodyElement.getChildElements().next();
         String value = soapElement.getValue();
         soapElement.setValue(value + "|ServerRequest");
      }
      catch (SOAPException e)
      {
         throw  new JAXRPCException(e);
      }

      return true;
   }

   public boolean handleResponse(MessageContext msgContext)
   {
      log.info("handleResponse");

      try
      {
         SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
         SOAPHeader soapHeader = soapMessage.getSOAPHeader();
         SOAPBody soapBody = soapMessage.getSOAPBody();

         SOAPFactory soapFactory = SOAPFactory.newInstance();
         Name headerName = soapFactory.createName("ServerResponse", "ns1", "http://somens");
         SOAPHeaderElement she = soapHeader.addHeaderElement(headerName);
         she.setValue("true");

         SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.getChildElements().next();
         SOAPElement soapElement = (SOAPElement)soapBodyElement.getChildElements().next();
         String value = soapElement.getValue();
         soapElement.setValue(value + "|ServerResponse");
      }
      catch (SOAPException e)
      {
         throw  new JAXRPCException(e);
      }

      return true;
   }
}
