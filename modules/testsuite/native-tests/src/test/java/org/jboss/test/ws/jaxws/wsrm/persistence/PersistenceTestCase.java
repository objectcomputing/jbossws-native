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
package org.jboss.test.ws.jaxws.wsrm.persistence;

import java.util.Iterator;

import org.jboss.ws.extensions.wsrm.persistence.RMMessage;
import org.jboss.ws.extensions.wsrm.persistence.RMMessageFactory;
import org.jboss.ws.extensions.wsrm.persistence.RMSequence;
import org.jboss.ws.extensions.wsrm.persistence.RMSequenceFactory;
import org.jboss.ws.extensions.wsrm.persistence.RMSequenceMetaData;
import org.jboss.ws.extensions.wsrm.persistence.RMSequenceMetaDataBuilder;
import org.jboss.ws.extensions.wsrm.persistence.RMSequenceMetaDataBuilderFactory;
import org.jboss.ws.extensions.wsrm.persistence.RMStore;

import org.jboss.wsf.test.JBossWSTest;

/**
 * WSRM persistent store test
 *
 * @author richard.opalka@jboss.com
 */
public final class PersistenceTestCase extends JBossWSTest
{

   public void testPersistency() throws Exception
   {
      
      if (true)
      {
         System.out.println("FIXME [JBWS-2044] Rewrite Server side file system based RM store");
         return;
      }
      
      RMSequenceMetaDataBuilder seqMDBuilder = RMSequenceMetaDataBuilderFactory.getInstance().newBuilder();
      RMSequenceMetaData sequenceMD = seqMDBuilder
         .setSOAPVersion("http://schemas.xmlsoap.org/soap/envelope/")
            .setWSRMVersion("http://docs.oasis-open.org/ws-rx/wsrm/200702")
               .setADDRVersion("http://www.w3.org/2005/08/addressing")
                  .setEndpointAddress("http://somehost:666/endpoint")
                     .setAcksToAddress("http://www.w3.org/2005/08/addressing/anonymous")
                        .toSequenceMetaData();
      
      RMSequence sequence = RMSequenceFactory.getInstance().newSequence(sequenceMD);
      RMMessage inboundMessage = RMMessageFactory.getInstance().newMessage("messageId", "data".getBytes());
      RMMessage outboundMessage = RMMessageFactory.getInstance().newMessage("messageId", "data".getBytes());
      sequence.addInboundMessage(inboundMessage);
      sequence.addOutboundMessage(outboundMessage);
      Iterator<Long> inboundIds = sequence.getInboundMessageNumbers();
      Iterator<Long> outboundIds = sequence.getOutboundMessageNumbers();
      inboundMessage = sequence.getInboundMessage(1);
      outboundMessage = sequence.getOutboundMessage(2);
      inboundMessage = sequence.getInboundMessage("messageId");
      outboundMessage = sequence.getOutboundMessage("messageId");
      RMStore store = RMStore.getInstance();
      sequence = store.read("sequenceOutboundId", true);
      sequence = store.read("sequenceInboundId", false);
      sequence.addInboundMessage(inboundMessage);
      store.write(sequence);
   }
   
}
