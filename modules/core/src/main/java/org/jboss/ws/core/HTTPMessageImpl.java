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
package org.jboss.ws.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.jboss.util.NotImplementedException;
import org.jboss.ws.core.soap.XMLFragment;
import org.jboss.ws.core.soap.attachment.MimeConstants;

/**
 * A generic HTTP message 
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 02-Apr-2007
 */
public class HTTPMessageImpl implements HTTPMessageAbstraction
{
   private MimeHeaders mimeHeaders;
   private XMLFragment xmlFragment;   

   public HTTPMessageImpl(MimeHeaders mimeHeaders, InputStream inputStream)
   {
      this.mimeHeaders = mimeHeaders;
      this.xmlFragment = new XMLFragment(new StreamSource(inputStream));
   }

   public HTTPMessageImpl(Source source)
   {
      this.mimeHeaders = new MimeHeaders();
      this.xmlFragment = new XMLFragment(source);
      
      initDefaultMimeHeaders();
   }

   public HTTPMessageImpl(Result result)
   {
      this.mimeHeaders = new MimeHeaders();
      this.xmlFragment = new XMLFragment(result);
      
      initDefaultMimeHeaders();
   }

   // TCL requirement
   public boolean doValidate()
   {
      this.xmlFragment.toElement();
      return true;
   }

   private void initDefaultMimeHeaders()
   {
      mimeHeaders.setHeader(MimeConstants.CONTENT_TYPE, MimeConstants.TYPE_XML_UTF8);
   }

   public XMLFragment getXmlFragment()
   {
      return xmlFragment;
   }

   public void setXmlFragment(XMLFragment xmlFragment)
   {
      this.xmlFragment = xmlFragment;
   }

   public MimeHeaders getMimeHeaders()
   {
      if (mimeHeaders == null)
         mimeHeaders = new MimeHeaders();
      
      return mimeHeaders;
   }

   public void setMimeHeaders(MimeHeaders mimeHeaders)
   {
      this.mimeHeaders = mimeHeaders;
   }

   public void writeTo(OutputStream outputStream) throws IOException
   {
      xmlFragment.writeTo(outputStream);
   }

   public boolean isFaultMessage()
   {
      return false;
   }

   public void addAttachmentPart(AttachmentPart part)
   {
      throw new NotImplementedException();
   }
}
