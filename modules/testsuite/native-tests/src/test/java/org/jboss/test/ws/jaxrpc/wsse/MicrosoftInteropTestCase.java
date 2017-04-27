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
package org.jboss.test.ws.jaxrpc.wsse;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.SecurityDecoder;
import org.jboss.ws.extensions.security.SecurityStore;
import org.jboss.ws.extensions.security.Util;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 * This test simulates a Microsoft WSE Client request
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 09/26/2005
 */
public class MicrosoftInteropTestCase extends JBossWSTest
{
   
   /** Test that we can build an envelope from InputStream */
   public void testMicrosoftRequest() throws Exception
   {
      String expEnv =
         "<?xml version='1.0' encoding='UTF-8'?>" +
         "<soap:Envelope" +
         "  xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'" +
         "  xmlns:wsa='http://schemas.xmlsoap.org/ws/2004/03/addressing'" +
         "  xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd'" +
         "  xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
         "  <soap:Header>" +
         "    <wsa:Action/>" +
         "    <wsa:MessageID>uuid:68a732d8-b630-4ded-bef0-9b83e735e0c7</wsa:MessageID>" +
         "    <wsa:ReplyTo>" +
         "      <wsa:Address>http://schemas.xmlsoap.org/ws/2004/03/addressing/role/anonymous</wsa:Address>" +
         "    </wsa:ReplyTo>" +
         "    <wsa:To>http://draught:8081/jbossws-jaxrpc-jse</wsa:To>" +
         "  </soap:Header>" +
         "  <soap:Body>" +
         "    <echoString xmlns='http://org.jboss.ws/jaxrpc/types'>" +
         "      <String_1 xmlns=''>test</String_1>" +
         "      <String_2 xmlns=''>this</String_2>" +
         "    </echoString>" +
         "  </soap:Body>" +
         "</soap:Envelope>";

      InputStream inputStream = new FileInputStream(getResourceFile("jaxrpc/wsse/interop/microsoft-wse.xml").getPath());

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMsg = factory.createMessage(null, inputStream);
      SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();

      // The Microsoft WSE message is timestamp protected like so:
      // <wsu:Timestamp wsu:Id="Timestamp-9e3e6632-f2a1-4b26-a682-9301a75003a7">
      //   <wsu:Created>2005-09-26T22:17:32Z</wsu:Created>
      //   <wsu:Expires>2005-09-26T22:22:32Z</wsu:Expires>
      // </wsu:Timestamp>

      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      cal.set(Calendar.DAY_OF_MONTH, 26);
      cal.set(Calendar.MONTH, 8);
      cal.set(Calendar.YEAR, 2005);
      cal.set(Calendar.HOUR_OF_DAY, 22);
      cal.set(Calendar.MINUTE, 22);
      cal.set(Calendar.SECOND, 25);

      SecurityDecoder decoder = new SecurityDecoder(new SecurityStore(), cal, null, null, null);
      decoder.decode(soapEnv.getOwnerDocument());
      decoder.complete();

      cleanupWsuIds(soapEnv);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      soapMsg.writeTo(baos);
      String wasEnv = new String(baos.toByteArray());
      
      assertEquals(DOMUtils.parse(expEnv), DOMUtils.parse(wasEnv));
   }

   // WS-Security leaves wsu:id attributes arround on elements which are not cleaned
   // up due to performance reasons. This, however, breaks comparisons, so we manually
   // fix this for tests.
   private void cleanupWsuIds(Element element)
   {
      element.removeAttributeNS(Constants.WSU_NS, "Id");
      element.removeAttribute("xmlns:wsu");

      Element child = Util.getFirstChildElement(element);
      while (child != null)
      {
         cleanupWsuIds(child);
         child = Util.getNextSiblingElement(child);
      }
   }
}
