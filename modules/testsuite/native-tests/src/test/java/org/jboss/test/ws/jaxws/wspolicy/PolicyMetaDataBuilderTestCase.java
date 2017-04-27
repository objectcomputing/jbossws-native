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

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.policy.Policy;
import org.jboss.ws.Constants;
import org.jboss.ws.extensions.policy.PolicyScopeLevel;
import org.jboss.ws.extensions.policy.deployer.PolicyDeployer;
import org.jboss.ws.extensions.policy.deployer.domainAssertion.NopAssertionDeployer;
import org.jboss.ws.extensions.policy.metadata.PolicyMetaDataBuilder;
import org.jboss.ws.extensions.policy.metadata.PolicyMetaExtension;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.common.URLLoaderAdapter;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;
import org.jboss.wsf.test.JBossWSTest;

/**
 * @author Alessio Soldano, <mailto:alessio.soldano@javalinux.it>
 *
 * since 29-May-2007
 */
public class PolicyMetaDataBuilderTestCase extends JBossWSTest
{
   public void testEndpointScopePolicies() throws Exception
   {
      UnifiedVirtualFile vfRoot = new URLLoaderAdapter(getResourceURL("jaxws/wspolicy"));
      UnifiedMetaData umd = new UnifiedMetaData(vfRoot);
      
      QName serviceName = new QName("http://org.jboss.ws/jaxws/endpoint", "TestService");
      ServiceMetaData serviceMetaData = new ServiceMetaData(umd, serviceName);
      QName portName = new QName("http://org.jboss.ws/jaxws/endpoint", "EndpointInterfacePort");
      QName portTypeName = new QName("http://org.jboss.ws/jaxws/endpoint", "EndpointInterface");
      EndpointMetaData epMetaData = new ServerEndpointMetaData(serviceMetaData, null, portName, portTypeName, Type.JAXWS);

      Map<String, Class> map = new HashMap<String, Class>();
      map.put("http://schemas.xmlsoap.org/ws/2005/02/rm/policy", NopAssertionDeployer.class);
      map.put("http://www.fabrikam123.example.com/stock", NopAssertionDeployer.class);
      map.put("http://schemas.xmlsoap.org/ws/2005/07/securitypolicy", NopAssertionDeployer.class);
      PolicyDeployer deployer = PolicyDeployer.newInstance(map);
      PolicyMetaDataBuilder builder = new PolicyMetaDataBuilder(deployer);

      WSDLDefinitions wsdlDefinitions = readWsdl(getResourceFile("jaxws/wspolicy/TestService.wsdl").getPath());
      builder.processPolicyExtensions(epMetaData, wsdlDefinitions);

      PolicyMetaExtension policyExt = (PolicyMetaExtension)epMetaData.getExtension(Constants.URI_WS_POLICY);
      Collection<Policy> bindingPolicies = policyExt.getPolicies(PolicyScopeLevel.WSDL_BINDING);
      assertNotNull(bindingPolicies);
      assertEquals(2, bindingPolicies.size());
      Iterator<Policy> bindingPoliciesIterator = bindingPolicies.iterator();
      String id1 = bindingPoliciesIterator.next().getId();
      String id2 = bindingPoliciesIterator.next().getId();
      assertTrue(("RmPolicy".equalsIgnoreCase(id1) && "X509EndpointPolicy".equalsIgnoreCase(id2))
            || ("RmPolicy".equalsIgnoreCase(id2) && "X509EndpointPolicy".equalsIgnoreCase(id1)));

      Collection<Policy> portPolicies = policyExt.getPolicies(PolicyScopeLevel.WSDL_PORT);
      assertNotNull(portPolicies);
      assertEquals(1, portPolicies.size());
      assertEquals("uselessPortPolicy", portPolicies.iterator().next().getId());

      Collection<Policy> portTypePolicies = policyExt.getPolicies(PolicyScopeLevel.WSDL_PORT_TYPE);
      assertNotNull(portTypePolicies);
      assertEquals(2, portTypePolicies.size());
      Iterator<Policy> portTypePoliciesIterator = portTypePolicies.iterator();
      String id3 = portTypePoliciesIterator.next().getId();
      String id4 = portTypePoliciesIterator.next().getId();
      assertTrue(("uselessPortTypePolicy".equalsIgnoreCase(id3) && "uselessPortTypePolicy2".equalsIgnoreCase(id4))
            || ("uselessPortTypePolicy".equalsIgnoreCase(id4) && "uselessPortTypePolicy2".equalsIgnoreCase(id3)));
   }

   public void testAnnotationEndpointScopePolicies() throws Exception
   {
      Map<String, Class> map = new HashMap<String, Class>();
      map.put("http://www.fabrikam123.example.com/stock", NopAssertionDeployer.class);
      PolicyDeployer deployer = PolicyDeployer.newInstance(map);
      PolicyMetaDataBuilder builder = new PolicyMetaDataBuilder(deployer);
      builder.setToolMode(true);

      UnifiedVirtualFile vfRoot = new URLLoaderAdapter(getResourceURL("jaxws/wspolicy"));
      UnifiedMetaData umd = new UnifiedMetaData(vfRoot);
      ServiceMetaData serviceMetaData = new ServiceMetaData(umd, new QName("dummyServiceName"));
      umd.addService(serviceMetaData);
      EndpointMetaData epMetaData = new ServerEndpointMetaData(serviceMetaData, null, new QName("dummyPortName"), new QName("dummyPortTypeName"), Type.JAXWS);
      serviceMetaData.addEndpoint(epMetaData);
      
      builder.processPolicyAnnotations(epMetaData, TestMultipleEndpointPolicy.class);

      PolicyMetaExtension policyExt = (PolicyMetaExtension)epMetaData.getExtension(Constants.URI_WS_POLICY);

      Collection<Policy> portPolicies = policyExt.getPolicies(PolicyScopeLevel.WSDL_PORT);
      assertNotNull(portPolicies);
      assertEquals(2, portPolicies.size());
      Iterator<Policy> portPoliciesIterator = portPolicies.iterator();
      String id1 = portPoliciesIterator.next().getId();
      String id2 = portPoliciesIterator.next().getId();
      assertTrue(("uselessPortPolicy".equalsIgnoreCase(id1) && "uselessPortPolicy2".equalsIgnoreCase(id2))
            || ("uselessPortPolicy".equalsIgnoreCase(id2) && "uselessPortPolicy2".equalsIgnoreCase(id1)));

      Collection<Policy> portTypePolicies = policyExt.getPolicies(PolicyScopeLevel.WSDL_PORT_TYPE);
      assertNotNull(portTypePolicies);
      assertEquals(2, portTypePolicies.size());
      Iterator<Policy> portTypePoliciesIterator = portTypePolicies.iterator();
      String id3 = portTypePoliciesIterator.next().getId();
      String id4 = portTypePoliciesIterator.next().getId();
      assertTrue(("uselessPortTypePolicy".equalsIgnoreCase(id3) && "uselessPortTypePolicy2".equalsIgnoreCase(id4))
            || ("uselessPortTypePolicy".equalsIgnoreCase(id4) && "uselessPortTypePolicy2".equalsIgnoreCase(id3)));

      Collection<Policy> bindingPolicies = policyExt.getPolicies(PolicyScopeLevel.WSDL_BINDING);
      assertNotNull(bindingPolicies);
      assertEquals(2, bindingPolicies.size());
      Iterator<Policy> bindingPoliciesIterator = bindingPolicies.iterator();
      String id5 = bindingPoliciesIterator.next().getId();
      String id6 = bindingPoliciesIterator.next().getId();
      assertTrue(("uselessBindingPolicy".equalsIgnoreCase(id5) && "uselessBindingPolicy2".equalsIgnoreCase(id6))
            || ("uselessBindingPolicy".equalsIgnoreCase(id6) && "uselessBindingPolicy2".equalsIgnoreCase(id5)));
   }

   private WSDLDefinitions readWsdl(String filename) throws Exception
   {
      File wsdlFile = new File(filename);
      assertTrue(wsdlFile.exists());
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlFile.toURL());
      assertNotNull(wsdlDefinitions);
      return wsdlDefinitions;
   }
}
