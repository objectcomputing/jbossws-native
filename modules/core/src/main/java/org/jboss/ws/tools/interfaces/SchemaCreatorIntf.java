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
package org.jboss.ws.tools.interfaces;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSTypeDefinition;

/**
 * Defines the contract for Schema Creating agents (Java -> XSD Process)
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 23, 2005
 */

public interface SchemaCreatorIntf
{

   /**
    * Add a package - namespace mapping entry
    * @param pkgname
    * @param ns
    * @throws IllegalArgumentException if either pkgname or ns is null
    */
   public void addPackageNamespaceMapping(String pkgname, String ns);

   /**
    * Return a HashMap of custom namespaces like ns1, ns2 etc
    * @return
    */
   public HashMap getCustomNamespaceMap();

   /**
    * get the XSModel representing the targetNS
    * @return
    */
   public JBossXSModel getXSModel();

   public JavaWsdlMapping getJavaWsdlMapping();

   /**
    * @return Returns the packageNamespaceMap.
    */
   public Map<String, String> getPackageNamespaceMap();

   /**
    * Return the Type Mapping
    * @return
    */
   public LiteralTypeMapping getTypeMapping();

   /**
    * Main method that is involved in generating a Schema Type
    * @param xmlType QName of the Complex Type. Can be null
    * @param javaType Java class for the type. Can be null
    * @return  Schema Type
    */
   public JBossXSTypeDefinition generateType(QName xmlType, Class javaType);

   public JBossXSTypeDefinition generateType(QName xmlType, Class javaType, Map<String, QName> elementNames);

   /**
    * Given a XML Type, return the Java class
    *
    * @param xmlType
    * @return
    */

   public Class getJavaType(QName xmlType);

   /**
    * Given a Java class, return the XML Type
    *
    * @param javaType
    * @return
    */

   public QName getXMLSchemaType(Class javaType);

   /**
    * Users can provide a customized map of java packages to xml namespace
    * @param packageNamespaceMap The packageNamespaceMap to set.
    */
   public void setPackageNamespaceMap(Map<String, String> packageNamespaceMap);

   /**
    * SchemaCreator maintains a map of namespaces that will be fed back
    * into WSDLDefinitions for all custom namespaces
    * @param nsuri
    * @return
    */
   public String allocatePrefix(String nsuri);

   /**
    * Set the XSModel representing the targetNS
    * @param xsm
    */
   public void setXSModel(JBossXSModel xsm);

}
