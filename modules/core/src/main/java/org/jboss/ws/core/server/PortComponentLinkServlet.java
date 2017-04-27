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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.management.EndpointRegistry;
import org.jboss.wsf.spi.management.EndpointRegistryFactory;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;

/**
 * A servlet that reports the serviceURL for a given service ID.
 * <p/>
 * When the web service client ENC is setup, it may contain port-component-link
 * entries that point to service endpoints in the same top level deployment.
 * The final serviceURL of those endpoints will become available after the
 * reference to the javax.xml.rpc.Service is bound to JNDI.
 * <p/>
 * When the client does a lookup of the javax.xml.rpc.Service from JNDI the ObjectFactory
 * will contact this servlet for the final serviceURL. It is acceptable that the client
 * wsdl does not contain the correct serviceURL if the client is using the port-component-link element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 29-May-2004
 */
public class PortComponentLinkServlet extends HttpServlet
{
   // provide logging
   private static final Logger log = Logger.getLogger(PortComponentLinkServlet.class);

   protected EndpointRegistry epRegistry;

   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);
      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      epRegistry = spiProvider.getSPI(EndpointRegistryFactory.class).getEndpointRegistry();      
   }

   /**
    * Get the serviceURL as string for a given serviceID.
    */
   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      String pcLink = req.getParameter("pcLink");
      if (pcLink == null)
         throw new IllegalArgumentException("Cannot obtain request parameter 'pcLink'");

      Endpoint endpoint = epRegistry.resolve( new PortComponentResolver(pcLink) );
      if (endpoint == null)
         throw new WSException("Cannot resolve port-component-link: " + pcLink);

      res.setContentType("text/plain");
      PrintWriter out = res.getWriter();

      ServerEndpointMetaData sepMetaData = endpoint.getAttachment(ServerEndpointMetaData.class);
      String endpointAddress = sepMetaData.getEndpointAddress();
      out.println(endpointAddress);

      log.debug("Resolved " + pcLink + " to: " + endpointAddress);
      out.close();
   }
}
