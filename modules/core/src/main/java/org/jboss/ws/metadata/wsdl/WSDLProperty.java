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
 * A "property" in the Features and Properties architecture represents a named runtime value which affects
 * the behaviour of some aspect of a Web service interaction, much like an environment variable. For
 * example, a reliable messaging SOAP module may specify a property to control the number of retries in the
 * case of network failure. WSDL documents may specify the value constraints for these properties by
 * referring to a Schema type, or by specifying a particular value. Properties, and hence property values, can
 * be shared amongst features/bindings/modules, and are named with URIs precisely to allow this type of
 * sharing.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public class WSDLProperty implements Serializable
{
   private static final long serialVersionUID = -7528676719881753461L;
   
   /** A REQUIRED uri attribute information item */
   private String uri;
   /** An OPTIONAL required attribute information item */
   private boolean required;
   /** The OPTIONAL value of the property, an ordered list of child information items, as
    * specified by the [children] property of element information items */
   private String value;
   /** A OPTIONAL type definition constraining the value of the property, or the token
    * #value if the {value} property is not empty.*/
   private QName constraint;
   
   private QName qnameValue;

   public WSDLProperty(String uri, String value)
   {
      if (uri == null)
         throw new IllegalArgumentException("Illegal property URI: " + uri);

      this.uri = uri;
      this.value = value;
   }

   public WSDLProperty(String uri, QName value)
   {
      if (uri == null)
         throw new IllegalArgumentException("Illegal property URI: " + uri);

      this.uri = uri;
      this.qnameValue = value;
   }

   public WSDLProperty(String uri, boolean required, String value, QName constraint)
   {
      if (uri == null)
         throw new IllegalArgumentException("Illegal property URI: " + uri);

      this.uri = uri;
      this.required = required;
      this.value = value;
      this.constraint = constraint;
   }

   public String getURI()
   {
      return uri;
   }

   public boolean isRequired()
   {
      return required;
   }

   public void setRequired(boolean required)
   {
      this.required = required;
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }

   public QName getConstraint()
   {
      return constraint;
   }

   public void setConstraint(QName constraint)
   {
      this.constraint = constraint;
   }
   
   public String toString()
   {
      return "[" + uri + "=" + value + "]";
   }
}
