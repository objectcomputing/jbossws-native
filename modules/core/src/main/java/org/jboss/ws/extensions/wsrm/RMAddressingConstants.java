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

import javax.xml.ws.addressing.AddressingBuilder;

import org.jboss.ws.extensions.wsrm.protocol.RMProvider;

/**
 * Addressing constants related to WS-RM protocol
 *
 * @author richard.opalka@jboss.com
 *
 * @since Dec 14, 2007
 */
public final class RMAddressingConstants
{

   private RMAddressingConstants()
   {
      // instances not allowed
   }
   
   public static final String CREATE_SEQUENCE_WSA_ACTION;
   public static final String CREATE_SEQUENCE_RESPONSE_WSA_ACTION;
   public static final String CLOSE_SEQUENCE_WSA_ACTION;
   public static final String CLOSE_SEQUENCE_RESPONSE_WSA_ACTION;
   public static final String SEQUENCE_ACKNOWLEDGEMENT_WSA_ACTION;
   public static final String TERMINATE_SEQUENCE_WSA_ACTION;
   public static final String TERMINATE_SEQUENCE_RESPONSE_WSA_ACTION;
   public static final String WSA_ANONYMOUS_URI = AddressingBuilder.getAddressingBuilder().newAddressingConstants().getAnonymousURI();
   
   static
   {
      String namespaceURI = RMProvider.get().getConstants().getNamespaceURI();
      CREATE_SEQUENCE_WSA_ACTION = namespaceURI + "/CreateSequence";
      CREATE_SEQUENCE_RESPONSE_WSA_ACTION = namespaceURI + "/CreateSequenceResponse";
      CLOSE_SEQUENCE_WSA_ACTION = namespaceURI + "/CloseSequence";
      CLOSE_SEQUENCE_RESPONSE_WSA_ACTION = namespaceURI + "/CloseSequenceResponse";
      SEQUENCE_ACKNOWLEDGEMENT_WSA_ACTION = namespaceURI + "/SequenceAcknowledgement";
      TERMINATE_SEQUENCE_WSA_ACTION = namespaceURI + "/TerminateSequence";
      TERMINATE_SEQUENCE_RESPONSE_WSA_ACTION = namespaceURI + "/TerminateSequenceResponse";
   }

}
