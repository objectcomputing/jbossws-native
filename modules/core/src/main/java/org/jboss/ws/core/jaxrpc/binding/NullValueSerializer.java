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
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.binding.SerializerSupport;
import org.jboss.ws.util.xml.BufferedStreamResult;
import org.jboss.xb.binding.NamespaceRegistry;
import org.w3c.dom.NamedNodeMap;

/**
 * Serializer for null values.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 24-Jun-2005
 */
public class NullValueSerializer extends SerializerSupport
{
   // provide logging
   private static final Logger log = Logger.getLogger(NullValueSerializer.class);

   /**
    *  Serializes an object null value.
    *
    *  If a message part of an RPC parameter is defined like this
    *
    *  <message name='SomeMessage'>
    *   <part name='partName' element='someElement'/>
    *  </message>
    *
    *  it is possible that the element definition does not allow
    *  null values. In that case an error should be generated.
    */
   public Result serialize(QName xmlName, QName xmlType, Object value, SerializationContext serContext, NamedNodeMap attributes) throws BindingException
   {
      log.debug("serialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + "]");

      NamespaceRegistry nsRegistry = serContext.getNamespaceRegistry();
      nsRegistry.registerURI(Constants.NS_SCHEMA_XSI, Constants.PREFIX_XSI);
      String xmlFragment = wrapValueStr(xmlName, null, nsRegistry, null, attributes, true);
      return new BufferedStreamResult(xmlFragment);
   }
}
