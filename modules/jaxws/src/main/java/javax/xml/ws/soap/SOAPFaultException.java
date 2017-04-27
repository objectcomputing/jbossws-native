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
package javax.xml.ws.soap;

import javax.xml.soap.SOAPFault;

/** The <code>SOAPFaultException</code> exception represents a
 *  SOAP 1.1 or 1.2 fault.
 *
 *  <p>A <code>SOAPFaultException</code> wraps a SAAJ <code>SOAPFault</code>
 *  that manages the SOAP-specific representation of faults.
 *  The <code>createFault</code> method of
 *  <code>javax.xml.soap.SOAPFactory</code> may be used to create an instance
 *  of <code>javax.xml.soap.SOAPFault</code> for use with the
 *  constructor. <code>SOAPBinding</code> contains an accessor for the
 *  <code>SOAPFactory</code> used by the binding instance.
 *
 *  <p>Note that the value of <code>getFault</code> is the only part of the
 *  exception used when searializing a SOAP fault.
 *
 *  <p>Refer to the SOAP specification for a complete
 *  description of SOAP faults.
 *
 *  @see javax.xml.soap.SOAPFault
 *  @see javax.xml.ws.soap.SOAPBinding#getSOAPFactory
 *  @see javax.xml.ws.ProtocolException
 *
 *  @since JAX-WS 2.0
 **/
public class SOAPFaultException extends javax.xml.ws.ProtocolException
{
   private static final long serialVersionUID = 3948617580148536298L;
   
   private SOAPFault fault;

   /** Constructor for SOAPFaultException
    *  @param fault   <code>SOAPFault</code> representing the fault
    *
    *  @see javax.xml.soap.SOAPFactory#createFault
    **/
   public SOAPFaultException(SOAPFault fault)
   {
      super(fault.getFaultString());
      this.fault = fault;
   }

   /** Gets the embedded <code>SOAPFault</code> instance.
    *
    *  @return <code>javax.xml.soap.SOAPFault</code> SOAP
    *          fault element
    **/
   public javax.xml.soap.SOAPFault getFault()
   {
      return this.fault;
   }
}
