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
package org.jboss.ws.extensions.wsrm;

/**
 * Represents message delivery assurance
 * 
 * @author richard.opalka@jboss.com
 * @see org.jboss.ws.extensions.wsrm.RMDeliveryAssuranceFactory
 */
public interface RMDeliveryAssurance
{

   /**
    * Returns associated message delivery assurance
    * @return non null delivery assurance object
    */
   RMDeliveryQuality getDeliveryQuality();
   
   /**
    * Specifies whether messages are to be delivered in order to the service instance or client proxy
    * @return <b>true</b> if in order delivery is required, <b>false</b> otherwise
    */
   boolean inOrder();
   
}
