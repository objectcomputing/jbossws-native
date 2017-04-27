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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.soap.attachment.ContentHandlerRegistry;
import org.jboss.ws.core.soap.attachment.SwapableMemoryDataSource;
import org.jboss.ws.core.utils.MimeUtils;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.wsf.common.IOUtils;
import org.jboss.xb.binding.sunday.marshalling.MarshallingContext;
import org.jboss.xb.binding.sunday.marshalling.TermBeforeMarshallingCallback;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBeforeSetParentCallback;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.UnmarshallingContext;

/**
 * Adopts raw binary contents to java types.
 * This class works in conjunction with the <code>XOPUnmarshallerImpl</code>
 * and <code>XOPMarshallerImpl</code>.
 *
 * @see XOPUnmarshallerImpl
 * @see XOPMarshallerImpl
 *
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Oct 19, 2006
 */
public class JBossXBContentAdapter implements TermBeforeMarshallingCallback, TermBeforeSetParentCallback {

   private static final Logger log = Logger.getLogger(JBossXBContentAdapter.class);
   private static final QName XMIME_BASE_64 = new QName(Constants.NS_XML_MIME, "base64Binary");
   private static final QName XOP_INCLUDE = new QName(Constants.NS_XOP, "Include");

   static
   {
      // Load JAF content handlers
      ContentHandlerRegistry.register();
   }

   /**
    * When XOP is disabled we need to convert java types to byte[]
    * before handing off to XB.
    */
   public Object beforeMarshalling(Object object, MarshallingContext marshallingContext) {

      boolean mtomDisabled = !XOPContext.isMTOMEnabled();
      boolean convertableType = object!=null && !(object instanceof byte[]);

      if( mtomDisabled && convertableType )
      {
         String contentType = MimeUtils.resolveMimeType(object);
         if(log.isDebugEnabled()) log.debug("Adopt " + object.getClass() + " to byte[], contentType " + contentType);

         DataHandler dh = new DataHandler(object, contentType);
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         try
         {
            IOUtils.copyStream(bout, dh.getInputStream());
            object = bout.toByteArray();
         }
         catch (IOException e)
         {
            throw new WSException("Failed to adopt XOP content type", e);
         }
      }

      return object;
   }

   /**
    * When XOP is disabled (inlined request) we receive a byte[] from XB
    * that needs to be converted in to java type
    */
   public Object beforeSetParent(Object object, UnmarshallingContext ctx) {

      if(null==object)
         return object;

      // may be null when it's actually an encoded request ?!
      Class targetClass = ctx.resolvePropertyType();

      if(null==targetClass) {
         throw new WSException("Failed to resolve target property type on "+ ctx.getParticle());
      }

      boolean isRegularMessage = !XOPContext.isXOPMessage();
      boolean isSimpleType = (object instanceof byte[]);
      boolean doTypesMatch = ( targetClass.equals(object.getClass()) );

      // Handle inlined requests.
      // In this case XB treats binaries as simple types that are unmarshalled to byte[]
      // Still type conversion will be necessary.
      if( isRegularMessage && isSimpleType && !doTypesMatch)
      {
         String contentType = MimeUtils.resolveMimeType(targetClass);
         if(log.isDebugEnabled()) log.debug("Adopt byte[] to " + targetClass +", contentType "+ contentType);

         try
         {
            DataHandler dh = new DataHandler(
                wrapAsDataSource(object, contentType)
            );

            if(targetClass.equals(DataHandler.class))
               object = dh;
            else
               object = dh.getContent();
         }
         catch (IOException e)
         {
            throw new WSException("Failed to adopt XOP content type", e);
         }
      }

      // Handle XOP encoded requests.
      // XB will use the XOPUnmarshaller callback and receive a DataHandler instance.
      // In this case we are be able to instantiate the correct content object
      // from the data handler, with the exception of content-type 'application/octet-stream'.
      // These attachments will be returned as DataHandler instances.
      else if(XOPContext.isXOPMessage() && (object instanceof DataHandler) && !doTypesMatch)
      {
         try
         {
            String contentType = MimeUtils.resolveMimeType(targetClass);
            if(log.isDebugEnabled()) log.debug("Adopt DataHandler to " + targetClass +", contentType "+ contentType);

            DataHandler dh = new DataHandler(
                wrapAsDataSource(object, contentType)
            );
            object = dh.getContent();

            // 'application/octet-stream' will return a byte[] instead fo the stream
            if(object instanceof InputStream)
            {
               ByteArrayOutputStream bout = new ByteArrayOutputStream();
               dh.writeTo(bout);
               object = bout.toByteArray();
            }
         }
         catch (IOException e)
         {
            throw new WSException("Failed to adopt XOP content type", e);
         }
      }

      return object;
   }

   private DataSource wrapAsDataSource(Object object, String contentType) throws IOException {

      DataSource ds;

      if(object instanceof byte[])
      {
         ds = new SwapableMemoryDataSource(new ByteArrayInputStream((byte[])object), contentType);
      }
      else if(object instanceof DataHandler)
      {
         ds = new SwapableMemoryDataSource(((DataHandler)object).getInputStream(), contentType);
      }
      else
      {
         throw new IllegalArgumentException("Failed to wrap as data source: "+object.getClass());
      }

      return ds;
   }

   /**
    * A factory method that registers the XB (un)marshalling adapters with schema binding.
    * These adapters convert java types into byte[] and reverse,
    * in order to match the jaxrpc-mapping declaration in case we receive or send an inlined request.
    */
   public static void register(SchemaBinding schemaBinding)
   {
      JBossXBContentAdapter contentAdapter = new JBossXBContentAdapter();

      // base64 simple types
      TypeBinding base64Type = schemaBinding.getType(org.jboss.xb.binding.Constants.QNAME_BASE64BINARY);
      base64Type.setBeforeMarshallingCallback( contentAdapter );
      base64Type.setBeforeSetParentCallback( contentAdapter );

      // xmime complex types
      TypeBinding xmimeBase64Type = schemaBinding.getType(XMIME_BASE_64);
      if(xmimeBase64Type!=null)
      {
         xmimeBase64Type.setBeforeMarshallingCallback( contentAdapter );
         xmimeBase64Type.setBeforeSetParentCallback( contentAdapter );

         // xop:Include
         /*ModelGroupBinding modelGroup = (ModelGroupBinding)xmimeBase64Type.getParticle().getTerm();
         ParticleBinding particle = (ParticleBinding)modelGroup.getParticles().iterator().next();
         ElementBinding xopInclude = (ElementBinding)particle.getTerm();

         if(! xopInclude.getQName().equals(XOP_INCLUDE))
            throw new WSException("Looks like the JBossXB XOP implementation has changed, please open a JIRA issue");

         xopInclude.setBeforeMarshallingCallback(contentAdapter);
         xopInclude.setBeforeSetParentCallback(contentAdapter);
                  */
      }
   }

}
