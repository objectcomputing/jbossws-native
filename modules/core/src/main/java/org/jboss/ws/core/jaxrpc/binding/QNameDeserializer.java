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
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.DeserializerSupport;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public class QNameDeserializer extends DeserializerSupport
{
   // provide logging
   private static final Logger log = Logger.getLogger(QNameDeserializer.class);

   public Object deserialize(QName xmlName, QName xmlType, Source xmlFragment, SerializationContext serContext) throws BindingException {
      return deserialize(xmlName, xmlType, sourceToString(xmlFragment), serContext);
   }

   private Object deserialize(QName xmlName, QName xmlType, String xmlFragment, SerializationContext serContext) throws BindingException
   {
      if(log.isDebugEnabled()) log.debug("deserialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + "]");

      QName value = null;

      NamespaceRegistry nsRegistry = serContext.getNamespaceRegistry();
      String valueStr = unwrapValueStr(xmlFragment, nsRegistry);
      if (valueStr != null)
      {
         value = SimpleTypeBindings.unmarshalQName(valueStr, nsRegistry);
      }

      return value;
   }

   /** Unwrap the value string from the XML fragment
    * @return The value string or null if the startTag contains a xsi:nil='true' attribute
    */
   protected String unwrapValueStr(String xmlFragment, NamespaceRegistry nsRegistry)
   {
      if (isEmptyElement(xmlFragment) == false)
      {
         // Register namespace declarations
         try
         {
            Element el = DOMUtils.parse(xmlFragment);
            NamedNodeMap attribs = el.getAttributes();
            for (int i = 0; i < attribs.getLength(); i++)
            {
               Node attr = attribs.item(i);
               String nodeName = attr.getNodeName();
               if (nodeName.startsWith("xmlns:"))
               {
                  String prefix = nodeName.substring(6);
                  String nsURI = attr.getNodeValue();
                  nsRegistry.registerURI(nsURI, prefix);
               }
            }
         }
         catch (IOException e)
         {
            throw new IllegalArgumentException("Cannot parse xmlFragment: " + xmlFragment);
         }
      }

      return super.unwrapValueStr(xmlFragment);
   }
}
