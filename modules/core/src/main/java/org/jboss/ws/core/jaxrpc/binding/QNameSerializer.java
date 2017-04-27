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

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;

import org.jboss.logging.Logger;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.binding.SerializerSupport;
import org.jboss.ws.util.xml.BufferedStreamResult;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.w3c.dom.NamedNodeMap;

/**
 * Serializer for QNames.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public class QNameSerializer extends SerializerSupport
{
   // provide logging
   private static final Logger log = Logger.getLogger(QNameSerializer.class);

   public Result serialize(QName xmlName, QName xmlType, Object value, SerializationContext serContext, NamedNodeMap attributes) throws BindingException
   {
      log.debug("serialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + "]");

      QName qnameValue = (QName)value;
      String nsURI = qnameValue.getNamespaceURI();

      NamespaceRegistry nsRegistry = serContext.getNamespaceRegistry();
      Set<String> nsExtras = new HashSet<String>();
      
      // Remove prefix and register again
      if (nsURI.length() > 0)
      {
         qnameValue = new QName(nsURI, qnameValue.getLocalPart());
         qnameValue = nsRegistry.registerQName(qnameValue);
         if (nsURI.equals(xmlName.getNamespaceURI()) == false)
            nsExtras.add(nsURI);
      }

      String valueStr = SimpleTypeBindings.marshalQName(qnameValue, nsRegistry);
      String xmlFragment = wrapValueStr(xmlName, valueStr, nsRegistry, nsExtras, attributes, true);
      return new BufferedStreamResult(xmlFragment);
   }
}
