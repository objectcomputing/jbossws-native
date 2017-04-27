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
package org.jboss.test.ws.jaxrpc.jbws812;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;
import org.jboss.wsf.common.DOMWriter;

public class ServerHandler extends GenericHandler
{
   private Logger log = Logger.getLogger(ServerHandler.class);

   public QName[] getHeaders()
   {
      return new QName[]{};
   }

   public boolean handleRequest(MessageContext msgContext)
   {
      try
      {
         SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
         SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
         log.info(DOMWriter.printNode(soapEnvelope, true));
      }
      catch (SOAPException ex)
      {
         throw new RuntimeException(ex);
      }
      return true;
   }

   public boolean handleResponse(MessageContext msgContext)
   {
      try
      {
         SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
         SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
         log.info(DOMWriter.printNode(soapEnvelope, true));
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
      return true;
   }
}
