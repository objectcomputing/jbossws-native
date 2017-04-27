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
 * <p><b>SequenceFault</b> element purpose is to carry the specific details of a fault generated during the
 * reliable messaging specific processing of a message belonging to a Sequence. WS-ReliableMessaging
 * nodes MUST use the <b>SequenceFault</b> container only in conjunction with the SOAP 1.1 fault mechanism.
 * WS-ReliableMessaging nodes MUST NOT use the <b>SequenceFault</b> container in conjunction with the
 * SOAP 1.2 binding.</p>
 * 
 * The following infoset defines its syntax:
 * <p><blockquote><pre>
 * &lt;SequenceFault ...&gt;
 *     &lt;wsrm:FaultCode&gt; wsrm:FaultCode &lt;/wsrm:FaultCode&gt;
 *     &lt;wsrm:Detail&gt; ... &lt;/wsrm:Detail&gt; ?
 *     ...
 * &lt;/SequenceFault&gt;
 * </pre></blockquote></p>
 * 
 * @author richard.opalka@jboss.com
 */
public interface RMSequenceFault extends RMSerializable
{
   /**
    * WS-ReliableMessaging nodes that generate a <b>SequenceFault</b> MUST set the value of this
    * element to a qualified name from the set of faults [Subcodes] defined below.
    * @param faultCode
    */
   void setFaultCode(RMSequenceFaultCode faultCode);
   
   /**
    * Getter
    * @return sequence fault code
    */
   RMSequenceFaultCode getFaultCode();
   
   /**
    * This element, if present, carries application specific error information
    * related to the fault being described.
    * @param detail
    */
   void setDetail(Exception detail);
   
   /**
    * Getter
    * @return application specific fault detail
    */
   Exception getDetail();
}
