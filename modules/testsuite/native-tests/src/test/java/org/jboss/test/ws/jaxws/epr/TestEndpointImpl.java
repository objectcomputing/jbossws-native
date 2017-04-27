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
package org.jboss.test.ws.jaxws.epr;

import java.io.ByteArrayOutputStream;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.ReferenceParameters;
import javax.xml.ws.addressing.soap.SOAPAddressingProperties;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.logging.Logger;
import org.jboss.ws.annotation.EndpointConfig;

@WebService(serviceName = "TestEndpointService", name = "TestEndpoint", targetNamespace = "http://org.jboss.ws/epr")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Stateless
@EndpointConfig(configName = "Standard WSAddressing Endpoint")
public class TestEndpointImpl implements TestEndpoint
{
   // provide logging
   private final static Logger log = Logger.getLogger(TestEndpointImpl.class);

   @Resource
   WebServiceContext context;

   @WebMethod
   public String echo(String input)
   {
      try
      {
         SOAPMessageContext msgContext = (SOAPMessageContext)context.getMessageContext();

         // log message
         SOAPMessage soapMessage = msgContext.getMessage();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         soapMessage.writeTo(baos);
         log.info(new String(baos.toByteArray()));
         
         SOAPAddressingProperties addrProps = (SOAPAddressingProperties)msgContext.get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
         ReferenceParameters refParams = addrProps.getReferenceParameters();
         for (Object refParam : refParams.getElements())
         {
            input += "|" + ((SOAPElement)refParam).getValue();
         }
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
      return input;
   }
}
