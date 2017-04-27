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

import java.io.File;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSModel;
import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBConstants;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBMarshallerImpl;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMappingFactory;
import org.jboss.wsf.common.DOMUtils;

/**
 * Test the JAXB marshalling of complex types
 *
 * @author Thomas.Diesler@jboss.org
 * @since 18-Oct-2004
 */
public class ComplexTypeMarshallerTestCase extends WSToolsBase
{

   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/types";

   /** Get the URL to the defining schema */
   protected XSModel getSchemaModel(QName xmlType, Class javaType) throws Exception
   {
      File xsdFile = getResourceFile("common/jbossxb/ComplexTypesService_RPC.xsd");
      assertTrue(xsdFile.exists());

      return parseSchema(xsdFile.toURL());
   }

   public void testBaseType() throws Exception
   {
      Base obj = new Base();
      obj.a = 100;
      obj.setB(200);

      QName xmlName = new QName(TARGET_NAMESPACE, "Base_1", "ns1");
      QName xmlType = new QName(TARGET_NAMESPACE, "Base", "ns1");

      XSModel model = getSchemaModel(xmlType, Base.class);

      StringWriter strwr;
      JBossXBMarshallerImpl marshaller = new JBossXBMarshallerImpl();
      marshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      marshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
      marshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      marshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());

      strwr = new StringWriter();
      marshaller.marshal(obj, strwr);

      String exp = 
         "<ns1:Base_1 xmlns:ns1='" + TARGET_NAMESPACE + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" + 
         " <a>100</a>" + 
         " <b>200</b>" + 
         "</ns1:Base_1>";
      
      String was = strwr.toString();
      assertEquals(DOMUtils.parse(exp), DOMUtils.parse(was)); 
   }

   public void testDerivedType() throws Exception
   {
      Derived obj = new Derived();
      obj.a = 100;
      obj.setB(200);
      obj.x = 300;

      QName xmlName = new QName(TARGET_NAMESPACE, "Derived_1", "ns1");
      QName xmlType = new QName(TARGET_NAMESPACE, "Derived", "ns1");

      XSModel model = getSchemaModel(xmlType, Derived.class);

      StringWriter strwr;
      JBossXBMarshallerImpl marshaller = new JBossXBMarshallerImpl();
      marshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      marshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
      marshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      marshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());

      strwr = new StringWriter();
      marshaller.marshal(obj, strwr);

      String exp = 
         "<ns1:Derived_1 xmlns:ns1='" + TARGET_NAMESPACE + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" + 
         " <a>100</a>" + 
         " <b>200</b>" + 
         " <x>300</x>" + 
         "</ns1:Derived_1>";

      String was = strwr.toString();
      assertEquals(DOMUtils.parse(exp), DOMUtils.parse(was));
   }

   public void testCompositeType() throws Exception
   {
      QName xmlName = new QName(TARGET_NAMESPACE, "CompositeType_1", "ns1");
      QName xmlType = new QName(TARGET_NAMESPACE, "Composite", "ns1");

      Composite obj = new Composite();
      obj.integer = new BigInteger("100");
      obj.string = "Hello World!";
      obj.composite = null;
      obj.dateTime = null;
      obj.qname = null;

      Composite sub = new Composite();
      sub.integer = new BigInteger("200");
      sub.string = "Hello Sub World!";
      obj.composite = null;
      sub.dateTime = null;
      sub.qname = null;

      obj.composite = sub;

      XSModel model = getSchemaModel(xmlType, Composite.class);

      StringWriter strwr;
      JBossXBMarshallerImpl marshaller = new JBossXBMarshallerImpl();
      marshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      marshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
      marshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      marshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());

      strwr = new StringWriter();
      marshaller.marshal(obj, strwr);

      String exp = 
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

      String was = strwr.toString();
      assertEquals(DOMUtils.parse(exp), DOMUtils.parse(was));
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
