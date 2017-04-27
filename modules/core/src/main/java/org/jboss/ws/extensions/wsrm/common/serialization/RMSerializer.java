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

import javax.xml.soap.SOAPMessage;

import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;

/**
 * Each WS-RM message de/serializer must implement this interface
 * @author richard.opalka@jboss.com
 */
interface RMSerializer
{
   
   /**
    * Serialize the specified <b>object</b> using passed RM <b>provider</b> to the <b>soapMessage</b>
    * @param object to be serialized
    * @param provider RM provider to be used
    * @param soapMessage where to write the data
    */
   void serialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage);
   
   /**
    * Deserialize the specified <b>object</b> using passed RM <b>provider</b> from the <b>soapMessage</b>
    * @param object to be deserialized
    * @param provider RM provider to be used
    * @param soapMessage from which to read the data
    */
   void deserialize(RMSerializable object, RMProvider provider, SOAPMessage soapMessage);
   
}
