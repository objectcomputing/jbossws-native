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
package org.jboss.ws.core.soap.attachment;

/**
 * Generic mime related constants.
 *
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 */
public class MimeConstants
{
   // Header constants
   public static final String ACCEPT = "Accept";
   public static final String CONTENT_ID = "Content-Id";
   public static final String CONTENT_TYPE = "Content-Type";
   public static final String CONTENT_LOCATION = "Content-Location";
   public static final String CONTENT_DESCRIPTION = "Content-Description";
   public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";

   // Types
   public static final String TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
   public static final String TYPE_APPLICATION_XOP_XML = "application/xop+xml";
   public static final String TYPE_MULTIPART_RELATED = "multipart/related";
   public static final String TYPE_TEXT_XML = "text/xml";
   public static final String TYPE_XML_UTF8 = TYPE_TEXT_XML + "; charset=UTF-8";
   public static final String TYPE_SOAP11 = TYPE_TEXT_XML;
   public static final String TYPE_SOAP12 = "application/soap+xml";
   public static final String TYPE_FASTINFOSET = "application/fastinfoset";

   // Encoding
   public static final String TEXT_8BIT_ENCODING = "8bit";
   public static final String TEXT_7BIT_ENCODING = "7bit";
   public static final String BINARY_ENCODING = "binary";
   public static final String QUOTED_PRINTABLE_ENCODING = "quoted-printable";
   public static final String BASE64_ENCODING = "base64";

   // Misc
   public static final String CID_DOMAIN = "ws.jboss.org";
   public static final String ROOTPART_CID = "<rootpart@" + CID_DOMAIN + ">";
   public static final String START_INFO_XOP = "text/xml";
}
