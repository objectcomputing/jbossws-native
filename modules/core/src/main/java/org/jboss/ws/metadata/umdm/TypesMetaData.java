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
package org.jboss.ws.metadata.umdm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;

/**
 * Types meta data
 *
 * @author Thomas.Diesler@jboss.org
 * @since 12-May-2005
 */
public class TypesMetaData
{
   // provide logging
   private static final Logger log = Logger.getLogger(TypesMetaData.class);

   // The parent meta data.
   private ServiceMetaData serviceMetaData;
   // The list of type meta data
   private List<TypeMappingMetaData> typeList = new ArrayList<TypeMappingMetaData>();

   private JBossXSModel schemaModel;

   public TypesMetaData(ServiceMetaData serviceMetaData)
   {
      this.serviceMetaData = serviceMetaData;
   }

   public ServiceMetaData getServiceMetaData()
   {
      return serviceMetaData;
   }

   public JBossXSModel getSchemaModel()
   {
      return schemaModel;
   }

   public void setSchemaModel(JBossXSModel model)
   {
      this.schemaModel = model;
   }

   public void addSchemaModel(JBossXSModel model)
   {
      if (this.schemaModel == null)
      {
         this.schemaModel = model;
      }
      else
      {
         this.schemaModel.merge(model);
      }
   }

   public List<TypeMappingMetaData> getTypeMappings()
   {
      return new ArrayList<TypeMappingMetaData>(typeList);
   }

   public void addTypeMapping(TypeMappingMetaData tmMetaData)
   {
      if (typeList.contains(tmMetaData) == false)
      {
         log.trace("Add type mapping: " + tmMetaData);
         typeList.add(tmMetaData);
      }
   }

   public TypeMappingMetaData getTypeMappingByXMLType(QName xmlType)
   {
      TypeMappingMetaData tmMetaData = null;
      for (TypeMappingMetaData aux : typeList)
      {
         boolean isElementScope = TypeMappingMetaData.QNAME_SCOPE_ELEMENT.equals(aux.getQNameScope());
         if (aux.getXmlType().equals(xmlType) && isElementScope == false)
         {
            if (tmMetaData != null)
            {
               log.error(tmMetaData + "\n" + aux);
               throw new WSException("Ambiguous type mappping for: " + xmlType);
            }
            tmMetaData = aux;
         }
      }

      if (tmMetaData == null && schemaModel != null)
      {
         // Simple types are not neccessary mapped in jaxrpc-mapping.xml, lazily add the mapping here
         XSTypeDefinition xsType = schemaModel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());
         if (xsType instanceof XSSimpleTypeDefinition)
         {
            XSSimpleTypeDefinition xsSimpleType = (XSSimpleTypeDefinition)xsType;
            String javaTypeName = null;
            
            // <simpleType name="FooStringListType">
            //   <list itemType="string"/>
            // </simpleType> 
            if (xsSimpleType.getVariety() == XSSimpleTypeDefinition.VARIETY_LIST)
            {
               XSSimpleTypeDefinition itemType = xsSimpleType.getItemType();
               QName xmlBaseType = new QName(itemType.getNamespace(), itemType.getName());
               javaTypeName = new LiteralTypeMapping().getJavaTypeName(xmlBaseType);
               if (javaTypeName != null)
               {
                  javaTypeName += "[]";
               }
            }

            // <simpleType name="FooIType">
            //   <restriction base="normalizedString">
            //     <pattern value="\d{3}-[A-Z0-9]{5}"/>
            //   </restriction>
            //  </simpleType>
            XSTypeDefinition xsBaseType = xsType.getBaseType();
            while (javaTypeName == null && xsBaseType != null)
            {
               QName xmlBaseType = new QName(xsBaseType.getNamespace(), xsBaseType.getName());
               javaTypeName = new LiteralTypeMapping().getJavaTypeName(xmlBaseType);
               xsBaseType = xsBaseType.getBaseType();
            }
            
            if (javaTypeName != null)
            {
               tmMetaData = new TypeMappingMetaData(this, xmlType, javaTypeName);
               tmMetaData.setQNameScope(TypeMappingMetaData.QNAME_SCOPE_SIMPLE_TYPE);
               if(log.isDebugEnabled()) log.debug("Adding a simpleType without jaxrpc-mapping: " + tmMetaData);
               addTypeMapping(tmMetaData);
            }
            else
            {
               log.warn("Cannot obtain javaTypeName for xmlType: " + xmlType);
            }
         }
      }

      return tmMetaData;
   }

   public TypeMappingMetaData getTypeMappingByJavaType(String javaTypeName)
   {
      TypeMappingMetaData tmMetaData = null;
      for (TypeMappingMetaData aux : typeList)
      {
         if (aux.getJavaTypeName().equals(javaTypeName))
            tmMetaData = aux;
      }
      return tmMetaData;
   }

   public String toString()
   {
      StringBuilder buffer = new StringBuilder("\nTypesMetaData: ");
      for (TypeMappingMetaData tmd : typeList)
      {
         buffer.append("\n  " + tmd);
      }
      buffer.append("\n" + (schemaModel != null ? schemaModel.serialize() : "<schema/>"));
      return buffer.toString();
   }
}
