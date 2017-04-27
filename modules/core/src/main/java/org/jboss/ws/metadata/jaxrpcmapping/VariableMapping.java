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

/**
 * XML mapping of the java-wsdl-mapping/java-xml-type-mapping/varaible-mapping element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class VariableMapping implements Serializable
{
   private static final long serialVersionUID = 4168728468137337167L;

   // The parent <java-wsdl-mapping> element
   private JavaXmlTypeMapping typeMapping;

   // The required <java-variable-name> element
   private String javaVariableName;
   // The optional <data-member> element
   private boolean dataMember;
   // The choice [<xml-attribute-name> | <xml-element-name> | <xml-wildcard>]
   private String xmlAttributeName;
   private String xmlElementName;
   private boolean xmlWildcard; 

   public VariableMapping(JavaXmlTypeMapping typeMapping)
   {
      this.typeMapping = typeMapping;
   }

   public JavaXmlTypeMapping getTypeMapping()
   {
      return typeMapping;
   }

   public boolean isDataMember()
   {
      return dataMember;
   }

   public void setDataMember(boolean dataMember)
   {
      this.dataMember = dataMember;
   }

   public String getJavaVariableName()
   {
      return javaVariableName;
   }

   public void setJavaVariableName(String javaVariableName)
   {
      this.javaVariableName = javaVariableName;
   }

   public String getXmlAttributeName()
   {
      return xmlAttributeName;
   }

   public void setXmlAttributeName(String xmlAttributeName)
   {
      this.xmlAttributeName = xmlAttributeName;
   }

   public String getXmlElementName()
   {
      return xmlElementName;
   }

   public void setXmlElementName(String xmlElementName)
   {
      this.xmlElementName = xmlElementName;
   }

   public boolean getXmlWildcard()
   {
      return xmlWildcard;
   }

   public void setXmlWildcard(boolean xmlWildcard)
   {
      this.xmlWildcard = xmlWildcard;
   }  
   
   public String serialize()
   {
      StringBuffer sb = new StringBuffer(); 
      sb.append("<variable-mapping>");
      sb.append("<java-variable-name>").append(javaVariableName).append("</java-variable-name>");
      if(dataMember)
      {
         sb.append("<data-member/>");
      }
      if (xmlElementName != null)
      {
         sb.append("<xml-element-name>").append(xmlElementName).append("</xml-element-name>");
      }
      else if (xmlAttributeName != null)
      {
         sb.append("<xml-attribute-name>").append(xmlAttributeName).append("</xml-attribute-name>");
      }
      else if (xmlWildcard)
      {
         sb.append("<xml-wildcard/>");
      }
      sb.append("</variable-mapping>");
      return sb.toString();
   }
}
