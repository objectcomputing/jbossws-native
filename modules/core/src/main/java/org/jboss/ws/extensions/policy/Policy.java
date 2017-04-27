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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jboss.ws.WSException;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.xb.binding.NamespaceRegistry;
import org.w3c.dom.Element;

/** 
 * At the abstract level a policy is a potentially empty collection of policy alternatives. A
 * policy with zero alternatives contains no choices; a policy with one or more alternatives
 * indicates choice in requirements (or capabilities) within the policy.
 * 
 * Alternatives are not ordered, and thus aspects such as preferences between alternatives
 * in a given context are beyond the scope of this specification.
 * 
 * Alternatives within a policy may differ significantly in terms of the behaviors they
 * indicate. Conversely, alternatives within a policy may be very similar. In either case, the
 * value or suitability of an alternative is generally a function of the semantics of assertions
 * within the alternative.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 13-Nov-2005
 */
public class Policy
{
   public static final String URI_POLICY = "http://schemas.xmlsoap.org/ws/2004/09/policy";
   public static final String URI_SECURITY_UTILITY = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

   // The target namespace
   private String targetNamespace;
   // The optional base URI
   private String baseURI;
   // The optional ID
   private String id;
   // The policy alternatives
   private Collection<PolicyAlternative> alternatives = new ArrayList<PolicyAlternative>();
   // The namespace registry for the policy
   private NamespaceRegistry nsRegistry = new NamespaceRegistry();

   // Hide constructor
   Policy()
   {
   }

   public NamespaceRegistry getNamespaceRegistry()
   {
      return nsRegistry;
   }

   public String getTargetNamespace()
   {
      return targetNamespace;
   }

   public void setTargetNamespace(String targetNamespace)
   {
      this.targetNamespace = targetNamespace;
   }

   public String getBaseURI()
   {
      return baseURI;
   }

   public void setBaseURI(String baseURI)
   {
      this.baseURI = baseURI;
   }

   public String getID()
   {
      return id;
   }

   public void setID(String id)
   {
      this.id = id;
   }

   public void addPolicyAlternative(PolicyAlternative polAlternative)
   {
      alternatives.add(polAlternative);
   }

   public Collection<PolicyAlternative> getPolicyAlternatives()
   {
      return new ArrayList<PolicyAlternative>(alternatives);
   }
   
   public void clearPolicyAlternatives()
   {
      alternatives.clear();
   }

   public Element toElement()
   {
      String xmlString = toXMLString(false);
      try
      {
         return DOMUtils.parse(xmlString);
      }
      catch (IOException ex)
      {
         throw new WSException("Cannot parse: " + xmlString, ex);
      }
   }
   
   public String toXMLString(boolean pretty)
   {
      if (pretty)
      {
         Element elPolicy = toElement();
         return DOMWriter.printNode(elPolicy, true);
      }
      
      StringBuilder xmlBuffer = new StringBuilder("<wsp:Policy");

      Iterator it = nsRegistry.getRegisteredURIs();
      while (it.hasNext())
      {
         String nsURI = (String)it.next();
         String prefix = nsRegistry.getPrefix(nsURI);
         xmlBuffer.append(" xmlns:" + prefix + "='" + nsURI + "'");
      }

      if (id != null)
      {
         xmlBuffer.append(" xmlns:wsu='" + URI_SECURITY_UTILITY + "'");
         xmlBuffer.append(" wsu:Id='" + id + "'");
      }
      if (baseURI != null)
      {
         xmlBuffer.append(" xml:base='" + baseURI + "'");
      }
      if (targetNamespace != null)
      {
         xmlBuffer.append(" TargetNamespace='" + targetNamespace + "'");
      }
      xmlBuffer.append(">");

      xmlBuffer.append("<wsp:ExactlyOne>");
      for (PolicyAlternative polAlternative : alternatives)
      {
         xmlBuffer.append(polAlternative.toXMLString(false));
      }
      xmlBuffer.append("</wsp:ExactlyOne>");

      xmlBuffer.append("</wsp:Policy>");
      
      String xmlString = xmlBuffer.toString();
      return xmlString;
   }

   public String toString()
   {
      return toXMLString(true);
   }
}
