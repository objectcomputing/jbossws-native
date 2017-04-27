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
package org.jboss.ws.extensions.wsrm.protocol;

import javax.xml.namespace.QName;

/**
 * WS-RM protocol elements SPI facade. Each WS-RM provider must implement this interface.
 *
 * @author richard.opalka@jboss.com
 */
public interface RMConstants
{

   /**
    * getter
    * @return wsrm <b>prefix</b>
    */
   String getPrefix();
   
   /**
    * getter
    * @return wsrm <b>namespace</b>
    */
   String getNamespaceURI();
   
   /**
    * getter
    * @return <b>LastMessage</b> QName
    */
   QName getLastMessageQName();

   /**
    * getter
    * @return <b>CreateSequence</b> QName
    */
   QName getCreateSequenceQName();

   /**
    * getter
    * @return <b>AcksTo</b> QName
    */
   QName getAcksToQName();

   /**
    * getter
    * @return <b>Expires</b> QName
    */
   QName getExpiresQName();

   /**
    * getter
    * @return <b>Offer</b> QName
    */
   QName getOfferQName();

   /**
    * getter
    * @return <b>Identifier</b> QName
    */
   QName getIdentifierQName();

   /**
    * getter
    * @return <b>Endpoint</b> QName
    */
   QName getEndpointQName();

   /**
    * getter
    * @return <b>IncompleteSequenceBehavior</b> QName
    */
   QName getIncompleteSequenceBehaviorQName();

   /**
    * getter
    * @return <b>CreateSequenceResponse</b> QName
    */
   QName getCreateSequenceResponseQName();

   /**
    * getter
    * @return <b>Accept</b> QName
    */
   QName getAcceptQName();

   /**
    * getter
    * @return <b>CloseSequence</b> QName
    */
   QName getCloseSequenceQName();

   /**
    * getter
    * @return <b>LastMessageNumber</b> QName
    */
   QName getLastMessageNumberQName();

   /**
    * getter
    * @return <b>CloseSequenceResponse</b> QName
    */
   QName getCloseSequenceResponseQName();

   /**
    * getter
    * @return <b>TerminateSequence</b> QName
    */
   QName getTerminateSequenceQName();

   /**
    * getter
    * @return <b>LastMsgNumber</b> QName
    */
   QName getLastMsgNumberQName();

   /**
    * getter
    * @return <b>TerminateSequenceResponse</b> QName
    */
   QName getTerminateSequenceResponseQName();

   /**
    * getter
    * @return <b>Sequence</b> QName
    */
   QName getSequenceQName();

   /**
    * getter
    * @return <b>MessageNumber</b> QName
    */
   QName getMessageNumberQName();

   /**
    * getter
    * @return <b>MaxMessageNumber</b> QName
    */
   QName getMaxMessageNumberQName();

   /**
    * getter
    * @return <b>AckRequested</b> QName
    */
   QName getAckRequestedQName();

   /**
    * getter
    * @return <b>SequenceAcknowledgement</b> QName
    */
   QName getSequenceAcknowledgementQName();

   /**
    * getter
    * @return <b>AcknowledgementRange</b> QName
    */
   QName getAcknowledgementRangeQName();

   /**
    * getter
    * @return <b>Upper</b> QName
    */
   QName getUpperQName();

   /**
    * getter
    * @return <b>Lower</b> QName
    */
   QName getLowerQName();

   /**
    * getter
    * @return <b>None</b> QName
    */
   QName getNoneQName();

   /**
    * getter
    * @return <b>Final</b> QName
    */
   QName getFinalQName();

   /**
    * getter
    * @return <b>Nack</b> QName
    */
   QName getNackQName();

   /**
    * getter
    * @return <b>SequenceFault</b> QName
    */
   QName getSequenceFaultQName();

   /**
    * getter
    * @return <b>FaultCode</b> QName
    */
   QName getFaultCodeQName();

   /**
    * getter
    * @return <b>Detail</b> QName
    */
   QName getDetailQName();

}
