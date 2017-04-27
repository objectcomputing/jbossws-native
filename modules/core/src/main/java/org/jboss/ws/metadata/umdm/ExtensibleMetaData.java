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
package org.jboss.ws.metadata.umdm;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for UMD elements that are extensible.
 * 
 * @author Heiko Braun, <heiko@openj.net>
 * @since 21-Mar-2006
 */
public abstract class ExtensibleMetaData
{
   private Map<String, MetaDataExtension> extensions = new HashMap<String, MetaDataExtension>();

   public Map<String, MetaDataExtension> getExtensions()
   {
      return extensions;
   }

   public void addExtension(MetaDataExtension ext)
   {
      getExtensions().put(ext.getExtensionNameSpace(), ext);
   }

   public MetaDataExtension getExtension(String namespace)
   {
      return getExtensions().get(namespace);
   }

}
