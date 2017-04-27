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
package org.jboss.ws.core.jaxrpc.binding;

import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.xs.XSModel;
import org.jboss.logging.Logger;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.ComplexTypeSerializer;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.jaxrpc.SerializationContextJAXRPC;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBConstants;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBMarshaller;
import org.jboss.ws.core.jaxrpc.binding.jbossxb.JBossXBMarshallerImpl;
import org.jboss.ws.core.soap.XMLFragment;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.util.xml.BufferedStreamResult;
import org.w3c.dom.NamedNodeMap;

/**
 * A Serializer that can handle complex types by delegating to JAXB.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public class JBossXBSerializer extends ComplexTypeSerializer
{
   // provide logging
   private static final Logger log = Logger.getLogger(JBossXBSerializer.class);

   private JBossXBMarshaller marshaller;

   public JBossXBSerializer() throws BindingException
   {
      // Get the JAXB marshaller for complex objects
      marshaller = new JBossXBMarshallerImpl();
   }

   /**
    * For marshalling the WS layer passes to the JAXB layer
    *
    *    - optional java object instance
    *    - required map of packaged or generated XSDSchema
    *    - required QName of the root element
    *    - optional QName of the root complex type
    *    - optional instance of JavaWsdlMapping
    *
    * If the object value is null, the corresponding XML representation of the nillable element should be marshalled.
    * The xmlType is redundant if the xmlName corresponds to a global element definition in schema.
    * If the java mapping is null, default mapping rules apply.
    *
    * The result is a self contained (i.e. contains all namespace definitions) XML document without the XML declaration.
    * In case of an marshalling problem a descriptive exception is thrown.
    */
   public Result serialize(QName xmlName, QName xmlType, Object value, SerializationContext serContext, NamedNodeMap attributes) throws BindingException
   {
      log.debug("serialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + "]");

      // Expect the specific JAXRPC serialization context
      SerializationContextJAXRPC jaxrpcContext = (SerializationContextJAXRPC)serContext;

      try
      {
         // Get the parsed model
         XSModel model = jaxrpcContext.getXsModel();

         // Get the jaxrpc-mapping.xml object graph
         JavaWsdlMapping jaxrpcMapping = jaxrpcContext.getJavaWsdlMapping();

         // schemabinding marshaller is the default delegate
         JBossXBMarshaller delegate = marshaller;

         // marshalling context
         delegate.setProperty(JBossXBConstants.JBXB_XS_MODEL, model);
         delegate.setProperty(JBossXBConstants.JBXB_TYPE_QNAME, xmlType);
         delegate.setProperty(JBossXBConstants.JBXB_ROOT_QNAME, xmlName);
         delegate.setProperty(JBossXBConstants.JBXB_JAVA_MAPPING, jaxrpcMapping);

         // marshall
         StringWriter strwr = new StringWriter();
         delegate.marshal(value, strwr);
         String xmlFragment = strwr.toString();

         log.debug("serialized: " + xmlFragment);
         return new BufferedStreamResult(xmlFragment);
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         throw new BindingException(ex);
      }
   }
}
