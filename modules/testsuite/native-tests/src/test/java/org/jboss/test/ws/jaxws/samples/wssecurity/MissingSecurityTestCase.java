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
package org.jboss.test.ws.jaxws.samples.wssecurity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test WS-Security server implementation behavior
 * when WS-Security is not used on client side
 *
 * @author alessio.soldano@jboss.com
 * @since 21-Feb-2008
 */
public class MissingSecurityTestCase extends JBossWSTest
{
   private String endpointUrl = "http://" + getServerHost() + ":8080/jaxws-samples-wssecurity-sign";
   
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(MissingSecurityTestCase.class, "jaxws-samples-wssecurity-sign.war");
   }

   public void testNoSOAPHeader() throws Exception
   {
      MessageFactory msgFactory = MessageFactory.newInstance();
      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();

      String reqEnv = 
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" + 
         " <env:Body>" + 
         "  <ns1:echoUserType xmlns:ns1='http://org.jboss.ws/samples/wssecurity'>" + 
         "   <user><msg>Kermit</msg></user>" + 
         "  </ns1:echoUserType>" + 
         " </env:Body>" + 
         "</env:Envelope>";
      SOAPMessage reqMsg = msgFactory.createMessage(null, new ByteArrayInputStream(reqEnv.getBytes()));

      URL epURL = new URL(endpointUrl);
      SOAPMessage resMsg = con.call(reqMsg, epURL);
      
      SOAPFault fault = resMsg.getSOAPBody().getFault();
      assertTrue(fault.getFaultString().contains("This service requires <wsse:Security>, which is missing."));
   }

   public void testNoSecurityHeader() throws Exception
   {
      Hello hello = getPort();
      UserType in0 = new UserType();
      in0.setMsg("Kermit");
      try
      {
         hello.echoUserType(in0);
         fail("Exception about missing security header expected");
      }
      catch (SOAPFaultException e)
      {
         assertTrue(e.getMessage().contains("This service requires <wsse:Security>, which is missing."));
      }
      catch (Exception e)
      {
         fail("Exception about missing security header expected");
      }
   }
   
   private Hello getPort() throws Exception
   {
      URL wsdlURL = getResourceURL("wsprovide/jaxws/samples/wssecurity/HelloService.wsdl");
      QName serviceName = new QName("http://org.jboss.ws/samples/wssecurity", "HelloService");

      Service service = Service.create(wsdlURL, serviceName);
      Hello port = (Hello)service.getPort(Hello.class);

      Map<String, Object> reqContext = ((BindingProvider)port).getRequestContext();
      reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

      return port;
   }
}
