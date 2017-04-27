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
import org.w3c.dom.Element;

/** 
 * A policy alternative is a potentially empty collection of policy assertions. An alternative
 * with zero assertions indicates no behaviors. An alternative with one or more assertions
 * indicates behaviors implied by those, and only those assertions.
 * 
 * The vocabulary of a policy alternative is the set of all assertion types within the
 * alternative. The vocabulary of a policy is the set of all assertion types used in the policy.
 * An assertion whose type is part of the policy's vocabulary but is not included in an
 * alternative is explicitly prohibited by the alternative.
 * 
 * Assertions within an alternative are not ordered, and thus aspects such as the order in
 * which behaviors (indicated by assertions) are applied to a subject are beyond the scope
 * of this specification.
 * 
 * A policy alternative MAY contain multiple instances of an assertion type. Mechanisms for
 * determining the aggregate behavior indicated by the assertion instances (and their Post-
 * Schema-Validation Infoset (PSVI) content, if any) are specific to the assertion type.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 13-Nov-2005
 */
public class PolicyAlternative
{
   private Collection<PolicyAssertion> assertions = new ArrayList<PolicyAssertion>();

   // Hide constructor
   PolicyAlternative()
   {
   }

   static PolicyAlternative parse(Element elAll)
   {
      // Work with a cloned copy, so parsing does not effect in input node
      elAll = (Element)elAll.cloneNode(true);
      
      PolicyAlternative all = new PolicyAlternative();
      Iterator it = DOMUtils.getChildElements(elAll);
      while (it.hasNext())
      {
         Element el = (Element)it.next();
         all.addPolicyAssertion(new PolicyAssertion(el));
      }
      return all;
   }
   
   public void addPolicyAssertion(PolicyAssertion polAssertion)
   {
      assertions.add(polAssertion);
   }
   
   public Collection<PolicyAssertion> getPolicyAssertions()
   {
      return new ArrayList<PolicyAssertion>(assertions);
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
         Element elAll = toElement();
         return DOMWriter.printNode(elAll, true);
      }
      
      StringBuilder xmlBuffer = new StringBuilder("<wsp:All>");
      for (PolicyAssertion assertion : assertions)
      {
         xmlBuffer.append(assertion.toXMLString(false));
      }
      xmlBuffer.append("</wsp:All>");
      return xmlBuffer.toString();
   }
   
   public String toString()
   {
      return toXMLString(true);
   }
}
