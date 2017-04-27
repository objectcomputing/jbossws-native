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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.soap.SOAPEnvelope;

import org.jboss.ws.WSException;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;

/**
 * Writes a SAAJ elements to an output stream.
 *
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @author Thomas.Diesler@jboss.com
 * @since 4-Aug-2006
 */
public class SOAPElementWriter
{

   // Print writer
   private PrintWriter out;
   // True, if the XML declaration should be written
   private boolean writeXMLDeclaration;
   // Explicit character set encoding
   private String charsetName;

   public SOAPElementWriter(Writer w)
   {
      this.out = new PrintWriter(w);
   }

   public SOAPElementWriter(OutputStream stream)
   {
      try
      {
         this.out = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
      }
      catch (UnsupportedEncodingException e)
      {
         // ignore, UTF-8 should be available
      }
   }

   public SOAPElementWriter(OutputStream stream, String charsetName)
   {
      try
      {
         this.out = new PrintWriter(new OutputStreamWriter(stream, charsetName));
         this.charsetName = charsetName;
      }
      catch (UnsupportedEncodingException e)
      {
         throw new IllegalArgumentException("Unsupported encoding: " + charsetName);
      }
   }

   /**
    * Set wheter the XML declaration should be written.
    * The default is false.
    */
   public SOAPElementWriter setWriteXMLDeclaration(boolean writeXMLDeclaration)
   {
      this.writeXMLDeclaration = writeXMLDeclaration;
      return this;
   }

   /**
    * Print a node with explicit prettyprinting.
    * The defaults for all other DOMWriter properties apply.
    */
   public static String writeElement(SOAPElementImpl element, boolean pretty)
   {
      if (element == null)
         return null;
      
      StringWriter strw = new StringWriter();
      new SOAPElementWriter(strw).writeElement(element);
      String xmlStr = strw.toString();
      
      if (pretty)
      {
         // This is expensive. Make sure it only happens for debugging
         try
         {
            // TODO: this unescapes sepcial chars, which might be quiet confusing
            // but they are actually send escaped.
            xmlStr = DOMWriter.printNode(DOMUtils.parse(xmlStr), true);
         }
         catch (IOException ex)
         {
            throw new WSException ("Cannot parse xml: " + xmlStr, ex);
         }
      }
      
      return xmlStr;
   }

   public void writeElement(SOAPElementImpl element)
   {
      writeElementInternal(element);
   }

   private void writeElementInternal(SOAPElementImpl element)
   {
      if (element != null)
      {
         try
         {
            if (writeXMLDeclaration == true && element instanceof SOAPEnvelope)
            {
               out.print("<?xml version='1.0'");
               if (charsetName != null)
                  out.print(" encoding='" + charsetName + "'");

               out.println("?>");
               writeXMLDeclaration = false;
            }

            element.writeElement(out);

            out.flush();
         }
         catch (IOException ex)
         {
            throw new WSException("Cannot write SOAP element", ex);
         }
      }
   }
}
