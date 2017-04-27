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

import org.jboss.ws.extensions.eventing.mgmt.EventDispatcher;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URI;

/**
 * Test the in vm event dispatching
 */
public class InVMServlet extends HttpServlet
{

   private final static String notification =
      "<WindReport type='critical'>\n" +
      "    <Date>030701</Date>\n" +
      "    <Time>0041</Time>\n" +
      "    <Speed>65</Speed>\n" +
      "    <Location>BRADENTON BEACH</Location>\n" +
      "    <County>MANATEE</County>\n" +
      "    <State>FL</State>\n" +
      "    <Lat>2746</Lat>\n" +
      "    <Long>8270</Long>\n" +
      "    <Comments xml:lang='en-US' >\n" +
      "        Should be a LOCAL invocation\n" +
      "    </Comments>\n" +
      "</WindReport>";

   protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
   {
      try
      {
         InitialContext iniCtx = new InitialContext();
         EventDispatcher delegate = (EventDispatcher)
               iniCtx.lookup(EventingConstants.DISPATCHER_JNDI_NAME);

         Element payload = DOMUtils.parse(notification);
         delegate.dispatch(new URI("http://www.jboss.org/wind/Warnings"), payload);

         httpServletResponse.getWriter().print("Notification successful");
      }
      catch (Exception e)
      {
         throw new ServletException("Failed to do in VM dispatching", e);
      }
   }
}
