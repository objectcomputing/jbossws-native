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
package org.jboss.ws.core.soap;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;

import org.jboss.logging.Logger;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;

/**
 * Derive the operation meta data from incomming SOAP message
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 22-Nov-2005
 */
public class SOAPMessageDispatcher
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPMessageDispatcher.class);

   /** Get the operation meta data for a given SOAP message
    */
   public OperationMetaData getDispatchDestination(EndpointMetaData epMetaData, SOAPMessage soapMessage) throws SOAPException
   {
      OperationMetaData opMetaData = null;

      // Dispatch based on wsa:Action
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      AddressingProperties inProps = (AddressingProperties)msgContext.get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
      if (inProps != null && inProps.getAction() != null)
      {
         String wsaAction = inProps.getAction().getURI().toASCIIString();
         for (OperationMetaData opAux : epMetaData.getOperations())
         {
            if (wsaAction.equals(opAux.getSOAPAction()))
            {
               opMetaData = opAux;
               log.debug("Use wsa:Action dispatch: " + wsaAction);
               break;
            }
         }
      }

      // Dispatch to JAXWS Provider
      if (opMetaData == null && epMetaData.getServiceMode() != null)
      {
         QName xmlName = new QName(epMetaData.getPortName().getNamespaceURI(), "invoke");
         opMetaData = epMetaData.getOperation(xmlName);
      }

      // Dispatch based on SOAPBodyElement name
      if (opMetaData == null)
      {
         SOAPBody soapBody = soapMessage.getSOAPBody();

         SOAPBodyElement soapBodyElement = null;
         Iterator bodyChildren = soapBody.getChildElements();
         while (bodyChildren.hasNext() && soapBodyElement == null)
         {
            Object childNode = bodyChildren.next();
            if (childNode instanceof SOAPBodyElement)
            {
               soapBodyElement = (SOAPBodyElement)childNode;
               //soapBodyElement.getValue(); //force transition to DOM-Valid model
            }
         }

         if (soapBodyElement == null)
         {
            boolean wsrmDisabled = epMetaData.getConfig().getRMMetaData() == null; 
            if ((epMetaData.getStyle() == Style.RPC) && (wsrmDisabled)) // RM hack
               throw new SOAPException("Empty SOAP body with no child element not supported for RPC");

            // [JBWS-1125] Support empty soap body elements
            for (OperationMetaData opAux : epMetaData.getOperations())
            {
               if (opAux.getParameters().size() == 0)
               {
                  log.debug ("Dispatching empty SOAP body");
                  opMetaData = opAux;
                  break;
               }
            }
         }
         else
         {
            Name soapName = soapBodyElement.getElementName();
            QName xmlElementName = new QName(soapName.getURI(), soapName.getLocalName());
            opMetaData = epMetaData.getOperation(xmlElementName);
         }
      }

      // Dispatch to a generic operation that takes an org.w3c.dom.Element
      if (opMetaData == null)
      {
         for (OperationMetaData opAux : epMetaData.getOperations())
         {
            if (opAux.isMessageEndpoint())
            {
               log.debug("Use generic message style dispatch");
               opMetaData = opAux;
               break;
            }
         }
      }

      log.debug("getDispatchDestination: " + (opMetaData != null ? opMetaData.getQName() : null));
      return opMetaData;
   }
}
