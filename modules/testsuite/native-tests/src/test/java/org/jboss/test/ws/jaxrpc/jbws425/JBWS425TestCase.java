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
package org.jboss.test.ws.jaxrpc.jbws425;

import java.io.ByteArrayInputStream;
import java.net.URL;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/**
 * No SOAPAction when message is sent with SOAPConnection
 *
 * http://jira.jboss.com/jira/browse/JBWS-425
 *
 * @author Thomas.Diesler@jboss.org
 * @since 30-Nov-2005
 */
public class JBWS425TestCase extends JBossWSTest
{
   private static final String SOAP_ACTION = "\"urn:some-soap-action\"";

   private static Hello endpoint;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS425TestCase.class, "jaxrpc-jbws425.war, jaxrpc-jbws425-client.jar");
   }

   public void setUp() throws Exception
   {
      super.setUp();
      if (endpoint == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
         endpoint = (Hello)service.getPort(Hello.class);
      }
   }

   public void testClientActionFromWSDL() throws Exception
   {
      String soapAction = endpoint.hello("Hello Server");
      assertEquals(SOAP_ACTION, soapAction);
   }

   // [JBWS-983] Configure SOAPAction on the Call object
   public void testCallActionFromProperty() throws Exception
   {
	   ServiceFactory factory = ServiceFactory.newInstance();
      URL wsdlUrl = new URL("http://" + getServerHost() + ":8080/jaxrpc-jbws425?wsdl");
      QName serviceName = new QName("http://org.jboss.test.webservice/jbws425", "HelloService");
      QName portName = new QName("http://org.jboss.test.webservice/jbws425", "HelloPort");
      Service service = factory.createService(wsdlUrl, serviceName);
      Call call = service.createCall(portName, "hello");
      call.setProperty(Call.SOAPACTION_URI_PROPERTY, "uri:property-action");
      String soapAction = (String)call.invoke(new Object[] {"Hello Server"});
      assertEquals("\"uri:property-action\"", soapAction);
   }

   public void testMessageAccess() throws Exception
   {
      String reqEnv =
         "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
         "  <soapenv:Body>" +
         "    <ns1:hello xmlns:ns1='http://org.jboss.test.webservice/jbws425'>" +
         "      <String_1>Hello Server</String_1>" +
         "    </ns1:hello>" +
         "  </soapenv:Body>" +
         "</soapenv:Envelope>";

      MessageFactory msgFactory = MessageFactory.newInstance();
      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();

      MimeHeaders mimeHeaders = new MimeHeaders();
      mimeHeaders.addHeader("SOAPAction", SOAP_ACTION);

      SOAPMessage reqMsg = msgFactory.createMessage(mimeHeaders, new ByteArrayInputStream(reqEnv.getBytes()));

      URL epURL = new URL("http://" + getServerHost() + ":8080/jaxrpc-jbws425");
      SOAPMessage resMsg = con.call(reqMsg, epURL);

      SOAPElement soapElement = (SOAPElement)resMsg.getSOAPBody().getChildElements().next();
      soapElement = (SOAPElement)soapElement.getChildElements().next();
      String soapAction = soapElement.getValue();
      assertEquals(SOAP_ACTION, soapAction);
   }

}
