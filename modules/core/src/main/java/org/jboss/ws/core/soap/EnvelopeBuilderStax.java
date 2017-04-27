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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.jboss.util.NotImplementedException;
import org.w3c.dom.Element;

/**
 * A SOAPEnvelope builder for JAXRPC based on Stax
 *  
 * @author Heiko Braun, <heiko.braun@jboss.com>
 * @author Thomas.Diesler@jboss.com
 * @since 15-Apr-2006
 */
public class EnvelopeBuilderStax implements EnvelopeBuilder
{
   private static final String END_ELEMENT_BRACKET = "</";
   private static final String EMPTY_STRING = "";
   private static final String CLOSING_BRACKET = ">";
   private static final String START_ELEMENT_BRACKET = "<";
   private static final String HEADER_ELEMENT_NAME = "Header";
   private static final String BODY_ELEMENT_NAME = "Body";
   private static final String FAULT_ELEMENT_NAME = "Fault";

   private static enum Part
   {
      ENVELOPE, HEADER, BODY, FAULT, RPC_PAYLOAD, DOC_PAYLOAD, BARE_PAYLOAD
   }

   private Part currentPart = Part.ENVELOPE;
   private Part previousPart = null;

   // saaj
   private SOAPPartImpl soapPart;
   private SOAPEnvelopeImpl soapEnv;

   private StringBuffer fragmentBuffer;
   private QName fragmentRootCursor = null;
   private QName currentRootElement = null;
   private XMLStreamReader reader;

   private static XMLInputFactory factory;

   public EnvelopeBuilderStax()
   {
      resetFragmentBuffer();
   }

   private void resetFragmentBuffer()
   {
      this.fragmentBuffer = new StringBuffer();
      this.fragmentBuffer.ensureCapacity(2048);
   }

   public SOAPEnvelope build(SOAPMessage soapMessage, InputStream in, boolean ignoreParseError) throws IOException, SOAPException
   {
      try
      {
         reader = getFactoryInstance().createXMLStreamReader(in);
      }
      catch (XMLStreamException e)
      {
         throw new IOException("Failed to create stream reader:" + e.getMessage());
      }

      try
      {
         soapPart = (SOAPPartImpl)soapMessage.getSOAPPart();

         while (reader.hasNext())
         {

            if (reader.isStartElement())
            {
               processStartElement();
            }
            else if (reader.isCharacters())
            {
               processCharacters();
            }
            else if (reader.isEndElement())
            {
               processEndElement();
            }

            reader.next();
         }

      }
      catch (XMLStreamException e)
      {
         if (!ignoreParseError)
            throw new IOException("Failed to parse stream: " + e.getMessage());
      }
      finally
      {
         try
         {
            if (reader != null)
               reader.close();
         }
         catch (XMLStreamException e)
         {
            // ignore
         }
      }
      return soapEnv;
   }

   private static synchronized XMLInputFactory getFactoryInstance()
   {
      if (null == factory)
      {
         System.setProperty("javax.xml.stream.XMLInputFactory", "com.ctc.wstx.stax.WstxInputFactory");
         factory = XMLInputFactory.newInstance();
         factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
      }
      return factory;

   }

   private void processCharacters() throws SOAPException
   {
      if (fragmentRootCursor != null)
         consumeCharacters();
   }

   private void consumeCharacters() throws SOAPException
   {

      String text = normalize(reader.getText());

      if (!atPartMargin() && !reader.isWhiteSpace())
      {

         fragmentBuffer.append(text);

         if (Part.FAULT == currentPart)
         {
            String localName = currentRootElement.getLocalPart();
            SOAPFault fault = soapEnv.getBody().getFault();
            if ("faultcode".equalsIgnoreCase(localName))
               fault.setFaultCode(text);
            else if ("faultactor".equalsIgnoreCase(localName))
               fault.setFaultActor(text);
            else if ("faultstring".equalsIgnoreCase(localName))
               fault.setFaultString(text);
         }
      }
   }

   private void processEndElement() throws SOAPException
   {
      if (fragmentRootCursor != null)
         consumeEndElement();
   }

   private void consumeEndElement() throws SOAPException
   {

      QName qName = reader.getName();

      fragmentBuffer.append(END_ELEMENT_BRACKET);
      fragmentBuffer.append(getFQElementName(qName));
      fragmentBuffer.append(CLOSING_BRACKET);

      if (fragmentRootCursor != null && fragmentRootCursor.equals(qName))
      {
         flushBuffer();
         fragmentRootCursor = null;
      }
   }

   private void flushBuffer() throws SOAPException
   {
      if (Part.HEADER == currentPart)
      {
         SOAPHeader soapHeader = soapEnv.getHeader();
         SOAPContentElement lastHeaderElement = (SOAPContentElement)soapHeader.getChildNodes().item(soapHeader.getChildNodes().getLength() - 1);
         lastHeaderElement.setXMLFragment(bufferToFragment(fragmentBuffer));
      }
      else if (Part.BODY == currentPart)
      {
         SOAPBody soapBody = soapEnv.getBody();
         SOAPContentElement lastBodyElement = (SOAPContentElement)soapBody.getChildNodes().item(soapBody.getChildNodes().getLength() - 1);
         lastBodyElement.setXMLFragment(bufferToFragment(fragmentBuffer));
      }
      else if (Part.FAULT == currentPart)
      {
         SOAPBody soapBody = soapEnv.getBody();
         SOAPContentElement faultElement = (SOAPContentElement)soapBody.getFault();
         faultElement.setXMLFragment(bufferToFragment(fragmentBuffer));
      }

      resetFragmentBuffer();
   }

   // TODO: this is rubbish. Use Source internally instead...
   private XMLFragment bufferToFragment(StringBuffer fragmentBuffer) {
      StreamSource source = new StreamSource(new ByteArrayInputStream(fragmentBuffer.toString().getBytes()));
      return new XMLFragment(source);
   }

   private void processStartElement() throws SOAPException
   {

      QName qName = reader.getName();
      currentRootElement = qName;

      // identify current envelope part
      togglePartMargin(qName);

      // toggle current element
      Element destElement = null;
      if (Part.ENVELOPE == currentPart)
      {
         // setup envelope impl
         soapEnv = new SOAPEnvelopeImpl(soapPart, qName.getNamespaceURI(), false);
         destElement = soapEnv; // soapEnv becomes current
      }
      else if (Part.HEADER == currentPart)
      {
         if (atPartMargin())
         {
            // the env:Header element itself
            SOAPHeader soapHeader = soapEnv.getHeader();
            destElement = soapHeader; // header becomes current
            previousPart = Part.HEADER;
         }
         else
         {
            // child element of env:Header
            if (fragmentRootCursor == null)
            {
               Name name = new NameImpl(qName.getLocalPart(), qName.getPrefix(), qName.getNamespaceURI());
               SOAPContentElement headerElement = new SOAPHeaderElementImpl(name);
               soapEnv.getHeader().addChildElement(headerElement);

               destElement = headerElement; // headerElement becomes current
               fragmentRootCursor = qName;
            }

            consumeStartElement();
         }
      }
      else if (Part.BODY == currentPart)
      {

         SOAPBody soapBody = soapEnv.getBody();

         if (atPartMargin())
         {
            // the env:Body element
            destElement = soapBody;
            previousPart = Part.BODY;
         }
         else
         {
            // payload not fault
            Name bodyElementName = new NameImpl(qName.getLocalPart(), qName.getPrefix(), qName.getNamespaceURI());

            if (fragmentRootCursor == null)
            {
               SOAPBodyElementDoc docBodyElement = new SOAPBodyElementDoc(bodyElementName);
               docBodyElement = (SOAPBodyElementDoc)soapBody.addChildElement(docBodyElement);

               destElement = docBodyElement;
               fragmentRootCursor = qName;
            }

            consumeStartElement();
         }
      }
      else if (Part.FAULT == currentPart)
      {
         // payload is fault
         if (atPartMargin())
         {
            SOAPBody soapBody = soapEnv.getBody();
            SOAPFaultImpl soapFault = new SOAPFaultImpl(soapEnv.getPrefix(), soapEnv.getNamespaceURI());
            soapBody.addChildElement(soapFault);
            destElement = soapFault;
            previousPart = Part.FAULT;
         }

         if (fragmentRootCursor == null)
         {
            fragmentRootCursor = qName;
         }

         consumeStartElement();
      }

      if (fragmentRootCursor == null) // constructing soap elements
      {
         copyAttributes(destElement);
      }
   }

   private void togglePartMargin(QName qName)
   {
      // identify the current part
      if (qName.getLocalPart().equalsIgnoreCase(HEADER_ELEMENT_NAME))
      {
         previousPart = currentPart;
         currentPart = Part.HEADER;
      }
      else if (qName.getLocalPart().equalsIgnoreCase(BODY_ELEMENT_NAME))
      {
         previousPart = currentPart;
         currentPart = Part.BODY;
      }
      else if (qName.getLocalPart().equalsIgnoreCase(FAULT_ELEMENT_NAME))
      {
         previousPart = currentPart;
         currentPart = Part.FAULT;
      }
   }

   private void consumeStartElement()
   {

      QName qName = reader.getName();

      // element
      fragmentBuffer.append(START_ELEMENT_BRACKET);
      fragmentBuffer.append(getFQElementName(qName));

      // local namespaces
      for (int x = 0; x < reader.getNamespaceCount(); x++)
      {
         if (reader.getNamespacePrefix(x) != null)
         {
            fragmentBuffer.append(" xmlns:");
            fragmentBuffer.append(reader.getNamespacePrefix(x)).append("='");
            fragmentBuffer.append(reader.getNamespaceURI(x)).append("'");
         }
         else if (reader.getNamespaceURI(x) != null)
         {
            fragmentBuffer.append(" xmlns='");
            fragmentBuffer.append(reader.getNamespaceURI(x)).append("'");
         }
      }

      // attributes
      if (reader.getAttributeCount() > 0)
      {
         for (int i = 0; i < reader.getAttributeCount(); i++)
         {
            QName attQName = reader.getAttributeName(i);
            fragmentBuffer.append(" ").append(getFQElementName(attQName));
            fragmentBuffer.append("='").append(reader.getAttributeValue(i)).append("'");
         }
      }

      fragmentBuffer.append(CLOSING_BRACKET);
   }

   private String getFQElementName(QName qName)
   {
      return !qName.getPrefix().equals(EMPTY_STRING) ? qName.getPrefix() + ":" + qName.getLocalPart() : qName.getLocalPart();
   }

   private void copyAttributes(Element destElement)
   {

      if (reader.getAttributeCount() == 0)
         return;

      for (int i = 0; i < reader.getAttributeCount(); i++)
      {
         destElement.setAttributeNS(reader.getAttributeNamespace(i), reader.getAttributeLocalName(i), reader.getAttributeValue(i));
      }
   }

   private boolean atPartMargin()
   {
      return previousPart != currentPart;
   }

   private static String normalize(String valueStr)
   {
      // We assume most strings will not contain characters that need "escaping",
      // and optimize for this case.
      boolean found = false;
      int i = 0;

      outer: for (; i < valueStr.length(); i++)
      {
         switch (valueStr.charAt(i))
         {
            case '<':
            case '>':
            case '&':
            case '"':
               found = true;
               break outer;
         }
      }

      if (!found)
         return valueStr;

      // Resume where we left off
      StringBuilder builder = new StringBuilder();
      builder.append(valueStr.substring(0, i));
      for (; i < valueStr.length(); i++)
      {
         char c = valueStr.charAt(i);
         switch (c)
         {
            case '<':
               builder.append("&lt;");
               break;
            case '>':
               builder.append("&gt;");
               break;
            case '&':
               builder.append("&amp;");
               break;
            case '"':
               builder.append("&quot;");
               break;
            default:
               builder.append(c);
         }
      }

      return builder.toString();
   }

   public SOAPEnvelope build(SOAPMessage soapMessage, Reader reader, boolean ignoreParseError) throws IOException, SOAPException
   {
      throw new NotImplementedException();
   }

   public SOAPEnvelope build(SOAPMessage soapMessage, Element domEnv) throws SOAPException
   {
      throw new NotImplementedException();
   }

   public SOAPBodyElement buildBodyElementDoc(SOAPBodyImpl soapBody, Element domBodyElement) throws SOAPException
   {
      throw new NotImplementedException();
   }

   public SOAPBodyElement buildBodyElementRpc(SOAPBodyImpl soapBody, Element domBodyElement) throws SOAPException
   {
      throw new NotImplementedException();
   }

   public Style getStyle()
   {
      throw new NotImplementedException();
   }

   public void setStyle(Style style)
   {
      throw new NotImplementedException();
   }
}
