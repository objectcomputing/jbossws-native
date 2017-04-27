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
package org.jboss.test.ws.jaxws.samples.news;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.wsf.spi.annotation.AuthMethod;
import org.jboss.wsf.spi.annotation.TransportGuarantee;
import org.jboss.wsf.spi.annotation.WebContext;
import org.jboss.ejb3.annotation.SecurityDomain;

@Stateless
@SecurityDomain("JBossWS")
@SOAPBinding
(
   style = SOAPBinding.Style.RPC,
   use = SOAPBinding.Use.LITERAL
)
@WebService
(
   name = "NewspaperSWAEndpoint",
   serviceName = "NewspaperSWAService",
   targetNamespace = "http://org.jboss.ws/samples/news",
   endpointInterface = "org.jboss.test.ws.jaxws.samples.news.NewspaperSWA"
)
@WebContext
(
   contextRoot = "/news",
   urlPattern = "/newspaper/swa",
   authMethod = AuthMethod.BASIC,
   transportGuarantee = TransportGuarantee.CONFIDENTIAL,
   secureWSDLAccess = false
)
public class SecureNewspaperSWAEndpoint extends AbstractNewspaperSWAEndpoint implements NewspaperSWA
{
   
}
