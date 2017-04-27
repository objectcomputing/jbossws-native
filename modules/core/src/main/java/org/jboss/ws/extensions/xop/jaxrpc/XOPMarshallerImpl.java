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
package org.jboss.ws.extensions.xop.jaxrpc;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.core.soap.attachment.ContentHandlerRegistry;
import org.jboss.ws.core.soap.attachment.MimeConstants;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.xb.binding.sunday.xop.XOPMarshaller;
import org.jboss.xb.binding.sunday.xop.XOPObject;

/**
 * The XOPUnmarshallerImpl allows callbacks from the binding layer towards the
 * soap processing components in order to optimize binary processing.
 *
 * @see XOPUnmarshallerImpl
 *
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since May 9, 2006
 */
public class XOPMarshallerImpl implements XOPMarshaller {

   private static final Logger log = Logger.getLogger(XOPMarshallerImpl.class);

   static
   {
      // Load JAF content handlers
      ContentHandlerRegistry.register();
   }

   public boolean isXOPPackage()
   {
      return XOPContext.isXOPMessage();
   }

   public String addMtomAttachment(XOPObject obj, String elementNamespace, String elementName)
   {

      CommonMessageContext msgContext = (CommonMessageContext)MessageContextAssociation.peekMessageContext();
      SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgContext.getSOAPMessage();

      QName xmlName = new QName(elementNamespace, elementName);
      if(log.isDebugEnabled()) log.debug("serialize: [xmlName=" + xmlName + "]");

      String cid = soapMessage.getCidGenerator().generateFromName(xmlName.getLocalPart());

      DataHandler dataHandler = XOPContext.createDataHandler(obj);
      AttachmentPart xopPart = soapMessage.createAttachmentPart(dataHandler);
      xopPart.addMimeHeader(MimeConstants.CONTENT_ID, '<'+cid+'>'); // RFC2392 requirement
      soapMessage.addAttachmentPart(xopPart);

      if(log.isDebugEnabled()) log.debug("Created attachment part " +cid+", with content-type " +xopPart.getContentType());

      return "cid:" + cid;

   }

   public String addMtomAttachment(byte[] data, String elementNamespace, String elementName)
   {
      /*
      TODO: this requires a java mail upgrade
      ByteArrayDataSource ds = new ByteArrayDataSource(data, MimeConstants.TYPE_APPLICATION_OCTET_STREAM);
      return addMtomAttachment(
        new DataHandler(
            ds, MimeConstants.TYPE_APPLICATION_OCTET_STREAM),
            elementNamespace, elementName
      );*/

      throw new NotImplementedException("Not implemented yet");
   }

   public String addSwaRefAttachment(Object obj)
   {
      throw new NotImplementedException();
   }


}
