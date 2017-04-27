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
package org.jboss.ws.core.jaxrpc.binding.jbossxb;

import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.xb.binding.MarshallingContext;
import org.jboss.xb.binding.ObjectLocalMarshaller;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.util.Dom2Sax;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since May 31, 2006
 */
public class JBossXBSupport {

   /**
    * Setup SchemaBinding associated with the ServiceMetaData.
    * In case of an unconfigured call it will be generated from JAXB properties.
    * <p>
    * The SchemaBinding expects to have an element binding for the
    * incomming xml element. Because the same element name can be reused
    * by various operations with different xml types, we have to add the
    * element binding on every invocation.
    *
    * @see JBossXBConstants#JBXB_ROOT_QNAME
    * @see JBossXBConstants#JBXB_TYPE_QNAME
    */
   public static SchemaBinding getOrCreateSchemaBinding(Map properties)
   {
      SchemaBinding schemaBinding = null;
      SchemaBindingBuilder bindingBuilder = new SchemaBindingBuilder();

      QName xmlName = (QName)properties.get(JBossXBConstants.JBXB_ROOT_QNAME);
      QName xmlType = (QName)properties.get(JBossXBConstants.JBXB_TYPE_QNAME);

      // Get the eagerly initialized SchemaBinding from the ServiceMetaData
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext != null)
      {
         OperationMetaData opMetaData = msgContext.getOperationMetaData();
         EndpointMetaData epMetaData = opMetaData.getEndpointMetaData();
         ServiceMetaData serviceMetaData = epMetaData.getServiceMetaData();
         schemaBinding = serviceMetaData.getSchemaBinding();
      }

      // In case of an unconfigured call generate the SchemaBinding from JAXB properties
      if (schemaBinding == null)
      {
         JBossXSModel xsModel = (JBossXSModel)properties.get(JBossXBConstants.JBXB_XS_MODEL);
         JavaWsdlMapping wsdlMapping = (JavaWsdlMapping)properties.get(JBossXBConstants.JBXB_JAVA_MAPPING);
         schemaBinding = bindingBuilder.buildSchemaBinding(xsModel, wsdlMapping);
      }

      // The SchemaBinding expects to have an element binding for the
      // incomming xml element. Because the same element name can be reused
      // by various operations with different xml types, we have to add the
      // element binding on every invocation.
      bindingBuilder.bindParameterToElement(schemaBinding, xmlName, xmlType);

      return schemaBinding;
   }

   /**
    * Create a Marshaller that serializes
    * <code>org.w3c.dom.Element</code>'s to a <code>org.xml.sax.ContentHandler</code>
    *
    * @return ObjectLocalMarshaller
    *
    * @see org.jboss.xb.binding.MarshallingContext#getContentHandler() 
    */
   public static ObjectLocalMarshaller getWildcardMarshaller()
   {
      return new ObjectLocalMarshaller() {
         public void marshal(MarshallingContext ctx, Object o)
         {
            if (o == null)
            {
               return;
            }

            Element e = (Element)o;
            ContentHandler ch = ctx.getContentHandler();
            try
            {
               Dom2Sax.dom2sax(e, ch);
            }
            catch (SAXException e1)
            {
               throw new IllegalStateException("Failed to marshal DOM element " + new QName(e.getNamespaceURI(), e.getLocalName()) + ": " + e1.getMessage());
            }
         }
      };
   }
}
