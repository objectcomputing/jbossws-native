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
package org.jboss.test.ws.tools;

import java.util.HashMap;

import javax.xml.namespace.QName;

import org.jboss.test.ws.common.jbossxb.complex.Base;
import org.jboss.test.ws.common.jbossxb.complex.Composite;
import org.jboss.test.ws.common.jbossxb.complex.Derived;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/** Test the XSD schema generator
 *
 * @author Thomas.Diesler@jboss.org
 * @author anil.saldhana@jboss.org
 * @since 22-Jan-2005
 */
public class SchemaGeneratorTestCase extends WSToolsBase
{

   private static final String SCHEMA_NAMESPACES =
      "targetNamespace='http://org.jboss.ws/types' " +
      "xmlns='http://www.w3.org/2001/XMLSchema' " +
      "xmlns:soap11-enc='http://schemas.xmlsoap.org/soap/encoding/' " +
      "xmlns:tns='http://org.jboss.ws/types' " +
      "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";

   /** Test a base type */
   public void testBaseType() throws Exception
   {
      Class javaType = Base.class;
      QName xmlType = new QName("http://org.jboss.ws/types", "Base");

      String xsdSchema = generateSchema(xmlType, javaType);

      Element expElement = DOMUtils.parse("<schema " + SCHEMA_NAMESPACES + ">" +
         "<complexType name='Base'>" +
         " <sequence>" +
         "  <element name='a' type='int'/>" +
         "  <element name='b' type='int'/>" +
         " </sequence>" +
         "</complexType>" +
         "</schema>");

      Element wasElement = DOMUtils.parse(xsdSchema);
      assertEquals(expElement, wasElement);
   }

   /** Test a derived type */
   public void testDerivedType() throws Exception
   {
      Class javaType = Derived.class;
      String namespace = "http://org.jboss.ws/types";
      QName xmlType = new QName(namespace, "Derived");
      HashMap packageNamespace = new HashMap();
      packageNamespace.put(Derived.class.getPackage().getName(), namespace);

      String xsdSchema = generateSchema(xmlType, javaType, packageNamespace);

      Element expElement = DOMUtils.parse("<schema " + SCHEMA_NAMESPACES + ">" +
         "<complexType name='Base'>" +
         " <sequence>" +
         "  <element name='a' type='int'/>" +
         "  <element name='b' type='int'/>" +
         " </sequence>" +
         "</complexType>" +

         "<complexType name='Derived'>" +
         " <complexContent>" +
         "  <extension base='tns:Base'>" +
         "   <sequence>" +
         "    <element name='x' type='int'/>" +
         "   </sequence>" +
         "  </extension>" +
         " </complexContent>" +
         "</complexType>" +
         "</schema>");

      Element wasElement = DOMUtils.parse(xsdSchema);
      assertEquals(expElement, wasElement);
   }

   /** Test a composite type */
   public void testCompositeType() throws Exception
   {
      Class javaType = Composite.class;
      QName xmlType = new QName("http://org.jboss.ws/types", "Composite");

      String xsdSchema = this.generateSchema(xmlType, javaType);

      //Anil:May05: Schema is now generated in sorted order
      Element expElement = DOMUtils.parse("<schema " + SCHEMA_NAMESPACES + ">" +
         "<complexType name='Composite'>" +
         " <sequence>" +
         "  <element name='composite' nillable='true' type='tns:Composite'/>" +
         "  <element name='dateTime' nillable='true' type='dateTime'/>" +
         "  <element name='integer' nillable='true' type='integer'/>" +
         "  <element name='qname' nillable='true' type='QName'/>" +
         "  <element name='string' nillable='true' type='string'/>" +
         " </sequence>" +
         "</complexType>" +
         "</schema>");

      Element wasElement = DOMUtils.parse(xsdSchema);
      assertEquals(expElement, wasElement);
   }

   /** Test BigDecimalArray type */
   public void testBigDecimalArrayType() throws Exception
   {
      Class javaType = BigDecimalArray.class;
      QName xmlType = new QName("http://org.jboss.ws/types", "BigDecimalArray");

      String xsdSchema = this.generateSchema(xmlType, javaType);

      Element expElement = DOMUtils.parse("<schema " + SCHEMA_NAMESPACES + ">" +
         "<complexType name='BigDecimalArray'>" +
         " <sequence>" +
         "  <element name='value' nillable='true' type='decimal' minOccurs='0' maxOccurs='unbounded'/>" +
         " </sequence>" +
         "</complexType>" +
         "</schema>");

      Element wasElement = DOMUtils.parse(xsdSchema);
      assertEquals(expElement, wasElement);
   }
}
