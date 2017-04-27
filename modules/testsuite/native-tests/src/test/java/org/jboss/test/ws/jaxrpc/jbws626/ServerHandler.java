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
package org.jboss.test.ws.jaxrpc.jbws626;

import org.jboss.ws.core.CommonMessageContext;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;


public class ServerHandler extends GenericHandler
{
   public QName[] getHeaders()
   {
      return new QName[] {};
   }

   public boolean handleRequest(MessageContext msgContext)
   {
      try
      {
         SOAPFactory factory = SOAPFactory.newInstance();
         
         SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
         SOAPBody soapBody = soapMessage.getSOAPBody();
         SOAPElement soapElement = (SOAPElement)soapBody.getChildElements().next();
         
         // Verify RPC element
         Name expName = factory.createName("echoArray", "ns1", "http://org.jboss.test.webservice/jbws626");
         Name wasName = soapElement.getElementName();
         if (expName.equals(wasName))
         {
            // Verify array wrapper element
            soapElement = (SOAPElement)soapElement.getChildElements().next();
            expName = factory.createName("arrayOfValueObj_1");
            wasName = soapElement.getElementName();
            assertElementName(expName, wasName);
            
            // Verify array element
            soapElement = (SOAPElement)soapElement.getChildElements().next();
            expName = factory.createName("value");
            wasName = soapElement.getElementName();
            assertElementName(expName, wasName);
            
            // Verify array item element
            Iterator arrayItems = soapElement.getChildElements();
            soapElement = (SOAPElement)arrayItems.next();
            expName = factory.createName("s1");
            wasName = soapElement.getElementName();
            assertElementName(expName, wasName);
            
            // Verify array item element
            soapElement = (SOAPElement)arrayItems.next();
            expName = factory.createName("s2");
            wasName = soapElement.getElementName();
            assertElementName(expName, wasName);
         }

         // for testing the CommonBindingProvider memory leak. Not related to this test...
         ((CommonMessageContext)msgContext).setModified(true);

      }
      catch (SOAPException ex)
      {
         throw new JAXRPCException(ex);
      }
      return true;
   }

   public boolean handleResponse(MessageContext msgContext)
   {
      return true;
   }

   private void assertElementName(Name expName, Name wasName)
   {
      if (expName.equals(wasName) == false)
         throw new JAXRPCException("Expected element name '" + expName + "', but was '" + wasName + "'");
   }
}
