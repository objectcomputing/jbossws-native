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
 * <p><b>CloseSequence</b> element MAY be sent by an RM Source to indicate that the RM Destination MUST NOT
 * accept any new messages for this Sequence This element MAY also be sent by an RM
 * Destination to indicate that it will not accept any new messages for this Sequence.</p>
 * 
 * The following infoset defines its syntax:
 * <p><blockquote><pre>
 * &lt;wsrm:CloseSequence ...&gt;
 *     &lt;wsrm:Identifier ...&gt; xs:anyURI &lt;/wsrm:Identifier&gt;
 *     &lt;wsrm:LastMsgNumber&gt; wsrm:MessageNumberType &lt;/wsrm:LastMsgNumber&gt; ?
 *     ... 
 * &lt;/wsrm:CloseSequence&gt;
 * </pre></blockquote></p>
 *  
 * @author richard.opalka@jboss.com
 */
public interface RMCloseSequence extends RMSerializable
{
   /**
    * The RM Source or RM Destination MUST include this element in any <b>CloseSequence</b> messages it
    * sends. The RM Source or RM Destination MUST set the value of this element to the absolute URI
    * (conformant with RFC3986) of the closing Sequence.
    * @param identifier
    */
   void setIdentifier(String identifier);
   
   /**
    * Getter
    * @return sequence identifier
    */
   String getIdentifier();
   
   /**
    * The RM Source SHOULD include this element in any <b>CloseSequence</b> message it sends. The
    * <b>LastMsgNumber</b> element specifies the highest assigned message number of all the Sequence
    * Traffic Messages for the closing Sequence.
    * @param lastMsgNumber
    */
   void setLastMsgNumber(long lastMsgNumber);
   
   /**
    * Getter
    * @return last message number
    */
   long getLastMsgNumber();
}
