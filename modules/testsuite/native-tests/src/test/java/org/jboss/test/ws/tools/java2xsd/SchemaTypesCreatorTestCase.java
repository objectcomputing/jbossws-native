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
import java.math.BigDecimal;

import javax.xml.namespace.QName;

import org.jboss.test.ws.tools.jbws_161.customexceptions.UserException;
import org.jboss.test.ws.tools.sei.Derived;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSTypeDefinition;
import org.jboss.ws.tools.schema.SchemaTypeCreator;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 *  Testcase that unit SchemaTypesCreator in generating the types
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 31, 2005
 */
public class SchemaTypesCreatorTestCase extends JBossWSTest
{
   String namespace = "<schema targetNamespace='http://jboss.org/types' "+
   " xmlns='http://www.w3.org/2001/XMLSchema' "+
   " xmlns:soap11-enc='http://schemas.xmlsoap.org/soap/encoding/' "+
   " xmlns:tns='http://jboss.org/types' "+
   " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>";
   String base  =  "<complexType name='Base'>" +
                      "<sequence> "+
                         "<element name='a' type='int'/>" +
                         "<element name='b' type='int'/>"+
                      "</sequence>"+
                   "</complexType>";
   String derived =  "<complexType name='Derived'>" +
                       "<complexContent>"+
                           "<extension base='tns:Base'> " +
                             "<sequence>"+
                                 "<element name='x' type='int'/>"+
                             "</sequence>"+
                           "</extension>"+
                       "</complexContent>"+
                      "</complexType>";
   String derivedArray ="<complexType name='Derived.Array'> <sequence>"+
   "<element maxOccurs='unbounded' minOccurs='0' name='value' nillable='true' "+
   "type='tns:Derived'/></sequence></complexType>";
   String derivedArrayArray  ="<complexType name='Derived.Array.Array'><sequence> "+
   "<element maxOccurs='unbounded' minOccurs='0' name='value' nillable='true' "+
   "type='tns:Derived.Array'/> </sequence></complexType>";

   public void testint()
   {
      SchemaTypeCreator tc = getSchemaCreator();
      JBossXSTypeDefinition xt = tc.generateType(null,int.class);
      assertNotNull(xt);
   }

   public void testInteger()
   {
      SchemaTypeCreator tc = getSchemaCreator();
      JBossXSTypeDefinition xt = tc.generateType(null,Integer.class);
      assertNotNull(xt);
   }

   public void testintArray() throws IOException
   {
      String exp = namespace +
      " <complexType name='int.Array'><sequence>"+
      "<element maxOccurs='unbounded' minOccurs='0' name='value' type='int'/>"+
      "</sequence></complexType></schema>";
      SchemaTypeCreator tc = getSchemaCreator();
      QName xmlType = new QName("http://jboss.org/types", "int.Array");
      JBossXSTypeDefinition xt = tc.generateType(xmlType, int[].class);
      assertEquals(DOMUtils.parse(exp),DOMUtils.parse(tc.getXSModel().serialize()));
   }

   public void testBigDecimalArray() throws IOException
   {
      String exp = namespace +
      " <complexType name='BigDecimal.Array'><sequence>"+
      "<element maxOccurs='unbounded' minOccurs='0' name='value' nillable='true' type='decimal'/>"+
      "</sequence></complexType></schema>";
      SchemaTypeCreator tc = getSchemaCreator();
      QName xmlType = new QName("http://jboss.org/types", "BigDecimal.Array");
      JBossXSTypeDefinition xt = tc.generateType(xmlType, BigDecimal[].class);
      assertEquals(DOMUtils.parse(exp),DOMUtils.parse(tc.getXSModel().serialize()));
   }

   public void testByteWrapperArray() throws IOException
   {
      String exp = namespace + "<complexType name='Byte.Array'> <sequence>"+
                   "<element maxOccurs='unbounded' minOccurs='0' name='value' "+
                   " nillable='true' type='byte'/> </sequence> </complexType> </schema>";

      QName xmlType = new QName("http://jboss.org/types", "Byte.Array");
      SchemaTypeCreator tc = getSchemaCreator();
      JBossXSTypeDefinition xt = tc.generateType(xmlType, Byte[].class);
      JBossXSModel xsmodel = tc.getXSModel();
      assertEquals(DOMUtils.parse(exp),DOMUtils.parse(tc.getXSModel().serialize()));
   }

   public void testDerivedArray() throws IOException
   {
      String exp = namespace + base +derived + derivedArray +"</schema>";

      SchemaTypeCreator tc = getSchemaCreator();
      tc.addPackageNamespaceMapping(Derived.class.getPackage().getName(), "http://jboss.org/types");
      QName xmlType = new QName("http://jboss.org/types", "Derived.Array");
      JBossXSTypeDefinition xt = tc.generateType(xmlType, Derived[].class);
      assertEquals(DOMUtils.parse(exp),DOMUtils.parse(tc.getXSModel().serialize()));
   }

   public void testDerivedArrayArray() throws IOException
   {
      String exp = namespace + base +derived +derivedArray + derivedArrayArray +"</schema>";
      SchemaTypeCreator tc = getSchemaCreator();
      tc.addPackageNamespaceMapping(Derived.class.getPackage().getName(), "http://jboss.org/types");
      QName xmlType = new QName("http://jboss.org/types", "Derived.Array.Array");
      JBossXSTypeDefinition xt = tc.generateType(xmlType, Derived[][].class);
      assertEquals(DOMUtils.parse(exp),DOMUtils.parse(tc.getXSModel().serialize()));
   }

   public void testDerivedArrayArrayArray() throws IOException
   {
      String exp = namespace + base +derived +derivedArray + derivedArrayArray +
      "<complexType name='Derived.Array.Array.Array'><sequence>"+
      "<element maxOccurs='unbounded' minOccurs='0' name='value' nillable='true' "+
      "type='tns:Derived.Array.Array'/></sequence></complexType>"+"</schema>";

      SchemaTypeCreator tc = getSchemaCreator();
      tc.addPackageNamespaceMapping(Derived.class.getPackage().getName(), "http://jboss.org/types");
      QName xmlType = new QName("http://jboss.org/types", "Derived.Array.Array.Array");
      JBossXSTypeDefinition xt = tc.generateType(xmlType, Derived[][][].class);
      assertEquals(DOMUtils.parse(exp),DOMUtils.parse(tc.getXSModel().serialize()));
   }

   public void testUserException() throws IOException
   {
      String exp = namespace + "<complexType name='UserException'><sequence/></complexType>" +
                   "<element name='UserException' type='tns:UserException'/></schema>";
      SchemaTypeCreator tc = getSchemaCreator();
      QName xmlType = new QName("http://jboss.org/types", "UserException");
      JBossXSTypeDefinition xt = tc.generateType(xmlType, UserException.class);
      Element exp1 = DOMUtils.parse(exp);
      assertEquals(exp1,DOMUtils.parse(tc.getXSModel().serialize()));
   }

   private SchemaTypeCreator getSchemaCreator()
   {
      SchemaTypeCreator tc = new SchemaTypeCreator();
      return tc;
   }
}
