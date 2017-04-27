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
package org.jboss.test.ws.jaxws.handlerlifecycle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.jboss.logging.Logger;
import org.jboss.ws.annotation.EndpointConfig;

@WebService(name = "SOAPEndpoint", targetNamespace = "http://org.jboss.ws/jaxws/handlerlifecycle")
@HandlerChain(file = "jaxws-server-handlers.xml")
@SOAPBinding(style = Style.RPC)

@EndpointConfig(configName = "Custom Server Config", configFile = "WEB-INF/jaxws-endpoint-config.xml")
public class SOAPEndpointBean
{
   private static Logger log = Logger.getLogger(SOAPEndpointBean.class);
   
   @Resource
   public WebServiceContext wsContext;

   @WebMethod
   public String runTest(String testName)
   {
      log.info("runTest: " + testName);
      
      try
      {
         Method method = getClass().getDeclaredMethod(testName, new Class[]{});
         method.invoke(this, new Object[]{});
      }
      catch (NoSuchMethodException ex)
      {
         // ignore this
      }
      catch (InvocationTargetException ex)
      {
         throw new IllegalStateException("Cannot invoke test method: " + testName, ex.getTargetException());
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Cannot test method: " + testName);
      }
      
      return testName + "Response";
   }

   public void testPropertyScoping()
   {
      MessageContext msgContext = wsContext.getMessageContext();
      if (msgContext.get("server-handler-prop") != null)
         throw new IllegalStateException("Found server-handler-prop");
      
      if (msgContext.get("server-app-prop") != Boolean.TRUE)
         throw new IllegalStateException("Cannot find server-app-prop");
   }
}
