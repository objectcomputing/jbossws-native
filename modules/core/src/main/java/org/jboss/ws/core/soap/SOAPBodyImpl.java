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

import java.util.Iterator;
import java.util.Locale;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.Text;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * An object that represents the contents of the SOAP body element in a SOAP message.
 * A SOAP body element consists of XML data that affects the way the application-specific content is processed.
 *
 * A SOAPBody object contains SOAPBodyElement objects, which have the content for the SOAP body.
 * A SOAPFault object, which carries status and/or error information, is an example of a SOAPBodyElement object.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPBodyImpl extends SOAPElementImpl implements SOAPBody
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPBodyImpl.class);

   public SOAPBodyImpl(String prefix, String namespace)
   {
      super("Body", prefix, namespace);
   }

   /** Convert the child into a SOAPBodyElement */
   public SOAPElement addChildElement(SOAPElement child) throws SOAPException
   {
      log.trace("addChildElement: " + child.getElementName());

      if ((child instanceof SOAPBodyElement) == false)
         child = convertToBodyElement(child);

      child = super.addChildElement(child);
      return child;
   }

   public SOAPBodyElement addBodyElement(Name name) throws SOAPException
   {
      log.trace("addBodyElement: " + name);
      SOAPBodyElement child = new SOAPBodyElementDoc(name);
      return (SOAPBodyElement)addChildElement(child);
   }

   public SOAPBodyElement addBodyElement(QName qname) throws SOAPException
   {
      log.trace("addBodyElement: " + qname);
      SOAPBodyElement child = new SOAPBodyElementDoc(qname);
      return (SOAPBodyElement)addChildElement(child);
   }

   public SOAPBodyElement addDocument(Document doc) throws SOAPException
   {
      log.trace("addDocument");
      Element rootElement = doc.getDocumentElement();
      SOAPFactoryImpl soapFactory = new SOAPFactoryImpl();
      SOAPElement soapElement = soapFactory.createElement(rootElement);
      return (SOAPBodyElement)addChildElement(soapElement);
   }

   public SOAPFault addFault() throws SOAPException
   {
      log.trace("addFault");
      if (hasFault())
         throw new SOAPException("A SOAPBody may contain at most one SOAPFault child element");

      SOAPFaultImpl soapFault = new SOAPFaultImpl(getPrefix(), getNamespaceURI());
      soapFault = (SOAPFaultImpl)addChildElement(soapFault);
      soapFault.setFaultCode(soapFault.getDefaultFaultCode());
      return soapFault;
   }

   public SOAPFault addFault(Name faultCode, String faultString) throws SOAPException
   {
      log.trace("addFault");
      if (hasFault())
         throw new SOAPException("A SOAPBody may contain at most one SOAPFault child element");

      SOAPFaultImpl soapFault = new SOAPFaultImpl(getPrefix(), getNamespaceURI());
      soapFault = (SOAPFaultImpl)addChildElement(soapFault);
      soapFault.setFaultCode(faultCode);
      soapFault.setFaultString(faultString);
      return soapFault;
   }

   public SOAPFault addFault(QName faultCode, String faultString) throws SOAPException
   {
      log.trace("addFault");
      if (hasFault())
         throw new SOAPException("A SOAPBody may contain at most one SOAPFault child element");

      SOAPFaultImpl soapFault = new SOAPFaultImpl(getPrefix(), getNamespaceURI());
      soapFault = (SOAPFaultImpl)addChildElement(soapFault);
      soapFault.setFaultCode(faultCode);
      soapFault.setFaultString(faultString);
      return soapFault;
   }

   public SOAPFault addFault(Name faultCode, String faultString, Locale locale) throws SOAPException
   {
      log.trace("addFault");
      if (hasFault())
         throw new SOAPException("A SOAPBody may contain at most one SOAPFault child element");

      SOAPFaultImpl soapFault = new SOAPFaultImpl(getPrefix(), getNamespaceURI());
      soapFault.setFaultCode(faultCode);
      soapFault.setFaultString(faultString, locale);
      addChildElement(soapFault);
      return soapFault;
   }

   public SOAPFault addFault(QName faultCode, String faultString, Locale locale) throws SOAPException
   {
      log.trace("addFault");
      if (hasFault())
         throw new SOAPException("A SOAPBody may contain at most one SOAPFault child element");

      SOAPFaultImpl soapFault = new SOAPFaultImpl(getPrefix(), getNamespaceURI());
      soapFault.setFaultCode(faultCode);
      soapFault.setFaultString(faultString, locale);
      addChildElement(soapFault);
      return soapFault;
   }

   public SOAPFault getFault()
   {
      log.trace("getFault");
      Iterator it = faultIterator();
      SOAPFault soapFault = it.hasNext() ? (SOAPFault)it.next() : null;
      return soapFault;
   }

   public boolean hasFault()
   {
      log.trace("hasFault");
      return faultIterator().hasNext();
   }

   private Iterator faultIterator()
   {
      return getChildElements(new QName(getNamespaceURI(), "Fault"));
   }

   public SOAPBodyElement getBodyElement()
   {
      SOAPBodyElement bodyElement = null;
      Iterator it = getChildElements();
      while (bodyElement == null && it.hasNext())
      {
         Object next = it.next();
         if (next instanceof SOAPBodyElement)
            bodyElement = (SOAPBodyElement)next;
      }
      return bodyElement;
   }

   public Node appendChild(Node newChild) throws DOMException
   {
      log.trace("appendChild: " + newChild.getNodeName());
      if (needsConversionToBodyElement(newChild))
         newChild = convertToBodyElement(newChild);

      return super.appendChild(newChild);
   }

   public Node insertBefore(Node newChild, Node refChild) throws DOMException
   {
      log.trace("insertBefore: " + newChild.getNodeName());
      if (needsConversionToBodyElement(newChild))
         newChild = convertToBodyElement(newChild);

      return super.insertBefore(newChild, refChild);
   }

   public Node replaceChild(Node newChild, Node oldChild) throws DOMException
   {
      log.trace("replaceChild: " + newChild.getNodeName());
      if (needsConversionToBodyElement(newChild))
         newChild = convertToBodyElement(newChild);

      return super.replaceChild(newChild, oldChild);
   }

   @Override
   public SOAPElement addAttribute(Name name, String value) throws SOAPException
   {
      String envNamespace = getNamespaceURI();
      if (Constants.NS_SOAP12_ENV.equals(envNamespace) && name.equals(new NameImpl("encodingStyle", Constants.PREFIX_ENV, envNamespace)))
         throw new SOAPException("Cannot set encodingStyle on: " + getElementQName());

      return super.addAttribute(name, value);
   }

   public Document extractContentAsDocument() throws SOAPException
   {
      log.trace("extractContentAsDocument");

      Iterator childElements = getChildElements();

      SOAPElementImpl childElement = null;

      while (childElements.hasNext() == true)
      {
         Object current = childElements.next();
         if (current instanceof SOAPElementImpl)
         {
            childElement = (SOAPElementImpl)current;
            break;
         }
      }

      // zero child elements?
      if (childElement == null)
         throw new SOAPException("Cannot find SOAPBodyElement");

      // more than one child element?
      while (childElements.hasNext() == true)
      {
         Object current = childElements.next();
         if (current instanceof SOAPElementImpl)
            throw new SOAPException("Multiple SOAPBodyElement");
      }

      if (childElement instanceof SOAPContentElement)
      {
         // cause expansion to DOM
         SOAPContentElement contentElement = (SOAPContentElement)childElement;
         // TODO change visibility of SOAPContentElement.expandToDOM() to package? 
         contentElement.hasChildNodes();
      }

      // child SOAPElement is removed as part of this process
      childElement.detachNode();

      // child element's owner document might be shared with other elements;
      // we have to create a separate document for returning to our caller
      Document newDocument = DOMUtils.getDocumentBuilder().newDocument();
      Node adoptedElement = newDocument.adoptNode(childElement.domNode);
      newDocument.appendChild(adoptedElement);

      return newDocument;
   }

   private static boolean needsConversionToBodyElement(Node newChild)
   {
      // JBCTS-440 #addTextNodeTest1 appends a Text node to a SOAPBody
      boolean validChild = newChild instanceof SOAPBodyElement;
      validChild = validChild || newChild instanceof DocumentFragment;
      validChild = validChild || newChild instanceof Text;
      validChild = validChild || newChild instanceof Comment;
      return validChild == false;
   }

   private static SOAPBodyElementDoc convertToBodyElement(Node node)
   {
      if (!(node instanceof SOAPElementImpl))
         throw new IllegalArgumentException("SOAPElement expected");

      SOAPElementImpl element = (SOAPElementImpl)node;
      element.detachNode();
      return new SOAPBodyElementDoc(element);
   }

}
