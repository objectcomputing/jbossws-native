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
package org.jboss.test.ws.jaxrpc.jbws168;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;

/**
 * A simple server side handler
 * 
 * @author thomas.diesler@jboss.org
 */
public class HelloHandler extends GenericHandler
{
   // provide logging
   private static final Logger log = Logger.getLogger(HelloHandler.class);

   protected QName[] headers;

   public QName[] getHeaders()
   {
      return headers;
   }

   public void init(HandlerInfo config)
   {
      headers = config.getHeaders();
   }

   public boolean handleRequest(MessageContext msgContext)
   {
      log.info("handleRequest");

      try
      {
         SOAPMessageContext soapContext = (SOAPMessageContext)msgContext;
         SOAPMessage soapMessage = soapContext.getMessage();
         SOAPFactory soapFactory = SOAPFactory.newInstance();

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         soapMessage.writeTo(baos);
         String msgStr = new String(baos.toByteArray());

         System.out.println(msgStr);

         SOAPBody soapBody = soapMessage.getSOAPBody();
         Name name = soapFactory.createName("hello", "ns1", "http://org.jboss.test.webservice/jbws168/types");
         SOAPElement helloElement = (SOAPElement)soapBody.getChildElements(name).next();

         /*
          <ns1:hello xmlns:ns1="http://org.jboss.test.webservice/jbws168/types">
          <UserType_1>
          <propC xsi:nil="1"/>
          <propA>A</propA>
          </UserType_1>
          </ns1:hello>
          */

         validateMessageContent(helloElement, "UserType_1");
      }
      catch (Exception e)
      {
         log.error("Handler processing error", e);
         return false;
      }

      return true;
   }

   public boolean handleResponse(MessageContext msgContext)
   {
      log.info("handleResponse");

      try
      {
         SOAPMessageContext soapContext = (SOAPMessageContext)msgContext;
         SOAPMessage soapMessage = soapContext.getMessage();
         SOAPFactory soapFactory = SOAPFactory.newInstance();

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         soapMessage.writeTo(baos);
         String msgStr = new String(baos.toByteArray());

         System.out.println(msgStr);

         SOAPBody soapBody = soapMessage.getSOAPBody();
         Name name = soapFactory.createName("helloResponse", "ns1", "http://org.jboss.test.webservice/jbws168/types");
         SOAPElement helloElement = (SOAPElement)soapBody.getChildElements(name).next();
         helloElement.getChildElements();

         /*
          <ns1:helloResponse xmlns:ns1="http://org.jboss.test.webservice/jbws168/types">
          <result>
          <propC xsi:nil="1"/>
          <propA>A</propA>
          </result>
          </ns1:helloResponse>
          */

         // The message gets serialized ok, but navigation of the SOAP tree fails
         // http://jira.jboss.com/jira/browse/JBWS-250
         //validateMessageContent(helloElement, "result");
      }
      catch (Exception e)
      {
         log.error("Handler processing error", e);
      }

      return true;
   }

   private void validateMessageContent(SOAPElement element, String rootName) throws SOAPException
   {
      SOAPFactory soapFactory = SOAPFactory.newInstance();
      SOAPElement utElement = (SOAPElement)element.getChildElements(soapFactory.createName(rootName)).next();

      Iterator it = utElement.getChildElements();
      SOAPElement propC = (SOAPElement)it.next();
      if (propC.getElementName().equals(soapFactory.createName("propC")) == false)
         throw new RuntimeException("Unexpected SOAP element: " + propC.getElementName());

      if (propC.hasAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "nil") == false)
         throw new RuntimeException("Cannot find attribute xsi:nil");

      String value = propC.getValue();
      if (value != null)
         throw new RuntimeException("Unexpected text value: " + value);

      SOAPElement propA = (SOAPElement)it.next();
      if (propA.getElementName().equals(soapFactory.createName("propA")) == false)
         throw new RuntimeException("Unexpected SOAP element: " + propA.getElementName());

      value = propA.getValue();
      if ("A".equals(value) == false)
         throw new RuntimeException("Unexpected text value: " + value);

      if (it.hasNext())
         throw new RuntimeException("Unexpected SOAP element");
   }
}
