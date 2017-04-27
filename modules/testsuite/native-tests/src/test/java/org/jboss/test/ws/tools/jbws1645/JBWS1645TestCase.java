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
package org.jboss.test.ws.tools.jbws1645;

import java.io.File;
import java.io.FileInputStream;
import java.io.Writer;

import org.apache.ws.policy.Policy;
import org.apache.ws.policy.util.DOMPolicyReader;
import org.apache.ws.policy.util.PolicyFactory;
import org.apache.ws.policy.util.PolicyReader;
import org.jboss.test.ws.tools.sei.StandardJavaTypes;
import org.jboss.test.ws.tools.validation.WSDLValidator;
import org.jboss.ws.Constants;
import org.jboss.ws.core.soap.Style;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.IOUtils;
import org.jboss.ws.extensions.policy.PolicyScopeLevel;
import org.jboss.ws.extensions.policy.metadata.PolicyMetaExtension;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ExtensibleMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.tools.JavaToWSDL;
import org.jboss.ws.tools.WSToolsConstants;
import org.jboss.ws.tools.metadata.ToolsUnifiedMetaDataBuilder;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.ws.tools.wsdl.WSDLWriter;

/**
 * 
 * @author Alessio Soldano, <alessio.soldano@javalinux.it>
 * @since 05-May-2007
 */
public class JBWS1645TestCase extends JBossWSTest
{
   private PolicyReader reader;
   
   public JBWS1645TestCase()
   {
      super();
      setPolicyReader();
   }
   
   public JBWS1645TestCase(String name)
   {
      super(name);
      setPolicyReader();
   }
   
   private void setPolicyReader()
   {
      reader = (DOMPolicyReader) PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);
   }
   
   public void testWSDLGeneratorWithPolicies() throws Exception
   {
      Class seiClass = StandardJavaTypes.class;
      String fixturefile = getResourceFile("tools/jbws1645/StandardJavaTypesServiceJBWS1645.wsdl").getAbsolutePath();
      
      File wsdlDir = createResourceFile("./tools/jbws1645");
      wsdlDir.mkdirs();
      
      String sname = WSDLUtils.getJustClassName(seiClass) + "Service";
      String wsdlPath = wsdlDir + "/" + sname + "JBWS1645.wsdl";
      String targetNamespace = "http://org.jboss.ws";
      Style style = Style.DOCUMENT;
      JavaToWSDL jwsdl = new JavaToWSDL(Constants.NS_WSDL11);
      jwsdl.setServiceName(sname);
      jwsdl.setTargetNamespace(targetNamespace);
      jwsdl.addFeature(WSToolsConstants.WSTOOLS_FEATURE_RESTRICT_TO_TARGET_NS, true);
      jwsdl.setStyle(style);
      
      //manually generate the umd using tools
      UnifiedMetaData umd = new ToolsUnifiedMetaDataBuilder(seiClass, targetNamespace,
            null, sname, style, null, null).getUnifiedMetaData();
      jwsdl.setUmd(umd);
      
      //manually add policies to the umd
      ServiceMetaData serviceMetaData = umd.getServices().get(0);
      EndpointMetaData epMetaData = serviceMetaData.getEndpoints().get(0);
      addPolicy(getResourceFile("tools/jbws1645/PortPolicy.txt"), PolicyScopeLevel.WSDL_PORT, epMetaData);
      addPolicy(getResourceFile("tools/jbws1645/PortTypePolicy.txt"), PolicyScopeLevel.WSDL_PORT_TYPE, epMetaData);
      addPolicy(getResourceFile("tools/jbws1645/BindingPolicy.txt"), PolicyScopeLevel.WSDL_BINDING, epMetaData);
      
      //generate the wsdl definitions and write the wsdl file
      WSDLDefinitions wsdl = jwsdl.generate(seiClass);
      
      //performe some trivial checks on wsdl definitions
      assertEquals(1, wsdl.getServices()[0].getEndpoints()[0].getExtensibilityElements(
            Constants.WSDL_ELEMENT_POLICYREFERENCE).size());
      assertNotNull(wsdl.getInterfaces()[0].getProperty(Constants.WSDL_PROPERTY_POLICYURIS));
      assertEquals(1, wsdl.getBindings()[0].getExtensibilityElements(Constants.WSDL_ELEMENT_POLICYREFERENCE).size());
      assertEquals(3, wsdl.getExtensibilityElements(Constants.WSDL_ELEMENT_POLICY).size());
      
      Writer fw = IOUtils.getCharsetFileWriter(new File(wsdlPath), Constants.DEFAULT_XML_CHARSET);
      new WSDLWriter(wsdl).write(fw, Constants.DEFAULT_XML_CHARSET);
      fw.close();
      
      //validate the generated WSDL
      validateGeneratedWSDL(new File(wsdlPath), new File(fixturefile));
      
   }

   
   
   public void testWSDLGeneratorWithMultiplePolicies() throws Exception
   {
      Class seiClass = StandardJavaTypes.class;
      String fixturefile = getResourceFile("tools/jbws1645/StandardJavaTypesServiceJBWS1645-Multiple.wsdl").getAbsolutePath();
      
      File wsdlDir = createResourceFile("./tools/jbws1645");
      wsdlDir.mkdirs();
      
      String sname = WSDLUtils.getJustClassName(seiClass) + "Service-Multiple";
      String wsdlPath = wsdlDir + "/" + sname + "JBWS1645-Multiple.wsdl";
      String targetNamespace = "http://org.jboss.ws";
      Style style = Style.DOCUMENT;
      JavaToWSDL jwsdl = new JavaToWSDL(Constants.NS_WSDL11);
      jwsdl.setServiceName(sname);
      jwsdl.setTargetNamespace(targetNamespace);
      jwsdl.addFeature(WSToolsConstants.WSTOOLS_FEATURE_RESTRICT_TO_TARGET_NS, true);
      jwsdl.setStyle(style);
      
      //manually generate the umd using tools
      UnifiedMetaData umd = new ToolsUnifiedMetaDataBuilder(seiClass, targetNamespace,
            null, sname, style, null, null).getUnifiedMetaData();
      jwsdl.setUmd(umd);
      
      //manually add policies to the umd
      ServiceMetaData serviceMetaData = umd.getServices().get(0);
      EndpointMetaData epMetaData = serviceMetaData.getEndpoints().get(0);
      addPolicy(getResourceFile("tools/jbws1645/PortPolicy.txt"), PolicyScopeLevel.WSDL_PORT, epMetaData);
      addPolicy(getResourceFile("tools/jbws1645/PortPolicy2.txt"), PolicyScopeLevel.WSDL_PORT, epMetaData);
      addPolicy(getResourceFile("tools/jbws1645/PortTypePolicy2.txt"), PolicyScopeLevel.WSDL_PORT_TYPE, epMetaData);
      addPolicy(getResourceFile("tools/jbws1645/PortTypePolicy.txt"), PolicyScopeLevel.WSDL_PORT_TYPE, epMetaData);
      addPolicy(getResourceFile("tools/jbws1645/BindingPolicy.txt"), PolicyScopeLevel.WSDL_BINDING, epMetaData);
      addPolicy(getResourceFile("tools/jbws1645/BindingPolicy2.txt"), PolicyScopeLevel.WSDL_BINDING, epMetaData);
      
      //generate the wsdl definitions and write the wsdl file
      WSDLDefinitions wsdl = jwsdl.generate(seiClass);
      
      //performe some trivial checks on wsdl definitions
      assertEquals(2, wsdl.getServices()[0].getEndpoints()[0].getExtensibilityElements(
            Constants.WSDL_ELEMENT_POLICYREFERENCE).size());
      assertNotNull(wsdl.getInterfaces()[0].getProperty(Constants.WSDL_PROPERTY_POLICYURIS));
      assertEquals(2, wsdl.getBindings()[0].getExtensibilityElements(Constants.WSDL_ELEMENT_POLICYREFERENCE).size());
      assertEquals(6, wsdl.getExtensibilityElements(Constants.WSDL_ELEMENT_POLICY).size());
      
      Writer fw = IOUtils.getCharsetFileWriter(new File(wsdlPath), Constants.DEFAULT_XML_CHARSET);
      new WSDLWriter(wsdl).write(fw, Constants.DEFAULT_XML_CHARSET);
      fw.close();
      
      //validate the generated WSDL
      validateGeneratedWSDL(new File(wsdlPath), new File(fixturefile));
      
   }
   
   private void addPolicy(File sourceFile, PolicyScopeLevel scope, ExtensibleMetaData extMetaData) throws Exception
   {
      PolicyMetaExtension ext = (PolicyMetaExtension)extMetaData.getExtension(Constants.URI_WS_POLICY);
      if (ext == null)
      {
         ext = new PolicyMetaExtension(Constants.URI_WS_POLICY);
         extMetaData.addExtension(ext);
      }
      FileInputStream fis = new FileInputStream(sourceFile);
      Policy policy = reader.readPolicy(fis);
      fis.close();
      ext.addPolicy(scope, policy);
   }
   
   private void validateGeneratedWSDL(File wsdlFile, File expectedWsdlFile) throws Exception
   {
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdl = factory.parse(wsdlFile.toURL());
      WSDLDefinitions expWsdl = factory.parse(expectedWsdlFile.toURL());
      assertNotNull(wsdl);
      assertNotNull(expWsdl);
      WSDLValidator validator = new WSDLValidator();
      assertTrue(validator.validate(expWsdl,wsdl));
   }
}
