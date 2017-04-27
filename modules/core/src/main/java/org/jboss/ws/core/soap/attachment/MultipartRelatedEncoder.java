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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPException;

import org.jboss.ws.core.soap.SOAPMessageImpl;

/**
 * MultipartRelatedEncoder encodes a SOAPMessage
 * into a multipart/related stream.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 17-Jan-2006
 */
public abstract class MultipartRelatedEncoder
{
   protected SOAPMessageImpl soapMessage;
   protected MimeMultipart multipart;

   public MultipartRelatedEncoder(SOAPMessageImpl soapMessage) throws SOAPException
   {
      this.soapMessage = soapMessage;
   }
   
   /** Build the multipart message */
   public abstract void encodeMultipartRelatedMessage() throws SOAPException, MessagingException;
   
   protected void addAttachmentParts(MimeMultipart multipart) throws SOAPException, MessagingException
   {
      Iterator attachmentParts = soapMessage.getAttachments();
      while (attachmentParts.hasNext())
      {
         AttachmentPart attachmentPart = (AttachmentPart)attachmentParts.next();
         DataHandler handler = attachmentPart.getDataHandler();
         MimeBodyPart mimePart = new MimeBodyPart();
         mimePart.setDataHandler(handler);

         Iterator mimeHeaders = attachmentPart.getAllMimeHeaders();
         while (mimeHeaders.hasNext())
         {
            MimeHeader soapHeader = (MimeHeader)mimeHeaders.next();
            mimePart.addHeader(soapHeader.getName(), soapHeader.getValue());
         }

         if (mimePart.getHeader(MimeConstants.CONTENT_TYPE) == null)
         {
            String type = handler.getContentType();
            mimePart.setHeader(MimeConstants.CONTENT_TYPE, (type != null) ? type : MimeConstants.TYPE_APPLICATION_OCTET_STREAM);
         }

         if (mimePart.getHeader(MimeConstants.CONTENT_ID) == null)
         {
            CIDGenerator cidGenerator = soapMessage.getCidGenerator();
            mimePart.setHeader(MimeConstants.CONTENT_ID, cidGenerator.generateFromCount());
         }

         // TODO - Binary encoding is the most efficient, however, some transports (old mail servers)
         // require 7 bit ascii. Can we ask the remoting layer about the transport's binary safety?
         mimePart.setHeader(MimeConstants.CONTENT_TRANSFER_ENCODING, MimeConstants.BINARY_ENCODING);

         multipart.addBodyPart(mimePart);
      }
   }
   
   /**
    * Returns the new content type of this encoder. This value must be specified
    * in the mime Content-type header in what ever way that the transport
    * requires it.
    *
    * @return the content type
    */
   public String getContentType()
   {
      String contentType = multipart.getContentType();
      return contentType;
   }

   /**
    * Writes this message to the specified output stream.
    *
    * @param os the stream to write this message
    */
   public void writeTo(OutputStream os) throws IOException
   {
      if (multipart == null)
         throw new IOException("No data to write because encoding failed on construction");

      try
      {
         // Ensure that the first boundary is always proceeded by CRLF
         os.write(13);
         os.write(10);
         multipart.writeTo(os);
      }
      catch (MessagingException e)
      {
         throw new IOException(e.getMessage());
      }
   }
}
