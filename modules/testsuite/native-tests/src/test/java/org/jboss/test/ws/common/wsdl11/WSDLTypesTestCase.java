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

import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.test.JBossWSTest;

/**
 * The WSDL may contain or import XSD schema definitions.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 17-Aug-2005
 */
public class WSDLTypesTestCase extends JBossWSTest
{
   public void testIncludedSchema() throws Exception
   {
      File wsdlLocation = getResourceFile("tools/wsdl11/TestServiceXsdInclude.wsdl");
      assertTrue(wsdlLocation.exists());

      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlLocation.toURL());

      JBossXSModel xsModel = WSDLUtils.getSchemaModel(wsdlDefinitions.getWsdlTypes());
      XSTypeDefinition typeDefinition = xsModel.getTypeDefinition("SimpleUserType", "http://org.jboss.ws/jaxrpc/types");
      assertNotNull(typeDefinition);
   }

   public void testImportedSchema() throws Exception
   {
      File wsdlLocation = getResourceFile("tools/wsdl11/TestServiceXsdImport.wsdl");
      assertTrue(wsdlLocation.exists());

      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = factory.parse(wsdlLocation.toURL());

      JBossXSModel xsModel = WSDLUtils.getSchemaModel(wsdlDefinitions.getWsdlTypes());
      XSTypeDefinition typeDefinition = xsModel.getTypeDefinition("SimpleUserType", "http://org.jboss.ws/jaxrpc/types");
      assertNotNull(typeDefinition);
   }
}
