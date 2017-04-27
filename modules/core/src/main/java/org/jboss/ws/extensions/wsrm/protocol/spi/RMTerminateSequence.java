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
 * <p><b>TerminateSequence</b> MAY be sent by an RM Source to indicate it has completed its use of the Sequence.
 * It indicates that the RM Destination can safely reclaim any resources related to the identified
 * Sequence. The RM Source MUST NOT send this element as a header block. The RM Source
 * MAY retransmit this element. Once this element is sent, other than this element, the RM Source
 * MUST NOT send any additional message to the RM Destination referencing this Sequence.</p>
 * 
 * <p>This element MAY also be sent by the RM Destination to indicate that it has unilaterally
 * terminated the Sequence. Upon sending this message the RM Destination MUST NOT accept
 * any additional messages (with the exception of the corresponding
 * <b>TerminateSequenceResponse</b>) for this Sequence. Upon receipt of a <b>TerminateSequence</b>
 * the RM Source MUST NOT send any additional messages (with the exception of the
 * corresponding <b>TerminateSequenceResponse</b>) for this Sequence.</p> 
 * 
 * The following infoset defines its syntax:
 * <p><blockquote><pre>
 * &lt;wsrm:TerminateSequence ...&gt;
 *     &lt;wsrm:Identifier ...&gt; xs:anyURI &lt;/wsrm:Identifier&gt;
 *     &lt;wsrm:LastMsgNumber&gt; wsrm:MessageNumberType &lt;/wsrm:LastMsgNumber&gt; ?
 *     ... 
 * &lt;/wsrm:TerminateSequence&gt;
 * </pre></blockquote></p>
 *  
 * @author richard.opalka@jboss.com
 */
public interface RMTerminateSequence extends RMSerializable
{
   /**
    * The RM Source or RM Destination MUST include this element in any TerminateSequence
    * message it sends. The RM Source or RM Destination MUST set the value of this element to the
    * absolute URI (conformant with RFC3986) of the terminating Sequence.
    * @param identifier
    */
   void setIdentifier(String identifier);
   
   /**
    * Getter
    * @return sequence identifier
    */
   String getIdentifier();
   
   /**
    * The RM Source SHOULD include this element in any TerminateSequence message it sends. The
    * <b>LastMsgNumber</b> element specifies the highest assigned message number of all the Sequence
    * Traffic Messages for the terminating Sequence.
    * @param lastMsgNumber
    */
   void setLastMsgNumber(long lastMsgNumber);
   
   /**
    * Getter
    * @return last message number
    */
   long getLastMsgNumber();
}
