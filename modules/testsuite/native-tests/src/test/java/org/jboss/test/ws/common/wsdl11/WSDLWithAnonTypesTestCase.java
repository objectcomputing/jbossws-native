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
package org.jboss.test.ws.common.wsdl11;

import java.io.File;
import java.io.IOException;

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 * Tests parsing of a wsdl that contains anonymous types
 *
 * @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 * @since Aug 31, 2005
 */
public class WSDLWithAnonTypesTestCase extends WSToolsBase
{
   String wsdlfile = getResourceFile("jaxrpc/anonymous/WEB-INF/wsdl/TestService.wsdl").getPath();

   public void testWSDLParse() throws IOException
   {
      File file = new File(wsdlfile);
      assertTrue(file.exists());
      WSDLDefinitions wsdl = this.getWSDLDefinitions(file);
      assertNotNull("Parsed WSDLDefinitions is null?", wsdl);
      // Now get the XSModel
      JBossXSModel xsmodel = WSDLUtils.getSchemaModel(wsdl.getWsdlTypes());
      assertNotNull("Schema Model is null?", xsmodel);

      // Lets get the global element called "root"
      XSElementDeclaration xe = xsmodel.getElementDeclaration("root", "http://org.jboss.ws/anonymous/types");
      assertNotNull("Global element is not null?", xe);
      checkSchema(DOMUtils.parse(xsmodel.serialize()));
      XSElementDeclaration decl = xsmodel.getElementDeclaration(">root>inside", "http://org.jboss.ws/anonymous/types");
      assertEquals(decl.getName(), "inside");

      XSTypeDefinition defi = xsmodel.getTypeDefinition(">>root>inside", "http://org.jboss.ws/anonymous/types");
      assertEquals(defi.getTypeCategory(), XSTypeDefinition.COMPLEX_TYPE);
      XSComplexTypeDefinition complex = (XSComplexTypeDefinition) defi;
      XSTerm term = complex.getParticle().getTerm();
      assertEquals(term.getType(), XSConstants.MODEL_GROUP);
      XSModelGroup group = (XSModelGroup) term;
      XSParticle particle = (XSParticle) group.getParticles().item(0);
      term = particle.getTerm();
      assertEquals(term.getType(), XSConstants.ELEMENT_DECLARATION);
      assertEquals(((XSElementDeclaration) term).getName(), "data2");
   }

   private void checkSchema(Element gen) throws IOException
   {
      StringBuffer buf = new StringBuffer("<schema targetNamespace='http://org.jboss.ws/anonymous/types' ");
      buf.append(" xmlns:tns='http://org.jboss.ws/anonymous/types' ");
      buf.append(" xmlns:soap11-enc='http://schemas.xmlsoap.org/soap/encoding/'");
      buf.append(" xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'");
      buf.append(" xmlns='http://www.w3.org/2001/XMLSchema'>");
      buf.append(" <complexType name='root'><sequence><element name='data' type='string'/>");
      buf.append("</sequence></complexType>");

      buf.append("<element name='root'><complexType><sequence>");
      buf.append("<element name='inside' minOccurs='1' maxOccurs='10'><complexType><sequence>");
      buf.append("<element name='data2' type='string'/></sequence></complexType>");
      buf.append("</element> <element ref='tns:someOtherElement' minOccurs='1' maxOccurs='20'/>");
      buf.append("</sequence></complexType></element>");
      buf.append("<element name='someOtherElement' type='int'/></schema>");
      Element exp = DOMUtils.parse(buf.toString());
      assertEquals(exp, gen);
   }
}
