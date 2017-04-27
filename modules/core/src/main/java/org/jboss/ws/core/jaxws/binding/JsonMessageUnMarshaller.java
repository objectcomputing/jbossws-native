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
package org.jboss.ws.core.jaxws.binding;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;
import org.jboss.remoting.marshal.UnMarshaller;
import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.extensions.json.BadgerFishDOMDocumentParser;
import org.w3c.dom.Document;

/**
 * @author Thomas.Diesler@jboss.org
 * @since 25-Nov-2004
 */
public class JsonMessageUnMarshaller implements UnMarshaller
{
   // Provide logging
   private static Logger log = Logger.getLogger(JsonMessageUnMarshaller.class);

   public Object read(InputStream inputStream, Map metadata) throws IOException, ClassNotFoundException
   {
      if (log.isTraceEnabled())
      {
         log.trace("Read input stream with metadata=" + metadata);
      }

      // TODO: this should not be a SOAP message
      try
      {
         MessageFactoryImpl factory = new MessageFactoryImpl();
         SOAPMessage soapMsg = factory.createMessage();
         Document doc = new BadgerFishDOMDocumentParser().parse(inputStream);
         soapMsg.getSOAPBody().addDocument(doc);
         return soapMsg;
      }
      catch (SOAPException ex)
      {
         IOException ioex = new IOException("Cannot unmarshall json input stream");
         ioex.initCause(ex);
         throw ioex;
      }
   }

   /**
    * Set the class loader to use for unmarhsalling.  This may
    * be needed when need to have access to class definitions that
    * are not part of this unmarshaller's parent classloader (especially
    * when doing remote classloading).
    *
    * @param classloader
    */
   public void setClassLoader(ClassLoader classloader)
   {
      //NO OP
   }

   public UnMarshaller cloneUnMarshaller() throws CloneNotSupportedException
   {
      return new JsonMessageUnMarshaller();
   }

   private MimeHeaders getMimeHeaders(Map metadata)
   {
      log.debug("getMimeHeaders from: " + metadata);

      MimeHeaders headers = new MimeHeaders();
      Iterator i = metadata.keySet().iterator();
      while (i.hasNext())
      {
         String key = (String)i.next();
         Object value = metadata.get(key);
         if (key != null && value instanceof List)
         {
            for (Object listValue : (List)value)
            {
               headers.addHeader(key, listValue.toString());
            }
         }
      }
      return headers;
   }
}
