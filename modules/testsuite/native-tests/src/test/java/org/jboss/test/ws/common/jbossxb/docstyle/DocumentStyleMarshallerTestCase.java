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

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

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
public class DocumentStyleMarshallerTestCase extends WSToolsBase
{

   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/jaxrpc/types";

   protected XSModel getSchemaModel() throws Exception
   {
      File xsdFile = getResourceFile("common/jbossxb/DocumentStyle.xsd");
      assertTrue(xsdFile.exists());

      return parseSchema(xsdFile.toURL());
   }

   /** Release the schema URL, used for removing temp files */
   protected void releaseSchemaLocations(Map xsdURLMap)
   {
   }

   public void testEchoStringRequest() throws Exception
   {
      EchoString_RequestStruct req = new EchoString_RequestStruct("Hello", "world!");

      QName xmlName = new QName(TARGET_NAMESPACE, "echoString", "ns1");
      QName xmlType = new QName(TARGET_NAMESPACE, "echoString", "ns1");

      XSModel model = getSchemaModel();

      StringWriter strwr;
      JBossXBMarshallerImpl marshaller = new JBossXBMarshallerImpl();
      marshaller.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
      marshaller.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
      marshaller.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
      marshaller.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, getJavaWSDLMapping());

      strwr = new StringWriter();
      marshaller.marshal(req, strwr);

      String exp = "<ns1:echoString xmlns:ns1='" + TARGET_NAMESPACE + "' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>"
            + " <String_1>Hello</String_1>" + " <String_2>world!</String_2>" + "</ns1:echoString>";
      String was = strwr.toString();

      assertEquals(DOMUtils.parse(exp), DOMUtils.parse(was));
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
