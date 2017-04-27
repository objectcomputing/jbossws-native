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
package org.jboss.ws.core.utils;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

/**
 * SAAJ utilities.
 * @author <a href="mailto:alex.guizar@jboss.com">Alejandro Guizar</a>
 */
public class SAAJUtils
{
   /** Set the qname value to the given element.
    * @param value a namespace qualified name; its namespace name MUST NOT be empty
    */
   public static void setQualifiedElementValue(SOAPElement element, QName value) throws SOAPException 
   {
      String prefix = ensureNamespaceDeclared(element, value.getPrefix(), value.getNamespaceURI());
      element.setValue(prefix + ':' + value.getLocalPart());
   }

   /** Set the qname value to the specified attribute of the given element.
    * @param value a namespace qualified name; its namespace name MUST NOT be empty
    */
   public static void setQualifiedAttributeValue(SOAPElement element, String attributeName, QName value) throws SOAPException
   {
      String prefix = ensureNamespaceDeclared(element, value.getPrefix(), value.getNamespaceURI());
      element.setAttribute(attributeName, prefix + ':' + value.getLocalPart());
   }

   /** Ensures the given namespace is declared in the scope of the given element.
    */
   private static String ensureNamespaceDeclared(SOAPElement element, String prefix, String nsURI) throws SOAPException
   {
      if (prefix.length() == 0)
      {
         // no given prefix, find prefix currently associated to given URI 
         prefix = getNamespacePrefix(element, nsURI);
         if (prefix == null)
         {
            // no prefix currently associated to given URI, declare namespace locally
            prefix = "valueNS";
            element.addNamespaceDeclaration(prefix, nsURI);
         }
      }
      // verify given prefix is associated to given URI
      else if (!nsURI.equals(element.getNamespaceURI(prefix)))
      {
         // prefix is associated with other/no URI, declare namespace locally
         element.addNamespaceDeclaration(prefix, nsURI);
      }
      return prefix;
   }

   /**
    * Returns the prefix of the namespace that has the given URI.
    * @param nsURI the URI of the namespace to search for
    * @return the prefix of the namespace or <code>null</code> if not found
    */
   public static String getNamespacePrefix(SOAPElement element, String nsURI)
   {
      Iterator it = element.getVisibleNamespacePrefixes();
      while (it.hasNext())
      {
         String prefix = (String)it.next();
         if (nsURI.equals(element.getNamespaceURI(prefix)))
            return prefix;
      }
      return null;
   }
}
