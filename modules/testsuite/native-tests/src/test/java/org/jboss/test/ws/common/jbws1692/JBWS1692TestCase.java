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
package org.jboss.test.ws.common.jbws1692;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import org.jboss.wsf.test.JBossWSTest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SOAPElement.importNode() not supported
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1692
 *
 * @author Thomas.Diesler@jboss.org
 * @since 13-Jun-2006
 */
public class JBWS1692TestCase extends JBossWSTest
{
   public void testImportNode() throws Exception
   {
      MessageFactory factory = MessageFactory.newInstance();
      File soapreqfile = getResourceFile("common/jbws1692/soap-request-template.xml");
      SOAPMessage msg = factory.createMessage(null, new FileInputStream(soapreqfile));

      // Get the node that needs to be imported
      Node someNode = getNode(getResourceFile("common/jbws1692/import-node.xml").getPath());

      // Import the node
      Node importedNode = msg.getSOAPPart().getOwnerDocument().importNode(someNode, true);
      
      // Append the node to the first child
      msg.getSOAPBody().getFirstChild().appendChild(importedNode);
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      msg.writeTo(baos);
      String resXML = new String(baos.toByteArray());
      assertTrue("Invalid response: " + resXML, resXML.indexOf("<a xmlns='urn:custom'><b:Request xmlns:b='urn:custom-ns'/></a>") > 0);
   }
   
   private Node getNode(String xmlFile) throws Exception
   {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setIgnoringComments(true);

      File requestFile = new File(xmlFile);
      FileInputStream fis = new FileInputStream(requestFile);

      Document doc = factory.newDocumentBuilder().parse(fis);
      NodeList nodes = doc.getElementsByTagNameNS("urn:custom-ns", "Request");
      return nodes.item(0);
   }
}
