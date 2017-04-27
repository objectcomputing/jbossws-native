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

import java.util.Map;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.util.NotImplementedException;
import org.jboss.ws.extensions.wsrm.RMFault;
import org.jboss.ws.extensions.wsrm.RMFaultConstant;
import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceAcknowledgement;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceFault;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;

/**
 * <b>SequenceFault</b> object de/serializer
 * @author richard.opalka@jboss.com
 */
final class RMSequenceFaultSerializer implements RMSerializer
{

   private static final RMSerializer INSTANCE = new RMSequenceFaultSerializer();
   
   private RMSequenceFaultSerializer()
   {
      // hide constructor
   }
   
   static RMSerializer getInstance()
   {
      return INSTANCE;
   }
   
   /**
    * Serialize <b>SequenceFault</b> using <b>provider</b> to the <b>soapMessage</b>
    * @param object to be serialized
    * @param provider wsrm provider to be used for serialization process
    * @param soapMessage soap message to which object will be serialized
    */
   public final void deserialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      throw new NotImplementedException();
   }

   /**
    * Deserialize <b>SequenceFault</b> using <b>provider</b> from the <b>soapMessage</b>
    * @param object to be deserialized
    * @param provider wsrm provider to be used for deserialization process
    * @param soapMessage soap message from which object will be deserialized
    */
   public final void serialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage)
   throws RMException
   {
      RMSequenceFault o = (RMSequenceFault)object;
      try
      {
         SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
         boolean isSoap11 = SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals(soapEnvelope.getElementQName().getNamespaceURI());
         if (false == isSoap11)
         {
            throw new NotImplementedException("TODO: implement SOAP 12 serialization");
         }
         
         RMConstants wsrmConstants = provider.getConstants();
         
         // Add xmlns:wsrm declaration
         soapEnvelope.addNamespaceDeclaration(wsrmConstants.getPrefix(), wsrmConstants.getNamespaceURI());

         // write required wsrm:SequenceFault element
         QName sequenceFaultQName = wsrmConstants.getSequenceFaultQName(); 
         SOAPElement sequenceFaultElement = soapEnvelope.getHeader().addChildElement(sequenceFaultQName);

         // write required wsrm:FaultCode element
         RMFault rmFault = (RMFault)o.getDetail();
         QName faultCodeQName = wsrmConstants.getFaultCodeQName();
         String subcode = wsrmConstants.getPrefix() + ":" + rmFault.getFaultCode().getSubcode().getValue();
         sequenceFaultElement.addChildElement(faultCodeQName).setValue(subcode);

         Map<String, Object> details = rmFault.getDetails();  
         if (details.size() > 0)
         {
            for (Iterator<String> i = details.keySet().iterator(); i.hasNext(); )
            {
               // write optional wsrm:Detail elements
               QName detailQName = wsrmConstants.getDetailQName();
               SOAPElement detailElement = sequenceFaultElement.addChildElement(detailQName);

               String key = i.next();
               if (RMFaultConstant.IDENTIFIER.equals(key))
               {
                  // write optional wsrm:Identifier element
                  String sequenceId = (String)details.get(key);
                  QName identifierQName = wsrmConstants.getIdentifierQName();
                  detailElement.addChildElement(identifierQName).setValue(sequenceId);
               }
               else if (RMFaultConstant.ACKNOWLEDGEMENT.equals(key))
               {
                  // write optional wsrm:AcknowledgementRange element
                  RMSequenceAcknowledgement.RMAcknowledgementRange ackRange = (RMSequenceAcknowledgement.RMAcknowledgementRange)details.get(key);
                  QName acknowledgementRangeQName = wsrmConstants.getAcknowledgementRangeQName();
                  QName upperQName = wsrmConstants.getUpperQName();
                  QName lowerQName = wsrmConstants.getLowerQName();

                  SOAPElement acknowledgementRangeElement = detailElement.addChildElement(acknowledgementRangeQName);
                  // write required wsrm:Lower attribute
                  acknowledgementRangeElement.addAttribute(lowerQName, String.valueOf(ackRange.getLower()));
                  // write required wsrm:Upper attribute
                  acknowledgementRangeElement.addAttribute(upperQName, String.valueOf(ackRange.getUpper()));
               }
               else if (RMFaultConstant.MAX_MESSAGE_NUMBER.equals(key))
               {
                  // write optional wsrm:MaxMessageNumber element
                  Long maxMessageNumber = (Long)details.get(key);
                  QName maxMessageNumberQName = wsrmConstants.getMaxMessageNumberQName();
                  detailElement.addChildElement(maxMessageNumberQName).setValue(maxMessageNumber.toString());
               }
               else throw new IllegalArgumentException("Can't serialize detail with key " + key);
            }
         }
      }
      catch (SOAPException se)
      {
         throw new RMException("Unable to serialize RM message", se);
      }
   }

}
