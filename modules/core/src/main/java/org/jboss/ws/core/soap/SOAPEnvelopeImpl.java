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

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.Constants;
import org.jboss.ws.core.CommonSOAPFaultException;
import org.w3c.dom.Document;

/**
 * The container for the SOAPHeader and SOAPBody portions of a SOAPPart object. By default, a
 * SOAPMessage object is created with a SOAPPart object that has a SOAPEnvelope object.
 * The SOAPEnvelope object by default has an empty SOAPBody object and an empty SOAPHeader object.
 * The SOAPBody object is required, and the SOAPHeader object, though optional, is used in the majority of cases.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPEnvelopeImpl extends SOAPElementImpl implements SOAPEnvelope
{
   // Reference the enclosing SOAPPart, so that getOwnerDocument() works correctly
   private SOAPPartImpl soapPart;

   /** Construct a SOAP envelope for the given SOAP version URI prefix, etc.
    */
   public SOAPEnvelopeImpl(SOAPPartImpl soapPart, SOAPElement element, boolean addHeaderAndBody) throws SOAPException
   {
      super((SOAPElementImpl)element);

      this.soapPart = soapPart;
      soapPart.setEnvelope(this);

      String prefix = getPrefix();
      String namespaceURI = getNamespaceURI();
      String localName = getLocalName();

      if ("Envelope".equals(localName) == false)
         throw new IllegalArgumentException("Cannot create SOAP envelope from: " + element.getElementQName());

      assertEnvelopeNamespace(namespaceURI);
      addNamespaceDeclaration(prefix, namespaceURI);

      if (addHeaderAndBody)
      {
         addHeader();
         addBody();
      }
   }

   /** Construct a SOAP envelope for the given SOAP version URI.
    */
   SOAPEnvelopeImpl(SOAPPartImpl soapPart, String namespace, boolean addHeaderAndBody) throws SOAPException
   {
      super("Envelope", Constants.PREFIX_ENV, namespace);

      this.soapPart = soapPart;
      soapPart.setEnvelope(this);

      assertEnvelopeNamespace(namespace);
      addNamespaceDeclaration(getPrefix(), namespace);

      if (addHeaderAndBody)
      {
         addHeader();
         addBody();
      }
   }

   public SOAPMessage getSOAPMessage()
   {
      return soapPart.getSOAPMessage();
   }

   public SOAPBody addBody() throws SOAPException
   {
      SOAPBody body = getBody();
      if (body != null)
         throw new SOAPException("SOAPEnvelope already has a body element");

      body = new SOAPBodyImpl(getPrefix(), getNamespaceURI());
      addChildElement(body);
      return body;
   }

   public SOAPHeader addHeader() throws SOAPException
   {
      SOAPHeader header = getHeader();
      if (header != null)
         throw new SOAPException("SOAPEnvelope already has a header element");

      header = new SOAPHeaderImpl(getPrefix(), getNamespaceURI());
      return (SOAPHeader)addChildElement(header);
   }

   @Override
   public SOAPElement addAttribute(Name name, String value) throws SOAPException
   {
      String envNamespace = getNamespaceURI();
      if (Constants.NS_SOAP12_ENV.equals(envNamespace) && name.equals(new NameImpl("encodingStyle", Constants.PREFIX_ENV, envNamespace)))
         throw new SOAPException("Cannot set encodingStyle on: " + getElementQName());

      return super.addAttribute(name, value);
   }

   /** Make sure the child is either a SOAPHeader or SOAPBody */
   public SOAPElement addChildElement(SOAPElement child) throws SOAPException
   {
      String envNamespace = getNamespaceURI();
      if (Constants.NS_SOAP12_ENV.equals(envNamespace) && !(child instanceof SOAPHeader) && !(child instanceof SOAPBody))
         throw new SOAPException("SOAPHeader or SOAPBody expected");

      return super.addChildElement(child);
   }

   public Name createName(String localName) throws SOAPException
   {
      return new NameImpl(localName);
   }

   public Name createName(String localName, String prefix, String uri) throws SOAPException
   {
      return new NameImpl(localName, prefix, uri);
   }

   public SOAPBody getBody() throws SOAPException
   {
      Iterator it = getChildElements();
      while (it.hasNext())
      {
         Node node = (Node)it.next();
         if ("Body".equals(node.getLocalName()))
            return (SOAPBody)node;
      }
      return null;
   }

   public SOAPHeader getHeader() throws SOAPException
   {
      Iterator it = getChildElements();
      while (it.hasNext())
      {
         Node node = (Node)it.next();
         if ("Header".equals(node.getLocalName()))
            return (SOAPHeader)node;
      }
      return null;
   }

   /**
    * Text nodes are not supported.
    */
   public SOAPElement addTextNode(String value) throws SOAPException
   {
      if (value.trim().length() > 0)
         throw new SOAPException("Cannot add Text node to SOAPEnvelope");

      return super.addTextNode(value);
   }

   public Document getOwnerDocument()
   {
      return soapPart;
   }

   private void assertEnvelopeNamespace(String namespaceURI)
   {
      if (!Constants.NS_SOAP12_ENV.equals(namespaceURI) && !Constants.NS_SOAP11_ENV.equals(namespaceURI))
      {
         QName faultCode = Constants.SOAP11_FAULT_CODE_VERSION_MISMATCH;
         String faultString = "Invalid SOAP envelope namespace: " + namespaceURI;
         throw new CommonSOAPFaultException(faultCode, faultString);
      }
   }
}
