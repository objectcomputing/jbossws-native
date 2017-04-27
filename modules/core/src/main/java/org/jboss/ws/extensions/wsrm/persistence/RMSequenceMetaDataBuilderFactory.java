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

import org.jboss.util.NotImplementedException;

/**
 * Sequence metadata builder factory
 *
 * @author richard.opalka@jboss.com
 */
public final class RMSequenceMetaDataBuilderFactory
{

   private static RMSequenceMetaDataBuilderFactory instance = new RMSequenceMetaDataBuilderFactory();
   
   private RMSequenceMetaDataBuilderFactory()
   {
      // forbidden instantiation
   }
   
   /**
    * Gets factory instance
    * @return factory instance
    */
   public static final RMSequenceMetaDataBuilderFactory getInstance()
   {
      return instance;
   }
   
   /**
    * Returns new sequence metadata builder instance
    * @return new sequence metadata builder instance
    */
   public final RMSequenceMetaDataBuilder newBuilder()
   {
      return new RMSequenceMetaDataBuilderImpl();
   }
   
   /**
    * @see org.jboss.ws.extensions.wsrm.persistence.RMSequenceMetaDataBuilder
    */
   private static class RMSequenceMetaDataBuilderImpl implements RMSequenceMetaDataBuilder
   {

      public void clear()
      {
         throw new NotImplementedException();
      }

      public RMSequenceMetaDataBuilder setAcksToAddress(String acksToAddr)
      {
         throw new NotImplementedException();
      }

      public RMSequenceMetaDataBuilder setADDRVersion(String addrVersion)
      {
         throw new NotImplementedException();
      }

      public RMSequenceMetaDataBuilder setEndpointAddress(String endpointAddr)
      {
         throw new NotImplementedException();
      }

      public RMSequenceMetaDataBuilder setSOAPVersion(String soapVersion)
      {
         throw new NotImplementedException();
      }

      public RMSequenceMetaDataBuilder setWSRMVersion(String wsrmVersion)
      {
         throw new NotImplementedException();
      }

      public RMSequenceMetaData toSequenceMetaData()
      {
         throw new NotImplementedException();
      }
      
   }
   
}
