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
package org.jboss.test.ws.jaxrpc.jbws1179;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.jaxrpc.handler.MessageContextJAXRPC;
import org.jboss.ws.core.soap.MessageContextAssociation;

/**
 * 
 * @author darran.lofthouse@jboss.com
 * @since 18-October-2006
 */
public class TestEndpointImpl implements TestEndpoint
{

   public String echoMessage(String message)
   {
      return message;
   }

   public boolean canAccessSession() throws RemoteException
   {
      CommonMessageContext context = MessageContextAssociation.peekMessageContext();
      HttpServletRequest req = (HttpServletRequest)context.get(MessageContextJAXRPC.SERVLET_REQUEST);
      
      HttpSession session = req.getSession(false);
      if (session != null)
         throw new RuntimeException("Session expected not to exist");

      session = req.getSession(true);
      session.setAttribute("Test", "Test String");
      String testString = (String)session.getAttribute("Test");
      if ("Test String".equals(testString) == false)
         throw new RuntimeException("Invalid attribute returned.");

      return true;
   }

}
