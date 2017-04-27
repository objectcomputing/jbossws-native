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
package org.jboss.test.ws.jaxrpc.utf16;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/** 
 * Tests R4001 in the WSI Basic Profile 1.0:
 * A RECEIVER MUST accept messages that include the Unicode Byte Order Mark (BOM).
 *
 * @author Thomas.Diesler@jboss.org
 * @since 09-Dec-2005
 */
public class UTF16TestCase extends JBossWSTest
{
   private SOAPConnection soapCon;
   
   private static String reqEnv = 
      "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
      "  <soapenv:Body>" +
      "    <ns1:hello xmlns:ns1='http://org.jboss.test.webservice/utf16'>" +
      "      <String_1>Kermit</String_1>" +
      "    </ns1:hello>" +
      "  </soapenv:Body>" +
      "</soapenv:Envelope>";
   
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(UTF16TestCase.class, "jaxrpc-utf16.war, jaxrpc-utf16-client.jar");
   }

   public void setUp() throws Exception 
   {
      super.setUp();
      SOAPConnectionFactory conFactory = SOAPConnectionFactory.newInstance();
      soapCon = conFactory.createConnection();
   }
   
   public void testClientAccess() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
      Hello endpoint = (Hello)service.getPort(Hello.class);
      
      String retObj = endpoint.hello("Kermit");
      assertEquals("Kermit", retObj);
   }
   
   /** Test SAAJ access without MIME headers, using the default encoding
    */
   public void testDefaultAccess() throws Exception
   {
      SOAPMessage reqMsg = createSOAPMessage(reqEnv, null);
      SOAPMessage resMsg = soapCon.call(reqMsg, getTargetAddress());
      verifyResponseMessage(resMsg);
   }

   /** Test UTF-8 SAAJ access
    */
   public void testUTF8Access() throws Exception
   {
      SOAPMessage reqMsg = createSOAPMessage(reqEnv, "UTF-8");
      SOAPMessage resMsg = soapCon.call(reqMsg, getTargetAddress());
      verifyResponseMessage(resMsg);
   }

   /** Test UTF-16 SAAJ access
    */
   public void testUTF16Access() throws Exception
   {
      SOAPMessage reqMsg = createSOAPMessage(reqEnv, "UTF-16");
      SOAPMessage resMsg = soapCon.call(reqMsg, getTargetAddress());
      verifyResponseMessage(resMsg);
   }

   private SOAPMessage createSOAPMessage(String envStr, String csName) throws IOException, SOAPException
   {
      MessageFactory msgFactory = MessageFactory.newInstance();
      
      MimeHeaders headers = null;
      if (csName != null)
      {
         headers = new MimeHeaders();
         headers.addHeader("Content-Type", "text/xml; charset=\"" + csName + "\"");
         envStr = "<?xml version='1.0' encoding='" + csName + "'?>" + envStr;
      }
      
      InputStream is = getInputStreamForString(envStr, csName);
      SOAPMessage reqMsg = msgFactory.createMessage(headers, is);
      return reqMsg;
   }

   private InputStream getInputStreamForString(String envStr, String csName) throws IOException
   {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      OutputStreamWriter osw;
      if (csName != null)
      {
         osw = new OutputStreamWriter(bos, csName);
      }
      else
      {
         osw = new OutputStreamWriter(bos, "UTF-8");
      }
      osw.write(envStr);
      osw.flush();
      
      ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
      return bis;
   }
   
   private void verifyResponseMessage(SOAPMessage resMsg) throws SOAPException
   {
      SOAPElement soapElement = (SOAPElement)resMsg.getSOAPBody().getChildElements().next();
      soapElement = (SOAPElement)soapElement.getChildElements().next();
      String retObj = soapElement.getValue();
      assertEquals("Kermit", retObj);
   }
   
   private String getTargetAddress()
   {
      return "http://" + getServerHost() + ":8080/jaxrpc-utf16";
   }
}
