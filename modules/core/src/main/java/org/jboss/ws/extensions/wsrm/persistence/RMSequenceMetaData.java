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

import org.jboss.ws.extensions.wsrm.RMDeliveryAssurance;

/**
 * Sequence meta data associated with sequence during its lifetime
 *
 * @author richard.opalka@jboss.com
 */
public interface RMSequenceMetaData
{

   /**
    * Gets sequence SOAP version
    * @return sequence SOAP version
    */
   public String  getSOAPVersion();
   
   /**
    * Gets sequence WSRM version
    * @return sequence WSRM version
    */
   public String  getWSRMVersion();
   
   /**
    * Gets sequence ADDR version
    * @return sequence ADDR version
    */
   public String  getADDRVersion();
   
   /**
    * Gets endpoint address
    * @return endpoint address
    */
   public String getEndpointAddress();
   
   /**
    * Gets acks to address
    * @return acks to address
    */
   public String getAcksToAddress();
   
   /**
    * Gets quality of service to be ensured
    * @return quality of service
    */
   public RMDeliveryAssurance getDeliveryAssurance();
   
}
