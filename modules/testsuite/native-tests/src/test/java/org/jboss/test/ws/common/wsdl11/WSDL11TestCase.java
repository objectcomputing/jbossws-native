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
package org.jboss.test.ws.common.wsdl11;

import java.io.File;
import java.io.Writer;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.test.ws.tools.validation.WSDLValidator;
import org.jboss.ws.Constants;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLExtensibilityElement;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLProperty;
import org.jboss.ws.metadata.wsdl.WSDLRPCPart;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.ws.tools.wsdl.WSDLWriter;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.IOUtils;
import org.w3c.dom.Element;

/**
 * Test the unmarshalling of wsdl-1.1 into the unified wsdl structure
 *
 * @author Thomas.Diesler@jboss.org
 * @since 02-Jun-2005
 */
public class WSDL11TestCase extends JBossWSTest
{
   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/jaxrpc/types";

   public void testDocLitSimple() throws Exception
   {
      File wsdlFile = getResourceFile("common/wsdl11/DocLitSimple.wsdl");
      assertTrue(wsdlFile.exists());

      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlFile.toURL());
      WSDLInterface wsdlInterface = wsdlDefinitions.getInterface(new QName(wsdlDefinitions.getTargetNamespace(), "JaxRpcTestService"));

      // check if the schema has been extracted
      WSDLTypes wsdlTypes = wsdlDefinitions.getWsdlTypes();
      assertNotNull(WSDLUtils.getSchemaModel(wsdlTypes));

      // check the echoString operation
      WSDLInterfaceOperation wsdlOperation = wsdlInterface.getOperation("echoString");
      assertEquals(Constants.URI_STYLE_DOCUMENT, wsdlOperation.getStyle());

      WSDLInterfaceOperationInput wsdlInput = wsdlOperation.getInput(new QName(TARGET_NAMESPACE, "echoString"));
      assertEquals(new QName(TARGET_NAMESPACE, "echoString"), wsdlInput.getXMLType());
      WSDLInterfaceOperationOutput wsdlOutput = wsdlOperation.getOutput(new QName(TARGET_NAMESPACE, "echoStringResponse"));
      assertEquals(new QName(TARGET_NAMESPACE, "echoStringResponse"), wsdlOutput.getXMLType());

      // check the echoSimpleUserType operation
      wsdlOperation = wsdlInterface.getOperation("echoSimpleUserType");
      assertEquals(Constants.URI_STYLE_DOCUMENT, wsdlOperation.getStyle());

      wsdlInput = wsdlOperation.getInput(new QName(TARGET_NAMESPACE, "echoSimpleUserType"));
      assertEquals(new QName(TARGET_NAMESPACE, "echoSimpleUserType"), wsdlInput.getXMLType());
      wsdlOutput = wsdlOperation.getOutput(new QName(TARGET_NAMESPACE, "echoSimpleUserTypeResponse"));
      assertEquals(new QName(TARGET_NAMESPACE, "echoSimpleUserTypeResponse"), wsdlOutput.getXMLType());

      QName xmlName = new QName(TARGET_NAMESPACE, "echoString");
      QName xmlType = new QName(TARGET_NAMESPACE, "echoString");
      assertEquals(xmlType, wsdlTypes.getXMLType(xmlName));
      xmlName = new QName(TARGET_NAMESPACE, "echoStringResponse");
      xmlType = new QName(TARGET_NAMESPACE, "echoStringResponse");
      assertEquals(xmlType, wsdlTypes.getXMLType(xmlName));

      xmlName = new QName(TARGET_NAMESPACE, "echoSimpleUserType");
      xmlType = new QName(TARGET_NAMESPACE, "echoSimpleUserType");
      assertEquals(xmlType, wsdlTypes.getXMLType(xmlName));
      xmlName = new QName(TARGET_NAMESPACE, "echoSimpleUserTypeResponse");
      xmlType = new QName(TARGET_NAMESPACE, "echoSimpleUserTypeResponse");
      assertEquals(xmlType, wsdlTypes.getXMLType(xmlName));
   }

   public void testRpcLitSimple() throws Exception
   {
      verifyRPC(getResourceFile("common/wsdl11/RpcLitSimple.wsdl").getPath());
   }

   public void testRpcLitImport() throws Exception
   {
      verifyRPC(getResourceFile("common/wsdl11/RpcLitImport.wsdl").getPath());
   }

   private void verifyRPC(String fileName) throws Exception
   {
      File wsdlFile = new File(fileName);
      assertTrue(wsdlFile.exists());

      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlFile.toURL());
      WSDLInterface wsdlInterface = wsdlDefinitions.getInterface(new QName(wsdlDefinitions.getTargetNamespace(), "JaxRpcTestService"));

      // check if the schema has been extracted
      WSDLTypes wsdlTypes = wsdlDefinitions.getWsdlTypes();
      assertNotNull(WSDLUtils.getSchemaModel(wsdlTypes));

      // check the echoString operation
      WSDLInterfaceOperation wsdlOperation = wsdlInterface.getOperation("echoString");
      assertEquals(Constants.URI_STYLE_RPC, wsdlOperation.getStyle());

      WSDLInterfaceOperationInput wsdlInput = wsdlOperation.getInputs()[0];
      WSDLRPCPart childPart = wsdlInput.getChildPart("String_1");
      assertEquals(Constants.TYPE_LITERAL_STRING, childPart.getType());
      childPart = wsdlInput.getChildPart("String_2");
      assertEquals(Constants.TYPE_LITERAL_STRING, childPart.getType());
      WSDLInterfaceOperationOutput wsdlOutput = wsdlOperation.getOutputs()[0];
      childPart = wsdlOutput.getChildPart("result");
      assertEquals(Constants.TYPE_LITERAL_STRING, childPart.getType());

      // check the echoSimpleUserType operation
      wsdlOperation = wsdlInterface.getOperation("echoSimpleUserType");
      assertEquals(Constants.URI_STYLE_RPC, wsdlOperation.getStyle());

      wsdlInput = wsdlOperation.getInputs()[0];
      childPart = wsdlInput.getChildPart("String_1");
      assertEquals(Constants.TYPE_LITERAL_STRING, childPart.getType());
      childPart = wsdlInput.getChildPart("SimpleUserType_2");
      assertEquals(new QName(TARGET_NAMESPACE, "SimpleUserType"), childPart.getType());
      wsdlOutput = wsdlOperation.getOutputs()[0];
      childPart = wsdlOutput.getChildPart("result");
      assertEquals(new QName(TARGET_NAMESPACE, "SimpleUserType"), childPart.getType());
   }


   public void testEventSourceBinding() throws Exception
   {
      File wsdlFile = getResourceFile("common/wsdl11/inherit/wind_inherit.wsdl");
      assertTrue(wsdlFile.exists());

      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlFile.toURL());

      WSDLService service = wsdlDefinitions.getService(new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing", "EventingService"));
      assertNotNull(service);
      WSDLEndpoint[] endpoints = service.getEndpoints();
      for (int i = 0; i < endpoints.length; i++)
      {
         WSDLEndpoint ep = endpoints[i];
         assertEquals(EventingConstants.NS_EVENTING, ep.getName().getNamespaceURI());
      }

      WSDLInterface warningsInterface = wsdlDefinitions.getInterface(new QName(wsdlDefinitions.getTargetNamespace(), "Warnings"));
      assertNotNull("Event source port type not parsed", warningsInterface);
      assertEquals(warningsInterface.getName().getNamespaceURI(), "http://www.example.org/oceanwatch");

      WSDLInterface eventSourceInterface = wsdlDefinitions.getInterface(new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing", "EventSource"));
      assertNotNull(eventSourceInterface);
      assertEquals(eventSourceInterface.getName().getNamespaceURI(), EventingConstants.NS_EVENTING);
   }

   public void testSwaMessages() throws Exception
   {
      File wsdlFile = getResourceFile("common/wsdl11/SwaTestService.wsdl");
      assertTrue(wsdlFile.exists());

      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlFile.toURL());
      assertNotNull(wsdlDefinitions); // should throw an Exception when SWA parts are not skipped
   }
   
   /**************************************************
    *  Test WSDL 1.1 marshal/unmarshal with policies *
    **************************************************/
   
   public void testPolicyAttachment() throws Exception
   {
      File wsdlFile = getResourceFile("common/wsdl11/PolicyAttachment.wsdl");
      assertTrue(wsdlFile.exists());
      testPolicyAttachment(wsdlFile);
      wsdlFile = getResourceFile("common/wsdl11/PolicyAttachmentFragment.wsdl");
      assertTrue(wsdlFile.exists());
      testPolicyAttachment(wsdlFile);
   }
   
   private void testPolicyAttachment(File wsdlFile) throws Exception
   {
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlFile.toURL());
      assertNotNull(wsdlDefinitions);
      List<WSDLExtensibilityElement> list = wsdlDefinitions.getExtensibilityElements(Constants.WSDL_ELEMENT_POLICY);
      assertNotNull(list);
      assertEquals(list.size(),5);
      for (WSDLExtensibilityElement extEl : list)
      {
         Element el = extEl.getElement();
         assertNotNull(el);
         QName qName = new QName(el.getNamespaceURI(),el.getLocalName(),el.getPrefix());
         assertEquals(qName,new QName("http://schemas.xmlsoap.org/ws/2004/09/policy","Policy","wsp"));
         assertNotNull(el.getAttributeNodeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd","Id"));
         //System.out.println(DOMWriter.printNode(extEl.getElement(),true));
      }
   }
   
   public void testServicePolicyRef() throws Exception
   {
      File wsdlFile = getResourceFile("common/wsdl11/PolicyAttachment.wsdl");
      testServicePolicyRef(wsdlFile);
      wsdlFile = getResourceFile("common/wsdl11/PolicyAttachmentFragment.wsdl");
      testServicePolicyRef(wsdlFile);
   }
   
   public void testServicePolicyRef(File wsdlFile) throws Exception
   {
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlFile.toURL());
      WSDLService wsdlService = wsdlDefinitions.getServices()[0];
      List<WSDLExtensibilityElement> list = wsdlService.getExtensibilityElements(
            Constants.WSDL_ELEMENT_POLICYREFERENCE);
      assertNotNull(list);
      assertEquals(list.size(),1);
      assertPolicyRef(list.get(0),"uselessServicePolicy");
   }
   
   public void testEndpointPolicyRef() throws Exception
   {
      File wsdlFile = getResourceFile("common/wsdl11/PolicyAttachment.wsdl");
      testEndpointPolicyRef(wsdlFile);
      wsdlFile = getResourceFile("common/wsdl11/PolicyAttachmentFragment.wsdl");
      testEndpointPolicyRef(wsdlFile);
   }
   
   public void testEndpointPolicyRef(File wsdlFile) throws Exception
   {
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlFile.toURL());
      WSDLService wsdlService = wsdlDefinitions.getServices()[0];
      WSDLEndpoint wsdlEndpoint = wsdlService.getEndpoints()[0];
      
      List<WSDLExtensibilityElement> extPortList = wsdlEndpoint.getExtensibilityElements(
            Constants.WSDL_ELEMENT_POLICYREFERENCE);
      assertNotNull(extPortList);
      assertEquals(extPortList.size(),1);
      assertPolicyRef(extPortList.get(0),"uselessPortPolicy");
      
      WSDLProperty extPortTypeProp = wsdlEndpoint.getInterface().getProperty(
            Constants.WSDL_PROPERTY_POLICYURIS);
      assertEquals(extPortTypeProp.getValue(),"#RmPolicy");
      
      WSDLBinding wsdlBinding = wsdlDefinitions.getBinding(wsdlEndpoint.getBinding());
      List<WSDLExtensibilityElement> extBinding = wsdlBinding.getExtensibilityElements(
            Constants.WSDL_ELEMENT_POLICYREFERENCE);
      assertNotNull(extBinding);
      assertEquals(extBinding.size(),2);
      assertPolicyRef(extBinding.get(0),"RmPolicy");
      assertPolicyRef(extBinding.get(1),"X509EndpointPolicy");
   }
   
   private void assertPolicyRef(WSDLExtensibilityElement extEl, String policyURI)
   {
      Element el = extEl.getElement();
      QName qName = new QName(el.getNamespaceURI(),el.getLocalName(),el.getPrefix());
      assertEquals(qName,new QName("http://schemas.xmlsoap.org/ws/2004/09/policy","PolicyReference","wsp"));
      assertNotNull(el);
      assertEquals(el.getAttributeNode("URI").getValue(),"#"+policyURI);
   }
   
   public void testPolicyAttachmentReadWrite() throws Exception
   {
      //Read wsdl containing policies from file and get the wsdl metadata model 
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(getResourceURL("/common/wsdl11/PolicyAttachment.wsdl"));
      assertNotNull(wsdlDefinitions);
      //set wsdlOneOne to null to force wsdl generation from metadata model
      wsdlDefinitions.setWsdlOneOneDefinition(null);
      
      //process the wsdl metadata model writing it back to another file
      File wsdlDir = new File("./tools/wsdl-out");
      if (!wsdlDir.exists()) wsdlDir.mkdirs();
      Writer fw = IOUtils.getCharsetFileWriter(new File(wsdlDir+"/GeneratedWsdlWithPolicies.wsdl"), Constants.DEFAULT_XML_CHARSET);
      new WSDLWriter(wsdlDefinitions).write(fw, Constants.DEFAULT_XML_CHARSET);
      fw.close();
      
      //parse the obtained file and validate the resulting wsdl metadata model against the first one
      WSDLDefinitions newWsdlDefinitions = factory.parse(new File(wsdlDir+"/GeneratedWsdlWithPolicies.wsdl").toURL());
      assertNotNull(newWsdlDefinitions);
      WSDLValidator validator = new WSDLValidator();
      assertTrue(validator.validate(wsdlDefinitions,newWsdlDefinitions));
   }
}
