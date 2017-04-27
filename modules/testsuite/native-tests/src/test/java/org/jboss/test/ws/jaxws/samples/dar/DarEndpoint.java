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

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.wsf.spi.annotation.AuthMethod;
import org.jboss.wsf.spi.annotation.TransportGuarantee;
import org.jboss.wsf.spi.annotation.WebContext;

/**
 * Performs DAR route optimization
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
@Stateless
@SOAPBinding
(
   style = SOAPBinding.Style.RPC,
   use = SOAPBinding.Use.LITERAL
)
@WebService
(
   name = "DarEndpoint",
   serviceName = "DarService",
   targetNamespace = "http://org.jboss.ws/samples/dar"
)
@WebContext
(
   contextRoot = "/dar",
   urlPattern = "/*",
   authMethod = AuthMethod.BASIC,
   transportGuarantee = TransportGuarantee.NONE,
   secureWSDLAccess = false
)
public class DarEndpoint
{
   @WebMethod(operationName = "process", action = "http://org.jboss.test.ws.jaxws.samples.dar/action/processIn")
   public DarResponse process(DarRequest request)
   {
      DarProcessor processor = new DarProcessor();
      return processor.process(request);
   }
}
