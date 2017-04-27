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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.soap.attachment.ContentHandlerRegistry;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.xb.binding.sunday.xop.XOPObject;
import org.jboss.xb.binding.sunday.xop.XOPUnmarshaller;

/**
 * The XOPUnmarshallerImpl allows callbacks from the binding layer towards the
 * soap processing components in order to optimize binary processing.
 *
 * @see XOPMarshallerImpl
 *
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since May 9, 2006
 */
public class XOPUnmarshallerImpl implements XOPUnmarshaller {

   private static final Logger log = Logger.getLogger(XOPUnmarshallerImpl.class);
   private static final QName XOP_INCLUDE = new QName(Constants.NS_XOP, "Include");

   static
   {
      // Load JAF content handlers
      ContentHandlerRegistry.register();
   }

   public boolean isXOPPackage()
   {
      return XOPContext.isXOPMessage();
   }

   public XOPObject getAttachmentAsDataHandler(String cid)
   {      
      try
      {
         AttachmentPart part = XOPContext.getAttachmentByCID(cid);
         DataHandler dataHandler = part.getDataHandler();
         String contentType = dataHandler.getContentType();

         // Wrapping the DataHandler shields XB from the JAF dependency
         XOPObject xopObject = new XOPObject(dataHandler);
         xopObject.setContentType(contentType);

         return xopObject;
      }
      catch(SOAPException e)
      {
         throw new WSException("Failed to access attachment part", e);
      }
   }

   public byte[] getAttachmentAsByteArray(String cid)
   {
      try
      {
         AttachmentPart part = XOPContext.getAttachmentByCID(cid);
         DataHandler dh = part.getDataHandler();
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         dh.writeTo(bout);

         return bout.toByteArray();
      }
      catch (SOAPException ex)
      {
         throw new WSException(ex);
      }
      catch(IOException e)
      {
         throw new WSException(e);
      }

   }
}
