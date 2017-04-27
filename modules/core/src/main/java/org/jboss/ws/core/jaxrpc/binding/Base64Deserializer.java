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

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.DeserializerSupport;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.ws.extensions.xop.jaxrpc.XOPUnmarshallerImpl;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.sunday.xop.XOPUnmarshaller;
import org.w3c.dom.Element;
/**
 * Deserializer for Base64
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public class Base64Deserializer extends DeserializerSupport
{
   // provide logging
   private static final Logger log = Logger.getLogger(Base64Deserializer.class);

   public Object deserialize(QName xmlName, QName xmlType, Source xmlFragment, SerializationContext serContext) throws BindingException {
      return deserialize(xmlName, xmlType, sourceToString(xmlFragment), serContext);
   }

   private Object deserialize(QName xmlName, QName xmlType, String xmlFragment, SerializationContext serContext) throws BindingException
   {
      if(log.isDebugEnabled()) log.debug("deserialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + "]");

      byte[] value = null;

      String valueStr = unwrapValueStr(xmlFragment);
      if(XOPContext.isXOPMessage())
      {
         try
         {
            Element xopInclude = DOMUtils.parse(valueStr);
            String cid = xopInclude.getAttribute("href");
            XOPUnmarshaller xopUnmarshaller = new XOPUnmarshallerImpl();
            value = xopUnmarshaller.getAttachmentAsByteArray(cid);
         }
         catch (IOException e)
         {
            throw new WSException("Failed to parse xopInclude element");
         }
      }
      else if (valueStr != null)
      {
         value = SimpleTypeBindings.unmarshalBase64(valueStr);
      }
      return value;
   }
}
