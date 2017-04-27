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
package org.jboss.ws.tools;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.rpc.encoding.TypeMapping;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.Configuration.OperationConfig;
import org.jboss.ws.tools.metadata.ToolsUnifiedMetaDataBuilder;
import org.jboss.ws.tools.wsdl.WSDLWriter;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/**
 * Generates a WSDL for a service endpoint.
 *
 * <BR/> This is the main entry point for all Java To WSDL needs.
 * <BR/> Features that can be set are derived from org.jboss.ws.tools.WSToolsConstants
 * <p/>
 * Notable ones are:<br/>
 * @see org.jboss.ws.tools.WSToolsConstants.WSTOOLS_FEATURE_RESTRICT_TO_TARGET_NS
 * @see org.jboss.ws.tools.WSToolsConstants.WSTOOLS_FEATURE_INCLUDE_SCHEMA_IN_WSDL
 * @see org.jboss.ws.tools.WSToolsConstants.WSTOOLS_FEATURE_USE_ANNOTATIONS
 *
 * @author Thomas.Diesler@jboss.org
 * @author Anil.Saldhana@jboss.org
 * @since 24-Jul-2005
 */
public class JavaToWSDL
{
   // provide logging
   private static final Logger log = Logger.getLogger(JavaToWSDL.class);

   // The required wsdl namespace URI
   private String wsdlNamespace;
   // The target namespace
   private String targetNamespace;
   //The type namespace (it can be different from the target namespace)
   private String typeNamespace;
   // The service name
   private String serviceName;
   // The portType name
   private String portTypeName;

   private Style style;

   private ParameterStyle parameterStyle;

   // Features as represented by Constants
   private Map<String, Boolean> features = new HashMap<String, Boolean>();

   // A Map of package/namespace mapping that needs to be passed onto types generator
   private Map<String,String> packageNamespaceMap = new HashMap<String, String>();

   private TypeMapping typeMapping = null;

   private JavaWsdlMapping javaWsdlMapping = null;

   private UnifiedMetaData umd = null;

   private boolean qualifiedElements = false;

   private Map<String, List<OperationConfig>> operationMap = null;

   /** Contruct a java to wsdl generator for a given wsdl version.
    * <p/>
    * WSDL-1.1 namespace URI: http://schemas.xmlsoap.org/wsdl/<br/>
    * WSDL-2.0 namespace URI: http://www.w3.org/2003/11/wsdl
    *
    * @param namespace wsdl namespace URI
    */
   public JavaToWSDL(String namespace)
   {
      if (Constants.NS_WSDL11.equals(namespace) == false)
         throw new IllegalArgumentException("Unsupported wsdl version: " + namespace);

      this.wsdlNamespace = namespace;
   }

   /**
    * Add a feature to this subsystem
    * @see org.jboss.ws.tools.WSToolsConstants
    * @param name
    * @param value
    */
   public void addFeature(String name, boolean value)
   {
      features.put(name, new Boolean(value));
   }

   /**
    * Return a feature if set
    * @see org.jboss.ws.tools.WSToolsConstants
    * @param name
    * @return boolean value representing the feature, if not
    * @throws IllegalStateException  Feature unrecognized
    */
   public boolean getFeature(String name)
   {
      Boolean val = features.get(name);
      if (val == null)
         throw new WSException("Feature value not available: " + name);

      return val.booleanValue();
   }

   /** Get the immutable wsdl namespace URI
    */
   public String getWsdlNamespace()
   {
      return wsdlNamespace;
   }

   /** Get the wsdl target namespace
    */
   public String getTargetNamespace()
   {
      return targetNamespace;
   }

   /** Set the wsdl target namespace
    */
   public void setTargetNamespace(String targetNamespace)
   {
      this.targetNamespace = targetNamespace;
   }

   /** Get the type Namespace */
   public String getTypeNamespace()
   {
      return typeNamespace;
   }

   /** Set the Type Namespace */
   public void setTypeNamespace(String typeNamespace)
   {
      this.typeNamespace = typeNamespace;
   }

   /** Get the wsdl service name
    */
   public String getServiceName()
   {
      return serviceName;
   }

   /** Set the wsdl service name
    */
   public void setServiceName(String serviceName)
   {
      this.serviceName = serviceName;
   }

   /** Get the wsdl service endpoint name
    */
   public String getPortTypeName()
   {
      return portTypeName;
   }

   /** Set the wsdl service PortType Name
    */
   public void setPortTypeName(String endpointName)
   {
      this.portTypeName = endpointName;
   }

   /**
    * During the WSDL generation process, a typeMapping will be
    * created that maps xml types -> java types
    *
    * @return  typeMapping
    * @exception IllegalStateException If typeMapping has not been generated
    */
   public TypeMapping getTypeMapping()
   {
      if(typeMapping == null)
         throw new WSException("TypeMapping has not been generated");
      return typeMapping;
   }

   public Style getStyle()
   {
      return style;
   }

   public void setStyle(Style style)
   {
      this.style = style;
   }

   public ParameterStyle getParameterStyle()
   {
      return parameterStyle;
   }

   public void setParameterStyle(ParameterStyle parameterStyle)
   {
      this.parameterStyle = parameterStyle;
   }

   /**
    * Users can customize a java package->xml namespace map
    * that will be used in the Java to WSDL process.
    * <br/>The package representing the endpoint will always be mapped
    * to the target namespace, irrespective of an attempt to
    * customize that in the map. If you desire to change that, then
    * think about changing just the type namespace as the types are
    * generated using the typenamespace.
    *
    * @param map  The Map
    */
   public void setPackageNamespaceMap(Map<String,String> map)
   {
      //Lets convert the namespace->package map to package->namespace map
      Set<String> keys = map.keySet();
      Iterator<String> iter = keys.iterator();
      while (iter != null && iter.hasNext())
      {
         String pkg = iter.next();
         packageNamespaceMap.put(map.get(pkg), pkg);
      }
   }

   public void setOperationMap(Map<String, List<OperationConfig>> operationMap)
   {
      this.operationMap = operationMap;
   }

   /**
    * Clients of Tools can build a UnifiedMetaData externally
    * and pass it to the Java To WSDL subsystem [Optional]
    *
    * @param um
    */
   public void setUnifiedMetaData(UnifiedMetaData um)
   {
      this.umd = um;
   }

   public UnifiedMetaData getUnifiedMetaData()
   {
      return umd;
   }

   public void setUmd(UnifiedMetaData umd)
   {
      this.umd = umd;
   }

   public boolean isQualifiedElements()
   {
      return qualifiedElements;
   }

   public void setQualifiedElements(boolean qualifiedElements)
   {
      this.qualifiedElements = qualifiedElements;
   }

   public JavaWsdlMapping getJavaWsdlMapping()
   {
      return javaWsdlMapping;
   }

   /** Generate the common WSDL definition for a given endpoint
    */
   public WSDLDefinitions generate(Class endpoint)
   {
      if(log.isDebugEnabled()) log.debug("generate [endpoint=" + endpoint.getName() + ",tnsURI=" + targetNamespace + ",service=" + serviceName
            + ",portType=" + portTypeName + "]");

      if( umd == null)
      {
         umd = new ToolsUnifiedMetaDataBuilder(endpoint, targetNamespace,
              typeNamespace,  serviceName, style, parameterStyle, operationMap).getUnifiedMetaData();
      }

      if (typeNamespace != null)
         packageNamespaceMap.put(endpoint.getPackage().getName(), typeNamespace);

      WSDLDefinitions wsdlDefinitions = null;
      try
      {
         if (Constants.NS_WSDL11.equals(wsdlNamespace))
         {
            JavaToWSDL11 javaWSDL11 = new JavaToWSDL11();
            javaWSDL11.addFeatures(features);
            javaWSDL11.setPackageNamespaceMap(packageNamespaceMap);
            javaWSDL11.addFeatures(features);
            if( umd != null )
               javaWSDL11.setUnifiedMetaData(umd);
            javaWSDL11.setQualifiedElements(qualifiedElements);

            wsdlDefinitions = javaWSDL11.generate(endpoint);
            typeMapping = javaWSDL11.getTypeMapping();
            javaWsdlMapping = javaWSDL11.getJavaWsdlMapping();
         }
         if (wsdlDefinitions == null)
            throw new WSException("Cannot generate WSDL definitions");

         // Debug the generated wsdl
         StringWriter sw = new StringWriter();
         new WSDLWriter(wsdlDefinitions).write(sw, Constants.DEFAULT_XML_CHARSET);
         if(log.isDebugEnabled()) log.debug("Generated WSDL:\n" + sw.toString());

         // Debug the generated mapping file
         String jaxrpcMappingStr = null;
         if (javaWsdlMapping != null)
         {
            Element root = DOMUtils.parse(javaWsdlMapping.serialize());
            jaxrpcMappingStr = DOMWriter.printNode(root, true);
         }
         if(log.isDebugEnabled()) log.debug("Generated Mapping:\n" + jaxrpcMappingStr);
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception e)
      {
         log.error("Cannot generate WSDL",e);
         throw new WSException("Cannot generate wsdl from: " + endpoint);
      }
      return wsdlDefinitions;
   }
}
