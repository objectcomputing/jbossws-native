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
package org.jboss.test.ws.jaxrpc.jbws1386;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.jboss.logging.Logger;

public class RequestServiceBean implements RequestService, ServiceLifecycle
{
   private Logger log = Logger.getLogger(this.getClass());
   
   private ServletEndpointContext sepCtx;

   public Message processClaim(Message message) throws RemoteException
   {
      log.info("message: " + message);
      
      // Test the value in the soap message
      try
      {
         SOAPMessageContext msgCtx = (SOAPMessageContext)sepCtx.getMessageContext();
         SOAPBody body = msgCtx.getMessage().getSOAPBody();
         QName rpcOpName = new QName("http://org.jboss.test.ws/jbws1386", "processClaim");
         SOAPElement bodyEl = (SOAPElement)body.getChildElements(rpcOpName).next();
         SOAPElement soapEl = (SOAPElement)bodyEl.getChildElements(new QName("Message_1")).next();
         soapEl = (SOAPElement)soapEl.getChildElements(new QName("data")).next();
         String value = soapEl.getValue();
         if (value.equals("YmFzZTY0") == false)
            throw new RuntimeException("Unexpected base64 value: " + value);
      }
      catch (SOAPException ex)
      {
         throw new RuntimeException(ex);
      }
      
      return message;
   }

   public void init(Object context) throws ServiceException
   {
      this.sepCtx = (ServletEndpointContext)context;
   }
   
   public void destroy()
   { }
}
