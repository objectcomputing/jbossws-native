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
 * <p>This element, if present, specifies the behavior that the destination will exhibit upon the closure or
 * termination of an incomplete Sequence. For the purposes of defining the values used, the term
 * "discard" refers to behavior equivalent to the Application Destination never processing a particular message.</p>
 * 
 * The following schema snippet defines allowed values:
 * <p><blockquote><pre>
 * &lt;xs:simpleType name=&quot;IncompleteSequenceBehaviorType&quot;&gt;
 *     &lt;xs:restriction base=&quot;xs:string&quot;&gt;
 *         &lt;xs:enumeration value=&quot;DiscardEntireSequence&quot;/&gt;
 *         &lt;xs:enumeration value=&quot;DiscardFollowingFirstGap&quot;/&gt;
 *         &lt;xs:enumeration value=&quot;NoDiscard&quot;/&gt;
 *     &lt;/xs:restriction&gt;
 * &lt;/xs:simpleType&gt;
 * </pre></blockquote></p>
 * 
 * @author richard.opalka@jboss.com
 */
public enum RMIncompleteSequenceBehavior
{
   /**
    * A value of <b>&quot;DiscardEntireSequence&quot;</b> indicates that the entire Sequence MUST be discarded if the
    * Sequence is closed, or terminated, when there are one or more gaps in the final <b>SequenceAcknowledgement</b>.
    */
   DISCARD_ENTIRE_SEQUENCE("DiscardEntireSequence"),
   
   /**
    * A value of <b>&quot;DiscardFollowingFirstGap&quot;</b> indicates that messages in the Sequence beyond the first
    * gap MUST be discarded when there are one or more gaps in the final <b>SequenceAcknowledgement</b>.
    */
   DISCARD_FOLLOWING_FIRST_GAP("DiscardFollowingFirstGap"),
   
   /**
    * The default value of <b>&quot;NoDiscard&quot;</b> indicates that no acknowledged messages in the Sequence will
    * be discarded.
    */
   NO_DISCARD("NoDiscard");
   
   private final String value;
   
   RMIncompleteSequenceBehavior(String value)
   {
      this.value = value;
   }
   
   public String toString()
   {
      return value;
   }
   
   /**
    * Returns this enum instance if value string matches, <b>null</b> otherwise
    * @param stringValue value in the form of string
    * @return enum or null if string not recognized
    */
   public static RMIncompleteSequenceBehavior getValue(String stringValue)
   {
      if (DISCARD_ENTIRE_SEQUENCE.toString().equals(stringValue)) return DISCARD_ENTIRE_SEQUENCE;
      if (DISCARD_FOLLOWING_FIRST_GAP.toString().equals(stringValue)) return DISCARD_FOLLOWING_FIRST_GAP;
      if (NO_DISCARD.toString().equals(stringValue)) return NO_DISCARD;
      return null;
   }
   
}
