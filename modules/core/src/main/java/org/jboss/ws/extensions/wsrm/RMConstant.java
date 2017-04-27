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
package org.jboss.ws.extensions.wsrm;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;

public final class RMConstant
{

   private RMConstant()
   {
      // instances not allowed
   }
   
   private static final String PREFIX = "wsrm";
   
   public static final String ONE_WAY_OPERATION = PREFIX + ".oneWayOperation";
   public static final String REQUEST_CONTEXT = PREFIX + ".requestContext";
   public static final String RESPONSE_CONTEXT = PREFIX + ".responseContext";
   public static final String SEQUENCE_REFERENCE = PREFIX + ".sequenceReference";
   public static final String FAULT_REFERENCE = PREFIX + ".faultReference";
   public static final String PROTOCOL_MESSAGES = PREFIX + ".protocolMessages";
   public static final String PROTOCOL_MESSAGES_MAPPING = PREFIX + ".protocolMessagesMapping";
   public static final String WSA_MESSAGE_ID = PREFIX + ".wsaMessageId";
   public static final Set<QName> PROTOCOL_OPERATION_QNAMES;
   
   static
   {
      Set<QName> temp = new HashSet<QName>();
      RMConstants constants = RMProvider.get().getConstants();
      temp.add(constants.getSequenceQName());
      temp.add(constants.getSequenceFaultQName());
      temp.add(constants.getSequenceAcknowledgementQName());
      temp.add(constants.getAckRequestedQName());
      temp.add(constants.getCreateSequenceQName());
      temp.add(constants.getCreateSequenceResponseQName());
      temp.add(constants.getCloseSequenceQName());
      temp.add(constants.getCloseSequenceResponseQName());
      temp.add(constants.getTerminateSequenceQName());
      temp.add(constants.getTerminateSequenceResponseQName());
      PROTOCOL_OPERATION_QNAMES = Collections.unmodifiableSet(temp);
   }

}
