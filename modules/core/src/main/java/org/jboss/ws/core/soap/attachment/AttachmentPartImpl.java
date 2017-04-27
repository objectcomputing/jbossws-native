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

import org.jboss.util.Base64;
import org.jboss.wsf.common.IOUtils;
import org.jboss.ws.WSException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.internet.MimeMultipart;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import java.io.*;
import java.util.Iterator;

/**
 * Implementation of the <code>AttachmentPart</code> interface.
 * @see javax.xml.soap.AttachmentPart
 *
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 * @author Thomas.Diesler@jboss.org
 */
public class AttachmentPartImpl extends AttachmentPart
{
   private MimeHeaders mimeHeaders = new MimeHeaders();

   private DataHandler dataHandler;

   private String cachedContentId;

   private String cachedContentType;

   private String cachedContentLocation;

   static
   {
      // Load JAF content handlers
      ContentHandlerRegistry.register();
   }

   public AttachmentPartImpl()
   {
   }

   public AttachmentPartImpl(DataHandler handler)
   {
      this.dataHandler = handler;
   }

   private void clearHeaderCache()
   {
      cachedContentId = null;
      cachedContentType = null;
      cachedContentLocation = null;
   }

   public void addMimeHeader(String name, String value)
   {
      clearHeaderCache();
      mimeHeaders.addHeader(name, value);
   }

   public void clearContent()
   {
      dataHandler = null;
   }

   public Iterator getAllMimeHeaders()
   {
      clearHeaderCache();
      return mimeHeaders.getAllHeaders();
   }

   public Object getContent() throws SOAPException
   {
      if (dataHandler == null)
         throw new SOAPException("No content available");

      try
      {
         return dataHandler.getContent();
      }
      catch (IOException e)
      {
         throw new SOAPException(e);
      }
   }

   public DataHandler getDataHandler() throws SOAPException
   {
      if (dataHandler == null)
         throw new SOAPException("No data handler on attachment");

      return dataHandler;
   }

   public Iterator getMatchingMimeHeaders(String[] names)
   {
      clearHeaderCache();
      return mimeHeaders.getMatchingHeaders(names);
   }

   public String[] getMimeHeader(String name)
   {
      return mimeHeaders.getHeader(name);
   }

   /**
    * Returns the first occurence of a MIME header.
    *
    * @param header the mime header
    * @return the value of the first occurence of a MIME header
    */
   public String getFirstMimeHeader(String header)
   {
      String[] values = mimeHeaders.getHeader(header.toLowerCase());
      if ((values != null) && (values.length > 0))
      {
         return values[0];
      }
      return null;
   }

   public Iterator getNonMatchingMimeHeaders(String[] names)
   {
      clearHeaderCache();
      return mimeHeaders.getNonMatchingHeaders(names);
   }

   public int getSize() throws SOAPException
   {
      if (dataHandler == null)
         return 0;

      try
      {
         // We may need to buffer the stream, otherwise an additional read may fail
         if((this.dataHandler.getDataSource() instanceof ByteArrayDataSource) == false)
            this.dataHandler = new DataHandler(new ByteArrayDataSource(dataHandler.getInputStream(), dataHandler.getContentType()));

         ByteArrayDataSource ds = (ByteArrayDataSource)this.dataHandler.getDataSource();
         return ds.getSize();
      }
      catch (IOException e)
      {
         throw new SOAPException(e);
      }
   }

   public void removeAllMimeHeaders()
   {
      clearHeaderCache();
      mimeHeaders.removeAllHeaders();
   }

   public void removeMimeHeader(String name)
   {
      clearHeaderCache();
      mimeHeaders.removeHeader(name);
   }

   /**
    * Sets the content of this attachment part to that of the given Object and sets the value of the Content-Type header
    * to the given type. The type of the Object should correspond to the value given for the Content-Type.
    * This depends on the particular set of DataContentHandler objects in use.
    *
    * @param object the Java object that makes up the content for this attachment part
    * @param contentType the MIME string that specifies the type of the content
    * @throws IllegalArgumentException if the contentType does not match the type of the content object,
    * or if there was no DataContentHandler object for this content object
    */
   public void setContent(Object object, String contentType)
   {

      // Override the content type if its a mime multipart object because we need to preserve
      // the all of the content type parameters
      if (object instanceof MimeMultipart)
         contentType = ((MimeMultipart)object).getContentType();

      dataHandler = new DataHandler(object, contentType);

      setContentType(contentType);
   }

   public void setDataHandler(DataHandler dataHandler)
   {
      if (dataHandler == null)
         throw new IllegalArgumentException("Null data handler");

      this.dataHandler = dataHandler;
      setContentType(dataHandler.getContentType());
   }

   public void setMimeHeader(String name, String value)
   {
      clearHeaderCache();
      mimeHeaders.setHeader(name, value);
   }

   public String getContentId()
   {
      if (cachedContentId == null)
      {
         cachedContentId = getFirstMimeHeader(MimeConstants.CONTENT_ID);
      }

      return cachedContentId;
   }

   public String getContentLocation()
   {
      if (cachedContentLocation == null)
      {
         cachedContentLocation = getFirstMimeHeader(MimeConstants.CONTENT_LOCATION);
      }

      return cachedContentLocation;
   }

   public String getContentType()
   {
      if (cachedContentType == null)
      {
         cachedContentType = getFirstMimeHeader(MimeConstants.CONTENT_TYPE);
      }

      return cachedContentType;
   }

   public void setContentId(String contentId)
   {
      setMimeHeader(MimeConstants.CONTENT_ID, contentId);
      cachedContentId = contentId;
   }

   public void setContentLocation(String contentLocation)
   {
      setMimeHeader(MimeConstants.CONTENT_LOCATION, contentLocation);
      cachedContentLocation = contentLocation;
   }

   public void setContentType(String contentType)
   {
      setMimeHeader(MimeConstants.CONTENT_TYPE, contentType);
      cachedContentType = contentType;
   }

   /**
    * Returns an InputStream which can be used to obtain the content of AttachmentPart as Base64 encoded character data,
    * this method would base64 encode the raw bytes of the attachment and return.
    *
    * @return an InputStream from which the Base64 encoded AttachmentPart can be read.
    * @throws SOAPException if there is no content set into this AttachmentPart object or if there was a data transformation error.
    * @since SAAJ 1.3
    */
   @Override
   public InputStream getBase64Content() throws SOAPException
   {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      try
      {
         // TODO: we might skip the buffer and encode lazily
         IOUtils.copyStream(bout, getDataHandler().getInputStream());
         String base64 = Base64.encodeBytes(bout.toByteArray());
         return new ByteArrayInputStream(base64.getBytes());
      }
      catch (IOException e)
      {
         throw new SOAPException(e);
      }
   }

   /**
    * Gets the content of this AttachmentPart object as an InputStream
    * as if a call had been made to getContent and no DataContentHandler
    * had been registered for the content-type of this AttachmentPart.
    *
    * Note that reading from the returned InputStream would result in consuming the data in the stream.
    * It is the responsibility of the caller to reset the InputStream appropriately before calling a Subsequent API.
    * If a copy of the raw attachment content is required then the getRawContentBytes() API should be used instead.
    *
    * @return an InputStream from which the raw data contained by the AttachmentPart can be accessed.
    * @throws SOAPException if there is no content set into this AttachmentPart object or if there was a data transformation error.
    * @since SAAJ 1.3
    */
   @Override
   public InputStream getRawContent() throws SOAPException
   {
      try
      {
         return getDataHandler().getInputStream();
      }
      catch (IOException e)
      {
         throw new SOAPException(e);
      }
   }

   /**
    * Gets the content of this AttachmentPart object as a byte[] array as if a call had been
    * made to getContent and no DataContentHandler had been registered for the content-type of this AttachmentPart.
    *
    * @return a byte[] array containing the raw data of the AttachmentPart.
    * @throws SOAPException if there is no content set into this AttachmentPart object or if there was a data transformation error.
    * @since SAAJ 1.3
    */
   @Override
   public byte[] getRawContentBytes() throws SOAPException
   {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      try
      {
         IOUtils.copyStream(bout, getDataHandler().getInputStream());
         return bout.toByteArray();
      }
      catch (IOException e)
      {
         throw new SOAPException(e);
      }
   }

   /**
    * Sets the content of this attachment part from the Base64 source InputStream
    * and sets the value of the Content-Type header to the value contained in contentType,
    * This method would first decode the base64 input and write the resulting raw bytes to the attachment.
    *
    * A subsequent call to getSize() may not be an exact measure of the content size.
    *
    * @param content the base64 encoded data to add to the attachment part
    * @param contentType the value to set into the Content-Type header
    * @throws SOAPException if an there is an error in setting the content
    * @throws NullPointerException if content is null
    * @since SAAJ 1.3
    */
   @Override
   public void setBase64Content(InputStream content, String contentType) throws SOAPException
   {
      if(null == content)
         throw new SOAPException("Content is null");

      try
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         IOUtils.copyStream(bout, content);
         content.close();
         byte[] bytes = bout.toByteArray();
         byte[] raw;

         try
         {
            // CTS passes contents that are not base64 encoded 
            raw = Base64.decode(bytes, 0, bytes.length);
         }
         catch (Throwable e)
         {
            throw new SOAPException(e);
         }

         dataHandler = new DataHandler(new ByteArrayDataSource(raw, contentType));
         setContentType(contentType);

      }
      catch (IOException e)
      {
         throw new SOAPException(e);
      }
   }

   /**
    * Sets the content of this attachment part to that contained by the InputStream content and sets the value of the Content-Type header to the value contained in contentType.
    *
    * A subsequent call to getSize() may not be an exact measure of the content size.
    *
    * @param content the raw data to add to the attachment part
    * @param contentType the value to set into the Content-Type header
    * @throws SOAPException if an there is an error in setting the content
    * @throws NullPointerException if content is null
    * @since SAAJ 1.3
    */
   @Override
   public void setRawContent(InputStream content, String contentType) throws SOAPException
   {
      if(null == content)
         throw new SOAPException("Content is null");
      
      dataHandler = new DataHandler(new ByteArrayDataSource(content, contentType));
      setContentType(contentType);
   }

   /**
    * Sets the content of this attachment part to that contained
    * by the byte[] array content and sets the value of the Content-Type
    * header to the value contained in contentType.
    *
    * @param content the raw data to add to the attachment part
    * @param contentType the value to set into the Content-Type header
    * @param offset the offset in the byte array of the content
    * @param len the number of bytes that form the content
    * @throws SOAPException if an there is an error in setting the content or content is null
    * @since SAAJ 1.3
    */
   @Override
   public void setRawContentBytes(byte[] content, int offset, int len, String contentType) throws SOAPException
   {
      setRawContent(new ByteArrayInputStream(content, offset, len), contentType);
   }

   class ByteArrayDataSource implements DataSource
   {
      private byte[] data;
      private String type;

      ByteArrayDataSource(InputStream is, String type) {
         this.type = type;
         try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            IOUtils.copyStream(os, is);            
            data = os.toByteArray();
         } catch (IOException ioex) {
            throw new WSException(ioex);
         }
      }

      ByteArrayDataSource(byte[] data, String type) {
         this.data = data;
         this.type = type;
      }

      ByteArrayDataSource(String data, String type) {
         try {
            this.data = data.getBytes("iso-8859-1");
         } catch (UnsupportedEncodingException uex) {
            throw new WSException(uex);
         }
         this.type = type;
      }

      public InputStream getInputStream() throws IOException {
         if (data == null)
            throw new IOException("no data");
         return new ByteArrayInputStream(data);
      }

      public OutputStream getOutputStream() throws IOException {
         throw new IOException("cannot do this");
      }

      public String getContentType() {
         return type;
      }

      public String getName() {
         return "ByteArrayDataSource";
      }

      public int getSize() {
         return this.data.length;
      }
   }
}
