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
package org.jboss.test.ws.jaxws.samples.dar;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.Action;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;

import org.jboss.logging.Logger;
import org.jboss.test.ws.jaxws.samples.dar.generated.reply.DarReplyEndpoint;
import org.jboss.test.ws.jaxws.samples.dar.generated.reply.DarResponse;
import org.jboss.test.ws.jaxws.samples.dar.generated.reply.Route;
import org.jboss.test.ws.jaxws.samples.dar.generated.reply.Stop;
import org.jboss.ws.annotation.EndpointConfig;

/**
 * The client endpoint receiving DAR responses
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
@WebService(name = "DarReplyEndpoint",
            targetNamespace = "http://org.jboss.ws/samples/dar",
            endpointInterface = "org.jboss.test.ws.jaxws.samples.dar.generated.reply.DarReplyEndpoint",
            wsdlLocation = "/WEB-INF/wsdl/reply.wsdl",
            serviceName = "DarReplyService")
@SOAPBinding(style = Style.RPC,
             use = Use.LITERAL)
@EndpointConfig(configName = "Standard WSAddressing Endpoint")
public class DarReplyEndpointImpl implements DarReplyEndpoint
{
   @Resource
   private WebServiceContext ctx;
   private static Logger log = Logger.getLogger(DarReplyEndpointImpl.class);
   
   @WebMethod(action = "http://org.jboss.test.ws.jaxws.samples.dar/action/receiveIn")
   @Oneway
   @Action(input = "http://org.jboss.test.ws.jaxws.samples.dar/action/receiveIn")
   public void receive(DarResponse arg0)
   {
      AddressingProperties props = (AddressingProperties)ctx.getMessageContext().get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
      log.info("Result received; relationship message id: " + props.getRelatesTo()[0].getID());
      List<Route> routes = arg0.getRoutes();
      for (Route route : routes)
      {
         log.info(route.getBusId() + ": ");
         StringBuilder sb = new StringBuilder();
         for (Stop stop : route.getStops())
         {
            sb.append(stop.getNode() + " ");
         }
         log.info(sb.toString());
      }
   }

}
