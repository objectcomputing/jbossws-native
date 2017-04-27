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
package org.jboss.test.ws.jaxrpc.jbws775;

import java.io.ByteArrayInputStream;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.jboss.wsf.common.DOMWriter;

/**
 * ComplexType cannot be constructed from attributes
 * 
 * http://jira.jboss.com/jira/browse/JBWS-775
 *
 * @author Thomas.Diesler@jboss.org
 * @since 24-Mar-2006
 */
public class JBWS775TestCase extends JBossWSTest
{
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS775TestCase.class, "jaxrpc-jbws775.war, jaxrpc-jbws775-client.jar");
   }

   public void testEndpointAccess() throws Exception
   {
      InitialContext iniCtx = getInitialContext();
      Service service = (Service)iniCtx.lookup("java:comp/env/service/DocumentTranslator");
      DocumentTranslator port = (DocumentTranslator)service.getPort(DocumentTranslator.class);

      TDocumentHead tDocHead = new TDocumentHead("title", "en");
      TDocumentBody tDocBody = new TDocumentBody(new String[] {"hi", "bye"});
      TDocument tDocReq = new TDocument(tDocHead, tDocBody);
      TTranslationRequest tReq = new TTranslationRequest("es", tDocReq);

      TDocument tDocRes = port.translate(tReq);
      assertEquals("en", tDocRes.getHead().getLanguage());
      assertEquals("title", tDocRes.getHead().getTitle());
      assertEquals("hi", tDocRes.getBody().getParagraph()[0]);
      assertEquals("bye", tDocRes.getBody().getParagraph()[1]);
   }

   public void testSAAJAccess() throws Exception
   {
      String reqStr =
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
         " <env:Body>" +
         "  <sns:translationRequest targetLanguage='es' xmlns:sns='http://example.com/translator/types' xmlns:vendor='http://jbpm.org/bpel'>" +
         "   <sns:document>" +
         "    <head language='en' title='title'/>" +
         "    <body>" +
         "     <paragraph>hi</paragraph>" +
         "     <paragraph>bye</paragraph>" +
         "    </body>" +
         "   </sns:document>" +
         "  </sns:translationRequest>" +
         " </env:Body>" +
         "</env:Envelope>";

      String resStr =
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
          "<env:Header/>" +
          "<env:Body>" +
           "<ns1:document xmlns:ns1='http://example.com/translator/types' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
            "<head language='en' title='title'/>" +
            "<body>" +
             "<paragraph>hi</paragraph>" +
             "<paragraph>bye</paragraph>" +
            "</body>" +
           "</ns1:document>" +
          "</env:Body>" +
         "</env:Envelope>";

      SOAPMessage reqMsg = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(reqStr.getBytes()));
      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
      SOAPMessage resMsg = con.call(reqMsg, "http://" + getServerHost() + ":8080/jaxrpc-jbws775/document");
      SOAPEnvelope resEnv = resMsg.getSOAPPart().getEnvelope();

      assertEquals(resStr, DOMWriter.printNode(resEnv, false));
   }
}
