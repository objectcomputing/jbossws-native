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
package org.jboss.test.ws.jaxrpc.jbws64;

import java.io.ByteArrayInputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Axis-1.2RC2 client may send messages like this
 *
 * <soapenv:Envelope>
 *  <soapenv:Body>
 *   <publish xmlns="http://webservices.est.useme.etish.com">
 *    <in0 xmlns="">joel</in0>
 *     <in1 xmlns="">secret</in1>
 *     <in2 xmlns="">1</in2>
 *     <in3 xmlns="">6</in3>
 *     <in4 xmlns="">2</in4>
 *    </publish>
 *   </soapenv:Body>
 * </soapenv:Envelope>
 *
 * http://jira.jboss.com/jira/browse/JBWS-64
 *
 * @author Thomas.Diesler@jboss.org
 * @since 08-Feb-2005
 */
public class JBWS64TestCase extends JBossWSTest
{
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS64TestCase.class, "jaxrpc-jbws64.war");
   }

   /**
    * Test JSE endpoint
    */
   public void testEndpoint() throws Exception
   {
      String reqMsgStr =
              "<soapenv:Envelope" +
              "    xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'" +
              "    xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
              "    xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
              "  <soapenv:Body>" +
              "    <publish xmlns='http://org.jboss.test.webservice/jbws64'>" +
              "       <String_1 xmlns=''>joel</String_1>" +
              "       <String_2 xmlns=''>secret</String_2>" +
              "       <String_3 xmlns=''>1</String_3>" +
              "       <String_4 xmlns=''>6</String_4>" +
              "       <String_5 xmlns=''>2</String_5>" +
              "     </publish>" +
              "  </soapenv:Body>" +
              "</soapenv:Envelope>";

      MessageFactory msgFactory = MessageFactory.newInstance();
      SOAPMessage reqMsg = msgFactory.createMessage(null, new ByteArrayInputStream(reqMsgStr.getBytes()));

      SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
      SOAPConnection con = factory.createConnection();
      SOAPMessage resMsg = con.call(reqMsg, "http://" + getServerHost() + ":8080/jaxrpc-jbws64");

      SOAPBody soapBody = resMsg.getSOAPBody();
      SOAPElement soapBodyElement = (SOAPElement)soapBody.getChildElements().next();
      SOAPElement soapElement = (SOAPElement)soapBodyElement.getChildElements().next();
      assertEquals("joel,secret,1,6,2", soapElement.getValue());
   }
}
