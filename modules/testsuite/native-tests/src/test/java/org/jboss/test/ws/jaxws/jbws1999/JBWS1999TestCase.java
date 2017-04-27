/*
* JBoss, Home of Professional Open Source.
* Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.jbws1999;

import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.ws.extensions.security.exception.FailedAuthenticationException;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test case to test UsernameToken authorization / authentication
 * for POJO endpoints.
 * 
 * @author darran.lofthouse@jboss.com
 * @since 12th January 2008
 * @see https://jira.jboss.org/jira/browse/JBWS-1999
 */
public class JBWS1999TestCase extends JBossWSTest
{

   private final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxws-jbws1999";

   private static final String FAULT_CODE = "wsse:FailedAuthentication";

   private static final String FAULT_STRING = FailedAuthenticationException.faultString;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1999TestCase.class, "jaxws-jbws1999.war");
   }

   public void testNoSecurity() throws Exception
   {
      Endpoint endpoint = getPort(false);

      String message = "NoSecurity Message";

      String response = endpoint.echoNoSecurity(message);
      assertEquals("Response Message", message, response);
   }

   public void testUnchecked_Default() throws Exception
   {
      Endpoint endpoint = getPort(false);

      String message = "UncheckedDefault Message";

      String response = endpoint.echoUnchecked(message);
      assertEquals("Response Message", message, response);
   }

   public void testUnchecked() throws Exception
   {
      Endpoint endpoint = getPort(true);
      Map<String, Object> requestContext = ((BindingProvider)endpoint).getRequestContext();
      requestContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
      requestContext.put(BindingProvider.PASSWORD_PROPERTY, "thefrog");

      String message = "Unchecked Message";

      String response = endpoint.echoUnchecked(message);
      assertEquals("Response Message", message, response);
   }

   public void testUnchecked_WrongPassword() throws Exception
   {
      Endpoint endpoint = getPort(true);
      Map<String, Object> requestContext = ((BindingProvider)endpoint).getRequestContext();
      requestContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
      requestContext.put(BindingProvider.PASSWORD_PROPERTY, "thepig");

      String message = "Unchecked_WrongPassword Message";

      try
      {
         endpoint.echoUnchecked(message);
         fail("Expected SOAPFaultException not thrown!!");
      }
      catch (SOAPFaultException sfe)
      {
         SOAPFault fault = sfe.getFault();
         String faultCode = fault.getFaultCode();
         assertEquals("Fault Code", FAULT_CODE, faultCode);
         String faultString = fault.getFaultString();
         assertEquals("Fault String", FAULT_STRING, faultString);
      }

   }

   public void testFriendRequired() throws Exception
   {
      Endpoint endpoint = getPort(true);
      Map<String, Object> requestContext = ((BindingProvider)endpoint).getRequestContext();
      requestContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
      requestContext.put(BindingProvider.PASSWORD_PROPERTY, "thefrog");

      String message = "FriendRequired Message";

      String response = endpoint.echoFriendRequired(message);
      assertEquals("Response Message", message, response);
   }

   public void testFriendRequired_WrongPassword() throws Exception
   {
      Endpoint endpoint = getPort(true);
      Map<String, Object> requestContext = ((BindingProvider)endpoint).getRequestContext();
      requestContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
      requestContext.put(BindingProvider.PASSWORD_PROPERTY, "thepig");

      String message = "FriendRequired Message";

      try
      {
         endpoint.echoFriendRequired(message);
         fail("Expected SOAPFaultException not thrown!!");
      }
      catch (SOAPFaultException sfe)
      {
         SOAPFault fault = sfe.getFault();
         String faultCode = fault.getFaultCode();
         assertEquals("Fault Code", FAULT_CODE, faultCode);
         String faultString = fault.getFaultString();
         assertEquals("Fault Message", FAULT_STRING, faultString);
      }
   }

   public void testEnemyRequired() throws Exception
   {
      Endpoint endpoint = getPort(true);
      Map<String, Object> requestContext = ((BindingProvider)endpoint).getRequestContext();
      requestContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
      requestContext.put(BindingProvider.PASSWORD_PROPERTY, "thefrog");

      String message = "EnemyRequired Message";

      try
      {
         endpoint.echoEnemyRequired(message);
         fail("Expected SOAPFaultException not thrown!!");
      }
      catch (SOAPFaultException sfe)
      {
         SOAPFault fault = sfe.getFault();
         String faultCode = fault.getFaultCode();
         assertEquals("Fault Code", FAULT_CODE, faultCode);
         String faultString = fault.getFaultString();
         assertEquals("Fault Message", FAULT_STRING, faultString);
      }
   }

   private Endpoint getPort(boolean enableSecurity) throws Exception
   {
      URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");
      QName serviceName = new QName("http://ws.jboss.org/jbws1999", "EndpointService");
      Endpoint port = Service.create(wsdlURL, serviceName).getPort(Endpoint.class);
      if (enableSecurity == true)
      {
         URL securityURL = getResourceURL("jaxws/jbws1999/jboss-wsse-client.xml");
         ((StubExt)port).setSecurityConfig(securityURL.toExternalForm());
         ((StubExt)port).setConfigName("Standard WSSecurity Client");
      }

      return port;
   }
}
