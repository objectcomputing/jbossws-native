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

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

/**
 * An implementation of a DOM Document fragment. This just delegates to 
 * 
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class DocumentFragmentImpl implements DocumentFragment
{
   private DocumentFragment delegate;

   public DocumentFragmentImpl(DocumentFragment fragment)
   {
      this.delegate = fragment;
   }

   public Node appendChild(Node arg0) throws DOMException
   {
      return delegate.appendChild(arg0);
   }

   public Node cloneNode(boolean arg0)
   {
      return delegate.cloneNode(arg0);
   }

   public short compareDocumentPosition(Node arg0) throws DOMException
   {
      return delegate.compareDocumentPosition(arg0);
   }

   public NamedNodeMap getAttributes()
   {
      return delegate.getAttributes();
   }

   public String getBaseURI()
   {
      return delegate.getBaseURI();
   }

   public NodeList getChildNodes()
   {
      return delegate.getChildNodes();
   }

   public Object getFeature(String arg0, String arg1)
   {
      return delegate.getFeature(arg0, arg1);
   }

   public Node getFirstChild()
   {
      return delegate.getFirstChild();
   }

   public Node getLastChild()
   {
      return delegate.getLastChild();
   }

   public String getLocalName()
   {
      return delegate.getLocalName();
   }

   public String getNamespaceURI()
   {
      return delegate.getNamespaceURI();
   }

   public Node getNextSibling()
   {
      return delegate.getNextSibling();
   }

   public String getNodeName()
   {
      return delegate.getNodeName();
   }

   public short getNodeType()
   {
      return delegate.getNodeType();
   }

   public String getNodeValue() throws DOMException
   {
      return delegate.getNodeValue();
   }

   public Document getOwnerDocument()
   {
      return delegate.getOwnerDocument();
   }

   public Node getParentNode()
   {
      return delegate.getParentNode();
   }

   public String getPrefix()
   {
      return delegate.getPrefix();
   }

   public Node getPreviousSibling()
   {
      return delegate.getPreviousSibling();
   }

   public String getTextContent() throws DOMException
   {
      return delegate.getTextContent();
   }

   public Object getUserData(String arg0)
   {
      return delegate.getUserData(arg0);
   }

   public boolean hasAttributes()
   {
      return delegate.hasAttributes();
   }

   public boolean hasChildNodes()
   {
      return delegate.hasChildNodes();
   }

   public Node insertBefore(Node arg0, Node arg1) throws DOMException
   {
      return delegate.insertBefore(arg0, arg1);
   }

   public boolean isDefaultNamespace(String arg0)
   {
      return delegate.isDefaultNamespace(arg0);
   }

   public boolean isEqualNode(Node arg0)
   {
      return delegate.isEqualNode(arg0);
   }

   public boolean isSameNode(Node arg0)
   {
      return delegate.isSameNode(arg0);
   }

   public boolean isSupported(String arg0, String arg1)
   {
      return delegate.isSupported(arg0, arg1);
   }

   public String lookupNamespaceURI(String arg0)
   {
      return delegate.lookupNamespaceURI(arg0);
   }

   public String lookupPrefix(String arg0)
   {
      return delegate.lookupPrefix(arg0);
   }

   public void normalize()
   {
      delegate.normalize();
   }

   public Node removeChild(Node arg0) throws DOMException
   {
      return delegate.removeChild(arg0);
   }

   public Node replaceChild(Node arg0, Node arg1) throws DOMException
   {
      return delegate.replaceChild(arg0, arg1);
   }

   public void setNodeValue(String arg0) throws DOMException
   {
      delegate.setNodeValue(arg0);
   }

   public void setPrefix(String arg0) throws DOMException
   {
      delegate.setPrefix(arg0);
   }

   public void setTextContent(String arg0) throws DOMException
   {
      delegate.setTextContent(arg0);
   }

   public Object setUserData(String arg0, Object arg1, UserDataHandler arg2)
   {
      return delegate.setUserData(arg0, arg1, arg2);
   }
}
