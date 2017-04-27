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

/**
 * WS-RM protocol elements SPI facade. Each WS-RM provider must implement this interface.
 *
 * @author richard.opalka@jboss.com
 */
public interface RMMessageFactory
{
   /**
    * Factory method
    * @return new CreateSequence instance
    */
   RMCreateSequence newCreateSequence();

   /**
    * Factory method
    * @return new CreateSequenceResponse instance
    */
   RMCreateSequenceResponse newCreateSequenceResponse();

   /**
    * Factory method
    * @return new CloseSequence instance or null if this message is not supported by underlying WS-RM provider
    */
   RMCloseSequence newCloseSequence();

   /**
    * Factory method
    * @return new CloseSequenceResponse instance or null if this message is not supported by underlying WS-RM provider
    */
   RMCloseSequenceResponse newCloseSequenceResponse();

   /**
    * Factory method
    * @return new TerminateSequence instance
    */
   RMTerminateSequence newTerminateSequence();

   /**
    * Factory method
    * @return new TerminateSequenceResponse instance or null if this message is not supported by underlying WS-RM provider
    */
   RMTerminateSequenceResponse newTerminateSequenceResponse();

   /**
    * Factory method
    * @return new Sequence instance
    */
   RMSequence newSequence();

   /**
    * Factory method
    * @return new AckRequested instance
    */
   RMAckRequested newAckRequested();

   /**
    * Factory method
    * @return new SequenceAcknowledgement instance
    */
   RMSequenceAcknowledgement newSequenceAcknowledgement();
   
   /**
    * Factory method
    * @return new SequenceFault instance
    */
   RMSequenceFault newSequenceFault();
}
