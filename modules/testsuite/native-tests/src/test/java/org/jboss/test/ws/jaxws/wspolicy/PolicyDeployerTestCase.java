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
package org.jboss.test.ws.jaxws.wspolicy;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.policy.AndCompositeAssertion;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.ws.policy.XorCompositeAssertion;
import org.jboss.ws.WSException;
import org.jboss.ws.extensions.policy.deployer.PolicyDeployer;
import org.jboss.ws.extensions.policy.deployer.domainAssertion.NopAssertionDeployer;
import org.jboss.ws.extensions.policy.deployer.exceptions.UnsupportedPolicy;
import org.jboss.wsf.test.JBossWSTest;

/**
 * @author Alessio Soldano, <mailto:alessio.soldano@javalinux.it>
 *
 * since 29-May-2007
 */
public class PolicyDeployerTestCase extends JBossWSTest
{
   
   public void testDeployEmptyPolicy() throws Exception
   {
      Map<String,Class> map = new HashMap<String,Class>();
      map.put("http://www.jboss.com/test/policy", NopAssertionDeployer.class);
      PolicyDeployer deployer = PolicyDeployer.newInstance(map);
      try
      {
         deployer.deployServerside(null, null);
         fail("deployServerSide should throw exception when invoked with null policy!");
      }
      catch (WSException wse)
      {
         //OK
      }
      Policy policy = new Policy();
      deployer.deployServerside(policy, null);
   }
   
   public void testDeploySingleAssertion() throws Exception
   {
      Map<String,Class> map = new HashMap<String,Class>();
      map.put("http://www.jboss.com/test/policy", NopAssertionDeployer.class);
      PolicyDeployer deployer = PolicyDeployer.newInstance(map);
      Policy policy = new Policy("myID");
      PrimitiveAssertion assertion = new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy","test"));
      policy.addTerm(assertion);
      deployer.deployServerside(policy, null);
      
      policy.remove(assertion);
      policy.addTerm(new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy2","test")));
      try
      {
         deployer.deployServerside(policy, null);
         fail("deployServerSide shouldn't be able to deploy this policy!");
      }
      catch (UnsupportedPolicy up)
      {
         //OK
      }
   }
   
   public void testDeployMultipleAssertion() throws Exception
   {
      Map<String,Class> map = new HashMap<String,Class>();
      map.put("http://www.jboss.com/test/policy", NopAssertionDeployer.class);
      map.put("http://www.jboss.com/test/policy2", NopAssertionDeployer.class);
      map.put("http://www.jboss.com/test/policy3", NopAssertionDeployer.class);
      PolicyDeployer deployer = PolicyDeployer.newInstance(map);
      Policy policy = new Policy("myID");
      policy.addTerm(new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy","test")));
      policy.addTerm(new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy2","test2")));
      deployer.deployServerside(policy, null);
      
      policy.addTerm(new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy4","test4")));
      try
      {
         deployer.deployServerside(policy, null);
         fail("deployServerSide shouldn't be able to deploy this policy!");
      }
      catch (UnsupportedPolicy up)
      {
         //OK
      }
   }
   
   public void testDeployMultipleAlternative() throws Exception
   {
      Map<String,Class> map = new HashMap<String,Class>();
      map.put("http://www.jboss.com/test/policy", NopAssertionDeployer.class);
      map.put("http://www.jboss.com/test/policy2", NopAssertionDeployer.class);
      PolicyDeployer deployer = PolicyDeployer.newInstance(map);
      Policy policy = new Policy("myID");
      PrimitiveAssertion assertion1 = new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy","test"));
      PrimitiveAssertion assertion2 = new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy2","test2"));
      XorCompositeAssertion xorAssertion = new XorCompositeAssertion();
      xorAssertion.addTerm(assertion1);
      xorAssertion.addTerm(assertion2);
      policy.addTerm(xorAssertion);
      deployer.deployServerside(policy, null);
      
      xorAssertion.remove(assertion2);
      xorAssertion.addTerm(new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy3","test3")));
      deployer.deployServerside(policy, null);
      
      xorAssertion.remove(assertion1);
      xorAssertion.addTerm(new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy4","test4")));
      try
      {
         deployer.deployServerside(policy, null);
         fail("deployServerSide shouldn't be able to deploy this policy (no alternative supported)!");
      }
      catch (UnsupportedPolicy up)
      {
         //OK
      }
   }
   
   public void testDeployMultipleComplexAlternative() throws Exception
   {
      Map<String,Class> map = new HashMap<String,Class>();
      map.put("http://www.jboss.com/test/policy", NopAssertionDeployer.class);
      map.put("http://www.jboss.com/test/policy2", NopAssertionDeployer.class);
      map.put("http://www.jboss.com/test/policy3", NopAssertionDeployer.class);
      PolicyDeployer deployer = PolicyDeployer.newInstance(map);
      Policy policy = new Policy("myID");
      PrimitiveAssertion assertion1 = new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy","test"));
      PrimitiveAssertion assertion2 = new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy2","test2"));
      PrimitiveAssertion assertion3 = new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy3","test3"));
      PrimitiveAssertion assertion4 = new PrimitiveAssertion(new QName("http://www.jboss.com/test/policy4","test4"));
      AndCompositeAssertion andAssertion1 = new AndCompositeAssertion();
      andAssertion1.addTerm(assertion1);
      andAssertion1.addTerm(assertion2);
      AndCompositeAssertion andAssertion2 = new AndCompositeAssertion();
      andAssertion2.addTerm(assertion3);
      andAssertion2.addTerm(assertion4);
      XorCompositeAssertion xorAssertion = new XorCompositeAssertion();
      xorAssertion.addTerm(andAssertion1);
      xorAssertion.addTerm(andAssertion2);
      policy.addTerm(xorAssertion);
      deployer.deployServerside(policy, null);
      
      xorAssertion.remove(andAssertion1);
      try
      {
         deployer.deployServerside(policy, null);
         fail("deployServerSide shouldn't be able to deploy this policy (no alternative supported)!");
      }
      catch (UnsupportedPolicy up)
      {
         //OK
      }
   }
   
}
