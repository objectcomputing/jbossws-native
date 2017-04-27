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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PolicyReference;
import org.apache.ws.policy.util.DOMPolicyReader;
import org.apache.ws.policy.util.PolicyFactory;
import org.apache.ws.policy.util.PolicyRegistry;
import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.extensions.policy.PolicyScopeLevel;
import org.jboss.ws.extensions.policy.annotation.PolicyAttachment;
import org.jboss.ws.extensions.policy.deployer.PolicyDeployer;
import org.jboss.ws.extensions.policy.deployer.exceptions.UnsupportedPolicy;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ExtensibleMetaData;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLExtensibilityElement;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLProperty;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;

/**
 * A meta data builder for policies; handles checks for policy support
 * and their eventual start on both server side and client side.
 * 
 * @author Alessio Soldano, <mailto:alessio.soldano@javalinux.it>
 *
 * @since 16-May-2007
 */
public class PolicyMetaDataBuilder
{
   private static final Logger log = Logger.getLogger(PolicyMetaDataBuilder.class);
   private boolean serverSide = true;
   private boolean toolMode = false;
   private PolicyDeployer customDeployer;

   public PolicyMetaDataBuilder()
   {

   }

   /**
    * To be used for tests or whenever a custom deployer is required
    * 
    * @param customDeployer
    */
   public PolicyMetaDataBuilder(PolicyDeployer customDeployer)
   {
      this.customDeployer = customDeployer;
   }

   /**
    * Creates a new PolicyMetaDataBuilder for server side policy processing.
    * 
    * @param toolMode   True if running WSProvideTask (no policy deployments)
    * @return
    */
   public static PolicyMetaDataBuilder getServerSidePolicyMetaDataBuilder(boolean toolMode)
   {
      PolicyMetaDataBuilder builder = new PolicyMetaDataBuilder();
      builder.setServerSide(true);
      builder.setToolMode(toolMode);
      return builder;
   }

   /**
    * Creates a new PolicyMetaDataBuilder for client side policy processing.
    * 
    * @return
    */
   public static PolicyMetaDataBuilder getClientSidePolicyMetaDataBuilder()
   {
      PolicyMetaDataBuilder builder = new PolicyMetaDataBuilder();
      builder.setServerSide(false);
      return builder;
   }

   public void processPolicyAnnotations(EndpointMetaData epMetaData, Class<?> sepClass) throws IOException
   {
      UnifiedVirtualFile vfRoot = epMetaData.getServiceMetaData().getUnifiedMetaData().getRootFile();
      for (org.jboss.ws.extensions.policy.annotation.Policy anPolicy : sepClass.getAnnotation(PolicyAttachment.class).value())
      {
         InputStream is = null;
         try
         {
            String policyFileLocation = anPolicy.policyFileLocation();
            if (policyFileLocation.length() == 0)
               throw new IllegalStateException("Cannot obtain @Policy.policyFileLocation");
            
            // The root virtual file is the uniform way to obtain resources
            // It should work in all containers, server/client side
            UnifiedVirtualFile vfPolicyFile = vfRoot.findChild(policyFileLocation);
            is = vfPolicyFile.toURL().openStream();
            
            DOMPolicyReader reader = (DOMPolicyReader)PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);
            Policy unnormalizedPolicy = reader.readPolicy(is);
            Policy normPolicy = (Policy)unnormalizedPolicy.normalize();
            log.info("Deploying Annotated Policy = " + policyFileLocation);
            PolicyScopeLevel scope = anPolicy.scope();
            if (PolicyScopeLevel.WSDL_PORT.equals(scope) || PolicyScopeLevel.WSDL_PORT_TYPE.equals(scope) || PolicyScopeLevel.WSDL_BINDING.equals(scope))
            {
               deployPolicy(normPolicy, scope, epMetaData);
            }
            else
            {
               throw new WSException("Policy scope " + scope + " not supported yet!");
            }
         }
         finally
         {
            try
            {
               is.close();
            }
            catch (Exception e)
            {
            }
         }
      }
   }

   public void processPolicyExtensions(EndpointMetaData epMetaData, WSDLDefinitions wsdlDefinitions)
   {
      //Collect all policies defined in our wsdl definitions
      DOMPolicyReader reader = (DOMPolicyReader)PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);
      PolicyRegistry localPolicyRegistry = new PolicyRegistry();
      for (WSDLExtensibilityElement policyElement : wsdlDefinitions.getExtensibilityElements(Constants.WSDL_ELEMENT_POLICY))
      {
         Policy policy = reader.readPolicy(policyElement.getElement());
         localPolicyRegistry.register(policy.getPolicyURI(), policy);
      }
      //Port scope
      WSDLService wsdlService = wsdlDefinitions.getService(epMetaData.getServiceMetaData().getServiceName());
      if (wsdlService != null)
      {
         WSDLEndpoint wsdlEndpoint = wsdlService.getEndpoint(epMetaData.getPortName());
         if (wsdlEndpoint != null)
         {
            List<WSDLExtensibilityElement> portPolicyRefList = wsdlEndpoint.getExtensibilityElements(Constants.WSDL_ELEMENT_POLICYREFERENCE);
            processPolicies(portPolicyRefList, PolicyScopeLevel.WSDL_PORT, localPolicyRegistry, epMetaData);
         }
         else
         {
            log
                  .warn("Cannot get port '" + epMetaData.getPortName()
                        + "' from the given wsdl definitions! Eventual policies attached to this port won't be considered.");
         }
      }
      else
      {
         log.warn("Cannot get service '" + epMetaData.getServiceMetaData().getServiceName()
               + "' from the given wsdl definitions!  Eventual policies attached to this service won't be considered.");
      }

      //Binding scope
      WSDLBinding wsdlBinding = wsdlDefinitions.getBindingByInterfaceName(epMetaData.getPortTypeName());
      if (wsdlBinding != null)
      {
         List<WSDLExtensibilityElement> bindingPolicyRefList = wsdlBinding.getExtensibilityElements(Constants.WSDL_ELEMENT_POLICYREFERENCE);
         processPolicies(bindingPolicyRefList, PolicyScopeLevel.WSDL_BINDING, localPolicyRegistry, epMetaData);
      }
      else
      {
         log.warn("Cannot get binding for portType '" + epMetaData.getPortTypeName()
               + "' from the given wsdl definitions!  Eventual policies attached to this binding won't be considered.");
      }

      //PortType scope
      WSDLInterface wsdlInterface = wsdlDefinitions.getInterface(epMetaData.getPortTypeName());
      if (wsdlInterface != null)
      {
         WSDLProperty portTypePolicyProp = wsdlInterface.getProperty(Constants.WSDL_PROPERTY_POLICYURIS);
         processPolicies(portTypePolicyProp, PolicyScopeLevel.WSDL_PORT_TYPE, localPolicyRegistry, epMetaData);
      }
      else
      {
         log.warn("Cannot get portType '" + epMetaData.getPortTypeName()
               + "' from the given wsdl definitions! Eventual policies attached to this portType won't be considered.");
      }
   }

   private void processPolicies(WSDLProperty policyProp, PolicyScopeLevel scope, PolicyRegistry localPolicies, ExtensibleMetaData extMetaData)
   {
      if (policyProp != null && policyProp.getValue() != null)
      {
         StringTokenizer st = new StringTokenizer(policyProp.getValue(), ", ", false);
         while (st.hasMoreTokens())
         {
            PolicyReference policyRef = new PolicyReference(st.nextToken());
            deployPolicy(resolvePolicyReference(policyRef, localPolicies), scope, extMetaData);
         }
      }
   }

   private void processPolicies(List<WSDLExtensibilityElement> policyReferences, PolicyScopeLevel scope, PolicyRegistry localPolicies, ExtensibleMetaData extMetaData)
   {
      if (policyReferences != null && policyReferences.size() != 0)
      {
         DOMPolicyReader reader = (DOMPolicyReader)PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);
         for (WSDLExtensibilityElement element : policyReferences)
         {
            PolicyReference policyRef = reader.readPolicyReference(element.getElement());
            deployPolicy(resolvePolicyReference(policyRef, localPolicies), scope, extMetaData);
         }
      }
   }

   private Policy resolvePolicyReference(PolicyReference policyRef, PolicyRegistry localPolicies)
   {
      Policy normPolicy;
      try
      {
         normPolicy = (Policy)policyRef.normalize(localPolicies);
      }
      catch (RuntimeException e)
      {
         //TODO!!! not a local policy: get the policy definition and create the policy
         normPolicy = null;
      }
      return normPolicy;
   }

   private void deployPolicy(Policy policy, PolicyScopeLevel scope, ExtensibleMetaData extMetaData)
   {
      PolicyDeployer deployer;
      if (customDeployer != null)
      {
         deployer = customDeployer;
      }
      else if (toolMode)
      {
         deployer = PolicyDeployer.newInstanceForTools();
      }
      else
      {
         deployer = PolicyDeployer.getInstance();
      }
      if (serverSide)
      {
         deployPolicyServerSide(policy, scope, extMetaData, deployer);
      }
      else
      {
         deployPolicyClientSide(policy, scope, extMetaData, deployer);
      }
   }

   private void deployPolicyServerSide(Policy policy, PolicyScopeLevel scope, ExtensibleMetaData extMetaData, PolicyDeployer deployer)
   {
      PolicyMetaExtension ext = (PolicyMetaExtension)extMetaData.getExtension(Constants.URI_WS_POLICY);
      if (ext == null)
      {
         ext = new PolicyMetaExtension(Constants.URI_WS_POLICY);
         extMetaData.addExtension(ext);
      }
      try
      {
         Policy deployedPolicy = deployer.deployServerside(policy, extMetaData);
         ext.addPolicy(scope, deployedPolicy);
      }
      catch (UnsupportedPolicy e)
      {
         log.warn("Policy Not supported:" + policy.getPolicyURI());
      }
   }

   private void deployPolicyClientSide(Policy policy, PolicyScopeLevel scope, ExtensibleMetaData extMetaData, PolicyDeployer deployer)
   {
      PolicyMetaExtension ext = (PolicyMetaExtension)extMetaData.getExtension(Constants.URI_WS_POLICY);
      if (ext == null)
      {
         ext = new PolicyMetaExtension(Constants.URI_WS_POLICY);
         extMetaData.addExtension(ext);
      }
      try
      {
         deployer.deployClientSide(policy, extMetaData);
         ext.addPolicy(scope, policy);
      }
      catch (UnsupportedPolicy e)
      {
         if (log.isDebugEnabled())
         {
            log.debug("Policy Not supported:" + policy.getPolicyURI());
         }
         WSException.rethrow("Policy not supported! " + policy.getPolicyURI(), e);
      }
   }

   public boolean isServerSide()
   {
      return serverSide;
   }

   public void setServerSide(boolean serverSide)
   {
      this.serverSide = serverSide;
   }

   public boolean isToolMode()
   {
      return toolMode;
   }

   public void setToolMode(boolean toolMode)
   {
      this.toolMode = toolMode;
   }
}
