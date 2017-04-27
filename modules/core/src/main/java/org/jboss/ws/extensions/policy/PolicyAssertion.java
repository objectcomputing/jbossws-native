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
package org.jboss.ws.extensions.policy;

import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** 
 * A policy assertion identifies a behavior that is a requirement (or capability) of a policy
 * subject. Assertions indicate domain-specific (e.g., security, transactions) semantics and
 * are expected to be defined in separate, domain-specific specifications.
 * 
 * Assertions are strongly typed. The type is identified only by the XML Infoset
 * [namespace name] and [local name] properties (that is, the qualified name or
 * QName) of the root Element Information Item representing the assertion. Assertions of
 * a given type MUST be consistently interpreted independent of their policy subjects.
 * 
 * The XML Infoset of an assertion MAY contain a non-empty [attributes] property and/or
 * a non-empty [children] property. Such content MAY be used to parameterize the
 * behavior indicated by the assertion. For example, an assertion identifying support for a
 * specific reliable messaging mechanism might include an Attribute Information Item to
 * indicate how long an endpoint will wait before sending an acknowledgement. However,
 * additional assertion content is not required when the identity of the root Element
 * Information Item alone is enough to convey the requirement (capability).
 *
 * @author Thomas.Diesler@jboss.org
 * @since 13-Nov-2005
 */
public class PolicyAssertion
{
   private Element assertionElement;
   private String nameSpace;

   PolicyAssertion(Element element)
   {
      Document doc = DOMUtils.getOwnerDocument();
      this.assertionElement = (Element)doc.adoptNode(element);
      this.nameSpace = assertionElement.getNamespaceURI();
   }

   public Element getElement()
   {
      return assertionElement;
   }

   public String toXMLString(boolean pretty)
   {
      return DOMWriter.printNode(assertionElement, pretty);
   }
   
   public String toString()
   {
      return toXMLString(true);
   }
   
   public String getNameSpace()
   {
      return nameSpace;
   }
}
