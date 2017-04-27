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
package org.jboss.test.ws.jaxws.webserviceref;

import org.jboss.logging.Logger;
import org.jboss.test.ws.jaxws.webserviceref.TestEndpoint;
import org.jboss.test.ws.jaxws.webserviceref.TestEndpointService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.WebServiceRefs;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@WebServiceRef(name = "Service1")
// Test multiple on type
@WebServiceRefs( { @WebServiceRef(name = "Service2"), @WebServiceRef(name = "Port1", type = TestEndpoint.class) })
public class TestEndpointClientTwo
{
   // provide logging
   private static final Logger log = Logger.getLogger(org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.class);

   // Test on field
   @WebServiceRef(name = "Service3")
   static Service service3;

   // Test on field
   @WebServiceRef(name = "Service4")
   static TestEndpointService service4;

   // Test on field
   @WebServiceRef(name = "Port2")
   static TestEndpoint port2;

   // Test on field
   @WebServiceRef(name = "Port3")
   static TestEndpoint port3;

   static InitialContext iniCtx;
   public static Map<String, String> testResult = new HashMap<String, String>();
   
   private static void setInitialCtx() throws NamingException
   {
      if (iniCtx == null)
      {
         InitialContext ctx = new InitialContext();
         Hashtable env = ctx.getEnvironment();
         env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming.client");
         env.put("j2ee.clientName", "jbossws-client");
         iniCtx = new InitialContext(env);
      }
   }

   public static void main(String[] args) throws Exception
   {
      String testName = args[0];
      org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo client = new org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo();
      Method method = org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.class.getMethod(testName, new Class[] { String.class });
      try
      {
         String retStr = (String)method.invoke(client, testName);
         org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.testResult.put(testName, retStr);
      }
      catch (InvocationTargetException ex)
      {
         org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.log.error("Invocation error", ex);
         org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.testResult.put(testName, ex.getTargetException().toString());
      }
      catch (Exception ex)
      {
         org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.log.error("Error", ex);
         org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.testResult.put(testName, ex.toString());
      }
   }

   /**
    * Customize service-class-name, service-qname
    */
   public String testService1(String reqStr) throws Exception
   {
      setInitialCtx();
      TestEndpointService service = (TestEndpointService)org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.iniCtx.lookup("java:comp/env/Service1");
      TestEndpoint port = service.getTestEndpointPort();
      return port.echo(reqStr);
   }

   /**
    * Customize config-name, config-file
    */
   public String testService2(String reqStr) throws Exception
   {
      setInitialCtx();
      Service service = (Service)org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.iniCtx.lookup("java:comp/env/Service2");

      TestEndpoint port = service.getPort(TestEndpoint.class);
      //verifyConfig((ConfigProvider)port);

      return port.echo(reqStr);
   }

   /**
    * Customize service-class-name, service-qname
    */
   public String testService3(String reqStr) throws Exception
   {
      TestEndpoint port = ((TestEndpointService)org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.service3).getTestEndpointPort();
      String resStr1 = port.echo(reqStr);

      setInitialCtx();
      TestEndpointService service = (TestEndpointService)org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.iniCtx.lookup("java:comp/env/Service3");
      port = service.getTestEndpointPort();

      String resStr2 = port.echo(reqStr);

      return resStr1 + resStr2;
   }

   /**
    * Customize config-name, config-file
    */
   public String testService4(String reqStr) throws Exception
   {
      TestEndpoint port = org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.service4.getTestEndpointPort();
      String resStr1 = port.echo(reqStr);
      //verifyConfig((ConfigProvider)port);

      setInitialCtx();
      TestEndpointService service = (TestEndpointService)org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.iniCtx.lookup("java:comp/env/Service4");
      port = service.getTestEndpointPort();
      //verifyConfig((ConfigProvider)port);

      String resStr2 = port.echo(reqStr);

      return resStr1 + resStr2;
   }

   /**
    * Customize port-info: port-qname, config-name, config-file
    */
   public String testPort1(String reqStr) throws Exception
   {
      setInitialCtx();
      TestEndpoint port = (TestEndpoint)org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.iniCtx.lookup("java:comp/env/Port1");
      //verifyConfig((ConfigProvider)port);

      return port.echo(reqStr);
   }

   /**
    * Customize port-info: service-endpoint-interface, config-name, config-file
    */
   public String testPort2(String reqStr) throws Exception
   {
      //verifyConfig((ConfigProvider)port2);
      String resStr1 = org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.port2.echo(reqStr);

      setInitialCtx();
      TestEndpoint port = (TestEndpoint)org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.iniCtx.lookup("java:comp/env/Port2");
      //verifyConfig((ConfigProvider)port);

      String resStr2 = port.echo(reqStr);

      return resStr1 + resStr2;
   }

   /**
    * Customize port-info: service-endpoint-interface, port-qname, stub-property
    */
   public String testPort3(String reqStr) throws Exception
   {
      String resStr1 = org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.port3.echo(reqStr);

      BindingProvider bp = (BindingProvider)org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.port3;
      verifyProperties(bp.getRequestContext());

      setInitialCtx();
      TestEndpoint port = (TestEndpoint)org.jboss.test.ws.jaxws.webserviceref.TestEndpointClientTwo.iniCtx.lookup("java:comp/env/Port3");
      String resStr2 = port.echo(reqStr);

      return resStr1 + resStr2;
   }

   private void verifyProperties(Map<String, Object> ctx)
   {
      String username = (String)ctx.get(BindingProvider.USERNAME_PROPERTY);
      if ("kermit".equals(username) == false)
         throw new RuntimeException("Invalid username: " + username);

      String password = (String)ctx.get(BindingProvider.PASSWORD_PROPERTY);
      if ("thefrog".equals(password) == false)
         throw new RuntimeException("Invalid password: " + password);
   }

   /*private void verifyConfig(ConfigProvider cp)
   {
      if ("Custom Client".equals(cp.getConfigName()) == false)
         throw new RuntimeException("Invalid config name: " + cp.getConfigName());

      if ("META-INF/jbossws-client-config.xml".equals(cp.getConfigFile()) == false)
         throw new RuntimeException("Invalid config file: " + cp.getConfigFile());
   } */
}
