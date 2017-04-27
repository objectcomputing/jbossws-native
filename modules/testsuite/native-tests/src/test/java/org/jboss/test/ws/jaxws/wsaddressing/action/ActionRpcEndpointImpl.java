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
package org.jboss.test.ws.jaxws.wsaddressing.action;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.logging.Logger;
import org.jboss.ws.annotation.EndpointConfig;

/**
 * WS-Addressing service endpoint
 *
 * @author Thomas.Diesler@jboss.org
 *
 * @since 24-Nov-2005
 */
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebService(
		name = "ActionEndpoint",
		targetNamespace = "http://org.jboss.ws/addressing/action",		
		endpointInterface = "org.jboss.test.ws.jaxws.wsaddressing.action.ActionEndpoint"
)
@EndpointConfig(configName = "Standard WSAddressing Endpoint")
public class ActionRpcEndpointImpl implements ActionEndpoint
{
   // provide logging
   private static Logger log = Logger.getLogger(ActionRpcEndpointImpl.class);

   @WebMethod(action = "urn:wsa-action-foo")
   @WebResult(name = "result")
   public String foo(@WebParam(name = "String_1") String item)
   {
      log.info("foo: " + item);
      return "foo:" + item;
   }

   @WebMethod(action = "urn:wsa-action-bar")
   @WebResult(name="result")
   public String bar(@WebParam(name = "String_1") String item)
   {
      log.info("bar: " + item);
      return "bar:" + item;
   }
}
