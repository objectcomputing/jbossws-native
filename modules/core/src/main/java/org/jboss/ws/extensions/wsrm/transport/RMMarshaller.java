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

import java.io.IOException;
import java.io.OutputStream;

import org.jboss.remoting.InvocationRequest;
import org.jboss.remoting.invocation.OnewayInvocation;
import org.jboss.remoting.marshal.Marshaller;

/**
 * Marshalls byte array to the output stream
 * 
 * @author richard.opalka@jboss.com
 */
public final class RMMarshaller implements Marshaller
{
   private static final Marshaller instance = new RMMarshaller();
   
   public Marshaller cloneMarshaller() throws CloneNotSupportedException
   {
      return getInstance();
   }
   
   public static Marshaller getInstance()
   {
      return instance;
   }
   
   public void write(Object dataObject, OutputStream output) throws IOException
   {
      if (dataObject instanceof InvocationRequest)
         dataObject = ((InvocationRequest)dataObject).getParameter();

      if (dataObject instanceof OnewayInvocation)
         dataObject = ((OnewayInvocation)dataObject).getParameters()[0];

      if ((dataObject instanceof byte[]) == false)
         throw new IllegalArgumentException("Not a byte array: " + dataObject);

      output.write((byte[])dataObject);
      output.flush();
   }
}

