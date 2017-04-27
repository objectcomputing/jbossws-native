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
package javax.xml.ws.wsaddressing;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;

import org.w3c.dom.Element;

/**
 * This class represents a W3C Addressing EndpointReferece which is
 * a remote reference to a web service endpoint that supports the
 * W3C WS-Addressing 1.0 - Core Recommendation.
 * <p>
 * Developers should use this class in their SEIs if they want to
 * pass/return endpoint references that represent the W3C WS-Addressing
 * recommendation.
 * <p>
 * JAXB will use the JAXB annotations and bind this class to XML infoset
 * that is consistent with that defined by WS-Addressing.  See
 * <a href="http://www.w3.org/TR/2006/REC-ws-addr-core-20060509/">
 * WS-Addressing</a> 
 * for more information on WS-Addressing EndpointReferences.
 *
 * @since JAX-WS 2.1
 */

// XmlRootElement allows this class to be marshalled on its own
@XmlRootElement(name = "EndpointReference", namespace = W3CEndpointReference.NS)
@XmlType(name = "EndpointReferenceType", namespace = W3CEndpointReference.NS)
public final class W3CEndpointReference extends EndpointReference
{
   protected static final String NS = "http://www.w3.org/2005/08/addressing";
   
   private final static JAXBContext w3cjc = getW3CJaxbContext();
   
   // private but necessary properties for databinding
   @XmlElement(name = "Address", namespace = NS)
   private Address address;
   @XmlElement(name = "ReferenceParameters", namespace = NS)
   private Elements referenceParameters;
   @XmlElement(name = "Metadata", namespace = NS)
   private Elements metadata;
   @XmlAnyAttribute
   Map<QName, String> attributes;
   @XmlAnyElement
   List<Element> elements;
   
   protected W3CEndpointReference()
   {
   }

   /**
    * Creates an EPR from infoset representation
    *
    * @param source A source object containing valid XmlInfoset
    * instance consistent with the W3C WS-Addressing Core
    * recommendation.
    *
    * @throws WebServiceException
    *   If the source does NOT contain a valid W3C WS-Addressing
    *   EndpointReference.
    * @throws NullPointerException
    *   If the <code>null</code> <code>source</code> value is given
    */
   public W3CEndpointReference(Source source)
   {
      try
      {
         W3CEndpointReference epr = w3cjc.createUnmarshaller().unmarshal(source, W3CEndpointReference.class).getValue();
         this.address = epr.address;
         this.metadata = epr.metadata;
         this.referenceParameters = epr.referenceParameters;
      }
      catch (JAXBException e)
      {
         throw new WebServiceException("Error unmarshalling W3CEndpointReference ", e);
      }
      catch (ClassCastException e)
      {
         throw new WebServiceException("Source did not contain W3CEndpointReference", e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void writeTo(Result result)
   {
      try
      {
         Marshaller marshaller = w3cjc.createMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
         marshaller.marshal(this, result);
      }
      catch (JAXBException e)
      {
         throw new WebServiceException("Error marshalling W3CEndpointReference. ", e);
      }
   }

   private static JAXBContext getW3CJaxbContext()
   {
      try
      {
         return JAXBContext.newInstance(new Class[] { W3CEndpointReference.class });
      }
      catch (JAXBException ex)
      {
         throw new WebServiceException("Cannot obtain JAXB context", ex);
      }
   }

   private static class Address
   {
      @XmlValue
      String uri;
      @XmlAnyAttribute
      Map<QName, String> attributes;
      
      protected Address()
      {
      }

      public Address(String uri)
      {
         this.uri = uri;
      }

      @XmlTransient
      public String getUri()
      {
         return uri;
      }

      public void setUri(String uri)
      {
         this.uri = uri;
      }

      @XmlTransient
      public Map<QName, String> getAttributes()
      {
         return attributes;
      }

      public void setAttributes(Map<QName, String> attributes)
      {
         this.attributes = attributes;
      }
   }

   private static class Elements
   {
      @XmlAnyElement
      List<Element> elements;
      @XmlAnyAttribute
      Map<QName, String> attributes;
      
      protected Elements()
      {
      }

      public Elements(List<Element> elements)
      {
         this.elements = elements;
      }

      @XmlTransient
      public List<Element> getElements()
      {
         return elements;
      }

      public void setElements(List<Element> elements)
      {
         this.elements = elements;
      }

      @XmlTransient
      public Map<QName, String> getAttributes()
      {
         return attributes;
      }

      public void setAttributes(Map<QName, String> attributes)
      {
         this.attributes = attributes;
      }
   }
}
