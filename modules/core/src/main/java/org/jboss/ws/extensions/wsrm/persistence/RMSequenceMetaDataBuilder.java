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
package org.jboss.ws.extensions.wsrm.persistence;

/**
 * Sequence metadata builder
 *
 * @author richard.opalka@jboss.com
 */
public interface RMSequenceMetaDataBuilder
{
   
   /**
    * Sets sequence SOAP version
    * @param soapVersion SOAP version used during sequence lifetime
    * @return this
    */
   RMSequenceMetaDataBuilder setSOAPVersion(String soapVersion);
   
   /**
    * Sets sequence WSRM version
    * @param wsrmVersion WSRM version used during sequence lifetime
    * @return this
    */
   RMSequenceMetaDataBuilder setWSRMVersion(String wsrmVersion);

   /**
    * Sets sequence ADDR version
    * @param addrVersion ADDR version used during sequence lifetime
    * @return this
    */
   RMSequenceMetaDataBuilder setADDRVersion(String addrVersion);
   
   /**
    * Sets endpoint address
    * @param endpointAddr endpoint address
    * @return this
    */
   RMSequenceMetaDataBuilder setEndpointAddress(String endpointAddr);
   
   /**
    * Sets acks to address
    * @param acksToAddr acks to address
    * @return this
    */
   RMSequenceMetaDataBuilder setAcksToAddress(String acksToAddr);
   
   /**
    * Builded sequence metadata
    * @return sequence metadata
    */
   RMSequenceMetaData toSequenceMetaData();
   
   /**
    * Clear internals so this instance can be reused
    */
   void clear();
   
}
