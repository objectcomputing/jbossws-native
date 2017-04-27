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
package org.jboss.test.ws.jaxrpc.jbws707;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.ws.core.soap.SOAPBodyImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** 
 * White spaces within <![CDATA[ ]]> element of a SOAP message are not retained
 * http://jira.jboss.com/jira/browse/JBWS-707
 * 
 * Invalid XML Characters not Properly Handled in Object Serialization
 * http://jira.jboss.com/jira/browse/JBWS-716
 *
 * @author Anil.Saldhana@jboss.org
 * @author Thomas.Diesler@jboss.org
 * @since 16-Feb-2006
 */
public class JBWS707TestCase extends JBossWSTest
{
   private static TestEndpoint port;
   
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS707TestCase.class, "jaxrpc-jbws707.war, jaxrpc-jbws707-client.jar");
   }

   public void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (TestEndpoint)service.getPort(TestEndpoint.class);
      }
   }
   
   public void testSpecialChars() throws Exception
   {
      String inStr = "&Test & this &";
      String outStr = port.echo(inStr);
      assertEquals(inStr, outStr);
      
      inStr = "<Test < this <";
      outStr = port.echo(inStr);
      assertEquals(inStr, outStr);
      
      inStr = ">Test > this >";
      outStr = port.echo(inStr);
      assertEquals(inStr, outStr);
      
      inStr = "\"Test \" this \"";
      outStr = port.echo(inStr);
      assertEquals(inStr, outStr);
   }
   
   public void testSpecialCharsInBean() throws Exception
   {
      UserType in = new UserType("&Test & this &");
      UserType out = port.echo(in);
      assertEquals(in, out);
      
      in = new UserType("<Test < this <");
      out = port.echo(in);
      assertEquals(in, out);
      
      in = new UserType(">Test > this >");
      out = port.echo(in);
      assertEquals(in, out);
      
      in = new UserType("\"Test \" this \"");
      out = port.echo(in);
      assertEquals(in, out);
   }
   
   public void testCDATA() throws Exception
   {
      String xmlStr = "<?xml version='1.0' encoding='UTF-8' ?>" + 
      "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/' xmlns:tns='http://uri.jboss.org'>" + 
      " <env:Body>" + 
      "   <tns:testMessage><![CDATA[  Hello  ]]></tns:testMessage>" +
      " </env:Body>" + 
      "</env:Envelope>";

      String expStr = "  Hello  ";
      assertEquals(expStr, parse(xmlStr));
      
      String out = port.echo(expStr);
      assertEquals(expStr, out);
   }

   public void testCDATAAmpersand() throws Exception
   {
      String xmlStr = 
         "<?xml version='1.0' encoding='UTF-8' ?>" + 
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/' xmlns:tns='http://uri.jboss.org'>" + 
         " <env:Body>" +
         "  <tns:myMessage>WelcometoJBoss!<![CDATA[  Hello  &&]]></tns:myMessage>" +
         " </env:Body>" + 
         "</env:Envelope>";
      
      String expStr = "WelcometoJBoss!  Hello  &&";
      assertEquals(expStr, parse(xmlStr));

      String out = port.echo(expStr);
      assertEquals(expStr, out);
   }

   public void testMultipleCDATA() throws Exception
   {
      String xmlStr = 
         "<?xml version='1.0' encoding='UTF-8' ?>" + 
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/' xmlns:tns='http://uri.jboss.org'>" + 
         " <env:Body>" +
         "  <tns:myMessage><![CDATA[  Hello  ]]>JBoss!<![CDATA[ ]]></tns:myMessage>" +
         " </env:Body>" + 
         "</env:Envelope>";
      
      String expStr = "  Hello  JBoss! ";
      assertEquals(expStr, parse(xmlStr));

      String out = port.echo(expStr);
      assertEquals(expStr, out);
   }

   public void testMultipleCDATAv2() throws Exception
   {
      String myXML = 
         "<?xml version='1.0' encoding='UTF-8' ?>" + 
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/' xmlns:tns='http://uri.jboss.org'>" + 
         " <env:Body>" +
         "  <tns:myMessage><![CDATA[  Hello  ]]>JBoss!<![CDATA[ ]]>.OSS</tns:myMessage>" +
         " </env:Body>" + 
         "</env:Envelope>";
      
      String expStr = "  Hello  JBoss! .OSS";
      assertEquals(expStr, parse(myXML));

      String out = port.echo(expStr);
      assertEquals(expStr, out);
   }

   private String parse(String xmlStr) throws SOAPException, IOException
   {
      MessageFactory mf = MessageFactory.newInstance();
      MimeHeaders mimeHeaders = new MimeHeaders();
      mimeHeaders.addHeader("Content-Type", "text/xml; charset=UTF-8");

      SOAPMessage soapMessage = mf.createMessage(mimeHeaders, new ByteArrayInputStream(xmlStr.getBytes()));
      SOAPBodyImpl soapBody = (SOAPBodyImpl)soapMessage.getSOAPBody();
      SOAPElement soapElement = soapBody.getBodyElement();
      
      StringBuffer builder = new StringBuffer();
      NodeList nlist = soapElement.getChildNodes();
      for (int i = 0; i < nlist.getLength(); i++)
      {
         Node child = nlist.item(i);
         String nodeValue = child.getNodeValue();
         builder.append(nodeValue);
      }

      return builder.toString();
   }
}
