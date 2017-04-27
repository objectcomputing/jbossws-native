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
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This test simulates a Sun JWSDP Client request
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 09/26/2005
 */
public class SunInteropTestCase extends JBossWSTest
{
   /** Test that we can build an envelope from InputStream */
   public void testSunSTRRequest() throws Exception
   {
      InputStream inputStream = new FileInputStream(getResourceFile("jaxrpc/wsse/interop/sun-xws.xml").getPath());

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMsg = factory.createMessage(null, inputStream);
      SOAPEnvelope env = soapMsg.getSOAPPart().getEnvelope();
      Document doc = env.getOwnerDocument();

      // The Sun JWSDP message is timestamp protected like so:
      // <wsu:Timestamp wsu:Id="Timestamp-9e3e6632-f2a1-4b26-a682-9301a75003a7">
      //   <wsu:Created>2005-09-26T22:17:32Z</wsu:Created>
      //   <wsu:Expires>2005-09-26T22:22:32Z</wsu:Expires>
      // </wsu:Timestamp>

      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      cal.set(Calendar.DAY_OF_MONTH, 28);
      cal.set(Calendar.MONTH, 8);
      cal.set(Calendar.YEAR, 2005);
      cal.set(Calendar.HOUR_OF_DAY, 5);
      cal.set(Calendar.MINUTE, 32);
      cal.set(Calendar.SECOND, 25);

      SecurityDecoder decoder = new SecurityDecoder(new SecurityStore(), cal, null, null, null);
      decoder.decode(doc);
      decoder.complete();

      cleanupWsuIds(doc.getDocumentElement());

      String decodedString = DOMWriter.printNode(doc, true);
      log.debug("Decoded message:" + decodedString);
   }

   /** Test that we can build an envelope from InputStream */
   public void testSunIssuerSerialSignEncrypt() throws Exception
   {
      InputStream inputStream = new FileInputStream(getResourceFile("jaxrpc/wsse/interop/sun-xws-issuerserial-sign-encrypt.xml").getPath());

      MessageFactory factory = new MessageFactoryImpl();
      SOAPMessage soapMsg = factory.createMessage(null, inputStream);
      SOAPEnvelope env = soapMsg.getSOAPPart().getEnvelope();
      Document doc = env.getOwnerDocument();

      // The Sun JWSDP message is timestamp protected like so:
      // <wsu:Timestamp wsu:Id="Timestamp-9e3e6632-f2a1-4b26-a682-9301a75003a7">
      //   <wsu:Created>2005-09-26T22:17:32Z</wsu:Created>
      //   <wsu:Expires>2005-09-26T22:22:32Z</wsu:Expires>
      // </wsu:Timestamp>

      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      cal.set(Calendar.DAY_OF_MONTH, 30);
      cal.set(Calendar.MONTH, 8);
      cal.set(Calendar.YEAR, 2005);
      cal.set(Calendar.HOUR_OF_DAY, 22);
      cal.set(Calendar.MINUTE, 8);
      cal.set(Calendar.SECOND, 40);


      SecurityDecoder decoder = new SecurityDecoder(new SecurityStore(), cal, null, null, null);
      decoder.decode(doc);
      decoder.complete();

      cleanupWsuIds(doc.getDocumentElement());

      String decodedString = DOMWriter.printNode(doc, true);
      log.debug("Decoded message:" + decodedString);
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
