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
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.wsf.spi.util.ServiceLoader;
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

/** An implementation of SOAPPart.
 * 
 *
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPPartImpl extends SOAPPart
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPPartImpl.class);

   private SOAPMessage soapMessage;
   private SOAPEnvelope soapEnvelope;

   private SOAPDocument doc = new SOAPDocument();

   SOAPPartImpl(SOAPMessage message)
   {
      this.soapMessage = message;
   }

   public SOAPMessage getSOAPMessage()
   {
      return soapMessage;
   }
   
   public SOAPEnvelope getEnvelope() throws SOAPException
   {
      return soapEnvelope;
   }

   public void setEnvelope(SOAPEnvelope soapEnvelope)
   {
      this.soapEnvelope = soapEnvelope;
   }

   public void removeMimeHeader(String s)
   {
      MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();
      mimeHeaders.removeHeader(s);
   }

   public void removeAllMimeHeaders()
   {
      MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();
      mimeHeaders.removeAllHeaders();
   }

   public String[] getMimeHeader(String name)
   {
      MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();
      return mimeHeaders.getHeader(name);
   }

   public void setMimeHeader(String name, String value)
   {
      MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();
      mimeHeaders.setHeader(name, value);
   }

   public void addMimeHeader(String name, String value)
   {
      MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();
      mimeHeaders.addHeader(name, value);
   }

   public Iterator getAllMimeHeaders()
   {
      MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();
      return mimeHeaders.getAllHeaders();
   }

   public Iterator getMatchingMimeHeaders(String names[])
   {
      MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();
      return mimeHeaders.getMatchingHeaders(names);
   }

   public Iterator getNonMatchingMimeHeaders(String names[])
   {
      MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();
      return mimeHeaders.getNonMatchingHeaders(names);
   }

   public void setContent(Source source) throws SOAPException
   {
      // R2714 For one-way operations, an INSTANCE MUST NOT return a HTTP response that contains a SOAP envelope. 
      // Specifically, the HTTP response entity-body must be empty.
      if (source == null)
      {
         if(log.isDebugEnabled()) log.debug("Setting content source to null removes the SOAPEnvelope");
         soapEnvelope = null;
         return;
      }

      // Start with a fresh soapMessage
      /*MessageFactory mf = MessageFactory.newInstance();
      soapMessage = mf.createMessage();
      soapMessage.getSOAPHeader().detachNode();*/

      if (source instanceof DOMSource)
      {
         Element domElement;
         DOMSource domSource = (DOMSource)source;
         Node node = domSource.getNode();
         if (node instanceof Document)
            domElement = ((Document)node).getDocumentElement();
         else if (node instanceof Element)
            domElement = (Element)node;
         else
            throw new SOAPException("Unsupported DOMSource node: " + node);

         EnvelopeBuilder envBuilder = (EnvelopeBuilder) ServiceLoader.loadService(EnvelopeBuilder.class.getName(), EnvelopeBuilderDOM.class.getName());
         envBuilder.setStyle(Style.DOCUMENT);
         envBuilder.build(soapMessage, domElement);
      }
      else if (source instanceof StreamSource)
      {
         try
         {
            StreamSource streamSource = (StreamSource)source;
            EnvelopeBuilder envBuilder = (EnvelopeBuilder)ServiceLoader.loadService(EnvelopeBuilder.class.getName(), EnvelopeBuilderDOM.class.getName());
            envBuilder.setStyle(Style.DOCUMENT);
            InputStream stream = streamSource.getInputStream();
            Reader reader = streamSource.getReader();
            if (stream != null)
               envBuilder.build(soapMessage, stream, false);
            else if (reader != null)
               envBuilder.build(soapMessage, reader, false);
         }
         catch (IOException e)
         {
            throw new SOAPException("Cannot parse stream source", e);
         }
      }
      else
      {
         throw new SOAPException("Unsupported source parameter: " + source);
      }
   }

   public Source getContent() throws SOAPException
   {
      return new DOMSource(soapEnvelope);
   }

   // Document *********************************************************************************************************

   public DOMImplementation getImplementation()
   {
      return doc.getImplementation();
   }

   public DocumentFragment createDocumentFragment()
   {
      return doc.createDocumentFragment();
   }

   public DocumentType getDoctype()
   {
      return doc.getDoctype();
   }

   public Element getDocumentElement()
   {
      return soapEnvelope;
   }

   public Attr createAttribute(String name) throws DOMException
   {
      return doc.createAttribute(name);
   }

   public CDATASection createCDATASection(String data) throws DOMException
   {
      return doc.createCDATASection(data);
   }

   public Comment createComment(String data)
   {
      return doc.createComment(data);
   }

   public Element createElement(String tagName) throws DOMException
   {
      return doc.createElement(tagName);
   }

   public Element getElementById(String elementId)
   {
      return doc.getElementById(elementId);
   }

   public EntityReference createEntityReference(String name) throws DOMException
   {
      return doc.createEntityReference(name);
   }

   public org.w3c.dom.Node importNode(org.w3c.dom.Node importedNode, boolean deep) throws DOMException
   {
      return doc.importNode(importedNode, deep);
   }

   public NodeList getElementsByTagName(String tagname)
   {
      return doc.getElementsByTagName(tagname);
   }

   public Text createTextNode(String data)
   {
      return doc.createTextNode(data);
   }

   public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException
   {
      return doc.createAttributeNS(namespaceURI, qualifiedName);
   }

   public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException
   {
      return doc.createElementNS(namespaceURI, qualifiedName);
   }

   public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
   {
      return doc.getElementsByTagNameNS(namespaceURI, localName);
   }

   public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException
   {
      return doc.createProcessingInstruction(target, data);
   }

   // Node *********************************************************************************************************

   public org.w3c.dom.Node appendChild(org.w3c.dom.Node node) throws DOMException
   {
      throw new NotImplementedException();
   }

   public org.w3c.dom.Node cloneNode(boolean b)
   {
      throw new NotImplementedException();
   }

   public NamedNodeMap getAttributes()
   {
      throw new NotImplementedException();
   }

   public NodeList getChildNodes()
   {
      List<NodeImpl> list = new ArrayList<NodeImpl>();
      if (soapEnvelope != null)
      {
         list.add((NodeImpl)soapEnvelope);
      }      
      return new NodeListImpl(list);
   }

   public org.w3c.dom.Node getFirstChild()
   {
      return soapEnvelope;
   }

   public org.w3c.dom.Node getLastChild()
   {
      return soapEnvelope;
   }

   public String getLocalName()
   {
      throw new NotImplementedException();
   }

   public String getNamespaceURI()
   {
      throw new NotImplementedException();
   }

   public org.w3c.dom.Node getNextSibling()
   {
      throw new NotImplementedException();
   }

   public String getNodeName()
   {
      return doc.getNodeName();
   }

   public short getNodeType()
   {
      return doc.getNodeType();
   }

   public String getNodeValue() throws DOMException
   {
      throw new NotImplementedException();
   }

   public Document getOwnerDocument()
   {
      return doc;
   }

   public org.w3c.dom.Node getParentNode()
   {
      throw new NotImplementedException();
   }

   public String getPrefix()
   {
      throw new NotImplementedException();
   }

   public org.w3c.dom.Node getPreviousSibling()
   {
      throw new NotImplementedException();
   }

   public boolean hasAttributes()
   {
      throw new NotImplementedException();
   }

   public boolean hasChildNodes()
   {
      throw new NotImplementedException();
   }

   public org.w3c.dom.Node insertBefore(org.w3c.dom.Node node, org.w3c.dom.Node node1) throws DOMException
   {
      throw new NotImplementedException();
   }

   public boolean isSupported(String s, String s1)
   {
      throw new NotImplementedException();
   }

   public void normalize()
   {
      throw new NotImplementedException();
   }

   public org.w3c.dom.Node removeChild(org.w3c.dom.Node node) throws DOMException
   {
      throw new NotImplementedException();
   }

   public org.w3c.dom.Node replaceChild(org.w3c.dom.Node node, org.w3c.dom.Node node1) throws DOMException
   {
      throw new NotImplementedException();
   }

   public void setNodeValue(String s) throws DOMException
   {
      throw new NotImplementedException();
   }

   public void setPrefix(String s) throws DOMException
   {
      throw new NotImplementedException();
   }

   public Node adoptNode(Node source) throws DOMException
   {
      throw new NotImplementedException("adoptNode");
   }

   public String getDocumentURI()
   {
      throw new NotImplementedException("getDocumentURI");
   }

   public DOMConfiguration getDomConfig()
   {
      throw new NotImplementedException("getDomConfig");
   }

   public String getInputEncoding()
   {
      throw new NotImplementedException("getInputEncoding");
   }

   public boolean getStrictErrorChecking()
   {
      throw new NotImplementedException("getStrictErrorChecking");
   }

   public String getXmlEncoding()
   {
      throw new NotImplementedException("getXmlEncoding");
   }

   public boolean getXmlStandalone()
   {
      throw new NotImplementedException("getXmlStandalone");
   }

   public String getXmlVersion()
   {
      throw new NotImplementedException("getXmlVersion");
   }

   public void normalizeDocument()
   {
      throw new NotImplementedException("normalizeDocument");
   }

   public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException
   {
      throw new NotImplementedException("renameNode");
   }

   public void setDocumentURI(String documentURI)
   {
      throw new NotImplementedException("setDocumentURI");
   }

   public void setStrictErrorChecking(boolean strictErrorChecking)
   {
      throw new NotImplementedException("setStrictErrorChecking");
   }

   public void setXmlStandalone(boolean xmlStandalone) throws DOMException
   {
      throw new NotImplementedException("setXmlStandalone");
   }

   public void setXmlVersion(String xmlVersion) throws DOMException
   {
      throw new NotImplementedException("setXmlVersion");
   }

   public short compareDocumentPosition(Node other) throws DOMException
   {
      throw new NotImplementedException("compareDocumentPosition");
   }

   public String getBaseURI()
   {
      throw new NotImplementedException("getBaseURI");
   }

   public Object getFeature(String feature, String version)
   {
      throw new NotImplementedException("getFeature");
   }

   public String getTextContent() throws DOMException
   {
      throw new NotImplementedException("getTextContent");
   }

   public Object getUserData(String key)
   {
      throw new NotImplementedException("getUserData");
   }

   public boolean isDefaultNamespace(String namespaceURI)
   {
      throw new NotImplementedException("isDefaultNamespace");
   }

   public boolean isEqualNode(Node arg)
   {
      throw new NotImplementedException("isEqualNode");
   }

   public boolean isSameNode(Node other)
   {
      throw new NotImplementedException("isSameNode");
   }

   public String lookupNamespaceURI(String prefix)
   {
      throw new NotImplementedException("lookupNamespaceURI");
   }

   public String lookupPrefix(String namespaceURI)
   {
      throw new NotImplementedException("lookupPrefix");
   }

   public void setTextContent(String textContent) throws DOMException
   {
      throw new NotImplementedException("setTextContent");
   }

   public Object setUserData(String key, Object data, UserDataHandler handler)
   {
      throw new NotImplementedException("setUserData");
   }

   public void detachNode()
   {
      //TODO: SAAJ 1.3
      throw new NotImplementedException();
   }

   public SOAPElement getParentElement()
   {
      //TODO: SAAJ 1.3
      throw new NotImplementedException();
   }

   public String getValue()
   {
      //TODO: SAAJ 1.3
      throw new NotImplementedException();
   }

   public void recycleNode()
   {
      //TODO: SAAJ 1.3
      throw new NotImplementedException();
   }

   public void setParentElement(SOAPElement parent) throws SOAPException
   {
      //TODO: SAAJ 1.3
      throw new NotImplementedException();
   }

   public void setValue(String value)
   {
      //TODO: SAAJ 1.3
      throw new NotImplementedException();
   }
}
