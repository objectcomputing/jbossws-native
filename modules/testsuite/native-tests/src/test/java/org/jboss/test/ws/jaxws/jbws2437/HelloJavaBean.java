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
package org.jboss.test.ws.jaxws.jbws2437;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.logging.Logger;
import org.jboss.wsf.spi.annotation.WebContext;

@Stateless
@WebService(name = "Hello", serviceName = "HelloService", targetNamespace = "http://org.jboss.ws/jaxws/jbws2437")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebContext(contextRoot="jaxws-jbws2437", urlPattern="/*")
public class HelloJavaBean implements Hello
{
   private Logger log = Logger.getLogger(HelloJavaBean.class);

   @WebMethod
   public String echo(@WebParam(name = "user") String in0)
   {
      log.info(in0);
      return in0;
   }
}
