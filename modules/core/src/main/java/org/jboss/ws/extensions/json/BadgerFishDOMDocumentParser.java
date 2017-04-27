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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.jettison.badgerfish.BadgerFishXMLInputFactory;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * An JSON BadgerFish DOM parser 
 *
 * @author Thomas.Diesler@jboss.com
 * @since 12-Mar-2008
 */
public class BadgerFishDOMDocumentParser
{
   public Document parse(InputStream input) throws IOException
   {
      try
      {
         BadgerFishXMLInputFactory inputFactory = new BadgerFishXMLInputFactory();
         XMLStreamReader streamReader = inputFactory.createXMLStreamReader(input);
         XMLInputFactory readerFactory = XMLInputFactory.newInstance();
         XMLEventReader eventReader = readerFactory.createXMLEventReader(streamReader);
         
         // Can not create a STaX writer for a DOMResult in woodstox-3.1.1
         // DOMResult result = new DOMResult();
         // XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(result);
         
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
         XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(baos);
         eventWriter.add(eventReader);
         eventWriter.close();
         
         // This parsing step should not be necessary, if we could output to a DOMResult
         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         return DOMUtils.getDocumentBuilder().parse(bais);
      }
      catch (XMLStreamException ex)
      {
         IOException ioex = new IOException("Cannot parse input stream");
         ioex.initCause(ex);
         throw ioex;
      }
      catch (SAXException ex)
      {
         IOException ioex = new IOException("Cannot import in target document");
         ioex.initCause(ex);
         throw ioex;
      }
   }
   
}
