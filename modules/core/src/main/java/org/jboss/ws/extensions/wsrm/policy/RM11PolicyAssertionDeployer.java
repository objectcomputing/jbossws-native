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
package org.jboss.ws.extensions.wsrm.policy;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.policy.AndCompositeAssertion;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.ws.policy.XorCompositeAssertion;
import org.jboss.ws.extensions.policy.deployer.domainAssertion.AssertionDeployer;
import org.jboss.ws.extensions.policy.deployer.exceptions.UnsupportedAssertion;
import org.jboss.ws.extensions.wsrm.config.RMDeliveryAssuranceConfig;
import org.jboss.ws.extensions.wsrm.config.RMConfig;
import org.jboss.ws.extensions.wsrm.config.RMPortConfig;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ExtensibleMetaData;

/**
 * Reliable messaging 1.1 policy deployer
 * 
 * @author richard.opalka@jboss.com
 */
public final class RM11PolicyAssertionDeployer implements AssertionDeployer
{

   private static final String WSRMP_NS = "http://docs.oasis-open.org/ws-rx/wsrmp/200702";
   private static final String WSRM_NS = "http://docs.oasis-open.org/ws-rx/wsrm/200702";
   private static final QName EXACTLY_ONCE = new QName(WSRMP_NS, "ExactlyOnce");
   private static final QName AT_LEAST_ONCE = new QName(WSRMP_NS, "AtLeastOnce");
   private static final QName AT_MOST_ONCE = new QName(WSRMP_NS, "AtMostOnce");
   private static final QName IN_ORDER = new QName(WSRMP_NS, "InOrder");
   private static final String FALSE = "false";
   private static final String TRUE = "true";
   
   /*
    * @see org.jboss.ws.extensions.policy.deployer.domainAssertion.AssertionDeployer#deployClientSide(org.apache.ws.policy.PrimitiveAssertion, org.jboss.ws.metadata.umdm.ExtensibleMetaData)
    */
   public void deployClientSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData)
   throws UnsupportedAssertion
   {
      deploy(assertion, extMetaData);
   }
   
   /*
    * @see org.jboss.ws.extensions.policy.deployer.domainAssertion.AssertionDeployer#deployServerSide(org.apache.ws.policy.PrimitiveAssertion, org.jboss.ws.metadata.umdm.ExtensibleMetaData)
    */
   public void deployServerSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData)
   throws UnsupportedAssertion
   {
      deploy(assertion, extMetaData);
   }
   
   private static void deploy(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData)
   throws UnsupportedAssertion
   {
      if (extMetaData instanceof EndpointMetaData)
      {
         EndpointMetaData endpointMD = (EndpointMetaData) extMetaData;
         
         // prepare wsrm metadata
         RMConfig rmMD = endpointMD.getConfig().getRMMetaData(); 
         if (rmMD == null)
         {
            rmMD = new RMConfig();
            endpointMD.getConfig().setRMMetaData(rmMD);
         }
         
         // construct new port metadata
         RMPortConfig portMD = new RMPortConfig();
         portMD.setPortName(endpointMD.getPortName());
         RMDeliveryAssuranceConfig deliveryMD = new RMDeliveryAssuranceConfig();
         deliveryMD.setInOrder("false");
         deliveryMD.setQuality("AtLeastOnce");
         portMD.setDeliveryAssurance(deliveryMD);
         
         // ensure port does not exists yet
         for (RMPortConfig pMD : rmMD.getPorts())
         {
            assert ! pMD.getPortName().equals(portMD.getPortName());
         }
         
         // set up port WSRMP metadata
         rmMD.getPorts().add(portMD);
         if (!WSRM_NS.equals(RMProvider.get().getNamespaceURI()))
         {
            throw new IllegalArgumentException("RM provider namespace mismatch");
         }
      }
   }

   private static RMDeliveryAssuranceConfig constructDeliveryAssurance(List<PrimitiveAssertion> assertions)
   throws UnsupportedAssertion
   {
      if (assertions.size() == 0)
      {
         // use default one
         RMDeliveryAssuranceConfig deliveryMD = new RMDeliveryAssuranceConfig();
         deliveryMD.setInOrder("false");
         deliveryMD.setQuality("AtLeastOnce");
         return deliveryMD;
      }
      
      if (assertions.size() == 1)
      {
         QName assertionQN = assertions.get(0).getName();
         assertIsWSRMPAssertion(assertionQN);
         
         RMDeliveryAssuranceConfig deliveryMD = new RMDeliveryAssuranceConfig();
         deliveryMD.setInOrder(FALSE);
         deliveryMD.setQuality(assertionQN.getLocalPart());
         return deliveryMD;
      }
      if (assertions.size() == 2)
      {
         QName firstAssertionQN = assertions.get(0).getName();
         assertIsWSRMPAssertion(firstAssertionQN);
         QName secondAssertionQN = assertions.get(1).getName();
         assertIsWSRMPAssertion(secondAssertionQN);
         
         boolean firstIsInOrder = firstAssertionQN.equals(IN_ORDER);
         
         RMDeliveryAssuranceConfig deliveryMD = new RMDeliveryAssuranceConfig();
         deliveryMD.setInOrder(TRUE);
         if (firstIsInOrder)
         {
            deliveryMD.setQuality(secondAssertionQN.getLocalPart());
         }
         else
         {
            deliveryMD.setQuality(firstAssertionQN.getLocalPart());
         }
         
         return deliveryMD;
      }
      
      throw new IllegalArgumentException();
   }
   
   private static void assertIsWSRMPAssertion(QName assertionQN) throws UnsupportedAssertion
   {
      if (assertionQN.equals(EXACTLY_ONCE)
         || assertionQN.equals(AT_LEAST_ONCE)
         || assertionQN.equals(AT_MOST_ONCE)
         || assertionQN.equals(IN_ORDER))
      {
         return; // recognized assertion - silently return
      }
      
      throw new UnsupportedAssertion();
   }
   
   private static List<PrimitiveAssertion> getWSRMPAssertions(PrimitiveAssertion assertion)
   {
      List<PrimitiveAssertion> retVal = new LinkedList<PrimitiveAssertion>();
      if (assertion.getTerms().size() != 0)
      {
         Policy policy = (Policy)assertion.getTerms().get(0);
         XorCompositeAssertion xor = (XorCompositeAssertion)policy.getTerms().get(0);
         AndCompositeAssertion and = (AndCompositeAssertion)xor.getTerms().get(0);
         List<?> primitiveAssertions = and.getTerms();

         for (int i = 0; i < primitiveAssertions.size(); i++)
         {
            retVal.add((PrimitiveAssertion)primitiveAssertions.get(i));
         }
      }
      return retVal;
   }

}
