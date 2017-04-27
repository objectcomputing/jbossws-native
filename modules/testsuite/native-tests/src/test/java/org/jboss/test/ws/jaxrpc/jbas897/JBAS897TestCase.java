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
package org.jboss.test.ws.jaxrpc.jbas897;

import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

import org.jboss.wsf.test.JBossWSTest;


/**
 * I made a typo in my ejb-jar.xml file so it didn't match the
 * <ejb-link> within my webservices.xml file. No complaint
 * from JBoss on deployment on this.

 * http://jira.jboss.com/jira/browse/JBAS-897
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Feb-2005
 */
public class JBAS897TestCase extends JBossWSTest
{
   private String NAMESPACE = "http://org.jboss.test.webservice/jbas897";

   private QName SERVICE_NAME = new QName(NAMESPACE, "HelloService");

   /**
    * Test JSE endpoint
    */
   public void testJSEEndpoint() throws Exception
   {
      deploy("jaxrpc-jbas897.war");
      try
      {
         ServiceFactory serviceFactory = ServiceFactory.newInstance();
         Service service = serviceFactory.createService(new URL("http://" + getServerHost() + ":8080/jaxrpc-jbas897/HelloJSE?wsdl"), SERVICE_NAME);
         Call call = (Call)service.createCall(new QName(NAMESPACE, "HelloPort"), "sayHello");
         String retstr = (String)call.invoke(new Object[] { "Hello" });
         assertEquals("'Hello' to you too!", retstr);
      }
      finally
      {
         undeploy("jaxrpc-jbas897.war");
      }
   }

   /**
    * Test JSE endpoint with invalid deployment
    */
   public void testJSEEndpointFail() throws Exception
   {
      try
      {
         try
         {
            deploy("jaxrpc-jbas897-fail.war");
         }
         catch (Exception e)
         {
            // expected
         }

         try
         {
            URL url = new URL("http://" + getServerHost() + ":8080/jaxrpc-jbas897-fail/HelloJSE");
            url.openStream();
            fail("Deployment was expected to fail");
         }
         catch (IOException e)
         {
            // expected
         }
      }
      finally
      {
         undeploy("jaxrpc-jbas897-fail.war");
      }
   }

   /**
    * Test EJB endpoint
    */
   public void testEJBEndpoint() throws Exception
   {
      deploy("jaxrpc-jbas897.jar");
      try
      {
         ServiceFactory serviceFactory = ServiceFactory.newInstance();
         Service service = serviceFactory.createService(new URL("http://" + getServerHost() + ":8080/jaxrpc-jbas897/HelloEJB?wsdl"), SERVICE_NAME);
         Call call = (Call)service.createCall(new QName(NAMESPACE, "HelloPort"), "sayHello");
         String retstr = (String)call.invoke(new Object[] { "Hello" });
         assertEquals("'Hello' to you too!", retstr);
      }
      finally
      {
         undeploy("jaxrpc-jbas897.jar");
      }
   }

   /**
    * Test EJB endpoint
    */
   public void testEJBEndpointFail() throws Exception
   {
      try
      {
         try
         {
            deploy("jaxrpc-jbas897-fail.jar");
         }
         catch (Exception e)
         {
            // expected
         }

         try
         {
            URL url = new URL("http://" + getServerHost() + ":8080/jaxrpc-jbas897-fail/ShoulBeHello");
            url.openStream();
            fail("Deployment was expected to fail");
         }
         catch (IOException e)
         {
            // expected
         }
      }
      finally
      {
         undeploy("jaxrpc-jbas897-fail.jar");
      }
   }
}
