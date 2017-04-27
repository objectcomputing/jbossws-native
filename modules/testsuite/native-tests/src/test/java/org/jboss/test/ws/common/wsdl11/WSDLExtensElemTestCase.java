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
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.ws.Constants;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLExtensibilityElement;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.test.JBossWSTest;
import org.w3c.dom.Element;

/**
 * Test the unmarshalling of wsdl-1.1 into the unified wsdl structure
 * using known and unknown wsdl extensibility elements.
 *
 * @author alessio.soldano@jboss.com
 * @since 15-Jan-2009
 */
public class WSDLExtensElemTestCase extends JBossWSTest
{
   public void testPolicyEndpointExtensibilityElements() throws Exception
   {
      File wsdlFile = getResourceFile("common/wsdl11/PolicyAttachment.wsdl");
      assertTrue(wsdlFile.exists());
      
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlFile.toURL());
      WSDLService wsdlService = wsdlDefinitions.getServices()[0];
      WSDLEndpoint wsdlEndpoint = wsdlService.getEndpoints()[0];
      
      List<WSDLExtensibilityElement> extPortList = wsdlEndpoint.getExtensibilityElements(
            Constants.WSDL_ELEMENT_POLICYREFERENCE);
      assertNotNull(extPortList);
      assertEquals(extPortList.size(),1);
      assertPolicyRef(extPortList.get(0),"uselessPortPolicy");
      List<WSDLExtensibilityElement> portNotUnderstoodList = wsdlEndpoint.getNotUnderstoodExtElements();
      assertNotNull(portNotUnderstoodList);
      assertEquals(0, portNotUnderstoodList.size());
      
      WSDLBinding wsdlBinding = wsdlDefinitions.getBinding(wsdlEndpoint.getBinding());
      List<WSDLExtensibilityElement> extBinding = wsdlBinding.getExtensibilityElements(
            Constants.WSDL_ELEMENT_POLICYREFERENCE);
      assertNotNull(extBinding);
      assertEquals(extBinding.size(),2);
      assertPolicyRef(extBinding.get(0),"RmPolicy");
      assertPolicyRef(extBinding.get(1),"X509EndpointPolicy");
      List<WSDLExtensibilityElement> bindingNotUnderstoodList = wsdlBinding.getNotUnderstoodExtElements();
      assertNotNull(bindingNotUnderstoodList);
      assertEquals(0, bindingNotUnderstoodList.size());
   }
   
   public void testPolicyAndUnkwnownEndpointExtensibilityElements() throws Exception
   {
      File wsdlFile = getResourceFile("common/wsdl11/PolicyAttachmentAndUnknownExtElem.wsdl");
      assertTrue(wsdlFile.exists());
      
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlFile.toURL());
      WSDLService wsdlService = wsdlDefinitions.getServices()[0];
      WSDLEndpoint wsdlEndpoint = wsdlService.getEndpoints()[0];
      
      List<WSDLExtensibilityElement> extPortList = wsdlEndpoint.getExtensibilityElements(
            Constants.WSDL_ELEMENT_POLICYREFERENCE);
      assertNotNull(extPortList);
      assertEquals(extPortList.size(),1);
      assertPolicyRef(extPortList.get(0),"uselessPortPolicy");
      List<WSDLExtensibilityElement> portNotUnderstoodList = wsdlEndpoint.getNotUnderstoodExtElements();
      assertNotNull(portNotUnderstoodList);
      assertEquals(1, portNotUnderstoodList.size());
      assertUnknownExtElem(portNotUnderstoodList.get(0), "foo1", "http://foo.org/foo1", "bar", true);
      
      WSDLBinding wsdlBinding = wsdlDefinitions.getBinding(wsdlEndpoint.getBinding());
      List<WSDLExtensibilityElement> extBinding = wsdlBinding.getExtensibilityElements(
            Constants.WSDL_ELEMENT_POLICYREFERENCE);
      assertNotNull(extBinding);
      assertEquals(extBinding.size(),2);
      assertPolicyRef(extBinding.get(0),"RmPolicy");
      assertPolicyRef(extBinding.get(1),"X509EndpointPolicy");
      List<WSDLExtensibilityElement> bindingNotUnderstoodList = wsdlBinding.getNotUnderstoodExtElements();
      assertNotNull(bindingNotUnderstoodList);
      assertEquals(2, bindingNotUnderstoodList.size());
      assertUnknownExtElem(bindingNotUnderstoodList.get(0), "foo1", "http://foo.org/foo1", "bar", false);
      assertUnknownExtElem(bindingNotUnderstoodList.get(1), "foo2", "http://foo.org/foo2", "bar", false);
   }
   
   private static void assertPolicyRef(WSDLExtensibilityElement extEl, String policyURI)
   {
      Element el = extEl.getElement();
      QName qName = new QName(el.getNamespaceURI(),el.getLocalName(),el.getPrefix());
      assertEquals(qName,new QName("http://schemas.xmlsoap.org/ws/2004/09/policy","PolicyReference","wsp"));
      assertEquals(el.getAttributeNode("URI").getValue(),"#"+policyURI);
   }
   
   private static void assertUnknownExtElem(WSDLExtensibilityElement extEl, String prefix, String namespaceURI, String localName, boolean required)
   {
      Element el = extEl.getElement();
      QName qName = new QName(el.getNamespaceURI(),el.getLocalName(),el.getPrefix());
      assertEquals(qName, new QName(namespaceURI, localName, prefix));
      assertEquals(required, "true".equals(el.getAttributeNS(Constants.NS_WSDL11, "required")));
      assertEquals(required, extEl.isRequired());
   }
}
