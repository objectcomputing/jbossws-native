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

import static org.jboss.ws.extensions.wsrm.common.serialization.RMSerializationHelper.getOptionalElement;
import static org.jboss.ws.extensions.wsrm.common.serialization.RMSerializationHelper.getRequiredElement;
import static org.jboss.ws.extensions.wsrm.common.serialization.RMSerializationHelper.getRequiredTextContent;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingConstants;

import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.common.RMHelper;
import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequenceResponse;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMIncompleteSequenceBehavior;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;

/**
 * <b>CreateSequenceResponse</b> object de/serializer
 * @author richard.opalka@jboss.com
 */
final class RMCreateSequenceResponseSerializer implements RMSerializer
{

   private static final AddressingConstants ADDRESSING_CONSTANTS = 
      AddressingBuilder.getAddressingBuilder().newAddressingConstants();
   
   private static final RMSerializer INSTANCE = new RMCreateSequenceResponseSerializer();
   
   private RMCreateSequenceResponseSerializer()
   {
      // hide constructor
   }
   
   static RMSerializer getInstance()
   {
      return INSTANCE;
   }
   
   /**
    * Deserialize <b>CreateSequenceResponse</b> using <b>provider</b> from the <b>soapMessage</b>
    * @param object to be deserialized
    * @param provider wsrm provider to be used for deserialization process
    * @param soapMessage soap message from which object will be deserialized
    */
   public final void deserialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMCreateSequenceResponse o = (RMCreateSequenceResponse)object;
      try
      {
         SOAPBody soapBody = soapMessage.getSOAPPart().getEnvelope().getBody();
         RMConstants wsrmConstants = provider.getConstants();
         
         // read required wsrm:CreateSequenceResponse element
         QName createSequenceResponseQName = wsrmConstants.getCreateSequenceResponseQName();
         SOAPElement createSequenceResponseElement = getRequiredElement(soapBody, createSequenceResponseQName, "soap body");

         // read required wsrm:Identifier element
         QName identifierQName = wsrmConstants.getIdentifierQName();
         SOAPElement identifierElement = getRequiredElement(createSequenceResponseElement, identifierQName, createSequenceResponseQName);
         String identifier = getRequiredTextContent(identifierElement, identifierQName);
         o.setIdentifier(identifier);
         
         // read optional wsrm:Expires element
         QName expiresQName = wsrmConstants.getExpiresQName();
         SOAPElement expiresElement = getOptionalElement(createSequenceResponseElement, expiresQName, createSequenceResponseQName);
         if (expiresElement != null)
         {
            String duration = getRequiredTextContent(expiresElement, expiresQName);
            o.setExpires(RMHelper.stringToDuration(duration));
         }

         // read optional wsrm:IncompleteSequenceBehavior element
         QName behaviorQName = wsrmConstants.getIncompleteSequenceBehaviorQName();
         SOAPElement behaviorElement = getOptionalElement(createSequenceResponseElement, behaviorQName, createSequenceResponseQName);
         if (behaviorElement != null)
         {
            String behaviorString = getRequiredTextContent(behaviorElement, behaviorQName);
            o.setIncompleteSequenceBehavior(RMIncompleteSequenceBehavior.getValue(behaviorString));
         }
         
         // read optional wsrm:Accept element
         QName acceptQName = wsrmConstants.getAcceptQName();
         SOAPElement acceptElement = getOptionalElement(createSequenceResponseElement, acceptQName, createSequenceResponseQName);
         if (acceptElement != null)
         {
            RMCreateSequenceResponse.RMAccept accept = o.newAccept();
            
            // read required wsrm:AcksTo element
            QName acksToQName = wsrmConstants.getAcksToQName();
            SOAPElement acksToElement = getRequiredElement(acceptElement, acksToQName, acceptQName);
            QName addressQName = ADDRESSING_CONSTANTS.getAddressQName();
            SOAPElement acksToAddressElement = getRequiredElement(acksToElement, addressQName, acksToQName);
            String acksToAddress = getRequiredTextContent(acksToAddressElement, addressQName);
            accept.setAcksTo(acksToAddress);

            // set created accept
            o.setAccept(accept);
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
    * Serialize <b>CreateSequenceResponse</b> using <b>provider</b> to the <b>soapMessage</b>
    * @param object to be serialized
    * @param provider wsrm provider to be used for serialization process
    * @param soapMessage soap message to which object will be serialized
    */
   public final void serialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMCreateSequenceResponse o = (RMCreateSequenceResponse)object;
      try 
      {
         SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
         RMConstants wsrmConstants = provider.getConstants();
         
         // Add xmlns:wsrm declaration
         soapEnvelope.addNamespaceDeclaration(wsrmConstants.getPrefix(), wsrmConstants.getNamespaceURI());

         // write required wsrm:CreateSequenceResponse element
         QName createSequenceResponseQName = wsrmConstants.getCreateSequenceResponseQName(); 
         SOAPElement createSequenceResponseElement = soapEnvelope.getBody().addChildElement(createSequenceResponseQName);

         // write required wsrm:Identifier element
         QName identifierQName = wsrmConstants.getIdentifierQName();
         createSequenceResponseElement.addChildElement(identifierQName).setValue(o.getIdentifier());
         
         if (o.getExpires() != null)
         {
            // write optional wsrm:Expires element
            QName expiresQName = wsrmConstants.getExpiresQName();
            createSequenceResponseElement.addChildElement(expiresQName).setValue(RMHelper.durationToString(o.getExpires()));
         }
         
         if (o.getIncompleteSequenceBehavior() != null)
         {
            // write optional wsrm:IncompleteSequenceBehavior element
            RMIncompleteSequenceBehavior behavior = o.getIncompleteSequenceBehavior();
            QName behaviorQName = wsrmConstants.getIncompleteSequenceBehaviorQName();
            SOAPElement behaviorElement = createSequenceResponseElement.addChildElement(behaviorQName);
            behaviorElement.setValue(behavior.toString());
         }
         
         if (o.getAccept() != null)
         {
            // write optional wsrm:Accept element
            QName acceptQName = wsrmConstants.getAcceptQName();
            SOAPElement acceptElement = createSequenceResponseElement.addChildElement(acceptQName);

            // write required wsrm:AcksTo element
            QName acksToQName = wsrmConstants.getAcksToQName();
            QName addressQName = ADDRESSING_CONSTANTS.getAddressQName();
            acceptElement.addChildElement(acksToQName)
               .addChildElement(addressQName)
                  .setValue(o.getAccept().getAcksTo());
         }
      }
      catch (SOAPException se)
      {
         throw new RMException("Unable to serialize RM message", se);
      }
   }

}
