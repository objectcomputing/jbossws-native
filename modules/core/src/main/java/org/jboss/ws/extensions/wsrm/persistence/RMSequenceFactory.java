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
 * Sequence factory used on both client and server sides
 *
 * @author richard.opalka@jboss.com
 */
public final class RMSequenceFactory
{

   private static RMSequenceFactory instance = new RMSequenceFactory();
   
   private RMSequenceFactory()
   {
      // forbidden inheritance
   }
   
   /**
    * Gets factory instance
    * @return factory instance
    */
   public static final RMSequenceFactory getInstance()
   {
      return instance;
   }
   
   /**
    * Creates new sequence instance. This method will be used on server side only.
    * The created sequence will have automatically generated both inbound and outbound ids.
    * @param seqMD sequence metadata
    * @return new server sequence instance
    */
   public RMSequence newSequence(RMSequenceMetaData seqMD)
   {
      return newSequence(seqMD, null);
   }
   
   /**
    * Creates new sequence instance. This method will be used on client side only.
    * The created sequence will have outbound id set to the value passed via <b>outboundId</b> parameter
    * and inbound id will be null until client will change it to the specified value lazily.
    * @param seqMD sequence metadata
    * @param outboundId outbound sequence id
    * @return new client sequence instance
    */
   public RMSequence newSequence(RMSequenceMetaData seqMD, String outboundId)
   {
      throw new NotImplementedException();
   }
   
}
