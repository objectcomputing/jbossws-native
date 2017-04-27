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
package org.jboss.ws.extensions.addressing;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.addressing.AddressingConstants;
import javax.xml.ws.addressing.AddressingException;
import javax.xml.ws.addressing.AttributedURI;
import javax.xml.ws.addressing.EndpointReference;
import javax.xml.ws.addressing.Metadata;
import javax.xml.ws.addressing.ReferenceParameters;
import javax.xml.ws.addressing.soap.SOAPAddressingBuilder;

import org.jboss.ws.WSException;
import org.jboss.ws.extensions.addressing.soap.SOAPAddressingBuilderImpl;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/** 
 * Abstraction of EndpointReference.  
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Nov-2005
 */
public class EndpointReferenceImpl extends AttributeElementExtensibleImpl implements EndpointReference
{
   private static AddressingConstants ADDR = new AddressingConstantsImpl();

   // The REQUIRED root element name 
   private QName rootQName = new QName(ADDR.getNamespaceURI(), "EndpointReference", ADDR.getNamespacePrefix());
   // This REQUIRED element (whose content is of type xs:anyURI) specifies the [address] property of the endpoint reference.
   private AttributedURIImpl address = new AttributedURIImpl(ADDR.getAnonymousURI());
   // This OPTIONAL element may contain elements from any namespace. Such elements form the [reference parameters] of the reference.
   private ReferenceParametersImpl refParams = new ReferenceParametersImpl();
   // This OPTIONAL element may contain elements from any namespace. 
   private MetadataImpl metadata = new MetadataImpl();

   public EndpointReferenceImpl(URI uri)
   {
      this.address = new AttributedURIImpl(uri);
   }

   public EndpointReferenceImpl(Element elRoot)
   {
      initFromElement(elRoot);
   }

   public QName getRootQName()
   {
      return rootQName;
   }

   public void setRootQName(QName rootElementName)
   {
      this.rootQName = rootElementName;
   }

   public AttributedURI getAddress()
   {
      return address;
   }

   public ReferenceParameters getReferenceParameters()
   {
      return refParams;
   }

   public Metadata getMetadata()
   {
      return metadata;
   }

   private void initFromElement(Element elRoot)
   {
      if (elRoot == null)
         throw new IllegalArgumentException("Cannot initialize from null element");

      try
      {
         Map<QName, String> attributes = DOMUtils.getAttributes(elRoot);
         for (QName attqname : attributes.keySet())
         {
            String value = attributes.get(attqname);
            addAttribute(attqname, value);
         }

         Iterator it = DOMUtils.getChildElements(elRoot);
         while (it.hasNext())
         {
            Element el = (Element)it.next();
            QName qname = DOMUtils.getElementQName(el);

            // Parse Address
            if (qname.equals(ADDR.getAddressQName()))
            {
               address = new AttributedURIImpl(DOMUtils.getTextContent(el));

               attributes = DOMUtils.getAttributes(el);
               for (QName attqname : attributes.keySet())
               {
                  String value = attributes.get(attqname);
                  address.addAttribute(attqname, value);
               }
            }
            // Parse ReferenceParameters
            else if (qname.equals(ADDR.getReferenceParametersQName()))
            {
               attributes = DOMUtils.getAttributes(el);
               for (QName attqname : attributes.keySet())
               {
                  String value = attributes.get(attqname);
                  refParams.addAttribute(attqname, value);
               }
               Iterator itel = DOMUtils.getChildElements(el);
               while (itel.hasNext())
               {
                  Element child = (Element)itel.next();
                  refParams.addElement(child);
               }
            }
            // Parse Metadata
            else if (qname.equals(ADDR.getMetadataQName()))
            {
               attributes = DOMUtils.getAttributes(el);
               for (QName attqname : attributes.keySet())
               {
                  String value = attributes.get(attqname);
                  metadata.addAttribute(attqname, value);
               }
               Iterator itel = DOMUtils.getChildElements(el);
               while (itel.hasNext())
               {
                  Element child = (Element)itel.next();
                  metadata.addElement(child);
               }
            }
            else
            {
               addElement(el);
            }
         }
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         throw new AddressingException("Cannot init EPR from element", ex);
      }
   }

   public Element toElement()
   {
      String xmlString = toXMLString(false);
      try
      {
         return DOMUtils.parse(xmlString);
      }
      catch (IOException ex)
      {
         throw new WSException("Cannot parse: " + xmlString, ex);
      }
   }

   public String toXMLString(boolean pretty)
   {
      if (pretty)
      {
         Element epRef = toElement();
         return DOMWriter.printNode(epRef, true);
      }

      SOAPAddressingBuilder builder = new SOAPAddressingBuilderImpl();
      AddressingConstants ADDR = builder.newAddressingConstants();

      String rootname = getPrefixedName(rootQName);
      StringBuilder xmlBuffer = new StringBuilder("<" + rootname);
      appendAttributes(xmlBuffer, getAttributes());
      xmlBuffer.append(">");

      // insert xmlns:wsa
      String wsaURI = ADDR.getNamespaceURI();
      String wsaPrefix = ADDR.getNamespacePrefix();
      String wsaDeclaration = " xmlns:" + wsaPrefix + "='" + wsaURI + "'";
      if (xmlBuffer.indexOf(wsaDeclaration) < 0)
      {
         xmlBuffer.insert(rootname.length() + 1, wsaDeclaration);
      }

      // append address
      xmlBuffer.append("<" + getPrefixedName(ADDR.getAddressQName()));
      appendAttributes(xmlBuffer, address.getAttributes());
      xmlBuffer.append(">");
      xmlBuffer.append(address.getURI() + "</" + getPrefixedName(ADDR.getAddressQName()) + ">");

      // append parameters
      if (refParams.getElements().size() > 0 || refParams.getAttributes().size() > 0)
      {
         xmlBuffer.append("<" + getPrefixedName(ADDR.getReferenceParametersQName()));
         appendAttributes(xmlBuffer, refParams.getAttributes());
         xmlBuffer.append(">");
         appendElements(xmlBuffer, refParams.getElements());
         xmlBuffer.append("</" + getPrefixedName(ADDR.getReferenceParametersQName()) + ">");
      }

      // append metadata
      if (metadata.getElements().size() > 0 || metadata.getAttributes().size() > 0)
      {
         xmlBuffer.append("<" + getPrefixedName(ADDR.getMetadataQName()));
         appendAttributes(xmlBuffer, metadata.getAttributes());
         xmlBuffer.append(">");
         appendElements(xmlBuffer, metadata.getElements());
         xmlBuffer.append("</" + getPrefixedName(ADDR.getMetadataQName()) + ">");
      }

      // append custom elements
      appendElements(xmlBuffer, getElements());

      xmlBuffer.append("</" + rootname + ">");

      String xmlString = xmlBuffer.toString();
      return xmlString;
   }

   private void appendAttributes(StringBuilder xmlBuffer, Map<QName, String> attributes)
   {
      for (QName qname : attributes.keySet())
      {
         String qualname = getPrefixedName(qname);
         String value = attributes.get(qname);
         xmlBuffer.append(" " + qualname + "='" + value + "'");
      }
   }

   private void appendElements(StringBuilder xmlBuffer, List<Object> elements)
   {
      for (Object obj : elements)
      {
         if (obj instanceof Element)
         {
            StringWriter strwr = new StringWriter();
            DOMWriter domWriter = new DOMWriter(strwr).setCompleteNamespaces(false);
            domWriter.print((Element)obj);
            String xmlFragment = strwr.toString();
            xmlBuffer.append(xmlFragment);
         }
         else if (obj instanceof String)
         {
            xmlBuffer.append(obj);
         }
         else
         {
            throw new AddressingException("Unsupported element: " + obj.getClass().getName());
         }
      }
   }

   private String getPrefixedName(QName qname)
   {
      String prefix = qname.getPrefix();
      String localPart = qname.getLocalPart();
      String qualname = (prefix != null && prefix.length() > 0 ? prefix + ":" + localPart : localPart);
      return qualname;
   }

   public String toString()
   {
      return toXMLString(true);
   }
}
