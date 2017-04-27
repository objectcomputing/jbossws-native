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
import javax.xml.soap.SOAPConstants;

import org.jboss.remoting.marshal.Marshaller;
import org.jboss.remoting.marshal.UnMarshaller;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.jaxws.binding.JsonMessageMarshaller;
import org.jboss.ws.core.jaxws.binding.JsonMessageUnMarshaller;
import org.jboss.ws.core.soap.attachment.MimeConstants;

/**
 * JSON remoting connection
 *
 * @author Thomas.Diesler@jboss.org 
 * @since 12-Mar-2008
 */
public class JsonConnectionHTTP extends HTTPRemotingConnection
{
   public UnMarshaller getUnmarshaller()
   {
      return new JsonMessageUnMarshaller();
   }

   public Marshaller getMarshaller()
   {
      return new JsonMessageMarshaller();
   }

   @Override
   protected void populateHeaders(MessageAbstraction reqMessage, Map<String, Object> metadata)
   {
      // TODO: fix the content-type
      MimeHeaders mimeHeaders = reqMessage.getMimeHeaders();
      mimeHeaders.setHeader(MimeConstants.CONTENT_TYPE, SOAPConstants.SOAP_1_1_CONTENT_TYPE);
      
      super.populateHeaders(reqMessage, metadata);
   }
}
