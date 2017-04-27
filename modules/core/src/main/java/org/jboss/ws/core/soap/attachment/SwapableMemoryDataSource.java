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
package org.jboss.ws.core.soap.attachment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.wsf.common.IOUtils;

/**
 * A datasource which offloads large attachments to disk.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 */
public class SwapableMemoryDataSource implements DataSource
{
   private static Logger log = Logger.getLogger(SwapableMemoryDataSource.class);

   private static final int BLOCK_SIZE = 32 * 1024;

   private static final int DEFAULT_MAX_MEMORY_SIZE = 64 * 1024;

   private static final String SWAP_PREFIX = "JBossWSattachment";

   private static final String SWAP_SUFFIX = ".dat";

   private File swapFile;

   private String contentType = MimeConstants.TYPE_APPLICATION_OCTET_STREAM;

   private byte[] content;

   private int contentLength;

   private int maxMemorySize = 64 * 1024;


   /**
    * Constructs a <code>SwapableMemoryDataSource</code> from inputStream, and contentType.
    * The instance then reads from the input stream, and stores it in memory unless the size
    * of the content is larger that 64KB, at whichpoint the stream is stored in a temporary
    * file on disk.
    *
    * @param inputStream the stream to read from
    * @param contentType the content type of this stream
    */
   public SwapableMemoryDataSource(InputStream inputStream, String contentType) throws IOException
   {
      this(inputStream, contentType, DEFAULT_MAX_MEMORY_SIZE);
   }

   /**
    * Constructs a <code>SwapableMemoryDataSource</code> from inputStream, and
    * contentType. The instance then reads from the input stream, and stores it
    * in memory unless the size of the content is larger than maxMemorySize, at
    * whichpoint the stream is stored in a temporary file on disk.
    *
    * @param inputStream the stream to read from
    * @param contentType the content type of this stream
    * @param maxMemorySize the maximum size in bytes that this data source is
    *                      allowed to allocate for stream storage
    */
   public SwapableMemoryDataSource(InputStream inputStream, String contentType, int maxMemorySize) throws IOException
   {
      if (contentType != null)
         this.contentType = contentType;

      this.maxMemorySize = maxMemorySize;

      load(inputStream);
   }

   private void load(InputStream inputStream) throws IOException
   {
      RawByteArrayOutputStream rbaos = new RawByteArrayOutputStream();
      OutputStream os = rbaos;

      byte[] buffer = new byte[BLOCK_SIZE];
      int count = inputStream.read(buffer);
      while (count > 0) {
         os.write(buffer, 0, count);

         if (rbaos != null && rbaos.size() > maxMemorySize)
         {
            File tmpdir = IOUtils.createTempDirectory();
            swapFile = File.createTempFile(SWAP_PREFIX, SWAP_SUFFIX, tmpdir);
            swapFile.deleteOnExit();
            os = new FileOutputStream(swapFile);
            rbaos.writeTo(os);
            rbaos = null;
         }

            count = inputStream.read(buffer);
      }

      os.flush();
      os.close();

      if (rbaos == null)
      {
         if(log.isDebugEnabled()) log.debug("Using swap file, location = " + swapFile.toURL() + " size = " + swapFile.length());
      }
      else
      {
         contentLength = rbaos.size();
         if(log.isDebugEnabled()) log.debug("Using memory buffer, size = " + contentLength);
         content = rbaos.getBytes();
      }
   }

   protected void finalize() throws Throwable
   {
      super.finalize();
      cleanup();
   }

   public void cleanup()
   {
      if (swapFile != null)
         swapFile.delete();
   }

   /**
    * Returns the content type of this data source.
    *
    * @return the content type
    */
   public String getContentType()
   {
      return contentType;
   }

   /**
    * Returns a new input stream on this data source. Multiple calls
    * are allowed because the data is stored.
    *
    * @return a new input stream at the start of the data
    */
   public InputStream getInputStream() throws IOException
   {
      if (content != null)
         return new ByteArrayInputStream(content, 0, contentLength);

      if (swapFile != null)
         return new FileInputStream(swapFile);

      throw new WSException("No content available");
   }

   /**
    * This method always returns null.
    *
    * @return null
    */
   public String getName()
   {
      return null;
   }

   /**
    * This method always returns null.
    *
    * @return null
    */
   public OutputStream getOutputStream() throws IOException
   {
      return null;
   }
}
