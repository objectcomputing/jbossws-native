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
package org.jboss.ws.extensions.wsrm.protocol.spec200502;

import org.jboss.ws.extensions.wsrm.protocol.RMConstants;
import org.jboss.ws.extensions.wsrm.protocol.RMMessageFactory;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.common.RMConstantsImpl;

/*
 * @author richard.opalka@jboss.com
 * @see org.jboss.ws.extensions.wsrm.spi.Provider
 */
public final class RMProviderImpl extends RMProvider
{
   
   public static final String IMPLEMENTATION_VERSION = "http://schemas.xmlsoap.org/ws/2005/02/rm";
   private static final RMConstants CONSTANTS = new RMConstantsImpl("wsrm10", IMPLEMENTATION_VERSION);
   private static final RMProvider INSTANCE = new RMProviderImpl();
   
   public RMProviderImpl()
   {
      // forbidden inheritance
   }
   
   public static RMProvider getInstance()
   {
      return INSTANCE; 
   }
   
   /*
    * @see org.jboss.ws.extensions.wsrm.spi.Provider#getMessageFactory()
    */
   @Override
   public RMMessageFactory getMessageFactory()
   {
      return RMMessageFactoryImpl.getInstance();
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.Provider#getConstants()
    */
   @Override
   public RMConstants getConstants()
   {
      return CONSTANTS;
   }

   /*
    * @see org.jboss.ws.extensions.wsrm.spi.Provider#getNamespaceURI()
    */
   @Override
   public String getNamespaceURI()
   {
      return IMPLEMENTATION_VERSION;
   }
   
}
