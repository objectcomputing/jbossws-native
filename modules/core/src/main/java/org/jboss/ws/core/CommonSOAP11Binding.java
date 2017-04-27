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
package org.jboss.ws.core;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

import org.jboss.ws.Constants;
import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.core.soap.SOAPFaultImpl;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.core.soap.Use;
import org.jboss.ws.metadata.umdm.OperationMetaData;

/**
 * The SOAP11Binding  
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jul-2006
 */
public abstract class CommonSOAP11Binding extends CommonSOAPBinding
{
   private MessageFactoryImpl msgFactory;

   public CommonSOAP11Binding()
   {
      msgFactory = new MessageFactoryImpl();
      msgFactory.setEnvNamespace(Constants.NS_SOAP11_ENV);
   }

   /** Create the SOAP-1.1 message */
   protected MessageAbstraction createMessage(OperationMetaData opMetaData) throws SOAPException
   {
      SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgFactory.createMessage();
      
      Use encStyle = opMetaData.getEndpointMetaData().getEncodingStyle();
      if (Use.ENCODED.equals(encStyle))
      {
         SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
         soapEnvelope.addNamespaceDeclaration(Constants.PREFIX_SOAP11_ENC, Constants.URI_SOAP11_ENC);
      }
         
      return soapMessage;
   }
   
   public abstract Set<String> getRoles();
   
   @Override
   protected void verifyUnderstoodHeader(SOAPHeaderElement element) throws Exception
   {      
      QName name = new QName(element.getNamespaceURI(), element.getLocalName());
      String actor = element.getActor();
      Set<String> roles = getRoles();
      
      boolean isActor = actor == null || actor.length() == 0 || Constants.URI_SOAP11_NEXT_ACTOR.equals(actor) || roles.contains(actor);
      if (isActor && !headerSource.getHeaders().contains(name))
      {
         QName faultCode = Constants.SOAP11_FAULT_CODE_MUST_UNDERSTAND;
         String faultString = "Unprocessed 'mustUnderstand' header element: " + element.getElementName();
         SOAPFaultImpl fault = new SOAPFaultImpl();
         fault.setFaultCode(faultCode);
         fault.setFaultString(faultString);
         throwFaultException(fault);
      }
   }
}
