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
package org.jboss.ws.extensions.wsrm.common.serialization;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import org.w3c.dom.Element;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.ws.extensions.wsrm.api.RMException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

/**
 * Serialization helper
 * @author richard.opalka@jboss.com
 */
final class RMSerializationHelper
{
   
   private RMSerializationHelper()
   {
      // no instances
   }
   
   public static String getRequiredTextContent(SOAPElement element, QName elementQName)
   {
      if (!DOMUtils.hasTextChildNodesOnly(element))
         throw new RMException(
            "Only text content is allowed for element " + elementQName);

      return DOMUtils.getTextContent(element).trim();
   }
   
   public static SOAPElement getRequiredElement(SOAPElement element, QName requiredQName, QName contextQName)
   {
      return (SOAPElement)getRequiredElement(element, requiredQName, contextQName.toString());
   }
   
   public static SOAPElement getRequiredElement(SOAPElement element, QName requiredQName, String context)
   {
      List<Element> childElements = DOMUtils.getChildElementsAsList(element, requiredQName);

      if (childElements.size() < 1)
         throw new RMException(
            "Required " + requiredQName + " element not found in " + context + " element");
      
      if (childElements.size() > 1)
         throw new RMException(
            "Only one " + requiredQName + " element can be present in " + context + " element");
      
      return (SOAPElement)childElements.get(0);
   }
   
   public static String getRequiredTextContent(SOAPElement element, QName attributeQName, QName elementQName)
   {
      String attributeValue = element.getAttributeValue(attributeQName);
      
      if (attributeValue == null)
         throw new RMException(
            "Required attribute " + attributeQName + " is missing in element " + elementQName);

      return attributeValue;
   }
   
   public static SOAPElement getOptionalElement(SOAPElement contextElement, QName optionalQName, QName contextQName)
   {
      List<Element> list = DOMUtils.getChildElementsAsList(contextElement, optionalQName);

      if (list.size() > 1)
         throw new RMException(
            "At most one " + optionalQName + " element can be present in " + contextQName + " element");
      
      return (SOAPElement)((list.size() == 1) ? list.get(0) : null);
   }
   
   public static List<SOAPElement> getOptionalElements(SOAPElement contextElement, QName optionalQName, QName contextQName)
   {
      List<Element> temp = DOMUtils.getChildElementsAsList(contextElement, optionalQName);
      
      if (temp.size() == 0)
      {
         return Collections.emptyList();
      }
      else
      {
         List<SOAPElement> retVal = new LinkedList<SOAPElement>();
         
         for (Element e : temp)
         {
            retVal.add((SOAPElement)e);
         }
         
         return retVal;
      }
   }
   
   public static long stringToLong(String toEvaluate, String errorMessage)
   {
      try
      {
         return Long.valueOf(toEvaluate);
      }
      catch (NumberFormatException nfe)
      {
         throw new RMException(errorMessage, nfe);
      }
   }
   
}
