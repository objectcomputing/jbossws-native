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
package org.jboss.test.ws.common.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Test the DOMWriter
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Aug-2006
 */
public class SOAPMessageWriterTestCase extends JBossWSTest
{
   public void testEnvelopeWriter() throws Exception
   {
      String expEnv = "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>"
         + "  <env:Header xmlns:wsa='http://www.w3.org/2005/08/addressing'>"
         + "    <wsa:To>http://fabrikam123.example/Purchasing</wsa:To>"
         + "    <wsa:ReplyTo>"
         + "      <wsa:Address>http://business456.example/client1</wsa:Address>"
         + "      <wsa:ReferenceParameters>"
         + "        <ns1:sessionid xmlns:ns1='http://somens'>someuniqueid</ns1:sessionid>"
         + "      </wsa:ReferenceParameters>"
         + "    </wsa:ReplyTo>"
         + "    <wsa:Action>http://fabrikam123.example/SubmitPO</wsa:Action>"
         + "    <wsa:MessageID>uuid:6B29FC40-CA47-1067-B31D-00DD010662DA</wsa:MessageID>"
         + "  </env:Header>" 
         + "  <env:Body/>" 
         + "</env:Envelope>";

      MessageFactory factory = MessageFactory.newInstance();
      SOAPMessage soapMessage = factory.createMessage(null, new ByteArrayInputStream(expEnv.getBytes()));

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      soapMessage.writeTo(baos);
      String wasEnv = new String(baos.toByteArray());
      
      assertEquals(DOMUtils.parse(expEnv), DOMUtils.parse(wasEnv));
   }
}
