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
package org.jboss.test.ws.common.jbossxb.complex;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigInteger;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSModel;
import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBConstants;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBUnmarshallerImpl;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMappingFactory;
import org.jboss.ws.tools.JavaToXSD;

/**
 * Test the JAXB unmarshalling of complex types
 *
 * @author Thomas.Diesler@jboss.org
 * @since 18-Oct-2004
 */
public class ComplexTypeUnmarshallerTestCase extends WSToolsBase
{

   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/types";

   /** Get the URL to the defining schema */
   protected XSModel getSchemaModel(QName xmlType, Class javaType) throws Exception
   {
      File xsdFile = getResourceFile("common/jbossxb/ComplexTypesService_RPC.xsd");
      assertTrue(xsdFile.exists());

      return new JavaToXSD().parseSchema(xsdFile.toURL());
   }

   public void testBaseType() throws Exception
   {
      String xmlStr = "<ns1:Base_1 xmlns:ns1='" + TARGET_NAMESPACE + "'><a>100</a><b>200</b></ns1:Base_1>";

      QName xmlName = new QName(TARGET_NAMESPACE, "Base_1");
      QName xmlType = new QName(TARGET_NAMESPACE, "Base");

      XSModel model = getSchemaModel(xmlType, Base.class);

      Base exp = new Base();
      exp.a = 100;
      exp.setB(200);

      JBossXBUnmarshallerImpl unmarshaller = new JBossXBUnmarshallerImpl();
      unmarshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      unmarshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());
      unmarshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      unmarshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);

      Object obj = unmarshaller.unmarshal(new ByteArrayInputStream(xmlStr.getBytes()));
      assertTrue("Unexpected return type: " + obj.getClass().getName(), obj instanceof Base);
      assertEquals(exp, obj);
   }

   public void testDerivedType() throws Exception
   {
      String xmlStr = "<ns1:Derived_1 xmlns:ns1='" + TARGET_NAMESPACE + "'><a>100</a><b>200</b><x>300</x></ns1:Derived_1>";

      Derived exp = new Derived();
      exp.a = 100;
      exp.setB(200);
      exp.x = 300;

      QName xmlName = new QName(TARGET_NAMESPACE, "Derived_1", "ns1");
      QName xmlType = new QName(TARGET_NAMESPACE, "Derived", "ns1");

      XSModel model = getSchemaModel(xmlType, Derived.class);

      JBossXBUnmarshallerImpl unmarshaller = new JBossXBUnmarshallerImpl();
      unmarshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      unmarshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());
      unmarshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      unmarshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);

      Object obj = unmarshaller.unmarshal(new ByteArrayInputStream(xmlStr.getBytes()));
      assertNotNull("Unexpected null object", obj);
      assertTrue("Unexpected return type: " + obj.getClass().getName(), obj instanceof Derived);
      assertEquals(exp, obj);
   }

   public void testCompositeType() throws Exception
   {
      String xmlStr = 
         "<ns1:CompositeType_1 xmlns:ns1='" + TARGET_NAMESPACE + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" + 
         " <composite>" + 
         "  <composite xsi:nil='1'/>" + 
         "  <dateTime xsi:nil='1'/>" + 
         "  <integer>200</integer>" + 
         "  <qname xsi:nil='1'/>" + 
         "  <string>Hello Sub World!</string>" + 
         " </composite>" + 
         " <dateTime xsi:nil='1'/>" + 
         " <integer>100</integer>" + 
         " <qname xsi:nil='1'/>" + 
         " <string>Hello World!</string>" + 
         "</ns1:CompositeType_1>";

      QName xmlName = new QName(TARGET_NAMESPACE, "CompositeType_1", "ns1");
      QName xmlType = new QName(TARGET_NAMESPACE, "Composite", "ns1");

      XSModel model = getSchemaModel(xmlType, Composite.class);

      Composite exp = new Composite();
      exp.integer = new BigInteger("100");
      exp.string = "Hello World!";
      exp.dateTime = null;
      exp.qname = null;

      Composite sub = new Composite();
      sub.integer = new BigInteger("200");
      sub.string = "Hello Sub World!";
      sub.composite = null;
      sub.dateTime = null;
      sub.qname = null;

      exp.composite = sub;

      JBossXBUnmarshallerImpl unmarshaller = new JBossXBUnmarshallerImpl();
      unmarshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      unmarshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());
      unmarshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      unmarshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);

      Object obj = unmarshaller.unmarshal(new ByteArrayInputStream(xmlStr.getBytes()));
      assertNotNull("Unexpected null object", obj);
      assertTrue("Unexpected return type: " + obj.getClass().getName(), obj instanceof Composite);
      assertEquals(exp, obj);
   }

   /**
    * Setup the required jaxrpc-mapping meta data
    */
   private JavaWsdlMapping getJavaWSDLMapping() throws Exception
   {
      JavaWsdlMappingFactory factory = JavaWsdlMappingFactory.newInstance();
      URL mappingURL = getResourceURL("common/jbossxb/ComplexTypesService_RPC.xml");
      JavaWsdlMapping javaWsdlMapping = factory.parse(mappingURL);
      return javaWsdlMapping;
   }
}
