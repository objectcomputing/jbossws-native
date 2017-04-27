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
 * Represents a WSDL 2.0 SOAP Header Block. The presence of the SOAP Header
 * Block component indicates that the service supports headers and MAY require a
 * web service consumer/client to use the header. It may appear up to one time
 * in the message.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class WSDLSOAPHeader implements Serializable
{
   private static final long serialVersionUID = -3102495235178249853L;

   private final QName element;
   private final String partName;
   private boolean required = false;
   private boolean mustUnderstand = false;
   private boolean includeInSignature = false;

   public WSDLSOAPHeader(QName element, String partName)
   {
      this.element = element;
      this.partName = partName;
   }

   /**
    * Returns the name of the header schema element that describes the header's
    * contents.
    *
    * @return the name of the header schema element
    */
   public QName getElement()
   {
      return element;
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
    * Indicates whether the resulting SOAP header has a mustUnderstand attribute
    * set to true.
    *
    * @return the value of the SOAP mustUnderstand attribute
    */
   public boolean isMustUnderstand()
   {
      return mustUnderstand;
   }

   /**
    * Specifies whether the resulting SOAP Header has a mustUnderstand attribute
    * set to true.
    *
    * @param mustUnderstand the value of the SOAP mustUnderstand attribute
    */
   public void setMustUnderstand(boolean mustUnderstand)
   {
      this.mustUnderstand = mustUnderstand;
   }

   /**
    * Indicates whether the resulting SOAP header must be present in the
    * message.
    *
    * @return true if the header must be present, otherwise false
    */
   public boolean isRequired()
   {
      return required;
   }

   /**
    * Specifies whether the resulting SOAP header is required to be present on
    * the message.
    *
    * @param required true if the header must be present, otherwise false
    */
   public void setRequired(boolean required)
   {
      this.required = required;
   }

   /**
    * Indicates the resulting WSDL should include this header as part of the
    * interface message. This is currently only valid for WSDL 1.1, as WSDL 2.0
    * does not have an equivalent way to specify this. This serves as a hint to
    * binding tools that the header should be mapped to a Java parameter.
    *
    * @return whether the header should be part of the interface message
    */
   public boolean isIncludeInSignature()
   {
      return includeInSignature;
   }

   /**
    * Speficies the resulting WSDL should include this header as part of the
    * interface message. This is currently only valid for WSDL 1.1, as WSDL 2.0
    * does not have an equivalent way to specify this. This serves as a hint to
    * binding tools that the header should be mapped to a Java parameter.
    *
    * @param includeInSignature whether the header should be part of the
    * interface message
    */
   public void setIncludeInSignature(boolean includeInSignature)
   {
      this.includeInSignature = includeInSignature;
   }
}
