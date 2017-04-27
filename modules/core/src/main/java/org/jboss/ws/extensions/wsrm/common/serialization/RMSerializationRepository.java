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

import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMAckRequested;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCloseSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCloseSequenceResponse;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequenceResponse;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceAcknowledgement;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceFault;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMTerminateSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMTerminateSequenceResponse;

import javax.xml.soap.SOAPMessage;

import java.util.Map;
import java.util.HashMap;

/**
 * Utility class used for de/serialization
 * @author richard.opalka@jboss.com
 */
final class RMSerializationRepository
{

   private static final Map<Class<? extends RMSerializable>, RMSerializer> SERIALIZER_REGISTRY;
   
   static
   {
      SERIALIZER_REGISTRY = new HashMap<Class<? extends RMSerializable>, RMSerializer>();
      SERIALIZER_REGISTRY.put(RMAckRequested.class, RMAckRequestedSerializer.getInstance());
      SERIALIZER_REGISTRY.put(RMCloseSequence.class, RMCloseSequenceSerializer.getInstance());
      SERIALIZER_REGISTRY.put(RMCloseSequenceResponse.class, RMCloseSequenceResponseSerializer.getInstance());
      SERIALIZER_REGISTRY.put(RMCreateSequence.class, RMCreateSequenceSerializer.getInstance());
      SERIALIZER_REGISTRY.put(RMCreateSequenceResponse.class, RMCreateSequenceResponseSerializer.getInstance());
      SERIALIZER_REGISTRY.put(RMSequenceAcknowledgement.class, RMSequenceAcknowledgementSerializer.getInstance());
      SERIALIZER_REGISTRY.put(RMSequenceFault.class, RMSequenceFaultSerializer.getInstance());
      SERIALIZER_REGISTRY.put(RMSequence.class, RMSequenceSerializer.getInstance());
      SERIALIZER_REGISTRY.put(RMTerminateSequence.class, RMTerminateSequenceSerializer.getInstance());
      SERIALIZER_REGISTRY.put(RMTerminateSequenceResponse.class, RMTerminateSequenceResponseSerializer.getInstance());
   }
   
   private RMSerializationRepository()
   {
      // no instances
   }
   
   /**
    * Serialize passed <b>object</b> data to the <b>soapMessage</b>
    * @param object to be serialized
    * @param soapMessage where to write data
    * @throws RMException if something went wrong
    */
   public static void serialize(RMAbstractSerializable object, SOAPMessage soapMessage)
   throws RMException
   {
      getSerializer(object).serialize(object, object.getProvider(), soapMessage);
   }

   /**
    * Initialize passed <b>object</b> using data in <b>soapMessage</b>
    * @param object to be initialized
    * @param soapMessage from which to read the data
    * @throws RMException if something went wrong
    */
   public static void deserialize(RMAbstractSerializable object, SOAPMessage soapMessage)
   throws RMException
   {
      getSerializer(object).deserialize(object, object.getProvider(), soapMessage);
   }
   
   /**
    * Lookups the serializer associated with the passed <b>object</b>
    * @param object to lookup serializer for
    * @return serializer to be used
    * @throws IllegalArgumentException if passed object has no defined serializer
    */
   private static RMSerializer getSerializer(RMSerializable object)
   {
      for (Class<? extends RMSerializable> serializable : SERIALIZER_REGISTRY.keySet())
      {
         if (serializable.isAssignableFrom(object.getClass()))
            return SERIALIZER_REGISTRY.get(serializable);
      }
      
      throw new IllegalArgumentException();
   }
   
}
