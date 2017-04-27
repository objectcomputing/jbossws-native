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
package org.jboss.ws.extensions.security;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Jason T. Greene
 */
public class WsuIdResolver extends ResourceResolverSpi
{
   Document doc;
   Element header;

   public WsuIdResolver(Document doc)
   {
      this.doc = doc;
   }

   public WsuIdResolver(Document doc, Element header)
   {
      this.doc = doc;
      this.header = header;
   }

   /**
    * @see org.apache.xml.security.utils.resolver.ResourceResolverSpi#engineCanResolve(org.w3c.dom.Attr, java.lang.String)
    */
   public boolean engineCanResolve(Attr uri, String baseURI)
   {
      if (uri == null)
         return false;

     String nodeValue = uri.getNodeValue();
     return nodeValue != null && nodeValue.startsWith("#");
   }

   /**
    * @see org.apache.xml.security.utils.resolver.ResourceResolverSpi#engineResolve(org.w3c.dom.Attr, java.lang.String)
    */
   public XMLSignatureInput engineResolve(Attr uri, String BaseURI) throws ResourceResolverException
   {
      //Document doc = uri.getOwnerDocument();
      String id = uri.getValue().substring(1);

      Element element = doc.getDocumentElement();
      element = Util.findElementByWsuId(element, id);

      // If its not in the document, try the header
      if (element == null && header != null)
         element = Util.findElementByWsuId(header, id);

      if (element == null)
         throw new ResourceResolverException(id, uri, BaseURI);

      XMLSignatureInput input = new XMLSignatureInput(element);
      input.setMIMEType("text/xml");

      input.setSourceURI(BaseURI + uri);

      return input;
   }
}
