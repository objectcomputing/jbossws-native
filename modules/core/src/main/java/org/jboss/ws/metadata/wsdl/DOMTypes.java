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

import javax.xml.namespace.QName;

import org.jboss.util.NotImplementedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A DOM Element based schema representation. Care should be taken to ensure
 * thread safety.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class DOMTypes extends WSDLTypes
{
   /*
    * Perhaps we should consider moving this to JDOM, or some other DOM
    * framework that supports concurrent readers. For now callers must
    * synchronize properly.
    *
    * Also could use a cached StAX pool.
    */
   private Element element;

   public DOMTypes(Document doc)
   {
      this.element = doc.createElementNS(null, "types");
   }

   public Element getElement()
   {
      return element;
   }

   public QName getXMLType(QName name)
   {
      throw new NotImplementedException();
   }
}
