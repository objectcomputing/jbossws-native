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
package org.jboss.ws.core.jaxws.handler;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.WebServiceException;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.WSException;
import org.jboss.ws.core.HTTPMessageImpl;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.soap.EnvelopeBuilder;
import org.jboss.ws.core.soap.EnvelopeBuilderDOM;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.SOAPBodyImpl;
import org.jboss.ws.core.soap.SOAPContentElement;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.core.soap.XMLFragment;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.spi.util.ServiceLoader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The LogicalMessageContext interface extends MessageContext to provide access to a the 
 * contained message as a protocol neutral LogicalMessage.
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 31-Aug-2006
 */
public class LogicalMessageImpl implements LogicalMessage
{
   // provide logging
   private static final Logger log = Logger.getLogger(LogicalMessageImpl.class);

   private Style style;
   private MessageAbstraction message;

   public LogicalMessageImpl(MessageAbstraction message, Style style)
   {
      this.style = style;
      this.message = message;
   }

   public Source getPayload()
   {
      Source source = null;
      if (message instanceof SOAPMessage)
      {
         SOAPMessage soapMessage = (SOAPMessage)message;
         SOAPBodyImpl soapBody = getSOAPBody(soapMessage);
         SOAPElement bodyElement = getBodyElement(soapBody);

         if (style == Style.RPC)
         {
            source = new DOMSource(bodyElement);
         }
         else
         {
            SOAPContentElement contentElement = (SOAPContentElement)bodyElement;
            source = contentElement.getXMLFragment().getSource();
         }
      }
      else if (message instanceof HTTPMessageImpl)
      {
         HTTPMessageImpl httpMessage = (HTTPMessageImpl)message;
         source = httpMessage.getXmlFragment().getSource();
      }
      return source;
   }

   public void setPayload(Source source)
   {
      if (message instanceof SOAPMessage)
      {
         SOAPMessage soapMessage = (SOAPMessage)message;
         SOAPBodyImpl soapBody = getSOAPBody(soapMessage);
         SOAPElement bodyElement = getBodyElement(soapBody);
         try
         {
            if (style == Style.RPC)
            {
               try
               {
                  soapBody.removeContents();
                  EnvelopeBuilder envBuilder = (EnvelopeBuilder)ServiceLoader.loadService(EnvelopeBuilder.class.getName(), EnvelopeBuilderDOM.class.getName());
                  envBuilder.setStyle(style);
                  Element domBodyElement = DOMUtils.sourceToElement(source);
                  envBuilder.buildBodyElementRpc(soapBody, domBodyElement);
               }
               catch (IOException ex)
               {
                  WSException.rethrow(ex);
               }
            }
            else
            {
               SOAPContentElement contentElement = (SOAPContentElement)bodyElement;
               contentElement.setXMLFragment(new XMLFragment(source));
            }
         }
         catch (SOAPException ex)
         {
            throw new WebServiceException("Cannot set xml payload", ex);
         }
      }
      else if (message instanceof HTTPMessageImpl)
      {
         HTTPMessageImpl httpMessage = (HTTPMessageImpl)message;
         httpMessage.setXmlFragment(new XMLFragment(source));
      }

      MessageContextAssociation.peekMessageContext().setModified(true);

   }

   public Object getPayload(JAXBContext jaxbContext)
   {
      Object payload = null;
      if (message instanceof SOAPMessage)
      {
         SOAPMessage soapMessage = (SOAPMessage)message;
         SOAPBodyImpl soapBody = getSOAPBody(soapMessage);

         SOAPContentElement bodyElement = (SOAPContentElement)getBodyElement(soapBody);
         if (bodyElement != null)
         {
            payload = bodyElement.getObjectValue();
         }
      }
      else if (message instanceof HTTPMessageImpl)
      {
         throw new NotImplementedException();
      }
      return payload;
   }

   public void setPayload(Object payload, JAXBContext jaxbContext)
   {
      if (message instanceof SOAPMessage)
      {
         SOAPMessage soapMessage = (SOAPMessage)message;
         SOAPBodyImpl soapBody = getSOAPBody(soapMessage);

         SOAPContentElement bodyElement = (SOAPContentElement)getBodyElement(soapBody);
         if (bodyElement != null)
         {
            bodyElement.setObjectValue(payload);
            MessageContextAssociation.peekMessageContext().setModified(true);
         }
      }
      else if (message instanceof HTTPMessageImpl)
      {
         throw new NotImplementedException();
      }
   }

   private SOAPElement getBodyElement(final SOAPBodyImpl soapBody)
   {
      SOAPElement bodyElement = null;

      NodeList nodes = soapBody.getChildNodes();
      for (int i = 0; i < nodes.getLength() && bodyElement == null; i++)
      {
         Node current = nodes.item(i);
         if (current instanceof SOAPElement)
         {
            bodyElement = (SOAPElement)current;
         }
      }

      return bodyElement;
   }

   private SOAPBodyImpl getSOAPBody(SOAPMessage soapMessage)
   {
      SOAPBodyImpl soapBody = null;
      try
      {
         soapBody = (SOAPBodyImpl)soapMessage.getSOAPBody();
      }
      catch (SOAPException ex)
      {
         WSException.rethrow(ex);
      }
      return soapBody;
   }
}
