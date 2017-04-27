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
package org.jboss.ws.extensions.xop.jaxws;

import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;

import org.jboss.logging.Logger;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.core.soap.attachment.ContentHandlerRegistry;
import org.jboss.ws.core.soap.attachment.MimeConstants;
import org.jboss.ws.extensions.xop.XOPContext;

/**
 * Enable JAXB marshalling to optimize storage of binary data.<br>
 * This API enables an efficient cooperative creation of optimized binary data formats between a JAXB marshalling process
 * and a MIME-based package processor. A JAXB implementation marshals the root body of a MIME-based package,
 * delegating the creation of referenceable MIME parts to the MIME-based package processor
 * that implements this abstraction.<p>
 * XOP processing is enabled when <code>isXOPPackage()</code> is true.
 * See <code>addMtomAttachment(DataHandler, String, String)</code> for details.
 * <p>
 * WS-I Attachment Profile 1.0 is supported by <code>addSwaRefAttachment(DataHandler)</code>
 * being called by the marshaller for each JAXB property related to {http://ws-i.org/profiles/basic/1.1/xsd}swaRef.
 *
 * @author <a href="heiko.braun@jboss.com">Heiko Braun</a>
 */
public class AttachmentMarshallerImpl extends AttachmentMarshaller
{
   // provide logging
   private static final Logger log = Logger.getLogger(AttachmentMarshallerImpl.class);

   static
   {
      // Load JAF content handlers
      ContentHandlerRegistry.register();
   }

   public AttachmentMarshallerImpl()
   {
      super();
   }

   /**
    * @param data - represents the data to be attached. Must be non-null.
    * @param elementNamespace - the namespace URI of the element that encloses the base64Binary data. Can be empty but never null.
    * @param elementLocalName - The local name of the element. Always a non-null valid string.
    *
    * @return content-id URI, cid, to the attachment containing data or null if data should be inlined.
    */
   public String addMtomAttachment(DataHandler data, String elementNamespace, String elementLocalName)
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgContext.getSOAPMessage();

      QName xmlName = new QName(elementNamespace, elementLocalName);
      if(log.isDebugEnabled()) log.debug("serialize: [xmlName=" + xmlName + "]");

      String cid = soapMessage.getCidGenerator().generateFromName(xmlName.getLocalPart());
      AttachmentPart xopPart = soapMessage.createAttachmentPart(data);
      xopPart.addMimeHeader(MimeConstants.CONTENT_ID, '<' + cid + '>'); // RFC2392 requirement
      soapMessage.addAttachmentPart(xopPart);

      if(log.isDebugEnabled()) log.debug("Created attachment part " + cid + ", with content-type " + xopPart.getContentType());

      return "cid:" + cid;
   }

   /**
    * @param data - represents the data to be attached. Must be non-null. The actual data region is specified by (data,offset,length) tuple.
    * @param offset - The offset within the array of the first byte to be read; must be non-negative and no larger than array.length
    * @param length - The number of bytes to be read from the given array; must be non-negative and no larger than array.length
    * @param mimeType - If the data has an associated MIME type known to JAXB, that is passed as this parameter. If none is known, "application/octet-stream". This parameter may never be null.
    * @param elementNamespace - the namespace URI of the element that encloses the base64Binary data. Can be empty but never null.
    * @param elementLocalName - The local name of the element. Always a non-null valid string.
    *
    * @return content-id URI, cid, to the attachment containing data or null if data should be inlined.
    */
   public String addMtomAttachment(byte[] data, int offset, int length,
                                   String mimeType, String elementNamespace, String elementLocalName)
   {

      if(true)
         mimeType = null; // ignore the mime type. otherwise the content handlers will fail

      String contentType = mimeType != null ? mimeType : "application/octet-stream";
      DataHandler dh = new DataHandler(data, contentType);
      return addMtomAttachment(dh, elementNamespace, elementLocalName);
   }

   public String addSwaRefAttachment(DataHandler dataHandler)
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgContext.getSOAPMessage();

      String cid = soapMessage.getCidGenerator().generateFromCount();
      AttachmentPart swaRefPart = soapMessage.createAttachmentPart(dataHandler);
      swaRefPart.addMimeHeader(MimeConstants.CONTENT_ID, '<' + cid + '>'); // RFC2392 requirement
      soapMessage.addAttachmentPart(swaRefPart);

      if(log.isDebugEnabled()) log.debug("Created attachment part " + cid + ", with content-type " + swaRefPart.getContentType());

      return "cid:" + cid;
   }

   public boolean isXOPPackage()
   {
      return XOPContext.isXOPMessage();
   }
}
