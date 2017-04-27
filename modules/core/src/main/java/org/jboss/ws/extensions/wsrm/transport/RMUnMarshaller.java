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
package org.jboss.ws.extensions.wsrm.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jboss.remoting.marshal.UnMarshaller;

/**
 * Unmarshalls byte array from the input stream
 * 
 * @author richard.opalka@jboss.com
 */
public final class RMUnMarshaller implements UnMarshaller
{
   private static final UnMarshaller instance = new RMUnMarshaller();

   public UnMarshaller cloneUnMarshaller() throws CloneNotSupportedException
   {
      return getInstance();
   }
   
   public static UnMarshaller getInstance()
   {
      return instance;
   }
   
   public Object read(InputStream is, Map metadata) throws IOException, ClassNotFoundException
   {
      if (is == null)
         return RMMessageFactory.newMessage(null, new RMMetadata(metadata)); // TODO: investigate why is == null (WSAddressing reply-to test)

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int count = -1;
      count = is.read(buffer);
      while (count != -1)
      {
         baos.write(buffer, 0, count);
         count = is.read(buffer);
      }
      return RMMessageFactory.newMessage(baos.toByteArray(), new RMMetadata(metadata));
   }

   public void setClassLoader(ClassLoader classloader)
   {
      // do nothing
   }
   
}

