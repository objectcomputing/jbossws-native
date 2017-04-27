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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.wsf.test.JBossWSTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Test for JBWS-2186
 *
 * @author alessio.soldano@jboss.org
 * @since 28-Jul-2008
 */
public class JBWS2186TestCase extends JBossWSTest
{
   public void test() throws Exception
   {
      MessageFactory messageFactory = MessageFactory.newInstance();
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      SOAPMessage message = messageFactory.createMessage();
      Document document = createDocument();
      SOAPBody body = message.getSOAPBody();
      transformer.transform(new DOMSource(document), new DOMResult(body));
      body = message.getSOAPBody();
      transformer.transform(new DOMSource(body), new StreamResult(System.out));
   }
   
   private Document createDocument() throws Exception {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder documentBuilder = factory.newDocumentBuilder();
      Document document = documentBuilder.newDocument();
      Element getLastTradePrice = document.createElementNS("http://example.com", "m:GetLastTradePrice");
      document.appendChild(getLastTradePrice);
      Element symbol = document.createElement("symbol");
      getLastTradePrice.appendChild(symbol);
      Text def = document.createTextNode("DEF");
      symbol.appendChild(def);
      return document;
  }
}
