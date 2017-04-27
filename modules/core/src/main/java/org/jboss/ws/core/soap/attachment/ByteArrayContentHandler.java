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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.jboss.ws.core.utils.MimeUtils;
import org.jboss.wsf.common.IOUtils;

/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Jul 31, 2006
 */
public class ByteArrayContentHandler implements DataContentHandler {

   private DataFlavor[] flavors = new ActivationDataFlavor[]
   {
         new ActivationDataFlavor(ByteArrayInputStream.class, "application/octet-stream", "OCTETS"),
   };

   public Object getContent(DataSource dataSource) throws IOException {
      return dataSource.getInputStream();
   }

   public Object getTransferData(DataFlavor dataFlavor, DataSource dataSource) throws UnsupportedFlavorException, IOException {
      return getContent(dataSource);
   }

   public DataFlavor[] getTransferDataFlavors() {
      return flavors;
   }

   public void writeTo(Object object, String string, OutputStream outputStream) throws IOException {

      if(object instanceof byte[])
      {
         outputStream.write((byte[])object);
      }
      else if(object instanceof DataHandler)
      {
         IOUtils.copyStream(outputStream, ((DataHandler)object).getInputStream());
      }
      else
      {
         MimeUtils.ByteArrayConverter converter = MimeUtils.getConverterForJavaType(object.getClass());
         converter.writeTo(object, outputStream);
      }
   }
}
