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
package org.jboss.test.ws.jaxws.jbws1850;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.ws.annotation.Documentation;
import org.jboss.wsf.spi.annotation.WebContext;

@WebService(name = "TestEndpoint", serviceName = "TestEndpointService", targetNamespace = "http://org.jboss.ws/jbws1850")
@Stateless(name = "TestEndpoint")
@Documentation(content = "This is a test service doing nothing special")
@WebContext(contextRoot="/jaxws-jbws1850", urlPattern="/*")
public class TestServiceImpl
{
   @WebMethod
   @Documentation(content = "This is the useless test operation of the test service")
   public String test(String first, String second)
   {
      return second;
   }
}
