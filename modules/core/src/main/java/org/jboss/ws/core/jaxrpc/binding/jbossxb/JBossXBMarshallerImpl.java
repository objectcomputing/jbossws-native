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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.WSException;
import org.jboss.ws.extensions.xop.jaxrpc.XOPMarshallerImpl;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaXmlTypeMapping;
import org.jboss.ws.metadata.jaxrpcmapping.VariableMapping;
import org.jboss.wsf.common.JavaUtils;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 * An implementation of a JAXB Marshaller that uses the JBossXB schema binding marshaller.
 *
 * @author Thomas.Diesler@jboss.org
 * @author Heiko.Braun@jboss.org
 * @since 05-Jul-2006
 */
public class JBossXBMarshallerImpl implements JBossXBMarshaller {

   // provide logging
   private static final Logger log = Logger.getLogger(JBossXBMarshallerImpl.class);

   // The marshaller properties
   private HashMap properties = new HashMap();

   private MarshallerImpl delegate;

   public JBossXBMarshallerImpl()
   {

      //ClassInfos.disableCache();

      delegate = new MarshallerImpl();
      delegate.setProperty(org.jboss.xb.binding.Marshaller.PROP_OUTPUT_XML_VERSION, "false");
      delegate.setProperty(org.jboss.xb.binding.Marshaller.PROP_OUTPUT_INDENTATION, "false");
      delegate.declareNamespace("xsi", Constants.NS_XML_SCHEMA_INSTANCE);
      delegate.setSupportNil(true);
   }

   /**
    * Marshal the content tree rooted at obj into a Writer.
    */
   public void marshal(Object obj, Writer writer) throws MarshalException
   {
      assertRequiredProperties();

      try
      {
         QName xmlName = (QName)getProperty(JBossXBConstants.JBXB_ROOT_QNAME);
         delegate.addRootElement(xmlName);

         QName xmlType = (QName)getProperty(JBossXBConstants.JBXB_TYPE_QNAME);
         boolean isAnonymousType = (xmlType != null && xmlType.getLocalPart().startsWith(">"));
         if (xmlType != null && !isAnonymousType)
         {
            delegate.setRootTypeQName(xmlType);
         }

         if (xmlName.getNamespaceURI().length() > 0)
         {
            String prefix = xmlName.getPrefix();
            String nsURI = xmlName.getNamespaceURI();
            delegate.declareNamespace(prefix, nsURI);
         }

         // wildcards still need to be mapped
         // todo: cleanup XB API
         JavaWsdlMapping wsdlMapping = (JavaWsdlMapping)getProperty(JBossXBConstants.JBXB_JAVA_MAPPING);
         if (wsdlMapping != null)
         {
            JavaXmlTypeMapping[] javaXmlMappings = wsdlMapping.getJavaXmlTypeMappings();
            if (javaXmlMappings != null)
            {
               for (int i = 0; i < javaXmlMappings.length; ++i)
               {
                  JavaXmlTypeMapping javaXmlMapping = javaXmlMappings[i];
                  VariableMapping[] variableMappings = javaXmlMapping.getVariableMappings();

                  if (variableMappings != null)
                  {
                     String clsName = javaXmlMapping.getJavaType();
                     Class cls = JavaUtils.loadJavaType(clsName, Thread.currentThread().getContextClassLoader());
                     QName clsQName = javaXmlMapping.getRootTypeQName();

                     if (clsQName != null)
                     {
                        // TODO: legacy API usage, see JBWS-1091
                        if ("complexType".equalsIgnoreCase(javaXmlMapping.getQnameScope()))
                        {
                           delegate.mapClassToXsiType(cls, clsQName.getNamespaceURI(), clsQName.getLocalPart());
                        }
                     }

                     for (int j = 0; j < variableMappings.length; ++j)
                     {
                        VariableMapping variableMapping = variableMappings[j];
                        if (variableMapping.getXmlWildcard())
                        {
                           delegate.mapFieldToWildcard(cls, "_any", JBossXBSupport.getWildcardMarshaller());
                        }
                     }
                  }
               }
            }
         }

         // the actual marshalling
         SchemaBinding schemaBinding = JBossXBSupport.getOrCreateSchemaBinding(properties);
         schemaBinding.setXopMarshaller(new XOPMarshallerImpl());
         delegate.marshal(schemaBinding, null, obj, writer);
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new MarshalException(e);
      }
   }

   /**
    * Marshal the content tree rooted at obj into SAX2 events.
    */
   public void marshal(Object obj, ContentHandler handler)
   {
      throw new NotImplementedException();
   }

   /**
    * Marshal the content tree rooted at obj into a DOM tree.
    */
   public void marshal(Object obj, Node node)
   {
      throw new NotImplementedException();
   }

   /**
    * Marshal the content tree rooted at obj into an output stream.
    */
   public void marshal(Object obj, OutputStream os) throws MarshalException
   {
      marshal(obj, new OutputStreamWriter(os));
   }

   /**
    * Get the particular property in the underlying implementation of
    * Marshaller.
    */
   public Object getProperty(String name)
   {
      if (name == null)
         throw new IllegalArgumentException("name parameter is null");

      return properties.get(name);
   }

   /**
    * Set the particular property in the underlying implementation of
    * Marshaller.
    *
    */
   public void setProperty(String name, Object value)
   {
      if (name == null)
         throw new IllegalArgumentException("name parameter is null");

      properties.put(name, value);
   }

   /**
    * Get a DOM tree view of the content tree(Optional).
    */
   public Node getNode(Object contentTree)
   {
      throw new NotImplementedException();
   }

   /**
    * Assert the required properties
    */
   private void assertRequiredProperties()
   {
      if (getProperty(JBossXBConstants.JBXB_SCHEMA_READER) == null && getProperty(JBossXBConstants.JBXB_XS_MODEL) == null)
         throw new WSException("Cannot find required property: " + JBossXBConstants.JBXB_XS_MODEL);

      if (getProperty(JBossXBConstants.JBXB_JAVA_MAPPING) == null)
         throw new WSException("Cannot find required property: " + JBossXBConstants.JBXB_JAVA_MAPPING);

      QName xmlName = (QName)getProperty(JBossXBConstants.JBXB_ROOT_QNAME);
      if (xmlName == null)
         throw new WSException("Cannot find required property: " + JBossXBConstants.JBXB_ROOT_QNAME);

      if (xmlName.getNamespaceURI().length() > 0 && xmlName.getPrefix().length() == 0)
         throw new IllegalArgumentException("The given root element name must be prefix qualified: " + xmlName);
   }
}
