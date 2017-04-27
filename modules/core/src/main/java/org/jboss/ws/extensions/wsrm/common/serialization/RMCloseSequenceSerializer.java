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
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCloseSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;

/**
 * <b>CloseSequence</b> object de/serializer
 * @author richard.opalka@jboss.com
 */
final class RMCloseSequenceSerializer implements RMSerializer
{

   private static final RMSerializer INSTANCE = new RMCloseSequenceSerializer();
   
   private RMCloseSequenceSerializer()
   {
      // hide constructor
   }
   
   static RMSerializer getInstance()
   {
      return INSTANCE;
   }
   
   /**
    * Deserialize <b>CloseSequence</b> using <b>provider</b> from the <b>soapMessage</b>
    * @param object to be deserialized
    * @param provider wsrm provider to be used for deserialization process
    * @param soapMessage soap message from which object will be deserialized
    */
   public final void deserialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMCloseSequence o = (RMCloseSequence)object;
      try
      {
         SOAPBody soapBody = soapMessage.getSOAPPart().getEnvelope().getBody();
         RMConstants wsrmConstants = provider.getConstants();
         
         // read required wsrm:CloseSequence element
         QName closeSequenceQName = wsrmConstants.getCloseSequenceQName();
         SOAPElement closeSequenceElement = getRequiredElement(soapBody, closeSequenceQName, "soap body");

         // read required wsrm:Identifier element
         QName identifierQName = wsrmConstants.getIdentifierQName();
         SOAPElement identifierElement = getRequiredElement(closeSequenceElement, identifierQName, closeSequenceQName);
         String identifier = getRequiredTextContent(identifierElement, identifierQName);
         o.setIdentifier(identifier);
         
         // read optional wsrm:LastMsgNumber element
         QName lastMsgNumberQName = wsrmConstants.getLastMsgNumberQName();
         SOAPElement lastMsgNumberElement = getOptionalElement(closeSequenceElement, lastMsgNumberQName, closeSequenceQName);
         if (lastMsgNumberElement != null)
         {
            String lastMsgNumberString = getRequiredTextContent(lastMsgNumberElement, lastMsgNumberQName);
            long lastMsgNumberValue = stringToLong(lastMsgNumberString, "Unable to parse LastMsgNumber element text content");
            o.setLastMsgNumber(lastMsgNumberValue);
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
    * Serialize <b>CloseSequence</b> using <b>provider</b> to the <b>soapMessage</b>
    * @param object to be serialized
    * @param provider wsrm provider to be used for serialization process
    * @param soapMessage soap message to which object will be serialized
    */
   public final void serialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMCloseSequence o = (RMCloseSequence)object;
      try
      {
         SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
         RMConstants wsrmConstants = provider.getConstants();
         
         // Add xmlns:wsrm declaration
         soapEnvelope.addNamespaceDeclaration(wsrmConstants.getPrefix(), wsrmConstants.getNamespaceURI());

         // write required wsrm:CloseSequence element
         QName closeSequenceQName = wsrmConstants.getCloseSequenceQName(); 
         SOAPElement closeSequenceElement = soapEnvelope.getBody().addChildElement(closeSequenceQName);

         // write required wsrm:Identifier element
         QName identifierQName = wsrmConstants.getIdentifierQName();
         closeSequenceElement.addChildElement(identifierQName).setValue(o.getIdentifier());
         
         if (o.getLastMsgNumber() != 0)
         {
            // write optional wsrm:LastMsgNumber element
            QName lastMsgNumberQName = wsrmConstants.getLastMsgNumberQName();
            SOAPElement lastMsgNumberElement = closeSequenceElement.addChildElement(lastMsgNumberQName);
            lastMsgNumberElement.setValue(String.valueOf(o.getLastMsgNumber()));
         }
      }
      catch (SOAPException se)
      {
         throw new RMException("Unable to serialize RM message", se);
      }
   }

}
