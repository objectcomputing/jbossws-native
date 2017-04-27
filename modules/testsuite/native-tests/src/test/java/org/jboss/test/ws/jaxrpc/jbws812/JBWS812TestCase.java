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
package org.jboss.test.ws.jaxrpc.jbws812;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/** 
 * Invalid byte 2 of 2-byte UTF-8 sequence
 *
 * http://jira.jboss.com/jira/browse/JBWS-812
 *
 * @author Thomas.Diesler@jboss.org
 * @since 11-Apr-2006
 */
public class JBWS812TestCase extends JBossWSTest
{
   private String reqEnv = 
      "<?xml version='1.0' encoding='UTF-8'?>" + 
      "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" + 
      " <env:Body>" + 
      "  <ns1:echoSimple xmlns:ns1='http://org.jboss.test.ws/jbws812'>" + 
      "   <String_1>&#xA0;</String_1>" + 
      "  </ns1:echoSimple>" + 
      " </env:Body>" + 
      "</env:Envelope>";
   
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS812TestCase.class, "jaxrpc-jbws812.war, jaxrpc-jbws812-client.jar");
   }

   /** Send the raw bytes via an HttpURLConnection
    */
   public void testHttpURLConnection() throws Exception
   {
      String targetAddress = "http://" + getServerHost() + ":8080/jaxrpc-jbws812";
      HttpURLConnection con = (HttpURLConnection)new URL(targetAddress).openConnection();
      con.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.connect();
      
      OutputStream outs = con.getOutputStream();
      outs.write(reqEnv.getBytes("UTF-8"));
      outs.close();
      
      int resCode = con.getResponseCode();
      assertEquals(200, resCode);
   }

   /** Test DOMUtils, DOMWriter roundtrip and send the via an HttpURLConnection
    */
   public void testDOMParseWrite() throws Exception
   {
      String targetAddress = "http://" + getServerHost() + ":8080/jaxrpc-jbws812";
      HttpURLConnection con = (HttpURLConnection)new URL(targetAddress).openConnection();
      con.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.connect();
      
      OutputStream outs = con.getOutputStream();
      Element reqMsg = DOMUtils.parse(reqEnv);
      new DOMWriter(outs).print(reqMsg);
      outs.close();
      
      int resCode = con.getResponseCode();
      assertEquals(200, resCode);
   }

   /** Send the SOAPMessage via an HttpURLConnection
    */
   public void testSOAPMessage() throws Exception
   {
      String targetAddress = "http://" + getServerHost() + ":8080/jaxrpc-jbws812";
      HttpURLConnection con = (HttpURLConnection)new URL(targetAddress).openConnection();
      con.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.connect();
      
      MessageFactory factory = MessageFactory.newInstance();
      MimeHeaders mimeHeaders = new MimeHeaders();
      mimeHeaders.addHeader("Content-Type", "text/xml; charset=UTF-8");
      SOAPMessage reqMsg = factory.createMessage(mimeHeaders, new ByteArrayInputStream(reqEnv.getBytes("UTF-8")));

      OutputStream outs = con.getOutputStream();
      reqMsg.writeTo(outs);
      outs.close();
      
      int resCode = con.getResponseCode();
      assertEquals(200, resCode);
   }

   /** Send the SOAPMessage via SOAPConnection
    */
   public void testSOAPConnection() throws Exception
   {
      MessageFactory factory = MessageFactory.newInstance();
      MimeHeaders mimeHeaders = new MimeHeaders();
      mimeHeaders.addHeader("Content-Type", "text/xml; charset=UTF-8");
      SOAPMessage reqMsg = factory.createMessage(mimeHeaders, new ByteArrayInputStream(reqEnv.getBytes("UTF-8")));

      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
      String targetAddress = "http://" + getServerHost() + ":8080/jaxrpc-jbws812";
      SOAPMessage resMsg = con.call(reqMsg, targetAddress);
      
      SOAPEnvelope resEnv = resMsg.getSOAPPart().getEnvelope();
      Name name = resEnv.createName("echoSimpleResponse", "ns1", "http://org.jboss.test.ws/jbws812");
      SOAPElement soapElement = (SOAPElement)resMsg.getSOAPBody().getChildElements(name).next();
      soapElement = (SOAPElement)soapElement.getChildElements(resEnv.createName("result")).next();
      
      String resValue = soapElement.getValue();
      assertEquals(160, resValue.charAt(0));
   }


   /** Test client proxy API
    */
   public void testClientProxy() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
      TestEndpoint port = (TestEndpoint)service.getPort(TestEndpoint.class);
      
      String resStr = port.echoSimple("\u00a0");
      assertEquals(160, resStr.charAt(0));
   }
}
