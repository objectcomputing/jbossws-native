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
package org.jboss.ws.tools.mapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.encoding.TypeMapping;

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.Constants;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.MethodParamPartsMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceEndpointInterfaceMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceEndpointMethodMapping;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.tools.JavaWriter;
import org.jboss.ws.tools.NamespacePackageMapping;
import org.jboss.ws.tools.XSDTypeToJava;
import org.jboss.ws.tools.XSDTypeToJava.VAR;
import org.jboss.ws.tools.helpers.MappingFileGeneratorHelper;

/**
 *  Generates the JAXRPC Mapping file from the WSDL Definitions.
 *  <dt>Guidance:
 *  <p>
 *  If there is knowledge of the ServiceEndpointInterface (SEI)
 *  as in serverside generation (Java->WSDL), will make use of it.
 *  </p>
 *  <p>
 *  The TypeMapping needs to be provided externally.
 *  </p>
 *  </dd>
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 5, 2005
 */
public class MappingFileGenerator
{
   /**
    * WSDLDefinitions object that is the root of the WSDL object model
    */
   protected WSDLDefinitions wsdlDefinitions;

   /**
    * Package Names to override
    */
   protected Map<String, String> namespacePackageMap = new HashMap<String,String>();

   /**
    * Service Name
    */
   protected String serviceName;

   /**
    * SEI Package Name to override
    */
   protected String packageName;

   /**
    * Service Endpoint Interface (if available).
    * <br/> Will be available for server side generation (Java -> WSDL)
    */
   protected Class serviceEndpointInterface = null;

   /**
    * Type Mapping that is input from outside
    */
   protected LiteralTypeMapping typeMapping = null;

   protected String parameterStyle;

   public MappingFileGenerator(WSDLDefinitions wsdl, TypeMapping typeM)
   {
      this.wsdlDefinitions = wsdl;
      String targetNS = wsdl.getTargetNamespace();
      packageName = NamespacePackageMapping.getJavaPackageName(targetNS);
      this.typeMapping = (LiteralTypeMapping)typeM;
   }

   /**
    * @return @see #wsdlDefinitions
    */
   public WSDLDefinitions getWsdlDefinitions()
   {
      return wsdlDefinitions;
   }

   public void setWsdlDefinitions(WSDLDefinitions wsdlDefinitions)
   {
      this.wsdlDefinitions = wsdlDefinitions;
   }

   /**
    * @return @see #packageName
    */
   public String getPackageName()
   {
      return packageName;
   }

   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

   public Map<String, String> getNamespacePackageMap()
   {
      return namespacePackageMap;
   }

   
   public void setNamespacePackageMap(Map<String, String> map)
   {
      namespacePackageMap = map;
   }

   /**
    * @return @see #serviceName
    */
   public String getServiceName()
   {
      return serviceName;
   }

   public void setServiceEndpointInterface(Class serviceEndpointInterface)
   {
      this.serviceEndpointInterface = serviceEndpointInterface;
   }

   public void setServiceName(String serviceName)
   {
      this.serviceName = serviceName;
   }

   public void setParameterStyle(String paramStyle)
   {
      this.parameterStyle = paramStyle;
   }

   /**
    * Method that generates the jaxrpc mapping metadata
    * <dt>Guidance:<dd>
    * <p>If you need the metadata serialized, please use:
    *    @see JavaWsdlMapping#serialize()
    * </p>
    * @throws IOException
    * @throws IllegalArgumentException mappingfilename is null
    */
   public JavaWsdlMapping generate() throws IOException
   {
      MappingFileGeneratorHelper helper = new MappingFileGeneratorHelper(this.wsdlDefinitions, this.serviceName, this.namespacePackageMap, this.serviceEndpointInterface,
            this.typeMapping, this.parameterStyle);
      JavaWsdlMapping jwm = new JavaWsdlMapping();

      //If the schema has types, we will need to generate the java/xml type mapping
      helper.constructJavaXmlTypeMapping(jwm);
      WSDLService[] services = wsdlDefinitions.getServices();
      int lenServices = 0;
      if (services != null)
         lenServices = services.length;
      for (int i = 0; i < lenServices; i++)
      {
         WSDLService wsdlService = services[i];
         jwm.addServiceInterfaceMappings(helper.constructServiceInterfaceMapping(jwm, wsdlService));
         helper.constructServiceEndpointInterfaceMapping(jwm, wsdlService);
      }

      // Add package to namespace mapping after helper has generated the rest of the file.
      String targetNS = wsdlDefinitions.getTargetNamespace();
      String typeNamespace = helper.getTypeNamespace();
      if (typeNamespace == null)
         typeNamespace = targetNS;

      //Construct package mapping
      //Check if the user has provided a typeNamespace
      if (typeNamespace != null && typeNamespace.equals(targetNS) == false || isServerSideGeneration())
         jwm.addPackageMapping(helper.constructPackageMapping(jwm, getPackageName(typeNamespace), typeNamespace));
      jwm.addPackageMapping(helper.constructPackageMapping(jwm, getPackageName(targetNS), targetNS));

      if (namespacePackageMap != null)
      {
         Set<String> keys = namespacePackageMap.keySet();
         Iterator<String> iter = keys.iterator();
         while (iter != null && iter.hasNext())
         {
            String ns = iter.next();
            if (jwm.getPackageNameForNamespaceURI(ns) == null)
            {
               jwm.addPackageMapping(helper.constructPackageMapping(jwm, namespacePackageMap.get(ns), ns));
            }
         }
      }

      return jwm;
   }

   //PRIVATE METHODS
   private boolean isServerSideGeneration()
   {
      return this.serviceEndpointInterface != null;
   }

   private String getPackageName(String targetNamespace)
   {
      //Get it from global config
      if (namespacePackageMap != null)
      {
         String pkg = namespacePackageMap.get(targetNamespace);
         if (pkg != null)
         {
            return pkg;
         }
      }
      //Default behaviour will always generate all classes in the SEI package only
      return packageName;
   }
}
