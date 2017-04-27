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
package org.jboss.test.ws.interop.wsa;

import org.jboss.ws.annotation.EndpointConfig;

import javax.jws.WebMethod;
import javax.jws.Oneway;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author Heiko.Braun@jboss.org
 * @since 23.01.2007
 */
@WebService(
   name = "Notify",
   targetNamespace = "http://tempuri.org/",
   endpointInterface = "org.jboss.test.ws.interop.wsa.Notify",
   wsdlLocation = "/WEB-INF/wsdl/service.wsdl",
   portName = "NotifyPort"
)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@EndpointConfig(configName = "Standard WSAddressing Endpoint")
public class NotifyImpl implements Notify {

   @WebMethod(operationName = "Notify", action = "http://example.org/action/notify")
   @Oneway
   public void notify(@WebParam(name = "NotifyMessage", targetNamespace = "http://example.org/notify", partName = "parameters") NotifyMessage parameters) {
      System.out.println("NotifyImpl: " + parameters.getNotify().getValue());
   }
}
