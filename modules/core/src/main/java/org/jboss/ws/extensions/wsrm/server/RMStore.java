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
package org.jboss.ws.extensions.wsrm.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jboss.logging.Logger;

/**
 * Server side RM store that de/serializes sequences
 *
 * @author richard.opalka@jboss.com
 */
public final class RMStore
{
   
   private static final Logger logger = Logger.getLogger(RMStore.class);
   
   public static final void serialize(String dataDir, RMServerSequence seq)
   {
      File dir = new File(dataDir);
      if (false == dir.exists())
      {
         dir.mkdirs();
      }
      File sequenceFile = new File(dir, seq.getId());
      if (seq.isTerminated() && sequenceFile.exists())
      {
         // throw away terminated sequences
         sequenceFile.delete();
         return;
      }
      
      FileOutputStream fos = null;
      try
      {
         fos = new FileOutputStream(sequenceFile);
         fos.write(seq.toByteArray());
      }
      catch (IOException ioe)
      {
         logger.error("Can't write sequence to file " + sequenceFile.getName(), ioe);
      }
      finally
      {
         if (fos != null)
         {
            try
            {
               fos.close();
            }
            catch (IOException ioe)
            {
               logger.error("Can't close sequence file " + sequenceFile.getName(), ioe);
            }
         }
      }
   }
   
   public static final RMServerSequence deserialize(String dataDir, String seqId, boolean inbound)
   {
      File[] sequences = new File(dataDir).listFiles();
      for (int i = 0; i < sequences.length; i++)
      {
         try
         {
            RMServerSequence sequence = new RMServerSequence(sequences[i]);
            boolean matches = inbound ? sequence.getInboundId().equals(seqId) : sequence.getOutboundId().equals(seqId);
            if (matches)
            {
               return sequence;
            }
            if ((sequence.getCreationTime() + sequence.getDuration()) >= System.currentTimeMillis())
            {
               sequences[i].delete(); // clean up timeouted sequences
            }
         }
         catch (IOException ioe)
         {
            logger.error("Can't read sequence from file " + sequences[i].getName(), ioe);
         }
      }
      
      return null;
   }
   
}
