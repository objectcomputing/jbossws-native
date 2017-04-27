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
package org.jboss.ws.extensions.wsrm.common.serialization;

import static org.jboss.ws.extensions.wsrm.common.serialization.RMSerializationHelper.stringToLong;
import static org.jboss.ws.extensions.wsrm.common.serialization.RMSerializationHelper.getOptionalElement;
import static org.jboss.ws.extensions.wsrm.common.serialization.RMSerializationHelper.getRequiredElement;
import static org.jboss.ws.extensions.wsrm.common.serialization.RMSerializationHelper.getRequiredTextContent;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMAckRequested;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;

/**
 * <b>AckRequested</b> object de/serializer
 * @author richard.opalka@jboss.com
 */
final class RMAckRequestedSerializer implements RMSerializer
{

   private static final RMSerializer INSTANCE = new RMAckRequestedSerializer();
   
   private RMAckRequestedSerializer()
   {
      // hide constructor
   }
   
   static RMSerializer getInstance()
   {
      return INSTANCE;
   }
   
   /**
    * Deserialize <b>AckRequested</b> using <b>provider</b> from the <b>soapMessage</b>
    * @param object to be deserialized
    * @param provider wsrm provider to be used for deserialization process
    * @param soapMessage soap message from which object will be deserialized
    */
   public final void deserialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMAckRequested o = (RMAckRequested)object;
      try
      {
         SOAPHeader soapHeader = soapMessage.getSOAPPart().getEnvelope().getHeader();
         RMConstants wsrmConstants = provider.getConstants();
         
         // read required wsrm:AckRequested element
         QName ackRequestedQName = wsrmConstants.getAckRequestedQName();
         SOAPElement ackRequestedElement = getRequiredElement(soapHeader, ackRequestedQName, "soap header");

         // read required wsrm:Identifier element
         QName identifierQName = wsrmConstants.getIdentifierQName();
         SOAPElement identifierElement = getRequiredElement(ackRequestedElement, identifierQName, ackRequestedQName);
         String identifier = getRequiredTextContent(identifierElement, identifierQName);
         o.setIdentifier(identifier);
         
         // read optional wsrm:MessageNumber element
         QName messageNumberQName = wsrmConstants.getMessageNumberQName();
         SOAPElement messageNumberElement = getOptionalElement(ackRequestedElement, messageNumberQName, ackRequestedQName);
         if (messageNumberElement != null)
         {
            String messageNumberString = getRequiredTextContent(messageNumberElement, messageNumberQName);
            long messageNumberValue = stringToLong(messageNumberString, "Unable to parse MessageNumber element text content");
            o.setMessageNumber(messageNumberValue);
         }
      }
      catch (SOAPException se)
      {
         throw new RMException("Unable to deserialize RM message", se);
      }
      catch (RuntimeException re)
      {
         throw new RMException("Unable to deserialize RM message", re);
      }
   }

   /**
    * Serialize <b>AckRequested</b> using <b>provider</b> to the <b>soapMessage</b>
    * @param object to be serialized
    * @param provider wsrm provider to be used for serialization process
    * @param soapMessage soap message to which object will be serialized
    */
   public final void serialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMAckRequested o = (RMAckRequested)object;
      try
      {
         SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
         RMConstants wsrmConstants = provider.getConstants();
         
         // Add xmlns:wsrm declaration
         soapEnvelope.addNamespaceDeclaration(wsrmConstants.getPrefix(), wsrmConstants.getNamespaceURI());

         // write required wsrm:AckRequested element
         QName ackRequestedQName = wsrmConstants.getAckRequestedQName(); 
         SOAPElement ackRequestedElement = soapEnvelope.getHeader().addChildElement(ackRequestedQName);

         // write required wsrm:Identifier element
         QName identifierQName = wsrmConstants.getIdentifierQName();
         ackRequestedElement.addChildElement(identifierQName).setValue(o.getIdentifier());
         
         if (o.getMessageNumber() != 0)
         {
            // write optional wsrm:MessageNumber element
            QName messageNumberQName = wsrmConstants.getMessageNumberQName();
            SOAPElement messageNumberElement = ackRequestedElement.addChildElement(messageNumberQName);
            messageNumberElement.setValue(String.valueOf(o.getMessageNumber()));
         }
      }
      catch (SOAPException se)
      {
         throw new RMException("Unable to serialize RM message", se);
      }
   }

}
