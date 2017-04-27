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
package org.jboss.test.ws.jaxrpc.jbws1303;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.Constants;

public class ServerHandler extends GenericHandler
{
   public QName[] getHeaders()
   {
      return new QName[] {};
   }

   /**
    * <env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/' 
    *               xmlns:xsi='http://www.w3c.org/2001/XMLSchema-instance' 
    &               xmlns:xsd='http://www.w3.org/2001/XMLSchema'>
    *  <env:Header/>
    *  <env:Body>
    *   <lastmodResponse xmlns='http://netid.msu.edu:8080/lastmod.pl'>
    *    <TimeChanged xsi:type='xsd:string'>yesterday</TimeChanged>
    *   </lastmodResponse>
    *  </env:Body>
    * </env:Envelope>
    */
   public boolean handleResponse(MessageContext msgContext)
   {
      try
      {
         SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
         SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
         soapEnvelope.addNamespaceDeclaration(Constants.PREFIX_XSD, Constants.NS_SCHEMA_XSD);
         soapEnvelope.addNamespaceDeclaration(Constants.PREFIX_XSI, Constants.NS_SCHEMA_XSI);
         SOAPElement bodyElement = soapMessage.getSOAPBody().addChildElement("lastmodResponse");
         bodyElement.setAttribute("xmlns", "http://netid.msu.edu:8080/lastmod.pl");
         SOAPElement soapElement = bodyElement.addChildElement("TimeChanged");
         soapElement.setAttributeNS("xsi", "type", "xsd:string");
         soapElement.setValue("yesterday");
         ((SOAPMessageContext)msgContext).setMessage(soapMessage);
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
      return true;
   }
}
