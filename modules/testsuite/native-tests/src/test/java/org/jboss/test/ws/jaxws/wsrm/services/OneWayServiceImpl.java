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
package org.jboss.test.ws.jaxws.wsrm.services;

import javax.jws.Oneway;
import javax.jws.WebService;
import java.util.Arrays;

@WebService
(
   name = "OneWay",
   serviceName = "OneWayService",
   wsdlLocation = "WEB-INF/wsdl/OneWayService.wsdl",
   targetNamespace = "http://www.jboss.org/jbossws/ws-extensions/wsrm",
   endpointInterface = "org.jboss.test.ws.jaxws.wsrm.services.OneWayServiceIface"
)
public class OneWayServiceImpl implements OneWayServiceIface
{
   @Oneway
   public void method1()
   {
      System.out.println("method1()");
   }

   @Oneway
   public void method2(String s)
   {
      System.out.println("method2(" + s + ")");
   }

   @Oneway
   public void method3(String[] sa)
   {
      System.out.println("method3(" + Arrays.asList(sa) + ")");
   }
}
