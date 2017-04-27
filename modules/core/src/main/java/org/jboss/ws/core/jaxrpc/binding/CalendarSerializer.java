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

import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.binding.SerializerSupport;
import org.jboss.ws.util.xml.BufferedStreamResult;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.w3c.dom.NamedNodeMap;

/**
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#dateTime">XML Schema 3.2.16</a>
 */
public class CalendarSerializer extends SerializerSupport
{
   // provide logging
   private static final Logger log = Logger.getLogger(CalendarSerializer.class);

   public Result serialize(QName xmlName, QName xmlType, Object value, SerializationContext serContext, NamedNodeMap attributes) throws BindingException
   {
      if (log.isDebugEnabled())
         log.debug("serialize: [xmlName=" + xmlName + ",xmlType=" + xmlType + "]");

      String valueStr;
      if (Constants.TYPE_LITERAL_DATE.equals(xmlType))
         valueStr = SimpleTypeBindings.marshalDate((Calendar)value);
      else if (Constants.TYPE_LITERAL_TIME.equals(xmlType))
         valueStr = SimpleTypeBindings.marshalTime((Calendar)value);
      else if (Constants.TYPE_LITERAL_DATETIME.equals(xmlType))
         valueStr = SimpleTypeBindings.marshalDateTime((Calendar)value);
      else
         throw new IllegalArgumentException("Invalid xmlType: " + xmlType);

      NamespaceRegistry nsRegistry = serContext.getNamespaceRegistry();
      String xmlFragment = wrapValueStr(xmlName, valueStr, nsRegistry, null, attributes, true);
      return new BufferedStreamResult(xmlFragment);
   }
}
