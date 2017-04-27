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
package org.jboss.test.ws.common.jbossxb.array;

import java.io.StringWriter;
import java.math.BigDecimal;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSModel;
import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBConstants;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBMarshallerImpl;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.wsf.common.DOMUtils;

/**
 * Test the JAXB marshalling of array types
 *
 * @author Thomas.Diesler@jboss.org
 * @since 29-Apr-2005
 */
public class BigDecimalArrayMarshallerTestCase extends WSToolsBase
{

   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/types";

   private static final QName xmlName = new QName(TARGET_NAMESPACE, "arrayOfBigDecimal", "ns1");

   private static final QName xmlType = new QName(TARGET_NAMESPACE, "BigDecimalArray", "ns1");

   public void testGenerateSchema() throws Exception
   {
      String xsdSchema = generateSchema(xmlType, BigDecimal[].class);

      String exp = "<schema targetNamespace='http://org.jboss.ws/types' " + SCHEMA_NAMESPACES
            + " xmlns:tns='http://org.jboss.ws/types'>" + " <complexType name='BigDecimalArray'>" + "  <sequence>"
            + "   <element name='value' type='decimal' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" + "  </sequence>"
            + " </complexType>" + "</schema>";

      assertEquals(DOMUtils.parse(exp), DOMUtils.parse(xsdSchema));
   }

   public void testMarshallArrayType() throws Exception
   {
      BigDecimal[] objArr = new BigDecimal[] { new BigDecimal(100), new BigDecimal(200), new BigDecimal(300) };

      XSModel model = generateSchemaXSModel(xmlType, BigDecimal[].class);

//      XSModel model = XsdBinder.loadSchema("<schema targetNamespace='http://org.jboss.ws/types' " + SCHEMA_NAMESPACES
//      + " xmlns:tns='http://org.jboss.ws/types'>" + " <complexType name='BigDecimalArray'>" + "  <sequence>"
//      + "   <element name='value' type='decimal' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" + "  </sequence>"
//      + " </complexType>" + "</schema>", "UTF-8");
      StringWriter strwr;
      JBossXBMarshallerImpl marshaller = new JBossXBMarshallerImpl();
      marshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      marshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      marshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
      marshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());

      strwr = new StringWriter();
      marshaller.marshal(objArr, strwr);

      String was = strwr.toString();
      assertNotNull("Resulting fragment cannot be null", was);
      assertTrue("Resulting fragment cannot be empty", was.length() > 0);

      String exp = "<ns1:arrayOfBigDecimal xmlns:ns1='" + TARGET_NAMESPACE
            + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" + " <value>100</value>" + " <value>200</value>"
            + " <value>300</value>" + "</ns1:arrayOfBigDecimal>";
      assertEquals(DOMUtils.parse(exp), DOMUtils.parse(was));
   }

   /**
    * Setup the required jaxrpc-mapping meta data
    */
   private JavaWsdlMapping getJavaWSDLMapping()
   {
      JavaWsdlMapping javaWsdlMapping = new JavaWsdlMapping();
      /*
       * This mapping should be optional JavaXmlTypeMapping xmlTypeMapping = new
       * JavaXmlTypeMapping(javaWsdlMapping);
       * xmlTypeMapping.setJavaType("java.math.BigDecimal[]");
       * xmlTypeMapping.setRootTypeQName(xmlType);
       * xmlTypeMapping.setQnameScope("complexType");
       * javaWsdlMapping.addJavaXmlTypeMappings(xmlTypeMapping);
       */
      return javaWsdlMapping;
   }
}
