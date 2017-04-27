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
package org.jboss.test.ws.jaxws.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.jboss.ws.extensions.json.BadgerFishDOMDocumentParser;
import org.jboss.ws.extensions.json.BadgerFishDOMDocumentSerializer;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.wsf.test.JBossWSTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test Json API 
 *
 * @author Thomas.Diesler@jboss.com
 * @since 12-Mar-2008
 */
public class JsonAPITestCase extends JBossWSTest
{
   public void testSimple() throws Exception
   {
      String xmlStr = "<kermit>the frog</kermit>";
      String expStr = "{\"kermit\":{\"$\":\"the frog\"}}";
      String resStr = toJSON(DOMUtils.parse(xmlStr));
      assertEquals("Unexpected result: " + resStr, expStr, resStr);

      String resXML = toXML(resStr);
      assertEquals("Unexpected result: " + resXML, xmlStr, resXML);
   }

   public void testSimpleAttribute() throws Exception
   {
      String xmlStr = "<kermit mygirl='piggy'>the frog</kermit>";
      String expStr = "{\"kermit\":{\"@mygirl\":\"piggy\",\"$\":\"the frog\"}}";
      String resStr = toJSON(DOMUtils.parse(xmlStr));
      assertEquals("Unexpected result: " + resStr, expStr, resStr);

      String resXML = toXML(resStr);
      assertEquals("Unexpected result: " + resXML, xmlStr, resXML);
   }

   public void testDefaultNamespace() throws Exception
   {
      String xmlStr = "<kermit xmlns='http://somens'>the frog</kermit>";
      String expStr = "{\"kermit\":{\"@xmlns\":{\"$\":\"http:\\/\\/somens\"},\"$\":\"the frog\"}}";
      String resStr = toJSON(DOMUtils.parse(xmlStr));
      assertEquals("Unexpected result: " + resStr, expStr, resStr);

      String resXML = toXML(resStr);
      assertEquals("Unexpected result: " + resXML, xmlStr, resXML);
   }

   public void testElementNamespace() throws Exception
   {
      String xmlStr = "<ns1:kermit xmlns:ns1='http://somens'>the frog</ns1:kermit>";
      String expStr = "{\"ns1:kermit\":{\"@xmlns\":{\"ns1\":\"http:\\/\\/somens\"},\"$\":\"the frog\"}}";
      String resStr = toJSON(DOMUtils.parse(xmlStr));
      assertEquals("Unexpected result: " + resStr, expStr, resStr);

      String resXML = toXML(resStr);
      assertEquals("Unexpected result: " + resXML, xmlStr, resXML);
   }

   public void testElementAttributeNamespace() throws Exception
   {
      String xmlStr = "<ns1:kermit ns1:mygirl='piggy' xmlns:ns1='http://somens'>the frog</ns1:kermit>";
      String expStr = "{\"ns1:kermit\":{\"@xmlns\":{\"ns1\":\"http:\\/\\/somens\"},\"@ns1:mygirl\":\"piggy\",\"$\":\"the frog\"}}";
      String resStr = toJSON(DOMUtils.parse(xmlStr));
      assertEquals("Unexpected result: " + resStr, expStr, resStr);

      String resXML = toXML(resStr);
      assertEquals("Unexpected result: " + resXML, xmlStr, resXML);
   }

   private String toJSON(Element srcDOM) throws Exception
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      new BadgerFishDOMDocumentSerializer(baos).serialize(srcDOM);
      return new String(baos.toByteArray());
   }

   private String toXML(String jsonStr) throws Exception
   {
      ByteArrayInputStream bais = new ByteArrayInputStream(jsonStr.getBytes());
      Document resDOM = new BadgerFishDOMDocumentParser().parse(bais);
      return DOMWriter.printNode(resDOM, false);
   }
}
