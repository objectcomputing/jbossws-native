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
package org.jboss.ws.core.client;

import java.util.Map;

import javax.xml.soap.MimeHeaders;

import org.jboss.remoting.marshal.Marshaller;
import org.jboss.remoting.marshal.UnMarshaller;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.soap.FastInfosetMarshaller;
import org.jboss.ws.core.soap.FastInfosetUnMarshaller;
import org.jboss.ws.core.soap.attachment.MimeConstants;

/**
 * SOAPConnection implementation
 *
 * @author Thomas.Diesler@jboss.org
 * @since 12-Mar-2008
 */
public class FastInfosetConnectionHTTP extends SOAPProtocolConnectionHTTP
{
   public UnMarshaller getUnmarshaller()
   {
      return new FastInfosetUnMarshaller();
   }

   public Marshaller getMarshaller()
   {
      return new FastInfosetMarshaller();
   }
   
   @Override
   protected void populateHeaders(MessageAbstraction reqMessage, Map<String, Object> metadata)
   {
      MimeHeaders mimeHeaders = reqMessage.getMimeHeaders();
      mimeHeaders.setHeader(MimeConstants.CONTENT_TYPE, MimeConstants.TYPE_FASTINFOSET);
      mimeHeaders.addHeader(MimeConstants.ACCEPT, MimeConstants.TYPE_FASTINFOSET);
      
      super.populateHeaders(reqMessage, metadata);
   }
}
