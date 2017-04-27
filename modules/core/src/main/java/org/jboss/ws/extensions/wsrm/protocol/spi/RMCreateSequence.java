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
 * <p><b>CreateSequence</b> element requests creation of a new Sequence between the RM Source that sends it, and the
 * RM Destination to which it is sent. The RM Source MUST NOT send this element as a header
 * block. The RM Destination MUST respond either with a <b>CreateSequenceResponse</b> response
 * message or a <b>CreateSequenceRefused</b> fault.</p>
 * 
 * The following infoset defines its syntax:
 * <p><blockquote><pre>
 * &lt;wsrm:CreateSequence ...&gt;
 *     &lt;wsrm:AcksTo&gt; wsa:EndpointReferenceType &lt;/wsrm:AcksTo&gt;
 *     &lt;wsrm:Expires ...&gt; xs:duration &lt;/wsrm:Expires&gt; ?
 *     &lt;wsrm:Offer ...&gt;
 *         &lt;wsrm:Identifier ...&gt; xs:anyURI &lt;/wsrm:Identifier&gt;
 *         &lt;wsrm:Endpoint&gt; wsa:EndpointReferenceType &lt;/wsrm:Endpoint&gt;
 *         &lt;wsrm:Expires ...&gt; xs:duration &lt;/wsrm:Expires&gt; ?
 *         &lt;wsrm:IncompleteSequenceBehavior&gt;
 *             wsrm:IncompleteSequenceBehaviorType
 *         &lt;/wsrm:IncompleteSequenceBehavior&gt; ?
 *          ... 
 *     &lt;/wsrm:Offer&gt; ?
 *     ... 
 * &lt;/wsrm:CreateSequence&gt;
 * </pre></blockquote></p>
 *  
 * @author richard.opalka@jboss.com
 */
public interface RMCreateSequence extends RMSerializable
{
   /**
    * <p>The RM Source MUST include this element in any CreateSequence message it sends. This
    * element is of type wsa:EndpointReferenceType (as specified by WS-Addressing). It specifies
    * the endpoint reference to which messages containing SequenceAcknowledgement header
    * blocks and faults related to the created Sequence are to be sent, unless otherwise noted in this
    * specification.</p>
    * <p>Implementations MUST NOT use an endpoint reference in the AcksTo element that would prevent
    * the sending of Sequence Acknowledgements back to the RM Source. For example, using the WS-Addressing
    * "http://www.w3.org/2005/08/addressing/none" URI would make it impossible for the RM
    * Destination to ever send Sequence Acknowledgements.</p>
    * @param address
    */
   void setAcksTo(String address);
   
   /**
    * Getter
    * @return address
    */
   String getAcksTo();

   /**
    * This element, if present, of type <b>xs:duration</b> specifies the RM Source's requested duration for
    * the Sequence. The RM Destination MAY either accept the requested duration or assign a lesser
    * value of its choosing. A value of <b>&quot;PT0S&quot;</b> indicates that the Sequence will never expire. Absence of
    * the element indicates an implied value of <b>&quot;PT0S&quot;</b>.
    * @param duration
    */
   void setExpires(Duration duration);
   
   /**
    * Getter
    * @return duration
    */
   Duration getExpires();
   
   /**
    * Factory method
    * @return new instance of Offer
    */
   RMOffer newOffer();
   
   /**
    * Setter
    * @param offer
    */
   void setOffer(RMOffer offer);
   
   /**
    * Getter
    * @return offer
    */
   RMOffer getOffer();

   /**
    * This element, if present, enables an RM Source to offer a corresponding Sequence for the reliable
    * exchange of messages Transmitted from RM Destination to RM Source.
    */
   interface RMOffer
   {
      /**
       * The RM Source MUST set the value of this element to an absolute URI (conformant with
       * RFC3986 [URI]) that uniquely identifies the offered Sequence.
       * @param identifier
       */
      void setIdentifier(String identifier);
      
      /**
       * Getter 
       * @return offered sequence identifier
       */
      String getIdentifier();
      
      /**
       * <p>An RM Source MUST include this element, of type <b>wsa:EndpointReferenceType</b> (as
       * specified by WS-Addressing). This element specifies the endpoint reference to which Sequence
       * Lifecycle Messages, Acknowledgement Requests, and fault messages related to the offered
       * Sequence are to be sent.</p>
       * 
       * <p>Implementations MUST NOT use an endpoint reference in the Endpoint element that would
       * prevent the sending of Sequence Lifecycle Message, etc. For example, using the WS-Addressing
       * "http://www.w3.org/2005/08/addressing/none" URI would make it impossible for the RM Destination
       * to ever send Sequence Lifecycle Messages (e.g. <b>TerminateSequence</b>) to the RM Source for
       * the Offered Sequence.</p>
       * 
       * <p>The Offer of an Endpoint containing the "http://www.w3.org/2005/08/addressing/anonymous" URI
       * as its address is problematic due to the inability of a source to connect to this address and retry
       * unacknowledged messages. Note that this specification does not
       * define any mechanisms for providing this assurance. In the absence of an extension that
       * addresses this issue, an RM Destination MUST NOT accept (via the
       * <b>/wsrm:CreateSequenceResponse/wsrm:Accept</b>) an Offer that
       * contains the "http://www.w3.org/2005/08/addressing/anonymous" URI as its address.</p>
       * @param address
       */
      void setEndpoint(String address);
      
      /**
       * Getter
       * @return offered endpoint address
       */
      String getEndpoint();
      
      /**
       * This element, if present, of type <b>xs:duration</b> specifies the duration for the offered Sequence. A
       * value of <b>&quot;PT0S&quot;</b> indicates that the offered Sequence will never expire. Absence of the element
       * indicates an implied value of <b>&quot;PT0S&quot;</b>.
       * @param duration
       */
      void setExpires(String duration);
      
      /**
       * Getter
       * @return offered sequence duration
       */
      String getExpires();
      
      /**
       * This element, if present, specifies the behavior that the destination will exhibit upon the closure or
       * termination of an incomplete Sequence.
       * @param incompleteSequenceBehavior
       */
      void setIncompleteSequenceBehavior(RMIncompleteSequenceBehavior incompleteSequenceBehavior);
      
      /**
       * Getter
       * @return offered incomplete sequence behavior
       */
      RMIncompleteSequenceBehavior getIncompleteSequenceBehavior();
   }
}
