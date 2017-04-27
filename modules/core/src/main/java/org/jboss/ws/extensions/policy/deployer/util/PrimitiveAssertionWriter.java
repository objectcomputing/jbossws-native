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
package org.jboss.ws.extensions.policy.deployer.util;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.PrimitiveAssertion;

/**
 * 
 * @author Stefano Maestri, <mailto:stefano.maestri@javalinux.it>
 *
 */
public class PrimitiveAssertionWriter
{

   private int num = 1;

   PrimitiveAssertionWriter()
   {
   }

   public static PrimitiveAssertionWriter newInstance()
   {
      return new PrimitiveAssertionWriter();
   }

   public void writePrimitiveAssertion(PrimitiveAssertion assertion, ByteArrayOutputStream stream) throws XMLStreamException
   {
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(stream);
      writePrimitiveAssertion(assertion, writer);
      
   }
   public void writePrimitiveAssertion(PrimitiveAssertion assertion, XMLStreamWriter writer) throws XMLStreamException
   {

      QName qname = assertion.getName();

      String writerPrefix = writer.getPrefix(qname.getNamespaceURI());
      if (writerPrefix != null)
      {
         writer.writeStartElement(qname.getNamespaceURI(), qname.getLocalPart());
      }
      else
      {
         String prefix = (qname.getPrefix() != null) ? qname.getPrefix() : generateNamespace();
         writer.writeStartElement(prefix, qname.getLocalPart(), qname.getNamespaceURI());
         writer.writeNamespace(prefix, qname.getNamespaceURI());
         writer.setPrefix(prefix, qname.getNamespaceURI());

      }

      Hashtable attributes = assertion.getAttributes();
      writeAttributes(attributes, writer);

      String text = (String)assertion.getStrValue();
      if (text != null)
      {
         writer.writeCharacters(text);
      }

      //A Primitive assertion can't have terms----to be verified
      List terms = assertion.getTerms();
      writeTerms(terms, writer);

      writer.writeEndElement();
      writer.flush();
   }

   private void writeTerms(List terms, XMLStreamWriter writer) throws XMLStreamException
   {

      Iterator iterator = terms.iterator();
      while (iterator.hasNext())
      {
         Assertion assertion = (Assertion)iterator.next();
         writePrimitiveAssertion((PrimitiveAssertion) assertion, writer);
      }
   }

   private void writeAttributes(Hashtable attributes, XMLStreamWriter writer) throws XMLStreamException
   {

      Iterator iterator = attributes.keySet().iterator();
      while (iterator.hasNext())
      {
         QName qname = (QName)iterator.next();
         String value = (String)attributes.get(qname);

         String prefix = qname.getPrefix();
         if (prefix != null)
         {
            writer.writeAttribute(prefix, qname.getNamespaceURI(), qname.getLocalPart(), value);
         }
         else
         {
            writer.writeAttribute(qname.getNamespaceURI(), qname.getLocalPart(), value);
         }
      }
   }

   private String generateNamespace()
   {
      return "ns" + num++;
   }
}
