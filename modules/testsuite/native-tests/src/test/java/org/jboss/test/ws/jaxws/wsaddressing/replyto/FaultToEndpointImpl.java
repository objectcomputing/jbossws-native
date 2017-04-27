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
package org.jboss.test.ws.jaxws.wsaddressing.replyto;

import java.rmi.RemoteException;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.logging.Logger;


/**
 * WS-Addressing service endpoint
 *
 * @author Thomas.Diesler@jboss.org
 *
 * @since 24-Nov-2005
 */
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.BARE)
@WebService(name = "FaultToEndpoint", serviceName = "FaultToEndpointService", targetNamespace = "http://org.jboss.ws/addressing/replyto")
public class FaultToEndpointImpl
{
   // provide logging
   private static Logger log = Logger.getLogger(FaultToEndpointImpl.class);

   public static String lastFault;

   @Oneway
   @WebMethod
   public void onFault(@WebParam(name="Fault", targetNamespace="http://schemas.xmlsoap.org/soap/envelope/") FaultType fault)
   {
      lastFault = fault.faultString;
   }

   @WebMethod
   public String getLastFault() throws RemoteException
   {
      log.info("getLastFault: " + lastFault);
      return lastFault;
   }
}
