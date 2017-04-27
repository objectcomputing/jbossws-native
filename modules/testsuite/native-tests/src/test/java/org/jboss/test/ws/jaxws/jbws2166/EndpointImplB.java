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
package org.jboss.test.ws.jaxws.jbws2166;

import javax.ejb.Stateless;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.soap.Addressing;

import org.jboss.logging.Logger;
import org.jboss.wsf.spi.annotation.WebContext;

@Stateless
@WebService(name = "EndpointB", serviceName = "EndpointBService", targetNamespace = "http://org.jboss.ws/jbws2166")
@WebContext(contextRoot = "/jaxws-jbws2166-B", urlPattern = "/*")
@Addressing
public class EndpointImplB implements EndpointB
{
   private Logger log = Logger.getLogger(EndpointImplB.class);
   private static String lastString;
   
   @Oneway
   @WebMethod(action="echo")
   public void echo(String s)
   {
      log.info("ECHO: " + s);
      lastString = s;
   }
   
   @WebMethod
   public String getString()
   {
      return lastString;
   }
}
