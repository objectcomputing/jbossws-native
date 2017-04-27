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
package org.jboss.ws.extensions.wsrm.protocol.spi;

/**
 * <p><b>AckRequested</b> element requests an Acknowledgement for the identified Sequence.</p>
 * 
 * The following infoset defines its syntax:
 * <p><blockquote><pre>
 * &lt;wsrm:AckRequested ...&gt;
 *     &lt;wsrm:Identifier ...&gt; xs:anyURI &lt;/wsrm:Identifier&gt;
 *     &lt;wsrm:MessageNumber&gt; xs:unsignedLong &lt;/wsrm:MessageNumber&gt; ?
 *     ... 
 * &lt;/wsrm:AckRequested&gt;
 * </pre></blockquote></p>
 *  
 * @author richard.opalka@jboss.com
 */
public interface RMAckRequested extends RMSerializable
{
   /**
    * An RM Source that includes an <b>AckRequested</b> header block in a SOAP envelope MUST include
    * this element in that header block. The RM Source MUST set the value of this element to the
    * absolute URI, (conformant with RFC3986), that uniquely identifies the Sequence to which the
    * request applies.
    * @param identifier
    */
   void setIdentifier(String identifier);

   /**
    * Getter
    * @return sequence identifier
    */
   String getIdentifier();
   
   /**
    * This optional element, if present, MUST contain an <b>xs:unsignedLong</b> representing the highest
    * <b>MessageNumber</b> sent by the RM Source within a Sequence. If present, it MAY be treated
    * as a hint to the RM Destination as an optimization to the process of preparing to transmit a
    * <b>SequenceAcknowledgement</b>.
    * @param lastMessageNumber
    */
   void setMessageNumber(long lastMessageNumber);
   
   /**
    * Getter
    * @return last message number in the sequence
    */
   long getMessageNumber();
}
