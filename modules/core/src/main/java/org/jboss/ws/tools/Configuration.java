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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 *  Configuration driving jbossws
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 11, 2005
 */
public class Configuration
{
   private JavaToWSDLConfig j2wc;
   private WSDLToJavaConfig w2jc;
   private GlobalConfig globalConfig;

   public Configuration()
   {
   }

   public JavaToWSDLConfig getJavaToWSDLConfig(boolean createNew)
   {
      if (createNew && j2wc == null)
         j2wc = new JavaToWSDLConfig();
      return j2wc;
   }

   public WSDLToJavaConfig getWSDLToJavaConfig(boolean createNew)
   {
      if (createNew && w2jc == null)
         w2jc = new WSDLToJavaConfig();
      return w2jc;
   }

   /**
    * @return Returns the globalConfig.
    */
   public GlobalConfig getGlobalConfig(boolean createNew)
   {
      if (createNew && globalConfig == null)
         globalConfig = new GlobalConfig();
      return globalConfig;
   }

   public void setGlobalConfig(GlobalConfig globalConfig)
   {
      this.globalConfig = globalConfig;
   }

   public void setJavaToWSDLConfig(JavaToWSDLConfig j2wc2)
   {
      this.j2wc = j2wc2;
   }

   public void setWSDLToJavaConfig(WSDLToJavaConfig wsdl2jc)
   {
      w2jc = wsdl2jc;
   }

   /**
    * Configuration for JavaToWSDL
    */
   public class JavaToWSDLConfig
   {
       // Is a jaxrpc-mapping file needed?
      public boolean mappingFileNeeded;
      // Name of the jaxrpc-mapping file
      public String mappingFileName;
      // Need webservices.xml file?
      public boolean wsxmlFileNeeded;
      // Target Namespace
      public String targetNamespace;
      // Type Namespace
      public String typeNamespace;
      // Service Name
      public String serviceName;
      // Endpoint Name
      public String endpointName;
      // Style of WSDL. {"rpc","doc"}
      public String wsdlStyle = "document";
      // Parameter style {"wrapped", "bare"} 
      public String parameterStyle = "wrapped";
      // WSDL Version  {"1.1","2.0"}
      public String wsdlVersion = "1.1";
      // Should Schema be included in the wsdl
      public boolean includeSchemaInWSDL = true;
      public boolean restrictSchemaToTargetNS;
      public String servletLink;
      public String ejbLink;
      public boolean wsxmlFileAppend;

      public Map<String, List<OperationConfig>> operations = new HashMap<String, List<OperationConfig>>();

      public OperationConfig createOperationConfig()
      {
         return new OperationConfig();
      }

      /**Configuration at the operation level*/
      /*public OperationConfig opConfig;

       public OperationConfig getOperationConfig(boolean createNew)
       {
       if(j2wc.opConfig == null)
       j2wc.opConfig = new OperationConfig();
       return j2wc.opConfig;
       }*/
   }

   /**
    * Configuration for WSDL To Java
    */
   public class WSDLToJavaConfig
   {
      public String wsdlLocation;
      // Parameter style {"wrapped", "bare"} 
      public String parameterStyle = "wrapped";
      // Should generated types be Serializable?
      public boolean serializableTypes;
      // Is a jaxrpc-mapping file needed?
      public boolean mappingFileNeeded;
      // Name of the jaxrpc-mapping file
      public String mappingFileName;
      // Need webservices.xml file?
      public boolean wsxmlFileNeeded;
      public String servletLink;
      public String ejbLink;
   }

   /**
    *  Global Configuration
    */
   public class GlobalConfig
   {
      public String nscollide = "Array";
      public Map<String, String> packageNamespaceMap = new HashMap<String, String>();
   }

   /**
    * Configuration at the operation/method level
    */
   public class OperationConfig
   {
      public String name;
      public boolean isOneWay = false;
      public List<ParameterConfig> params = new ArrayList<ParameterConfig>();
      public QName returnXmlName;

      public ParameterConfig createParameterConfig()
      {
         return new ParameterConfig();
      }
   }

   /**
    * A ParameterConfig.
    */
   public class ParameterConfig
   {
      public String javaType;
      public QName xmlName;
      public String mode;
      public boolean header;
   }
}
