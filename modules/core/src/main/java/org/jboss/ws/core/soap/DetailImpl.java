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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A container for DetailEntry objects. DetailEntry objects give detailed error information that is application-specific
 * and related to the SOAPBody object that contains it.
 *
 * A Detail object, which is part of a SOAPFault object, can be retrieved using the method SOAPFault.getDetail.
 *
 * The Detail interface provides two methods. One creates a new DetailEntry object and also automatically adds
 * it to the Detail object. The second method gets a list of the DetailEntry objects contained in a Detail object.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class DetailImpl extends SOAPFaultElementImpl implements Detail
{
   // provide logging
   private static Logger log = Logger.getLogger(DetailImpl.class);

   /** Creates a SOAP 1.1 detail element. */
   public DetailImpl()
   {
      super("detail");
   }

   /** Creates a SOAP 1.2 prefix:Detail element. */
   public DetailImpl(String prefix, String namespace)
   {
      super("Detail", prefix, namespace);
   }

   /** Converts the given element to a Detail. */
   DetailImpl(SOAPElementImpl element)
   {
      super(element.getElementName());

      // altough detail schema does not define attributes, copy them for completeness 
      DOMUtils.copyAttributes(this, element);

      try
      {
         NodeList nodeList = element.getChildNodes();
         for (int i = 0; i < nodeList.getLength(); i++)
         {
            Node node = nodeList.item(i);
            if (node instanceof SOAPElement)
               addChildElement((SOAPElement)node);
            else
               appendChild(node);
         }
      }
      catch (SOAPException e)
      {
         throw new WSException("Unable to create fault detail", e);
      }
   }

   public DetailEntry addDetailEntry(Name name) throws SOAPException
   {
      DetailEntryImpl detailEntry = new DetailEntryImpl(name);
      addChildElement(detailEntry);
      return detailEntry;
   }

   public DetailEntry addDetailEntry(QName qname) throws SOAPException
   {
      DetailEntryImpl detailEntry = new DetailEntryImpl(qname);
      addChildElement(detailEntry);
      return detailEntry;
   }

   public Iterator getDetailEntries()
   {
      List<DetailEntry> list = new ArrayList<DetailEntry>();

      NodeList nodeList = getChildNodes();
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         org.w3c.dom.Node node = nodeList.item(i);
         if (node instanceof DetailEntry)
            list.add((DetailEntry)node);
      }

      return list.iterator();
   }

   @Override
   public SOAPElement addChildElement(SOAPElement child) throws SOAPException
   {
      if (!(child instanceof DetailEntry))
         child = convertToDetailEntry((SOAPElementImpl)child);

      return super.addChildElement(child);
   }

   private static DetailEntry convertToDetailEntry(SOAPElementImpl element)
   {
      element.detachNode();
      DetailEntryImpl detailEntry = new DetailEntryImpl(element);
      log.trace("convertToDetailEntry : " + detailEntry);
      return detailEntry;
   }
}
