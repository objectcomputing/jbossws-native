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
package org.jboss.test.ws.jaxws.wsdd;

import org.jboss.ws.core.soap.TextImpl;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.wsf.common.handler.GenericSOAPHandler;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.WebServiceException;
import javax.xml.soap.*;

/**
 * Inbound the handler appends to the echo message,
 * outbound it changes the boolean value of the checkMTOM invocation. 
 * 
 * @author Heiko.Braun@jboss.org
 * @since Mar 12, 2007
 */
public class CustomHandler extends GenericSOAPHandler {

   protected boolean handleInbound(MessageContext msgContext)
   {
      try
      {
         SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
         SOAPBody soapBody = soapMessage.getSOAPBody();

         SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.getChildElements().next();
         if(soapBodyElement.getChildElements().hasNext())
         {
            SOAPElement payload = (SOAPElement)soapBodyElement.getChildElements().next();
            SOAPElement message = (SOAPElement)payload.getChildElements().next();
            String value = message.getValue();
            message.setValue(value + "World");
         }
      }
      catch (SOAPException e)
      {
         throw  new WebServiceException(e);
      }
      return true;
   }

   protected boolean handleOutbound(MessageContext msgContext)
   {
      try
      {
         SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
         SOAPBody soapBody = soapMessage.getSOAPBody();

         SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.getChildElements().next();
         if(soapBodyElement.getChildElements().hasNext())
         {
            SOAPElement payload = (SOAPElement)soapBodyElement.getChildElements().next();
            if(soapBodyElement.getElementQName().getLocalPart().indexOf("MTOM") != -1)
            {
               TextImpl response = (TextImpl)payload.getChildElements().next();
               String value = response.getValue();
               response.setValue( String.valueOf(XOPContext.isMTOMEnabled()) );
            }

         }
      }
      catch (SOAPException e)
      {
         throw  new WebServiceException(e);
      }

      return true;
   }
}
