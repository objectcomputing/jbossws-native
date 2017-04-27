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
package org.jboss.ws.extensions.policy.metadata;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.ws.policy.Policy;
import org.jboss.ws.extensions.policy.PolicyScopeLevel;
import org.jboss.ws.metadata.umdm.MetaDataExtension;

public class PolicyMetaExtension extends MetaDataExtension
{
   //Policies may be attached to a policy subject with different policy scopes
   private Map<PolicyScopeLevel,Collection<Policy>> policies = new HashMap<PolicyScopeLevel,Collection<Policy>>();
   
   public PolicyMetaExtension(String extensionNameSpace)
   {
      super(extensionNameSpace);
   }
   
   public void addPolicy(PolicyScopeLevel scope, Policy policy)
   {
      Collection<Policy> list;
      if (!policies.containsKey(scope))
      {
         list = new LinkedList<Policy>();
         policies.put(scope,list);
      }
      else
      {
         list = policies.get(scope);
      }
      list.add(policy);
   }
   
   public Collection<Policy> getPolicies(PolicyScopeLevel scope)
   {
      Collection<Policy> policyCollection = policies.get(scope);
      return policyCollection == null ? new LinkedList<Policy>() : policyCollection;
   }
   
   public Collection<Policy> getAllPolicies()
   {
      Collection<Policy> list = new LinkedList<Policy>();
      for (PolicyScopeLevel scope : policies.keySet())
      {
         list.addAll(policies.get(scope));
      }
      return list;
   }
   
}
