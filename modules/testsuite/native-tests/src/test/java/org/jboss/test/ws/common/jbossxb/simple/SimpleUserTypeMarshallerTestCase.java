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

import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSModel;
import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBConstants;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBMarshallerImpl;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.MarshalException;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.PackageMapping;
import org.jboss.wsf.common.DOMUtils;

/**
 * Test the JAXB marshalling of a SimpleUserType
 *
 * @author Thomas.Diesler@jboss.org
 * @since 29-Apr-2005
 */
public class SimpleUserTypeMarshallerTestCase extends WSToolsBase
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

   public void testMarshallSimpleUserType() throws Exception
   {
      SimpleUserType obj = new SimpleUserType();

      String exp =
         "<ns1:SimpleUser xmlns:ns1='" + TARGET_NAMESPACE + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
         " <a>0</a>" +
         " <b>0</b>" +
         " <d xsi:nil='1'/>" +
         "</ns1:SimpleUser>";

      String was = marshalSimpleType(obj);
      assertEquals(DOMUtils.parse(exp), DOMUtils.parse(was));
   }

   public void testMarshallSimpleUserTypeWithDate() throws Exception
   {
      TimeZone tz = TimeZone.getDefault();
      TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
      try
      {
         GregorianCalendar cal = new GregorianCalendar(1968, 11, 16);
         SimpleUserType obj = new SimpleUserType(1, 2, cal.getTime());

         String exp =
            "<ns1:SimpleUser xmlns:ns1='" + TARGET_NAMESPACE + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
            " <a>1</a>" +
            " <b>2</b>" +
            " <d>1968-12-16T00:00:00.000Z</d>" +
            "</ns1:SimpleUser>";

         String was = marshalSimpleType(obj);
         assertEquals(DOMUtils.parse(exp), DOMUtils.parse(was));
      }
      finally
      {
         TimeZone.setDefault(tz);
      }
   }

   /**
    * [JBWS-800] ClassCastException when trying to marshal a SybaseTimestamp
    * 
    * http://jira.jboss.org/jira/browse/JBWS-800
    */
   public void testMarshallSimpleUserTypeWithUserDate() throws Exception
   {
      class UserDate extends Date
      {
         public UserDate(long date)
         {
            super(date);
         }
      }

      TimeZone tz = TimeZone.getDefault();
      TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
      try
      {
         GregorianCalendar cal = new GregorianCalendar(1968, 11, 16);
         UserDate userDate = new UserDate(cal.getTime().getTime());
         SimpleUserType obj = new SimpleUserType(1, 2, userDate);

         String exp =
            "<ns1:SimpleUser xmlns:ns1='" + TARGET_NAMESPACE + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
            " <a>1</a>" +
            " <b>2</b>" +
            " <d>1968-12-16T00:00:00.000Z</d>" +
            "</ns1:SimpleUser>";

         String was = marshalSimpleType(obj);
         assertEquals(DOMUtils.parse(exp), DOMUtils.parse(was));
      }
      finally
      {
         TimeZone.setDefault(tz);
      }
   }

   private String marshalSimpleType(SimpleUserType obj) throws Exception, MarshalException
   {
      QName xmlName = new QName(TARGET_NAMESPACE, "SimpleUser", "ns1");
      QName xmlType = new QName(TARGET_NAMESPACE, "SimpleUserType", "ns1");

      XSModel model = generateSchemaXSModel(xmlType, SimpleUserType.class);
      StringWriter strwr;
      JBossXBMarshallerImpl marshaller = new JBossXBMarshallerImpl();
      marshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      marshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
      marshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      marshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());

      strwr = new StringWriter();
      marshaller.marshal(obj, strwr);

      String was = strwr.toString();
      assertNotNull("Resulting fragment cannot be null", was);
      assertTrue("Resulting fragment cannot be empty", was.length() > 0);
      return was;
   }

   public void testMarshallSimpleUserTypeNoPrefix() throws Exception
   {
      SimpleUserType obj = new SimpleUserType();

      QName xmlName = new QName(TARGET_NAMESPACE, "SimpleUser");
      QName xmlType = new QName(TARGET_NAMESPACE, "SimpleUserType");

      XSModel model = generateSchemaXSModel(xmlType, SimpleUserType.class);

      StringWriter strwr;
      try
      {
         JBossXBMarshallerImpl marshaller = new JBossXBMarshallerImpl();
         marshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
         marshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
         marshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
         marshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());

         strwr = new StringWriter();
         marshaller.marshal(obj, strwr);
         fail("The given root element name must be prefix qualified");
      }
      catch (RuntimeException ex)
      {
         // expected: The given root element name must be prefix qualified
      }
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
      packageMapping.setPackageType("org.jboss.test.ws.common.jbossxb");
      return javaWsdlMapping;
   }
}
