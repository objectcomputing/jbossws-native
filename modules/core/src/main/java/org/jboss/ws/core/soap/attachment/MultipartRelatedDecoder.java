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
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.activation.DataHandler;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.ParseException;
import javax.xml.soap.AttachmentPart;

import org.jboss.ws.WSException;

/**
 * Abstract MutilPartRelatedDecoder decodes a mime multipart/related stream.
 *
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 * @author Thomas.Diesler@jboss.org
 * @since 18-Jan-2006
 */
public class MultipartRelatedDecoder
{
   private ContentType contentType;

   private String rootType;

   private AttachmentPartImpl rootPart;

   private LinkedList<AttachmentPart> relatedParts = new LinkedList<AttachmentPart>();

   /**
    * Constructs a <code>MultipartRelatedDecoder</code>. This will block until the message
    * has been decoded.
    *
    * @param contentType the mime Content-Type header provided by the transport
    * @param stream The stream to pull the multipart message from
    */
   public MultipartRelatedDecoder(ContentType contentType) throws IOException, MessagingException
   {
      this.contentType = contentType;
      if (MimeConstants.TYPE_MULTIPART_RELATED.equalsIgnoreCase(contentType.getBaseType()) == false)
         throw new IllegalArgumentException("Multipart related decoder called with a non-multipart/related type");

      rootType = contentType.getParameter("type");
      if (rootType == null)
         throw new IllegalArgumentException("multipart/related type is invalid, it is missing the root type parameter");
   }

   private boolean isValidRootType(String type) throws ParseException
   {
      // The type multipart/related parameter can not have parameters itself
      ContentType contentType = new ContentType(type);
      type = contentType.getBaseType();

      return rootType.equals(type);
   }

   public void decodeMultipartRelatedMessage(InputStream stream) throws IOException, MessagingException
   {
      String boundaryParameter = contentType.getParameter("boundary");
      String start = contentType.getParameter("start");
      byte[] boundary;
      byte[] crlf;

      if (boundaryParameter == null)
         throw new IllegalArgumentException("multipart/related content type did not contain a boundary");

      try
      {         
         // [JBWS-1393] - Problem interpreting messages with attachment when confronted with no <start> header
         if (start == null)
            boundary = ("--" + boundaryParameter).getBytes("US-ASCII");
         else 
            boundary = ("\r\n--" + boundaryParameter).getBytes("US-ASCII");
            
            crlf = ("\r\n").getBytes("US-ASCII");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new WSException("US-ASCII not supported, this should never happen");
      }
      
      // [JBWS-1620] - Incorrect handling of MIME boundaries in MultipartRelatedDecoder
      PushbackInputStream pushBackStream = new PushbackInputStream(stream,2);
      pushBackStream.unread(crlf);

      BoundaryDelimitedInputStream delimitedStream = new BoundaryDelimitedInputStream(pushBackStream, boundary);

      // Eat first inner stream since its empty
      byte[] buffer = new byte[256];
      while (delimitedStream.read(buffer) != -1)
      {
      }

      while (!delimitedStream.isOuterStreamClosed())
      {
         // If the stream is empty or an end marker is reached, skip to the next
         // one
         if (!advanceToHeaders(delimitedStream))
            continue;

         InternetHeaders headers = new InternetHeaders(delimitedStream);
         String typeHeader[] = headers.getHeader(MimeConstants.CONTENT_TYPE);

         if (typeHeader == null)
            throw new IllegalArgumentException("multipart/related stream invalid, component Content-type missing.");

         SwapableMemoryDataSource source = new SwapableMemoryDataSource(delimitedStream, typeHeader[0]);
         AttachmentPartImpl part = new AttachmentPartImpl(new DataHandler(source));

         Enumeration enumeration = headers.getAllHeaders();

         while (enumeration.hasMoreElements())
         {
            Header header = (Header)enumeration.nextElement();
            part.addMimeHeader(header.getName(), header.getValue());
         }

         // The root part is either the one pointed to by the start parameter, or
         // the first occuring part if start is not defined.

         /*
         if ( start != null && part.getContentId().startsWith("<")) {
            if ( !(start.charAt(0)=='<')) {
               start = "<"+start+">";
            }
         }
         */

         if (rootPart == null && (start == null || start.equals(part.getContentId())))
         {
            if (isValidRootType(part.getContentType()) == false)
               throw new IllegalArgumentException("multipart/related type specified a root type other than the one" + " that was found.");

            rootPart = part;
         }
         else
         {
            relatedParts.add(part);
         }
      }
      if (rootPart == null)
         throw new IllegalArgumentException("multipart/related stream invalid, no root part was found");
   }

   private boolean advanceToHeaders(InputStream stream) throws IOException
   {
      boolean dash = false, cr = false;
      while (true)
      {
         int b = stream.read();

         switch (b)
         {
            case -1:
               return false;
            case '\r':
               cr = true;
               dash = false;
               break;
            case '-':
               if (dash == true)
               {
                  // Two dashes indicate no further content
                  stream.close();
                  return false;
               }
               dash = true;
               cr = false;
               break;
            case '\n':
               if (cr == true)
                  return true;
               dash = false;
               break;
            default:
               dash = false;
               cr = false;
         }
      }
   }

   /**
    * Returns an <code>AttachmentPart</code> representing the root part of this multipart/related message.
    *
    * @return the root part of this multipart/related message
    */
   public AttachmentPart getRootPart()
   {
      return rootPart;
   }

   /**
    * Returns a collection of <code>AttachmentPart</code> objects that represent the attachments on this message.
    * If there are no attachments, an empty collection is returned.
    */
   public Collection<AttachmentPart> getRelatedParts()
   {
      return relatedParts;
   }
}
