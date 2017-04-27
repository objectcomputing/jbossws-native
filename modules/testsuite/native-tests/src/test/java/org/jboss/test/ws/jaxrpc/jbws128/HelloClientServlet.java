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
package org.jboss.test.ws.jaxrpc.jbws128;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.Service;

import org.jboss.logging.Logger;

public class HelloClientServlet extends HttpServlet
{
   private Logger log = Logger.getLogger(HelloClientServlet.class);

   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      try
      {
         InitialContext iniCtx = new InitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
         Hello hello = (Hello)service.getPort(Hello.class);

         log.info("Got port: " + hello);

         res.setContentType("text/plain");
         PrintWriter out = res.getWriter();
         out.print(hello.hello("Kermit"));
         out.close();
      }
      catch (Exception e)
      {
         log.error("Cannot access service", e);
      }
   }
}
