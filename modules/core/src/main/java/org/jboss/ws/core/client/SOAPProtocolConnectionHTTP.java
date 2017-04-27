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

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.remoting.marshal.Marshaller;
import org.jboss.remoting.marshal.UnMarshaller;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.soap.SOAPMessageMarshaller;
import org.jboss.ws.core.soap.SOAPMessageUnMarshallerHTTP;
import org.jboss.ws.extensions.xop.XOPContext;

/**
 * SOAPConnection implementation
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 * @since 02-Apr-2007
 */
public class SOAPProtocolConnectionHTTP extends HTTPRemotingConnection
{
   public UnMarshaller getUnmarshaller()
   {
      return new SOAPMessageUnMarshallerHTTP();
   }

   public Marshaller getMarshaller()
   {
      return new SOAPMessageMarshaller();
   }

   public MessageAbstraction invoke(MessageAbstraction reqMessage, Object endpoint, boolean oneway) throws IOException
   {
      try
      {
         // enforce xop transitions
         // TODO: there should be a clear transition to an immutable object model
         XOPContext.eagerlyCreateAttachments();

         // save object model changes
         SOAPMessage soapMessage = (SOAPMessage)reqMessage;
         if (reqMessage != null && soapMessage.saveRequired())
            soapMessage.saveChanges();

         return super.invoke(reqMessage, endpoint, oneway);
      }
      catch (SOAPException ex)
      {
         IOException io = new IOException();
         io.initCause(ex);
         throw io;
      }
   }

   protected void populateHeaders(MessageAbstraction reqMessage, Map<String, Object> metadata)
   {
      super.populateHeaders(reqMessage, metadata);

      Properties props = (Properties)metadata.get("HEADER");

      // R2744 A HTTP request MESSAGE MUST contain a SOAPAction HTTP header field
      // with a quoted value equal to the value of the soapAction attribute of
      // soapbind:operation, if present in the corresponding WSDL description.

      // R2745 A HTTP request MESSAGE MUST contain a SOAPAction HTTP header field
      // with a quoted empty string value, if in the corresponding WSDL description,
      // the soapAction attribute of soapbind:operation is either not present, or
      // present with an empty string as its value.

      MimeHeaders mimeHeaders = reqMessage.getMimeHeaders();
      String[] action = mimeHeaders.getHeader("SOAPAction");
      if (action != null && action.length > 0)
      {
         String soapAction = action[0];

         // R1109 The value of the SOAPAction HTTP header field in a HTTP request MESSAGE MUST be a quoted string.
         if (soapAction.startsWith("\"") == false || soapAction.endsWith("\"") == false)
            soapAction = "\"" + soapAction + "\"";

         props.put("SOAPAction", soapAction);
      }
      else
      {
         props.put("SOAPAction", "\"\"");
      }

   }
}
