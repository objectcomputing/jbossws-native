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

import org.w3c.dom.Element;

/**
 * Common metadata for (unknown) wsdl extensibility elements
 * 
 * @author Alessio Soldano, <alessio.soldano@javalinux.it>
 * @since 24-Apr-2007
 */
public class WSDLExtensibilityElement implements Serializable
{
   private static final long serialVersionUID = -7528676719881753461L;
   
   /** A REQUIRED uri attribute information item */
   private String uri;
   /** An OPTIONAL required attribute information item */
   private boolean required;
   
   private Element element;
   
   public WSDLExtensibilityElement(String uri, Element element)
   {
      this.element = element;
      this.uri = uri;
   }
   
   public Element getElement()
   {
      return element;
   }

   public void setElement(Element element)
   {
      this.element = element;
   }

   public boolean isRequired()
   {
      return required;
   }

   public void setRequired(boolean required)
   {
      this.required = required;
   }

   public String getUri()
   {
      return uri;
   }

   public void setUri(String uri)
   {
      this.uri = uri;
   }
   
}
