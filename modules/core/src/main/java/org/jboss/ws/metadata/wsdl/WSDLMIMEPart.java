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
package org.jboss.ws.metadata.wsdl;

import java.io.Serializable;

import javax.xml.namespace.QName;

/**
 * Represents a WSDL 1.1 MIME Multipart attachment.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class WSDLMIMEPart implements Serializable
{
   private static final long serialVersionUID = -3102495235178249853L;

   private final String partName;
   private final QName xmlType;
   private final String mimeTypes;

   public WSDLMIMEPart(String partName, QName xmlType, String mimeTypes)
   {
      this.mimeTypes = mimeTypes;
      this.partName = partName;
      this.xmlType = xmlType;
   }

   /**
    * Returns the xml type of this attachment. Typically xsd:hexBinary
    *
    * @return the name of the header schema element
    */
   public QName getXmlType()
   {
      return xmlType;
   }

   /**
    * Returns the name of the WSDL 1.1 part, if the output is WSDL 1.1
    *
    * @return the name of the part
    */
   public String getPartName()
   {
      return partName;
   }

   /**
    * Returns a comma seperated list of allowed mime types.
    *
    * @return the mime types
    */
   public String getMimeTypes()
   {
      return mimeTypes;
   }
}
