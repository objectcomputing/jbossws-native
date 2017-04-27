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

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSModel;
import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBConstants;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBUnmarshallerImpl;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.wsf.common.DOMUtils;

/**
 * Test the JAXB marshalling of array types
 *
 * @author Thomas.Diesler@jboss.org
 * @since 29-Apr-2005
 */
public class CalendarArrayUnmarshallerTestCase extends WSToolsBase
{

   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/types";

   private static final QName xmlName = new QName(TARGET_NAMESPACE, "arrayOfCalendar", "ns1");

   private static final QName xmlType = new QName(TARGET_NAMESPACE, "CalendarArray", "ns1");

   public void testGenerateSchema() throws Exception
   {
      String xsdSchema = generateSchema(xmlType, Calendar[].class);

      String exp = "<schema targetNamespace='http://org.jboss.ws/types' " + SCHEMA_NAMESPACES
            + " xmlns:tns='http://org.jboss.ws/types'>" + " <complexType name='CalendarArray'>" + "  <sequence>"
            + "   <element name='value' type='dateTime' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" + "  </sequence>"
            + " </complexType>" + "</schema>";

      assertEquals(DOMUtils.parse(exp), DOMUtils.parse(xsdSchema));
   }

   public void testUnmarshallArrayType() throws Exception
   {
      XSModel model = generateSchemaXSModel(xmlType, Calendar[].class);

      Calendar[] objArr = null;
      JBossXBUnmarshallerImpl unmarshaller = new JBossXBUnmarshallerImpl();
      unmarshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      unmarshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
      unmarshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      unmarshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());

      String xml = "<ns1:arrayOfCalendar xmlns:ns1='" + TARGET_NAMESPACE + "'>" + " <value>1968-06-16T14:23:55.000Z</value>"
            + " <value>1969-07-17T15:24:56.000Z</value>" + " <value>1970-08-18T16:25:57.000Z</value>" + "</ns1:arrayOfCalendar>";

      objArr = (Calendar[]) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

      assertNotNull("Resulting obj array cannot be null", objArr);
      assertEquals("Unexpected array length", 3, objArr.length);

      Calendar cal1 = new GregorianCalendar(1968, 5, 16, 14, 23, 55);
      cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal2 = new GregorianCalendar(1969, 6, 17, 15, 24, 56);
      cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
      Calendar cal3 = new GregorianCalendar(1970, 7, 18, 16, 25, 57);
      cal3.setTimeZone(TimeZone.getTimeZone("GMT"));

      assertEquals(cal1, objArr[0]);
      assertEquals(cal2, objArr[1]);
      assertEquals(cal3, objArr[2]);
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
       * xmlTypeMapping.setJavaType("java.util.Calendar[]");
       * xmlTypeMapping.setRootTypeQName(xmlType);
       * xmlTypeMapping.setQnameScope("complexType");
       * javaWsdlMapping.addJavaXmlTypeMappings(xmlTypeMapping);
       */
      return javaWsdlMapping;
   }
}
