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
package org.jboss.test.ws.jaxws.samples.wssecurity;

import java.security.Principal;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.WebServiceContext;

import org.jboss.logging.Logger;
import org.jboss.ws.annotation.EndpointConfig;

/**
 * The SEI implementation used by the SimpleUsernameTestCase
 *
 * @author <a href="mailto:mageshbk@jboss.com">Magesh Kumar B</a>
 * @author Thomas.Diesler@jboss.com
 * @since 15-Aug-2007
 */
@WebService(name = "UsernameEndpoint", serviceName = "UsernameService", targetNamespace = "http://org.jboss.ws/samples/wssecurity")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class UsernameBean
{
   private Logger log = Logger.getLogger(UsernameBean.class);

   @Resource
   WebServiceContext wsCtx;

   @WebMethod
   @WebResult(partName = "return")
   public String getUsernameToken()
   {
      Principal principal = wsCtx.getUserPrincipal();
      log.info("getUsernameToken: " + principal);
      return principal.toString();
   }
}
