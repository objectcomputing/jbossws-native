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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.jboss.ws.core.utils.JBossWSEntityResolver;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSComplexTypeDefinition;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSElementDeclaration;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSEntityResolver;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSParticle;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.ws.tools.JavaToXSD;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.xb.binding.sunday.unmarshalling.LSInputAdaptor;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.w3c.dom.ls.LSInput;


/**
 * Tests the Schema Parsing / creation capability of the Xerces Schema Parser
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 13, 2005
 */

public class XercesSchemaParserTestCase extends JBossWSTest
{
   // Set up the test
   protected void setUp()
   {
      createDir("tools/wsdlwritedir");
   }

   public void createDir(String path)
   {
      File file = createResourceFile(path);
      if (file.exists() == false)
         file.mkdirs();
   }

   /**
    * Test a basic schema with one or more complex types
    * @throws Exception
    */
   public void testCustomTypesSchema() throws Exception
   {
      String typeNS = "http://org.jboss.ws/types";
      XSLoader xsloader = SchemaUtils.getInstance().getXSLoader();
      XSModel xsmodel = xsloader.loadURI(getResourceFile("tools/wsdlfixture/CustomInterface20.xsd").getAbsolutePath());
      assertNotNull("XSModel is null?", xsmodel);

      XSNamedMap xsnamedmap = getXSNamedMap(xsmodel, typeNS);
      assertEquals(2, xsnamedmap.getLength());
      assertNotNull(xsnamedmap.itemByName(typeNS, "SomeException"));
      assertNotNull(xsnamedmap.itemByName(typeNS, "Base"));
   }

   /**
    * Test a complex schema that deals with multiple schema elements
    * and an array of a custom type
    * @throws Exception
    */
   public void testCustomTypesArraySchema() throws Exception
   {
      SchemaUtils utils = SchemaUtils.getInstance();
      String typeNS = "http://org.jboss/types";
      String arrTypeNS = "http://org.jboss/types/arrays/org/jboss/test/ws/tools/jbws_161/custom";

      Map map = new HashMap();
      map.put(typeNS,getResourceURL("/tools/wsdlfixture/customtype/CustomTypeObj.xsd"));
      map.put(arrTypeNS,getResourceURL("/tools/wsdlfixture/customtype/CustomTypeArrays.xsd"));

      XSModel xsmodel = new JavaToXSD().parseSchema(map);
      assertNotNull("XSModel is null?", xsmodel);

      XSNamedMap xsnamedmap = getXSNamedMap(xsmodel, typeNS);
      assertNotNull("XSNamedMap is null?", xsnamedmap);
      assertEquals(1, xsnamedmap.getLength());
      XSObject xobj = xsnamedmap.item(0);
      assertEquals("HelloObj", xobj.getName());
      assertTrue("HelloObj is a complex type?",xobj instanceof XSComplexTypeDefinition);

      xsnamedmap = getXSNamedMap(xsmodel, arrTypeNS);
      assertEquals(1, xsnamedmap.getLength());
      xobj = xsnamedmap.item(0);
      assertEquals("HelloObjArray", xobj.getName());
      assertTrue("HelloObjArray is a complex type?",xobj instanceof XSComplexTypeDefinition);
      XSComplexTypeDefinition complexType = (XSComplexTypeDefinition) xobj;
      XSModelGroupImpl sequence = (XSModelGroupImpl) complexType.getParticle().getTerm();
      XSElementDecl valueElement = (XSElementDecl) ((XSParticleDecl) sequence.getParticles().item(0)).getTerm();
      String name = valueElement.getTypeDefinition().getName();

      assertEquals("HelloObj", name);

      //Test the case when the schema files are parsed one by one
      XSModel newxsmodel = utils.parseSchema(getResourceFile("tools/wsdlfixture/customtype/CustomTypeObj.xsd").getAbsolutePath());
      assertNotNull("XSModel is null?", newxsmodel);
      xsnamedmap = getXSNamedMap(newxsmodel, typeNS);
      assertNotNull("XSNamedMap is null?", xsnamedmap);
      assertEquals(1, xsnamedmap.getLength());
      xobj = xsnamedmap.item(0);
      assertEquals("HelloObj", xobj.getName());
      assertTrue("HelloObj is a complex type?",xobj instanceof XSComplexTypeDefinition);

      newxsmodel = utils.parseSchema(getResourceFile("tools/wsdlfixture/customtype/CustomTypeArrays.xsd").getAbsolutePath());
      assertNotNull("XSModel is null?", newxsmodel);
      xsnamedmap = getXSNamedMap(newxsmodel, arrTypeNS);
      assertNotNull("XSNamedMap is null?", xsnamedmap);
      assertEquals(1, xsnamedmap.getLength());
      xobj = xsnamedmap.item(0);
      assertEquals("HelloObjArray", xobj.getName());
      assertTrue("HelloObjArray is a complex type?",xobj instanceof XSComplexTypeDefinition);
      assertTrue("HelloObjArray is not a Wild Card?",!(xobj instanceof XSWildcard));
      XSComplexTypeDefinition xc = (XSComplexTypeDefinition)xobj;
      XSParticle xspart =  xc.getParticle();
      assertNotNull("Particle is null?",xspart);
      XSTerm xsterm = xspart.getTerm();
      assertNotNull("XSTerm is null?",xsterm);
      assertTrue("XSTerm is a model group",xsterm instanceof XSModelGroup);
      XSModelGroup xm = (XSModelGroup)xsterm;
      XSObjectList objlist = xm.getParticles();
      XSParticle xp = (XSParticle)objlist.item(0);
      assertTrue("First particle is XSElementDeclaration", xp.getTerm() instanceof XSElementDeclaration);

   }

   public void testJBossXBSchemaParsing() throws Exception
   {
      SchemaUtils utils = SchemaUtils.getInstance();
      String typeNS = "http://org.jboss/types";
      String arrTypeNS = "http://org.jboss/types/arrays/org/jboss/test/ws/tools/jbws_161/custom";
      String arr[] = {getResourceFile("tools/wsdlfixture/customtype/CustomTypeObj.xsd").getAbsolutePath(),
                      getResourceFile("tools/wsdlfixture/customtype/CustomTypeArrays.xsd").getAbsolutePath()};
      XSLoader xsloader = utils.getXSLoader();
      Map map = new HashMap();
      map.put(typeNS,getResourceURL("/tools/wsdlfixture/customtype/CustomTypeObj.xsd"));
      map.put(arrTypeNS,getResourceURL("/tools/wsdlfixture/customtype/CustomTypeArrays.xsd"));

      ((XMLSchemaLoader)xsloader).setEntityResolver(new JBossXSEntityResolver(new JBossWSEntityResolver(), map));
      //Construct a StringList
      StringList slist = new StringListImpl(arr, 2);
      XSModel xsmodel = xsloader.loadURIList(slist);
      assertNotNull("XSModel is null?", xsmodel);

      XSNamedMap xsnamedmap = getXSNamedMap(xsmodel, typeNS);
      assertNotNull("XSNamedMap is null?", xsnamedmap);
      assertEquals(1, xsnamedmap.getLength());
      XSObject xobj = xsnamedmap.item(0);
      assertEquals("HelloObj", xobj.getName());
      assertTrue("HelloObj is a complex type?",xobj instanceof XSComplexTypeDefinition);

      xsnamedmap = getXSNamedMap(xsmodel, arrTypeNS);
      assertEquals(1, xsnamedmap.getLength());
      xobj = xsnamedmap.item(0);
      assertEquals("HelloObjArray", xobj.getName());
      assertTrue("HelloObjArray is a complex type?",xobj instanceof XSComplexTypeDefinition);
      XSComplexTypeDefinition complexType = (XSComplexTypeDefinition) xobj;
      XSModelGroupImpl sequence = (XSModelGroupImpl) complexType.getParticle().getTerm();
      XSElementDecl valueElement = (XSElementDecl) ((XSParticleDecl) sequence.getParticles().item(0)).getTerm();
      String name = valueElement.getTypeDefinition().getName();

      //System.out.println("FIXME JBWS-357");
      assertEquals("HelloObj", name);

   }


   /**
    * Test Xerces ability to create XML schema
    * @throws Exception
    */
   public void testJBossSchemaCreation() throws Exception
   {
      String typeNS = "http://org.jboss/types";
      JBossXSModel xsmodel = new JBossXSModel();
      JBossXSComplexTypeDefinition ct = new JBossXSComplexTypeDefinition();
      ct.setName("HelloObj");
      ct.setNamespace(typeNS);
      JBossXSParticle xsp = new JBossXSParticle();
      JBossXSElementDeclaration xsel = new JBossXSElementDeclaration();
      xsel.setName("msg");
      xsel.setTypeDefinition(SchemaUtils.getInstance().getSchemaBasicType("string"));
      xsel.setNillable(true);
      xsp.setTerm(xsel);
      ct.setParticle(xsp);
      xsmodel.addXSComplexTypeDefinition(ct);  //Always add global complex types to global types
      //Lets write the schema into a file
      File xsdOutFile = createResourceFile("tools/wsdlwritedir/HelloObj.xsd");
      xsdOutFile.delete();
      xsmodel.writeTo(new FileOutputStream(xsdOutFile));
   }

   private XSNamedMap getXSNamedMap(XSModel xsmodel, String ns)
   {
      short typedef = XSTypeDefinition.COMPLEX_TYPE;
      XSNamedMap xsnamedmap = xsmodel.getComponentsByNamespace(typedef, ns);
      assertNotNull("XSNamedMap is null?", xsnamedmap);
      return xsnamedmap;
   }

   private SchemaBindingResolver getSchemaBindingResolver(final Map map)
   {
      return new SchemaBindingResolver()
      {
         public String getBaseURI()
         {
            throw new UnsupportedOperationException("getBaseURI is not implemented.");
         }

         public void setBaseURI(String baseURI)
         {
            throw new UnsupportedOperationException("setBaseURI is not implemented.");
         }

         public SchemaBinding resolve(String nsUri, String baseURI, String schemaLocation)
         {
            throw new UnsupportedOperationException("resolve is not implemented.");
         }

         public LSInput resolveAsLSInput(String nsUri, String baseUri, String schemaLocation)
         {
            URL url = (URL)map.get(nsUri);
            if(url != null)
               try
               {
                  return new LSInputAdaptor(url.openStream() , null);
               }
               catch (IOException e)
               {
                   System.out.println("URL is bad for schema parsing");
               }
            return null;
         }
      };
   }
}
