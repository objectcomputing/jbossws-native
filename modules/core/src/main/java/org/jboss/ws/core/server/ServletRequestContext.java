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
package org.jboss.ws.core.server;

import org.jboss.wsf.spi.invocation.InvocationContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Implementation of ServletEndpointContext
 *
 * @author Thomas.Diesler@jboss.org
 */
public class ServletRequestContext extends InvocationContext
{
   private ServletContext context;
   private HttpServletRequest request;
   private HttpServletResponse response;

   public ServletRequestContext(ServletContext context, HttpServletRequest request, HttpServletResponse response)
   {
      this.context = context;
      this.request = request;
      this.response = response;
   }

   public HttpSession getHttpSession()
   {
      return request.getSession(false);
   }

   public ServletContext getServletContext()
   {
      return context;
   }

   public HttpServletRequest getHttpServletRequest()
   {
      return request;
   }

   public HttpServletResponse getHttpServletResponse()
   {
      return response;
   }
}
