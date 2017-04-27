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

import javax.xml.soap.SOAPMessage;

import org.jboss.ws.extensions.wsrm.api.RMException;

/**
 * This interface identifies classes that are de/serializable from/to SOAP messages
 *
 * @author richard.opalka@jboss.com
 */
public interface RMSerializable
{
   
   /**
    * Serialize object instance to SOAP message
    * @param soapMessage 
    * @throws RMException is something went wrong
    */
   void serializeTo(SOAPMessage soapMessage) throws RMException;
   
   /**
    * Deserialize object instance from SOAP message
    * @param soapMessage ReliableMessagingException is something went wrong
    */
   void deserializeFrom(SOAPMessage soapMessage) throws RMException;
   
   /**
    * Validate object state if everything is all right
    * @throws  RMException if object is in incorrect state
    */
   void validate() throws RMException;
   
}
