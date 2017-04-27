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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.Text;

import org.jboss.ws.Constants;
import org.jboss.ws.core.utils.SAAJUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

/**
 * A representation of the SOAP header element. A SOAP header element consists of XML data that affects the way the
 * application-specific content is processed by the message provider. For example, transaction semantics,
 * authentication information, and so on, can be specified as the content of a SOAPHeader object.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPHeaderImpl extends SOAPElementImpl implements SOAPHeader
{
   public SOAPHeaderImpl(String prefix, String namespace)
   {
      super("Header", prefix, namespace);
   }

   /** Add a SOAPHeaderElement as a child of this SOAPHeader instance.
    */
   public SOAPElement addChildElement(SOAPElement child) throws SOAPException
   {      
      QName qname = child.getElementQName();
      if (qname == null || qname.getNamespaceURI().length() == 0)
         throw new SOAPException("Invalid SOAPHeaderElement name: " + qname);

      // Check that we get a SOAPHeaderElement
      if ((child instanceof SOAPHeaderElement) == false)
         child = convertToHeaderElement(child);

      return super.addChildElement(child);
   }

   /** Attaching a Text node is not legal.
    */
   @Override
   public SOAPElement addTextNode(String value) throws SOAPException
   {
      // JBCTS-440 #addTextNodeTest2 adds a text node to a SOAPHeader and expects a SOAPException
      if (Constants.NS_SOAP12_ENV.equals(getNamespaceURI()))
         throw new SOAPException("Attaching a Text node to this SOAP 1.2 Element is not legal: " + getLocalName());

      return super.addTextNode(value);
   }

   /** Creates a new SOAPHeaderElement object initialized with the specified name and adds it to this SOAPHeader object.
    */
   public SOAPHeaderElement addHeaderElement(Name name) throws SOAPException
   {
      if (name == null)
         throw new SOAPException("Invalid SOAPHeaderElement name: " + name);

      return addHeaderElement(((NameImpl)name).toQName());
   }

   public SOAPHeaderElement addHeaderElement(QName qname) throws SOAPException
   {
      if (qname == null || qname.getNamespaceURI().length() == 0 || qname.getPrefix().length() == 0)
         throw new SOAPException("Invalid SOAPHeaderElement name: " + qname);

      SOAPHeaderElementImpl headerElement = new SOAPHeaderElementImpl(qname);
      addChildElement(headerElement);
      return headerElement;
   }

   /** Returns an Iterator over all the SOAPHeaderElement objects in this SOAPHeader object.
    */
   public Iterator examineAllHeaderElements()
   {
      // make a defensive copy
      ArrayList<SOAPHeaderElement> list = new ArrayList<SOAPHeaderElement>();
      Iterator it = getChildElements();
      while (it.hasNext())
      {
         SOAPHeaderElement shElement = (SOAPHeaderElement)it.next();
         list.add(shElement);
      }
      return list.iterator();
   }

   /** Returns an Iterator over all the SOAPHeaderElement objects in this SOAPHeader object that have the specified actor.
    */
   public Iterator examineHeaderElements(String actor)
   {
      if (actor == null)
         throw new IllegalArgumentException("Invalid actor: " + actor);

      // make a defensive copy
      ArrayList<SOAPHeaderElement> list = new ArrayList<SOAPHeaderElement>();
      Iterator it = getChildElements();
      while (it.hasNext())
      {
         SOAPHeaderElement shElement = (SOAPHeaderElement)it.next();
         if (actor.equals(shElement.getActor()))
            list.add(shElement);
      }
      return list.iterator();
   }

   /** Returns an Iterator over all the SOAPHeaderElement objects in this SOAPHeader object that have the specified
    * actor and that have a MustUnderstand attribute whose value is equivalent to true.
    */
   public Iterator examineMustUnderstandHeaderElements(String actor)
   {
      if (actor == null)
         throw new IllegalArgumentException("Invalid actor: " + actor);

      // make a defensive copy
      ArrayList<SOAPHeaderElement> list = new ArrayList<SOAPHeaderElement>();
      Iterator it = getChildElements();
      while (it.hasNext())
      {
         SOAPHeaderElement shElement = (SOAPHeaderElement)it.next();
         if (actor.equals(shElement.getActor()) && shElement.getMustUnderstand())
            list.add(shElement);
      }
      return list.iterator();
   }

   public Iterator extractAllHeaderElements()
   {
      // make a defensive copy
      ArrayList<SOAPHeaderElement> list = new ArrayList<SOAPHeaderElement>();
      Iterator it = getChildElements();
      while (it.hasNext())
      {
         SOAPHeaderElement shElement = (SOAPHeaderElement)it.next();
         removeChild(shElement);
         list.add(shElement);
      }
      return list.iterator();
   }

   public Iterator extractHeaderElements(String actor)
   {
      if (actor == null)
         throw new IllegalArgumentException("Invalid actor: " + actor);

      // make a defensive copy
      ArrayList<SOAPHeaderElement> list = new ArrayList<SOAPHeaderElement>();
      Iterator it = getChildElements();
      while (it.hasNext())
      {
         SOAPHeaderElement shElement = (SOAPHeaderElement)it.next();
         if (actor.equals(shElement.getActor()))
         {
            removeChild(shElement);
            list.add(shElement);
         }
      }
      return list.iterator();
   }

   public Node appendChild(Node newChild) throws DOMException
   {
      if (needsConversionToHeaderElement(newChild))
         newChild = convertToHeaderElement(newChild);

      return super.appendChild(newChild);
   }

   public Node insertBefore(Node newChild, Node refChild) throws DOMException
   {
      if (needsConversionToHeaderElement(newChild))
         newChild = convertToHeaderElement(newChild);

      return super.insertBefore(newChild, refChild);
   }

   public Node replaceChild(Node newChild, Node oldChild) throws DOMException
   {
      if (needsConversionToHeaderElement(newChild))
         newChild = convertToHeaderElement(newChild);

      return super.replaceChild(newChild, oldChild);
   }

   public SOAPHeaderElement addNotUnderstoodHeaderElement(QName qname) throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Header does not support the concept of NotUnderstood");

      // create NotUnderstood header block
      QName notUnderstoodName = new QName(getNamespaceURI(), "NotUnderstood", getPrefix());
      SOAPHeaderElement notUnderstoodElement = addHeaderElement(notUnderstoodName);

      // set qname attribute
      SAAJUtils.setQualifiedAttributeValue(notUnderstoodElement, "qname", qname);

      return notUnderstoodElement;
   }

   public SOAPHeaderElement addUpgradeHeaderElement(Iterator supportedSoapUris) throws SOAPException
   {
      if (supportedSoapUris == null)
         throw new SOAPException("list of supported URIs cannot be null");

      if (!supportedSoapUris.hasNext())
         throw new SOAPException("list of supported URIs cannot be empty");

      final String namespaceURI = getNamespaceURI();
      final String prefix = getPrefix();

      // create Upgrade header block
      QName upgradeName = new QName(namespaceURI, "Upgrade", prefix);
      SOAPHeaderElement upgradeElement = addHeaderElement(upgradeName);

      while (supportedSoapUris.hasNext())
      {
         String soapUri = (String)supportedSoapUris.next();

         SOAPElement supportedElement = upgradeElement.addChildElement("SupportedEnvelope", prefix, namespaceURI);
         SAAJUtils.setQualifiedAttributeValue(supportedElement, "qname", new QName(soapUri, "Envelope"));
      }
      return upgradeElement;
   }

   public SOAPHeaderElement addUpgradeHeaderElement(String[] supportedSoapUris) throws SOAPException
   {
      if (supportedSoapUris == null)
         throw new SOAPException("list of supported URIs cannot be null");

      return addUpgradeHeaderElement(Arrays.asList(supportedSoapUris).iterator());
   }

   public SOAPHeaderElement addUpgradeHeaderElement(String supportedSoapUri) throws SOAPException
   {
      if (supportedSoapUri == null)
         throw new SOAPException("supported URI cannot be null");

      return addUpgradeHeaderElement(Collections.singletonList(supportedSoapUri).iterator());
   }

   private static boolean needsConversionToHeaderElement(Node newChild)
   {
      // JBCTS-440 #addTextNodeTest2 appends a Text node to a SOAPHeader
      boolean validChild = newChild instanceof SOAPHeaderElementImpl;
      validChild = validChild || newChild instanceof DocumentFragment;
      validChild = validChild || newChild instanceof Text;
      validChild = validChild || newChild instanceof Comment;
      return validChild == false;
   }

   private static SOAPHeaderElementImpl convertToHeaderElement(Node node)
   {
      if (!(node instanceof SOAPElementImpl))
         throw new IllegalArgumentException("SOAPElement expected");

      SOAPElementImpl element = (SOAPElementImpl)node;

      // convert to SOAPHeaderElement
      element.detachNode();
      return new SOAPHeaderElementImpl(element);
   }
}
