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
package org.jboss.test.ws.jaxrpc.addressrewrite;

import java.net.URL;

import javax.management.Attribute;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

import org.jboss.wsf.common.ObjectNameFactory;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Test soap:address rewriting in the WSDL.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 22-Jun-2007
 */
public class AddressRewriteTestCase extends JBossWSTest
{
   private static final ObjectName SERVER_CONFIG_OBJECT_NAME = ObjectNameFactory.create("jboss.ws:service=ServerConfig");
   
   private static String NAMESPACE = "http://test.jboss.org/addressrewrite";
   private String wsdlLocation;
   private String wsdlLocationSec;

   private Boolean modifySOAPAddress;
   private String webServiceHost;

   public void setUp() throws Exception
   {
      wsdlLocation = "http://" + getServerHost() + ":8080/jaxrpc-addressrewrite/ValidURL?wsdl";
      wsdlLocationSec = "http://" + getServerHost() + ":8080/jaxrpc-addressrewrite-sec/ValidURL?wsdl";
      modifySOAPAddress = (Boolean)getServer().getAttribute(SERVER_CONFIG_OBJECT_NAME, "ModifySOAPAddress");
      webServiceHost = (String)getServer().getAttribute(SERVER_CONFIG_OBJECT_NAME, "WebServiceHost");
   }

   public void tearDown() throws Exception
   {
      Attribute attr = new Attribute("ModifySOAPAddress", modifySOAPAddress);
      getServer().setAttribute(SERVER_CONFIG_OBJECT_NAME, attr);
   }

   public void testRewrite() throws Exception
   {
      setModifySOAPAddress(true);
      deploy("jaxrpc-addressrewrite.war");
      try
      {
         ServiceFactory serviceFactory = ServiceFactory.newInstance();

         Service service = serviceFactory.createService(new URL(wsdlLocation), new QName(NAMESPACE, "ValidURLService"));
         Call call = (Call)service.createCall(new QName(NAMESPACE, "ValidURLPort"), "sayHello");
         assertEquals("http://" + webServiceHost + ":8080/jaxrpc-addressrewrite/ValidURL", call.getTargetEndpointAddress());

         service = serviceFactory.createService(new URL(wsdlLocation), new QName(NAMESPACE, "InvalidURLService"));
         call = (Call)service.createCall(new QName(NAMESPACE, "InvalidURLPort"), "sayHello");
         assertEquals("http://" + webServiceHost + ":8080/jaxrpc-addressrewrite/InvalidURL", call.getTargetEndpointAddress());

         service = serviceFactory.createService(new URL(wsdlLocation), new QName(NAMESPACE, "ValidSecureURLService"));
         call = (Call)service.createCall(new QName(NAMESPACE, "ValidSecureURLPort"), "sayHello");
         assertEquals("https://" + webServiceHost + ":8443/jaxrpc-addressrewrite/ValidSecureURL", call.getTargetEndpointAddress());

         service = serviceFactory.createService(new URL(wsdlLocation), new QName(NAMESPACE, "InvalidSecureURLService"));
         call = (Call)service.createCall(new QName(NAMESPACE, "InvalidSecureURLPort"), "sayHello");
         assertEquals("https://" + webServiceHost + ":8443/jaxrpc-addressrewrite/InvalidSecureURL", call.getTargetEndpointAddress());
      }
      finally
      {
         undeploy("jaxrpc-addressrewrite.war");
      }
   }

   /**
    * Rewrite soap:address URL according to transport guarantee
    * 
    * http://jira.jboss.org/jira/browse/JBWS-454
    */
   public void testSecureRewrite() throws Exception
   {
      deploy("jaxrpc-addressrewrite-sec.war");
      try
      {
         ServiceFactory serviceFactory = ServiceFactory.newInstance();

         Service service = serviceFactory.createService(new URL(wsdlLocationSec), new QName(NAMESPACE, "ValidURLService"));
         Call call = (Call)service.createCall(new QName(NAMESPACE, "ValidURLPort"), "sayHello");
         assertEquals("https://" + webServiceHost + ":8443/jaxrpc-addressrewrite-sec/ValidURL", call.getTargetEndpointAddress());

         service = serviceFactory.createService(new URL(wsdlLocationSec), new QName(NAMESPACE, "InvalidURLService"));
         call = (Call)service.createCall(new QName(NAMESPACE, "InvalidURLPort"), "sayHello");
         assertEquals("https://" + webServiceHost + ":8443/jaxrpc-addressrewrite-sec/InvalidURL", call.getTargetEndpointAddress());

         service = serviceFactory.createService(new URL(wsdlLocationSec), new QName(NAMESPACE, "ValidSecureURLService"));
         call = (Call)service.createCall(new QName(NAMESPACE, "ValidSecureURLPort"), "sayHello");
         assertEquals("https://" + webServiceHost + ":8443/jaxrpc-addressrewrite-sec/ValidSecureURL", call.getTargetEndpointAddress());

         service = serviceFactory.createService(new URL(wsdlLocationSec), new QName(NAMESPACE, "InvalidSecureURLService"));
         call = (Call)service.createCall(new QName(NAMESPACE, "InvalidSecureURLPort"), "sayHello");
         assertEquals("https://" + webServiceHost + ":8443/jaxrpc-addressrewrite-sec/InvalidSecureURL", call.getTargetEndpointAddress());
      }
      finally
      {
         undeploy("jaxrpc-addressrewrite-sec.war");
      }
   }

   public void testNoRewrite() throws Exception
   {
      setModifySOAPAddress(false);
      deploy("jaxrpc-addressrewrite.war");
      try
      {
         ServiceFactory serviceFactory = ServiceFactory.newInstance();

         Service service = serviceFactory.createService(new URL(wsdlLocation), new QName(NAMESPACE, "ValidURLService"));
         Call call = (Call)service.createCall(new QName(NAMESPACE, "ValidURLPort"), "sayHello");
         assertEquals("http://somehost:80/somepath", call.getTargetEndpointAddress());

         service = serviceFactory.createService(new URL(wsdlLocation), new QName(NAMESPACE, "InvalidURLService"));
         call = (Call)service.createCall(new QName(NAMESPACE, "InvalidURLPort"), "sayHello");
         assertEquals("http://" + webServiceHost + ":8080/jaxrpc-addressrewrite/InvalidURL", call.getTargetEndpointAddress());

         service = serviceFactory.createService(new URL(wsdlLocation), new QName(NAMESPACE, "ValidSecureURLService"));
         call = (Call)service.createCall(new QName(NAMESPACE, "ValidSecureURLPort"), "sayHello");
         assertEquals("https://somehost:443/some-secure-path", call.getTargetEndpointAddress());

         service = serviceFactory.createService(new URL(wsdlLocation), new QName(NAMESPACE, "InvalidSecureURLService"));
         call = (Call)service.createCall(new QName(NAMESPACE, "InvalidSecureURLPort"), "sayHello");
         assertEquals("https://" + webServiceHost + ":8443/jaxrpc-addressrewrite/InvalidSecureURL", call.getTargetEndpointAddress());
      }
      finally
      {
         undeploy("jaxrpc-addressrewrite.war");
      }
   }

   private void setModifySOAPAddress(Boolean value) throws Exception
   {
      Attribute attr = new Attribute("ModifySOAPAddress", value);
      getServer().setAttribute(SERVER_CONFIG_OBJECT_NAME, attr);
   }
}
