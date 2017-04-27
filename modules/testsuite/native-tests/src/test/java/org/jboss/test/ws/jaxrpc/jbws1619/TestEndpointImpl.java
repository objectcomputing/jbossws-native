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
package org.jboss.test.ws.jaxrpc.jbws1619;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;

import org.jboss.logging.Logger;

public class TestEndpointImpl implements TestEndpoint, ServiceLifecycle
{
   private Logger log = Logger.getLogger(TestEndpointImpl.class);
   
   private ServletEndpointContext context;

   public String echoString(String message) throws RemoteException
   {
      HttpSession httpSession = null;
      if ("Use ServletEndpointContext".equals(message))
      {
         httpSession = context.getHttpSession();
      }
      else if ("Use MessageContext".equals(message))
      {
         MessageContext msgContext = context.getMessageContext();
         HttpServletRequest req = (HttpServletRequest)msgContext.getProperty("javax.xml.ws.servlet.request");
         httpSession = req.getSession(true);
      }
      
      log.info("echoString: " + httpSession);
      return "httpSession: " + httpSession;
   }

   public void init(Object context) throws ServiceException
   {
      this.context = (ServletEndpointContext)context;
   }

   public void destroy()
   {
   }
}
