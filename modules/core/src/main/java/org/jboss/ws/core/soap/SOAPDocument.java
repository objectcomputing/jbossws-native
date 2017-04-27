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

import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

/**
 * <code>SOAPDocument</code> ensures that the propper SAAJ elements are
 * returned when Document calls are made from a DOM client. This implementation
 * enscapsulates a single ThreadLocal Document object.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class SOAPDocument implements Document
{
   // TODO Revisit methods that are restricted or not implemented.

   private Document doc = DOMUtils.getOwnerDocument();

   // Document methods

   public DocumentType getDoctype()
   {
      return doc.getDoctype();
   }

   public DOMImplementation getImplementation()
   {
      // Should this be allowed?
      return doc.getImplementation();
   }

   public Element getDocumentElement()
   {
      // The base SOAPDocument does not have an element, only SOAPPart will
      return null;
   }

   public Element createElement(String tagName) throws DOMException
   {
      return new SOAPElementImpl(tagName);
   }

   public DocumentFragment createDocumentFragment()
   {
      return doc.createDocumentFragment();
   }

   public Text createTextNode(String data)
   {
      return doc.createTextNode(data);
   }

   public Comment createComment(String data)
   {
      return doc.createComment(data);
   }

   public CDATASection createCDATASection(String data) throws DOMException
   {
      return doc.createCDATASection(data);
   }

   public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException
   {
      return doc.createProcessingInstruction(target, data);
   }

   public Attr createAttribute(String name) throws DOMException
   {
      return doc.createAttribute(name);
   }

   public EntityReference createEntityReference(String name) throws DOMException
   {
      // Not allowed
      return null;
   }

   public NodeList getElementsByTagName(String tagname)
   {
      // The base SOAPDocument does not have an element, only SOAPPart will
      return null;
   }

   public Node importNode(Node importedNode, boolean deep) throws DOMException
   {
      // This should never be needed
      return doc.importNode(importedNode, deep);
   }

   public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException
   {
      int loc = qualifiedName.indexOf(":");

      if (loc == -1)
         return new SOAPElementImpl(qualifiedName, null, namespaceURI);

      if (loc == qualifiedName.length() - 1)
         throw new IllegalArgumentException("Invalid qualified name");

      return new SOAPElementImpl(qualifiedName.substring(loc + 1), qualifiedName.substring(0, loc), namespaceURI);
   }

   public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException
   {
      return doc.createAttributeNS(namespaceURI, qualifiedName);
   }

   public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
   {
      // The base SOAPDocument does not have an element, only SOAPPart will
      return null;
   }

   public Element getElementById(String elementId)
   {
      // The base SOAPDocument does not have an element, only SOAPPart will
      return null;
   }

   // Node methods
   public String getNodeName()
   {
      return doc.getNodeName();
   }

   public String getNodeValue() throws DOMException
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public void setNodeValue(String nodeValue) throws DOMException
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public short getNodeType()
   {
      return doc.getNodeType();
   }

   public Node getParentNode()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public NodeList getChildNodes()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public Node getFirstChild()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public Node getLastChild()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public Node getPreviousSibling()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public Node getNextSibling()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public NamedNodeMap getAttributes()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public Document getOwnerDocument()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public Node insertBefore(Node newChild, Node refChild) throws DOMException
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public Node replaceChild(Node newChild, Node oldChild) throws DOMException
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public Node removeChild(Node oldChild) throws DOMException
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public Node appendChild(Node newChild) throws DOMException
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public boolean hasChildNodes()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public Node cloneNode(boolean deep)
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public void normalize()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public boolean isSupported(String feature, String version)
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public String getNamespaceURI()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public String getPrefix()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public void setPrefix(String prefix) throws DOMException
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public String getLocalName()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   public boolean hasAttributes()
   {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Node operations not allowed on SOAPDocument");
   }

   // DOM3 methods

   public String getInputEncoding()
   {
      // FIXME getInputEncoding
      return null;
   }

   public String getXmlEncoding()
   {
      // FIXME getXmlEncoding
      return null;
   }

   public boolean getXmlStandalone()
   {
      // FIXME getXmlStandalone
      return false;
   }

   public void setXmlStandalone(boolean arg0) throws DOMException
   {
      // FIXME setXmlStandalone
   }

   public String getXmlVersion()
   {
      // FIXME getXmlVersion
      return null;
   }

   public void setXmlVersion(String arg0) throws DOMException
   {
      // FIXME setXmlVersion
   }

   public boolean getStrictErrorChecking()
   {
      // FIXME getStrictErrorChecking
      return false;
   }

   public void setStrictErrorChecking(boolean arg0)
   {
      // FIXME setStrictErrorChecking

   }

   public String getDocumentURI()
   {
      // FIXME getDocumentURI
      return null;
   }

   public void setDocumentURI(String arg0)
   {
      // FIXME setDocumentURI

   }

   public Node adoptNode(Node arg0) throws DOMException
   {
      // FIXME adoptNode
      return null;
   }

   public DOMConfiguration getDomConfig()
   {
      // FIXME getDomConfig
      return null;
   }

   public void normalizeDocument()
   {
      // FIXME normalizeDocument

   }

   public Node renameNode(Node arg0, String arg1, String arg2) throws DOMException
   {
      // FIXME renameNode
      return null;
   }

   public String getBaseURI()
   {
      // FIXME getBaseURI
      return null;
   }

   public short compareDocumentPosition(Node arg0) throws DOMException
   {
      // FIXME compareDocumentPosition
      return 0;
   }

   public String getTextContent() throws DOMException
   {
      // FIXME getTextContent
      return null;
   }

   public void setTextContent(String arg0) throws DOMException
   {
      // FIXME setTextContent

   }

   public boolean isSameNode(Node arg0)
   {
      // FIXME isSameNode
      return false;
   }

   public String lookupPrefix(String arg0)
   {
      // FIXME lookupPrefix
      return null;
   }

   public boolean isDefaultNamespace(String arg0)
   {
      // FIXME isDefaultNamespace
      return false;
   }

   public String lookupNamespaceURI(String arg0)
   {
      // FIXME lookupNamespaceURI
      return null;
   }

   public boolean isEqualNode(Node arg0)
   {
      // FIXME isEqualNode
      return false;
   }

   public Object getFeature(String arg0, String arg1)
   {
      // FIXME hasFeature
      return null;
   }

   public Object setUserData(String arg0, Object arg1, UserDataHandler arg2)
   {
      // FIXME setUserData
      return null;
   }

   public Object getUserData(String arg0)
   {
      // FIXME getUserData
      return null;
   }
}
