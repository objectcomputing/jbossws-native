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
package org.jboss.ws.core.soap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.SOAPMessageAbstraction;
import org.jboss.ws.core.soap.attachment.AttachmentPartImpl;
import org.jboss.ws.core.soap.attachment.CIDGenerator;
import org.jboss.ws.core.soap.attachment.MimeConstants;
import org.jboss.ws.core.soap.attachment.MultipartRelatedEncoder;
import org.jboss.ws.core.soap.attachment.MultipartRelatedSwAEncoder;
import org.jboss.ws.core.soap.attachment.MultipartRelatedXOPEncoder;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;

/**
 * The root class for all SOAP messages. As transmitted on the "wire", a SOAP message is an XML document or a
 * MIME message whose first body part is an XML/SOAP document.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 */
public class SOAPMessageImpl extends SOAPMessage implements SOAPMessageAbstraction
{
   private static Logger log = Logger.getLogger(SOAPMessageImpl.class);
   
   private boolean saveRequired = true;
   private MimeHeaders mimeHeaders = new MimeHeaders();
   private List<AttachmentPart> attachments = new LinkedList<AttachmentPart>();
   private CIDGenerator cidGenerator = new CIDGenerator();
   private boolean isXOPMessage;
   private boolean isSWARefMessage;
   private SOAPPartImpl soapPart;   
   private MultipartRelatedEncoder multipartRelatedEncoder;
   private static final boolean writeXMLDeclaration = Boolean.getBoolean(WRITE_XML_DECLARATION);

   // Cache the associated operation meta data
   private OperationMetaData opMetaData;

   SOAPMessageImpl() throws SOAPException
   {
      soapPart = new SOAPPartImpl(this);
      setProperty(CHARACTER_SET_ENCODING, "UTF-8");
      setProperty(WRITE_XML_DECLARATION, writeXMLDeclaration);
   }

   public CIDGenerator getCidGenerator()
   {
      return cidGenerator;
   }

   public boolean isXOPMessage()
   {
      return isXOPMessage;
   }

   public void setXOPMessage(boolean isXOPMessage)
   {
      this.isXOPMessage = isXOPMessage;
   }

   public boolean isSWARefMessage()
   {
      return isSWARefMessage;
   }

   public void setSWARefMessage(boolean isSWAMessage)
   {
      this.isSWARefMessage = isSWAMessage;
   }

   public void setAttachments(Collection<AttachmentPart> parts) throws SOAPException
   {
      for (AttachmentPart part : parts)
      {
         attachments.add(part);
      }
      saveRequired = true;
   }

   public void addAttachmentPart(AttachmentPart part)
   {
      if (part == null)
         return;

      attachments.add(part);
      saveRequired = true;
   }

   public AttachmentPart getAttachmentByContentId(String cid) throws SOAPException
   {
      String cidDecoded = decode(cid);
      
      for (AttachmentPart part : attachments)
      {
         String contentIdDecoded = decode(part.getContentId());
         if (contentIdDecoded.equals(cidDecoded))
            return part;
      }

      return null;
   }
   
   public String decode(String cid)
   {
      String cidDecoded = cid;
      
      try
      {
         cidDecoded = URLDecoder.decode(cid, "UTF-8");
      }
      catch (UnsupportedEncodingException ex)
      {
         log.error("Cannot decode name for cid: " + ex);
      }
      
      return cidDecoded;
   }

   public AttachmentPart removeAttachmentByContentId(String cid)
   {
      try
      {
         AttachmentPart part = getAttachmentByContentId(cid);
         if (part != null)
         {
            attachments.remove(part);
            return part;
         }
      }
      catch (SOAPException ex)
      {
         // this used to not throw SOAPException
         log.error("Ignore SOAPException: " + ex);
      }

      return null;
   }

   public AttachmentPart getAttachmentByPartName(String partName)
   {
      for (AttachmentPart part : attachments)
      {
         String contentId = part.getContentId();
         if (contentId.startsWith("<" + partName + "="))
            return part;
      }
      return null;
   }

   public AttachmentPart createAttachmentPart()
   {
      return new AttachmentPartImpl();
   }

   public String getContentDescription()
   {
      String[] value = mimeHeaders.getHeader(MimeConstants.CONTENT_DESCRIPTION);

      return (value == null) ? null : value[0];
   }

   public void setContentDescription(String description)
   {
      mimeHeaders.setHeader(MimeConstants.CONTENT_DESCRIPTION, description);
   }

   public MimeHeaders getMimeHeaders()
   {
      return mimeHeaders;
   }

   public void setMimeHeaders(MimeHeaders headers)
   {
      if (headers == null)
         throw new IllegalArgumentException("MimeHeaders cannot be null");
      this.mimeHeaders = headers;
   }

   public SOAPPart getSOAPPart()
   {
      return soapPart;
   }

   public void removeAllAttachments()
   {
      attachments.clear();
      saveRequired = true;
   }

   public int countAttachments()
   {
      return attachments.size();
   }

   public Iterator getAttachments()
   {
      // Someone could call remove on this iterator, affecting the attachment count
      saveRequired = true;
      return attachments.iterator();
   }

   public Iterator getAttachments(MimeHeaders headers)
   {
      if (headers == null)
         throw new WSException("MimeHeaders can not be null");

      return new MimeMatchingAttachmentsIterator(headers, attachments);
   }
   
   private String getSOAPContentType() throws SOAPException
   {
      //Check binding type in the endpoint metadata
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext != null && Constants.SOAP12HTTP_BINDING.equalsIgnoreCase(msgContext.getEndpointMetaData().getBindingId()))
      {
         return SOAPConstants.SOAP_1_2_CONTENT_TYPE;
      }
      //Check the message envelope
      SOAPEnvelope env = soapPart != null ? soapPart.getEnvelope() : null;
      if (env != null && SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE.equals(env.getNamespaceURI()))
      {
         return SOAPConstants.SOAP_1_2_CONTENT_TYPE;
      }
      //Default to soap 1.1
      return SOAPConstants.SOAP_1_1_CONTENT_TYPE;
   }
   
   public void saveChanges() throws SOAPException
   {
      if (saveRequired == true)
      {
         try
         {
            boolean hasAttachments = attachments.size() > 0;

            if (isXOPMessage() && !XOPContext.isMTOMEnabled() && hasAttachments)
               throw new IllegalStateException("XOP parameter not properly inlined");

            // default content-type
            String contentType = getSOAPContentType() + "; charset=" + getCharSetEncoding();

            if (hasAttachments)
            {
               if (isXOPMessage() && XOPContext.isMTOMEnabled())
               {
                  multipartRelatedEncoder = new MultipartRelatedXOPEncoder(this);
                  multipartRelatedEncoder.encodeMultipartRelatedMessage();
                  contentType = multipartRelatedEncoder.getContentType();
               }
               else
               {
                  multipartRelatedEncoder = new MultipartRelatedSwAEncoder(this);
                  multipartRelatedEncoder.encodeMultipartRelatedMessage();
                  contentType = multipartRelatedEncoder.getContentType();
               }
            }

            mimeHeaders.setHeader(MimeConstants.CONTENT_TYPE, contentType);
         }
         catch (MessagingException ex)
         {
            throw new SOAPException(ex);
         }

         /*
          * We are lazily encoding our message, which means that currently
          * Content-Length is not being calculated. This should not be a problem
          * because HTTP/1.1 does not require that it be sent (WS Basic Profile 1.1
          * states that implementations SHOULD send HTTP 1.1). If it is determined
          * that it must be sent, this perhaps should be done by the transport
          * layer. However, there could be a space optimization where length is
          * precalulated per attachment, and that calculation would, of course,
          * belong here.
          */

         saveRequired = false;
      }

      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if(msgContext!=null) msgContext.setModified(true);
   }

   public boolean saveRequired()
   {
      return saveRequired;
   }

   public void writeTo(OutputStream outs) throws IOException
   {
      try
      {
         // Save all changes
         saveChanges();

         // If there are attachments then we delegate encoding to MultipartRelatedEncoder
         if (attachments.size() > 0)
         {
            multipartRelatedEncoder.writeTo(outs);
         }
         else
         {
            SOAPEnvelope soapEnv = getSOAPPart().getEnvelope();
            if (soapEnv != null)
            {
               boolean writeXML = isWriteXMLDeclaration();
               String charsetEncoding = getCharSetEncoding();
               SOAPElementWriter writer = new SOAPElementWriter(outs, charsetEncoding);
               writer.setWriteXMLDeclaration(writeXML).writeElement((SOAPEnvelopeImpl)soapEnv);
            }
         }
      }
      catch (SOAPException ex)
      {
         WSException.rethrow(ex);
      }
   }

   private String getCharSetEncoding() throws SOAPException
   {
      String charsetName = (String)getProperty(CHARACTER_SET_ENCODING);
      if (charsetName == null)
         charsetName = "UTF-8";
      return charsetName;
   }

   /** Get the operation meta data for this SOAP message
    */
   public OperationMetaData getOperationMetaData(EndpointMetaData epMetaData) throws SOAPException
   {
      if (opMetaData == null)
      {
         SOAPMessageDispatcher dispatcher = new SOAPMessageDispatcher();
         opMetaData = dispatcher.getDispatchDestination(epMetaData, this);
      }
      return opMetaData;
   }

   public boolean isFaultMessage()
   {
      SOAPFault soapFault = null;
      try
      {
         soapFault = getSOAPBody().getFault();
      }
      catch (Exception ignore)
      {
      }
      return soapFault != null;
   }

   private boolean isWriteXMLDeclaration() throws SOAPException
   {
      Boolean booleanValue = new Boolean(false);
      Object propValue = getProperty(WRITE_XML_DECLARATION);
      if (propValue instanceof Boolean)
         booleanValue = (Boolean)propValue;
      if (propValue instanceof String)
         booleanValue = new Boolean((String)propValue);
      return booleanValue.booleanValue();
   }

   public static class MimeMatchingAttachmentsIterator implements Iterator
   {
      private Iterator iterator;

      private MimeHeaders headers = new MimeHeaders();

      private AttachmentPart lastMatch;

      public MimeMatchingAttachmentsIterator(MimeHeaders headers, List attachments)
      {
         iterator = attachments.iterator();

         if (headers != null)
            this.headers = headers;
      }

      private boolean containsAllHeaders(Iterator headerIterator, AttachmentPart part)
      {
         while (headerIterator.hasNext())
         {
            MimeHeader header = (MimeHeader)headerIterator.next();
            String[] values = part.getMimeHeader(header.getName());
            if (values == null)
               return false;

            boolean match = false;
            for (int j = 0; j < values.length; j++)
            {
               if (values[j].equalsIgnoreCase(header.getValue()))
               {
                  match = true;
                  break;
               }
            }

            if (!match)
               return false;
         }

         return true;
      }

      private void nextMatch()
      {
         while (iterator.hasNext())
         {
            AttachmentPart part = (AttachmentPart)iterator.next();
            if (containsAllHeaders(headers.getAllHeaders(), part))
            {
               lastMatch = part;
               break;
            }
         }
      }

      public boolean hasNext()
      {
         if (lastMatch == null)
            nextMatch();

         return lastMatch != null;
      }

      public Object next()
      {
         if (!hasNext())
            return null;

         Object retval = lastMatch;
         lastMatch = null;

         return retval;
      }

      public void remove()
      {
         iterator.remove();
      }
   }

   @Override
   public AttachmentPart getAttachment(SOAPElement element) throws SOAPException
   {
      String ref = element.getAttribute("href");

      if (ref.length() == 0)
      {
         ref = element.getValue();
         if (ref == null || ref.length() == 0)
            return null;
      }

      return getAttachmentByRef(ref);
   }

   private AttachmentPart getAttachmentByRef(String ref) throws SOAPException
   {
      AttachmentPart attachment;
      if (ref.startsWith("cid:"))
      {
         /* SwA 2000-12-11 section 3: if a Content-ID header is present, then an absolute URI 
          * label for the part is formed using the CID URI scheme
          * 
          * WS-I AP 1.0 section 3.5: when using the CID URI scheme, the syntax and rules defined
          * in RFC 2392 apply.
          */
         String cid = '<' + ref.substring("cid:".length()) + '>';
         attachment = getAttachmentByContentId(cid);
      }
      else
      {
         /* SwA 2000-12-11 section 3: If a Content-Location header is present with an absolute URI
          * value then that URI is a label for the part
          */
         attachment = getAttachmentByContentLocation(ref);
      }

      if (attachment == null)
      {
         // autogenerated CID based on part name
         attachment = getAttachmentByPartName(ref);
      }

      return attachment;
   }

   /**
    * Looks for the first {@linkplain AttachmentPart attachment} where the
    * {@linkplain AttachmentPart#getContentLocation() content location} matches the
    * given <code>location</code>.
    * @param location the content location to match
    * @return the matching attachment or <code>null</code> if no match was found
    */
   private AttachmentPart getAttachmentByContentLocation(String location)
   {
      for (AttachmentPart attachment : attachments)
      {
         if (location.equals(attachment.getContentLocation()))
            return attachment;
      }
      return null;
   }

   @Override
   public void removeAttachments(MimeHeaders headers)
   {
      /* this code exploits the fact that MimeMatchingAttachmentsIterator.next() returns null
       * rather than throwing an exception when there are no more elements 
       */
      Iterator attachmentItr = new MimeMatchingAttachmentsIterator(headers, attachments);
      while (attachmentItr.next() != null)
         attachmentItr.remove();
   }
}
