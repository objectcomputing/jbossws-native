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
package org.jboss.test.ws.jaxws.wseventing;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/**
 * Simulates an eventsink endpoint.
 * 
 * @author Heiko Braun, <heiko@openj.net>
 * @since 05-Jan-2006
 */
public class EventSinkServlet extends HttpServlet
{
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      doRequest(request, response);
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      doRequest(request, response);
   }

   protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {

      StringBuffer sb = new StringBuffer();
      BufferedReader reader = request.getReader();

      String s = reader.readLine();
      sb.append(s);
      while (s != null)
      {
         s = reader.readLine();
         if (s != null)
            sb.append(s);
      }

      reader.close();

      Element soapEl = DOMUtils.parse(sb.toString());
      String pretty = DOMWriter.printNode(soapEl, true);
      getServletContext().log("EventSink received: \n" + pretty);
   }
}
