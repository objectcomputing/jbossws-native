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

import java.util.List;

/**
 * 
 * <p><b>wsrm:SequenceAcknowledgement</b> element contains the sequence acknowledgement information</p>
 * 
 * The following infoset defines its syntax:
 * <p><blockquote><pre>
 * &lt;wsrm:SequenceAcknowledgement ...&gt;
 *     &lt;wsrm:Identifier ...&gt; xs:anyURI &lt;/wsrm:Identifier&gt;
 *     [ [ [ &lt;wsrm:AcknowledgementRange  ...
 *             Upper="wsrm:MessageNumberType"
 *             Lower="wsrm:MessageNumberType"/&gt; + 
 *         | &lt;wsrm:None/&gt; ]
 *         &lt;wsrm:Final/&gt; ? ]
 *     | &lt;wsrm:Nack&gt; wsrm:MessageNumberType &lt;/wsrm:Nack&gt; + ]
 *     
 *     ... 
 * &lt;/wsrm:SequenceAcknowledgement&gt;
 * </pre></blockquote></p>
 *  
 * @author richard.opalka@jboss.com
 */
public interface RMSequenceAcknowledgement extends RMSerializable
{
   /**
    * An RM Destination that includes a <b>SequenceAcknowledgement</b> header block in a SOAP
    * envelope MUST include this element in that header block. The RM Destination MUST set the
    * value of this element to the absolute URI (conformant with RFC3986) that uniquely identifies the
    * Sequence. The RM Destination MUST NOT include multiple <b>SequenceAcknowledgement</b>
    * header blocks that share the same value for <b>Identifier</b> within the same SOAP envelope.
    * @param identifier
    */
   void setIdentifier(String identifier);
   
   /**
    * Getter
    * @return sequence identifier
    */
   String getIdentifier();
   
   /**
    * The RM Destination MAY include this element within a <b>SequenceAcknowledgement</b> header
    * block. This element indicates that the RM Destination is not receiving new messages for the
    * specified Sequence. The RM Source can be assured that the ranges of messages acknowledged
    * by this SequenceAcknowledgement header block will not change in the future. The RM
    * Destination MUST include this element when the Sequence is closed. The RM Destination MUST
    * NOT include this element when sending a <b>Nack</b>; it can only be used when sending
    * <b>AcknowledgementRange</b> elements or a <b>None</b>.
    */
   void setFinal();
   
   /**
    * Getter
    * @return true if <b>SequenceAcknowledgement</b> is <b>Final</b>, false otherwise
    */
   boolean isFinal();
   
   /**
    * The RM Destination MUST include this element within a <b>SequenceAcknowledgement</b> header
    * block if the RM Destination has not accepted any messages for the specified Sequence. The RM
    * Destination MUST NOT include this element if a sibling <b>AcknowledgementRange</b> or <b>Nack</b>
    * element is also present as a child of the <b>SequenceAcknowledgement</b>.
    */
   void setNone();
   
   /**
    * Getter
    * @return true if <b>SequenceAcknowledgement</b> is <b>None</b>, false otherwise
    */
   boolean isNone();
   
   /**
    * The RM Destination MAY include this element within a <b>SequenceAcknowledgement</b> header
    * block. If used, the RM Destination MUST set the value of this element to a <b>MessageNumberType</b>
    * representing the <b>MessageNumber</b> of an unreceived message in a Sequence. The RM Destination
    * MUST NOT include a <b>Nack</b> element if a sibling <b>AcknowledgementRange</b> or <b>None</b> element is
    * also present as a child of <b>SequenceAcknowledgement</b>. Upon the receipt of a <b>Nack</b>, an RM
    * Source SHOULD retransmit the message identified by the <b>Nack</b>. The RM Destination MUST NOT
    * issue a <b>SequenceAcknowledgement</b> containing a <b>Nack</b> for a message that it has previously
    * acknowledged within an <b>AcknowledgementRange</b>. The RM Source SHOULD ignore a
    * <b>SequenceAcknowledgement</b> containing a <b>Nack</b> for a message that has previously been
    * acknowledged within an <b>AcknowledgementRange</b>.
    * @param messageNumber
    */
   void addNack(long messageNumber);
   
   /**
    * Getter
    * @return list of not ackonwledged message numbers
    */
   List<Long> getNacks();
   
   /**
    * Factory method
    * @return new instance of AcknowledgementRange
    */
   RMAcknowledgementRange newAcknowledgementRange();
   
   /**
    * Setter
    * @param acknowledgementRange
    */
   void addAcknowledgementRange(RMAcknowledgementRange acknowledgementRange);
   
   /**
    * Getter 
    * @return list of acknowledged ranges
    */
   List<RMAcknowledgementRange> getAcknowledgementRanges();
   
   /**
    * The RM Destination MAY include one or more instances of this element within a
    * <b>SequenceAcknowledgement</b> header block. It contains a range of Sequence message numbers
    * successfully accepted by the RM Destination. The ranges MUST NOT overlap. The RM
    * Destination MUST NOT include this element if a sibling <b>Nack</b> or <b>None</b> element is also present as
    * a child of <b>SequenceAcknowledgement</b>.
    */
   interface RMAcknowledgementRange
   {
      /**
       * The RM Destination MUST set the value of this attribute equal to the message number of the
       * highest contiguous message in a Sequence range accepted by the RM Destination.
       * @param upper
       */
      void setUpper(long upper);
      
      /**
       * Getter
       * @return upper value
       */
      long getUpper();
      
      /**
       * The RM Destination MUST set the value of this attribute equal to the message number of the
       * lowest contiguous message in a Sequence range accepted by the RM Destination.
       * @param lower
       */
      void setLower(long lower);
      
      /**
       * Getter
       * @return lower value
       */
      long getLower();
   }
}
