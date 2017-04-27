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
package org.jboss.ws.metadata.wsdl.xsd;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xerces.impl.dv.xs.SchemaDVFactoryImpl;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSComplexTypeDefinition;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSElementDeclaration;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSErrorHandler;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSSimpleTypeDefinition;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSStringList;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSTypeDefinition;
import org.jboss.wsf.spi.management.ServerConfig;
import org.jboss.wsf.spi.management.ServerConfigFactory;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;

/**
 *  Util class that deals with XML Schema
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 12, 2005
 */
public class SchemaUtils
{
   private static SchemaUtils ourInstance = new SchemaUtils();

   protected static String xsNS = Constants.NS_SCHEMA_XSD;

   private static Map<Class, QName> toolsTypeMappingOverride = new HashMap<Class, QName>();

   static
   {
      toolsTypeMappingOverride.put(byte[].class, Constants.TYPE_LITERAL_BASE64BINARY);
   }

   public static SchemaUtils getInstance()
   {
      return ourInstance;
   }

   private SchemaUtils()
   {
   }

   /**
    * Checks if the XS Particle is of array type
    * @param xsparticle
    * @return
    */

   public boolean isArrayType(XSParticle xsparticle)
   {
      //Determine if it is an arrayType
      int maxOccurs = xsparticle.getMaxOccurs();
      return xsparticle.getMaxOccursUnbounded() || maxOccurs > 1;
   }

   public static boolean isWrapperArrayType(XSTypeDefinition xst)
   {
      return unwrapArrayType(xst) != null;
   }

   public static XSElementDeclaration unwrapArrayType(XSTypeDefinition xst)
   {
      if (xst instanceof XSComplexTypeDefinition == false)
         return null;

      XSComplexTypeDefinition xc = (XSComplexTypeDefinition)xst;
      if (xc.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_EMPTY)
         return null;

      XSParticle xsp = xc.getParticle();
      if (xsp == null)
         return null;

      XSTerm xsterm = xsp.getTerm();
      if (xsterm instanceof XSModelGroup == false)
         return null;

      XSModelGroup xm = (XSModelGroup)xsterm;
      XSObjectList xo = xm.getParticles();
      if (xo.getLength() != 1)
         return null;

      XSParticle xp = (XSParticle)xo.item(0);
      XSTerm term = xp.getTerm();
      if ((xp.getMaxOccursUnbounded() || xp.getMaxOccurs() > 1) == false || term instanceof XSElementDeclaration == false)
         return null;

      return (XSElementDeclaration)term;
   }

   /**
    * Generate a QName for a simple type
    * @param simple
    * @return
    */
   public static QName handleSimpleType(XSSimpleTypeDefinition simple)
   {
      if (simple == null)
         throw new IllegalArgumentException("XSSimpleTypeDefinition passed is null");

      //Check if the type of SimpleType is a plain xsd type
      if (Constants.NS_SCHEMA_XSD.equals(simple.getNamespace()))
         return createQNameFromXSSimpleType(simple);

      switch (simple.getVariety())
      {
         case XSSimpleTypeDefinition.VARIETY_LIST:
            return handleSimpleType(simple.getItemType());
         case XSSimpleTypeDefinition.VARIETY_UNION:
            XSObjectList list = simple.getMemberTypes();
            if (list.getLength() > 0)
               return handleSimpleType((XSSimpleTypeDefinition)list.item(0));
            throw new WSException("Empty union type not expected");
         case XSSimpleTypeDefinition.VARIETY_ABSENT:
            throw new WSException("Absent variety is not supported in simple types");
      }

      XSTypeDefinition base = simple.getBaseType();
      while (!Constants.NS_SCHEMA_XSD.equals(base.getNamespace()))
         base = base.getBaseType();

      if (!(base instanceof XSSimpleTypeDefinition))
         throw new WSException("Expected base type to be a simple type");

      return new QName(base.getNamespace(), base.getName());
   }

   /**
    * For the basic xsd types, patch prefix and ns uri
    * @param qname
    * @return
    */
   public QName patchXSDQName(QName qname)
   {
      if (qname == null)
         return null;
      return new QName(Constants.NS_SCHEMA_XSD, qname.getLocalPart(), Constants.PREFIX_XSD);
   }

   /**
    * Given a XSTypeDefinition and the QName for the element,
    * create a XSElementDeclaration object
    * @param xmlName Name of the element
    * @param xst Type of the element
    * @return XSElementDeclaration
    */
   public XSElementDeclaration createXSElementDeclaration(QName xmlName, XSTypeDefinition xst)
   {
      String name = xmlName.getLocalPart();
      String ns = xmlName.getNamespaceURI();
      JBossXSElementDeclaration jbel = new JBossXSElementDeclaration(name, ns);
      jbel.setTypeDefinition(xst);
      jbel.setScope(XSConstants.SCOPE_GLOBAL);
      return jbel;
   }

   /**
    * Create a QName given a Simple Type
    * @param xs
    * @return   qname
    */
   public static QName createQNameFromXSSimpleType(XSSimpleTypeDefinition xs)
   {
      String nsuri = xs.getNamespace();
      String localpart = xs.getName();
      if (xsNS.equals(nsuri))
         return new QName(nsuri, localpart, Constants.PREFIX_XSD);
      else return new QName(nsuri, localpart);
   }

   /**
    * Return typemapping override for tools if any
    *
    * @param javaType
    * @return
    */
   public QName getToolsOverrideInTypeMapping(Class javaType)
   {
      return toolsTypeMappingOverride.get(javaType);
   }

   /**
    * Check if a global element exists
    * @param xmlName  QName of the element that needs to be checked
    * @param xsmodel Schema model that is passed
    * @return
    */
   public boolean hasGlobalElement(QName xmlName, XSModel xsmodel)
   {
      if (xmlName == null)
         throw new IllegalArgumentException("xmlName is null");
      if (xsmodel == null)
         throw new IllegalArgumentException("XSModel is null");
      boolean bool = false;
      String name = xmlName.getLocalPart();
      if (name == null)
         throw new IllegalArgumentException("xmlName has a null name");
      String ns = xmlName.getNamespaceURI();
      if (ns == null)
         throw new IllegalArgumentException("xmlName has a null namespace");
      if (xsmodel.getElementDeclaration(name, ns) != null)
         bool = true;
      return bool;
   }

   /**
    * Check if a Complex Type  exists
    * @param xmlType  QName of the Complex Type that needs to be checked
    * @param xsmodel Schema model that is passed
    * @return
    */
   public boolean hasComplexTypeDefinition(QName xmlType, URL xsdLocation)
   {
      if (xsdLocation == null)
         throw new IllegalArgumentException("xsdLocation is null");
      XSModel xsmodel = parseSchema(xsdLocation);
      return this.hasComplexTypeDefinition(xmlType, xsmodel);
   }

   /**
    * Check if a Global Element  exists
    * @param xmlType  QName of the global element that needs to be checked
    * @param xsmodel Schema model that is passed
    * @return
    */
   public boolean hasGlobalElement(QName xmlName, URL xsdLocation)
   {
      if (xmlName == null)
         throw new IllegalArgumentException("xmlName is null");
      if (xsdLocation == null)
         throw new IllegalArgumentException("xsdLocation is null");
      XSModel xsmodel = parseSchema(xsdLocation);
      boolean bool = false;
      String name = xmlName.getLocalPart();
      if (name == null)
         throw new IllegalArgumentException("xmlName has a null name");
      String ns = xmlName.getNamespaceURI();
      if (ns == null)
         throw new IllegalArgumentException("xmlName has a null namespace");
      if (xsmodel.getElementDeclaration(name, ns) != null)
         bool = true;
      return bool;
   }

   /**
    * Check if a Complex Type  exists
    * @param xmlType  QName of the Complex Type that needs to be checked
    * @param xsmodel Schema model that is passed
    * @return
    */
   public boolean hasComplexTypeDefinition(QName xmlType, XSModel xsmodel)
   {
      if (xmlType == null)
         throw new IllegalArgumentException("xmlType is null");
      if (xsmodel == null)
         throw new IllegalArgumentException("XSModel is null");
      boolean bool = false;
      String name = xmlType.getLocalPart();
      if (name == null)
         throw new IllegalArgumentException("xmlName has a null name");
      String ns = xmlType.getNamespaceURI();
      if (ns == null)
         throw new IllegalArgumentException("xmlName has a null namespace");
      if (xsmodel.getTypeDefinition(name, ns) != null)
         bool = true;
      return bool;
   }

   /**
    * Get formatted string for the qname representing the xstype
    * @param xstype
    * @return
    */
   public String getFormattedString(XSTypeDefinition xstype)
   {
      String ns = xstype.getNamespace();
      String name = xstype.getName();
      if (!ns.equals(xsNS))
         name = getPrefix(ns) + ":" + name;

      return name;
   }

   /**
    * Get a QName for a xs type
    * @param xstype
    * @return
    */
   public QName getQName(XSTypeDefinition xstype)
   {
      String prefix = null;
      String ns = xstype.getNamespace();
      String name = xstype.getName();
      if (!ns.equals(xsNS))
         prefix = Constants.PREFIX_TNS;
      else prefix = Constants.PREFIX_XSD;

      return new QName(ns, name, prefix);
   }

   /**
    * Get a schema basic type
    * TODO: Migrate this off of the Xerces Impl
    * @param localpart
    * @return
    */
   public JBossXSTypeDefinition getSchemaBasicType(String localpart)
   {
      JBossXSTypeDefinition xt = null;
      /**
       * Special case: xs:anyType
       */
      if ("anyType".equals(localpart))
      {
         JBossXSComplexTypeDefinition ct = new JBossXSComplexTypeDefinition(localpart, Constants.NS_SCHEMA_XSD);
         ct.setContentType(XSComplexTypeDefinition.CONTENTTYPE_EMPTY);
         xt = ct;
      }
      else
      {
         XSSimpleTypeDefinition xstype = (new SchemaDVFactoryImpl()).getBuiltInType(localpart);
         xt = new JBossXSSimpleTypeDefinition(xstype);

      }
      return xt;
   }

   /**
    * Get an instance of XSLoader that is capable of
    * parsing schema files
    *
    * @return
    */
   public XSLoader getXSLoader()
   {
      XMLSchemaLoader xsloader = new XMLSchemaLoader();
      JBossXSErrorHandler eh = new JBossXSErrorHandler();
      xsloader.setErrorHandler(eh);
      xsloader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", new XMLGrammarPoolImpl());
      return xsloader;
   }

   /**
    * Get an instance of XSLoader that is capable of
    * parsing schema files
    * @param xeh XML Error handler
    * @param xer XML Entity Resolver
    * @return
    */
   public XSLoader getXSLoader(XMLErrorHandler xeh, XMLEntityResolver xer)
   {
      XMLSchemaLoader xsloader = new XMLSchemaLoader();
      xsloader.setEntityResolver(xer);
      xsloader.setErrorHandler(xeh);
      xsloader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", new XMLGrammarPoolImpl());
      return xsloader;
   }

   /**
    * Given a schema file, generate a schema model
    * @param schemaLoc java.net.URL object to the schema file
    * @return schema model
    */
   public XSModel parseSchema(URL schemaLoc)
   {
      return parseSchema(schemaLoc.toExternalForm());
   }

   /**
    * Given a schema file, generate a schema model
    * @param schemaLoc  string representation to the location of schema
    * @return schema model
    */
   public XSModel parseSchema(String schemaLoc)
   {
      XSLoader xsloader = getXSLoader();
      XSModel xsModel = xsloader.loadURI(schemaLoc);
      if (xsModel == null)
         throw new WSException("Cannot parse schema: " + schemaLoc);
      return xsModel;
   }

   /**
    * Given a list of schema locations, parse and
    * provide a Xerces XSModel
    *
    * @param locations
    * @return
    */
   public XSModel parseSchema(List<String> locations)
   {
      JBossXSStringList slist = new JBossXSStringList(locations);
      XSLoader xsloader = getXSLoader();
      return xsloader.loadURIList(slist);
   }

   /**
    * Checks if the XSModel is empty
    * @param xsmodel
    * @return  true (if empty) and false (if not empty)
    */
   public boolean isEmptySchema(XSModel xsmodel)
   {
      if (xsmodel == null)
         return true;
      String targetNS = getTargetNamespace(xsmodel);
      if (targetNS == null)
         throw new WSException("Target Namespace of xsmodel is null");
      XSNamedMap tmap = xsmodel.getComponentsByNamespace(XSConstants.TYPE_DEFINITION, targetNS);
      XSNamedMap emap = xsmodel.getComponentsByNamespace(XSConstants.ELEMENT_DECLARATION, targetNS);

      if (tmap != null && tmap.getLength() > 0)
         return false;
      if (emap != null && emap.getLength() > 0)
         return false;

      return true;
   }

   /**
    * Checks if the XSModel is empty given a namespace
    * @param xsmodel Schema Model to check
    * @param namespace namespace to check components for
    * @return  true (if empty) and false (if not empty)
    */
   public boolean isEmptySchema(XSModel xsmodel, String namespace)
   {
      if (xsmodel == null)
         return true;
      if (namespace == null)
         throw new WSException("Target Namespace of xsmodel is null");
      XSNamedMap tmap = xsmodel.getComponentsByNamespace(XSConstants.TYPE_DEFINITION, namespace);
      XSNamedMap emap = xsmodel.getComponentsByNamespace(XSConstants.ELEMENT_DECLARATION, namespace);

      if (tmap != null && tmap.getLength() > 0)
         return false;
      if (emap != null && emap.getLength() > 0)
         return false;

      return true;
   }

   /**
    * Get the schema definitions as a String
    * @param targetNS The Target Namespace
    * @return
    */
   public static String getSchemaDefinitions(String targetNS)
   {
      StringBuilder buffer = new StringBuilder();
      buffer.append(" targetNamespace='" + targetNS + "'");
      buffer.append(" xmlns='" + Constants.NS_SCHEMA_XSD + "'");
      buffer.append(" xmlns:" + Constants.PREFIX_SOAP11_ENC + "='" + Constants.URI_SOAP11_ENC + "'");
      buffer.append(" xmlns:" + Constants.PREFIX_TNS + "='" + targetNS + "'");
      //buffer.append(" xmlns:" + WSDLConstants.PREFIX_XSD + "='" + WSDLConstants.NS_SCHEMA_XSD + "'");
      buffer.append(" xmlns:" + Constants.PREFIX_XSI + "='" + Constants.NS_SCHEMA_XSI + "'");
      buffer.append(">");
      return buffer.toString();
   }

   /** Get the temp file for a given namespace
    */
   public static File getSchemaTempFile(String targetNS) throws IOException
   {
      if (targetNS.length() == 0)
         throw new IllegalArgumentException("Invalid null target namespace");

      String fname = targetNS;
      if (fname.indexOf("://") > 0)
         fname = fname.substring(fname.indexOf("://") + 3);

      File tmpdir = null;
      try
      {
         SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
         ServerConfig serverConfig = spiProvider.getSPI(ServerConfigFactory.class).getServerConfig();File tmpDir = serverConfig.getServerTempDir();
         tmpdir = serverConfig.getServerTempDir();
         tmpdir = new File(tmpdir.getCanonicalPath() + "/jbossws");
         tmpdir.mkdirs();
      }
      catch (Throwable th)
      {
         // ignore if the server config cannot be found
         // this would be the case if we are on the client side
      }

      fname = fname.replace('/', '_');
      fname = fname.replace(':', '_');
      fname = fname.replace('?', '_');
      
      return File.createTempFile("JBossWS_" + fname, ".xsd", tmpdir);
   }

   /**
    * Get the TargetNamespace from the schema model
    */
   public static String getTargetNamespace(XSModel xsmodel)
   {
      if (xsmodel == null)
         throw new IllegalArgumentException("Illegal Null Argument: xsmodel");
      String targetNS = null;
      StringList slist = xsmodel.getNamespaces();
      int len = slist != null ? slist.getLength() : 0;
      for (int i = 0; i < len; i++)
      {
         String ns = slist.item(i);
         if (Constants.NS_SCHEMA_XSD.equals(ns))
            continue;
         targetNS = ns;
         break;
      }
      return targetNS;
   }

   private String getPrefix(String namespace)
   {
      if (Constants.URI_SOAP11_ENC.equals(namespace))
         return Constants.PREFIX_SOAP11_ENC;
      else if (Constants.NS_SOAP11_ENV.equals(namespace) || Constants.NS_SOAP12_ENV.equals(namespace))
         return Constants.PREFIX_ENV;

      // Fall back to target namespace even though this is incorrect
      return Constants.PREFIX_TNS;
   }
}
