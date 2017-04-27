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
package org.jboss.test.ws.jaxrpc.jbws84;

import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Document;

/**
 * @author Thomas.Diesler@jboss.org
 * @since 26-Nov-2004
 */
public class MessageJavaBean implements Message
{
   // provide logging
   private final Logger log = Logger.getLogger(MessageJavaBean.class);

   /** javax.xml.soap.SOAPElement
    */
   public SOAPElement processSOAPElement(SOAPElement reqElement) throws RemoteException
   {
      String msgStr = DOMWriter.printNode(reqElement, true);
      log.info("processSOAPElement: " + msgStr);

      try
      {
         SOAPFactory soapFactory = SOAPFactory.newInstance();

         Name name = soapFactory.createName("Order", PREFIX, NAMESPACE_URI);
         Name elementName = reqElement.getElementName();
         if (name.equals(elementName) == false)
            throw new IllegalArgumentException("Unexpected element: " + elementName);

         name = soapFactory.createName("Customer");
         SOAPElement custElement = (SOAPElement)reqElement.getChildElements(name).next();
         String elementValue = custElement.getValue();
         if ("Customer".equals(custElement.getLocalName()) && "Kermit".equals(elementValue) == false)
            throw new IllegalArgumentException("Unexpected element value: " + elementValue);

         name = soapFactory.createName("Item");
         SOAPElement itemElement = (SOAPElement)reqElement.getChildElements(name).next();
         elementValue = itemElement.getValue();
         if ("Item".equals(itemElement.getLocalName()) && "Ferrari".equals(elementValue) == false)
            throw new IllegalArgumentException("Unexpected element value: " + elementValue);

         MessageFactory msgFactory = MessageFactory.newInstance();
         SOAPMessage resMessage = msgFactory.createMessage();
         SOAPBody soapBody = resMessage.getSOAPBody();

         DocumentBuilder builder = getDocumentBuilder();
         Document doc = builder.parse(new ByteArrayInputStream(Message.response.getBytes()));
         soapBody.addDocument(doc);

         SOAPElement resElement = (SOAPElement)soapBody.getChildElements().next();
         return resElement;
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new RemoteException(e.toString(), e);
      }
   }

   private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException
   {
      // Setup document builder
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      docBuilderFactory.setNamespaceAware(true);

      DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
      return builder;
   }
}
