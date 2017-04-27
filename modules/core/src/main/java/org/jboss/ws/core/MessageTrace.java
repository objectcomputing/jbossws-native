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
package org.jboss.ws.core;

import java.io.ByteArrayInputStream;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamSource;

import org.jboss.logging.Logger;
import org.jboss.ws.core.soap.SOAPElementImpl;
import org.jboss.ws.core.soap.SOAPElementWriter;
import org.jboss.ws.core.soap.XMLFragment;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/**
 * Trace incomming/outgoing messages
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Apr-2007
 */
public final class MessageTrace
{
   private static final Logger msgLog = Logger.getLogger(MessageTrace.class);

   private MessageTrace()
   {
      // forbidden constructor
   }

   public static void traceMessage(String messagePrefix, Object message)
   {
      if (!msgLog.isTraceEnabled()) return;
      
      if (message instanceof SOAPMessage)
      {
         try
         {
            SOAPEnvelope soapEnv = ((SOAPMessage)message).getSOAPPart().getEnvelope();
            if (soapEnv != null)
            {
               String envStr = SOAPElementWriter.writeElement((SOAPElementImpl)soapEnv, true);
               msgLog.trace(messagePrefix + "\n" + envStr);
            }
         }
         catch (SOAPException ex)
         {
            msgLog.error("Cannot trace SOAPMessage", ex);
         }
      }
      else if (message instanceof HTTPMessageImpl)
      {
         HTTPMessageImpl httpMessage = (HTTPMessageImpl)message;
         Element root = httpMessage.getXmlFragment().toElement();
         String xmlString = DOMWriter.printNode(root, true);
         msgLog.trace(messagePrefix + "\n" + xmlString);
      }
      else if (message instanceof byte[])
      {
         Element root = new XMLFragment(new StreamSource(new ByteArrayInputStream((byte[])message))).toElement();
         String xmlString = DOMWriter.printNode(root, true);
         msgLog.trace(messagePrefix + "\n" + xmlString);
      }
      else if (message instanceof String)
      {
         Element root = new XMLFragment(new StreamSource(new ByteArrayInputStream(((String)message).getBytes()))).toElement();
         String xmlString = DOMWriter.printNode(root, true);
         msgLog.trace(messagePrefix + "\n" + xmlString);
      }
      else
      {
          msgLog.warn("Unsupported message type: " + message);
      }
   }
}
