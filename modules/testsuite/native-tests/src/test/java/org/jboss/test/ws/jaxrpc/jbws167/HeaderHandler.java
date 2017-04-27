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
package org.jboss.test.ws.jaxrpc.jbws167;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.MimeHeaders;

import org.jboss.logging.Logger;

public class HeaderHandler extends GenericHandler
{
   // provide logging
   private static Logger log = Logger.getLogger(HeaderHandler.class);
   
   protected QName[] headers;

   public QName[] getHeaders()
   {
      return headers;
   }

   public void init(HandlerInfo config)
   {
      headers = config.getHeaders();
   }

   public void destroy()
   {
   }

   public boolean handleRequest(MessageContext msgContext)
   {
      return true;
   }

   public boolean handleResponse(MessageContext msgContext)
   {
      log.info("handleResponse");
      SOAPMessageContext context = (SOAPMessageContext) msgContext;
      MimeHeaders headers = context.getMessage().getMimeHeaders();
      headers.addHeader("server-test-header", "1");
      headers.addHeader("server-test-header", "2");
      headers.addHeader("server-test-header", "3");
      return true;
   }

   public boolean handleFault(MessageContext msgContext)
   {
      return true;
   }
}
