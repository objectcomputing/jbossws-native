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
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.binding.SerializerSupport;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.ws.extensions.xop.jaxrpc.XOPMarshallerImpl;
import org.jboss.ws.util.xml.BufferedStreamResult;
import org.jboss.wsf.common.JavaUtils;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.sunday.xop.XOPMarshaller;
import org.jboss.xb.binding.sunday.xop.XOPObject;
import org.w3c.dom.NamedNodeMap;

/**
 * Serializer for base64
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public class Base64Serializer extends SerializerSupport
{
   // provide logging
   private static final Logger log = Logger.getLogger(Base64Serializer.class);

   public Result serialize(QName xmlName, QName xmlType, Object value, SerializationContext serContext, NamedNodeMap attributes) throws BindingException
   {
      if (log.isDebugEnabled())
         log.debug("serialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + "]");

      String xmlFragment = null;
      NamespaceRegistry nsRegistry = serContext.getNamespaceRegistry();

      if (XOPContext.isXOPMessage())
      {
         XOPMarshaller xopMarshaller = new XOPMarshallerImpl();
         XOPObject xopObject = new XOPObject(value);
         xopObject.setContentType("application/octet-stream");
         String cid = xopMarshaller.addMtomAttachment(xopObject, xmlName.getNamespaceURI(), xmlType.getLocalPart());
         String xopInclude = "<xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='" + cid + "'/>";
         xmlFragment = wrapValueStr(xmlName, xopInclude, nsRegistry, null, attributes, false);
      }
      else
      {
         value = JavaUtils.getPrimitiveValueArray(value);
         String valueStr = SimpleTypeBindings.marshalBase64((byte[])value);
         xmlFragment = wrapValueStr(xmlName, valueStr, nsRegistry, null, attributes, true);
      }
      return new BufferedStreamResult(xmlFragment);
   }
}
