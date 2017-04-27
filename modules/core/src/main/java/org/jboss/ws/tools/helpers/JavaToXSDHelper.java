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
package org.jboss.ws.tools.helpers;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSModel;
import org.jboss.ws.Constants;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.ws.tools.interfaces.SchemaCreatorIntf;
import org.jboss.ws.tools.schema.SchemaTypeCreator;

/**
 *  Helper class used by the JavaToXSD subsystem
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 8, 2005
 */
public class JavaToXSDHelper
{
   protected WSDLUtils utils = WSDLUtils.getInstance();
   protected SchemaUtils schemautils = SchemaUtils.getInstance();
   private SchemaCreatorIntf creator = null;

   private String wsdlStyle = Constants.RPC_LITERAL;

   protected String jaxwsAssert = "JAXRPC2.0 Assertion:";

   public JavaToXSDHelper()
   {
      creator = new SchemaTypeCreator();
   }

   /**
    * Given a XMLType and a JavaType, generate a JBossXSModel
    * @param xmlType QName of the XMLType
    * @param javaType Class Type
    * @param targetNamespace
    * @return JBossXSModel
    * @throws IllegalArgumentException if targetNamespace is null
    */
   public JBossXSModel generateXSModel(QName xmlType, Class javaType,
             String targetNamespace)
   {
      if(targetNamespace == null)
         throw new IllegalArgumentException("Illegal Null Argument: targetNamespace");

      XSModel xsmodel = creator.getXSModel();
      if(xsmodel == null) creator.setXSModel(new JBossXSModel());
      //    Special case: if javaType is a Standard jaxrpc holder
      Class cls = utils.getJavaTypeForHolder(javaType);
      if(cls != null) return null;
      creator.generateType(null, javaType  );
      return creator.getXSModel();
   }

   /**
    * Get the SchemaCreator
    *
    * @return
    */
   public SchemaCreatorIntf getSchemaCreator()
   {
      return creator;
   }


   /**
    * Get the WSDL Style
    *
    * @return
    */
   public String getWsdlStyle()
   {
      return wsdlStyle;
   }

   /**
    * A customized Package->Namespace map
    *
    * @param map
    */
   public void setPackageNamespaceMap(Map<String,String> map)
   {
      creator.setPackageNamespaceMap(map);
   }

   /**
    * Set the WSDL Style
    *
    * @param wsdlStyle
    */
   public void setWsdlStyle(String wsdlStyle)
   {
      this.wsdlStyle = wsdlStyle;
   }
}
