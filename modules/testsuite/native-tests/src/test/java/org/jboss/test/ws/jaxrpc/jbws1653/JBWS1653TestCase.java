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
package org.jboss.test.ws.jaxrpc.jbws1653;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * [JBWS-1653] Post-handler-chain not invoked for "Standard Client" configuration
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 26-Jun-2007
 */
public class JBWS1653TestCase extends JBossWSTest
{
   
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1653TestCase.class, "jaxrpc-jbws1653.war, jaxrpc-jbws1653-client.jar");
   }

   public void setUp() throws Exception
   {
      ClientHandler.message = null;
   }

   public void testStandardConfig() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
      TestEndpoint port = (TestEndpoint)service.getPort(TestEndpoint.class);

      String retStr = port.echoString("kermit");
      assertEquals("kermit", retStr);
      assertNull(ClientHandler.message);
   }

   public void testStandardConfigConfiguredDII() throws Exception
   {
      ServiceFactoryImpl factory = new ServiceFactoryImpl();
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxrpc-jbws1653/TestEndpoint?wsdl");
      QName qname = new QName("http://org.jboss.test.ws/jbws1653", "TestService");
      Service service = factory.createService(wsdlURL, qname);

      Call call = service.createCall();
      call.setOperationName(new QName("http://org.jboss.test.ws/jbws1653", "echoString"));

      call.setTargetEndpointAddress("http://" + getServerHost() + ":8080/jaxrpc-jbws1653/TestEndpoint");

      String hello = "Hello";
      Object retObj = call.invoke(new Object[] { hello });
      assertEquals(hello, retObj);

      assertNull(ClientHandler.message);
   }

   public void testStandardConfigFullyConfiguredDII() throws Exception
   {
      ServiceFactoryImpl factory = new ServiceFactoryImpl();
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxrpc-jbws1653/TestEndpoint?wsdl");
      URL mappingURL = getResourceURL("jaxrpc/jbws1653/WEB-INF/jaxrpc-mapping.xml");
      QName qname = new QName("http://org.jboss.test.ws/jbws1653", "TestService");
      Service service = factory.createService(wsdlURL, qname, mappingURL);
      TestEndpoint port = (TestEndpoint)service.getPort(TestEndpoint.class);

      String retStr = port.echoString("kermit");
      assertEquals("kermit", retStr);
      assertNull(ClientHandler.message);
   }

   public void testCustomConfig() throws Exception
   {
      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      URLClassLoader urlLoader = new URLClassLoader(new URL[] {}, ctxLoader) {
         public URL findResource(String resName)
         {
            URL resURL = super.findResource(resName);
            try
            {
               if (resName.endsWith("META-INF/standard-jaxrpc-client-config.xml"))
                  resURL = getResourceURL("jaxrpc/jbws1653/META-INF/standard-jaxrpc-client-config.xml");
            }
            catch (MalformedURLException ex)
            {
               // ignore
            }
            return resURL;
         }

         public URL getResource(String resName)
         {
            URL resURL = super.getResource(resName);
            try
            {
               if (resName.endsWith("META-INF/standard-jaxrpc-client-config.xml"))
                  resURL = getResourceURL("jaxrpc/jbws1653/META-INF/standard-jaxrpc-client-config.xml");
            }
            catch (MalformedURLException ex)
            {
               // ignore
            }
            return resURL;
         }
      };
      
      Thread.currentThread().setContextClassLoader(urlLoader);
      try
      {
         URL configURL = urlLoader.findResource("META-INF/standard-jaxrpc-client-config.xml");
         assertTrue("Invalid config url: " + configURL, configURL.toExternalForm().indexOf("jbws1653") > 0);

         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         TestEndpoint port = (TestEndpoint)service.getPort(TestEndpoint.class);

         String retStr = port.echoString("kermit");
         assertEquals("kermit", retStr);
         assertEquals("kermit", ClientHandler.message);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(ctxLoader);
      }
   }

   public void testCustomConfigConfiguredDII() throws Exception
   {
      if (false)
      {
         System.out.println("FIXME [JBWS-1771] Post-handler-chain not invoked for \"Standard Client\" configuration with DII client");
         return;
      }

      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      URLClassLoader urlLoader = new URLClassLoader(new URL[] {}, ctxLoader) {
         public URL getResource(String resName)
         {
            URL resURL = super.getResource(resName);
            try
            {
               if (resName.endsWith("META-INF/standard-jaxrpc-client-config.xml"))
                  resURL = getResourceURL("jaxrpc/jbws1653/META-INF/standard-jaxrpc-client-config.xml");
            }
            catch (MalformedURLException ex)
            {
               // ignore
            }
            return resURL;
         }
      };
      
      Thread.currentThread().setContextClassLoader(urlLoader);
      try
      {
         ServiceFactoryImpl factory = new ServiceFactoryImpl();
         URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxrpc-jbws1653/TestEndpoint?wsdl");
         QName qname = new QName("http://org.jboss.test.ws/jbws1653", "TestService");
         Service service = factory.createService(wsdlURL, qname);

         Call call = service.createCall();
         call.setOperationName(new QName("http://org.jboss.test.ws/jbws1653", "echoString"));

         call.setTargetEndpointAddress("http://" + getServerHost() + ":8080/jaxrpc-jbws1653/TestEndpoint");

         String hello = "Hello";

         Object retObj = call.invoke(new Object[] { hello });
         assertEquals(hello, retObj);
         assertEquals(hello, ClientHandler.message);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(ctxLoader);
      }
   }

   public void testCustomConfigFullyConfiguredDII() throws Exception
   {
      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      URLClassLoader urlLoader = new URLClassLoader(new URL[] {}, ctxLoader) {
         public URL getResource(String resName)
         {
            URL resURL = super.getResource(resName);
            try
            {
               if (resName.endsWith("META-INF/standard-jaxrpc-client-config.xml"))
                  resURL = getResourceURL("jaxrpc/jbws1653/META-INF/standard-jaxrpc-client-config.xml");
            }
            catch (MalformedURLException ex)
            {
               // ignore
            }
            return resURL;
         }
      };
      
      Thread.currentThread().setContextClassLoader(urlLoader);
      try
      {
         ServiceFactoryImpl factory = new ServiceFactoryImpl();
         URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxrpc-jbws1653/TestEndpoint?wsdl");
         URL mappingURL = getResourceURL("jaxrpc/jbws1653/WEB-INF/jaxrpc-mapping.xml");
         QName qname = new QName("http://org.jboss.test.ws/jbws1653", "TestService");
         Service service = factory.createService(wsdlURL, qname, mappingURL);
         TestEndpoint port = (TestEndpoint)service.getPort(TestEndpoint.class);

         String retStr = port.echoString("thefrog");
         assertEquals("thefrog", retStr);
         assertEquals("thefrog", ClientHandler.message);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(ctxLoader);
      }
   }
}
