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
package org.jboss.test.ws.jaxrpc.encoded.href;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;

public class HRefHandler extends GenericHandler
{
   // Provide logging
   private static Logger log = Logger.getLogger(HRefHandler.class);
   
   private static boolean hrefEncoding;

   protected QName[] headers;

   public QName[] getHeaders()
   {
      return headers;
   }

   public void init(HandlerInfo info)
   {
      log.info("init: " + info);
      headers = info.getHeaders();
   }
   
   public static void setHrefEncoding(boolean flag)
   {
      hrefEncoding = flag;
   }

   public boolean handleRequest(MessageContext msgContext)
   {
      log.info("handleRequest");

      if (hrefEncoding)
      {
         try
         {
           String envStr = 
              "<env:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
                "<env:Body env:encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' xmlns:enc='http://schemas.xmlsoap.org/soap/encoding/'>" +
                  "<ns1:base64BinaryTest xmlns:ns1='http://marshalltestservice.org/wsdl'>" +
                    "<arrayOfbyte_1 href='#ID1'/>" +
                  "</ns1:base64BinaryTest>" +
                  "<enc:base64 id='ID1' xsi:type='enc:base64'>SHJlZkVuY29kZWRSZXF1ZXN0</enc:base64>" +
                "</env:Body>" +
              "</env:Envelope>";
           
           MessageFactory factory = MessageFactory.newInstance();
           SOAPMessage reqMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
           ((SOAPMessageContext)msgContext).setMessage(reqMessage);
        }
        catch (Exception e)
        {
           throw  new JAXRPCException(e);
        }
      }

      return true;
   }
   

   public boolean handleResponse(MessageContext msgContext)
   {
      log.info("handleResponse");

      if (hrefEncoding)
      {
         try
         {
           String envStr = 
              "<env:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
                "<env:Body env:encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' xmlns:enc='http://schemas.xmlsoap.org/soap/encoding/'>" +
                  "<ns1:base64BinaryTestResponse xmlns:ns1='http://marshalltestservice.org/wsdl'>" +
                    "<result href='#ID1'/>" +
                  "</ns1:base64BinaryTestResponse>" +
                  "<enc:base64 id='ID1' xsi:type='enc:base64'>SHJlZkVuY29kZWRSZXNwb25zZQ==</enc:base64>" +
                "</env:Body>" +
              "</env:Envelope>";
           
           MessageFactory factory = MessageFactory.newInstance();
           SOAPMessage resMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
           ((SOAPMessageContext)msgContext).setMessage(resMessage);
        }
        catch (Exception e)
        {
           throw  new JAXRPCException(e);
        }
      }

      return true;
   }
}
