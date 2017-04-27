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
package org.jboss.ws.extensions.wsrm.persistence;

import java.util.Iterator;

/**
 * Storable sequence
 *
 * @author richard.opalka@jboss.com
 */
public interface RMSequence
{
   
   /**
    * Gets sequence outbound id
    * @return sequence outbound id
    */
   String getOutboundId();

   /**
    * Gets sequence inbound id
    * @return sequence inbound id
    */
   String getInboundId();
   
   /**
    * This method will be used on client side only.
    * Inbound sequence id is available on first response message from server
    * @param inboundId inbound id to set
    */
   void setInboundId(String inboundId);
   
   /**
    * Gets sequence metadata
    * @return sequence metadata
    */
   RMSequenceMetaData getMetaData();
   
   /**
    * Sets sequence state
    * @param seqState new sequence state
    */
   void setState(RMSequenceState seqState);
   
   /**
    * Gets sequence state
    * @return sequence state
    */
   RMSequenceState getState();
   
   /**
    * Adds new inbound message to the sequence
    * @param msg inbound message persistence wrapper
    */
   void addInboundMessage(RMMessage msg);
   
   /**
    * Adds new outbound message to the sequence
    * @param msg outbound message persistence wrapper
    */
   void addOutboundMessage(RMMessage msg);
   
   /**
    * Gets all arrived inbound messages ids
    * @return all arrived inbound messages ids
    */
   Iterator<Long> getInboundMessageNumbers();

   /**
    * Gets all sent inbound messages ids
    * @return all sent inbound messages ids
    */
   Iterator<Long> getOutboundMessageNumbers();
   
   /**
    * Gets particular inbound message wrapper identified by its message number
    * @param msgInboundNo message inbound number
    * @return message wrapper
    */
   RMMessage getInboundMessage(long msgInboundNo);

   /**
    * Gets particular outbound message wrapper identified by its message number
    * @param msgInboundNo message outbound number
    * @return message wrapper
    */
   RMMessage getOutboundMessage(long msgOutboundNo);

   /**
    * Gets particular inbound message wrapper identified by its message id
    * @param msgInboundNo message inbound number
    * @return message wrapper
    */
   RMMessage getInboundMessage(String messageId);

   /**
    * Gets particular outbound message wrapper identified by its message id
    * @param msgInboundNo message outbound number
    * @return message wrapper
    */
   RMMessage getOutboundMessage(String messageId);

}
