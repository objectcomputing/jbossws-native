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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.test.ws.jaxws.wsrm.emulator.Util;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Immutable object implementation representing <b>/views/view/request</b> configuration element content
 *
 * @author richard.opalka@jboss.com
 *
 * @since Nov 7, 2007
 */
final class RequestImpl implements Request
{
   
   private final String httpMethod;
   private final String pathInfo;
   private final Map<String, String> properties;
   private final Map<String, String> matches;

   RequestImpl(Element e, Map<String, String> namespaces)
   {
      this.httpMethod = e.getAttribute(HTTP_METHOD_ATTRIBUTE);
      this.pathInfo = e.getAttribute(PATH_INFO_ATTRIBUTE);
      NodeList contains = e.getElementsByTagName(CONTAINS_ELEMENT);
      if ((contains != null) && (contains.getLength() == 1))
      {
         NodeList nodes = ((Element)contains.item(0)).getElementsByTagName(NODE_ELEMENT);
         Map<String, String> toFill = new HashMap<String, String>();
         this.matches = Collections.unmodifiableMap(toFill);
         for (int i = 0; i < nodes.getLength(); i++)
         {
            String nameAttrValue = ((Element)nodes.item(i)).getAttribute(NAME_ATTRIBUTE);
            String equalsAttrValue = ((Element)nodes.item(i)).getAttribute(EQUALS_ATTRIBUTE);
            toFill.put(Util.replaceAll(nameAttrValue, namespaces), equalsAttrValue);
         }
      }
      else
      {
         this.matches = Collections.emptyMap();
      }
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

   public final String getHttpMethod()
   {
      return this.httpMethod;
   }

   public final Map<String, String> getMatches()
   {
      return this.matches;
   }

   public final String getPathInfo()
   {
      return this.pathInfo;
   }

   public final Map<String, String> getProperties()
   {
      return this.properties;
   }
   
   public final String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(REQUEST_ELEMENT).append(EQUAL).append(LEFT_BRACKET);
      sb.append(HTTP_METHOD_ATTRIBUTE).append(EQUAL).append(this.httpMethod).append(COMMA).append(SPACE);
      sb.append(PATH_INFO_ATTRIBUTE).append(EQUAL).append(this.pathInfo).append(COMMA).append(SPACE);
      sb.append(PROPERTIES).append(EQUAL).append(this.properties).append(COMMA).append(SPACE);
      sb.append(MATCHES).append(EQUAL).append(this.matches).append(RIGHT_BRACKET);
      return sb.toString();
   }

}
