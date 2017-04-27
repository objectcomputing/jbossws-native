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

import java.io.IOException;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.jboss.ws.WSException;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * A factory for Policy construction 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 13-Nov-2005
 */
public class PolicyFactory
{
   // PolicyFactory Singelton
   private static PolicyFactory factory = new PolicyFactory();
   
   // Hide constructor
   private PolicyFactory()
   {
   }

   public static PolicyFactory newInstance() 
   {
      return factory;
   }

   public Policy createPolicy(String strPolicy)
   {
      try
      {
         return createPolicy(DOMUtils.parse(strPolicy));
      }
      catch (IOException ex)
      {
         throw new IllegalArgumentException("Cannot parse: " + strPolicy, ex);
      }
   }

   public Policy createPolicy(Element elPolicy)
   {
      // Work with a cloned copy, so parsing does not effect in input node
      elPolicy = (Element)elPolicy.cloneNode(true);
      
      Policy policy = new Policy();
      policy.setTargetNamespace(DOMUtils.getAttributeValue(elPolicy, "TargetNamespace"));
      policy.setBaseURI(DOMUtils.getAttributeValue(elPolicy, "xml:base"));
      policy.setID(DOMUtils.getAttributeValue(elPolicy, new QName(Policy.URI_SECURITY_UTILITY, "Id")));

      // Register namespaces
      NamedNodeMap attribs = elPolicy.getAttributes();
      for (int i = 0; i < attribs.getLength(); i++)
      {
         Attr attr = (Attr)attribs.item(i);
         String attrName = attr.getName();
         String attrValue = attr.getValue();
         if (attrName.startsWith("xmlns:"))
         {
            String prefix = attrName.substring(6);
            policy.getNamespaceRegistry().registerURI(attrValue, prefix);
         }
      }

      // Parse wsp:ExactlyOne
      QName oneQName = new QName(Policy.URI_POLICY, "ExactlyOne");
      Element elExactlyOne = DOMUtils.getFirstChildElement(elPolicy, oneQName);
      if (elExactlyOne == null)
         throw new WSException("Cannot find child element: " + oneQName);

      // Parse wsp:All
      QName allQName = new QName(Policy.URI_POLICY, "All");
      Element elAll = DOMUtils.getFirstChildElement(elExactlyOne, allQName);
      if (elAll == null)
         throw new WSException("Cannot find child element: " + allQName);

      Iterator it = DOMUtils.getChildElements(elExactlyOne, allQName);
      while (it.hasNext())
      {
         elAll = (Element)it.next();
         PolicyAlternative all = PolicyAlternative.parse(elAll);
         policy.addPolicyAlternative(all);
      }

      return policy;
   }
}
