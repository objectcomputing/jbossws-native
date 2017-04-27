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
package org.jboss.test.ws.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMapping;

import org.apache.xerces.xs.XSModel;
import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.WSDL11Validator;
import org.jboss.test.ws.tools.validation.WSDLValidator;
import org.jboss.ws.Constants;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.tools.JavaToWSDL;
import org.jboss.ws.tools.JavaToXSD;
import org.jboss.ws.tools.WSToolsConstants;
import org.jboss.ws.tools.client.ServiceCreator;
import org.jboss.ws.tools.exceptions.JBossWSToolsException;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestHelper;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.JavaUtils;
import org.w3c.dom.Element;

/**
 *  Base class for the Tools Tests
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Sep 5, 2005
 */
public class WSToolsBase extends JBossWSTest
{
   protected static final String SCHEMA_NAMESPACES = "xmlns='http://www.w3.org/2001/XMLSchema' " + "xmlns:soap11-enc='http://schemas.xmlsoap.org/soap/encoding/' "
         + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";

   protected String OUT_DIR = "tools/";

   private TypeMapping typeMapping = null;

   public Class loadClass(String cname) throws ClassNotFoundException
   {
      return JavaUtils.loadJavaType(cname);
   }

   public void mkdirs(String path)
   {
      createResourceFile(path).mkdirs();
   }

   /** Get the Schema as a String */
   public String generateSchema(QName xmlType, Class javaType) throws Exception
   {
      String nsuri = xmlType.getNamespaceURI();
      JavaToXSD javaToXSD = new JavaToXSD();
      JBossXSModel xsmodel = javaToXSD.generateForSingleType(xmlType, javaType);
      return xsmodel.serialize();
   }

   /** Get the Schema as a String */
   public String generateSchema(QName xmlType, Class javaType, Map packageNamespace) throws Exception
   {
      String nsuri = xmlType.getNamespaceURI();
      JavaToXSD javaToXSD = new JavaToXSD();
      javaToXSD.setPackageNamespaceMap(packageNamespace);
      JBossXSModel xsmodel = javaToXSD.generateForSingleType(xmlType, javaType);
      return xsmodel.serialize();
   }

   /** Get the Schema as an XSModel */
   public XSModel generateSchemaXSModel(QName xmlType, Class javaType) throws Exception
   {
      String nsuri = xmlType.getNamespaceURI();
      JavaToXSD javaToXSD = new JavaToXSD();
      return javaToXSD.generateForSingleType(xmlType, javaType);
   }

   /** Parse a schema */
   public XSModel parseSchema(URL url)
   {
      JavaToXSD javaToXSD = new JavaToXSD();
      return javaToXSD.parseSchema(url);
   }

   /**
    * Parse a set of schema files given a map of namespace versus schema locations
    *
    * @param schemaLocationsMap
    * @return
    */
   public XSModel parseSchema(Map schemaLocationsMap)
   {
      JavaToXSD javaToXSD = new JavaToXSD();
      return javaToXSD.parseSchema(schemaLocationsMap);
   }

   /**
    * Validate that two interfaces are identical
    * Check for imports is done as an extra step
    * @throws Exception
    */
   public void assertExactSourceFiles(File file1, File file2) throws Exception
   {

      JBossSourceComparator sc = new JBossSourceComparator(file1, file2);
      assertTrue("Source Files Match:", sc.validate());
      sc.validateImports();
   }

   protected WSDLDefinitions getWSDLDefinitions(File wsdlFile) throws MalformedURLException
   {
      WSDLDefinitionsFactory wsdlFactory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlDefinitions = wsdlFactory.parse(wsdlFile.toURL());
      return wsdlDefinitions;
   }

   protected WSDLDefinitions generateWSDL(Class seiClass, String serviceName, String wsdlPath, String targetNamespace, String typeNamespace, Style style, Map featureMap)
         throws IOException
   {
      JavaToWSDL jwsdl = new JavaToWSDL(Constants.NS_WSDL11);
      jwsdl.setServiceName(serviceName);
      jwsdl.setTargetNamespace(targetNamespace);
      jwsdl.setTypeNamespace(typeNamespace);
      jwsdl.setStyle(style);
      //Add the features
      Iterator keys = featureMap.keySet().iterator();
      while (keys.hasNext())
      {
         String key = (String)keys.next();
         Boolean value = (Boolean)featureMap.get(key);
         jwsdl.addFeature(key, value.booleanValue());
      }
      WSDLDefinitions wsdl = jwsdl.generate(seiClass);
      typeMapping = jwsdl.getTypeMapping();
      return wsdl;
   }

   protected Map getBasicFeatures()
   {
      Map fmap = new HashMap();
      fmap.put(WSToolsConstants.WSTOOLS_FEATURE_RESTRICT_TO_TARGET_NS, new Boolean(true));
      return fmap;
   }

   protected TypeMapping getLastGeneratedTypeMapping()
   {
      return typeMapping;
   }

   //   protected void generateMappingFile(String packageName, WSDLDefinitions wsdl, TypeMapping typeMapping, String serviceName, String fileLoc, Class seiClass,
   //         String typeNamespace) throws IOException
   //   {
   //      MappingFileGenerator mgf = new MappingFileGenerator(wsdl, new LiteralTypeMapping());
   //      mgf.setPackageName(packageName);
   //      mgf.setServiceName(serviceName);
   //      if (seiClass != null)
   //         mgf.setServiceEndpointInterface(seiClass);
   //      if (typeNamespace != null && typeNamespace.length() > 0)
   //         mgf.setTypeNamespace(typeNamespace);
   //      JavaWsdlMapping jwm = mgf.generate();
   //      FileWriter fw = new FileWriter(fileLoc);
   //      fw.write(DOMWriter.printNode(DOMUtils.parse(jwm.serialize()), true));
   //      fw.close();
   //   }

   protected void generateServiceFile(String packageName, WSDLDefinitions wsdl, String location) throws IOException
   {
      ServiceCreator sc = new ServiceCreator();
      sc.setPackageName(packageName);
      sc.setDirLocation(createResourceFile(location));
      sc.setWsdl(wsdl);
      sc.createServiceDescriptor();
   }

   protected void validateXML(String fixtureFile, String genFile) throws Exception
   {
      File wsdlfix = new File(fixtureFile);
      Element exp = DOMUtils.parse(wsdlfix.toURL().openStream());
      File wsdlFile = new File(genFile);
      assertNotNull("Generated WSDL File exists?", wsdlFile);
      Element was = DOMUtils.parse(wsdlFile.toURL().openStream());
      assertEquals(exp, was);
   }

   protected void appendWSDLNamespaceToSchema(Element schemaEl) throws IOException
   {
      schemaEl.setAttribute("xmlns:wsdl", Constants.NS_WSDL11);
   }

   protected void removeSoapEncNamespaceFromSchema(Element schemaEl) throws IOException
   {
      //Remove the soap-enc namespace
      schemaEl.removeAttribute("xmlns:soap11-enc");
   }

   protected boolean semanticallyValidateWSDL(String expPath, String genpath) throws Exception
   {
      boolean bool = true;
      File wsdlfix = new File(expPath);
      Element exp = DOMUtils.parse(wsdlfix.toURL().openStream());
      File wsdlFile = new File(genpath);
      assertTrue("Generated WSDL File exists", wsdlFile.exists());
      Element was = DOMUtils.parse(wsdlFile.toURL().openStream());
      //assertEquals(exp,was);
      //Now that we have figured out that the wsdl files are well formed,
      //lets do the semantic wsdl validation
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlExp = factory.parse(wsdlfix.toURL());

      WSDLDefinitions wsdlActual = factory.parse(wsdlFile.toURL());
      WSDLValidator validator = new WSDL11Validator();
      try
      {
         bool = validator.validate(wsdlExp, wsdlActual);
         if (bool == false)
            fail("WSDL do not match");
      }
      catch (JBossWSToolsException e)
      {
         fail(e.getLocalizedMessage());
      }
      catch (Exception e)
      {
         fail("Unknown exception:" + e.getLocalizedMessage());
      }
      return bool;
   }
}
