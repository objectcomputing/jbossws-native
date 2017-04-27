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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;
import org.jboss.remoting.marshal.UnMarshaller;

/**
 * @author Thomas.Diesler@jboss.org
 * @since 25-Nov-2004
 */
public class SOAPMessageUnMarshaller implements UnMarshaller
{
   // Provide logging
   private static Logger log = Logger.getLogger(SOAPMessageUnMarshaller.class);

   public Object read(InputStream inputStream, Map metadata) throws IOException, ClassNotFoundException
   {
      if (log.isTraceEnabled())
         log.trace("Read input stream with metadata=" + metadata);

      try
      {
         SOAPMessage soapMsg = getMessageFactory().createMessage(null, inputStream, true);

         return soapMsg;
      }
      catch (SOAPException e)
      {
         log.error("Cannot unmarshall SOAPMessage", e);
         IOException e2 = new IOException(e.toString());
         e2.initCause(e);
         throw e2;
      }
   }

   protected MessageFactoryImpl getMessageFactory()
   {
      return new MessageFactoryImpl();
   }

   public void setClassLoader(ClassLoader classloader)
   {
   }

   public UnMarshaller cloneUnMarshaller() throws CloneNotSupportedException
   {
      return new SOAPMessageUnMarshaller();
   }
}
