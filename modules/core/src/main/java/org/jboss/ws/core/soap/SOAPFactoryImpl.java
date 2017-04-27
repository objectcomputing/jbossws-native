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

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SOAPFactory implementation.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPFactoryImpl extends SOAPFactory
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPFactoryImpl.class);

   // The envelope namespace used by the SOAPFactoryImpl
   // JBCTS-441 null means the specified protocol was DYNAMIC_SOAP_PROTOCOL
   private String envNamespace;

   public SOAPFactoryImpl()
   {
      envNamespace = SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE;
   }

   public SOAPFactoryImpl(String protocol) throws SOAPException
   {
      if (SOAPConstants.SOAP_1_2_PROTOCOL.equals(protocol))
         envNamespace = SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE;
      else if (SOAPConstants.SOAP_1_1_PROTOCOL.equals(protocol))
         envNamespace = SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE;
      else if (SOAPConstants.DYNAMIC_SOAP_PROTOCOL.equals(protocol))
         envNamespace = null;
      // JBCTS-441 #newInstanceTest4 passes "BOGUS" as the protocol and
      // expects us to throw SOAPException
      else
         throw new SOAPException("Unknown protocol: " + protocol);
   }

   @Override
   public SOAPElement createElement(Name name) throws SOAPException
   {
      return new SOAPElementImpl(name);
   }

   @Override
   public SOAPElement createElement(QName qname) throws SOAPException
   {
      return createElement(new NameImpl(qname));
   }

   @Override
   public SOAPElement createElement(String localName) throws SOAPException
   {
      return new SOAPElementImpl(localName);
   }

   @Override
   public SOAPElement createElement(String localName, String prefix, String uri) throws SOAPException
   {
      return new SOAPElementImpl(localName, prefix, uri);
   }

   @Override
   public SOAPElement createElement(Element domElement) throws SOAPException
   {
      return createElement(domElement, true);
   }

   /**
    * Create a SOAPElement from a DOM Element.
    * This method is not part of the javax.xml.soap.SOAPFactory interface.
    */
   public SOAPElement createElement(Element domElement, boolean deep) throws SOAPException
   {
      if (domElement == null)
         throw new IllegalArgumentException("Source node cannot be null");

      // Can only use this optimization if we are doing a deep copy.
      if (domElement instanceof SOAPElement && deep==true)
         return (SOAPElement)domElement;

      String localName = domElement.getLocalName();
      String prefix = domElement.getPrefix() != null ? domElement.getPrefix() : "";
      String nsURI = domElement.getNamespaceURI() != null ? domElement.getNamespaceURI() : "";

      SOAPFactory factory = SOAPFactory.newInstance();
      SOAPElement soapElement = factory.createElement(localName, prefix, nsURI);

      DOMUtils.copyAttributes(soapElement, domElement);

      if (deep)
      {
         // Add the child elements as well
         NodeList nlist = domElement.getChildNodes();
         for (int i = 0; i < nlist.getLength(); i++)
         {
            Node child = nlist.item(i);
            short nodeType = child.getNodeType();
            if (nodeType == Node.ELEMENT_NODE)
            {
               SOAPElement soapChild = createElement((Element)child);
               soapElement.addChildElement(soapChild);
               if (Constants.NAME_XOP_INCLUDE.equals(soapChild.getElementQName()))
                  XOPContext.inlineXOPData(soapChild);
            }
            else if (nodeType == Node.TEXT_NODE)
            {
               String nodeValue = child.getNodeValue();
               soapElement.addTextNode(nodeValue);
            }
            else if (nodeType == Node.CDATA_SECTION_NODE)
            {
               String nodeValue = child.getNodeValue();
               soapElement.addTextNode(nodeValue);
            }
            else
            {
               log.trace("Ignore child type: " + nodeType);
            }
         }
      }

      return soapElement;
   }

   @Override
   public Detail createDetail() throws SOAPException
   {
      assertEnvNamespace();

      return SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals(envNamespace) ? new DetailImpl() :
         new DetailImpl(SOAPConstants.SOAP_ENV_PREFIX, envNamespace);
   }

   @Override
   public Name createName(String localName, String prefix, String uri) throws SOAPException
   {
      return new NameImpl(localName, prefix, uri);
   }

   @Override
   public Name createName(String localName) throws SOAPException
   {
      return new NameImpl(localName);
   }

   @Override
   public SOAPFault createFault(String reasonText, QName faultCode) throws SOAPException
   {
      assertEnvNamespace();

      SOAPFaultImpl soapFault = new SOAPFaultImpl(SOAPConstants.SOAP_ENV_PREFIX, envNamespace);
      soapFault.setFaultCode(faultCode);
      soapFault.setFaultString(reasonText);
      return soapFault;
   }

   @Override
   public SOAPFault createFault() throws SOAPException
   {
      assertEnvNamespace();

      SOAPFaultImpl soapFault = new SOAPFaultImpl(SOAPConstants.SOAP_ENV_PREFIX, envNamespace);
      soapFault.setFaultCode(soapFault.getDefaultFaultCode());
      return soapFault;
   }

   private void assertEnvNamespace()
   {
      if (envNamespace == null)
         throw new UnsupportedOperationException("Envelope namespace not specified, use one of the SOAP protocols");
   }
}
