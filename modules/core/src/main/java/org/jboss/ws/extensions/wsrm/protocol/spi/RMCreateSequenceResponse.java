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

import javax.xml.datatype.Duration;

/**
 * <p><b>CreateSequenceResponse</b> element is sent in the body of the response message in response to a <b>CreateSequence</b>
 * request message. It indicates that the RM Destination has created a new Sequence at the
 * request of the RM Source. The RM Destination MUST NOT send this element as a header block.</p>
 * 
 * The following infoset defines its syntax:
 * <p><blockquote><pre>
 * &lt;CreateSequenceResponse ...&gt;
 *     &lt;wsrm:Identifier ...&gt; xs:anyURI &lt;/wsrm:Identifier&gt;
 *     &lt;wsrm:Expires ...&gt; xs:duration &lt;/wsrm:Expires&gt; ?
 *     &lt;wsrm:IncompleteSequenceBehavior&gt;
 *         wsrm:IncompleteSequenceBehaviorType
 *     &lt;/wsrm:IncompleteSequenceBehavior&gt; ?
 *     &lt;wsrm:Accept ...&gt;
 *         &lt;wsrm:AcksTo&gt; wsa:EndpointReferenceType &lt;/wsrm:AcksTo&gt;
 *         ...
 *     &lt;/wsrm:Accept&gt; ?
 *     ... 
 * &lt;/CreateSequenceResponse&gt;
 * </pre></blockquote></p>
 *  
 * @author richard.opalka@jboss.com
 */
public interface RMCreateSequenceResponse extends RMSerializable
{
   /**
    * The RM Destination MUST include this element within any CreateSequenceResponse message it
    * sends. The RM Destination MUST set the value of this element to the absolute URI (conformant
    * with RFC3986) that uniquely identifies the Sequence that has been created by the RM Destination.
    * @param identifier
    */
   void setIdentifier(String identifier);
   
   /**
    * Getter 
    * @return sequence identifier
    */
   String getIdentifier();
   
   /**
    * This element, if present, of type <b>xs:duration</b> accepts or refines the RM Source's requested
    * duration for the Sequence. It specifies the amount of time after which any resources associated
    * with the Sequence SHOULD be reclaimed thus causing the Sequence to be silently terminated. At
    * the RM Destination this duration is measured from a point proximate to Sequence creation and at
    * the RM Source this duration is measured from a point approximate to the successful processing of
    * the <b>CreateSequenceResponse</b>. A value of "PT0S" indicates that the Sequence will never
    * expire. Absence of the element indicates an implied value of "PT0S". The RM Destination MUST
    * set the value of this element to be equal to or less than the value requested by the RM Source in
    * the corresponding <b>CreateSequence</b> message.
    * @param duration
    */
   void setExpires(Duration duration);
   
   /**
    * Getter
    * @return sequence duration
    */
   Duration getExpires();
   
   /**
    * This element, if present, specifies the behavior that the destination will exhibit upon the closure or
    * termination of an incomplete Sequence.
    * @param incompleteSequenceBehavior
    */
   void setIncompleteSequenceBehavior(RMIncompleteSequenceBehavior incompleteSequenceBehavior);
   
   /**
    * Getter
    * @return used incomplete sequence behavior type
    */
   RMIncompleteSequenceBehavior getIncompleteSequenceBehavior();
   
   /**
    * Factory method
    * @return new instance of accept
    */
   RMAccept newAccept();
   
   /**
    * Setter
    * @param accept
    */
   void setAccept(RMAccept accept);
   
   /**
    * Getter
    * @return accept
    */
   RMAccept getAccept();
   
   /**
    * <p>This element, if present, enables an RM Destination to accept the offer of a corresponding
    * Sequence for the reliable exchange of messages Transmitted from RM Destination to RM Source.</p>
    * 
    * <p>Note: If a <b>CreateSequenceResponse</b> is returned without a child Accept in response to a
    * <b>CreateSequence</b> that did contain a child Offer, then the RM Source MAY immediately reclaim
    * any resources associated with the unused offered Sequence.</p>
    */
   interface RMAccept
   {
      /**
       * <p>The RM Destination MUST include this element, of type <b>wsa:EndpointReferenceType</b> (as
       * specified by WS-Addressing). It specifies the endpoint reference to which messages containing
       * <b>SequenceAcknowledgement</b> header blocks and faults related to the created Sequence are to
       * be sent, unless otherwise noted in this specification.</p>
       * 
       * <p>Implementations MUST NOT use an endpoint reference in the AcksTo element that would prevent
       * the sending of Sequence Acknowledgements back to the RM Source. For example, using the 
       * WS-Addressing "http://www.w3.org/2005/08/addressing/none" URI would make it impossible for the RM
       * Destination to ever send Sequence Acknowledgements.</p>
       * @param address
       */
      void setAcksTo(String address);
      
      /**
       * Getter
       * @return address
       */
      String getAcksTo();
   }
}
