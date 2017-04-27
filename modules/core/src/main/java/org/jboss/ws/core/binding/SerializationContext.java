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
package org.jboss.ws.core.binding;

import java.util.HashMap;
import java.util.Map;

import org.jboss.xb.binding.NamespaceRegistry;

/**
 * An abstract serialization context
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public abstract class SerializationContext
{
   private Class javaType;
   
   private TypeMappingImpl typeMapping;
   // The namespace registry that is valid for this serialization context
   private NamespaceRegistry namespaceRegistry = new NamespaceRegistry();
   // An arbitrary property bag
   private Map<Object, Object> properties = new HashMap<Object, Object>();

   public Object getProperty(Object key)
   {
      return properties.get(key);
   }

   public void setProperty(Object key, Object value)
   {
      properties.put(key, value);
   }

   public TypeMappingImpl getTypeMapping()
   {
      return typeMapping;
   }

   public void setTypeMapping(TypeMappingImpl typeMapping)
   {
      this.typeMapping = typeMapping;
   }

   public NamespaceRegistry getNamespaceRegistry()
   {
      return namespaceRegistry;
   }

   public Class getJavaType()
   {
      return javaType;
   }

   public void setJavaType(Class javaType)
   {
      this.javaType = javaType;
   }
}
