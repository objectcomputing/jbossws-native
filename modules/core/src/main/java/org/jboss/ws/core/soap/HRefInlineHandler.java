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

import java.util.Iterator;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Text;

import org.jboss.logging.Logger;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Inline rpc/encoded hrefs
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 27-Mar-2007
 */
public class HRefInlineHandler
{
   // provide logging
   private static Logger log = Logger.getLogger(HRefInlineHandler.class);

   private SOAPFactoryImpl soapFactory = new SOAPFactoryImpl();
   private SOAPBodyImpl soapBody;

   public HRefInlineHandler(SOAPBodyImpl soapBody)
   {
      this.soapBody = soapBody;
   }

   public void processHRefs() throws SOAPException
   {
      String bodyStr = DOMWriter.printNode(soapBody, true);
      log.debug("Begin processHRefs:\n" + bodyStr);

      SOAPBodyElement soapBodyElement = soapBody.getBodyElement();
      processElement(soapBodyElement);
      
      // Process elements after SOAPBodyElement
      Iterator it = soapBody.getChildElements();
      while (it.hasNext())
      {
         Object next = it.next();
         if (next instanceof SOAPElement)
         {
            // Remove id elements
            SOAPElement soapElement = (SOAPElement)next;
            if ((soapElement instanceof SOAPBodyElement) == false)
               soapBody.removeChild(soapElement);
         }
      }

      bodyStr = DOMWriter.printNode(soapBody, true);
      log.debug("End processHRefs:\n" + bodyStr);
   }

   private void processElement(SOAPElement soapElement) throws SOAPException
   {
      // Do inner first outer last
      Iterator it = soapElement.getChildElements();
      while (it.hasNext())
      {
         Node childElement = (Node)it.next();
         if (childElement instanceof SOAPElement)
            processElement((SOAPElement)childElement);
      }

      String href = soapElement.getAttribute("href");
      if (href.length() > 0)
      {
         processHRef(soapElement, href);
         soapElement.removeAttribute("href");
      }
   }

   private void processHRef(SOAPElement hrefElement, String href) throws SOAPException
   {
      SOAPElement idElement = null;

      Iterator it = soapBody.getChildElements();
      while (it.hasNext())
      {
         Object next = it.next();
         if (next instanceof SOAPElement)
         {
            SOAPElement auxElement = (SOAPElement)next;
            if (href.equals("#" + auxElement.getAttribute("id")))
            {
               idElement = (SOAPElement)auxElement;
               break;
            }
         }
      }

      if (idElement == null)
         throw new IllegalStateException("Cannot get href element: " + href);

      // process nested hrefs
      processElement(idElement);

      // Copy most attributes, except id
      copyMostAttributes(hrefElement, idElement);

      // Append id element children
      if (DOMUtils.hasChildElements(idElement))
      {
         Iterator itid = idElement.getChildElements();
         while (itid.hasNext())
         {
            Node childNode = (Node)itid.next();
            if (childNode instanceof SOAPElement)
            {
               SOAPElement childClone = soapFactory.createElement((SOAPElement)childNode, true);
               hrefElement.addChildElement(childClone);
            }
            else if (childNode instanceof Text)
            {
               String value = childNode.getValue();
               hrefElement.setValue(value);
            }
         }
      }
      // If no children, copy the value
      else
      {
         String value = idElement.getValue();
         hrefElement.setValue(value);
      }
   }

   private void copyMostAttributes(Element destElement, Element srcElement)
   {
      NamedNodeMap attribs = srcElement.getAttributes();
      for (int i = 0; i < attribs.getLength(); i++)
      {
         Attr attr = (Attr)attribs.item(i);
         String uri = attr.getNamespaceURI();
         String qname = attr.getName();
         String value = attr.getNodeValue();

         // Do not copy the id attribute
         if ("id".equals(qname) == false)
            destElement.setAttributeNS(uri, qname, value);
      }
   }

}
