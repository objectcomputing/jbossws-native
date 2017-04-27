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
package org.jboss.ws.core.soap;

import java.lang.reflect.Method;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.binding.AbstractSerializerFactory;
import org.jboss.ws.core.binding.SerializerSupport;
import org.jboss.ws.core.binding.TypeMappingImpl;
import org.jboss.ws.core.jaxrpc.binding.NullValueSerializer;
import org.jboss.ws.core.jaxws.SerializationContextJAXWS;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.wsf.common.JavaUtils;

/**
 * Represents the OBJECT_VALID state of an {@link SOAPContentElement}.<br>
 *
 * @author Heiko.Braun@jboss.org
 * @since 05.02.2007
 */
public class ObjectContent extends SOAPContent
{

   private static Logger log = Logger.getLogger(ObjectContent.class);

   // The java object content of this element.
   private Object objectValue;

   protected ObjectContent(SOAPContentElement container)
   {
      super(container);
   }

   State getState()
   {
      return State.OBJECT_VALID;
   }

   SOAPContent transitionTo(State nextState)
   {
      SOAPContent next = null;

      if (nextState == State.XML_VALID)
      {
         XMLFragment fragment = marshallObjectContents();
         XMLContent xmlValid = new XMLContent(container);
         xmlValid.setXMLFragment(fragment);
         next = xmlValid;
      }
      else if (nextState == State.OBJECT_VALID)
      {
         next = this;
      }
      else if (nextState == State.DOM_VALID)
      {
         // first transition to XML valid
         XMLFragment fragment = marshallObjectContents();
         XMLContent tmp = new XMLContent(container);
         tmp.setXMLFragment(fragment);

         // finally from XML valid to DOM valid
         next = tmp.transitionTo(State.DOM_VALID);
      }
      else
      {
         throw new IllegalArgumentException("Illegal state requested: " + nextState);
      }

      return next;
   }

   public Source getPayload()
   {
      throw new IllegalStateException("Payload not available");
   }

   public void setPayload(Source source)
   {
      throw new IllegalStateException("Payload cannot be set on object content");
   }

   public XMLFragment getXMLFragment()
   {

      throw new IllegalStateException("XMLFragment not available");
   }

   public void setXMLFragment(XMLFragment xmlFragment)
   {
      throw new IllegalStateException("XMLFragment not available");
   }

   public Object getObjectValue()
   {
      return objectValue;
   }

   public void setObjectValue(Object objValue)
   {
      this.objectValue = objValue;
   }

   private XMLFragment marshallObjectContents()
   {
      QName xmlType = container.getXmlType();
      Class javaType = container.getJavaType();

      log.debug("getXMLFragment from Object [xmlType=" + xmlType + ",javaType=" + javaType + "]");

      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext == null)
         throw new WSException("MessageContext not available");

      SerializationContext serContext = msgContext.getSerializationContext();
      serContext.setJavaType(javaType);
      ParameterMetaData pmd = container.getParamMetaData();
      OperationMetaData opMetaData = pmd.getOperationMetaData();
      List<Class> registeredTypes = opMetaData.getEndpointMetaData().getRegisteredTypes();
      serContext.setProperty(SerializationContextJAXWS.JAXB_CONTEXT_TYPES, registeredTypes.toArray(new Class[0]));

      TypeMappingImpl typeMapping = serContext.getTypeMapping();
      XMLFragment xmlFragment = null;
      try
      {
         SerializerSupport ser;
         if (objectValue != null)
         {
            AbstractSerializerFactory serializerFactory = getSerializerFactory(typeMapping, javaType, xmlType);
            ser = (SerializerSupport)serializerFactory.getSerializer();
         }
         else
         {
            ser = new NullValueSerializer();
         }

         Result result = ser.serialize(container, serContext);

         xmlFragment = new XMLFragment(result);
         log.debug("xmlFragment: " + xmlFragment);
      }
      catch (BindingException e)
      {
         throw new WSException(e);
      }

      return xmlFragment;
   }

   /**
    * Get the serializer factory for a given javaType and xmlType
    */
   private AbstractSerializerFactory getSerializerFactory(TypeMappingImpl typeMapping, Class javaType, QName xmlType)
   {
      AbstractSerializerFactory serializerFactory = (AbstractSerializerFactory)typeMapping.getSerializer(javaType, xmlType);

      // The type mapping might contain a mapping for the array wrapper bean
      if (serializerFactory == null && javaType.isArray())
      {
         Class arrayWrapperType = typeMapping.getJavaType(xmlType);
         if (arrayWrapperType != null)
         {
            try
            {
               Method toArrayMethod = arrayWrapperType.getMethod("toArray", new Class[] {});
               Class returnType = toArrayMethod.getReturnType();
               if (JavaUtils.isAssignableFrom(javaType, returnType))
               {
                  serializerFactory = (AbstractSerializerFactory)typeMapping.getSerializer(arrayWrapperType, xmlType);
               }
            }
            catch (NoSuchMethodException e)
            {
               // ignore
            }
         }
      }

      if (serializerFactory == null)
         throw new WSException("Cannot obtain serializer factory for: [xmlType=" + xmlType + ",javaType=" + javaType + "]");

      return serializerFactory;
   }

}
