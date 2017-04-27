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
import java.io.InputStream;
import java.io.Reader;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonSOAPFaultException;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A SOAPEnvelope builder for JAXRPC based on DOM 
 * 
 * @author Heiko Braun, <heiko.braun@jboss.com>
 * @author Thomas.Diesler@jboss.com
 * @since 19-Apr-2006
 */
public class EnvelopeBuilderDOM implements EnvelopeBuilder
{
   // provide logging
   private static Logger log = Logger.getLogger(EnvelopeBuilderDOM.class);

   private SOAPFactoryImpl soapFactory = new SOAPFactoryImpl();
   private Style style;

   public Style getStyle()
   {
      return style;
   }

   public void setStyle(Style style)
   {
      this.style = style;
   }

   public SOAPEnvelope build(SOAPMessage soapMessage, InputStream ins, boolean ignoreParseError) throws IOException, SOAPException
   {
      // Parse the XML input stream
      Element domEnv = null;
      try
      {
         InputSource inputSource = new InputSource(ins);
         inputSource.setEncoding(getEncoding(soapMessage));
         domEnv = DOMUtils.parse(inputSource);
      }
      catch (IOException ex)
      {
         if (ignoreParseError)
         {
            return null;
         }
         QName faultCode = Constants.SOAP11_FAULT_CODE_CLIENT;
         throw new CommonSOAPFaultException(faultCode, ex.getMessage());
      }

      return build(soapMessage, domEnv);
   }

   public SOAPEnvelope build(SOAPMessage soapMessage, Reader reader, boolean ignoreParseError) throws IOException, SOAPException
   {
      // Parse the XML input stream
      Element domEnv = null;
      try
      {
         InputSource inputSource = new InputSource( reader );
         inputSource.setEncoding(getEncoding(soapMessage));
         domEnv = DOMUtils.parse(inputSource);
      }
      catch (IOException ex)
      {
         if (ignoreParseError)
         {
            return null;
         }
         QName faultCode = Constants.SOAP11_FAULT_CODE_CLIENT;
         throw new CommonSOAPFaultException(faultCode, ex.getMessage());
      }

      return build(soapMessage, domEnv);
   }

   private String getEncoding(SOAPMessage soapMessage) throws SOAPException
   {
      String encoding = (String)soapMessage.getProperty(SOAPMessage.CHARACTER_SET_ENCODING);
      if (encoding == null)
      {
         encoding = "UTF-8";
      }
      return encoding;  
   }
   
   public SOAPEnvelope build(SOAPMessage soapMessage, Element domEnv) throws SOAPException
   {
      // Construct the envelope
      SOAPPartImpl soapPart = (SOAPPartImpl)soapMessage.getSOAPPart();
      SOAPEnvelopeImpl soapEnv = new SOAPEnvelopeImpl(soapPart, soapFactory.createElement(domEnv, false), false);

      DOMUtils.copyAttributes(soapEnv, domEnv);

      NodeList envChildNodes = domEnv.getChildNodes();
      for (int i = 0; i < envChildNodes.getLength(); i++)
      {
         Node child = envChildNodes.item(i);
         short childType = child.getNodeType();
         if (childType == Node.ELEMENT_NODE)
         {
            String elName = child.getLocalName();
            if ("Header".equals(elName))
            {
               buildSOAPHeader(soapEnv, (Element)child);
            }
            else if ("Body".equals(elName))
            {
               buildSOAPBody(soapEnv, (Element)child);
            }
            else
            {
               log.warn("Ignore envelope child: " + elName);
            }
         }
      }

      return soapEnv;
   }

   private SOAPHeader buildSOAPHeader(SOAPEnvelopeImpl soapEnv, Element domHeader) throws SOAPException
   {
      SOAPHeader soapHeader = soapEnv.getHeader();
      if (soapHeader == null)
         soapHeader = soapEnv.addHeader();

      DOMUtils.copyAttributes(soapHeader, domHeader);

      NodeList headerChildNodes = domHeader.getChildNodes();
      for (int i = 0; i < headerChildNodes.getLength(); i++)
      {
         Node child = headerChildNodes.item(i);
         short childType = child.getNodeType();
         if (childType == Node.ELEMENT_NODE)
         {
            Element srcElement = (Element)child;
            XMLFragment xmlFragment = new XMLFragment(new DOMSource(srcElement));

            Name name = new NameImpl(srcElement.getLocalName(), srcElement.getPrefix(), srcElement.getNamespaceURI());
            SOAPContentElement destElement = new SOAPHeaderElementImpl(name);
            soapHeader.addChildElement(destElement);

            DOMUtils.copyAttributes(destElement, srcElement);
            destElement.setXMLFragment(xmlFragment);
         }
         else
         {
            log.warn("Ignore child type: " + childType);
         }
      }

      return soapHeader;
   }

   private SOAPBody buildSOAPBody(SOAPEnvelopeImpl soapEnv, Element domBody) throws SOAPException
   {
      String envNS = soapEnv.getNamespaceURI();

      SOAPBodyImpl soapBody = (SOAPBodyImpl)soapEnv.getBody();
      if (soapBody == null)
         soapBody = (SOAPBodyImpl)soapEnv.addBody();

      DOMUtils.copyAttributes(soapBody, domBody);

      SOAPBodyElement soapBodyElement = null;
      boolean attachHRefElements = Constants.URI_SOAP11_ENC.equals(soapEnv.getAttributeNS(envNS, "encodingStyle"));

      NodeList bodyChildNodes = domBody.getChildNodes();
      for (int i = 0; i < bodyChildNodes.getLength(); i++)
      {
         Node child = bodyChildNodes.item(i);
         short childType = child.getNodeType();
         if (childType == Node.ELEMENT_NODE)
         {
            if (soapBodyElement == null)
            {
               soapBodyElement = buildSOAPBodyElement(soapEnv, (Element)child);
               attachHRefElements = attachHRefElements || Constants.URI_SOAP11_ENC.equals(soapBody.getAttributeNS(envNS, "encodingStyle"));
            }
            else if (attachHRefElements)
            {
               // Process additional soap encoded body elements
               soapBody.addChildElement(soapFactory.createElement((Element)child, true));
            }
         }
         else if (childType == Node.COMMENT_NODE)
         {
            appendCommentNode(soapBody, child);
         }
         else if (childType == Node.TEXT_NODE)
         {
            appendTextNode(soapBody, child);
         }
         else
         {
            log.warn("Ignore child type: " + childType);
         }
      }

      // Inline all attached href elements
      if (attachHRefElements)
      {
         HRefInlineHandler inlineHandler = new HRefInlineHandler(soapBody);
         inlineHandler.processHRefs();
      }

      return soapBody;
   }

   private SOAPBodyElement buildSOAPBodyElement(SOAPEnvelopeImpl soapEnv, Element domBodyElement) throws SOAPException
   {
      String envNS = soapEnv.getNamespaceURI();
      String envPrefix = soapEnv.getPrefix();

      SOAPBodyImpl soapBody = (SOAPBodyImpl)soapEnv.getBody();
      QName beName = DOMUtils.getElementQName(domBodyElement);

      SOAPBodyElement soapBodyElement = null;

      // Process a <env:Fault> message
      if (beName.equals(new QName(envNS, "Fault")))
      {
         SOAPFaultImpl soapFault = new SOAPFaultImpl(envPrefix, envNS);
         soapBody.addChildElement(soapFault);
         soapBodyElement = soapFault;

         DOMUtils.copyAttributes(soapFault, domBodyElement);

         // copy everything and let soapFault discover child elements itself
         XMLFragment xmlFragment = new XMLFragment(new DOMSource(domBodyElement));
         soapFault.setXMLFragment(xmlFragment);
      }

      // Process and RPC or DOCUMENT style message
      else
      {
         if (style == Style.DOCUMENT)
         {
            buildBodyElementDoc(soapBody, domBodyElement);
         }
         else if (style == Style.RPC)
         {
            soapBodyElement = buildBodyElementRpc(soapBody, domBodyElement);
         }
         else if (style == null)
         {
            buildBodyElementDefault(soapBody, domBodyElement);
         }
         else
         {
            throw new WSException("Unsupported message style: " + style);
         }
      }

      return soapBodyElement;
   }

   public SOAPBodyElement buildBodyElementDoc(SOAPBodyImpl soapBody, Element domBodyElement) throws SOAPException
   {
      Element srcElement = (Element)domBodyElement;

      QName beName = DOMUtils.getElementQName(domBodyElement);
      SOAPBodyElementDoc soapBodyElement = new SOAPBodyElementDoc(beName);
      SOAPContentElement contentElement = (SOAPContentElement)soapBody.addChildElement(soapBodyElement);

      DOMUtils.copyAttributes(contentElement, srcElement);

      XMLFragment xmlFragment = new XMLFragment(new DOMSource(srcElement));
      contentElement.setXMLFragment(xmlFragment);

      return soapBodyElement;
   }

   public SOAPBodyElement buildBodyElementRpc(SOAPBodyImpl soapBody, Element domBodyElement) throws SOAPException
   {
      QName beName = DOMUtils.getElementQName(domBodyElement);
      SOAPBodyElementRpc soapBodyElement = new SOAPBodyElementRpc(beName);
      soapBodyElement = (SOAPBodyElementRpc)soapBody.addChildElement(soapBodyElement);

      DOMUtils.copyAttributes(soapBodyElement, domBodyElement);

      NodeList nlist = domBodyElement.getChildNodes();
      for (int i = 0; i < nlist.getLength(); i++)
      {
         Node child = nlist.item(i);
         short childType = child.getNodeType();
         if (childType == Node.ELEMENT_NODE)
         {
            Element srcElement = (Element)child;
            Name name = new NameImpl(srcElement.getLocalName(), srcElement.getPrefix(), srcElement.getNamespaceURI());
            SOAPContentElement destElement = new SOAPContentElement(name);
            destElement = (SOAPContentElement)soapBodyElement.addChildElement(destElement);

            DOMUtils.copyAttributes(destElement, srcElement);

            XMLFragment xmlFragment = new XMLFragment(new DOMSource(srcElement));
            destElement.setXMLFragment(xmlFragment);
         }
         else if (childType == Node.COMMENT_NODE)
         {
            appendCommentNode(soapBodyElement, child);
         }
         else if (childType == Node.TEXT_NODE)
         {
            appendTextNode(soapBodyElement, child);
         }
         else
         {
            log.warn("Ignore child type: " + childType);
         }
      }

      return soapBodyElement;
   }

   public SOAPBodyElement buildBodyElementDefault(SOAPBodyImpl soapBody, Element domBodyElement) throws SOAPException
   {
      QName beName = DOMUtils.getElementQName(domBodyElement);
      SOAPBodyElement soapBodyElement = new SOAPBodyElementMessage(beName);
      soapBodyElement = (SOAPBodyElementMessage)soapBody.addChildElement(soapBodyElement);

      DOMUtils.copyAttributes(soapBodyElement, domBodyElement);

      NodeList nlist = domBodyElement.getChildNodes();
      for (int i = 0; i < nlist.getLength(); i++)
      {
         Node child = nlist.item(i);
         short childType = child.getNodeType();
         if (childType == Node.ELEMENT_NODE)
         {
            SOAPElement soapElement = soapFactory.createElement((Element)child);
            soapBodyElement.addChildElement(soapElement);
         }
         else if (childType == Node.COMMENT_NODE)
         {
            appendCommentNode(soapBodyElement, child);
         }
         else if (childType == Node.TEXT_NODE)
         {
            appendTextNode(soapBodyElement, child);
         }
         else if (childType == Node.CDATA_SECTION_NODE)
         {
            String nodeValue = child.getNodeValue();
            soapBodyElement.addTextNode(nodeValue);
         }
         else
         {
            log.warn("Ignore child type: " + childType);
         }
      }

      return soapBodyElement;
   }

   private void appendCommentNode(SOAPElement soapElement, Node child)
   {
      String nodeValue = child.getNodeValue();
      Document ownerDoc = soapElement.getOwnerDocument();
      Comment comment = ownerDoc.createComment(nodeValue);
      soapElement.appendChild(comment);
   }

   private void appendTextNode(SOAPElement soapElement, Node child) throws SOAPException
   {
      String nodeValue = child.getNodeValue();
      soapElement.addTextNode(nodeValue);
   }
}
