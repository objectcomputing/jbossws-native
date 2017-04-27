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
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;

/**
 * <b>Sequence</b> object de/serializer
 * @author richard.opalka@jboss.com
 */
final class RMSequenceSerializer implements RMSerializer
{

   private static final RMSerializer INSTANCE = new RMSequenceSerializer();
   
   private RMSequenceSerializer()
   {
      // hide constructor
   }
   
   static RMSerializer getInstance()
   {
      return INSTANCE;
   }
   
   /**
    * Deserialize <b>Sequence</b> using <b>provider</b> from the <b>soapMessage</b>
    * @param object to be deserialized
    * @param provider wsrm provider to be used for deserialization process
    * @param soapMessage soap message from which object will be deserialized
    */
   public final void deserialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMSequence o = (RMSequence)object;
      try
      {
         SOAPHeader soapHeader = soapMessage.getSOAPPart().getEnvelope().getHeader();
         RMConstants wsrmConstants = provider.getConstants();
         
         // read required wsrm:Sequence element
         QName sequenceQName = wsrmConstants.getSequenceQName();
         SOAPElement sequenceElement = getRequiredElement(soapHeader, sequenceQName, "soap header");

         // read required wsrm:Identifier element
         QName identifierQName = wsrmConstants.getIdentifierQName();
         SOAPElement identifierElement = getRequiredElement(sequenceElement, identifierQName, sequenceQName);
         String identifier = getRequiredTextContent(identifierElement, identifierQName);
         o.setIdentifier(identifier);
         
         // read required wsrm:MessageNumber element
         QName messageNumberQName = wsrmConstants.getMessageNumberQName();
         SOAPElement messageNumberElement = getRequiredElement(sequenceElement, messageNumberQName, sequenceQName);
         String messageNumberString = getRequiredTextContent(messageNumberElement, messageNumberQName);
         long messageNumberValue = stringToLong(messageNumberString, "Unable to parse MessageNumber element text content");
         o.setMessageNumber(messageNumberValue);
         
         // read optional wsrm:LastMessage element
         QName lastMessageQName = wsrmConstants.getLastMessageQName();
         SOAPElement lastMessageElement = getOptionalElement(sequenceElement, lastMessageQName, sequenceQName);
         if (lastMessageElement != null)
         {
            o.setLastMessage();
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
    * Serialize <b>Sequence</b> using <b>provider</b> to the <b>soapMessage</b>
    * @param object to be serialized
    * @param provider wsrm provider to be used for serialization process
    * @param soapMessage soap message to which object will be serialized
    */
   public final void serialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMSequence o = (RMSequence)object;
      try
      {
         SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
         RMConstants wsrmConstants = provider.getConstants();
         
         // Add xmlns:wsrm declaration
         soapEnvelope.addNamespaceDeclaration(wsrmConstants.getPrefix(), wsrmConstants.getNamespaceURI());

         // write required wsrm:Sequence element
         QName sequenceQName = wsrmConstants.getSequenceQName(); 
         SOAPElement sequenceElement = soapEnvelope.getHeader().addChildElement(sequenceQName);

         // write required wsrm:Identifier element
         QName identifierQName = wsrmConstants.getIdentifierQName();
         sequenceElement.addChildElement(identifierQName).setValue(o.getIdentifier());
         
         // write required wsrm:MessageNumber element
         QName messageNumberQName = wsrmConstants.getMessageNumberQName();
         SOAPElement messageNumberElement = sequenceElement.addChildElement(messageNumberQName);
         messageNumberElement.setValue(String.valueOf(o.getMessageNumber()));
         
         if (o.isLastMessage())
         {
            // write optional wsrm:LastMessage element
            QName lastMessageQName = wsrmConstants.getLastMessageQName();
            sequenceElement.addChildElement(lastMessageQName);
         }
      }
      catch (SOAPException se)
      {
         throw new RMException("Unable to serialize RM message", se);
      }
   }

}
