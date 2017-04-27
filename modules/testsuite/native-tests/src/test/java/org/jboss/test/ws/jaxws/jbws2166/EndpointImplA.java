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
package org.jboss.test.ws.jaxws.jbws2166;

import java.net.URI;
import java.net.URL;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature;

import org.jboss.logging.Logger;
import org.jboss.wsf.spi.annotation.WebContext;

@Stateless
@WebService(name = "EndpointA", serviceName = "EndpointAService", targetNamespace = "http://org.jboss.ws/jbws2166")
@WebContext(contextRoot = "/jaxws-jbws2166-A", urlPattern = "/*")
@Addressing
public class EndpointImplA implements EndpointA
{
   private Logger log = Logger.getLogger(EndpointImplA.class);
   
   @Resource
   protected WebServiceContext ctx;
   
   @Oneway
   @WebMethod(action="echo")
   public void echo(String s)
   {
      log.info("ECHO: " + s);
      try
      {
         AddressingProperties serverProps = (AddressingProperties)ctx.getMessageContext().get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
         URI endpointUri = serverProps.getReplyTo().getAddress().getURI();
         URL wsdlURL = new URL(endpointUri.toString() + "?wsdl");
         QName serviceName = new QName("http://org.jboss.ws/jbws2166", "EndpointBService");
   
         Service service = Service.create(wsdlURL, serviceName);
         EndpointB port = (EndpointB)service.getPort(EndpointB.class, new AddressingFeature());
         BindingProvider bindingProvider = (BindingProvider)port;

         AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
         AddressingProperties props = builder.newAddressingProperties();
         props.initializeAsReply(serverProps, false);
         props.setAction(builder.newURI("echo"));
         
         bindingProvider.getRequestContext().put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, props);
         port.echo(s);
      }
      catch (Exception e)
      {
         log.error("Error while calling Service B", e);
      }
   }
}
