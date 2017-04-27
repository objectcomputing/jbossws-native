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
 * <p><b>Sequence</b> protocol element associates the message in which it is contained with a previously
 * established RM Sequence. It contains the Sequence's unique identifier and the containing
 * message's ordinal position within that Sequence. The RM Destination MUST understand the
 * <b>Sequence</b> header block. The RM Source MUST assign a <b>mustUnderstand</b> attribute with a
 * value 1/true (from the namespace corresponding to the version of SOAP to which the <b>Sequence</b>
 * SOAP header block is bound) to the <b>Sequence</b> header block element.</p>
 * 
 * The following infoset defines its syntax:
 * <p><blockquote><pre>
 * &lt;wsrm:Sequence ...&gt;
 *     &lt;wsrm:Identifier ...&gt; xs:anyURI &lt;/wsrm:Identifier&gt;
 *     &lt;wsrm:MessageNumber&gt; wsrm:MessageNumberType &lt;/wsrm:MessageNumber&gt;
 *     &lt;wsrm:LastMessage/&gt; ?
 *     ...
 * &lt;/wsrm:Sequence&gt;
 * </pre></blockquote></p>
 * 
 * @author richard.opalka@jboss.com
 */
public interface RMSequence extends RMSerializable
{
   /**
    * An RM Source that includes a <b>Sequence</b> header block in a SOAP envelope MUST include this
    * element in that header block. The RM Source MUST set the value of this element to the absolute
    * URI (conformant with RFC3986) that uniquely identifies the Sequence.
    * @param identifier
    */
   void setIdentifier(String identifier);
   
   /**
    * Getter
    * @return sequence identifier
    */
   String getIdentifier();
   
   /**
    * The RM Source MUST include this element within any Sequence headers it creates. This element
    * is of type <b>MessageNumberType</b>. It represents the ordinal position of the message within a
    * Sequence. Sequence message numbers start at 1 and monotonically increase by 1 throughout
    * the Sequence.
    * @param messageNumber
    */
   void setMessageNumber(long messageNumber);
   
   /**
    * Getter
    * @return message number within specified sequence
    */
   long getMessageNumber();
   
   /**
    * This element MAY be included by the RM Source endpoint. The <b>LastMessage</b> element has no content.
    * @param lastMessage
    */
   void setLastMessage();
   
   /**
    * Getter
    * @return last message indicator
    */
   boolean isLastMessage();
}
