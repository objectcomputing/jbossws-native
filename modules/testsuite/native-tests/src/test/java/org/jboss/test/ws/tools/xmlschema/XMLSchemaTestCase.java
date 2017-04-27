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
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.jboss.test.ws.tools.WSToolsBase;

/** All the XML schema related test cases  
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Mar 17, 2005
 */
public class XMLSchemaTestCase extends WSToolsBase
{
   /**
    * Given a set of xml schemas, check if the xmltype
    * returned for a xmlname is valid
    * @throws MalformedURLException 
    */
   public void testXMLTypeReturned() throws MalformedURLException
   {
      String filename = "CustomInterface20.xsd";
      File xsdFile = getResourceFile("tools/wsdlfixture/" + filename);
      XSModel xsmodel = parseSchema(xsdFile.toURL());
      assertNotNull("XSModel is null?", xsmodel);
      XSNamedMap xsmap = xsmodel.getComponentsByNamespace(XSConstants.ELEMENT_DECLARATION, "http://org.jboss.ws/types");
      assertNotNull("XSNamedMap is null?", xsmap);
   }

   /**
    * Check include in schema
    * @throws MalformedURLException 
    */
   public void testXSDInclude() throws MalformedURLException
   {
      String filename = "SchemaMain.xsd";
      File xsdFile = getResourceFile("tools/xsd/schemainclude/" + filename);
      assertTrue("Does Schema file exist?", xsdFile.exists());
      XSModel xsmodel = parseSchema(xsdFile.toURL());
      assertNotNull("XSModel is null?", xsmodel);
      XSNamedMap xsmap = xsmodel.getComponentsByNamespace(XSConstants.TYPE_DEFINITION, "http://org.jboss.ws/types");
      assertNotNull("XSNamedMap is null?", xsmap);
      XSObject xsobj = xsmap.itemByName("http://org.jboss.ws/types", "USAddress");
      assertNotNull("Is XSObject null?", xsobj);
   }

   /**
    * Check bad include in schema
    */
   public void testBadXSDInclude()
   {
      String filename = "SchemaBadMain.xsd";
      File xsdFile = getResourceFile("tools/xsd/schemainclude/" + filename);
      try
      {
         XSModel xsmodel = parseSchema(xsdFile.toURL());
         assertNotNull("XSModel is null?", xsmodel);
         fail("Test should have failed as we have a bad schema include");
      }
      catch (Throwable t)
      {
         //pass
      }
   }

   /**
    * Check import in schema
    * @throws MalformedURLException 
    */
   public void testXSDImport() throws MalformedURLException
   {
      String filename = "SchemaMain.xsd";
      File xsdFile = getResourceFile("tools/xsd/schemaimport/" + filename);
      //Create an Hashmap of <namespace,URL>
      Map schemaMap = new HashMap();
      schemaMap.put("http://org.jboss.ws/types", xsdFile.toURL());
      schemaMap.put("http://org.jboss.ws/types2", getResourceURL("/tools/xsd/schemaimport/SchemaImport.xsd"));
      XSModel xsmodel = parseSchema(schemaMap);
      assertNotNull("XSModel is null?", xsmodel);
      XSNamedMap xsmap = xsmodel.getComponentsByNamespace(XSConstants.TYPE_DEFINITION, "http://org.jboss.ws/types2");
      assertNotNull("XSNamedMap is null?", xsmap);
      XSObject xsobj = xsmap.itemByName("http://org.jboss.ws/types2", "USAddress");
      assertNotNull("Is XSObject null?", xsobj);
   }
}
