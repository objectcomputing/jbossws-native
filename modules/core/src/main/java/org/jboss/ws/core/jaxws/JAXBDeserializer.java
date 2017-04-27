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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;

import org.jboss.logging.Logger;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.ComplexTypeDeserializer;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.binding.TypeMappingImpl;
import org.jboss.ws.extensions.xop.jaxws.AttachmentUnmarshallerImpl;
import org.jboss.wsf.spi.binding.BindingCustomization;

/**
 * A Deserializer that can handle complex types by delegating to JAXB.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public class JAXBDeserializer extends ComplexTypeDeserializer
{
   // provide logging
   private static final Logger log = Logger.getLogger(JAXBDeserializer.class);

   public JAXBDeserializer() throws BindingException
   {
   }

   public Object deserialize(QName xmlName, QName xmlType, Source xmlFragment, SerializationContext serContext) throws BindingException
   {
      if(log.isDebugEnabled()) log.debug("deserialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + "]");

      Object value = null;
      try
      {
         Class[] javaTypes = (Class[])serContext.getProperty(SerializationContextJAXWS.JAXB_CONTEXT_TYPES);

         TypeMappingImpl typeMapping = serContext.getTypeMapping();
         Class javaType = typeMapping.getJavaType(xmlType);

         JAXBContext jaxbContext = getJAXBContext(javaTypes);

         Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
         unmarshaller.setAttachmentUnmarshaller( new AttachmentUnmarshallerImpl());

         JAXBElement jbe = unmarshaller.unmarshal(xmlFragment, javaType);
         value = jbe.getValue();

         if(log.isDebugEnabled()) log.debug("deserialized: " + (value != null ? value.getClass().getName() : null));
      }
      catch (Exception ex)
      {
         handleUnmarshallException(ex);
      }
      return value;

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

   // 4.21 Conformance (Marshalling failure): If an error occurs when using the supplied JAXBContext to marshall
   // a request or unmarshall a response, an implementation MUST throw a WebServiceException whose
   // cause is set to the original JAXBException.
   private void handleUnmarshallException(Exception ex)
   {
      if (ex instanceof WebServiceException)
         throw (WebServiceException)ex;

      throw new WebServiceException(ex);
   }
}
