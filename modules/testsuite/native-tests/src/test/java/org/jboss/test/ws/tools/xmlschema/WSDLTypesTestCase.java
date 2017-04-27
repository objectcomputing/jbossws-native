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
package org.jboss.test.ws.tools.xmlschema;

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.tools.WSDLToJava;
import org.jboss.ws.tools.interfaces.WSDLToJavaIntf;
import org.jboss.wsf.test.JBossWSTest;

/**
 *  Test case that tests the WSDLTypes
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Mar 17, 2005
 */
public class WSDLTypesTestCase extends JBossWSTest
{
   public void testXMLTypeReturned()
   {
      String filename = "StandardJavaTypesService_DOC_11.wsdl";
      File wsdlFile = getResourceFile("tools/wsdlfixture/" + filename);
      WSDLTypes types = null;
      try
      {
         WSDLToJavaIntf wsdljava = new WSDLToJava();
         wsdljava.setTypeMapping(new LiteralTypeMapping());
         WSDLDefinitions wsdl = wsdljava.convertWSDL2Java(wsdlFile.toURL());
         types = wsdl.getWsdlTypes();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      assertNotNull("WSDLTypes is null?", types);
      QName n = new QName("http://org.jboss.ws/types", "echoCalendar", "tns");
      QName type = types.getXMLType(n);
      assertNotNull("Type returned is null?", type);
      assertEquals("Type match", "echoCalendar", type.getLocalPart());
   }

   public void testXMLSchemaReturned()
   {
      String filename = "StandardJavaTypesService_DOC_11.wsdl";
      File wsdlFile = getResourceFile("tools/wsdlfixture/" + filename);
      WSDLTypes types = null;
      try
      {
         WSDLToJavaIntf wsdljava = new WSDLToJava();
         wsdljava.setTypeMapping(new LiteralTypeMapping());
         WSDLDefinitions wsdl = wsdljava.convertWSDL2Java(wsdlFile.toURL());
         types = wsdl.getWsdlTypes();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      assertNotNull("WSDLTypes is null?", types);
      JBossXSModel xsmodel = WSDLUtils.getSchemaModel(types);
      assertNotNull("XSModel is null?",xsmodel);
      //List xsmodelList = types.getAllSchemaModels();
      //assertNotNull("Schema List is null?", xsmodelList);
   }
}
