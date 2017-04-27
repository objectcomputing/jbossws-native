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

import org.apache.ws.policy.PrimitiveAssertion;
import org.jboss.ws.extensions.policy.deployer.domainAssertion.AssertionDeployer;
import org.jboss.ws.extensions.policy.deployer.exceptions.UnsupportedAssertion;
import org.jboss.ws.extensions.wsrm.config.RMConfig;
import org.jboss.ws.extensions.wsrm.config.RMDeliveryAssuranceConfig;
import org.jboss.ws.extensions.wsrm.config.RMPortConfig;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ExtensibleMetaData;

/**
 * Reliable messaging 1.1 policy deployer
 *
 * @author richard.opalka@jboss.com
 */
public final class RM10PolicyAssertionDeployer implements AssertionDeployer
{
   private static final String WSRM_NS = "http://schemas.xmlsoap.org/ws/2005/02/rm";
   
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
   
}
