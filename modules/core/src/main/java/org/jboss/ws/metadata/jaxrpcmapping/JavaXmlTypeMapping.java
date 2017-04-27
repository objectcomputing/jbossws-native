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

import javax.xml.namespace.QName;

/**
 * XML mapping of the java-wsdl-mapping/java-xml-type-mapping element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class JavaXmlTypeMapping implements Serializable
{
   private static final long serialVersionUID = -7671078579082015103L;

   // The parent <java-wsdl-mapping> element
   private JavaWsdlMapping javaWsdlMapping;

   /** The required <java-type> element
    * The java-type element is the fully qualified class name of a Java class.
    */
   private String javaType;
   // The choice <root-type-qname> element
   private QName rootTypeQName;
   // The choice <anonymous-type-qname> element
   private QName anonymousTypeQName;

   /** The required <qname-scope> element
    * The qname-scope elements scopes the reference of a QName to the WSDL element type it applies to.
    * The value of qname-scope may be simpleType, complexType, or element
    */
   private String qnameScope;

   // Zero or more <variable-mapping> elements
   private ArrayList variableMappings = new ArrayList();

   public JavaXmlTypeMapping(JavaWsdlMapping javaWsdlMapping)
   {
      this.javaWsdlMapping = javaWsdlMapping;
   }

   public JavaWsdlMapping getJavaWsdlMapping()
   {
      return javaWsdlMapping;
   }

   public String getJavaType()
   {
      return javaType;
   }

   public void setJavaType(String javaType)
   {
      this.javaType = javaType;
   }

   public String getQnameScope()
   {
      return qnameScope;
   }

   public void setQNameScope(String qnameScope)
   {
      this.qnameScope = qnameScope;
   }

   public QName getRootTypeQName()
   {
      return rootTypeQName;
   }

   public void setRootTypeQName(QName rootTypeQName)
   {
      this.rootTypeQName = rootTypeQName;
   }

   public QName getAnonymousTypeQName()
   {
      return anonymousTypeQName;
   }

   public void setAnonymousTypeQName(QName anonymousTypeQName)
   {
      this.anonymousTypeQName = anonymousTypeQName;
   }

   public VariableMapping[] getVariableMappings()
   {
      VariableMapping[] arr = new VariableMapping[variableMappings.size()];
      variableMappings.toArray(arr);
      return arr;
   }

   public void addVariableMapping(VariableMapping variableMapping)
   {
      variableMappings.add(variableMapping);
   }

   public String toString()
   {
      return "[qname=" + rootTypeQName + ",javaType=" + javaType + ",scope=" + qnameScope + "]";
   }

   public String serialize()
   {
      StringBuffer sb = new StringBuffer(100);
      sb.append("<java-xml-type-mapping>");
      sb.append("<java-type>").append(javaType).append("</java-type>");

      if (rootTypeQName != null)
      {
         sb.append("<root-type-qname xmlns:typeNS='").append(rootTypeQName.getNamespaceURI()).append("'>");
         sb.append(rootTypeQName.getPrefix()).append(':').append(rootTypeQName.getLocalPart());
         sb.append("</root-type-qname>");
      }

      if (anonymousTypeQName != null)
      {
         sb.append("<anonymous-type-qname xmlns:typeNS='").append(anonymousTypeQName.getNamespaceURI()).append("'>");
         sb.append(anonymousTypeQName.getPrefix()).append(':').append(anonymousTypeQName.getLocalPart());
         sb.append("</anonymous-type-qname>");
      }

      sb.append("<qname-scope>").append(qnameScope).append("</qname-scope>");

      int len = variableMappings.size();
      for(int i = 0 ; i < len ; i ++)
         sb.append(((VariableMapping)variableMappings.get(i)).serialize());

      sb.append("</java-xml-type-mapping>");
      return sb.toString();
   }
}
