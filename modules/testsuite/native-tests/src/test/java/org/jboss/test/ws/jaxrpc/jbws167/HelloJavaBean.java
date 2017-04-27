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
package org.jboss.test.ws.jaxrpc.jbws167;

import java.util.Arrays;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;

public class HelloJavaBean implements Hello, ServiceLifecycle
{
   private ServletEndpointContext context;

   private Logger log = Logger.getLogger(HelloJavaBean.class);

   public String hello(String in0)
   {
      log.info("hello: " + in0);

      SOAPMessageContext msgContext = (SOAPMessageContext)context.getMessageContext();
      SOAPMessage soapMessage = msgContext.getMessage();

      String[] soapAction = soapMessage.getMimeHeaders().getHeader("SOAPAction");
      log.info("soapAction: " + Arrays.asList(soapAction));

      String retStr;
      if (soapAction != null && soapAction.length == 1 && soapAction[0].equals("\"/foo/bar\""))
      {
         retStr = "[pass]";
      }
      else
      {
         retStr = "[failed] SOAPAction = " + Arrays.asList(soapAction);
      }

      return retStr;
   }

   // ServiceLifecycle *******************************************************************************************

   public void init(Object context) throws ServiceException
   {
      this.context = (ServletEndpointContext)context;
   }

   public void destroy()
   {
      this.context = null;
   }
}
