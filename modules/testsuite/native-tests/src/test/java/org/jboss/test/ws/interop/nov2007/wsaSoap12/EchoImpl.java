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
package org.jboss.test.ws.interop.nov2007.wsaSoap12;

import org.jboss.ws.annotation.EndpointConfig;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Action;
import javax.xml.ws.BindingType;

/**
 * @author Alessio Soldano, alessio.soldano@jboss.com
 * @since 31-Oct-2007
 */
@WebService(
   name = "Echo",
   targetNamespace = "http://tempuri.org/",
   wsdlLocation = "/WEB-INF/wsdl/service.wsdl",
   endpointInterface = "org.jboss.test.ws.interop.nov2007.wsaSoap12.Echo",
   portName = "CustomBinding_Echo1"
)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@EndpointConfig(configName = "Standard SOAP 1.2 WSAddressing Endpoint")
@BindingType(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class EchoImpl implements Echo {
   
   @WebMethod(operationName = "Echo", action = "http://example.org/action/echoIn")
   @WebResult(name = "echoOut", targetNamespace = "http://example.org/echo", partName = "echoOut")
   @Action(input = "http://example.org/action/echoIn", output = "http://example.org/action/echoOut")
   public String echo(
         @WebParam(name = "echoIn", targetNamespace = "http://example.org/echo", partName = "echoIn")
         String echoIn)
   {
      return echoIn;
   }
}
