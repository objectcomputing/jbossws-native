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
package org.jboss.ws.extensions.wsrm.jaxws;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;
import org.jboss.ws.extensions.wsrm.RMConstant;
import org.jboss.ws.extensions.wsrm.RMFault;
import org.jboss.ws.extensions.wsrm.RMSequence;
import org.jboss.ws.extensions.wsrm.api.RMException;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;
import org.jboss.wsf.common.handler.GenericSOAPHandler;

/**
 * RM generic JAX-WS handler
 *
 * @author richard.opalka@jboss.com
 *
 * @since Oct 23, 2007
 */
public abstract class RMHandlerAbstractBase extends GenericSOAPHandler
{
   protected final Logger log = Logger.getLogger(getClass());

   public final Set<QName> getHeaders()
   {
      return RMConstant.PROTOCOL_OPERATION_QNAMES;
   }
   
   protected final void serialize(QName msgQN, List<QName> outMsgs, Map<QName, RMSerializable> data, SOAPMessage soapMessage, RMSequence seq)
   {
      RMSerializable msg = RMHandlerHelper.prepareData(msgQN, outMsgs, seq);
      if (msg != null)
      {
         msg.serializeTo(soapMessage);
         data.put(msgQN, msg);
         log.debug(msgQN.getLocalPart() + " WSRM message was serialized to payload");
      }
   }
   
   protected final void deserialize(QName msgQN, SOAPMessage soapMessage, List<QName> messages, Map<QName, RMSerializable> data)
   {
      try
      {
         RMSerializable wsrmMsg = RMHandlerHelper.getMessage(msgQN);
         if (wsrmMsg != null)
         {
            wsrmMsg.deserializeFrom(soapMessage);
            messages.add(msgQN);
            data.put(msgQN, wsrmMsg);
            log.debug(msgQN.getLocalPart() + " WSRM message was deserialized from payload");
         }
      }
      catch (RMException ignore) {}
   }
   
   protected final void serialize(QName msgQN, List<QName> outMsgs, Map<QName, RMSerializable> data, SOAPMessage soapMessage, RMFault fault)
   {
      RMSerializable msg = RMHandlerHelper.prepareData(msgQN, outMsgs, fault);
      if (msg != null)
      {
         msg.serializeTo(soapMessage);
         data.put(msgQN, msg);
         log.debug(msgQN.getLocalPart() + " WSRM message was serialized to payload");
      }
   }
   
}
