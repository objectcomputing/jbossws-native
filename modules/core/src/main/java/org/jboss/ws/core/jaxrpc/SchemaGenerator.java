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
package org.jboss.ws.core.jaxrpc;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.tools.JavaToXSD;

/** A generator for XML schema that is used by unconfigured DII clients
 *
 * @author Thomas.Diesler@jboss.org
 * @author Anil.Saldhana@jboss.org
 * @since 03-Jun-2005
 */
public class SchemaGenerator
{
   // provide logging
   private static final Logger log = Logger.getLogger(SchemaGenerator.class);

   private boolean restrictToTargetNamespace = true;

   public JBossXSModel generateXSDSchema(QName xmlType, Class javaType)
   {
      if(log.isDebugEnabled()) log.debug("generateXSDSchema: [xmlType=" + xmlType + ",javaType=" + javaType.getName() + "]");
      try
      {
         assertXmlType(xmlType);
         String nsuri = xmlType.getNamespaceURI();
         Class componentType = javaType;
         while (componentType.isArray())
            componentType = componentType.getComponentType();

         JavaToXSD javaToXSD = new JavaToXSD();

         // Force all DII arrays to use the same array namespace for the component
         if (! componentType.isPrimitive())
         {
            Map<String, String> namespaceMap = new HashMap<String, String>();
            namespaceMap.put(componentType.getPackage().getName(), nsuri);
            javaToXSD.setPackageNamespaceMap(namespaceMap);
         }

         JBossXSModel xsModel = javaToXSD.generateForSingleType(xmlType, javaType);
         if (xsModel == null)
            throw new WSException("Cannot generate XSModel");

         if(log.isDebugEnabled()) log.debug("\n" + xsModel.serialize());
         return xsModel;
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception e)
      {
         throw new JAXRPCException("Cannot generate xsdSchema for: " + xmlType, e);
      }
   }

   /**
    * Flag that indicates that the types generated fall under the target namespace
    * default is false
    * @param restrictToTargetNamespace The restrictToTargetNamespace to set.
    */
   public void setRestrictToTargetNamespace(boolean restrictToTargetNamespace)
   {
      this.restrictToTargetNamespace = restrictToTargetNamespace;
   }

   /** Assert that the given namespace has a valid URI
    */
   private void assertXmlType(QName xmlType)
   {
      String nsURI = xmlType.getNamespaceURI();
      if (nsURI.length() == 0)
         throw new IllegalArgumentException("Invalid namespace for type: " + xmlType);
   }
}
