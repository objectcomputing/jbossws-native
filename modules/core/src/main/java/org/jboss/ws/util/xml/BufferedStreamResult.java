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
package org.jboss.ws.util.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.transform.stream.StreamResult;

import org.jboss.ws.WSException;
import org.jboss.wsf.common.IOUtils;

/**
 * Buffered StreamResult Utility class
 * @author Richard.Opalka@jboss.org
 * @author Heiko.Braun@jboss.org
 * @author Thomas.Diesler@jboss.org
 * @since 06.02.2007
 */
public final class BufferedStreamResult extends StreamResult
{
   
   private final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

   public BufferedStreamResult()
   {
   }

   public BufferedStreamResult(String xmlFragment)
   {
      try
      {
         IOUtils.copyStream(getOutputStream(), new ByteArrayInputStream(xmlFragment.getBytes("UTF-8")));
      }
      catch (IOException e)
      {
         WSException.rethrow(e);
      }
   }

   @Override
   public final OutputStream getOutputStream()
   {
      return baos;
   }

   @Override
   public final String toString()
   {
      try
      {
         return baos.toString("UTF-8");
      }
      catch (UnsupportedEncodingException e)
      {
         WSException.rethrow(e);
      }

      return null;
   }

   @Override
   public final Writer getWriter()
   {
      return null;
   }

   @Override
   public final void setWriter(Writer writer)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void setOutputStream(OutputStream outputStream)
   {
      throw new UnsupportedOperationException();
   }

}
