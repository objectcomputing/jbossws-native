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
package org.jboss.test.ws.common.jbossxb.docstyle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSModel;
import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBConstants;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBUnmarshallerImpl;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMappingFactory;

/**
 * Test the JAXB unmarshalling of complex types
 *
 * @author Thomas.Diesler@jboss.org
 * @since 18-Oct-2004
 */
public class DocumentStyleUnmarshallerTestCase extends WSToolsBase
{

   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/jaxrpc/types";

   protected XSModel getSchemaModel() throws Exception
   {
      File xsdFile = getResourceFile("common/jbossxb/DocumentStyle.xsd");
      assertTrue(xsdFile.exists());

      return parseSchema(xsdFile.toURL());
   }

   public void testEchoStringRequest() throws Exception
   {
      String xmlStr = "" + "<ns1:echoString xmlns:ns1='" + TARGET_NAMESPACE
            + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" + " <String_1>Hello</String_1>"
            + " <String_2>world!</String_2>" + "</ns1:echoString>";

      QName xmlName = new QName(TARGET_NAMESPACE, "echoString");
      QName xmlType = new QName(TARGET_NAMESPACE, "echoString");

      XSModel model = getSchemaModel();

      EchoString_RequestStruct exp = new EchoString_RequestStruct("Hello", "world!");

      JBossXBUnmarshallerImpl unmarshaller = new JBossXBUnmarshallerImpl();
      unmarshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      unmarshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());
      unmarshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      unmarshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);

      Object obj = unmarshaller.unmarshal(new ByteArrayInputStream(xmlStr.getBytes()));

      assertEquals(exp, obj);
   }

   /**
    * Setup the required jaxrpc-mapping meta data
    */
   private JavaWsdlMapping getJavaWSDLMapping() throws Exception
   {
      JavaWsdlMappingFactory factory = JavaWsdlMappingFactory.newInstance();
      URL mappingURL = getResourceURL("common/jbossxb/DocumentStyle.xml");
      JavaWsdlMapping javaWsdlMapping = factory.parse(mappingURL);
      return javaWsdlMapping;
   }
}
