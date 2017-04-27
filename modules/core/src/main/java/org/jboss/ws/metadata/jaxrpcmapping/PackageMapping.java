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
 * XML mapping of the java-wsdl-mapping/package-mapping element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class PackageMapping implements Serializable
{
   private static final long serialVersionUID = 8105452343429986503L;

   // The parent <java-wsdl-mapping> element
   private JavaWsdlMapping javaWsdlMapping;

   // The required <package-type> element
   private String packageType;
   // The required <namespaceURI> element
   private String namespaceURI;

   public PackageMapping(JavaWsdlMapping javaWsdlMapping)
   {
      this.javaWsdlMapping = javaWsdlMapping;
   }

   public JavaWsdlMapping getJavaWsdlMapping()
   {
      return javaWsdlMapping;
   }

   public String getNamespaceURI()
   {
      return namespaceURI;
   }

   public void setNamespaceURI(String namespaceURI)
   {
      this.namespaceURI = namespaceURI;
   }

   public String getPackageType()
   {
      return packageType;
   }

   public void setPackageType(String packageType)
   {
      this.packageType = packageType;
   }

   public String toString()
   {
      return "[namespaceURI=" + namespaceURI + ",packageType=" + packageType + "]";
   }
   
   public String serialize()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<package-mapping>").append("<package-type>").append(packageType).append("</package-type>");
      sb.append("<namespaceURI>").append(namespaceURI).append("</namespaceURI>").append("</package-mapping>");
      return sb.toString();
   }
}
