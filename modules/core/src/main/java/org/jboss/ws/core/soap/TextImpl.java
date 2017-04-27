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

import java.io.IOException;
import java.io.Writer;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

/**
 * A representation of a node whose value is text. A Text
 * object may represent text that is content or text that is a comment.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class TextImpl extends NodeImpl implements javax.xml.soap.Text
{

   public TextImpl(org.w3c.dom.Node node)
   {
      super(node);
   }

   /** Retrieves whether this object represents a comment.
    */
   public boolean isComment()
   {
      return domNode.getNodeType() == org.w3c.dom.Node.COMMENT_NODE;
   }

   public String getValue()
   {
      return getNodeValue();
   }

   public void setValue(String value)
   {
      setNodeValue(value);
   }

   public void writeNode(Writer out) throws IOException
   {
      String nodeValue = getNodeValue();
      if (isComment() && !nodeValue.startsWith("<!--"))
         out.write("<!--" + nodeValue + "-->");
      else
         out.write(nodeValue);
   }
   
   /**
    * Breaks this node into two nodes at the specified <code>offset</code>, keeping both in the tree as siblings.
    * <p/>
    * After being split, this node will contain all the content up to the <code>offset</code> point. A
    * new node of the same type, which contains all the content at and after the <code>offset</code> point, is returned.
    * If the original node had a parent node, the new node is inserted as the next sibling of the original node.
    * When the <code>offset</code> is equal to the length of this node, the new node has no data.
    *
    * @param offset The 16-bit unit offset at which to split, starting from <code>0</code>.
    * @return The new node, of the same type as this node.
    * @throws org.w3c.dom.DOMException INDEX_SIZE_ERR: Raised if the specified offset is negative or greater
    *                      than the number of 16-bit units in <code>data</code>.
    *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
    */
   public Text splitText(int offset) throws DOMException
   {

      if (offset < 0 || offset > getNodeValue().length())
         throw new IllegalArgumentException("Invalid offset [" + offset + "] for '" + getNodeValue() + "'");

      String before = getNodeValue().substring(0, offset + 1);
      setNodeValue(before);

      String after = getNodeValue().substring(offset + 1);
      TextImpl txtNode = new TextImpl(domNode.getOwnerDocument().createTextNode(after));

      org.w3c.dom.Node parent = getParentNode();
      if (parent != null)
      {
         org.w3c.dom.Node sibling = getNextSibling();
         if (sibling == null)
            parent.appendChild(txtNode);
         else
            parent.insertBefore(txtNode, sibling);
      }

      return txtNode;
   }

   // org.w3c.dom.CharacterData ***************************************************************************************

   /**
    * The number of 16-bit units that are available through <code>data</code>
    * and the <code>substringData</code> method below. This may have the
    * value zero, i.e., <code>CharacterData</code> nodes may be empty.
    */
   public int getLength()
   {
      return getNodeValue().length();
   }

   /**
    * Remove a range of 16-bit units from the node. Upon success,
    * <code>data</code> and <code>length</code> reflect the change.
    *
    * @param offset The offset from which to start removing.
    * @param count  The number of 16-bit units to delete. If the sum of
    *               <code>offset</code> and <code>count</code> exceeds
    *               <code>length</code> then all 16-bit units from <code>offset</code>
    *               to the end of the data are deleted.
    * @throws org.w3c.dom.DOMException INDEX_SIZE_ERR: Raised if the specified <code>offset</code> is
    *                                  negative or greater than the number of 16-bit units in
    *                                  <code>data</code>, or if the specified <code>count</code> is
    *                                  negative.
    *                                  <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
    */
   public void deleteData(int offset, int count) throws DOMException
   {
      String value = getNodeValue().substring(0, offset + 1);
      setNodeValue(value);
   }

   /**
    * The character data of the node that implements this interface. The DOM
    * implementation may not put arbitrary limits on the amount of data
    * that may be stored in a <code>CharacterData</code> node. However,
    * implementation limits may mean that the entirety of a node's data may
    * not fit into a single <code>DOMString</code>. In such cases, the user
    * may call <code>substringData</code> to retrieve the data in
    * appropriately sized pieces.
    *
    * @throws org.w3c.dom.DOMException NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
    * @throws org.w3c.dom.DOMException DOMSTRING_SIZE_ERR: Raised when it would return more characters than
    *                                  fit in a <code>DOMString</code> variable on the implementation
    *                                  platform.
    */
   public String getData() throws DOMException
   {
      return getNodeValue();
   }

   /**
    * Extracts a range of data from the node.
    *
    * @param offset Start offset of substring to extract.
    * @param count  The number of 16-bit units to extract.
    * @return The specified substring. If the sum of <code>offset</code> and
    *         <code>count</code> exceeds the <code>length</code>, then all 16-bit
    *         units to the end of the data are returned.
    * @throws org.w3c.dom.DOMException INDEX_SIZE_ERR: Raised if the specified <code>offset</code> is
    *                                  negative or greater than the number of 16-bit units in
    *                                  <code>data</code>, or if the specified <code>count</code> is
    *                                  negative.
    *                                  <br>DOMSTRING_SIZE_ERR: Raised if the specified range of text does
    *                                  not fit into a <code>DOMString</code>.
    */
   public String substringData(int offset, int count) throws DOMException
   {
      return getNodeValue().substring(offset, offset + count);
   }

   /**
    * Replace the characters starting at the specified 16-bit unit offset
    * with the specified string.
    *
    * @param offset The offset from which to start replacing.
    * @param count  The number of 16-bit units to replace. If the sum of
    *               <code>offset</code> and <code>count</code> exceeds
    *               <code>length</code>, then all 16-bit units to the end of the data
    *               are replaced; (i.e., the effect is the same as a <code>remove</code>
    *               method call with the same range, followed by an <code>append</code>
    *               method invocation).
    * @param arg    The <code>DOMString</code> with which the range must be
    *               replaced.
    * @throws org.w3c.dom.DOMException INDEX_SIZE_ERR: Raised if the specified <code>offset</code> is
    *                                  negative or greater than the number of 16-bit units in
    *                                  <code>data</code>, or if the specified <code>count</code> is
    *                                  negative.
    *                                  <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
    */
   public void replaceData(int offset, int count, String arg) throws DOMException
   {
      StringBuilder buffer = new StringBuilder(getNodeValue());
      buffer.replace(offset, offset + count, arg);
      setNodeValue(buffer.toString());
   }

   /**
    * Insert a string at the specified 16-bit unit offset.
    *
    * @param offset The character offset at which to insert.
    * @param arg    The <code>DOMString</code> to insert.
    * @throws org.w3c.dom.DOMException INDEX_SIZE_ERR: Raised if the specified <code>offset</code> is
    *                                  negative or greater than the number of 16-bit units in
    *                                  <code>data</code>.
    *                                  <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
    */
   public void insertData(int offset, String arg) throws DOMException
   {
      StringBuilder buffer = new StringBuilder(getNodeValue());
      buffer.insert(offset, arg);
      setNodeValue(buffer.toString());
   }

   /**
    * Append the string to the end of the character data of the node. Upon
    * success, <code>data</code> provides access to the concatenation of
    * <code>data</code> and the <code>DOMString</code> specified.
    *
    * @param arg The <code>DOMString</code> to append.
    * @throws org.w3c.dom.DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
    */
   public void appendData(String arg) throws DOMException
   {
      setNodeValue(getNodeValue() + arg);
   }

   /**
    * The character data of the node that implements this interface. The DOM
    * implementation may not put arbitrary limits on the amount of data
    * that may be stored in a <code>CharacterData</code> node. However,
    * implementation limits may mean that the entirety of a node's data may
    * not fit into a single <code>DOMString</code>. In such cases, the user
    * may call <code>substringData</code> to retrieve the data in
    * appropriately sized pieces.
    *
    * @throws org.w3c.dom.DOMException NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
    * @throws org.w3c.dom.DOMException DOMSTRING_SIZE_ERR: Raised when it would return more characters than
    *                                  fit in a <code>DOMString</code> variable on the implementation
    *                                  platform.
    */
   public void setData(String data) throws DOMException
   {
      setNodeValue(data);
   }
   
   // Stubbed out org.w3c.dom.Text methods **************************

   /**
    * TODO - complete the implementation
    * 
    * Returns whether this text node contains <a href='http://www.w3.org/TR/2004/REC-xml-infoset-20040204#infoitem.character'>
    * element content whitespace</a>, often abusively called "ignorable whitespace". The text node is 
    * determined to contain whitespace in element content during the load 
    * of the document or if validation occurs while using 
    * <code>Document.normalizeDocument()</code>.
    * @since DOM Level 3
    */
   public boolean isElementContentWhitespace()
   {
      return false;
   }

   /**
    * TODO - complete the implementation
    * 
    * Returns all text of <code>Text</code> nodes logically-adjacent text 
    * nodes to this node, concatenated in document order.
    * <br>For instance, in the example below <code>wholeText</code> on the 
    * <code>Text</code> node that contains "bar" returns "barfoo", while on 
    * the <code>Text</code> node that contains "foo" it returns "barfoo". 
    * @since DOM Level 3
    */
   public String getWholeText()
   {
      return null;
   }

   /**
    * TODO - complete the implementation
    * 
    * Replaces the text of the current node and all logically-adjacent text 
    * nodes with the specified text. All logically-adjacent text nodes are 
    * removed including the current node unless it was the recipient of the 
    * replacement text.
    * <br>This method returns the node which received the replacement text. 
    * The returned node is: 
    * <ul>
    * <li><code>null</code>, when the replacement text is 
    * the empty string;
    * </li>
    * <li>the current node, except when the current node is 
    * read-only;
    * </li>
    * <li> a new <code>Text</code> node of the same type (
    * <code>Text</code> or <code>CDATASection</code>) as the current node 
    * inserted at the location of the replacement.
    * </li>
    * </ul>
    * <br>For instance, in the above example calling 
    * <code>replaceWholeText</code> on the <code>Text</code> node that 
    * contains "bar" with "yo" in argument results in the following: 
    * <br>Where the nodes to be removed are read-only descendants of an 
    * <code>EntityReference</code>, the <code>EntityReference</code> must 
    * be removed instead of the read-only nodes. If any 
    * <code>EntityReference</code> to be removed has descendants that are 
    * not <code>EntityReference</code>, <code>Text</code>, or 
    * <code>CDATASection</code> nodes, the <code>replaceWholeText</code> 
    * method must fail before performing any modification of the document, 
    * raising a <code>DOMException</code> with the code 
    * <code>NO_MODIFICATION_ALLOWED_ERR</code>.
    * <br>For instance, in the example below calling 
    * <code>replaceWholeText</code> on the <code>Text</code> node that 
    * contains "bar" fails, because the <code>EntityReference</code> node 
    * "ent" contains an <code>Element</code> node which cannot be removed.
    * @param content The content of the replacing <code>Text</code> node.
    * @return The <code>Text</code> node created with the specified content.
    * @exception org.w3c.dom.DOMException
    *   NO_MODIFICATION_ALLOWED_ERR: Raised if one of the <code>Text</code> 
    *   nodes being replaced is readonly.
    * @since DOM Level 3
    */
   public Text replaceWholeText(String content)
           throws DOMException
   {
      return null;
   }
}
