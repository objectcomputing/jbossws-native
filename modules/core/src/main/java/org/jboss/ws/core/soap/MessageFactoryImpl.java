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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service.Mode;

import org.jboss.logging.Logger;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.attachment.MimeConstants;
import org.jboss.ws.core.soap.attachment.MultipartRelatedDecoder;
import org.jboss.ws.feature.FastInfosetFeature;
import org.jboss.ws.metadata.umdm.FeatureSet;
import org.jboss.wsf.common.IOUtils;
import org.jboss.wsf.spi.util.ServiceLoader;

/**
 * MessageFactory implementation
 * 
 * @author Thomas.Diesler@jboss.org
 */
public class MessageFactoryImpl extends MessageFactory
{
   private static Logger log = Logger.getLogger(MessageFactoryImpl.class);

   // The envelope namespace used by the MessageFactory
   private String envNamespace;

   // The JAXWS ServiceMode
   private Mode serviceMode;
   // The style used by this MessageFactory
   private Style style;
   // The features used by this MessageFactory
   private FeatureSet features = new FeatureSet();
   // Used if the style is dynamic
   private boolean dynamic;

   public MessageFactoryImpl()
   {
      envNamespace = SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE;
   }

   public MessageFactoryImpl(String protocol) throws SOAPException
   {
      if (SOAPConstants.SOAP_1_1_PROTOCOL.equals(protocol) || SOAPConstants.DEFAULT_SOAP_PROTOCOL.equals(protocol))
         envNamespace = SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE;
      else if (SOAPConstants.SOAP_1_2_PROTOCOL.equals(protocol))
         envNamespace = SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE;
      else if (SOAPConstants.DYNAMIC_SOAP_PROTOCOL.equals(protocol))
         dynamic = true;
      else
         throw new SOAPException("Unknown protocol: " + protocol);
   }

   /**
    * Get the SOAP envelope URI this factory will use when creating envelopes.
    */
   public String getEnvNamespace()
   {
      return envNamespace;
   }

   /**
    * Set the SOAP envelope URI this factory will use when creating envelopes.
    */
   public void setEnvNamespace(String envelopeURI)
   {
      this.envNamespace = envelopeURI;
   }

   /**
    * Get the Style this message factory will use
    */
   public Style getStyle()
   {
      if (style == null)
      {
         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         if (msgContext != null && msgContext.getOperationMetaData() != null)
         {
            style = msgContext.getOperationMetaData().getStyle();
         }
         log.trace("Using style: " + style);
      }
      return style;
   }

   public void setStyle(Style style)
   {
      this.style = style;
   }

   public Mode getServiceMode()
   {
      return serviceMode;
   }

   public void setServiceMode(Mode serviceMode)
   {
      this.serviceMode = serviceMode;
   }

   public void addFeature(WebServiceFeature feature)
   {
      this.features.addFeature(feature);
   }
   
   public void setFeatures(FeatureSet features)
   {
      this.features = features;
   }
   
   /**
    * Creates a new SOAPMessage object with the default SOAPPart, SOAPEnvelope,
    * SOAPBody, and SOAPHeader objects. Profile-specific message factories can
    * choose to prepopulate the SOAPMessage object with profile-specific
    * headers.
    * 
    * Content can be added to this message's SOAPPart object, and the message
    * can be sent "as is" when a message containing only a SOAP part is
    * sufficient. Otherwise, the SOAPMessage object needs to create one or more
    * AttachmentPart objects and add them to itself. Any content that is not in
    * XML format must be in an AttachmentPart object.
    * 
    * @return a new SOAPMessage object
    * @throws javax.xml.soap.SOAPException
    *             if a SOAP error occurs
    */
   public SOAPMessage createMessage() throws SOAPException
   {
      if (dynamic)
         throw new UnsupportedOperationException("Cannot create default message when protocol is dynamic");

      SOAPMessageImpl soapMessage = new SOAPMessageImpl();
      SOAPPartImpl soapPart = (SOAPPartImpl)soapMessage.getSOAPPart();
      new SOAPEnvelopeImpl(soapPart, envNamespace, true);
      return soapMessage;
   }

   /**
    * Internalizes the contents of the given InputStream object into a new
    * SOAPMessage object and returns the SOAPMessage object.
    * 
    * @param mimeHeaders
    *            the transport-specific headers passed to the message in a
    *            transport-independent fashion for creation of the message
    * @param ins
    *            the InputStream object that contains the data for a message
    * @return a new SOAPMessage object containing the data from the given
    *         InputStream object
    * @throws java.io.IOException
    *             if there is a problem in reading data from the input stream
    * @throws javax.xml.soap.SOAPException
    *             if the message is invalid
    */
   public SOAPMessage createMessage(MimeHeaders mimeHeaders, InputStream ins) throws IOException, SOAPException
   {
      return createMessage(mimeHeaders, ins, false);
   }

   public SOAPMessage createMessage(MimeHeaders mimeHeaders, InputStream inputStream, boolean ignoreParseError) throws IOException, SOAPException
   {
      if (mimeHeaders == null)
      {
         mimeHeaders = new MimeHeaders();
      }
      else if (log.isTraceEnabled())
      {
         Iterator<MimeHeader> itMimeHeaders = mimeHeaders.getAllHeaders();
         while (itMimeHeaders.hasNext())
         {
            MimeHeader mh = itMimeHeaders.next();
            log.trace(mh);
         }
      }

      ContentType contentType = getContentType(mimeHeaders);
      log.debug("createMessage: [contentType=" + contentType + "]");

      SOAPMessageImpl soapMessage = new SOAPMessageImpl();
      String encoding = contentType.getParameterList().get("charset");
      if (encoding != null)
      {
         soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, encoding);
      }
      if (inputStream != null)
      {
         // Debug the incoming message
         if (log.isTraceEnabled())
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            IOUtils.copyStream(baos, inputStream);
            byte[] bytes = baos.toByteArray();

            log.trace("createMessage\n" + new String(bytes));
            inputStream = new ByteArrayInputStream(bytes);
         }

         Collection<AttachmentPart> attachments = null;
         if (isMultipartRelatedContent(contentType))
         {
            MultipartRelatedDecoder decoder;
            try
            {
               decoder = new MultipartRelatedDecoder(contentType);
               decoder.decodeMultipartRelatedMessage(inputStream);
            }
            catch (RuntimeException rte)
            {
               throw rte;
            }
            catch (IOException ex)
            {
               throw ex;
            }
            catch (Exception ex)
            {
               throw new SOAPException("Cannot decode multipart related message", ex);
            }

            inputStream = decoder.getRootPart().getDataHandler().getInputStream();
            attachments = decoder.getRelatedParts();
            if (isXOPContent(contentType))
            {
               soapMessage.setXOPMessage(true);
            }
         }
         else if (isFastInfosetContent(contentType))
         {
            if (!features.isFeatureEnabled(FastInfosetFeature.class))
            {
               throw new SOAPException("FastInfoset support is not enabled, use FastInfosetFeature to enable it.");
            }
         }
         else if (isSoapContent(contentType) == false)
         {
            throw new SOAPException("Unsupported content type: " + contentType);
         }

         if (mimeHeaders != null)
            soapMessage.setMimeHeaders(mimeHeaders);

         if (attachments != null)
            soapMessage.setAttachments(attachments);

         // Get the SOAPEnvelope builder
         EnvelopeBuilder envBuilder;
         if (features.isFeatureEnabled(FastInfosetFeature.class))
         {
            envBuilder = new FastInfosetEnvelopeBuilder();
         }
         else
         {
            envBuilder = (EnvelopeBuilder)ServiceLoader.loadService(EnvelopeBuilder.class.getName(), null);
         }

         // Build the payload
         envBuilder.setStyle(getStyle());
         envBuilder.build(soapMessage, inputStream, ignoreParseError);
      }

      return soapMessage;
   }

   private static ContentType getContentType(MimeHeaders headers) throws SOAPException
   {
      ContentType contentType = null;
      try
      {
         String[] type = headers.getHeader(MimeConstants.CONTENT_TYPE);
         if (type != null)
         {
            contentType = new ContentType(type[0]);
         }
         else
         {
            contentType = new ContentType(MimeConstants.TYPE_SOAP11);
         }
         return contentType;
      }
      catch (ParseException e)
      {
         throw new SOAPException("Could not parse content type:" + e);
      }
   }

   private boolean isSoapContent(ContentType type)
   {
      String baseType = type.getBaseType();
      return MimeConstants.TYPE_SOAP11.equalsIgnoreCase(baseType) || MimeConstants.TYPE_SOAP12.equalsIgnoreCase(baseType);
   }
   
   private boolean isFastInfosetContent(ContentType type)
   {
      String baseType = type.getBaseType();
      return MimeConstants.TYPE_FASTINFOSET.equalsIgnoreCase(baseType);
   }

   private boolean isMultipartRelatedContent(ContentType type)
   {
      String baseType = type.getBaseType();
      return MimeConstants.TYPE_MULTIPART_RELATED.equalsIgnoreCase(baseType);
   }
   
   private boolean isXOPContent(ContentType type)
   {      
      String paramType = type.getParameter("type");
      return MimeConstants.TYPE_APPLICATION_XOP_XML.endsWith(paramType);
   }
}
