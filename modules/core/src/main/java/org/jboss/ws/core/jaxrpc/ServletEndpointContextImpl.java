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
package org.jboss.ws.core.jaxrpc;

import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.server.ServletEndpointContext;

import org.jboss.ws.core.server.ServletRequestContext;
import org.jboss.ws.core.soap.MessageContextAssociation;

/**
 * Implementation of <code>ServletEndpointContext</code>
 *
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 */
public class ServletEndpointContextImpl implements ServletEndpointContext
{
   private ServletContext context;
   private HttpServletRequest request;
   private HttpServletResponse response;

   public ServletEndpointContextImpl(ServletRequestContext context)
   {
      this.context = context.getServletContext();
      this.request = context.getHttpServletRequest();
      this.response = context.getHttpServletResponse();
   }

   /**
    * The getHttpSession method returns the current HTTP session (as a javax.servlet.http.HTTPSession).
    * When invoked by the service endpoint within a remote method implementation, the getHttpSession returns the HTTP
    * session associated currently with this method invocation. This method returns null if there is no HTTP session
    * currently active and associated with this service endpoint. An endpoint class should not rely on an active HTTP
    * session being always there; the underlying JAX-RPC runtime system is responsible for managing whether or not there
    * is an active HTTP session.
    *
    * The getHttpSession method throws JAXRPCException if invoked by an non HTTP bound endpoint.
    *
    * @return The HTTP session associated with the current invocation or null if there is no active session.
    */
   public HttpSession getHttpSession()
   {
      // [JBWS-1619] ServletEndpointContext.getHttpSession has an incorrect implementation
      return request.getSession(false);
   }

   public MessageContext getMessageContext()
   {
      return (MessageContext)MessageContextAssociation.peekMessageContext();
   }

   public ServletContext getServletContext()
   {
      return context;
   }

   public Principal getUserPrincipal()
   {
      return request.getUserPrincipal();
   }

   public boolean isUserInRole(String role)
   {
      return request.isUserInRole(role);
   }

   // BEGIN non-standard access methods

   public HttpServletRequest getHttpServletRequest()
   {
      return request;
   }

   public HttpServletResponse getHttpServletResponse()
   {
      return response;
   }

   // END non-standard access methods
}
