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

import java.util.Collections;
import java.util.Map;

import javax.xml.ws.WebServiceException;

/**
 * RM fault
 *
 * @author richard.opalka@jboss.com
 * 
 * @see org.jboss.ws.extensions.wsrm.RMFaultCode
 * @see org.jboss.ws.extensions.wsrm.RMFaultConstant
 */
public class RMFault extends WebServiceException
{

   private final RMFaultCode faultCode;
   private final Map<String, Object> details;
   
   /**
    * Constructor
    * @param faultCode reason
    */
   public RMFault(RMFaultCode faultCode)
   {
      this(faultCode, Collections.EMPTY_MAP);
   }
   
   /**
    * Constructor
    * @param faultCode reason
    * @param details to be de/serialized
    */
   public RMFault(RMFaultCode faultCode, Map<String, Object> details)
   {
      super();
      this.faultCode = faultCode;
      this.details = details;
   }
   
   /**
    * Gets fault code
    * @return fault code
    */
   public final RMFaultCode getFaultCode()
   {
      return this.faultCode;
   }
   
   /**
    * Gets details
    * @return details
    */
   public final Map<String, Object> getDetails()
   {
      return this.details;
   }

   @Override
   public String getMessage()
   {
      return faultCode.getReason();
   }

}
