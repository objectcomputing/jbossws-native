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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.jboss.ws.Constants;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Attr;

/**
 * An object representing the contents in the SOAP header part of the SOAP envelope.
 * The immediate children of a SOAPHeader object can be represented only as SOAPHeaderElement objects.
 *
 * A SOAPHeaderElement object can have other SOAPElement objects as its children.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPHeaderElementImpl extends SOAPContentElement implements SOAPHeaderElement
{
   
   public SOAPHeaderElementImpl(Name name)
   {
      super(name);
   }
   
   public SOAPHeaderElementImpl(QName qname)
   {
      super(qname);
   }

   public SOAPHeaderElementImpl(SOAPElementImpl element)
   {
      super(element);
   }

   public String getRole()
   {
      final String headerURI = getParentElement().getNamespaceURI();

      if (Constants.NS_SOAP11_ENV.equals(headerURI))
         throw new UnsupportedOperationException("SOAP 1.1 does not support the concept of Role");

      Attr roleAttr = getAttributeNodeNS(headerURI, Constants.SOAP12_ATTR_ROLE);
      return roleAttr != null ? roleAttr.getValue() : null;
   }

   public void setRole(String roleURI)
   {
      final SOAPElement header = getParentElement();
      final String headerURI = header.getNamespaceURI();

      if (Constants.NS_SOAP11_ENV.equals(headerURI))
         throw new UnsupportedOperationException("SOAP 1.1 does not support the concept of Role");

      setAttributeNS(headerURI, header.getPrefix() + ":" + Constants.SOAP12_ATTR_ROLE, roleURI);
   }

   public boolean getRelay()
   {
      final String headerURI = getParentElement().getNamespaceURI();

      if (Constants.NS_SOAP11_ENV.equals(headerURI))
         throw new UnsupportedOperationException("SOAP 1.1 does not support the concept of Role");

      return DOMUtils.getAttributeValueAsBoolean(this, new QName(headerURI, Constants.SOAP12_ATTR_RELAY));
   }

   public void setRelay(boolean relay)
   {
      final SOAPElement header = getParentElement();
      final String headerURI = header.getNamespaceURI();

      if (Constants.NS_SOAP11_ENV.equals(headerURI))
         throw new UnsupportedOperationException("SOAP 1.1 does not support the concept of Role");

      setAttributeNS(headerURI, header.getPrefix() + ":" + Constants.SOAP12_ATTR_RELAY, Boolean.toString(relay));
   }
   
   public String getActor()
   {
      final String headerURI = getParentElement().getNamespaceURI();

      if (!Constants.NS_SOAP11_ENV.equals(headerURI))
         return getRole();

      Attr actorAttr = getAttributeNodeNS(headerURI, Constants.SOAP11_ATTR_ACTOR);
      return actorAttr != null ? actorAttr.getValue() : null;
   }

   public void setActor(String actorURI)
   {
      final SOAPElement header = getParentElement();
      final String headerURI = header.getNamespaceURI();

      if (Constants.NS_SOAP11_ENV.equals(headerURI))
         setAttributeNS(headerURI, header.getPrefix() + ":" + Constants.SOAP11_ATTR_ACTOR, actorURI);
      else
         setRole(actorURI);
   }

   public boolean getMustUnderstand()
   {
      final String headerURI = getParentElement().getNamespaceURI();

      return DOMUtils.getAttributeValueAsBoolean(this, new QName(headerURI, Constants.SOAP11_ATTR_MUST_UNDERSTAND));
   }

   public void setMustUnderstand(boolean mustUnderstand)
   {
      final SOAPElement header = getParentElement();
      final String headerURI = header.getNamespaceURI();

      setAttributeNS(headerURI, header.getPrefix()  + ":" + Constants.SOAP11_ATTR_MUST_UNDERSTAND, mustUnderstand ? "1" : "0");
   }

   public void setParentElement(SOAPElement parent) throws SOAPException
   {
      if (parent == null)
         throw new SOAPException("Invalid null parent element");
      
      if ((parent instanceof SOAPHeader) == false)
         throw new SOAPException("Invalid parent element: " + parent.getElementName());
      
      super.setParentElement(parent);
   }

   @Override
   public void writeElement(Writer writer) throws IOException
   {
      StringWriter strwr = new StringWriter(256);
      super.writeElement(strwr);
      
      SOAPHeader soapHeader = (SOAPHeader)getParentElement();
      SOAPEnvelope soapEnvelope = (SOAPEnvelope)soapHeader.getParentElement();
      
      // Find known namespace declarations
      List<String> knownNamespaces = new ArrayList<String>();
      Iterator prefixes = soapEnvelope.getNamespacePrefixes();
      while (prefixes.hasNext())
      {
         String prefix = (String)prefixes.next();
         String nsURI = soapEnvelope.getNamespaceURI(prefix);
         String xmlns = " xmlns:" + prefix + "='" + nsURI + "'";
         knownNamespaces.add(xmlns);
      }
      prefixes = soapHeader.getNamespacePrefixes();
      while (prefixes.hasNext())
      {
         String prefix = (String)prefixes.next();
         String nsURI = soapHeader.getNamespaceURI(prefix);
         String xmlns = " xmlns:" + prefix + "='" + nsURI + "'";
         knownNamespaces.add(xmlns);
      }
      
      // Remove known namespace declarations
      String xmlFragment = strwr.toString();
      for (String xmlns : knownNamespaces)
      {
         int start = xmlFragment.indexOf(xmlns);
         while (start > 0)
         {
            int end = start + xmlns.length();
            xmlFragment = xmlFragment.substring(0, start) + xmlFragment.substring(end);
            start = xmlFragment.indexOf(xmlns);
         }
      }
      
      writer.write(xmlFragment);
   }
}
