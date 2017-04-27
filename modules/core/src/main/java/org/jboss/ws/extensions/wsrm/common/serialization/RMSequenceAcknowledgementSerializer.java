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
import static org.jboss.ws.extensions.wsrm.common.serialization.RMSerializationHelper.getOptionalElements;
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
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceAcknowledgement;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;

import java.util.List;

/**
 * <b>SequenceAcknowledgement</b> object de/serializer
 * @author richard.opalka@jboss.com
 */
final class RMSequenceAcknowledgementSerializer implements RMSerializer
{

   private static final RMSerializer INSTANCE = new RMSequenceAcknowledgementSerializer();
   
   private RMSequenceAcknowledgementSerializer()
   {
      // hide constructor
   }
   
   static RMSerializer getInstance()
   {
      return INSTANCE;
   }
   
   /**
    * Deserialize <b>SequenceAcknowledgement</b> using <b>provider</b> from the <b>soapMessage</b>
    * @param object to be deserialized
    * @param provider wsrm provider to be used for deserialization process
    * @param soapMessage soap message from which object will be deserialized
    */
   public final void deserialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMSequenceAcknowledgement o = (RMSequenceAcknowledgement)object;
      try
      {
         SOAPHeader soapHeader = soapMessage.getSOAPPart().getEnvelope().getHeader();
         RMConstants wsrmConstants = provider.getConstants();
         
         // read required wsrm:SequenceAcknowledgement element
         QName sequenceAckQName = wsrmConstants.getSequenceAcknowledgementQName();
         SOAPElement sequenceAckElement = getRequiredElement(soapHeader, sequenceAckQName, "soap header");

         // read required wsrm:Identifier element
         QName identifierQName = wsrmConstants.getIdentifierQName();
         SOAPElement identifierElement = getRequiredElement(sequenceAckElement, identifierQName, sequenceAckQName);
         String identifier = getRequiredTextContent(identifierElement, identifierQName);
         o.setIdentifier(identifier);
         
         // read optional wsrm:Final element
         QName finalQName = wsrmConstants.getFinalQName();
         SOAPElement finalElement = getOptionalElement(sequenceAckElement, finalQName, sequenceAckQName);
         if (finalElement != null)
         {
            o.setFinal();
         }
         
         // read optional wsrm:None element
         QName noneQName = wsrmConstants.getNoneQName();
         SOAPElement noneElement = getOptionalElement(sequenceAckElement, noneQName, sequenceAckQName);
         if (noneElement != null)
         {
            o.setNone();
         }
         
         // read optional wsrm:Nack elements
         QName nackQName = wsrmConstants.getNackQName();
         List<SOAPElement> nackElements = getOptionalElements(sequenceAckElement, nackQName, sequenceAckQName);
         for (SOAPElement nackElement : nackElements)
         {
            String messageId = getRequiredTextContent(nackElement, nackQName);
            o.addNack(stringToLong(messageId, "Unable to parse Nack element text content"));
         }
         
         // read optional wsrm:AcknowledgementRange elements
         QName ackRangeQName = wsrmConstants.getAcknowledgementRangeQName();
         List<SOAPElement> ackRangeElements = getOptionalElements(sequenceAckElement, ackRangeQName, sequenceAckQName);
         if (ackRangeElements.size() != 0)
         {
            QName upperQName = wsrmConstants.getUpperQName();
            QName lowerQName = wsrmConstants.getLowerQName();

            for (SOAPElement ackRangeElement : ackRangeElements)
            {
               RMSequenceAcknowledgement.RMAcknowledgementRange ackRange = o.newAcknowledgementRange();
            
               // read required wsrm:Upper attribute
               String upper = getRequiredTextContent(ackRangeElement, upperQName, ackRangeQName);
               ackRange.setUpper(stringToLong(upper, "Unable to parse Upper attribute text content"));
            
               // read required wsrm:Lower attribute
               String lower = getRequiredTextContent(ackRangeElement, lowerQName, ackRangeQName);
               ackRange.setLower(stringToLong(lower, "Unable to parse Lower attribute text content"));
            
               // set created acknowledgement range
               o.addAcknowledgementRange(ackRange);
            }
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
    * Serialize <b>SequenceAcknowledgement</b> using <b>provider</b> to the <b>soapMessage</b>
    * @param object to be serialized
    * @param provider wsrm provider to be used for serialization process
    * @param soapMessage soap message to which object will be serialized
    */
   public final void serialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMSequenceAcknowledgement o = (RMSequenceAcknowledgement)object;
      try
      {
         SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
         RMConstants wsrmConstants = provider.getConstants();
         
         // Add xmlns:wsrm declaration
         soapEnvelope.addNamespaceDeclaration(wsrmConstants.getPrefix(), wsrmConstants.getNamespaceURI());

         // write required wsrm:SequenceAcknowledgement element
         QName sequenceAckQName = wsrmConstants.getSequenceAcknowledgementQName(); 
         SOAPElement sequenceAckElement = soapEnvelope.getHeader().addChildElement(sequenceAckQName);

         // write required wsrm:Identifier element
         QName identifierQName = wsrmConstants.getIdentifierQName();
         sequenceAckElement.addChildElement(identifierQName).setValue(o.getIdentifier());
         
         if (o.isFinal())
         {
            // write optional wsrm:Final element
            QName finalQName = wsrmConstants.getFinalQName();
            sequenceAckElement.addChildElement(finalQName);
         }
         
         if (o.isNone())
         {
            // write optional wsrm:None element
            QName noneQName = wsrmConstants.getNoneQName();
            sequenceAckElement.addChildElement(noneQName);
         }
         
         List<Long> nacks = o.getNacks();
         if (nacks.size() != 0)
         {
            QName nackQName = wsrmConstants.getNackQName();

            // write optional wsrm:Nack elements
            for (Long messageId : nacks)
            {
               sequenceAckElement.addChildElement(nackQName).setValue(String.valueOf(messageId));
            }
         }
         
         List<RMSequenceAcknowledgement.RMAcknowledgementRange> ackRanges = o.getAcknowledgementRanges();
         if (ackRanges.size() != 0)
         {
            QName acknowledgementRangeQName = wsrmConstants.getAcknowledgementRangeQName();
            QName upperQName = wsrmConstants.getUpperQName();
            QName lowerQName = wsrmConstants.getLowerQName();

            // write optional wsrm:AcknowledgementRange elements
            for (RMSequenceAcknowledgement.RMAcknowledgementRange ackRange : ackRanges)
            {
               SOAPElement acknowledgementRangeElement = sequenceAckElement.addChildElement(acknowledgementRangeQName);
               // write required wsrm:Lower attribute
               acknowledgementRangeElement.addAttribute(lowerQName, String.valueOf(ackRange.getLower()));
               // write required wsrm:Upper attribute
               acknowledgementRangeElement.addAttribute(upperQName, String.valueOf(ackRange.getUpper()));
            }
         }
      }
      catch (SOAPException se)
      {
         throw new RMException("Unable to serialize RM message", se);
      }
   }

}
