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
package org.jboss.ws.tools.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.tools.Configuration;
import org.jboss.ws.tools.Configuration.GlobalConfig;
import org.jboss.ws.tools.Configuration.JavaToWSDLConfig;
import org.jboss.ws.tools.Configuration.OperationConfig;
import org.jboss.ws.tools.Configuration.ParameterConfig;
import org.jboss.ws.tools.Configuration.WSDLToJavaConfig;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;

/**
 *  Reads the XML configuration file passed to jbossws
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @author Thomas.Diesler@jboss.org
 *  @since   11-Aug-2005
 */
public class ToolsSchemaConfigReader implements ObjectModelFactory
{
   // Tags
   private static final String PARAMETER_TAG = "parameter";
   private static final String WEBSERVICES_TAG = "webservices";
   private static final String MAPPING_TAG = "mapping";
   private static final String PACKAGE_NAMESPACE_TAG = "package-namespace";
   private static final String NAMESPACES_TAG = "namespaces";
   private static final String SERVICE_TAG = "service";
   private static final String GLOBAL_TAG = "global";
   private static final String JAVA_WSDL_TAG = "java-wsdl";
   private static final String WSDL_JAVA_TAG = "wsdl-java";
   private static final String OPERATION_TAG = "operation";

   // Attributes
   private static final String HEADER_ATTRIBUTE = "header";
   private static final String MODE_ATTRIBUTE = "mode";
   private static final String XML_NAME_ATTRIBUTE = "xml-name";
   private static final String TYPE_ATTRIBUTE = "type";
   private static final String NAMESPACE_ATTRIBUTE = "namespace";
   private static final String PACKAGE_ATTRIBUTE = "package";
   private static final String EJB_LINK_ATTRIBUTE = "ejb-link";
   private static final String SERVLET_LINK_ATTRIBUTE = "servlet-link";
   private static final String APPEND_ATTRIBUTE = "append";
   private static final String TYPE_NAMESPACE_ATTRIBUTE = "type-namespace";
   private static final String TARGET_NAMESPACE_ATTRIBUTE = "target-namespace";
   private static final String RETURN_XML_NAME_ATTRIBUTE = "return-xml-name";
   private static final String ONEWAY_ATTRIBUTE = "one-way";
   private static final String PARAMETER_STYLE_ATTRIBUTE = "parameter-style";
   private static final String SERIALIZABLE_TYPES_ATTRIBUTE = "serializable-types";
   private static final String STYLE_ATTRIBUTE = "style";
   private static final String ENDPOINT_ATTRIBUTE = "endpoint";
   private static final String NAME_ATTRIBUTE = "name";
   private static final String LOCATION_ATTRIBUTE = "location";
   private static final String FILE_ATTRIBUTE = "file";

   // provide logging
   private static final Logger log = Logger.getLogger(ToolsSchemaConfigReader.class);

   public ToolsSchemaConfigReader()
   {
   }

   public Configuration readConfig(String configLocation) throws IOException
   {
      log.trace("Inside readConfig: " + configLocation);
      if (configLocation == null)
         throw new IllegalArgumentException("Config URL passed is null");

      URL configURL = null;
      try
      {
         configURL = new URL(configLocation);
      }
      catch (MalformedURLException e)
      {
         // ignore
      }

      if (configURL == null)
      {
         File configFile = new File(configLocation);
         if (configFile.exists())
            configURL = configFile.toURL();
      }

      if (configURL == null)
      {
         ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
         configURL = ctxLoader.getResource(configLocation);
      }

      if (configURL == null)
         throw new IllegalArgumentException("Cannot load config from: " + configLocation);

      Configuration config = new Configuration();
      InputStream is = configURL.openStream();
      try
      {
         Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
         unmarshaller.setNamespaceAware(true);
         unmarshaller.setSchemaValidation(true);
         unmarshaller.setValidation(true);
         unmarshaller.unmarshal(is, this, config);
      }
      catch (JBossXBException ex)
      {
         IOException ioex = new IOException("Cannot parse config: " + ex.getMessage());
         ioex.initCause(ex);
         throw ioex;
      }
      finally
      {
         is.close();
      }

      if (config.getJavaToWSDLConfig(false) == null && config.getWSDLToJavaConfig(false) == null)
         throw new WSException("Invalid configuration file, either " + JAVA_WSDL_TAG + ", or " + WSDL_JAVA_TAG + " must be present");

      log.trace("Exit readConfig");
      return config;
   }

   public Object newRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName, Attributes attrs)
   {
      if (root instanceof Configuration)
         return root;
      else return null;
   }

   /**
    * This method is called by the object model factory and returns the root of the object graph.
    */
   public Object newChild(Configuration config, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("Inside newChild:localName=" + localName);
      if (JAVA_WSDL_TAG.equals(localName))
      {
         JavaToWSDLConfig j2wsdlc = config.getJavaToWSDLConfig(true);
         return j2wsdlc;
      }
      else if (WSDL_JAVA_TAG.equals(localName))
      {
         WSDLToJavaConfig wsdl2jc = config.getWSDLToJavaConfig(true);
         wsdl2jc.wsdlLocation = attrs.getValue(LOCATION_ATTRIBUTE);
         String paramStyle = attrs.getValue(PARAMETER_STYLE_ATTRIBUTE);
         if (paramStyle != null)
            wsdl2jc.parameterStyle = paramStyle;
         String serializableTypes = attrs.getValue(SERIALIZABLE_TYPES_ATTRIBUTE);
         if ("true".equals(serializableTypes) || "1".equals(serializableTypes))
            wsdl2jc.serializableTypes = true;         
            
         return wsdl2jc;
      }
      else if (GLOBAL_TAG.equals(localName))
      {
         GlobalConfig globalc = config.getGlobalConfig(true);
         return globalc;
      }
      return config;
   }

   /**
    * This method is called by the object model factory and returns the root of the object graph.
    */
   public Object newChild(JavaToWSDLConfig j2wsdlc, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      String errorStr = "Problem parsing tag:" + JAVA_WSDL_TAG;

      if (SERVICE_TAG.equals(localName))
      {
         j2wsdlc.serviceName = attrs.getValue(NAME_ATTRIBUTE);
         j2wsdlc.endpointName = attrs.getValue(ENDPOINT_ATTRIBUTE);
         j2wsdlc.wsdlStyle = getOptionalAttribute(attrs, STYLE_ATTRIBUTE, "document");
         j2wsdlc.parameterStyle = getOptionalAttribute(attrs, PARAMETER_STYLE_ATTRIBUTE, "wrapped");
      }
      else if (OPERATION_TAG.equals(localName))
      {
         OperationConfig operation = j2wsdlc.createOperationConfig();
         operation.name = attrs.getValue(NAME_ATTRIBUTE);
         String oneWay = attrs.getValue(ONEWAY_ATTRIBUTE);
         operation.isOneWay = "true".equals(oneWay) || "1".equals(oneWay);
         String returnXmlName = attrs.getValue(RETURN_XML_NAME_ATTRIBUTE);
         if (returnXmlName != null)
            operation.returnXmlName = navigator.resolveQName(returnXmlName);

         return operation;
      }
      else if (NAMESPACES_TAG.equals(localName))
      {
         errorStr += NAMESPACES_TAG;
         j2wsdlc.targetNamespace = getNamespace(navigator, TARGET_NAMESPACE_ATTRIBUTE, errorStr, attrs);
         j2wsdlc.typeNamespace = getNamespace(navigator, TYPE_NAMESPACE_ATTRIBUTE, errorStr, attrs);
         if (j2wsdlc.typeNamespace == null)
            j2wsdlc.typeNamespace = j2wsdlc.targetNamespace;
      }
      else if (MAPPING_TAG.equals(localName))
      {
         j2wsdlc.mappingFileNeeded = true;
         j2wsdlc.mappingFileName = getOptionalAttribute(attrs, FILE_ATTRIBUTE, "jaxrpc-mapping.xml");
      }
      else if (WEBSERVICES_TAG.equals(localName))
      {
         j2wsdlc.wsxmlFileNeeded = true;
         j2wsdlc.servletLink = getOptionalAttribute(attrs, SERVLET_LINK_ATTRIBUTE, null);
         j2wsdlc.ejbLink = getOptionalAttribute(attrs, EJB_LINK_ATTRIBUTE, null);
         if (j2wsdlc.ejbLink == null && j2wsdlc.servletLink == null)
            throw new WSException("Either servletLink or ejbLink should be specified");
         String wsxmlFileAppend = attrs.getValue(APPEND_ATTRIBUTE);
         j2wsdlc.wsxmlFileAppend = "true".equals(wsxmlFileAppend) || "1".equals(wsxmlFileAppend);
      }
      return j2wsdlc;
   }

   /**
    * This method is called by the object model factory and returns the root of the object graph.
    */
   public Object newChild(GlobalConfig globalc, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      if (PACKAGE_NAMESPACE_TAG.equals(localName))
      {
         String pkgname = attrs.getValue(PACKAGE_ATTRIBUTE);
         String ns = attrs.getValue(NAMESPACE_ATTRIBUTE);
         globalc.packageNamespaceMap.put(ns, pkgname);
      }
      return globalc;
   }

   /**
    * This method is called by the object model factory and returns the root of the object graph.
    */
   public Object newChild(WSDLToJavaConfig wsdl2jc, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      if (MAPPING_TAG.equals(localName))
      {
         wsdl2jc.mappingFileNeeded = true;
         wsdl2jc.mappingFileName = getOptionalAttribute(attrs, FILE_ATTRIBUTE, "jaxrpc-mapping.xml");
      }
      else if (WEBSERVICES_TAG.equals(localName))
      {
         wsdl2jc.wsxmlFileNeeded = true;
         wsdl2jc.servletLink = getOptionalAttribute(attrs, SERVLET_LINK_ATTRIBUTE, null);
         wsdl2jc.ejbLink = getOptionalAttribute(attrs, EJB_LINK_ATTRIBUTE, null);
         if (wsdl2jc.ejbLink == null && wsdl2jc.servletLink == null)
            throw new WSException("Either servletLink or ejbLink should be specified");
      }
      return wsdl2jc;
   }

   /**
    * This method is called by the object model factory and returns the root of the object graph.
    */
   public Object newChild(OperationConfig op, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      if (PARAMETER_TAG.equals(localName))
      {
         ParameterConfig parameter = op.createParameterConfig();
         parameter.javaType = attrs.getValue(TYPE_ATTRIBUTE);
         String xmlName = attrs.getValue(XML_NAME_ATTRIBUTE);
         if (xmlName != null)
            parameter.xmlName = navigator.resolveQName(xmlName);
         parameter.mode = attrs.getValue(MODE_ATTRIBUTE);
         String header = attrs.getValue(HEADER_ATTRIBUTE);
         if (header != null)
            parameter.header = "true".equals(header) || "1".equals(header);

         return parameter;
      }

      return null;
   }

   /**
    */
   public Object addChild(Configuration config, WSDLToJavaConfig wsdl2jc, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      config.setWSDLToJavaConfig(wsdl2jc);
      return config;
   }

   /**
    */
   public Object addChild(Configuration config, GlobalConfig global, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      config.setGlobalConfig(global);
      return config;
   }

   /**
    */
   public Object addChild(Configuration config, JavaToWSDLConfig j2wc, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      config.setJavaToWSDLConfig(j2wc);
      return config;
   }

   /**
    */
   public Object addChild(JavaToWSDLConfig j2wc, OperationConfig opc, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      List<OperationConfig> list = j2wc.operations.get(opc.name);
      if (list == null)
      {
         list = new ArrayList<OperationConfig>();
         list.add(opc);
         j2wc.operations.put(opc.name, list);
      }
      else
      {
         list.add(opc);
      }

      return j2wc;
   }

   public Object addChild(OperationConfig opc, ParameterConfig pc, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      opc.params.add(pc);
      return opc;
   }

   public Object completeRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName)
   {
      return root;
   }

   //PRIVATE METHODS
   private String getOptionalAttribute(Attributes attrs, String attribName, String defaultValue)
   {
      String value = attrs.getValue(attribName);
      if (value == null)
         return defaultValue;

      return value;
   }

   private String getNamespace(UnmarshallingContext navigator, String attribName, String errorStr, Attributes attrs)
   {
      try
      {
         return attrs.getValue(attribName);
      }
      catch (RuntimeException e)
      {
         throw new WSException(errorStr + " attribute=" + attribName);
      }
   }
}
