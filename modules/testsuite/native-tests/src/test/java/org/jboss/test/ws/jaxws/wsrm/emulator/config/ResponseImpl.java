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
package org.jboss.test.ws.jaxws.wsrm.emulator.config;

import static org.jboss.test.ws.jaxws.wsrm.emulator.Constant.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jboss.test.ws.jaxws.wsrm.emulator.Util;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Immutable object implementation representing <b>/views/view/response</b> configuration element content
 *
 * @author richard.opalka@jboss.com
 *
 * @since Nov 7, 2007
 */
final class ResponseImpl implements Response
{
   
   private final String resource;
   private final String statusCode;
   private final String contentType;
   private final Map<String, String> properties;
   
   ResponseImpl(Element e, Map<String, String> namespaces)
   {
      this.resource = e.getAttribute(RESOURCE_ATTRIBUTE);
      this.statusCode = e.getAttribute(STATUS_CODE_ATTRIBUTE);
      this.contentType = e.getAttribute(CONTENT_TYPE_ATTRIBUTE);
      NodeList setNodes = e.getElementsByTagName(SET_ELEMENT);
      if ((setNodes != null) && (setNodes.getLength() > 0))
      {
         Map<String, String> toFill = new HashMap<String, String>();
         this.properties = Collections.unmodifiableMap(toFill);
         for (int i = 0; i < setNodes.getLength(); i++)
         {
            String key = ((Element)setNodes.item(i)).getAttribute(PROPERTY_ATTRIBUTE);
            String val = ((Element)setNodes.item(i)).getAttribute(VALUE_ATTRIBUTE);
            toFill.put(key, Util.replaceAll(val, namespaces));
         }
      }
      else
      {
         this.properties = Collections.emptyMap();
      }
   }

   public final String getContentType()
   {
      return this.contentType;
   }

   public final Map<String, String> getProperties()
   {
      return this.properties;
   }

   public final String getResource()
   {
      return this.resource;
   }

   public final String getStatusCode()
   {
      return this.statusCode;
   }
   
   public final String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(RESPONSE_ELEMENT).append(EQUAL).append(LEFT_BRACKET);
      sb.append(RESOURCE_ATTRIBUTE).append(EQUAL).append(this.resource).append(COMMA).append(SPACE);
      sb.append(STATUS_CODE_ATTRIBUTE).append(EQUAL).append(this.statusCode).append(COMMA).append(SPACE);
      sb.append(CONTENT_TYPE_ATTRIBUTE).append(EQUAL).append(this.contentType).append(COMMA).append(SPACE);
      sb.append(PROPERTIES).append(EQUAL).append(this.properties).append(RIGHT_BRACKET);
      return sb.toString();
   }
   
}
