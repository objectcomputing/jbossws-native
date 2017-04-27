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

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;


/**
 * Type mapping meta data
 *
 * @author Thomas.Diesler@jboss.org
 * @since 12-May-2005
 */
public class TypeMappingMetaData
{
   // The parent meta data.
   private TypesMetaData typesMetaData;

   private QName xmlType;
   private String javaTypeName;
   private String qnameScope;

   // List of allowed qname scopes
   public static final String QNAME_SCOPE_SIMPLE_TYPE = "simpleType";
   public static final String QNAME_SCOPE_COMPLEX_TYPE = "complexType";
   public static final String QNAME_SCOPE_ELEMENT = "element";
   private static List<String> allowedScopes = Arrays.asList(new String[] { QNAME_SCOPE_COMPLEX_TYPE, QNAME_SCOPE_SIMPLE_TYPE, QNAME_SCOPE_ELEMENT });

   public TypeMappingMetaData(TypesMetaData typesMetaData, QName xmlType, String javaTypeName)
   {
      if (xmlType == null)
         throw new IllegalArgumentException("Invalid null xmlType");
      if (javaTypeName == null)
         throw new IllegalArgumentException("Invalid null javaTypeName");
      
      this.typesMetaData = typesMetaData;
      this.javaTypeName = javaTypeName;
      this.xmlType = xmlType;
      this.qnameScope = QNAME_SCOPE_COMPLEX_TYPE;
   }

   public TypesMetaData getTypesMetaData()
   {
      return typesMetaData;
   }

   public String getJavaTypeName()
   {
      return javaTypeName;
   }

   public QName getXmlType()
   {
      return xmlType;
   }

   public String getQNameScope()
   {
      return qnameScope;
   }

   public void setQNameScope(String qnameScope)
   {
      if (allowedScopes.contains(qnameScope) == false)
         throw new IllegalArgumentException("Invalid qname scope: " + qnameScope);
      
      this.qnameScope = qnameScope;
   }

   public int hashCode()
   {
      return toString().hashCode();
   }
   
   public boolean equals(Object obj)
   {
      if (obj instanceof TypeMappingMetaData == false) return false;
      TypeMappingMetaData other = (TypeMappingMetaData)obj;
      return toString().equals(other.toString());
   }
   
   public String toString()
   {
      return "[" + qnameScope + "=" + xmlType + ",javaType=" + javaTypeName + "]";
   }
}
