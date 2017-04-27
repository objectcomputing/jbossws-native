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

import javax.xml.namespace.QName;
import javax.xml.transform.Result;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.binding.AbstractSerializerFactory;
import org.jboss.ws.core.binding.SerializerSupport;
import org.jboss.ws.core.binding.TypeMappingImpl;
import org.jboss.ws.core.soap.NameImpl;
import org.jboss.ws.core.soap.SOAPContentElement;
import org.jboss.ws.core.soap.XMLFragment;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.util.xml.BufferedStreamResult;
import org.jboss.wsf.common.JavaUtils;
import org.w3c.dom.NamedNodeMap;

/**
 * A Serializer that can handle SOAP encoded arrays.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 31-Oct-2005
 */
public class SOAPArraySerializer extends SerializerSupport
{
   // provide logging
   private static final Logger log = Logger.getLogger(SOAPArraySerializer.class);

   private ParameterMetaData paramMetaData;
   private SerializerSupport compSerializer;
   private NullValueSerializer nullSerializer;
   private boolean isArrayComponentType;
   private StringBuilder buffer;

   public SOAPArraySerializer() throws BindingException
   {
      nullSerializer = new NullValueSerializer();
   }

   public Result serialize(SOAPContentElement soapElement, SerializationContext serContext) throws BindingException
   {
      paramMetaData = soapElement.getParamMetaData();
      QName xmlName = soapElement.getElementQName();
      QName xmlType = soapElement.getXmlType();
      Object value = soapElement.getObjectValue();
      NamedNodeMap attributes = soapElement.getAttributes();
      return serialize(xmlName, xmlType, value, serContext, attributes);
   }

   public Result serialize(QName xmlName, QName xmlType, Object value, SerializationContext serContext, NamedNodeMap attributes) throws BindingException
   {
      log.debug("serialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + ",valueType=" + value.getClass().getName() + "]");
      try
      {
         if (paramMetaData == null)
            throw new IllegalStateException("Use serialize(SOAPContenentElement, SerializationContext)");

         QName compXmlName = paramMetaData.getXmlName();
         QName compXmlType = paramMetaData.getSOAPArrayCompType();
         Class javaType = paramMetaData.getJavaType();

         Class compJavaType = javaType.getComponentType();
         isArrayComponentType = isArrayJavaType(compJavaType) && isArrayXmlType(compXmlType);
         while (compJavaType.getComponentType() != null && isArrayComponentType == false)
         {
            compJavaType = compJavaType.getComponentType();
            isArrayComponentType = isArrayJavaType(compJavaType) && isArrayXmlType(compXmlType);
         }

         TypeMappingImpl typeMapping = serContext.getTypeMapping();
         if (compXmlType == null)
         {
            compXmlType = typeMapping.getXMLType(compJavaType);
            paramMetaData.setSOAPArrayCompType(compXmlType);
         }

         if (compXmlType == null)
            throw new WSException("Cannot obtain component xmlType for: " + compJavaType);

         // Get the component type serializer factory
         log.debug("Get component serializer for: [javaType=" + compJavaType.getName() + ",xmlType=" + compXmlType + "]");
         AbstractSerializerFactory compSerializerFactory = (AbstractSerializerFactory)typeMapping.getSerializer(compJavaType, compXmlType);
         if (compSerializerFactory == null)
         {
            log.warn("Cannot obtain component serializer for: [javaType=" + compJavaType.getName() + ",xmlType=" + compXmlType + "]");
            compSerializerFactory = (AbstractSerializerFactory)typeMapping.getSerializer(null, compXmlType);
         }
         if (compSerializerFactory == null)
            throw new WSException("Cannot obtain component serializer for: " + compXmlType);

         // Get the component type serializer
         compSerializer = (SerializerSupport)compSerializerFactory.getSerializer();

         // Get the corresponding wrapper type
         if (JavaUtils.isPrimitive(value.getClass()))
            value = JavaUtils.getWrapperValueArray(value);

         String nodeName = new NameImpl(compXmlName).getQualifiedName();

         buffer = new StringBuilder("<" + nodeName + " xmlns:" + Constants.PREFIX_SOAP11_ENC + "='" + Constants.URI_SOAP11_ENC + "' ");

         if (!(value instanceof Object[]))
            throw new WSException("Unsupported array type: " + javaType);

         Object[] objArr = (Object[])value;
         String arrayDim = "" + objArr.length;

         // Get multiple array dimension
         Object[] subArr = (Object[])value;
         while (isArrayComponentType == false && subArr.length > 0 && subArr[0] instanceof Object[])
         {
            subArr = (Object[])subArr[0];
            arrayDim += "," + subArr.length;
         }

         compXmlType = serContext.getNamespaceRegistry().registerQName(compXmlType);
         compXmlName = serContext.getNamespaceRegistry().registerQName(compXmlName);
         String arrayType = Constants.PREFIX_SOAP11_ENC + ":arrayType='" + compXmlType.getPrefix() + ":" + compXmlType.getLocalPart() + "[" + arrayDim + "]'";

         buffer.append(arrayType);
         buffer.append(" xmlns:" + Constants.PREFIX_XSI + "='" + Constants.NS_SCHEMA_XSI + "'");
         if (compXmlType.getNamespaceURI().equals(Constants.URI_SOAP11_ENC) == false)
            buffer.append(" xmlns:" + compXmlType.getPrefix() + "='" + compXmlType.getNamespaceURI() + "'");
         if (compXmlName.getNamespaceURI().length() > 0 && compXmlName.getNamespaceURI().equals(compXmlType.getNamespaceURI()) == false)
            buffer.append(" xmlns:" + compXmlName.getPrefix() + "='" + compXmlName.getNamespaceURI() + "'");

         buffer.append(">");
         serializeArrayComponents(compXmlName, compXmlType, serContext, objArr);
         buffer.append("</" + nodeName + ">");
         String xmlFragment = buffer.toString();
         
         log.debug("serialized: " + xmlFragment);
         return new BufferedStreamResult(xmlFragment);
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new BindingException(e);
      }
   }

   private void serializeArrayComponents(QName xmlName, QName xmlType, SerializationContext serContext, Object[] objArr) throws BindingException
   {
      for (Object compValue : objArr)
      {
         if (isArrayComponentType == false && compValue instanceof Object[])
         {
            serializeArrayComponents(xmlName, xmlType, serContext, (Object[])compValue);
         }
         else
         {
            SerializerSupport ser = compSerializer;

            // Null component value
            if (compValue == null)
            {
               ser = nullSerializer;
            }

            Result result = ser.serialize(new QName("item"), xmlType, compValue, serContext, null);
            buffer.append(new XMLFragment(result).toXMLString());
         }
      }
   }

   /** True for all array xmlTypes, i.e. nmtokens, base64Binary, hexBinary
    * 
    *  FIXME: This method should be removed as soon as we can reliably get the SOAP
    *  arrayType from wsdl + schema. 
    */
   private boolean isArrayXmlType(QName xmlType)
   {
      boolean isArrayType = Constants.TYPE_SOAP11_BASE64.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_SOAP11_BASE64.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_SOAP11_BASE64BINARY.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_SOAP11_HEXBINARY.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_SOAP11_NMTOKENS.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_LITERAL_BASE64BINARY.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_LITERAL_HEXBINARY.equals(xmlType);
      isArrayType = isArrayType || Constants.TYPE_LITERAL_NMTOKENS.equals(xmlType);
      return isArrayType;
   }

   /** True for all array javaTypes, i.e. String[], Byte[], byte[] 
    * 
    *  FIXME: This method should be removed as soon as we can reliably get the SOAP
    *  arrayType from wsdl + schema. 
    */
   private boolean isArrayJavaType(Class javaType)
   {
      boolean isBinaryType = String[].class.equals(javaType) || Byte[].class.equals(javaType) || byte[].class.equals(javaType);
      return isBinaryType;
   }

}
