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

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMSource;

import org.codehaus.jettison.badgerfish.BadgerFishXMLOutputFactory;
import org.w3c.dom.Element;

/**
 * An JSON BadgerFish DOM serializer 
 *
 * @author Thomas.Diesler@jboss.com
 * @since 12-Mar-2008
 */
public class BadgerFishDOMDocumentSerializer
{
   private OutputStream output;

   public BadgerFishDOMDocumentSerializer(OutputStream output)
   {
      this.output = output;
   }

   public void serialize(Element el) throws IOException
   {
      if (output == null)
         throw new IllegalStateException("OutputStream cannot be null");
      
      try
      {
         DOMSource source = new DOMSource(el);
         XMLInputFactory readerFactory = XMLInputFactory.newInstance();
         XMLStreamReader streamReader = readerFactory.createXMLStreamReader(source);
         XMLEventReader eventReader = readerFactory.createXMLEventReader(streamReader);

         BadgerFishXMLOutputFactory writerFactory = new BadgerFishXMLOutputFactory();
         XMLStreamWriter streamWriter = writerFactory.createXMLStreamWriter(output);
         // Not implemented in jettison-1.0-RC2
         // XMLEventWriter eventWriter = writerFactory.createXMLEventWriter(output);
         XMLEventWriter eventWriter = new BadgerFishXMLEventWriter(streamWriter);
         eventWriter.add(eventReader);
         eventWriter.close();
      }
      catch (XMLStreamException ex)
      {
         IOException ioex = new IOException("Cannot serialize: " + el);
         ioex.initCause(ex);
         throw ioex;
      }
   }
}
