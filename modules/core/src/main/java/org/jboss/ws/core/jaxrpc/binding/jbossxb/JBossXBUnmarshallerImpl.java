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

import java.io.InputStream;
import java.util.HashMap;

import org.jboss.ws.WSException;
import org.jboss.ws.extensions.xop.jaxrpc.XOPUnmarshallerImpl;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;

/**
 * An implementation of a JAXB Unmarshaller.
 *
 * @author Thomas.Diesler@jboss.org
 * @author Alexey.Loubyansky@jboss.org
 * @since 18-Oct-2004
 */
public class JBossXBUnmarshallerImpl implements JBossXBUnmarshaller
{
   // The marshaller properties
   private HashMap<String, Object> properties = new HashMap<String, Object>();

   /**
    * Unmarshal XML data from the specified InputStream and return the resulting content tree.
    */
   public Object unmarshal(InputStream is) throws UnmarshalException
   {
      assertRequiredProperties();

      org.jboss.xb.binding.Unmarshaller unm = UnmarshallerFactory.newInstance().newUnmarshaller();
      SchemaBinding schemaBinding = JBossXBSupport.getOrCreateSchemaBinding(properties);
      XOPUnmarshallerImpl xopUnmarshaller = new XOPUnmarshallerImpl();
      schemaBinding.setXopUnmarshaller(xopUnmarshaller);

      try
      {
         return unm.unmarshal(is, schemaBinding);
      }
      catch (JBossXBException e)
      {
         throw new UnmarshalException(e.getMessage(), e);
      }
   }

   /**
    * Get the particular property in the underlying implementation of Unmarshaller.
    */
   public Object getProperty(String name)
   {
      if (name == null)
         throw new IllegalArgumentException("name parameter is null");

      return properties.get(name);
   }

   /**
    * Set the particular property in the underlying implementation of Unmarshaller.
    */
   public void setProperty(String name, Object value)
   {
      if (name == null)
         throw new IllegalArgumentException("name parameter is null");

      properties.put(name, value);
   }


   /** Assert the required properties
    */
   private void assertRequiredProperties()
   {
      if (getProperty(JBossXBConstants.JBXB_XS_MODEL) == null)
      {
         throw new WSException("Cannot find required property: " + JBossXBConstants.JBXB_XS_MODEL);
      }

      if (getProperty(JBossXBConstants.JBXB_JAVA_MAPPING) == null)
      {
         throw new WSException("Cannot find required property: " + JBossXBConstants.JBXB_JAVA_MAPPING);
      }
   }
}
