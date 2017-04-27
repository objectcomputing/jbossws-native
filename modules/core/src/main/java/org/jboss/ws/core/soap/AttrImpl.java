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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

/**
 * Facade for DOM Attr. Helps ensure that the propper SAAJ entities are returned
 * after DOM calls are made.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class AttrImpl extends NodeImpl implements Attr
{
   private SOAPElementImpl element;

   private Attr attr;

   public AttrImpl(SOAPElementImpl element, Attr attr)
   {
      super(attr);
      this.attr = attr;
      this.element = element;
   }

   public String getName()
   {
      return attr.getName();
   }

   public Element getOwnerElement()
   {
      return element;
   }

   public Document getOwnerDocument()
   {
      return element.getOwnerDocument();
   }

   public boolean getSpecified()
   {
      return attr.getSpecified();
   }

   public String getValue()
   {
      return attr.getValue();
   }

   public TypeInfo getSchemaTypeInfo()
   {
      // TODO Implement DOM3
      return null;
   }

   public boolean isId()
   {
      // TODO Implement DOM3
      return false;
   }
}
