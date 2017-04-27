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
package org.jboss.test.ws.common.jbossxb.simple;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSModel;
import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBConstants;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBUnmarshallerImpl;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.PackageMapping;
import org.jboss.wsf.common.DOMUtils;

/**
 * Test the JAXB unmarshalling of a SimpleUserType
 *
 * @author Thomas.Diesler@jboss.org
 * @since 29-Apr-2005
 */
public class SimpleUserTypeUnmarshallerTestCase extends WSToolsBase
{

   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/types";

   public void testGenerateSchema() throws Exception
   {
      QName xmlType = new QName(TARGET_NAMESPACE, "SimpleUserType", "ns1");
      String xsdSchema = generateSchema(xmlType, SimpleUserType.class);

      String exp =
      "<schema targetNamespace='http://org.jboss.ws/types' " + SCHEMA_NAMESPACES + " xmlns:tns='http://org.jboss.ws/types'>" +
      " <complexType name='SimpleUserType'>" +
      "  <sequence>" +
      "   <element name='a' type='int'/>" +
      "   <element name='b' type='int'/>" +
      "   <element name='d' nillable='true' type='dateTime'/>" +
      "  </sequence>" +
      " </complexType>" +
      "</schema>";

      assertEquals(DOMUtils.parse(exp), DOMUtils.parse(xsdSchema));
   }

   public void testUnmarshallSimpleUserType() throws Exception
   {
      QName xmlName = new QName(TARGET_NAMESPACE, "SimpleUser", "ns1");
      QName xmlType = new QName(TARGET_NAMESPACE, "SimpleUserType", "ns1");

      XSModel model = generateSchemaXSModel(xmlType, SimpleUserType.class);

      SimpleUserType obj = null;
      JBossXBUnmarshallerImpl unmarshaller = new JBossXBUnmarshallerImpl();
      unmarshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      unmarshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
      unmarshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      unmarshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());

      String xml =
         "<ns1:SimpleUser xmlns:ns1='" + TARGET_NAMESPACE + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
         " <a>0</a>" +
         " <b>0</b>" +
         " <d xsi:nil='1'/>" +
         "</ns1:SimpleUser>";

      obj = (SimpleUserType)unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

      SimpleUserType exp = new SimpleUserType();
      assertNotNull("Resulting object cannot be null", obj);
      assertEquals(exp, obj);
   }

   public void testUnmarshallSimpleUserTypeNoPrefix() throws Exception
   {
      QName xmlName = new QName(TARGET_NAMESPACE, "SimpleUser");
      QName xmlType = new QName(TARGET_NAMESPACE, "SimpleUserType");

      XSModel model = generateSchemaXSModel(xmlType, SimpleUserType.class);

      SimpleUserType obj = null;
      JBossXBUnmarshallerImpl unmarshaller = new JBossXBUnmarshallerImpl();
      unmarshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      unmarshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
      unmarshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      unmarshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());

      String xml =
         "<ns1:SimpleUser xmlns:ns1='" + TARGET_NAMESPACE + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
         " <a>0</a>" +
         " <b>0</b>" +
         " <d xsi:nil='1'/>" +
         "</ns1:SimpleUser>";

      obj = (SimpleUserType)unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

      SimpleUserType exp = new SimpleUserType();
      assertNotNull("Resulting object cannot be null", obj);
      assertEquals(exp, obj);
   }

   /**
    * Setup the required jaxrpc-mapping meta data
    */
   private JavaWsdlMapping getJavaWSDLMapping()
   {
      JavaWsdlMapping javaWsdlMapping = new JavaWsdlMapping();
      PackageMapping packageMapping = new PackageMapping(javaWsdlMapping);
      javaWsdlMapping.addPackageMapping(packageMapping);
      packageMapping.setNamespaceURI(TARGET_NAMESPACE);
      packageMapping.setPackageType("org.jboss.test.ws.common.jbossxb.simple");
      return javaWsdlMapping;
   }
}
