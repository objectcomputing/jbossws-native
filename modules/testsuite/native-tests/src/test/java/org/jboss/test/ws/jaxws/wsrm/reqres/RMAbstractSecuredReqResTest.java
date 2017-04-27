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
package org.jboss.test.ws.jaxws.wsrm.reqres;

import static org.jboss.test.ws.jaxws.wsrm.Helper.setAddrProps;

import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.xml.namespace.QName;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import org.jboss.test.ws.jaxws.wsrm.services.SecuredReqResServiceIface;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.extensions.wsrm.api.RMProvider;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Secured reliable JBoss WebService client invoking req/res methods
 *
 * @author richard.opalka@jboss.com
 */
public abstract class RMAbstractSecuredReqResTest extends JBossWSTest
{
   private static final String helloWorldMessage = "Hello World";
   private final String serviceURL = "http://" + getServerHost() + ":8080/jaxws-secured-wsrm/SecuredReqResService";
   private Exception handlerException;
   private boolean asyncHandlerCalled;
   private SecuredReqResServiceIface proxy;
   private static final TimeUnit testTimeUnit = TimeUnit.SECONDS;
   private static final long testWaitPeriod = 300L;
   private static final Executor testExecutor = new ThreadPoolExecutor(
      0, 5, testWaitPeriod, testTimeUnit, new SynchronousQueue<Runnable>()
   );
   
   private enum InvocationType
   {
      SYNC, ASYNC, ASYNC_FUTURE
   }

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      if (proxy == null)
      {
         QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wsrm", "SecuredReqResService");
         URL wsdlURL = new URL(serviceURL + "?wsdl");
         Service service = Service.create(wsdlURL, serviceName);
         service.setExecutor(testExecutor);
         proxy = (SecuredReqResServiceIface)service.getPort(SecuredReqResServiceIface.class);
         ((StubExt)proxy).setConfigName(getConfigName(), "META-INF/wsrm-jaxws-client-config.xml");
      }
   }
   
   public void testSynchronousInvocation() throws Exception
   {
      doReliableMessageExchange(proxy, InvocationType.SYNC);
   }
   
   public void testAsynchronousInvocation() throws Exception
   {
      doReliableMessageExchange(proxy, InvocationType.ASYNC);
   }
   
   public void testAsynchronousInvocationUsingFuture() throws Exception
   {
      doReliableMessageExchange(proxy, InvocationType.ASYNC_FUTURE);
   }
   
   private void doSynchronousInvocation() throws Exception
   {
      assertEquals(proxy.echo(helloWorldMessage), helloWorldMessage);
   }
   
   private void doAsynchronousInvocation() throws Exception
   {
      Response<String> response = proxy.echoAsync(helloWorldMessage);
      assertEquals(response.get(), helloWorldMessage); // hidden future pattern
   }

   private void doAsynchronousInvocationUsingFuture() throws Exception
   {
      AsyncHandler<String> handler = new AsyncHandler<String>()
      {
         public void handleResponse(Response<String> response)
         {
            try
            {
               String retStr = (String) response.get(testWaitPeriod, testTimeUnit);
               assertEquals(helloWorldMessage, retStr);
               asyncHandlerCalled = true;
            }
            catch (Exception ex)
            {
               handlerException = ex;
            }
         }
      };
      Future<?> future = proxy.echoAsync(helloWorldMessage, handler);
      future.get(testWaitPeriod, testTimeUnit);
      ensureAsyncStatus();
   }
   
   private void ensureAsyncStatus() throws Exception
   {
      if (handlerException != null) throw handlerException;
      assertTrue("Async handler called", asyncHandlerCalled);
      handlerException = null;
      asyncHandlerCalled = false;
   }
   
   private void invokeWebServiceMethod(InvocationType invocationType) throws Exception
   {
      switch (invocationType) {
         case SYNC: doSynchronousInvocation(); break;
         case ASYNC: doAsynchronousInvocation(); break;
         case ASYNC_FUTURE: doAsynchronousInvocationUsingFuture(); break;
         default : fail("Unknown invocation type");
      }
   }
   
   private void doReliableMessageExchange(Object proxyObject, InvocationType invocationType) throws Exception
   {
      setAddrProps(proxy, "http://useless/action", serviceURL);
      invokeWebServiceMethod(invocationType);
      setAddrProps(proxy, "http://useless/action", serviceURL);
      invokeWebServiceMethod(invocationType);
      setAddrProps(proxy, "http://useless/action", serviceURL);
      invokeWebServiceMethod(invocationType);
      ((RMProvider)proxy).closeSequence();
   }
   
   public static String getClasspath()
   {
      return "jaxws-secured-wsrm.war, jaxws-secured-wsrm-client.jar";
   }
   
   protected abstract String getConfigName();

}
