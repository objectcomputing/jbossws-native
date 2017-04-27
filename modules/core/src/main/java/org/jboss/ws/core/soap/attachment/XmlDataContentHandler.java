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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.xml.transform.stream.StreamSource;

import org.jboss.wsf.common.IOUtils;

/**
 * <code>XmlDataContentHandler</code> is a JAF content handler that provides
 * marchalling/unmarshalling between a <code>StreamSource</code> and a generic
 * stream.
 *
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 */
public class XmlDataContentHandler implements DataContentHandler
{
   private DataFlavor[] flavors = new ActivationDataFlavor[]
   {
         new ActivationDataFlavor(StreamSource.class, "text/xml", "XML"),
         new ActivationDataFlavor(StreamSource.class, "application/xml", "XML")
   };

   /**
    * Returns a {@link StreamSource} from the specified
    * data source.
    *
    * @param ds the activation datasource
    * @return an XML stream source
    */
   public Object getContent(DataSource ds) throws IOException
   {
      return new StreamSource(ds.getInputStream());
   }

   /**
    * Returns a {@link StreamSource} from the specified
    * data source. The flavor must be one of the ones returned by {@link #getTransferDataFlavors()}.
    *
    * @param df the flavor specifiying the mime type of ds
    * @param ds the activation data source
    * @return an XML stream source
    */
   public Object getTransferData(DataFlavor df, DataSource ds) throws UnsupportedFlavorException, IOException
   {
      return getContent(ds);
   }

   /**
    * Returns the acceptable data flavors that this content handler supports.
    *
    * @return array of <code>ActivationDataHandlers</code>
    */
   public DataFlavor[] getTransferDataFlavors()
   {
      return flavors;
   }

   /**
    * Writes the passed in {@link StreamSource} object using the specified
    * mime type to the specified output stream. The mime type must be text/xml.
    *
    * @param obj an XML stream source
    * @param mimeType the string "text/xml"
    * @param os the output stream to write this xml stream to
    */
   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException
   {
      if (! mimeType.startsWith(MimeConstants.TYPE_TEXT_XML) && ! mimeType.startsWith("application/xml"))
         throw new IOException("Expected text/xml or application/xml, got " + mimeType);

      if (! (obj instanceof StreamSource))
         throw new IOException("XML Content handler only supports a StreamSource content object");

      // TODO - add support for reader source
      InputStream stream = ((StreamSource) obj).getInputStream();
      if (stream != null)
      {
         IOUtils.copyStream(os, stream);
      }
      else
      {
         IOUtils.copyReader(os, ((StreamSource) obj).getReader());

      }
   }
}
