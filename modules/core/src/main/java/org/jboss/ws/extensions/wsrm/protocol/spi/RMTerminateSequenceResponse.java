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
 * <p><b>TerminateSequenceResponse</b> is sent in the body of a message in response to receipt of a <b>TerminateSequence</b> 
 * request message. It indicates that the responder has terminated the Sequence. The responder
 * MUST NOT send this element as a header block.</p>
 * 
 * The following infoset defines its syntax:
 * <p><blockquote><pre>
 * &lt;wsrm:TerminateSequenceResponse ...&gt;
 *     &lt;wsrm:Identifier ...&gt; xs:anyURI &lt;/wsrm:Identifier&gt;
 *     ... 
 * &lt;/wsrm:TerminateSequenceResponse&gt;
 * </pre></blockquote></p>
 *  
 * @author richard.opalka@jboss.com
 */
public interface RMTerminateSequenceResponse extends RMSerializable
{
   /**
    * The responder (RM Source or RM Destination) MUST include this element in any
    * <b>TerminateSequenceResponse</b> message it sends. The responder MUST set the value of this
    * element to the absolute URI (conformant with RFC3986) of the terminating Sequence.
    * @param identifier
    */
   void setIdentifier(String identifier);

   /**
    * Getter
    * @return sequence identifier
    */
   String getIdentifier();
}
