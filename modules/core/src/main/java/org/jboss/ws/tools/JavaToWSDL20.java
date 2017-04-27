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

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;

/**
 * Generates a WSDL-2.0 from a Service Endpoint Interface
 * @author Thomas.Diesler@jboss.org
 * @author Anil.Saldhana@jboss.org
 * @since 10-Oct-2004
 */
public class JavaToWSDL20
{
   // provide logging
   private static final Logger log = Logger.getLogger(JavaToWSDL20.class);

   // The required wsdl namespace URI
   private final String wsdlNamespace = Constants.NS_WSDL11;
   // The target namespace
   private String targetNamespace;
   //The type namespace (it can be different from the target namespace)
   private String typeNamespace;
   // The service name
   private String serviceName;

   /** Name of the PortType*/
   private String portTypeName;

   /** Features as represented by Constants*/
   private HashMap<String,Boolean> features = new HashMap<String,Boolean>();

   // A Map of package/namespace mapping that needs to be passed onto types generator
   private Map<String,String> packageNamespaceMap = null;

   /**
    * CTR in the simplest form
    * @param sei  Class that represents the SEI
    * @param targetNS Target Namespace
    * @param typeNS Type Namespace
    */
   /*public JavaToWSDL20(Class sei, String targetNS, String typeNS )
   {
      super(sei,targetNS, typeNS);
      this.wsdlNamespace = Constants.NS_WSDL20;
   }*/

   /**
    * Default Constructor
    */
   public JavaToWSDL20()
   {
      if(log.isDebugEnabled()) log.debug("Creating JavaToWSDL20 instance");
   }

   /**
    * Add a feature to this subsystem
    * @param name
    * @param value
    */
   public void addFeature(String name, boolean value)
   {
      features.put(name,new Boolean(value));
   }

   /**
    * Append features
    * @param map
    */
   public void addFeatures(Map<String,Boolean> map)
   {
      features.putAll(map);
   }

   /**
    * Return a feature if set
    * @param name
    * @return boolean value representing the feature, if not
    * @throws IllegalStateException  Feature unrecognized
    */
   public boolean getFeature(String name)
   {
      Boolean val = features.get(name);
      if(val != null) return val.booleanValue();
       throw new WSException("Feature unrecognized");
   }

   /**
    * @return Returns the endpointName.
    */
   public String getPortTypeName()
   {
      return portTypeName;
   }



   /**
    * @param endpointName The endpointName to set.
    */
   public void setPortTypeName(String endpointName)
   {
      this.portTypeName = endpointName;
   }

   /**
    * A customized Package->Namespace map
    *
    * @param map
    */
   public void setPackageNamespaceMap(Map<String,String> map)
   {
      packageNamespaceMap = map;
   }



   /**
    * @return Returns the serviceName.
    */
   public String getServiceName()
   {
      return serviceName;
   }


   /**
    * @param serviceName The serviceName to set.
    */
   public void setServiceName(String serviceName)
   {
      this.serviceName = serviceName;
   }


   /**
    * @return Returns the targetNamespace.
    */
   public String getTargetNamespace()
   {
      return targetNamespace;
   }


   /**
    * @param targetNamespace The targetNamespace to set.
    */
   public void setTargetNamespace(String targetNamespace)
   {
      this.targetNamespace = targetNamespace;
   }

   /**
    * Get the TypeNamespace
    *
    * @return
    */
   public String getTypeNamespace()
   {
      return typeNamespace;
   }

   /**
    *
    * Set the TypeNamespace
    * @param typeNamespace
    */
   public void setTypeNamespace(String typeNamespace)
   {
      this.typeNamespace = typeNamespace;
   }

   /**
    *   Generate the common WSDL definition for a given endpoint
    */
   public WSDLDefinitions generate(Class endpoint)
   {
//      WSDLDefinitions wsdl =  new WSDLDefinitions();
//      wsdl.setWsdlNamespace(this.wsdlNamespace);
//      //Delegate the generation of WSDL to a Helper class
//      JavaToWSDLHelper helper = new JavaToWSDLHelper(wsdl,this.wsdlNamespace,this.targetNamespace);
//      if(typeNamespace == null ) typeNamespace = targetNamespace;
//      helper.setTypeNamespace(typeNamespace);
//      JavaToXSDIntf jxsd = new JavaToXSD(null);
//      if(packageNamespaceMap != null)
//         jxsd.setPackageNamespaceMap(packageNamespaceMap);
//      jxsd.addFeatures(features);
//      helper.setJavaToXSD(jxsd);
//      helper.setEndpoint(endpoint);
//      helper.appendDefinitions();
//      //TODO: Revisit when wsdl 2.0 support is provided
//      /*helper.generateTypesForXSD();
//      helper.generateInterfaces(portTypeName);
//      helper.generateBindings();
//      helper.generateServices(this.serviceName); */
//      return wsdl;

      return null;
   }

}
