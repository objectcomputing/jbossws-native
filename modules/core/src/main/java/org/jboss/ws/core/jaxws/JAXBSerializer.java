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
package org.jboss.ws.core.jaxws;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.ws.WebServiceException;

import org.jboss.logging.Logger;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.ComplexTypeSerializer;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.extensions.xop.jaxws.AttachmentMarshallerImpl;
import org.jboss.ws.util.xml.BufferedStreamResult;
import org.jboss.wsf.spi.binding.BindingCustomization;
import org.w3c.dom.NamedNodeMap;

/**
 * A Serializer that can handle complex types by delegating to JAXB.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public class JAXBSerializer extends ComplexTypeSerializer
{
   // provide logging
   private static final Logger log = Logger.getLogger(JAXBSerializer.class);

   public JAXBSerializer() throws BindingException
   {
   }

   @Override
   public Result serialize(QName xmlName, QName xmlType, Object value, SerializationContext serContext, NamedNodeMap attributes) throws BindingException
   {
      if (log.isDebugEnabled()) log.debug("serialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + "]");

      Result result = null;
      try
      {
         Class expectedType = serContext.getJavaType();
         Class[] types = getClassesForContextCreation(serContext, value);
         
         JAXBContext jaxbContext = getJAXBContext(types);

         Marshaller marshaller = jaxbContext.createMarshaller();

         marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
         marshaller.setAttachmentMarshaller(new AttachmentMarshallerImpl());

         // It's safe to pass a stream result, because the SCE will always be in XML_VALID state afterwards.
         // This state can safely be written to an outstream. See XMLFragment and XMLContent as well.
         result = new BufferedStreamResult();
         marshaller.marshal(new JAXBElement(xmlName, expectedType, value), result);

         if (log.isDebugEnabled()) log.debug("serialized: " + result);
      }
      catch (Exception ex)
      {
         handleMarshallException(ex);
      }

      return result;
   }
   
   /**
    * Selects the array of classes to use to create the JAXBContext, as
    * using the array of types registered for the current endpoint saves time.
    * 
    * @param serContext
    * @param value
    * @return
    */
   private Class[] getClassesForContextCreation(SerializationContext serContext, Object value)
   {
      // The serialization context contains the base type, which is needed for JAXB to marshall xsi:type correctly.
      // This should be more efficient and accurate than searching the type mapping
      Class expectedType = serContext.getJavaType();
      Class actualType = value.getClass();
      Class[] types = shouldFilter(actualType) ? new Class[]{expectedType} : new Class[]{expectedType, actualType};
      Class[] registeredTypes = (Class[])serContext.getProperty(SerializationContextJAXWS.JAXB_CONTEXT_TYPES);
      List<Class> typesList = Arrays.asList(registeredTypes);
      return typesList.containsAll(Arrays.asList(types)) ? registeredTypes : types;
   }
   
   /**
    * Retrieve JAXBContext from cache or create new one and cache it.
    * @param types
    * @return JAXBContext
    */
   private JAXBContext getJAXBContext(Class[] types){
      JAXBContextCache cache = JAXBContextCache.getContextCache();
      JAXBContext context = cache.get(types);
      if(null==context)
      {
         BindingCustomization bindingCustomization = getBindingCustomization();
         context = JAXBContextFactory.newInstance().createContext(types, bindingCustomization);
         cache.add(types, context);
      }
      return context;
   }

   // Remove this when we add a XMLGregorianCalendar Serializer
   private boolean shouldFilter(Class<?> actualType)
   {
      return XMLGregorianCalendar.class.isAssignableFrom(actualType);
   }

   // 4.21 Conformance (Marshalling failure): If an error occurs when using the supplied JAXBContext to marshall
   // a request or unmarshall a response, an implementation MUST throw a WebServiceException whose
   // cause is set to the original JAXBException.
   private void handleMarshallException(Exception ex)
   {
      if (ex instanceof WebServiceException)
         throw (WebServiceException)ex;

      throw new WebServiceException(ex);
   }
}
