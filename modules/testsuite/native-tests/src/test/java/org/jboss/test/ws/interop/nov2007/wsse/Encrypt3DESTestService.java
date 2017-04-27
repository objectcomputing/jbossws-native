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
package org.jboss.test.ws.interop.nov2007.wsse;

import javax.jws.WebService;

import org.jboss.ws.annotation.EndpointConfig;


/**
 * WCF Interoperability Plug-fest - November 2007
 * 
 * IPingService test implementation
 * 
 * @author Alessio Soldano <alessio.soldano@jboss.com>
 * 
 * @since 29-Oct-2007
 */
@WebService(
      wsdlLocation = "WEB-INF/wsdl/WsSecurity10.wsdl", 
      serviceName = "PingService10",
      name = "IPingService",
      targetNamespace = "http://InteropBaseAddress/interop",
      endpointInterface = "org.jboss.test.ws.interop.nov2007.wsse.IPingService",
      portName = "MutualCertificate10SignEncryptRsa15TripleDes_IPingService")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class Encrypt3DESTestService extends TestService implements IPingService
{
   
}
