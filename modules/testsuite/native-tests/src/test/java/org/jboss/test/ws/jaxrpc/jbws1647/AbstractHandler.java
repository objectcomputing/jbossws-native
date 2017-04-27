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
package org.jboss.test.ws.jaxrpc.jbws1647;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;
import org.jboss.wsf.common.DOMWriter;

/**
 * 
 * @author darran.lofthouse@jboss.com
 * @since 15 May 2007
 */
public abstract class AbstractHandler extends GenericHandler
{
   private static final Logger log = Logger.getLogger(AbstractHandler.class);

   public abstract String getMessageBody();

   public boolean handleRequest(MessageContext msgContext)
   {
      log.info("handleRequest");

      SOAPMessageContext messageContext = (SOAPMessageContext)msgContext;
      SOAPMessage soapMessage = messageContext.getMessage();

      try
      {
         SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
         String wasEnv = DOMWriter.printNode(soapEnv, false);
         String wasBody = wasEnv.substring(wasEnv.indexOf("<env:Body>"));
         wasBody = wasBody.substring(0, wasBody.indexOf("</env:Envelope>"));
         if (wasBody.equals(getMessageBody()) == false)
         {
            log.error("Exp Body: " + getMessageBody());
            log.error("Was Body: " + wasBody);
            throw new RuntimeException("Received message does not contain expected soap body.");
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException("Unable to process SOAPMessage", e);
      }
      return true;
   }

   public QName[] getHeaders()
   {
      return null;
   }
}
