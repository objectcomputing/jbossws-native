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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.WSException;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

/**
 * A representation of a node (element) in an XML document.
 * This interface extends the standard DOM Node interface with methods for getting and setting the value of a node,
 * for getting and setting the parent of a node, and for removing a node.
 *
 * When creating a DOM2 tree the objects maintained by the tree are <code>org.w3c.dom.Node</code> objects
 * and not <code>javax.xml.soap.Node</code> objects.
 * <p/>
 * This implementation shields the client from the the underlying DOM2 tree, returning <code>javax.xml.soap.Node</code>
 * objects.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class NodeImpl implements javax.xml.soap.Node
{
   // provide logging
   private static Logger log = Logger.getLogger(NodeImpl.class);

   // The parent of this Node
   protected SOAPElementImpl soapParent;
   // This org.w3c.dom.Node
   protected org.w3c.dom.Node domNode;
   // A list of soap children
   private List<NodeImpl> soapChildren = new ArrayList<NodeImpl>();
   // A hash with the user data
   private Map<String, Object> userData;

   /** Construct the Node for a given org.w3c.dom.Node
    *
    * This constructor is used:
    *
    * 1) SOAPElement construction
    * 2) Text construction
    */
   NodeImpl(org.w3c.dom.Node node)
   {
      // Method selection in Java is done at compile time
      // Late binding does not work in this case
      if (node instanceof NodeImpl)
         throw new IllegalArgumentException("Copy constructor should be used");

      domNode = node;

      // SOAP child elements should be constructed externally
      if (DOMUtils.hasChildElements(node))
         throw new IllegalArgumentException("Node cannot have child elements");
   }

   /** The copy constructor  used when converting types (i.e. SOAPElement -> SOAPHeaderElement)
    */
   NodeImpl(NodeImpl node)
   {
      soapParent = node.soapParent;
      domNode = node.domNode;
      Iterator i = node.soapChildren.iterator();
      while (i.hasNext())
      {
         NodeImpl childNode = (NodeImpl)i.next();
         childNode.soapParent = (SOAPElementImpl)this;
         soapChildren.add(childNode);
      }
   }

   // javax.xml.soap.Node *********************************************************************************************

   /**
    * Removes this Node object from the tree.
    */
   public void detachNode()
   {
      org.w3c.dom.Node domParent = domNode.getParentNode();
      if (domParent != null)
         domParent.removeChild(domNode);

      if (soapParent != null)
         ((NodeImpl)soapParent).soapChildren.remove(this);

      soapParent = null;
   }

   /**
    * Returns the parent node of this Node object.
    * This method can throw an UnsupportedOperationException if the tree is not kept in memory.
    *
    * @return the SOAPElement object that is the parent of this Node object or null if this Node object is root
    */
   public SOAPElement getParentElement()
   {
      return soapParent;
   }

   /**
    * Sets the parent of this Node object to the given SOAPElement object.
    *
    * @param parent the SOAPElement object to be set as the parent of this Node object
    * @throws javax.xml.soap.SOAPException if there is a problem in setting the parent to the given node
    */
   public void setParentElement(SOAPElement parent) throws SOAPException
   {
      // detach from the old parent
      if (soapParent != null)
         detachNode();

      soapParent = (SOAPElementImpl)parent;
   }

   /**
    * Returns the value of this node if this is a Text node or the value of the immediate child of this node otherwise.
    * <p/>
    * If there is an immediate child of this Node that it is a Text node then it's value will be returned.
    * If there is more than one Text node then the value of the first Text Node will be returned.
    * Otherwise null is returned.
    *
    * @return a String with the text of this node if this is a Text node or the text contained by the first immediate
    *         child of this Node object that is a Text object if such a child exists; null otherwise.
    */
   public String getValue()
   {
      // The Text node should overwrite getValue
      if (this instanceof javax.xml.soap.Text)
         throw new WSException("javax.xml.soap.Text should take care of this");

      String nodeValue = null;
      org.w3c.dom.Node child = (org.w3c.dom.Node)getFirstChild();
      if (child instanceof org.w3c.dom.Text)
         nodeValue = ((org.w3c.dom.Text)child).getNodeValue();

      return nodeValue;
   }

   /**
    * If this is a Text node then this method will set its value, otherwise it sets the value of the immediate (Text) child of this node.
    * <p/>
    * The value of the immediate child of this node can be set only if, there is one child node and
    * that node is a Text node, or if there are no children in which case a child Text node will be created.
    *
    * @param value A value string
    * @throws IllegalStateException if the node is not a Text node and either has more than one child node or has a child node that is not a Text node.
    */
   public void setValue(String value)
   {
      // The Text node should overwrite setValue
      if (this instanceof javax.xml.soap.Text)
         throw new WSException("javax.xml.soap.Text should take care of this");

      org.w3c.dom.Node child = (org.w3c.dom.Node)getFirstChild();

      if (child instanceof org.w3c.dom.Text)
         ((org.w3c.dom.Text)child).setNodeValue(value);

      if (child == null)
      {
         child = domNode.getOwnerDocument().createTextNode(value);
         appendChild(new TextImpl(child));
      }
   }

   /**
    * Notifies the implementation that this Node object is no longer being used by the application and that the
    * implementation is free to reuse this object for nodes that may be created later.
    * Calling the method recycleNode implies that the method detachNode has been called previously.
    */
   public void recycleNode()
   {

   }

   private List<NodeImpl> convertDocumentFragment(DocumentFragment docFragment) throws DOMException
   {
      List<NodeImpl> list = new ArrayList<NodeImpl>();
      try
      {
         SOAPFactoryImpl soapFactory = new SOAPFactoryImpl();
         for (Node node = docFragment.getFirstChild(); node != null; node = node.getNextSibling())
         {
            switch (node.getNodeType())
            {
               case Node.ELEMENT_NODE:
               {
                  SOAPElementImpl soapChild = (SOAPElementImpl)soapFactory.createElement((Element)node);
                  list.add(soapChild);
                  break;
               }
               case Node.TEXT_NODE:
               {
                  TextImpl text = new TextImpl(node);
                  list.add(text);
                  break;
               }
               case Node.CDATA_SECTION_NODE:
               {
                  TextImpl text = new TextImpl(node);
                  list.add(text);
                  break;
               }
            }
         }
      }
      catch (SOAPException ex)
      {
         throw new DOMException(DOMException.INVALID_STATE_ERR, "Could not convert a document fragment to a node");
      }
      return list;
   }

   // BEGIN org.w3c.dom.Node *******************************************************************************************

   public String getNodeName()
   {
      return domNode.getNodeName();
   }

   public String getNodeValue() throws DOMException
   {
      return domNode.getNodeValue();
   }

   public void setNodeValue(String nodeValue) throws DOMException
   {
      domNode.setNodeValue(nodeValue);
   }

   public short getNodeType()
   {
      return domNode.getNodeType();
   }

   public org.w3c.dom.Node getParentNode()
   {
      assertSOAPParent();
      return soapParent;
   }

   public NodeList getChildNodes()
   {
      return new NodeListImpl(soapChildren);
   }

   public org.w3c.dom.Node getFirstChild()
   {
      NodeImpl child = null;
      org.w3c.dom.Node domChild = domNode.getFirstChild();
      if (domChild != null)
      {
         if (!soapChildren.isEmpty()) //see JBWS-2186
         {
            child = (NodeImpl)soapChildren.get(0);
            if (domChild != child.domNode)
               throw new WSException("Inconsistent node, child lists not synchronized");
         }
      }
      return child;
   }

   public org.w3c.dom.Node getLastChild()
   {
      NodeImpl child = null;
      org.w3c.dom.Node domChild = domNode.getLastChild();
      if (domChild != null)
      {
         child = (NodeImpl)soapChildren.get(soapChildren.size() - 1);
         if (domChild != child.domNode)
            throw new WSException("Inconsistent node, child lists not synchronized");
      }
      return child;
   }

   public org.w3c.dom.Node getPreviousSibling()
   {
      assertSOAPParent();

      NodeImpl sibling = null;
      if (soapParent != null)
      {
         List children = ((NodeImpl)soapParent).soapChildren;
         for (int i = 0; i < children.size(); i++)
         {
            NodeImpl node = (NodeImpl)children.get(i);
            if (node == this && i > 0)
            {
               sibling = (NodeImpl)children.get(i - 1);
               break;
            }
         }

         if (sibling != null && sibling.domNode != domNode.getPreviousSibling())
            throw new WSException("Inconsistent node, child lists not synchronized");
      }

      return sibling;
   }

   public org.w3c.dom.Node getNextSibling()
   {
      assertSOAPParent();

      NodeImpl sibling = null;
      if (soapParent != null)
      {
         List children = ((NodeImpl)soapParent).soapChildren;
         for (int i = 0; i < children.size(); i++)
         {
            NodeImpl node = (NodeImpl)children.get(i);
            if (node == this && (i + 1) < children.size())
            {
               sibling = (NodeImpl)children.get(i + 1);
               break;
            }
         }

         if (sibling != null && sibling.domNode != domNode.getNextSibling())
            throw new WSException("Inconsistent node, child lists not synchronized");
      }

      return sibling;
   }

   public NamedNodeMap getAttributes()
   {
      return domNode.getAttributes();
   }

   public Document getOwnerDocument()
   {
      // Climb the tree in hopes of finding the soap envelope.
      // If it's not there (a detached subtree), then we return a non-associated document
      if (soapParent == null)
         return new SOAPDocument();

      return soapParent.getOwnerDocument();
   }

   public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException
   {
      // DOM says that if refChild is null, an append is performed
      if (refChild == null)
         return appendChild(newChild);

      newChild = convertDOMNode(newChild);
      refChild = convertDOMNode(refChild);

      if (newChild instanceof DocumentFragment)
      {
         List<NodeImpl> list = convertDocumentFragment((DocumentFragment)newChild);
         for (NodeImpl node : list)
         {
            insertBefore(node, refChild);
         }
         return newChild;
      }

      int index = soapChildren.indexOf(refChild);
      if (index < 0)
         throw new IllegalArgumentException("Cannot find refChild in list of javax.xml.soap.Node children");

      NodeImpl soapNewNode = (NodeImpl)newChild;
      soapNewNode.detachNode();

      NodeImpl soapRefNode = (NodeImpl)refChild;
      domNode.insertBefore(soapNewNode.domNode, soapRefNode.domNode);
      soapChildren.add(index, soapNewNode);

      soapNewNode.soapParent = (SOAPElementImpl)this;

      return newChild;
   }

   public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException
   {
      newChild = convertDOMNode(newChild);
      oldChild = convertDOMNode(oldChild);

      if (newChild instanceof DocumentFragment)
      {
         insertBefore(newChild, oldChild);
         ((NodeImpl)oldChild).detachNode();
         return newChild;
      }

      int index = soapChildren.indexOf(oldChild);
      if (index < 0)
         throw new DOMException(DOMException.NOT_FOUND_ERR, "Cannot find oldChild in list of javax.xml.soap.Node children");

      NodeImpl soapNewNode = (NodeImpl)newChild;
      NodeImpl soapOldNode = (NodeImpl)oldChild;

      soapNewNode.detachNode();

      if (soapNewNode.domNode != soapOldNode.domNode)
         domNode.replaceChild(soapNewNode.domNode, soapOldNode.domNode);

      soapChildren.remove(index);
      soapChildren.add(index, soapNewNode);

      soapNewNode.soapParent = soapOldNode.soapParent;
      soapOldNode.soapParent = null;

      return newChild;
   }

   public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException
   {
      oldChild = convertDOMNode(oldChild);

      int index = soapChildren.indexOf(oldChild);
      if (index < 0)
         throw new DOMException(DOMException.NOT_FOUND_ERR, "Cannot find oldChild in list of javax.xml.soap.Node children");

      NodeImpl soapOldNode = (NodeImpl)oldChild;
      domNode.removeChild(soapOldNode.domNode);
      soapChildren.remove(index);

      soapOldNode.soapParent = null;

      return oldChild;
   }

   public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException
   {
      newChild = convertDOMNode(newChild);

      if (newChild instanceof DocumentFragment)
      {
         List<NodeImpl> list = convertDocumentFragment((DocumentFragment)newChild);
         for (NodeImpl node : list)
         {
            appendChild(node);
         }
         return newChild;
      }

      if ((this instanceof SOAPElementImpl) == false)
         throw new DOMException(DOMException.INVALID_ACCESS_ERR, "Cannot append child to this node: " + this);

      NodeImpl soapNode = (NodeImpl)newChild;
      soapNode.detachNode();

      domNode.appendChild(soapNode.domNode);
      soapNode.soapParent = (SOAPElementImpl)this;

      soapChildren.add(soapNode);

      return newChild;
   }

   public boolean hasChildNodes()
   {
      return domNode.hasChildNodes();
   }

   public org.w3c.dom.Node cloneNode(boolean deep)
   {
      return domNode.cloneNode(deep);
   }

   public void normalize()
   {
      domNode.normalize();
   }

   public boolean isSupported(String feature, String version)
   {
      return domNode.isSupported(feature, version);
   }

   public String getNamespaceURI()
   {
      return domNode.getNamespaceURI();
   }

   public String getPrefix()
   {
      return domNode.getPrefix();
   }

   public void setPrefix(String prefix) throws DOMException
   {
      domNode.setPrefix(prefix);
   }

   public String getLocalName()
   {
      return domNode.getLocalName();
   }

   public boolean hasAttributes()
   {
      return domNode.hasAttributes();
   }

   public int hashCode()
   {
      return domNode.hashCode();
   }

   public String toString()
   {
      return super.toString() + "[" + domNode.toString() + "]";
   }

   private Node convertDOMNode(org.w3c.dom.Node node)
   {
      Node retNode;
      if (node instanceof NodeImpl)
      {
         retNode = node;
      }
      else if (node instanceof DocumentFragment)
      {
         retNode = new DocumentFragmentImpl((DocumentFragment)node);
      }
      else if (node instanceof org.w3c.dom.Text)
      {
         retNode = new TextImpl(node);
      }
      else if (node instanceof org.w3c.dom.Comment)
      {
         retNode = new TextImpl(node);
      }
      else if (node instanceof org.w3c.dom.Element)
      {
         try
         {
            retNode = new SOAPFactoryImpl().createElement((Element)node);
         }
         catch (SOAPException ex)
         {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "CAnnot convert to SOAP element: " + node);
         }
      }
      else
      {
         throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Operation not supported on this type of node: " + node);
      }
      return retNode;
   }

   private void assertSOAPParent()
   {
      org.w3c.dom.Node domParent = domNode.getParentNode();
      if (domParent != null && soapParent == null)
         throw new WSException("Inconsistent node, has a DOM parent but no SOAP parent [" + this + "] " + DOMWriter.printNode(this, false));
      if (domParent != null && soapParent != null && domParent != soapParent.domNode)
         throw new WSException("Inconsistent node, SOAP parent is not identical with DOM parent [" + this + "] " + DOMWriter.printNode(this, false));
   }

   // END org.w3c.dom.Node *******************************************************************************************

   // BEGIN org.w3c.dom.Node DOM Level 3 *****************************************************************************

   public short compareDocumentPosition(Node other) throws DOMException
   {
      // FIXME compareDocumentPosition
      throw new NotImplementedException("compareDocumentPosition");
   }

   public String getBaseURI()
   {
      // FIXME getBaseURI
      throw new NotImplementedException("getBaseURI");
   }

   public Object getFeature(String feature, String version)
   {
      // FIXME getFeature
      throw new NotImplementedException("getFeature");
   }

   public String getTextContent() throws DOMException
   {
      // FIXME getTextContent
      throw new NotImplementedException("getTextContent");
   }

   public Object getUserData(String key)
   {
      if (userData != null)
      {
         return userData.get(key);
      }
      return null;
   }

   public boolean isDefaultNamespace(String namespaceURI)
   {
      // FIXME isDefaultNamespace
      throw new NotImplementedException("isDefaultNamespace");
   }

   public boolean isEqualNode(Node arg)
   {
      return domNode.isEqualNode(arg);
   }

   public boolean isSameNode(Node other)
   {
      // FIXME isSameNode
      throw new NotImplementedException("isSameNode");
   }

   public String lookupNamespaceURI(String prefix)
   {
      // FIXME lookupNamespaceURI
      throw new NotImplementedException("lookupNamespaceURI");
   }

   public String lookupPrefix(String namespaceURI)
   {
      // FIXME lookupPrefix
      throw new NotImplementedException("lookupPrefix");
   }

   public void setTextContent(String textContent) throws DOMException
   {
      // FIXME setTextContent
      throw new NotImplementedException("setTextContent");
   }

   public Object setUserData(String key, Object data, UserDataHandler handler)
   {
      if (userData == null)
      {
         userData = new HashMap<String, Object>();
      }
      return userData.put(key, data);
   }

   // END org.w3c.dom.Node DOM Level 3 *****************************************************************************
}
