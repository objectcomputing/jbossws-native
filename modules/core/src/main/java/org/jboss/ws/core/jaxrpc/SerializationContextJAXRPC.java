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

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSModel;
import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.jaxrpc.handler.SOAPMessageContextJAXRPC;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaXmlTypeMapping;
import org.jboss.ws.metadata.jaxrpcmapping.PackageMapping;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.TypesMetaData;
import org.jboss.wsf.common.JavaUtils;

/**
 * The serialization context for JAXRPC endpoints/clients
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public class SerializationContextJAXRPC extends SerializationContext
{
   // provide logging
   private static final Logger log = Logger.getLogger(SerializationContextJAXRPC.class);

   // XML mapping from jaxrpc-mapping.xml
   private JavaWsdlMapping jaxrpcMapping;

   public JavaWsdlMapping getJavaWsdlMapping()
   {
      if (jaxrpcMapping == null)
      {
         log.debug("Generate jaxrpcMapping from typeMapping");

         jaxrpcMapping = new JavaWsdlMapping();
         for (QName xmlType : getTypeMapping().getRegisteredXmlTypes())
         {
            String nsURI = xmlType.getNamespaceURI();
            if (!Constants.NS_SCHEMA_XSD.equals(nsURI) && !Constants.NS_ATTACHMENT_MIME_TYPE.equals(nsURI))
            {
               Class javaType = getTypeMapping().getJavaType(xmlType);
               String javaTypeName = javaType.getName();

               Class componentType = javaType;
               while (componentType.isArray())
                  componentType = componentType.getComponentType();

               if (JavaUtils.isPrimitive(componentType))
                  componentType = JavaUtils.getWrapperType(componentType);

               Package packageObject = componentType.getPackage();
               String packageName = (packageObject != null) ? packageObject.getName() : "";
               String packageType = jaxrpcMapping.getPackageNameForNamespaceURI(nsURI);
               if (packageName.equals(packageType) == false)
               {
                  PackageMapping packageMapping = new PackageMapping(jaxrpcMapping);
                  packageMapping.setNamespaceURI(nsURI);
                  packageMapping.setPackageType(packageName);
                  jaxrpcMapping.addPackageMapping(packageMapping);
                  if (log.isDebugEnabled())
                     log.debug("Add package mapping: " + packageMapping);
               }

               // Do not add mappings for array types
               if (javaType.isArray())
                  continue;

               JavaXmlTypeMapping xmlTypeMapping = jaxrpcMapping.getTypeMappingForQName(xmlType);
               if (xmlTypeMapping == null)
               {
                  xmlTypeMapping = new JavaXmlTypeMapping(jaxrpcMapping);
                  xmlTypeMapping.setQNameScope("complexType");
                  xmlTypeMapping.setJavaType(javaTypeName);
                  xmlTypeMapping.setRootTypeQName(xmlType);
                  jaxrpcMapping.addJavaXmlTypeMappings(xmlTypeMapping);
                  if (log.isDebugEnabled())
                     log.debug("Add type mapping: " + xmlTypeMapping);
               }
            }
         }
      }
      return jaxrpcMapping;
   }

   public void setJavaWsdlMapping(JavaWsdlMapping jaxrpcMapping)
   {
      this.jaxrpcMapping = jaxrpcMapping;
   }

   public XSModel getXsModel()
   {
      SOAPMessageContextJAXRPC msgContext = (SOAPMessageContextJAXRPC)MessageContextAssociation.peekMessageContext();
      if (msgContext == null)
         throw new WSException("MessageContext not available");

      OperationMetaData opMetaData = msgContext.getOperationMetaData();
      ServiceMetaData serviceMetaData = opMetaData.getEndpointMetaData().getServiceMetaData();
      TypesMetaData typesMetaData = serviceMetaData.getTypesMetaData();
      return typesMetaData.getSchemaModel();
   }
}
