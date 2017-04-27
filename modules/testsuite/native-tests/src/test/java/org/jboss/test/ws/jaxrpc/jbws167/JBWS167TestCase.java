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
package org.jboss.test.ws.jaxrpc.jbws167;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/**
 * Tests that we can send and receive HTTP headers. Also test the we support multiple
 * values per header (like cookies)
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 05-Mar-2005
 */
public class JBWS167TestCase extends JBossWSTest
{
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS167TestCase.class, "jaxrpc-jbws167.war");
   }

   public void testHttpHeaders() throws Exception
   {
      String NS_PREFIX = "ns1";
      String NS_URI = "http://org.jboss.test.webservice/jbws167";
      String rpcMethodName = "hello";

      MessageFactory mf = MessageFactory.newInstance();
      SOAPMessage msg = mf.createMessage();
      SOAPPart sp = msg.getSOAPPart();
      SOAPEnvelope envelope = sp.getEnvelope();
      SOAPBody bdy = envelope.getBody();
      SOAPBodyElement sbe = bdy.addBodyElement(envelope.createName(rpcMethodName, NS_PREFIX, NS_URI));

      // Add a some child elements
      sbe.addChildElement(envelope.createName("String_1")).addTextNode("Some hello");

      MimeHeaders headers = msg.getMimeHeaders();
      headers.addHeader("SOAPAction", "/foo/bar");
      headers.addHeader("testme", "testme");
      headers.addHeader("testme", "testme2");

      SOAPConnectionFactory conFactory = SOAPConnectionFactory.newInstance();
      SOAPConnection con = conFactory.createConnection();
      SOAPMessage resMessage = con.call(msg, new URL("http://" + getServerHost() + ":8080/jaxrpc-jbws167"));

      SOAPBody soapBody = resMessage.getSOAPBody();
      SOAPEnvelope soapEnvelope = (SOAPEnvelope) soapBody.getParentElement();

      Name rpcName = soapEnvelope.createName(rpcMethodName + "Response", NS_PREFIX, NS_URI);
      Iterator childElements = soapBody.getChildElements(rpcName);
      assertTrue("Expexted child: " + rpcName, childElements.hasNext());

      SOAPElement bodyChild = (SOAPElement) childElements.next();
      Name resName = soapEnvelope.createName("result");
      SOAPElement resElement = (SOAPElement) bodyChild.getChildElements(resName).next();
      String value = resElement.getValue();
      assertEquals("[pass]", value);

      verifyResponseHeaders(resMessage);
   }

   private void verifyResponseHeaders(SOAPMessage message)
   {
      MimeHeaders headers = message.getMimeHeaders();
      assertNotNull(headers);

      HashSet expectedValues = new HashSet();
      expectedValues.add("1");
      expectedValues.add("2");
      expectedValues.add("3");

      String[] values = headers.getHeader("server-test-header");
      assertTrue("Expected 3 values for server test header", values != null && values.length == 3);
      for (int i = 0; i < 3; i++)
      {
         assertTrue("Expected results did not match (got " + values[i] + " for a result)", expectedValues.contains(values[i]));
         expectedValues.remove(values[i]);
      }
    }
}
