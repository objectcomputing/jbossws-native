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

import org.jboss.ws.WSException;
import org.jboss.ws.core.soap.attachment.ContentHandlerRegistry;
import org.jboss.ws.extensions.xop.XOPContext;

import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * <p>Enables JAXB unmarshalling of a root document containing optimized binary data formats.</p>
 *
 * <p>This API enables an efficient cooperative processing of optimized
 * binary data formats between a JAXB 2.0 implementation and MIME-based package
 * processor (MTOM/XOP and WS-I AP 1.0). JAXB unmarshals the body of a package, delegating the
 * understanding of the packaging format being used to a MIME-based
 * package processor that implements this abstract class.</p>
 *
 * <p>This abstract class identifies if a package requires XOP processing, <a href="../../../../javax/xml/bind/attachment/AttachmentUnmarshaller.html#isXOPPackage%28%29"><code>isXOPPackage()</code></a> and provides retrieval of binary content stored as attachments by content-id.</p>
 *
 * <h2>Identifying the content-id, cid, to pass to <code>getAttachment*(String cid)</code></h2>
 * <ul>
 * <li>
 * For XOP processing, the infoset representation of the cid is described in step 2a in
 * <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/#interpreting_xop_packages">Section 3.2 Interpreting XOP Packages</a>
 * </li>
 *
 * <li>
 * For WS-I AP 1.0, the cid is identified as an element or attribute of type <code>ref:swaRef </code> specified in
 * <a href="http://www.ws-i.org/Profiles/AttachmentsProfile-1.0-2004-08-24.html#Referencing_Attachments_from_the_SOAP_Envelope">Section 4.4 Referencing Attachments from the SOAP Envelope</a>
 * </li>
 * </ul>
 * <p>
 */
public class AttachmentUnmarshallerImpl extends AttachmentUnmarshaller
{

   static
   {
      // Load JAF content handlers
      ContentHandlerRegistry.register();
   }

   public AttachmentUnmarshallerImpl()
   {
      super();
   }

   public boolean isXOPPackage()
   {
      return XOPContext.isXOPMessage();           
   }

   public DataHandler getAttachmentAsDataHandler(String cid)
   {
      try
      {
         AttachmentPart part = XOPContext.getAttachmentByCID(cid);
         return part.getDataHandler();
      }
      catch (SOAPException e)
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
      catch (IOException e)
      {
         throw new WSException(e);
      }
   }
}
