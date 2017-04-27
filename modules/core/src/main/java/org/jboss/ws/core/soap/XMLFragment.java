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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.util.xml.BufferedStreamResult;
import org.jboss.ws.util.xml.BufferedStreamSource;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/**
 * A XMLFragment represent an XML {@link Source}.
 * 
 * The basic idea is that any {@link SOAPContentElement} XML_VALID state
 * (either before unmarshalling or after marshalling) is represented through a single interface.<br>
 *
 * @see SOAPContentElement
 * @see XMLContent
 *
 * @author Heiko.Braun@jboss.org
 * @author Thomas.Diesler@jboss.org
 * @since 05-Feb-2007
 */
public class XMLFragment
{
   // provide logging
   private static Logger log = Logger.getLogger(XMLFragment.class);

   private Source source;

   private final static String XML_PROC = "<?xml";

   // An exception that is created when a client 
   // accesses a StreamSource that can only be read once
   private RuntimeException streamSourceAccessMarker;

   public XMLFragment(Source source)
   {
      this.source = source;
   }

   public XMLFragment(String xmlString)
   {
      source = new BufferedStreamSource(xmlString.getBytes());
   }

   public XMLFragment(Result result)
   {
      if (result instanceof DOMResult)
      {
         DOMResult domResult = (DOMResult)result;
         source = new DOMSource(domResult.getNode());
      }
      else if (result instanceof BufferedStreamResult)
      {
         BufferedStreamResult br = (BufferedStreamResult)result;
         ByteArrayOutputStream baos = (ByteArrayOutputStream)br.getOutputStream();
         source = new BufferedStreamSource(baos.toByteArray());
      }
      else
      {
         throw new IllegalArgumentException("Unsupported result type: " + result);
      }
   }

   public Source getSource()
   {
      source = beginSourceAccess(source);
      endSourceAccess();
      return source;
   }

   /** Transform the Source to an XML string
    */
   public String toXMLString()
   {
      try
      {
         StringWriter strWriter = new StringWriter(1024);
         writeSourceInternal(strWriter);
         return strWriter.toString();
      }
      catch (IOException ex)
      {
         throw new WSException(ex);
      }
   }

   /** Transform the Source to an Element
    */
   public Element toElement()
   {
      Element retElement = null;
      try
      {
         source = beginSourceAccess(source);
         retElement = DOMUtils.sourceToElement(source);
         endSourceAccess();
      }
      catch (IOException ex)
      {
         handleSourceAccessException(ex);
      }
      return retElement;
   }

   public void writeTo(Writer writer) throws IOException
   {
      writeSourceInternal(writer);
   }

   public void writeTo(OutputStream out) throws IOException
   {
      PrintWriter printWriter = new PrintWriter(out);
      writeSourceInternal(printWriter);
      printWriter.flush();
      printWriter.close();
   }

   /**
    * Should only be called with <code>jbossws.SOAPMessage==TRACE</code>
    */
   private void writeSourceInternal(Writer writer) throws IOException
   {
      try
      {
         source = beginSourceAccess(source);

         if (source instanceof DOMSource)
         {
            DOMSource domSource = (DOMSource)source;
            new DOMWriter(writer).print(domSource.getNode());
         }
         else if (source instanceof StreamSource || source instanceof SAXSource)
         {
            StreamSource streamSource = (StreamSource)source;
            boolean newReader = false;

            Reader reader = streamSource.getReader();
            {
               if (reader == null)
               {
                  reader = new InputStreamReader(streamSource.getInputStream(), "UTF-8");
                  newReader = true;
               }
            }

            char[] cbuf = new char[1024];
            int len = reader.read(cbuf);
            int off = 0;

            if (len == -1)
               throw new IOException("StreamSource already exhausted");

            // Remove XML processing instruction
            String xmlProc = new String(cbuf, 0, XML_PROC.length());
            if (XML_PROC.equals(xmlProc))
            {
               off = XML_PROC.length();
               while (cbuf[off] != '>' && off < len)
                  off++;

               if (cbuf[off] != '>')
                  throw new IllegalStateException("Cannot find end of XML processing instruction");

               off++;
               len -= off;
            }

            while (len > 0)
            {
               writer.write(cbuf, off, len);
               len = reader.read(cbuf);
               off = 0;
            }
            
            if (newReader)
               reader.close();
         }
         else
         {
            throw new IllegalArgumentException("Unsupported source type: " + source);
         }

         endSourceAccess();
      }
      catch (IOException ex)
      {
         handleSourceAccessException(ex);
      }
   }

   private Source beginSourceAccess(Source source)
   {
      // no need to buffer those
      if (source instanceof BufferedStreamSource || source instanceof DOMSource)
         return source;

      // Buffer the source content
      if (source instanceof StreamSource)
      {
         source = new BufferedStreamSource((StreamSource)source);
      }
      else
      {
         try
         {
            Element element = DOMUtils.sourceToElement(source);
            source = new DOMSource(element);
         }
         catch (IOException ex)
         {
            WSException.rethrow(ex);
         }
      }

      return source;
   }

   private void endSourceAccess()
   {
      // Create the marker exception
      if (source instanceof StreamSource)
      {
         streamSourceAccessMarker = new RuntimeException();
      }
   }

   private void handleSourceAccessException(IOException ex)
   {
      if (source instanceof StreamSource && streamSourceAccessMarker != null)
      {
         log.error("StreamSource was previously accessed from", streamSourceAccessMarker);
      }
      WSException.rethrow(ex);
   }

   public String toString()
   {
      return "[source=" + source + "]";
   }
}
