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
package org.jboss.test.ws.jaxws.fastinfoset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.feature.FastInfosetFeature;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.wsf.test.JBossWSTest;
import org.jvnet.fastinfoset.FastInfosetException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.xml.fastinfoset.dom.DOMDocumentParser;
import com.sun.xml.fastinfoset.dom.DOMDocumentSerializer;

/**
 * Test FastInfoset functionality
 *
 * @author Thomas.Diesler@jboss.com
 * @since 12-Mar-2008
 */
public class FastInfosetAPITestCase extends JBossWSTest
{
   public void testSimple() throws Exception
   {
      String srcXML = "<root>hello world</root>";
      ByteArrayInputStream bais = getFastInputStream(srcXML);
      Document resDoc = getFastDocument(bais);
      String resXML = DOMWriter.printNode(resDoc, false);
      assertEquals(srcXML, resXML);
   }
   
   public void testSimpleNamespace() throws Exception
   {
      String srcXML = "<root xmlns='http://somens'>hello world</root>";
      ByteArrayInputStream bais = getFastInputStream(srcXML);
      Document resDoc = getFastDocument(bais);
      String resXML = DOMWriter.printNode(resDoc, false);
      assertEquals(srcXML, resXML);
   }
   
   public void testPrefixedNamespace() throws Exception
   {
      String srcXML = "<ns1:root xmlns:ns1='http://somens'>hello world</ns1:root>";
      ByteArrayInputStream bais = getFastInputStream(srcXML);
      Document resDoc = getFastDocument(bais);
      String resXML = DOMWriter.printNode(resDoc, false);
      assertEquals(srcXML, resXML);
   }

   public void testMessageFactory() throws Exception
   {
      String srcXML = 
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
         " <env:Body>" +
         "  <ns1:echo xmlns:ns1='http://org.jboss.ws/fastinfoset'><arg0>hello world</arg0></ns1:echo>" +
         " </env:Body>" +
         "</env:Envelope>";
      
      Element srcEnv = DOMUtils.parse(srcXML);
      ByteArrayInputStream bais = getFastInputStream(srcXML);

      MessageFactoryImpl factory = new MessageFactoryImpl();
      factory.addFeature(new FastInfosetFeature());
      SOAPMessage soapMessage = factory.createMessage(null, bais);
      SOAPEnvelope resEnv = soapMessage.getSOAPPart().getEnvelope();
      assertEquals(srcEnv, resEnv);
   }

   private ByteArrayInputStream getFastInputStream(String srcXML) throws IOException
   {
      DOMDocumentSerializer serializer = new DOMDocumentSerializer();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      serializer.setOutputStream(baos);
      Element srcRoot = DOMUtils.parse(srcXML);
      serializer.serialize(srcRoot);

      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      return bais;
   }

   private Document getFastDocument(ByteArrayInputStream bais) throws FastInfosetException, IOException
   {
      DOMDocumentParser parser = new DOMDocumentParser();
      Document resDoc = DOMUtils.getDocumentBuilder().newDocument();
      parser.parse(resDoc, bais);
      return resDoc;
   }
}
