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
package org.jboss.ws.metadata.jaxrpcmapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;

/**
 * XML mapping of the java-wsdl-mapping root element in jaxrpc-mapping.xml
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class JavaWsdlMapping implements Serializable
{
   private static final long serialVersionUID = -142671631068024054L;

   // provide logging
   private static Logger log = Logger.getLogger(JavaWsdlMapping.class);

   // One or more <package-mapping> elements
   private List packageMappings = new ArrayList();
   // Zero or more <java-xml-type-mapping> elements
   private List javaXmlTypeMappings = new ArrayList();
   // Zero or more <exception-mapping> elements
   private List exceptionMappings = new ArrayList();
   // Zero or more <service-interface-mapping> elements
   private List serviceInterfaceMappings = new ArrayList();
   // Zero or more <service-endpoint-interface-mapping> elements
   private List serviceEndpointInterfaceMappings = new ArrayList();

   public PackageMapping[] getPackageMappings()
   {
      PackageMapping[] arr = new PackageMapping[packageMappings.size()];
      packageMappings.toArray(arr);
      return arr;
   }

   public JavaXmlTypeMapping[] getJavaXmlTypeMappings()
   {
      JavaXmlTypeMapping[] arr = new JavaXmlTypeMapping[javaXmlTypeMappings.size()];
      javaXmlTypeMappings.toArray(arr);
      return arr;
   }

   public ExceptionMapping[] getExceptionMappings()
   {
      ExceptionMapping[] arr = new ExceptionMapping[exceptionMappings.size()];
      exceptionMappings.toArray(arr);
      return arr;
   }

   public ServiceInterfaceMapping[] getServiceInterfaceMappings()
   {
      ServiceInterfaceMapping[] arr = new ServiceInterfaceMapping[serviceInterfaceMappings.size()];
      serviceInterfaceMappings.toArray(arr);
      return arr;
   }

   public ServiceEndpointInterfaceMapping[] getServiceEndpointInterfaceMappings()
   {
      ServiceEndpointInterfaceMapping[] arr = new ServiceEndpointInterfaceMapping[serviceEndpointInterfaceMappings.size()];
      serviceEndpointInterfaceMappings.toArray(arr);
      return arr;
   }

   // convenience methods ********************************************************************

   /** Get the package string for a given URI
    */
   public String getPackageNameForNamespaceURI(String nsURI)
   {
      String packageStr = null;
      for (int i = 0; packageStr == null && i < packageMappings.size(); i++)
      {
         PackageMapping mapping = (PackageMapping)packageMappings.get(i);
         if (mapping.getNamespaceURI().equals(nsURI))
            packageStr = mapping.getPackageType();
      }
      return packageStr;
   }

   /** Get the type mapping fo a given root-type-qname
    */
   public JavaXmlTypeMapping getTypeMappingForQName(QName xmlType)
   {
      JavaXmlTypeMapping typeMapping = null;

      if (xmlType != null)
      {
         // Check the <root-type-qname>
         Iterator it = javaXmlTypeMappings.iterator();
         while (typeMapping == null && it.hasNext())
         {
            JavaXmlTypeMapping mapping = (JavaXmlTypeMapping)it.next();
            if (xmlType.equals(mapping.getRootTypeQName()))
               typeMapping = mapping;
         }

         // Check the <anonymous-type-qname>
         it = javaXmlTypeMappings.iterator();
         while (typeMapping == null && it.hasNext())
         {
            JavaXmlTypeMapping mapping = (JavaXmlTypeMapping)it.next();
            QName anonymousQName = mapping.getAnonymousTypeQName();
            if (anonymousQName != null)
            {
               if (xmlType.getNamespaceURI().equals(anonymousQName.getNamespaceURI()))
               {
                  String localPart = xmlType.getLocalPart();
                  if (anonymousQName.getLocalPart().equals(localPart))
                     typeMapping = mapping;
                  if (anonymousQName.getLocalPart().equals(">" + localPart))
                     typeMapping = mapping;
               }
            }
         }

         if (typeMapping == null)
            log.warn("Cannot find jaxrpc-mapping for type: " + xmlType);
      }

      return typeMapping;
   }

   /** Get the exception mapping fo a given wsdl message
    */
   public ExceptionMapping getExceptionMappingForMessageQName(QName wsdlMessage)
   {
      ExceptionMapping exMapping = null;

      if (wsdlMessage != null)
      {
         Iterator it = exceptionMappings.iterator();
         while (it.hasNext())
         {
            ExceptionMapping mapping = (ExceptionMapping)it.next();
            if (wsdlMessage.equals(mapping.getWsdlMessage()))
               exMapping = mapping;
         }
      }

      return exMapping;
   }

   /** Get the exception mapping fo a given exception type
    */
   public ExceptionMapping getExceptionMappingForExceptionType(String javaType)
   {
      ExceptionMapping exMapping = null;

      if (javaType != null)
      {
         Iterator it = exceptionMappings.iterator();
         while (it.hasNext())
         {
            ExceptionMapping mapping = (ExceptionMapping)it.next();
            if (javaType.equals(mapping.getExceptionType()))
               exMapping = mapping;
         }
      }

      return exMapping;
   }

   /** Get the port type qname for a given service endpoint infterface
    */
   public QName getPortTypeQNameForServiceEndpointInterface(String seiName)
   {
      QName portTypeQName = null;

      ServiceEndpointInterfaceMapping[] seiMappings = getServiceEndpointInterfaceMappings();
      for (int i = 0; i < seiMappings.length; i++)
      {
         ServiceEndpointInterfaceMapping aux = seiMappings[i];
         if (aux.getServiceEndpointInterface().equals(seiName))
            portTypeQName = aux.getWsdlPortType();
      }

      return portTypeQName;
   }

   /** Get the service endpoint infterfacemapping for a given port type qname
    */
   public ServiceEndpointInterfaceMapping getServiceEndpointInterfaceMappingByPortType(QName portType)
   {
      ServiceEndpointInterfaceMapping seiMapping = null;

      ServiceEndpointInterfaceMapping[] seiMappings = getServiceEndpointInterfaceMappings();
      for (int i = 0; seiMapping == null && i < seiMappings.length; i++)
      {
         ServiceEndpointInterfaceMapping aux = seiMappings[i];
         if (aux.getWsdlPortType().equals(portType))
            seiMapping = aux;
      }

      return seiMapping;
   }

   /** Get the service endpoint infterface mapping for a service endpoint infterface
    */
   public ServiceEndpointInterfaceMapping getServiceEndpointInterfaceMapping(String seiName)
   {
      ServiceEndpointInterfaceMapping seiMapping = null;

      ServiceEndpointInterfaceMapping[] seiMappings = getServiceEndpointInterfaceMappings();
      for (int i = 0; seiMapping == null && i < seiMappings.length; i++)
      {
         ServiceEndpointInterfaceMapping aux = seiMappings[i];
         if (aux.getServiceEndpointInterface().equals(seiName))
            seiMapping = aux;
      }

      return seiMapping;
   }

   /**
    * Serialize the model as a String (Should return the mapping file)
    *
    * @return
    */
   public String serialize()
   {
      StringBuffer sb = new StringBuffer();
      //Append Standard Namespace Header
      sb.append("<java-wsdl-mapping version='1.1' ").append("xmlns='http://java.sun.com/xml/ns/j2ee' ");
      sb.append("xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' ");
      sb.append("xsi:schemaLocation='http://java.sun.com/xml/ns/j2ee ");
      sb.append("http://www.ibm.com/webservices/xsd/j2ee_jaxrpc_mapping_1_1.xsd'>");

      Iterator piter = packageMappings.iterator();
      while (piter != null && piter.hasNext())
         sb.append(((PackageMapping)piter.next()).serialize());

      Iterator jxiter = javaXmlTypeMappings.iterator();
      while (jxiter.hasNext())
         sb.append(((JavaXmlTypeMapping)jxiter.next()).serialize());

      for (Iterator i = exceptionMappings.iterator(); i.hasNext();)
         sb.append(((ExceptionMapping)i.next()).serialize());

      // A <service-interface-mapping> is followed by 1 or many <service-endpoint-interface-mapping> elements
      int lenSIM = serviceInterfaceMappings.size();
      for (int i = 0; i < lenSIM; i++)
      {
         ServiceInterfaceMapping sim = (ServiceInterfaceMapping)serviceInterfaceMappings.get(i);
         sb.append(sim.serialize());
         ServiceEndpointInterfaceMapping seim = (ServiceEndpointInterfaceMapping)serviceEndpointInterfaceMappings.get(i);
         sb.append(seim.serialize());
      }

      int lenSEI = serviceEndpointInterfaceMappings.size();
      for (int i = lenSIM; i < lenSEI; i++)
      {
         ServiceEndpointInterfaceMapping seim = (ServiceEndpointInterfaceMapping)serviceEndpointInterfaceMappings.get(i);
         sb.append(seim.serialize());
      }
      
      sb.append("</java-wsdl-mapping>");

      return sb.toString();
   }

   // factory methods ********************************************************************

   public void addPackageMapping(PackageMapping packageMapping)
   {
      packageMappings.add(packageMapping);
   }

   public void addJavaXmlTypeMappings(JavaXmlTypeMapping typeMapping)
   {
      javaXmlTypeMappings.add(typeMapping);
   }

   public void addExceptionMappings(ExceptionMapping exceptionMapping)
   {
      exceptionMappings.add(exceptionMapping);
   }

   public void addServiceInterfaceMappings(ServiceInterfaceMapping serviceInterfaceMapping)
   {
      serviceInterfaceMappings.add(serviceInterfaceMapping);
   }

   public void addServiceEndpointInterfaceMappings(ServiceEndpointInterfaceMapping serviceEndpointInterfaceMapping)
   {
      serviceEndpointInterfaceMappings.add(serviceEndpointInterfaceMapping);
   }
}
