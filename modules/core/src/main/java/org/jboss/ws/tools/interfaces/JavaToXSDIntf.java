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

import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;


/**
 * Interface that defines the contract for all Java To Schema converters
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 23, 2005
 */

public interface JavaToXSDIntf
{

   /**
    * Method that is used to obtain a Schema Model given
    * a Java class, a XMLName and a XMLType
    * @param xmlType
    * @param javaType Class object that is the type of the xmlType
    * @return a schema model
    * @throws IOException
    */
   public JBossXSModel generateForSingleType(QName xmlType, Class javaType)
   throws IOException;

   public JBossXSModel generateForSingleType(QName xmlType, Class javaType, Map<String, QName> elementNames)
   throws IOException;

   /**
    * Get the SchemaCreator (that deals with creation of schema types)
    * Pluggable feature of JavaToXSD
    * @return
    */
   public SchemaCreatorIntf getSchemaCreator();

   /**
    * A map of package->namespace map that denote user customization
    *
    * @param map
    */
   public void setPackageNamespaceMap(Map<String,String> map);

   /**
    * Set the WSDLStyle
    * @see org.jboss.ws.Constants for Constants.DOCUMENT_LITERAL
    *      and  Constants.RPC_LITERAL
    * @param wsdlStyle
    */
   public void setWSDLStyle(String wsdlStyle);
}

