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
package org.jboss.test.ws.tools.java2xsd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.test.ws.common.jbossxb.complex.Derived;
import org.jboss.test.ws.common.jbossxb.simple.SimpleUserType;
import org.jboss.ws.Constants;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.tools.JavaToXSD;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 * Test the JBossXS Model
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 2, 2005
 */

public class SchemaModelTestCase extends JBossWSTest
{
    public void testJBossXSSchemaModel() throws IOException
    {
       String exp = "<schema  targetNamespace='http://org/jboss/test/ws/jaxb/simple' " +
            "xmlns:tns='http://org/jboss/test/ws/jaxb/simple' " +
            "xmlns='http://www.w3.org/2001/XMLSchema' " +
            "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
            "xmlns:soap11-enc='http://schemas.xmlsoap.org/soap/encoding/'>" +
            "<complexType name='SimpleUserType'>" +
            "<sequence>" +
            "<element name='a' type='int'/>" +
            "<element name='b' type='int'/>" +
            "<element name='d' nillable='true' type='dateTime'/>" +
            "</sequence>" +
            "</complexType>" +
            "</schema>";
       String targetNS = "http://org/jboss/test/ws/jaxb/simple";
       QName qn = new QName(targetNS,"SimpleUserType", Constants.PREFIX_TNS);
       JavaToXSD jxsd = new JavaToXSD();
       //String act = jxsd.generateSchemaAsString(qn,SimpleUserType.class) ;
       String act = jxsd.generateForSingleType(qn,SimpleUserType.class).serialize() ;
       Element expEl =  DOMUtils.parse(exp);
       Element actEl =  DOMUtils.parse(act);
       assertEquals(expEl,actEl);
       //assertEquals(exp,act);
    }

    /**
     * Test serialization of complex types with inheritance
     * @throws Exception
     */

    public void testComplexTypesInheritance() throws Exception
    {
       String exp = "<schema  targetNamespace='http://org.jboss.ws/types' " +
            "xmlns:tns='http://org.jboss.ws/types' " +
             "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "+
             "xmlns:soap11-enc='http://schemas.xmlsoap.org/soap/encoding/' " +
             "xmlns='http://www.w3.org/2001/XMLSchema'>"+
                "<complexType name='Base'>" +
                   "<sequence> "+
                      "<element name='a' type='int'/>" +
                      "<element name='b' type='int'/>"+
                   "</sequence>"+
                 "</complexType>"+
                 "<complexType name='Derived'>" +
                   "<complexContent>"+
                       "<extension base='tns:Base'> " +
                             "<sequence>"+
                                  "<element name='x' type='int'/>"+
                              "</sequence>"+
                        "</extension>"+
                    "</complexContent>"+
                  "</complexType>"+
              "</schema>";
       String TARGET_NAMESPACE = "http://org.jboss.ws/types";
       QName xmlType = new QName(TARGET_NAMESPACE, "Derived", "ns1");
       String nsuri = xmlType.getNamespaceURI();

       JavaToXSD javaToXSD = new JavaToXSD();
       Map packageNamespace = new HashMap();
       packageNamespace.put(Derived.class.getPackage().getName(), nsuri);
       javaToXSD.setPackageNamespaceMap(packageNamespace);

       JBossXSModel xsmodel =  javaToXSD.generateForSingleType(xmlType, Derived.class);
       Element expEl =  DOMUtils.parse(exp);
       Element actEl =  DOMUtils.parse(xsmodel.serialize());
       assertEquals(expEl,actEl);
    }
}

