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
package org.jboss.test.ws.jaxrpc.enventry;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;

abstract public class EnvEntryHandler extends GenericHandler
{
   // Provide logging
   private static Logger log = Logger.getLogger(EnvEntryHandler.class);

   private QName[] headers = new QName[]{};

   public QName[] getHeaders()
   {
      return headers;
   }

   public boolean handleRequest(MessageContext msgContext)
   {
      String value = handleRequestAndResponse(msgContext);
      log.info("handleRequest: " + value);
      return true;
   }

   public boolean handleResponse(MessageContext msgContext)
   {
      String value = handleRequestAndResponse(msgContext);
      log.info("handleResponse: " + value);
      return true;
   }

   private String handleRequestAndResponse(MessageContext msgContext)
   {
      String value = null;
      try
      {
         SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
         SOAPBody soapBody = soapMessage.getSOAPBody();

         SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.getChildElements().next();
         SOAPElement soapElement = (SOAPElement)soapBodyElement.getChildElements().next();
         value = soapElement.getValue();
         
         InitialContext ic = getInitialContext();
         String strValue = (String)ic.lookup("java:comp/env/jsr109/entry1");
         Integer intValue = (Integer)ic.lookup("java:comp/env/jsr109/entry2");
         
         value = value + ":" + getHandlerName() + ":" + strValue + ":" + intValue;
         soapElement.setValue(value);
      }
      catch (Exception e)
      {
         throw  new JAXRPCException(e);
      }

      return value;
   }
  
   private String getHandlerName()
   {
      String handlerName = getClass().getName();
      return handlerName.substring(handlerName.lastIndexOf(".") + 1);
   }

   abstract public InitialContext getInitialContext() throws NamingException;

   /**
    * Get the JBoss server host from system property "jboss.bind.address"
    * This defaults to "" + getServerHost() + ""
    */
   public String getServerHost()
   {
      String hostName = System.getProperty("jboss.bind.address", "localhost");
      return hostName;
   }
}
