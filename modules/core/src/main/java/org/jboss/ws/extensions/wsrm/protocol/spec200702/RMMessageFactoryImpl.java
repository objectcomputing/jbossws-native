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
package org.jboss.ws.extensions.wsrm.protocol.spec200702;

import org.jboss.ws.extensions.wsrm.protocol.RMMessageFactory;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMAckRequested;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCloseSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCloseSequenceResponse;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequenceResponse;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceAcknowledgement;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceFault;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMTerminateSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMTerminateSequenceResponse;

/*
 * @author richard.opalka@jboss.com
 * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory
 */
final class RMMessageFactoryImpl implements RMMessageFactory
{
   
   private static final RMMessageFactory INSTANCE = new RMMessageFactoryImpl();
   
   private RMMessageFactoryImpl()
   {
      // forbidden inheritance
   }
   
   public static RMMessageFactory getInstance()
   {
      return INSTANCE;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory#newAckRequested()
    */
   public RMAckRequested newAckRequested()
   {
      return new RMAckRequestedImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory#newCloseSequence()
    */
   public RMCloseSequence newCloseSequence()
   {
      return new RMCloseSequenceImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory#newCloseSequenceResponse()
    */
   public RMCloseSequenceResponse newCloseSequenceResponse()
   {
      return new RMCloseSequenceResponseImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory#newCreateSequence()
    */
   public RMCreateSequence newCreateSequence()
   {
      return new RMCreateSequenceImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory#newCreateSequenceResponse()
    */
   public RMCreateSequenceResponse newCreateSequenceResponse()
   {
      return new RMCreateSequenceResponseImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory#newSequence()
    */
   public RMSequence newSequence()
   {
      return new RMSequenceImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory#newSequenceAcknowledgement()
    */
   public RMSequenceAcknowledgement newSequenceAcknowledgement()
   {
      return new RMSequenceAcknowledgementImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory#newSequenceFault()
    */
   public RMSequenceFault newSequenceFault()
   {
      return new RMSequenceFaultImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory#newTerminateSequence()
    */
   public RMTerminateSequence newTerminateSequence()
   {
      return new RMTerminateSequenceImpl();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.MessageFactory#newTerminateSequenceResponse()
    */
   public RMTerminateSequenceResponse newTerminateSequenceResponse()
   {
      return new RMTerminateSequenceResponseImpl();
   }
   
}
