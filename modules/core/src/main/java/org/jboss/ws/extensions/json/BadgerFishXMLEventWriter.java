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
package org.jboss.ws.extensions.json;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * An BadgerFishXMLEventWriter that delegates to an XMLStreamWriter. 
 *
 * @author Thomas.Diesler@jboss.com
 * @since 12-Mar-2008
 */
public class BadgerFishXMLEventWriter implements XMLEventWriter
{
   private XMLStreamWriter streamWriter;

   public BadgerFishXMLEventWriter(XMLStreamWriter streamWriter)
   {
      this.streamWriter = streamWriter;
   }

   public void add(XMLEvent event) throws XMLStreamException
   {
      if (event.isStartDocument())
      {
         streamWriter.writeStartDocument();
      }
      else if (event.isStartElement())
      {
         StartElement element = event.asStartElement();
         QName elQName = element.getName();
         if (elQName.getPrefix().length() > 0 && elQName.getNamespaceURI().length() > 0)
            streamWriter.writeStartElement(elQName.getPrefix(), elQName.getLocalPart(), elQName.getNamespaceURI());
         else if (elQName.getNamespaceURI().length() > 0)
            streamWriter.writeStartElement(elQName.getNamespaceURI(), elQName.getLocalPart());
         else
            streamWriter.writeStartElement(elQName.getLocalPart());
         
         // Add element namespaces 
         Iterator namespaces = element.getNamespaces();
         while (namespaces.hasNext())
         {
            Namespace ns = (Namespace)namespaces.next();
            String prefix = ns.getPrefix();
            String nsURI = ns.getNamespaceURI();
            streamWriter.writeNamespace(prefix, nsURI);
         }

         // Add element attributes 
         Iterator attris = element.getAttributes();
         while (attris.hasNext())
         {
            Attribute attr = (Attribute)attris.next();
            QName atQName = attr.getName();
            String value = attr.getValue();
            if (atQName.getPrefix().length() > 0 && atQName.getNamespaceURI().length() > 0)
               streamWriter.writeAttribute(atQName.getPrefix(), atQName.getNamespaceURI(), atQName.getLocalPart(), value);
            else if (atQName.getNamespaceURI().length() > 0)
               streamWriter.writeAttribute(atQName.getNamespaceURI(), atQName.getLocalPart(), value);
            else
               streamWriter.writeAttribute(atQName.getLocalPart(), value);
         }
      }
      else if (event.isCharacters())
      {
         Characters chars = event.asCharacters();
         streamWriter.writeCharacters(chars.getData());
      }
      else if (event.isEndElement())
      {
         streamWriter.writeEndElement();
      }
      else if (event.isEndDocument())
      {
         streamWriter.writeEndDocument();
      }
      else
      {
         throw new XMLStreamException("Unsupported event type: " + event);
      }
   }

   public void add(XMLEventReader eventReader) throws XMLStreamException
   {
      while (eventReader.hasNext())
      {
         XMLEvent event = eventReader.nextEvent();
         add(event);
      }
      close();
   }

   public void close() throws XMLStreamException
   {
      streamWriter.close();
   }

   public void flush() throws XMLStreamException
   {
      streamWriter.flush();
   }

   public NamespaceContext getNamespaceContext()
   {
      return streamWriter.getNamespaceContext();
   }

   public String getPrefix(String prefix) throws XMLStreamException
   {
      return streamWriter.getPrefix(prefix);
   }

   public void setDefaultNamespace(String namespace) throws XMLStreamException
   {
      streamWriter.setDefaultNamespace(namespace);
   }

   public void setNamespaceContext(NamespaceContext nsContext) throws XMLStreamException
   {
      streamWriter.setNamespaceContext(nsContext);
   }

   public void setPrefix(String prefix, String uri) throws XMLStreamException
   {
      streamWriter.setPrefix(prefix, uri);
   }
}
